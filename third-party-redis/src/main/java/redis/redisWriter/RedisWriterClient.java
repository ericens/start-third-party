package redis.redisWriter;

import lombok.extern.slf4j.Slf4j;
import redis.redisWriter.impl.ImplRedisWriterHash;

/**
 * Created by @author linxin on 2018/12/13.  <br>
 */
@Slf4j
public class RedisWriterClient {

    public static void main(String[] args) {
        try {
            IRedisWriter iRedisWriter=new ImplRedisWriterHash();
            iRedisWriter.setSplitter(",");
            iRedisWriter.writerDir("/Users/ericens/tmp/data");

        }catch ( Exception e){
            log.error("error found",e);
        }
    }
}
