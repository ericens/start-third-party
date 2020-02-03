package zlx.mysql;
import	java.util.concurrent.CountDownLatch;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ericens on 2017/3/26.
 *  本地tps基本在1k 左右。
 */
@Slf4j
public class HighTPSTest {


    @Test
    public void timeout() throws SQLException, InterruptedException {
        log.info("Service1Test.......start");
        Connection con = null;
        con = ConnectionUtils.getMysqlConnection();
        AccountDAO accountDAO = new AccountDAO(con);
        for(int i=10000;i<=20000;i++){
            try {
                String str="INSERT INTO orders.account (id, name, money) VALUES ("+i+", null, '10000');";
                accountDAO.doSql(str);
            }catch (Exception e){
                log.error("error",e);
            }
        }
        Long startTime=System.currentTimeMillis();
        int threadCount=20;
        CountDownLatch c=new CountDownLatch(threadCount);
        int start=1;
        for(int i=1;i<=threadCount;i++){
            new Thread(new MyTask(start,start+1000,accountDAO,c)).start();
            start=start+1000;
        }
        c.await();
        Long endTime=System.currentTimeMillis();
        log.info("real time :{}",endTime-startTime);

        log.info("Service1Test.......end");

    }
    class MyTask implements Runnable{
        int start;
        int end;
        AccountDAO accountDAO;
        CountDownLatch c;
        public MyTask(int s,int e,AccountDAO accountDAO,CountDownLatch c){
            this.start = s;
            this.end=e;
            this.accountDAO=accountDAO;
            this.c=c;
            log.info("start:{},end:{}",start,end);
        }
        @Override
        public void run() {
            Long startTime=System.currentTimeMillis();
            try {
                for(int i=start; i <= end; i++){
                    accountDAO.update_delete(new Object[]{i});
                }
            }catch (Exception e){
                log.error("error",e);
            }
            Long endTime=System.currentTimeMillis();

            log.info("time :{}",endTime-startTime);
            c.countDown();
        }
    }

}
