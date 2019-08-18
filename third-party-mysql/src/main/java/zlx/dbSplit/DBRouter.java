package zlx.dbSplit;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBRouter {
    /**
     * 根据,获取此数据应该存取  位置信息
     * @param id
     * @return
     */
    RouteInfo getRoute(int id){
        return getRoute(1,id);
    }


    RouteInfo getRoute(int version,int id){
        switch (version){
            case 1:
                return getRoutev1(id);
            case 2:
                return getRoutev2(id);
            case 3:
                return getRoutev3(id);
        }

        return null;
    }

    public RouteInfo getRoutev1(int id){
        RouteInfo routeInfo=null;
        if(id<Constant.R_2_K){
            routeInfo=RouteInfo.builder().db(0).table(id%2).build();
        }
        log.debug("version:1,id:{}, route:{}",id, JSON.toJSONString(routeInfo));
        return routeInfo;
    }

    /**
     * v1 到v2 时
     *  1.   t1 从db0 迁移到 db1的 t1
     *  2.   形成了老数据： db0:(t0)       db1( t1)
     *  3.   加上  新数据： db0:(t0，t2)  db1(t1,t3)
     * @param id
     * @return
     */
    public RouteInfo getRoutev2(int id){
        RouteInfo routeInfo=null;
        int db=-1;
        int table=-1;
        if(id<Constant.R_2_K){
            table=id%2;
            db=table;
        }
        else if(id<Constant.R_4_K){
            table=id%4;
            db=id%2;
        }
        routeInfo=RouteInfo.builder().db(db).table(table).build();
        log.debug("version:2,id:{}, route:{}",id, JSON.toJSONString(routeInfo));
        return routeInfo;
    }


    /**
     *      db0:(t0，t2)  db1(t1,t3)
     * 迁移v2 到v3 时
     *  1.   形成了老数据： db0:(t0)      db1( t1)      db2( t2)       db3( t3)
     *  3.   加上  新数据： db0:(t0,t4)   db1( t1,t5)   db2( t2,t6)    db3( t3,t7)
     * @param id
     * @return
     */
    public RouteInfo getRoutev3(int id){
        RouteInfo routeInfo=null;
        int db=-1;
        int table=-1;
        if(id<Constant.R_2_K){
            table=id%2;
            db=table;
        }
        else if(id<Constant.R_4_K){
            table=id%4;
            db=table;
        }
        else if(id<Constant.R_8_K){
            table=id%8;
            db=id%5;
        }
        routeInfo=RouteInfo.builder().db(db).table(table).build();
        log.debug("version:3,id:{}, route:{}",id, JSON.toJSONString(routeInfo));
        return routeInfo;
    }





}
