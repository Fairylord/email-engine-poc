package test.charset;

/**
 * Created by HUANGYE2 on 11/21/2017.
 */

import java.nio.charset.*;
import java.nio.charset.spi.*;
import java.util.*;

public class CP932CharsetProvider extends CharsetProvider {
    private static final String badCharset = "cp932";
    private static final String goodCharset = "MS932";

    public Charset charsetForName(String charset) {
        if (charset.equalsIgnoreCase(badCharset))
            return Charset.forName(goodCharset);
        return null;
    }

    public Iterator<Charset> charsets() {
        return Collections.emptyIterator();
    }
}
