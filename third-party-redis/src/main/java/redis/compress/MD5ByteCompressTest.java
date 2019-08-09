package redis.compress;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by @author linxin on 2018/12/11.  <br>
 *   md5(str) 信息产生md5,是128位，16个字节。
 *   1. 存md5后的字符串，要32字符,getByte[],要32字节
 *   2. 存md5的byte[], 只要16字节，
 *   3. 通过byte [] orgin=Hex.decodeHex(s.toCharArray()); 还原原来的 byte字节
 */
@Slf4j
public class MD5ByteCompressTest {


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

    @Test
    public void md5Test() throws NoSuchAlgorithmException, DecoderException {
        for(int i=0;i<10;i++){
            String str="xxxxi"+i;
            String md5Hex = DigestUtils.md5Hex(str).toUpperCase();  //32位字符串
            log.info("{},{},strlen:{},byteLen:{}",str,md5Hex,md5Hex.length(),md5Hex.getBytes().length);
        }

        log.info(" ============================= ");

        for(int i=0;i<10;i++){
            String str="xxxxi"+i;

            //
            byte disgte[] = DigestUtils.md5(str.getBytes());
            log.info("xxxxxx lenght: {}",disgte.length);
            for(int j=0;j<disgte.length;j++){
                System.out.print( disgte[j]);
            }
            System.out.println("old");
            String s=Hex.encodeHexString(disgte).toUpperCase();
            log.info("{},{}",str,s);


            byte [] orgin=Hex.decodeHex(s.toCharArray());
            for(int j=0;j<orgin.length;j++){
                System.out.print( orgin[j]);
            }
            System.out.println("new");
        }





    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //使用byte 节省内存测试
    @Test
    public void md5StrTest(){
        jedis.flushDB();
        for(int i=0;i<100*10000;i++){
            jedis.set("key"+i,"012b10d77ab69c5c0fa0cd7e7b69e1f6");
        }
        String info=jedis.info("Memory");
        log.info("md5StrTest info:"+info);

        /*

md5StrTest info:# Memory
used_memory:137253584
used_memory_human:130.90M
used_memory_rss:147750912
used_memory_peak:137253584
used_memory_peak_human:130.90M
used_memory_lua:35840
mem_fragmentation_ratio:1.08
mem_allocator:libc
         */
    }



    @Test
    public void md5ByteTest() throws DecoderException {
        jedis.flushDB();
        String imeiMD5="012b10d77ab69c5c0fa0cd7e7b69e1f6";
        byte  bytes[]=Hex.decodeHex(imeiMD5.toCharArray());
        for(int i=0;i<100*10000;i++){
            jedis.set(("key"+i).getBytes(),bytes);
        }
        String info=jedis.info("Memory");
        log.info("md5ByteTest info:"+info);
/*
md5ByteTest info:# Memory
used_memory:121253776
used_memory_human:115.64M
used_memory_rss:149229568
used_memory_peak:137253584
used_memory_peak_human:130.90M
used_memory_lua:35840
mem_fragmentation_ratio:1.23
mem_allocator:libc
 */
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
