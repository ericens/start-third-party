package zlx.mysql;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Properties;


/**
 * Created by ericens on 2017/3/25.
 */
@Slf4j
public class ConnectionUtils {


    //default
    static String driver =null;
    static String url = null;
    static String name = null;
    static String pwd = null;

    public static void getDBConfig(){
        try {
            ClassLoader classLoader= ConnectionUtils.class.getClassLoader();
            FileInputStream fileInputStream=new FileInputStream(classLoader.getResource("dbconfig.properties").getPath());
            Properties properties=new Properties();
            properties.load(fileInputStream);

            name=(String) properties.get("username");
            pwd=(String)properties.get("password");
            url=(String)properties.get("url");
            driver=(String) properties.get("driverClassName");
            log.debug("name:{},pwd:{},url:{},driver:{}",name,pwd,url,driver);
            Class.forName(driver);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    //使用ThreadLocal存储当前线程中的Connection对象
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();


    public static Connection getMysqlConnection() {
        getDBConfig();
        Connection conn = threadLocal.get();
        try {
            if (conn == null) {
                conn = DriverManager.getConnection(url,name,pwd);
                threadLocal.set(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void startTransaction(){
        try{
            Connection conn =  threadLocal.get();
            if(conn==null){
                conn = DriverManager.getConnection(url,name,pwd);
                //把 conn绑定到当前线程上
                threadLocal.set(conn);
            }
            //开启事务
            conn.setAutoCommit(false);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void rollback(){
        try{
            //从当前线程中获取Connection
            Connection conn = threadLocal.get();
            if(conn!=null){
                //回滚事务
                conn.rollback();
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void rollback(Savepoint sp){
        try{
            //从当前线程中获取Connection
            Connection conn = threadLocal.get();
            if(conn!=null){
                //回滚事务
                conn.rollback(sp);
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static void commit(){
        try{
            //从当前线程中获取Connection
            Connection conn = threadLocal.get();
            if(conn!=null){
                //提交事务
                conn.commit();
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //关闭数据库连接(注意，并不是真的关闭，而是把连接还给数据库连接池)
    public static void close(){
        try{
            //从当前线程中获取Connection
            Connection conn = threadLocal.get();
            if(conn!=null){
                conn.close();
                //解除当前线程上绑定conn
                threadLocal.remove();
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
