package redis.jedis;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

@Slf4j
public class TestJedis {

    private  Jedis jedis;
    private  ShardedJedis sharding;
    private  ShardedJedisPool pool;


    Logger logger= LoggerFactory.getLogger(TestJedis.class);

    @Before
    public  void setUpBeforeClass() throws Exception {
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("localhost",6379),
                new JedisShardInfo("localhost",6379)); //使用相同的ip:port,仅作测试


        jedis = new Jedis("localhost");
        sharding = new ShardedJedis(shards);

        pool = new ShardedJedisPool(new JedisPoolConfig(), shards);
    }



    @After
    public  void tearDownAfterClass() throws Throwable {
        jedis.disconnect();
        sharding.disconnect();
        pool.destroy();
    }

















    @Test
    public void zSetTest(){
        String key="zset";
        long socre=20110101;
        jedis.del(key);

        jedis.zadd(key,socre,"a");
        jedis.zadd(key,socre+1,"b");
        jedis.zadd(key,socre+2,"cc");
        jedis.zadd(key,socre+2,"ee");
        jedis.zadd(key,socre+4,"f");
        jedis.zadd(key,socre+5,"a");

        Set<String> result=jedis.zrangeByScore(key,socre,Integer.MAX_VALUE);
        logger.info("result:{}",result);

        jedis.zremrangeByScore(key,socre+2,socre+2);
        result=jedis.zrangeByScore(key,socre,socre+4);
        logger.info("result:{}",result);


        jedis.zadd(key,socre+2,"xxx");
        result=jedis.zrangeByScore(key,socre,Integer.MAX_VALUE);
        logger.info("result:{}",result);

        jedis.zadd(key,socre+4,"xxx");
        result=jedis.zrangeByScore(key,socre,Integer.MAX_VALUE);
        logger.info("result:{}",result);

        jedis.zremrangeByScore(key,socre,socre+4);
        result=jedis.zrangeByScore(key,socre,Integer.MAX_VALUE);
        logger.info("result:{}",result);


    }

    @Test
    public void testSetN() throws InterruptedException {
        logger.info("Simple SET: " );
        final String key="xxxxx";
        jedis.del(key);
        //for(int i=0;i<100;i++){
            if(jedis.setnx(key,"111"+Thread.currentThread().getName())>0){
                logger.info("Simple SET: {}",jedis.get(key) );
                while(jedis.expire(key,1)<0);
            }
            Thread.sleep(100);
        //}


    }

    public static  void main(String args[]) throws Exception {


        for(int i=0;i<20;i++){
            new Thread(new Runnable() {
                public void run() {
                    try {
                        TestJedis test=new TestJedis();
                        test.setUpBeforeClass();
                        test.testSetN();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        }

    }

    @Test
    public void testIncreMent() throws InterruptedException {
        logger.info("Simple SET: " );
        final String key="xxxxx";
        long x=0;
        jedis.del(key);
        for(int i=0;i<1000;i++){
            if((x=jedis.incr(key))>0){
                logger.info("Simple SET: {}",x );
            }
        }


    }


    @Test
    public void test1Normal() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String result = jedis.set("n" + i, "n" + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Simple SET: " + ((end - start)/1000.0) + " seconds");
    }

    @Test
    public void test2Trans() {
        long start = System.currentTimeMillis();
        Transaction tx = jedis.multi();
        for (int i = 0; i < 100000; i++) {
            tx.set("t" + i, "t" + i);
        }
        //System.out.println(tx.get("t1000").get());

        List<Object> results = tx.exec();
        long end = System.currentTimeMillis();
        System.out.println("Transaction SET: " + ((end - start)/1000.0) + " seconds");
    }

    @Test
    public void test3Pipelined() {
        Pipeline pipeline = jedis.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("p" + i, "p" + i);
        }
        //System.out.println(pipeline.get("p1000").get());
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined SET: " + ((end - start)/1000.0) + " seconds");
    }

    @Test
    public void test4combPipelineTrans() {
        //抛异常，stack overflow
        long start = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        pipeline.multi();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("" + i, "" + i);
        }
        pipeline.exec();
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined transaction: " + ((end - start)/1000.0) + " seconds");
    }

    @Test
    public void test5shardNormal() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String result = sharding.set("sn" + i, "n" + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Simple@Sharing SET: " + ((end - start)/1000.0) + " seconds");
    }

    @Test
    public void test6shardpipelined() {
        ShardedJedisPipeline pipeline = sharding.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("sp" + i, "p" + i);
        }
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined@Sharing SET: " + ((end - start)/1000.0) + " seconds");
    }

    @Test
    public void test7shardSimplePool() {
        ShardedJedis one = pool.getResource();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String result = one.set("spn" + i, "n" + i);
        }
        long end = System.currentTimeMillis();
        pool.returnResource(one);
        System.out.println("Simple@Pool SET: " + ((end - start)/1000.0) + " seconds");
    }

    @Test
    public void test8shardPipelinedPool() {
        ShardedJedis one = pool.getResource();

        ShardedJedisPipeline pipeline = one.pipelined();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("sppn" + i, "n" + i);
        }
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        pool.returnResource(one);
        System.out.println("Pipelined@Pool SET: " + ((end - start)/1000.0) + " seconds");
    }
}
