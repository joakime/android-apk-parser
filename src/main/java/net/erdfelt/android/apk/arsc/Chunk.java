package net.erdfelt.android.apk.arsc;

import java.nio.ByteBuffer;

import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.FormattedLog;

public class Chunk {
    private static final FormattedLog LOG = new FormattedLog(Chunk.class);
    
    public static enum Type {
        NULL(0x0000), STRING_POOL(0x0001), TABLE(0x0002), XML(0x0003),

        // Sub Types for XML
        XML_FIRST_CHUNK(0x0100),
        XML_START_NS(0x0100),
        XML_END_NS(0x0101),
        XML_START_ELEM(0x0102),
        XML_END_ELEM(0x0103),
        XML_CDATA(0x0104),
        XML_LAST_CHUNK(0x017f),
        XML_RESOURCE_MAP(0x0180),

        // Sub Types for type TABLE (0x0002)
        TABLE_PACKAGE(0x0200),
        TABLE_TYPE(0x0201),
        TABLE_TYPE_SPEC(0x0202);

        private final int id;

        private Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Type fromId(short typeId) {
            for (Type type : values()) {
                if (type.id == typeId) {
                    return type;
                }
            }
            return null;
        }
    }

    protected final int location;
    protected final short typeId;
    protected final Type type;
    protected final short headerSize;
    protected final int dataSize;

    public Chunk(ByteBuffer buf) throws ParseException {
        this.location = buf.position();
        this.typeId = buf.getShort();
        this.type = Type.fromId(typeId);
        if (type == null) {
            throw new ParseException(String.format("Unrecognized type: 0x%04X", typeId));
        }
        this.headerSize = buf.getShort();
        this.dataSize = buf.getInt();
        LOG.debug("[0x%04X] Chunk - %s", this.location, this.type);
    }

    public void setPositionAfter(ByteBuffer buf) {
        buf.position(location + dataSize);
    }
}
