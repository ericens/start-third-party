RedisPoolTest
 1. 测试单例的的情况
 2. 测试多线程情况：volatile + synchronize
 3. 测试redis链接用完的异常：
    达到线程池的最大数：Exception in thread "pool-1-thread-15" redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
    达到redis-server的最大链接数。
 
4. 查看所有链接
   CLIENT LIST ，发现泄露后，可以crontab 进行监控，发现泄露链接。
5. redis-server默认链接超时： 
   timeout 0,用不出超时



## java 链接redis
1. 单机单链接

    Jedis jedis =new Jedis("192.168.25.153", 6379); 
    String result =jedis.get("hello");
2. 单机版使用连接池
    JedisPool jedisPool = new JedisPool("192.168.25.153", 6379);
    Jedis jedis = jedisPool.getResource(); 
    jedis.set("jedis","test");
    jedis.close();
    jedisPool.close();
    
3. 集群redis,单机链接，redis可能返回move指令报错。
    hostAndPort 为集群中其中一个节点
    jedisPool = new JedisPool(jedisPoolConfig, hostAndPort.getHost(),hostAndPort.getPort());
    
4. 集群版链接池版

JedisPoolConfig config = new JedisPoolConfig();
config.setMaxTotal(30);
config.setMaxIdle(2);
 
 
Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
 
jedisClusterNode.add(new HostAndPort("192.168.101.3", 7001));
jedisClusterNode.add(new HostAndPort("192.168.101.3", 7002));
jedisClusterNode.add(new HostAndPort("192.168.101.3", 7003));
jedisClusterNode.add(new HostAndPort("192.168.101.3", 7004));
jedisClusterNode.add(new HostAndPort("192.168.101.3", 7005));
jedisClusterNode.add(new HostAndPort("192.168.101.3", 7006));
 
JedisCluster jc = new JedisCluster(jedisClusterNode, config);
 

#### cluster pipeLine

1. cluster  默认不支持piple
2. 自己实现接口pile接口
	1. 自己缓存每个cmd命令的 client.
	2. connection类默认 把redis jedisCommands 发送到 outputStream里面。没有flush所以有缓存cmd命令的效果
		3. 默认的jedis 单机cmd命令实现，会调用flush，发送到redis-server.
	3. sync的时候，
        调用的client.getOne()方法, 每个命令一个结果
        sync时候，flush stream();
            1. 发现有mv等异常，则抛异常。
            2. 后面命令执行，getAll()
3. 需要上层重试，
    


### 查看
hscan key 0
