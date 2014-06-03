package net.erdfelt.android.apk.arsc;

import java.io.PrintStream;
import java.nio.ByteBuffer;

import net.erdfelt.android.apk.io.BufUtil;
import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.Dumpable;
import net.erdfelt.android.apk.util.FormattedLog;

public class ResStringPool implements Dumpable {
    private static final FormattedLog LOG = new FormattedLog(ResStringPool.class);

    private final static int FLAG_UTF8 = 1 << 8;

    private int stringCount;
    private int styleCount;
    private int flags;
    private int stringsStart;
    private int stylesStart;

    private int stringOffsets[];
    private String strings[];

    public void parse(Chunk chunk, ByteBuffer buf) throws ParseException {
        // header fields
        this.stringCount = buf.getInt();
        this.styleCount = buf.getInt();
        this.flags = buf.getInt();
        this.stringsStart = buf.getInt();
        this.stylesStart = buf.getInt();

        // collect string offsets
        stringOffsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            stringOffsets[i] = buf.getInt();
        }

        // remember position
        int pos = buf.position();

        // collect strings themselves
        int stringsBase = chunk.location + stringsStart;
        LOG.debug("stringsBase = 0x%08X", stringsBase);

        boolean utf8 = (flags & FLAG_UTF8) != 0;

        strings = new String[stringCount];
        for (int i = 0; i < stringCount; i++) {
            buf.position(stringsBase + stringOffsets[i]);

            if (utf8) {
                strings[i] = getUtf8String(buf);
            } else {
                strings[i] = getUtf16String(buf);
            }
            LOG.debug("string[%d] = %s", i, strings[i]);
        }

        // restore position
        buf.position(pos);
    }

    private String getUtf16String(ByteBuffer bb) {
        int len = BufUtil.readUtf16Length(bb);

        byte buf[] = new byte[len * 2];
        bb.get(buf);

        return new String(buf, 0, buf.length, BufUtil.UTF16LE);
    }

    private String getUtf8String(ByteBuffer bb) {
        // bug in android arsc? the utf8 length sequence seems to be duplicated.
        int len = BufUtil.readUtf8Length(bb);
        len = BufUtil.readUtf8Length(bb);

        byte buf[] = new byte[len];
        bb.get(buf);

        return new String(buf, 0, buf.length, BufUtil.UTF8);
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

    public String getString(int idx) {
        return strings[idx];
    }
}
