package org.zlx.qrCode;

/**
 * Created by @author linxin on 20/10/2017.  <br>
 */

import net.glxn.qrgen.image.ImageType;

import java.io.*;

/**
 * 二维码生成
 * 1. 直接展示字符串
 * 2. 跳转至指定的 url
 */
public class Main {
    public static void main(String[] args) {
        String str="http://www.cnblogs.com/xz-luckydog/p/6402568.html";
        String url="hello world";
        generateRCCode(str,"/Users/ericens/downloads/1.JPG");

        generateRCCode(url,"/Users/ericens/downloads/2.JPG");

    }

    public static void generateRCCode( String str,String path) {
        ByteArrayOutputStream out = net.glxn.qrgen.QRCode.from(str).to(ImageType.PNG).stream();

        try {
            FileOutputStream fout = new FileOutputStream(new File(
                    path));
            fout.write(out.toByteArray());

            fout.flush();
            fout.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
