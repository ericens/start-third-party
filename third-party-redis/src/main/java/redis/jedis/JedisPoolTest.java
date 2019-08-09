package redis.jedis;


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.*;

import java.util.*;

import static java.lang.Thread.sleep;

public class JedisPoolTest {
	private static JedisPool jedisPool;


	public static void main(String[] args) {

		List list=new ArrayList();

		for (int i = 0; i < 10000000; i++) {
			char s[]=null;
			try {

				 s= new char[1024*1024];
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(i%100==0){
				list.add(s);
				out("i is "+i);
			}
		}
	}

	private JedisPoolConfig initPoolConfig() {
		out("initPoolConfig..begin...........");
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 控制一个pool最多有多少个状态为idle的jedis实例
		jedisPoolConfig.setMaxIdle(100);// 超时时间
		jedisPoolConfig.setMaxWaitMillis(1000);

		// 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
		jedisPoolConfig.setTestOnBorrow(true);

		// 在还会给pool时，是否提前进行validate操作
		jedisPoolConfig.setTestOnReturn(true);
		return jedisPoolConfig;
	}

	static void  out(Object o){
		System.out.println(o);
	}

	@Before
	public void create_pool() {
		out("create_pool ");
		JedisPoolConfig jedisPoolConfig = initPoolConfig();
		jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1",6379);

	}

	@Test
	public  void testGetResure(){
		Jedis jedis = null;

		for(int i=0;i<10000000;i++){
			try {
				jedis = jedisPool.getResource();
				out(jedis.set("blog_pool", "java2000_wl"));
				String s[] =new String[1000];
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if(jedis!=null){
						jedis.close();
					}
				}catch ( Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public  void testSentinel(){
		Jedis jedis = null;

		for(int i=0;i<1000000;i++){
			try {
				jedis = jedisPool.getResource();
				out(new Date()+":"+jedis.set("blog_pool", "java2000_wl"));
				String s[] =new String[1000];
				sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if(jedis!=null){
						jedis.close();
					}
				}catch ( Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public  void testSentinel2( )
	{
		Set<String> sentinels = new HashSet<String>();
		sentinels.add("127.0.0.1:26379");
		JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);

		Jedis jedis = pool.getResource();




		for(int i=0;i<100000;i++){
			try {
				sleep(1000);
				out(new Date()+jedis.set("jedis", "jedis"));

			} catch (Exception e) {

				try{
					jedis.close();
				}
				catch (Exception e2){
					out("xxxxx");
				}

				try{
					jedis=pool.getResource();
				}catch (Exception e2){
					out("xxxxx");
				}
				e.printStackTrace();
			}finally {

			}

		}

		try{
			jedis.close();
		}
		catch (Exception e){
			out("xxxxx");
		}


	}


	@Test
	public void testPipeLine() {
		out("testSet..begin...........");
		Jedis jedis = null;
		int count=1;
		// 从池中获取一个jedis实例
		try {
			jedis = jedisPool.getResource();
			Pipeline pipeline=jedis.pipelined();
			Long start=System.currentTimeMillis();
			for(int i=0;i<count;i++){
				pipeline.set("xx"+i,"yy"+i);
			}
			pipeline.hgetAll("xxx");
			sleep(1000);
			pipeline.sync();
			out( System.currentTimeMillis()-start);



			start=System.currentTimeMillis();
			for(int i=0;i<count;i++){
				jedis.set("xx"+i,"yy"+i);
			}
			out( System.currentTimeMillis()-start);

		} catch (Exception e) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();

		} finally {
			// 还会到连接池
			jedisPool.returnResource(jedis);
		}
	}




	// /第四步：测试
	@Test
	public void testSet() {
		out("testSet..begin...........");
		Jedis jedis = null;
		// 从池中获取一个jedis实例
		try {
			jedis = jedisPool.getResource();
			out(jedis.set("blog_pool", "java2000_wl"));
		} catch (Exception e) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			e.printStackTrace();

		} finally {
			// 还会到连接池
			jedisPool.returnResource(jedis);
		}
	}

	@Test
	public void testGet() {
		out("testGet..begin...........");
		Jedis jedis = null;
		try {
			// 从池中获取一个jedis实例
			jedis = jedisPool.getResource();
			System.out.println("get from jedis "+jedis.get("blog_pool"));
		} catch (Exception e) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} finally {
			// 还会到连接池
			jedisPool.returnResource(jedis);
		}
	}
}
