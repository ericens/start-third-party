package redis.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;


public class Infrastructure {

    public static Jedis getRedis(){
        String constr = "127.0.0.1" ;
        Jedis jedis = new Jedis(constr) ;
        return jedis ;
    }




    @Test
    public  void ping() {
        //Connecting to Redis server on localhost
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //check whether server is running or not
        System.out.println("Server is running: "+jedis.ping());
    }

    @Test
    public  void Stringtest() {
        //Connecting to Redis server on localhost
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //set the data in redis string
        jedis.set("tutorial-name", "Redis tutorial");
        // Get the stored data and print it
        System.out.println("Stored string in redis:: "+ jedis.get("tutorial-name"));
    }

    @Test
    public  void Listtest() {
        //Connecting to Redis server on localhost
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //store data in redis list
        jedis.lpush("tutorial-list", "Redis");
        jedis.lpush("tutorial-list", "Mongodb");
        jedis.lpush("tutorial-list", "Mysql");
        // Get the stored data and print it
        List<String> list = jedis.lrange("tutorial-list", 0 ,5);
        for(int i=0; i<list.size(); i++) {
            System.out.println("Stored string in redis:: "+list.get(i));
        }

    }


    /**
     * 1. lpush,  rpop 进行任务的通知，task里面带了
     *      task
     *          todoList
     *          sendTime.
     * 2. pop时候，根据sendTime决定是否处理
     *
     * 目的： 需要
     */

}
