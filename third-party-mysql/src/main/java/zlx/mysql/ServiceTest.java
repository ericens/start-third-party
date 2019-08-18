package zlx.mysql;

import zlx.mysql.ServiceWithTransaction;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ericens on 2017/3/26.
 */
@Slf4j
public class ServiceTest {


    @Test
    public void transferWithNoErrorTest() throws SQLException {
        log.info("Service1Test.......start");
        ServiceWithTransaction service=new ServiceWithTransaction();
        service.errorFun=t->{ return null;};
        service.transfer(1,2,1);
        log.info("Service1Test.......end");

    }

    @Test
    public void transferWithErrorTest() throws SQLException {
        log.info("Service1Test.......start");
        ServiceWithTransaction service=new ServiceWithTransaction();
        service.errorFun=t->{ int x=1/0;return null;};
        service.transfer(1,2,1);
        log.info("Service1Test.......end");

    }

    @Test
    public void getAllTest() throws SQLException {
        ServiceWithTransaction service=new ServiceWithTransaction();
        List list=service.getAccountList();
        log.info("the result:{}",JSON.toJSONString(list));
    }

    @Test
    public void withNoSavePointTest() throws SQLException {
        ServiceWithTransaction service=new ServiceWithTransaction();
        service.stepInspectPlugin.apply(null);
        service.transferSavepoint(1,2,3,false);
        service.stepInspectPlugin.apply(null);

    }

    @Test
    public void withSavePointTest() throws SQLException {
        ServiceWithTransaction service=new ServiceWithTransaction();
        service.stepInspectPlugin.apply(null);
        service.transferSavepoint(1,2,3,true);
        service.stepInspectPlugin.apply(null);

    }
}
