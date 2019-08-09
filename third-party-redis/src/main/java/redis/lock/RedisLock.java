package redis.lock;

/**
 * Created by @author linxin on 2018/9/26.  <br>
 *     基于redis的分布式锁工具
 * 主要是：
 *          Long res = jedis.setnx(key,"11");
 *       成功
 *          jedis.expire(key, expire);
 *       失败
 *          检查是否有失效，没有也设置。jedis.expire(key, expire);
 *
 *
 * 由于 这一把锁公用了一个  Jedis实例。
 *    在多线程情况下需要同步。不然出现，
 *          A 写入一部分cmd
 *          B 写入一部分cmd
 *    报错如：
 *      Caused by: java.net.SocketException: Broken pipe (Write failed)
 *      redis.clients.jedis.exceptions.JedisConnectionException: java.net.SocketException: Broken pipe (Write failed)
 */


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;


public class RedisLock implements java.util.concurrent.locks.Lock {

    private final static String PREFIX = "LOCK-FLAG-";
    Jedis jedis = RedisSupport.getJedis();

    public RedisLock(String lockId,Integer expire) {
        this.uid=lockId;
        this.expire=expire;
        this.key=PREFIX+uid;
    }

    public RedisLock(String lockId) {
        this.uid=lockId;
        this.key=PREFIX+uid;
    }
    /**
     * @param uid：锁的唯一标识
     * @param expire：锁自动释放时间、默认10分钟
     */
    String uid;
    Integer expire=60;
    String key;

    @Override
    public  boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (time <= 0) {
            return false;
        }
        Long maxWiat=unit.toMillis(time);

        boolean flag;
        long sum=0L;
        while ( (flag=tryLock()) ==false){
            Thread.sleep(10);
            if((sum=sum+10) > maxWiat){
                break;
            }
        }
        return flag;
    }

    @Override
    public synchronized boolean tryLock() {
        try {
            Long res = jedis.setnx(key,"11");
            if (res == 1) {
                //设置超时
                jedis.expire(key, expire);
                return true;
            }else {
                if(-1==jedis.ttl(key)){
                    //重新设置超时
                    jedis.expire(key, expire);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public synchronized void unlock() {
        jedis.del(key);
    }



    @Override
    public void lock() {
        while (!tryLock()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (!tryLock()){
            Thread.sleep(10);
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}


