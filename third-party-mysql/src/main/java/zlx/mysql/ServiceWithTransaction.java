package zlx.mysql;
import	java.util.function.Function;

import com.alibaba.fastjson.JSON;
import zlx.mysql.entity.Account;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

/**
 * Created by ericens on 2017/3/26.
 * <p>
 * 在此进行事务 控制
 */
@Slf4j
public class ServiceWithTransaction {
    /*
     * tranfer the amt from ida to idb
     *  事务管理
     * */


    public  Function errorFun=t  ->{
        getAccountList();
        log.info("模拟异常:1/0");
        int y=1/0;   //模拟 异常
        return null;
    };

    public  Function stepInspectPlugin=t  ->{
        try {
            Thread thread=new Thread(() -> {
                log.info("other thread. {}  list:{}", t, JSON.toJSONString(getAccountList()));
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
        }
        log.info("same thread. {}  list:{}", t, JSON.toJSONString(getAccountList()));
        return null;
    };


    public void transfer(int ida, int idb, double amt) {
        log.info("begin to transfer,in 3 step accountFrom:{},accountTo:{},amt:{}",ida,idb,amt);
        stepInspectPlugin.apply("before step1 apply");
        Connection con = null;
        try {
            con = ConnectionUtils.getMysqlConnection();
            con.setAutoCommit(false);
            AccountDAO accountDAO = new AccountDAO(con);

            accountDAO.update_delete(new Object[]{ida});
            log.info("step1. after reduce money from  accountFrom");
            stepInspectPlugin.apply("after step1 apply");

            //模拟异常
            errorFun.apply(null);

            accountDAO.update_add(new Object[]{idb});
            log.info("step2. after add money to  accountFrom");
            stepInspectPlugin.apply("after step2 apply");


            con.commit();
            log.info("step3. commit");
            stepInspectPlugin.apply("after step3 apply");

        } catch (Exception e) {
            log.info("Exception step:不写rollbak,则是自动回滚,也就是没有提交.");
            log.info("Exception step: 协商 rollbak  则 手动回滚");
            e.printStackTrace();
        } finally {
            if (con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            stepInspectPlugin.apply("finally");
        }

    }


    /**
     * tranfer the amt from ida to idb
     *  savepoint 测试
     **/
    public void transferSavepoint(int ida, int idb, int idc, boolean shouldSavePoint) {
        Connection con = null;
        Savepoint savepoint=null;
        try {
            con= ConnectionUtils.getMysqlConnection();
            con.setAutoCommit(false);

            //sqla
            AccountDAO accountDAO = new AccountDAO(con);
            accountDAO.update_delete(new Object[]{ida});

            //save point
            savepoint = con.setSavepoint();
            log.info("sqla complete, and save point complete");
            stepInspectPlugin.apply("sqla complete");

            // sqlb
            accountDAO.update_add(new Object[]{idb});
            errorFun.apply(null);
            log.info("sqlb complete, and save point complete");


            accountDAO.update_add(new Object[]{idc});
            log.info("sqlc complete, and save point complete");
            con.commit();

        } catch (Exception e) {
            try {
                if(shouldSavePoint){
                    log.info("rollback with savepoint，也要提交");
                    con.rollback(savepoint);
                    con.commit();

                }
                else
                {
                    //这样回滚是全部回滚,而不管是否是有savepoint
                    log.info("rollback without savepoint");
                    con.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

    }

    public List<Account> getAccountList() {
        Connection con = ConnectionUtils.getMysqlConnection();
        AccountDAO accountDAO = new AccountDAO(con);
        try {
            return accountDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
