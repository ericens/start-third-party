package zlx.mysql;

import zlx.mysql.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ericens on 2017/3/26.
 * 把 事务提升到  service.
 */
@Slf4j
public class AccountDAO {
    Connection con;

    public AccountDAO(Connection con){
        this.con=con;
    }

    public void doSql(String sql){
        QueryRunner queryRunner=new  QueryRunner();
        try {
            int insert = queryRunner.update(con, sql,null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update_add(Object param[]) throws SQLException {
        try {
            String sql="update account set money=money + 1  where id =? ";
            QueryRunner queryRunner=new  QueryRunner();
            int insert = queryRunner.update(con, sql,param);
        } catch (SQLException e){
            e.printStackTrace();
            throw e;
        }


    }

    public void update_delete(Object param[]) throws SQLException {
        try {
            String sql="update account set money=money - 1  where id =? ";
            QueryRunner queryRunner=new  QueryRunner();
            int insert = queryRunner.update(con, sql,param);
        } catch (SQLException e){
            e.printStackTrace();
            throw e;
        }

    }


    public List getAll() throws SQLException {
        QueryRunner qr = new QueryRunner();
        String sql = "select * from account";
        List list = (List) qr.query(con,sql, new BeanListHandler(Account.class));
        return list;
    }
}
