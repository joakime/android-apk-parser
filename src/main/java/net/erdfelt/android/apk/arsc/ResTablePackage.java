package net.erdfelt.android.apk.arsc;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.erdfelt.android.apk.io.BufUtil;
import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.Dumpable;
import net.erdfelt.android.apk.util.FormattedLog;

public class ResTablePackage implements Dumpable {
    private static final FormattedLog LOG = new FormattedLog(ResTablePackage.class);

    private static class Config {
        int size;
        byte imsi[] = new byte[4];
        // TODO: more fields here
    }

    private static class TypeSpec {
        public static final int SPEC_PUBLIC = 0x40000000;

        byte id;
        byte res0;
        short res1;
        int entryCount;
        int entries[];
        
        public void parse(Chunk subchunk, ByteBuffer buf) {
            this.id = buf.get();
            this.res0 = buf.get();
            this.res1 = buf.getShort();
            this.entryCount = buf.getInt();
            this.entries = new int[this.entryCount];

            for (int i = 0; i < this.entryCount; i++) {
                this.entries[i] = buf.getInt();
            }
        }
    }

    private static class Type {
        public static final int NO_ENTRY = 0xFFFFFFFF;

        byte id;
        byte res0;
        short res1;
        int entryCount;
        int entryDataOffset;
        String name;
    }

    private int packageId;
    private String packageName;
    private int typeStringsOffset;
    private int typeStringsCount;
    private ResStringPool typeStringsPool;

    private int keyStringsOffset;
    private int keyStringsCount;
    private ResStringPool keyStringsPool;

    private Map<Integer, Type> typeMap = new HashMap<Integer, Type>();
    private List<TypeSpec> typeSpecs = new ArrayList<TypeSpec>();

    public void parse(Chunk chunk, ByteBuffer buf) throws ParseException {
        this.packageId = buf.getInt();
        this.packageName = BufUtil.readNullTerminatedUtf16String(buf, 128);

        this.typeStringsOffset = buf.getInt();
        this.typeStringsCount = buf.getInt();

        this.keyStringsOffset = buf.getInt();
        this.keyStringsCount = buf.getInt();

        if (typeStringsOffset > 0) {
            buf.position(chunk.location + typeStringsOffset);
            LOG.debug("typeStringsStart: 0x%08X", chunk.location + typeStringsOffset);
            Chunk typeStringsChunk = new Chunk(buf);
            typeStringsPool = new ResStringPool();
            typeStringsPool.parse(typeStringsChunk, buf);
            typeStringsChunk.setPositionAfter(buf);
        }

        if (keyStringsOffset > 0) {
            buf.position(chunk.location + keyStringsOffset);
            LOG.debug("keyStringsStart: 0x%08X", chunk.location + keyStringsOffset);
            Chunk keyStringsChunk = new Chunk(buf);
            keyStringsPool = new ResStringPool();
            keyStringsPool.parse(keyStringsChunk, buf);
            keyStringsChunk.setPositionAfter(buf);
        }

        while (hasPendingChunk(chunk, buf)) {
            Chunk subchunk = new Chunk(buf);
            switch (subchunk.type) {
            case TABLE_TYPE_SPEC:
                TypeSpec spec = new TypeSpec();
                spec.parse(subchunk,buf);
                typeSpecs.add(spec);
                break;
            case TABLE_TYPE:
                // TODO: read type
                break;
            default:
                LOG.debug("Unrecognized Chunk Type for sub-table use: %s", subchunk.type);
            }
            subchunk.setPositionAfter(buf);
        }
    }

    private boolean hasPendingChunk(Chunk chunk, ByteBuffer buf) {
        int endPos = chunk.location + chunk.dataSize;
        if (buf.position() >= endPos) {
            return false;
        }
        return buf.hasRemaining() && (endPos - buf.position()) > 0;
    }

    public int getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getTypeStringsOffset() {
        return typeStringsOffset;
    }

    public int getTypeStringsCount() {
        return typeStringsCount;
    }

    public int getKeyStringsOffset() {
        return keyStringsOffset;
    }

    public int getKeyStringsCount() {
        return keyStringsCount;
    }

    public void dump(PrintStream out, String indent) {
        out.printf("%s PACKAGE [ name              = %s %n", indent, packageName);
        out.printf("%s           packageId         = %,d %n", indent, packageId);
        out.printf("%s           typeStringsOffset = 0x%08X %n", indent, typeStringsOffset);
        out.printf("%s           typeStringsCount  = %,d %n", indent, typeStringsCount);
        out.printf("%s           keyStringsOffset  = 0x%08X %n", indent, keyStringsOffset);
        out.printf("%s           keyStringsCount   = %,d ]%n", indent, keyStringsCount);

        if (typeStringsPool != null) {
            typeStringsPool.dump(out, indent + "  ");
        }
        if (keyStringsPool != null) {
            keyStringsPool.dump(out, indent + "  ");
        }
    }
}