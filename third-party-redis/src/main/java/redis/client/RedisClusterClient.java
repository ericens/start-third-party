package redis.client;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by @author linxin on 2018/12/11.  <br>
 */
@Slf4j
public class RedisClusterClient {
    private static volatile JedisCluster jedisCluster;

    public static JedisCluster getClusterInstance(){
        if(jedisCluster==null){
            synchronized (RedisClusterClient.class){
                createClusterPool(null);
            }
        }
        return jedisCluster;
    }
    public static JedisCluster getClusterInstance(Set<HostAndPort> hostAndPorts){
        if(jedisCluster==null){
            synchronized (RedisClusterClient.class){
                createClusterPool(hostAndPorts);
            }
        }
        return jedisCluster;
    }



    private static synchronized void createClusterPool(Set<HostAndPort> hostAndPorts) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(30);
        config.setMaxIdle(2);

        if(hostAndPorts==null || hostAndPorts.isEmpty()){
            log.warn("no hostAndPorts provided: use local default setting");
            hostAndPorts = new HashSet();
            hostAndPorts.add(new HostAndPort("127.0.0.1", 7001));
            hostAndPorts.add(new HostAndPort("127.0.0.1", 7002));
            hostAndPorts.add(new HostAndPort("127.0.0.1", 7003));
            hostAndPorts.add(new HostAndPort("127.0.0.1", 7004));
            hostAndPorts.add(new HostAndPort("127.0.0.1", 7005));hostAndPorts.add(new HostAndPort("127.0.0.1", 7000));
        }
        jedisCluster = new JedisCluster(hostAndPorts, config);


    }
}
