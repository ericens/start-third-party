package zlx.dbSplit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by ericens on 2017/3/26.
 */
@Slf4j
public class RouteTest {

    DBCenter dbCenter = new DBCenter();

    @Test
    public void routeTest(){
        setData(1,0);
        log.info("......................origin data..........................\n\n\n");
        dbCenter.printMetaInfo();
        validateData(1,0);


        dbCenter.move(0,1,1,1);
        dbCenter.printMetaInfo();
        log.info("......................dbCenter.move(0,1,1,1)..........................\n\n\n");
        validateData(2,0);


        setData(2,Constant.R_2_K);
        log.info("......................setData(2,Constant.R_2_K);.........................\n\n\n");
        dbCenter.printMetaInfo();
        validateData(2,Constant.R_2_K);

        dbCenter.move(0,2,2,2);
        dbCenter.move(1,3,3,3);
        log.info("......................move.........................\n\n\n");
        dbCenter.printMetaInfo();

        validateData(3,0);
        validateData(3,Constant.R_2_K);

        setData(3, Constant.R_4_K);
        log.info("......................setData(3, Constant.R_4_K);.........................\n\n\n");
        dbCenter.printMetaInfo();
        validateData(3,Constant.R_4_K);

    }

    public void setData(int version,int step){
        for(int i=0;i<8;i++){
            dbCenter.put(version,i+step,i+step);
        }
    }

    public void validateData(int version,int step){
        for(int i=0;i<8;i++){
          assert i+step==(Integer) dbCenter.get(version,i+step);
        }
    }
}
