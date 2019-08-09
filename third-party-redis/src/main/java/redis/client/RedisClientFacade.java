package redis.client;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCommands;

import java.util.Set;

/**
 * Created by @author linxin on 2018/12/13.  <br>
 */
public class RedisClientFacade {

    public static JedisCommands getClusterJedis(Set<HostAndPort> hostAndPorts){
       return  RedisClusterClient.getClusterInstance(hostAndPorts);
    }

    public static JedisCommands getJedis(HostAndPort hostAndPort){
        return  RedisPoolClient.getInstance(hostAndPort).getResource();
    }

}
