package zlx.dbSplit;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 通过Map模拟整个数据库,所有库，所有表
 */
@Slf4j
public class DBCenter {
    /**
     *  <dbid,<tableid,<id,Object>>
     */
    Map<Integer, Map<Integer,Map<Integer,Object>>> store =new HashMap();
    DBRouter dbRouter=new DBRouter();

    /**
     * 把数据 保存在后端数据库中，
     *  1. 根据id 计算 routeInfo
     *  2. 指定的库、指定表中
     * @param id
     * @param data
     */
    public void put(int id,Object data){
        put(1,id,data);
    }
    public void put(int version,int id,Object data){
        RouteInfo routeInfo=dbRouter.getRoute(version,id);
        store.putIfAbsent(routeInfo.db,new HashMap<>());
        Map db= store.get(routeInfo.db);

        db.putIfAbsent(routeInfo.table,new HashMap<>());
        Map table= (Map)db.get(routeInfo.table);
        table.put(id,data);

    }


    /**
     * 把 oldTable从oldDB,  迁移到newDB的newTable
     * @param oldDB
     * @param oldTable
     * @param newDB
     * @param newTable
     */
    public void move(int oldDB,int oldTable,int newDB,int newTable){
        Map table= store.get(oldDB).get(oldTable);
        if(table == null)
            return;
        store.putIfAbsent(newDB,new HashMap<>());
        store.get(newDB).put(newTable, table);

        store.get(oldDB).remove(oldTable);
    }


    public void printMetaInfo(){
        log.info("the meta info .....");
        log.info("{}", JSON.toJSONString(store,true));
//        Iterator dbIterator= store.entrySet().iterator();
//        while(dbIterator.hasNext()){
//            Map  dbEntry=(Map)dbIterator.next();
//            Iterator tableIteraor=dbEntry.entrySet().iterator();
//            while(tableIteraor.hasNext()){
//
//                Map  tableEntry=(Map)tableIteraor.next();
//                Iterator valueIterator=tableEntry.entrySet().iterator();
//                while(valueIterator.hasNext()){
//                    Map.Entry  valueEntry=(Map.Entry)valueIterator.next();
//                    log.info("key:{},value:{}",valueEntry.getKey(),valueEntry.getValue());
//
//                }
//            }
//        }
    }



    public Object get(int id){
        return get(1,id);
    }

    public Object get(int version,int id){
        RouteInfo routeInfo=dbRouter.getRoute(version,id);
        Map db= store.getOrDefault(routeInfo.db,new HashMap<>());
        Map table= (Map)db.getOrDefault(routeInfo.table,new HashMap<>());
        return table.get(id);
    }
}
