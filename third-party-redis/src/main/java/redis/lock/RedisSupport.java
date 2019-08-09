package redis.lock;

/**
 * Created by @author linxin on 2018/9/26.  <br>
 *     基于redis的分布式锁工具
 */


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisSupport {

    static JedisPool jedisPool;
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(5000);
        config.setTestOnBorrow(true);

        // 构造池
        jedisPool = new JedisPool(config, "127.0.0.1", 6379);
    }

    public static Jedis getJedis(){

        return jedisPool.getResource();
    }

    public static void returnResource(Jedis jedis){
        jedis.close();
    }

}


