package net.erdfelt.android.apk.xml;

import java.io.IOException;

public class Attribute {
    private static final int TYPE_STRING = 0x03000008;
    private static final int TYPE_ID_REF = 0x01000008;
    private static final int TYPE_INT    = 0x10000008;

    private Namespace        namespace;
    private String           name;
    private String           value;

    public Attribute(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public Attribute(BinaryXmlInputStream le) throws IOException {
        // Attribute layout
        // word0 : namespace Uri index
        // word1 : name index
        // word2 : value string index
        // word3 : value type
        // word4 : value data
        this.namespace = le.readNamespaceRef();
        this.name = le.readStringRef();
        this.value = le.readStringRef();
        int valueType = le.readInt();
        int valueData = le.readInt();

        switch (valueType) {
            case TYPE_STRING:
                this.value = le.getString(valueData);
                break;
            case TYPE_INT:
                this.value = Integer.toString(valueData);
                break;
            case TYPE_ID_REF:
                this.value = String.format("@id/0x%08X", valueData);
                break;
            default:
                this.value = String.format("(0x%08X/0x%08X)", valueType, valueData);
                break;
        }
    }

    @Override
    public String toString() {
        return String.format("%s%s=\"%s\"", namespace, name, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
