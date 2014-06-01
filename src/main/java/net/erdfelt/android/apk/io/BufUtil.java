package net.erdfelt.android.apk.io;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BufUtil {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static String readLZString(ByteBuffer buf, int maxSize) {
        int maxLength = Math.max(maxSize, buf.remaining());
        byte sbuf[] = new byte[maxLength];

        for (int i = 0; i < maxLength; i++) {
            sbuf[i] = (byte) buf.get();
            if (sbuf[i] == 0x00) {
                return new String(sbuf, 0, i, UTF8);
            }
        }
        return new String(sbuf, 0, maxLength, UTF8);
    }
}
