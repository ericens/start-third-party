package redis.client;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by @author linxin on 2018/12/10.  <br>
 *
 */


public class RedisClientTest {
    @Slf4j
   public static class RedisPoolClientTest{

        JedisPool jedisPool=RedisPoolClient.getInstance(new HostAndPort("127.0.0.1",7004));
        @Test
        public void poolClientTest() throws InterruptedException {
            log.info("poolClientTest");

            ExecutorService s= Executors.newFixedThreadPool(50);

            for(int i=0;i<100000;i++){
                s.execute(new Runnable() {
                    @Override
                    public void run() {
                        Jedis jedis=jedisPool.getResource();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        jedis.close();
                        log.info("return jedis......{}",jedis.toString());
                    }
                });
            }

            for(int i=0;i<10;i++){
                log.info("go:{}",jedisPool.getNumActive());
                Thread.sleep(1000);
                log.info("go");
            }

            s.shutdown();
        }
    }


    @Slf4j
    public  static class RedisClusterClientTest{
        @Test
        public void clusterTest() throws InterruptedException, IOException {
            ExecutorService s= Executors.newFixedThreadPool(50);
            final JedisCluster jedisCluster=RedisClusterClient.getClusterInstance();
            log.info("clusterTest");
            for(int i=0;i<100000;i++){
                s.execute(new Runnable() {
                    @Override
                    public void run() {

                        jedisCluster.set("i"+RandomUtils.nextInt(0,10000),"");

                    }
                });
            }

            Thread.sleep(5000);
            jedisCluster.close();
            s.shutdown();


        }
    }



}
