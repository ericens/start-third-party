package redis.compress;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by @author linxin on 2018/12/11.  <br>
 *   1. key value的str结构，
 *   2. hash 的  key,field,value形式
 *   3. 多个key的相同部分,只需要存一遍。

 *   画像：
 *      imei : set<tag1,tag2,tag2>
 *   反作弊（询价次数，出价次数等）
 *      imei count
 *          => key,field,count
 *   素材
 *      10_20: set<id,id>
 *      10_30: set<id,id>
 *      nc:set<id,id>
 *      sz:set<id,id>

 *
 */
@Slf4j
public class HashCompressTest {



    private Jedis jedis;
    private ShardedJedis sharding;
    private ShardedJedisPool pool;


    @Before
    public  void setUpBeforeClass() throws Exception {
        List<JedisShardInfo> shards = Arrays.asList(
                new JedisShardInfo("127.0.0.1",6379),
                new JedisShardInfo("127.0.0.1",6379)); //使用相同的ip:port,仅作测试


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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  使用md5作为key,-1,查看内存占用。
     *  <key,-1>
     */
    @Test
    public void keyValueSetTest(){
        jedis.flushAll();
        long start = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        for(long i=0;i<100*10000;i++){
            String md5Id=DigestUtils.md5Hex("key"+i);
            pipeline.set(md5Id,md5Id);
            if(i/10000==0){
                pipeline.syncAndReturnAll();
                pipeline=jedis.pipelined();
            }
        }
        pipeline.syncAndReturnAll();
        String info=jedis.info("Memory");
        log.info("keyValueSetTest info:"+info);
        long end = System.currentTimeMillis();
        log.info("time elapse:" +(end-start));


        /*

md5StrTest info:# Memory
used_memory:89414912
used_memory_human:85.27M
used_memory_rss:105091072
used_memory_peak:95731072
used_memory_peak_human:91.30M
used_memory_lua:35840
mem_fragmentation_ratio:1.18
mem_allocator:libc
         */
    }

    /**
     * 使用md5的 部分作为key,部分为field,-1,查看内存占用。
     *      <key，field,-1>
     * @throws DecoderException
     */
    @Test
    public void HashSetMapTest() throws DecoderException {
        jedis.flushAll();
        long start = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        byte [] bytes1="1".getBytes();
        int len=3;
        for(long i=0;i<100*10000;i++){
            String md5Id=DigestUtils.md5Hex("key"+i);
            byte  bytes[]=md5Id.getBytes();

            byte []key=new byte[len];
            byte []field=new byte[32-len];
            System.arraycopy(bytes,0,key,0,len);
            System.arraycopy(bytes,len,field,0,32-len);

            pipeline.hset(key,field,bytes1);
            if(i/10000==0){
                pipeline.syncAndReturnAll();
                pipeline=jedis.pipelined();
            }
        }
        pipeline.syncAndReturnAll();
        String info=jedis.info("Memory");
        log.info("HashSetMapTest info:"+info);
        long end = System.currentTimeMillis();
        log.info("time elapse:" +(end-start));

/*
md5ByteTest info:# Memory
used_memory:119085440
used_memory_human:35.57M
used_memory_rss:137474048
used_memory_peak:121573776
used_memory_peak_human:115.94M
used_memory_lua:35840
mem_fragmentation_ratio:1.15
mem_allocator:libc
 */
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
