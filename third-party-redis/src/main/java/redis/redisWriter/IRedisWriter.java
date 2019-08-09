package redis.redisWriter;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.client.RedisClientFacade;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCommands;

import java.io.File;
import java.util.*;

/**
 * Created by @author linxin on 2018/12/13.  <br>
 *
 * 根据keys 写入一个目录下的所有的文件到redis.
 *
 */
@Slf4j
@Data
public abstract class IRedisWriter {
    protected final static String CHANNEL ="TT";
    protected final static String BM ="bm";
    protected final static String B ="b";

    private String splitter =",";
    private List keys= Arrays.asList(BM,B);

    public JedisCommands jedisCommands;


    public void writerDir(String dir){

        Set<HostAndPort> jedisClusterNode = new HashSet();
        jedisClusterNode.add(new HostAndPort("127.0.0.1",6379));
        jedisClusterNode.add(new HostAndPort("127.0.0.1",6379));
        jedisClusterNode.add(new HostAndPort("127.0.0.1",6379));
        jedisClusterNode.add(new HostAndPort("127.0.0.1",6379));
        jedisClusterNode.add(new HostAndPort("127.0.0.1",6379));
        jedisClusterNode.add(new HostAndPort("127.0.0.1",6379));
        jedisCommands=RedisClientFacade.getClusterJedis(jedisClusterNode);


        File files[]=new File(dir).listFiles();
        for( File f:files){
            if(true==doWriterFile(f.getPath())){
                log.info("path:{} file success!",f.getAbsolutePath());
            }else {
                log.error("path:{} file fail!" ,f.getAbsolutePath());
            }

        }
    }

    /**
     *  按照splitter，把一行进行分割，根据keys进行放入map 返回。
     * @param line
     * @return
     */
    protected Map lineToMap(String line){
        if(org.apache.commons.lang3.StringUtils.isBlank(line)){
            log.warn("lineToMap the line is blank");
            return null;
        }
        Map data=new HashMap<>(keys.size());
        String item[]=line.split(splitter);
        if(item!=null && item.length==keys.size()) {
            for (int i = 0; i < keys.size(); i++) {
                data.put(keys.get(i), item[i].trim());
            }
        }else{
            log.warn("the line items not equal with keys! line:{},keys:{}",line, JSON.toJSONString(keys));
        }
        return data;
    }

    public abstract boolean doWriterFile(String path);


}
