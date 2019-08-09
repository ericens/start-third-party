package redis.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**

 * 1. 测试单例的的情况
 * 2. 测试多线程情况：volatile + synchronize
 * 3. 测试redis链接用完的异常：
 *    达到线程池最大数：Exception in thread "pool-1-thread-15" redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
 *
 *    查看所有链接
 *    CLIENT LIST
 *
 */
public class RedisPoolClient {

    static Logger log =LoggerFactory.getLogger(RedisClientTest.class);
    private static volatile JedisPool jedisPool;

    public static JedisPool getInstance(HostAndPort hostAndPort){
        if(jedisPool==null){
            synchronized (RedisClientTest.class){
                createPool(hostAndPort);
            }
        }
        return jedisPool;
    }



    private static void createPool(HostAndPort hostAndPort) {
        log.info("initPoolConfig..begin...........");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 控制一个pool最多有多少个状态为idle的jedis实例
        jedisPoolConfig.setMaxIdle(10*1000);// 超时时间
        jedisPoolConfig.setMaxWaitMillis(10L*1000);
        jedisPoolConfig.setMaxTotal(1000);
        jedisPool = new JedisPool(jedisPoolConfig, hostAndPort.getHost(),hostAndPort.getPort());

    }


}
