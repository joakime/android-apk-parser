package net.erdfelt.android.apk.arsc;

public enum ChunkType {
    NULL(0x0000), 
    STRING_POOL(0x0001), 
    TABLE(0x0002), 
    XML(0x0003),

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

    private ChunkType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ChunkType fromId(short typeId) {
        for (ChunkType type : values()) {
            if (type.id == typeId) {
                return type;
            }
        }
        return null;
    }
}
