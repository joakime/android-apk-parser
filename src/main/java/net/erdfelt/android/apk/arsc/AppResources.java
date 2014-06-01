package net.erdfelt.android.apk.arsc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.erdfelt.android.apk.io.IO;
import net.erdfelt.android.apk.io.ParseException;

public class AppResources {
    private static final Logger LOG = Logger.getLogger(AppResources.class.getName());

    private ResTable table;

    public AppResources(File file) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer buf = in.getChannel().map(MapMode.READ_ONLY, 0, file.length());
            buf.order(ByteOrder.LITTLE_ENDIAN);

            table = (ResTable) parseChunk(buf);
            table.dump(System.out, "");
        } finally {
            IO.close(in);
        }
    }

    public AppResources(InputStream in, int size) throws IOException {
        byte buf[] = new byte[size];
        int length = in.read(buf, 0, size);
        if (length != size) {
            throw new IOException(String.format("Only read %,d bytes of %,d expected bytes", length, size));
        }
        ByteBuffer bb = ByteBuffer.wrap(buf);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        table = (ResTable) parseChunk(bb);
        table.dump(System.out, "");
    }

    public static Res parseChunk(ByteBuffer buf, Res... parents) throws ParseException {
        // Read Chunk Header
        short typeId = buf.getShort();
        ChunkType type = ChunkType.fromId(typeId);
        if (type == null) {
            throw new ParseException(String.format("Unrecognized type: 0x%04X", typeId));
        }
        short headerSize = buf.getShort();
        int dataSize = buf.getInt();

        debug("pos: 0x%08X", buf.position());
        debug("Chunk: %s - header:%d - data:%d", type, headerSize, dataSize);

        // Create Type
        Res res = newType(type);
        debug("New Type - %s", res.getClass().getName());

        int pos, size, end;

        // Parse Type Headers
        pos = buf.position();
        size = headerSize - 8;
        end = pos + size;

        debug("Parse Header - %s: (%s)", res.getClass().getName(), buf);
        res.parseHeader(buf);
        for (Res parent : parents) {
            if (buf.hasRemaining()) {
                debug("Parse Header - %s: (%s)", parent.getClass().getName(), buf);
                parent.parseHeader(buf);
            }
        }
        buf.position(pos + size);

        // Parse Type Data
        pos = buf.position();
        size = dataSize - headerSize;
        end = pos + size;

        debug("Parse Data - %s: (%s)", res.getClass().getName(), buf);
        res.parseData(buf);
        for (Res parent : parents) {
            if (buf.hasRemaining()) {
                debug("Parse Data - %s: (%s)", parent.getClass().getName(), buf);
                parent.parseData(buf);
            }
        }
        buf.position(pos + size);
        return res;
    }

    public static Res newType(ChunkType type) throws ParseException {
        switch (type) {
        case TABLE:
            return new ResTable();
        case STRING_POOL:
            return new ResStringPool();
        default:
            throw new ParseException(type + " Not yet implemented");
        }
    }

    private static void debug(String format, Object... args) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format(format, args));
        }
    }

    public ResTable getTable() {
        return table;
    }

    private static void warn(String format, Object... args) {
        LOG.warning(String.format(format, args));
    }
}
