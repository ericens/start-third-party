package org.zlx.javabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationTest {

    private static Logger logger= LoggerFactory.getLogger(AnnotationTest.class);

    /**
     * @param args
     */
    @SuppressWarnings(":deprecation")
    //这里就是注解，称为压缩警告，这是JDK内部自带的一个注解，一个注解就是一个类，在这里使用了这个注解就是创建了SuppressWarnings类的一个实例对象
    public static void main(String[] args) {
        System.runFinalizersOnExit(true);
        //The method runFinalizersOnExit(boolean) from the type System is deprecated(过时的，废弃的)
        //这里的runFinalizersOnExit()方法画了一条横线表示此方法已经过时了，不建议使用了
        sayHello();
    }
    @Deprecated //这也是JDK内部自带的一个注解，意思就是说这个方法已经废弃了，不建议使用了
    public static void sayHello(){
        logger.info("hi,孤傲苍狼");
    }
    @Override //这也是JDK1.5之后内部提供的一个注解，意思就是要重写(覆盖)JDK内部的toString()方法
    public String toString(){
        return "孤傲苍狼";
    }
}
