package net.erdfelt.android.apk;

import java.io.IOException;

import org.apache.commons.io.input.SwappedDataInputStream;

public class Attribute {
    private static final int TYPE_STRING = 0x03000008;
    private static final int TYPE_ID_REF = 0x01000008;
    private static final int TYPE_INT    = 0x10000008;

    private String           namespaceUri;
    private String           name;
    private String           value;

    public Attribute(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public Attribute(SwappedDataInputStream le, StringsChunk strings) throws IOException {
        // Attribute layout
        // word0 : namespace Uri index
        // word1 : name index
        // word2 : value string index
        // word3 : value type
        // word4 : value data
        int nsUriIndex = le.readInt();
        int nameIndex = le.readInt();
        int valueIndex = le.readInt();
        int valueType = le.readInt();
        int valueData = le.readInt();

        this.namespaceUri = strings.getString(nsUriIndex);
        this.name = strings.getString(nameIndex);
        this.value = strings.getString(valueIndex);

        switch (valueType) {
            case TYPE_STRING:
                this.value = strings.getString(valueData);
                break;
            case TYPE_INT:
                this.value = Integer.toString(valueData);
                break;
            case TYPE_ID_REF:
                this.value = "@id/" + Integer.toString(valueData);
                break;
            default:
                this.value= String.format("(0x%08X/0x%08X)", valueType, valueData);
                break;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (namespaceUri != null) {
            sb.append(namespaceUri).append(':');
        }
        sb.append(name);
        sb.append("=\"").append(value).append("\"");
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
