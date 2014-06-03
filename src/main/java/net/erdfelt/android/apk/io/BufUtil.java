package net.erdfelt.android.apk.io;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BufUtil {
    public static final Charset UTF8 = Charset.forName("UTF-8");
    public static final Charset UTF16LE = Charset.forName("UTF-16LE");

    public static String readNullTerminatedUtf16String(ByteBuffer buf, int fieldSize) throws ParseException {
        int bufSize = fieldSize * 2;

        if (bufSize > buf.remaining()) {
            throw new ParseException(String.format(
                    "Unable to read UTF16 String, expected %,d bytes, but only had %,d bytes left in buffer", bufSize,
                    buf.remaining()));
        }

        int pos = buf.position();
        ByteBuffer strBuf = ByteBuffer.allocate(bufSize);

        ByteBuffer window = buf.slice();
        window.limit(bufSize);

        while (window.hasRemaining()) {
            short val = window.getShort();
            if (val == 0) {
                break;
            }
            strBuf.putShort(val);
        }

        buf.position(pos + bufSize);
        strBuf.flip();
        return toUtf16String(strBuf);
    }

    public static int readUtf16Length(ByteBuffer buf) {
        int len = (buf.getShort() & 0xFFFF);
        if (len > 0x7FFF) {
            len = ((len & 0x7FFF) << 8);
            len |= (buf.getShort() & 0xFFFF);
        }
        return len;
    }

    public static int readUtf8Length(ByteBuffer buf) {
        int len = (buf.get() & 0xFF);
        if ((len & 0x80) != 0) {
            len = (len & 0x7F) << 8;
            len |= (buf.get() & 0xFF);
        }
        return len;
    }

    private static String toUtf16String(ByteBuffer bb) {
        byte buf[] = new byte[bb.remaining()];
        bb.slice().get(buf);
        return new String(buf, 0, buf.length, UTF16LE);
    }
}
