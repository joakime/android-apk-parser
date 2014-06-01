package net.erdfelt.android.apk.arsc;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.Dumpable;

public class ResStringPool implements Res, Dumpable {
    private static final Logger LOG = Logger.getLogger(ResStringPool.class.getName());
    private final static Charset UTF16 = Charset.forName("UTF-16");

    private int stringCount;
    private int styleCount;
    private int flags;
    private int stringsStart;
    private int stylesStart;

    private int stringOffsets[];
    private String strings[];

    public void parseData(ByteBuffer buf) throws ParseException {
        // collect string offsets
        stringOffsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            stringOffsets[i] = buf.getInt();
            debug("stringOffsets[%d]: 0x%04X", i, stringOffsets[i]);
        }

        // remember position
        int pos = buf.position();

        // collect strings themselves
        debug("stringsStart = 0x%08X", stringsStart);
        strings = new String[stringCount];
        for (int i = 0; i < stringCount; i++) {
            buf.position(stringsStart + stringOffsets[i]);
            debug("string[ offset:%d - pos:0x%08X ]", stringOffsets[i], buf.position());
            short size = buf.getShort();
            debug("string[ size=%,d ]", size);
            if (size > 0) {
//                byte raw[] = new byte[size];
//                buf.get(raw);
//                strings[i] = new String(raw, 0, size, UTF16);
            } else {
                strings[i] = ""; // empty string special case
            }
        }

        // restore position
        buf.position(pos);
    }

    public void parseHeader(ByteBuffer buf) throws ParseException {
        this.stringCount = buf.getInt();
        this.styleCount = buf.getInt();
        this.flags = buf.getInt();
        this.stringsStart = buf.getInt();
        this.stylesStart = buf.getInt();
    }

    public int getStringCount() {
        return stringCount;
    }

    public int getStyleCount() {
        return styleCount;
    }

    public int getFlags() {
        return flags;
    }

    public int getStringsStart() {
        return stringsStart;
    }

    public int getStylesStart() {
        return stylesStart;
    }

    public void dump(PrintStream out, String indent) {
        out.printf("%s STRING_POOL [ stringCount = %,d %n", indent, stringCount);
        out.printf("%s               styleCount  = %,d ]%n", indent, styleCount);

        out.printf("%s   Strings[%n", indent);
        for (int i = 0; i < stringCount; i++) {
            out.printf("%s     [%2d] \"%s\"%n", indent, i, strings[i]);
        }
        out.printf("%s   ]%n", indent);
    }

    private static void debug(String format, Object... args) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format(format, args));
        }
    }
}
