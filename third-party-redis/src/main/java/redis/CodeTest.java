package redis;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Created by @author linxin on 2018/9/9.  <br>
 */
public class CodeTest {


    public static byte[] hexStringToBytes(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            //16进制字符转换成int->位运算（取int(32位)低8位,即位与运算 &0xFF）->强转成byte
            String substring=hexString.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) (0xFF & Integer.parseInt(substring, 16));
            System.out.println(substring+" : "+bytes[i]);
        }
        return bytes;
    }

    public static boolean isValidMd5Hex(String hex) {
        return StringUtils.isNotEmpty(hex) && !hex.toLowerCase().equals("null") && hex.length() == 32;
    }

    @Test
    public void byteTest() throws DecoderException {
        String md5Id=DigestUtils.md5Hex("sadfasfzxvasfajqojroqjifaosiasdasdfds");
        System.out.println(md5Id);
        System.out.println(md5Id.length());
        byte[] imeiByMd5 = hexStringToBytes(md5Id);


        System.out.println(isValidMd5Hex(md5Id));

        for (byte b : imeiByMd5) {
            System.out.print(b+",");
        }

        System.out.println();
        imeiByMd5=Hex.decodeHex(md5Id.toCharArray());
        for (byte b : imeiByMd5) {
            System.out.print(b+",");
        }

    }


}
