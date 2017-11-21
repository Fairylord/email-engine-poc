package test.charset;

import java.nio.charset.Charset;

/**
 * Created by HUANGYE2 on 11/21/2017.
 */
public class StringCodingByCustomizedCharsetProvider {

    public static void main(String[] args) {
        String s1 = "ABCDE, 日本語ラテン文字漢字";

        /**
         * 如果采用这种方式, 则不需要修改原有的代码.
         * 只需要把Provider类和Resource下面的文件写好就行.
         */

        // Use my own charset provider to encode and decode.
        // Will NOT throw UnsupportedCharsetException!
        byte[] barray = s1.getBytes(Charset.forName("cp932"));  // 模拟CP932编码的输入
        String s2 = new String(barray, Charset.forName("cp932"));

        System.out.println(s2);
    }
}
