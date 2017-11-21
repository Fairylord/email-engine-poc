package test.charset;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Created by HUANGYE2 on 11/21/2017.
 */
public class StringCodingByStream {

    public static void main(String[] args) {
        String s1 = "ABCDE, 日本語ラテン文字漢字";

        byte[] barray = s1.getBytes(Charset.forName("cp932"));  // 模拟CP932编码的输入

        /**
         * 如果采用这种方式, 则需要修改原有的代码.
         * Catch UnsupportedCharsetException 来判断是否碰到了JDK不支持的编码
         */

        try {
            // 假设这里的逻辑是原先的 part.getContent()
            // ...
            // ...
            // 碰到cp932! 抛异常!
            throw new UnsupportedCharsetException("cp932");
        } catch (UnsupportedCharsetException ue) {
            // 先判断是不是真的cp932.
            if(ue.getCharsetName().equalsIgnoreCase("cp932")) {
                System.out.println("发现cp932编码!");
                // 通过获取输入流, 强行指定MS932编码格式做解码
                InputStream is = new ByteArrayInputStream(barray);  // 获得一个输入流, 在下面用自定义的编码格式做解码
                String s2 = null;
                try {
                    s2 = IOUtils.toString(is, Charset.forName("MS932"));    // JDK支持MS932!
                    System.out.println(s2);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
            else {
                // 可能是其余我们没遇到的奇怪的编码!
                throw ue;
            }


        }
    }
}
