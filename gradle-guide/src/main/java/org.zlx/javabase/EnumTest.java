package org.zlx.javabase;

import lombok.extern.slf4j.Slf4j;

/**
 * 枚举：
 *  1. 默认是String 属性的 static声明的实例
 *  2. 加入其它字段属性，需要响应构造函数的，也是static声明的实例
 *
 */
@Slf4j
public class EnumTest {
    public enum Date {
        MON, TUE, WED, THU, FRI, SAT, SUN;
    }


    public  enum DayWithValue {
        MON(1,"111"),
        TUE(2,"222"),
        WED(3,"333"),
        THU(1,"4444");

        private int value;
        private String description;

        DayWithValue(int val, String description) {
            this.value = val;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }


    }

    public static void main(String[] args) {

        for (DayWithValue e : DayWithValue.values()) {
            log.info("string:{},value:{},desc:{}",e.toString(),e.getValue(),e.getDescription());
        }



        System.out.println("----------------我是分隔线------------------");


        for (Date e : Date.values()) {
            System.out.println(e.toString());
        }

        System.out.println("----------------我是分隔线------------------");
        Date test = Date.TUE;
        switch (test) {
        case MON:
            System.out.println("今天是星期一");
            break;
        case TUE:
            System.out.println("今天是星期二");
            break;
        // ... ...
        default:
            System.out.println(test);
            break;
        }
    }
}
