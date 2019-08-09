package redis.redisWriter.impl;

import lombok.extern.slf4j.Slf4j;
import redis.redisWriter.IRedisWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by @author linxin on 2018/12/13.  <br>
 *     redis的结构为: jedisCommands.hmset(CHANNEL, dataAll);
 */
@Slf4j
public class ImplRedisWriterHash extends IRedisWriter {

    @Override
    public boolean doWriterFile(String path) {
        String line = null;
        long lineCount=0L, writeCount=0L;
        Map<String,String > dataAll=new HashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path)))){
            while ((line = br.readLine()) != null){
                log.info("line:{}",line);
                Map<String,String> data= lineToMap(line);
                if(data!=null && data.get(BM)!=null && data.get(B)!=null ){
                    dataAll.put( data.get(BM),data.get(B)  );
                    writeCount++;
                }
                lineCount++;
                if(lineCount%5000==0 && dataAll.size()!=0){
                    jedisCommands.hmset(CHANNEL, dataAll);
                    log.info("syn data:{}", lineCount);
                    dataAll.clear();
                }
            }
            if(!dataAll.isEmpty()){
                jedisCommands.hmset(CHANNEL,dataAll);
            }
        }catch (Exception e){
            log.error("error:",e);
        }
        log.info("syn data  lineCount:{}, writeCount:{}",lineCount,writeCount);
        return true;
    }
}
