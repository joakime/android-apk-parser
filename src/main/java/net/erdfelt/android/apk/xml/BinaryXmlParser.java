package net.erdfelt.android.apk.xml;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BinaryXmlParser {
    public static final int         END_OF_STREAM = 0xFFFFFFFF;

    public static final int         AXML_FILE     = 0x00080003;
    public static final int         RESOURCE_IDS  = 0x00080180;

    public static final int         STRING_TABLE  = 0x001C0001;

    public static final int         DOC_START     = 0x00100100;
    public static final int         DOC_END       = 0x00100104;

    public static final int         NS_START      = 0x00100100;
    public static final int         NS_END        = 0x00100101;
    public static final int         TAG_START     = 0x00100102;
    public static final int         TAG_END       = 0x00100103;

    public static final int         XML_TEXT      = 0x00100104;

    private List<BinaryXmlListener> listeners     = new ArrayList<BinaryXmlListener>();
    @SuppressWarnings("unused")
    private int[]                   resourceIds;

    public void addListener(BinaryXmlListener listener) {
        listeners.add(listener);
    }

    public synchronized void parse(InputStream in) throws IOException {
        BinaryXmlInputStream le = new BinaryXmlInputStream(in);

        try {
            boolean done = false;
            while (!done) {
                done = parseChunk(le);
            }
        } catch (EOFException e) {
            // Reached end of stream.
        }
    }

    private boolean parseChunk(BinaryXmlInputStream le) throws IOException {
        int chunkId = le.readChunkId();
        switch (chunkId) {
            case AXML_FILE:
                le.skipInt(); // Chunk Size
                break;
            case STRING_TABLE:
                le.readStringTableDef();
                break;
            case RESOURCE_IDS:
                parseResourceIds(le);
                break;
            case NS_START:
                le.readNamespaceDef(true);
                break;
            case NS_END:
                le.readNamespaceDef(false);
                break;
            case TAG_START:
                parseTag(le);
                break;
            case TAG_END:
                parseTagEnd(le);
                break;
            case XML_TEXT:
                parseText(le);
                break;
            case END_OF_STREAM:
                return true;
            default:
                System.out.printf("Unknown Chunk ID 0x%08X (%d)%n", chunkId, chunkId);
                for (int i = 0; i < 8; i++) {
                    le.unknownInt("debug");
                }
                return true;
        }
        return false;
    }

    private void parseResourceIds(BinaryXmlInputStream le) throws IOException {
        int chunkSize = le.readInt();
        resourceIds = le.readIntArray(((chunkSize / 4) - 2));
    }

    @SuppressWarnings("unused")
    private void parseTag(BinaryXmlInputStream le) throws IOException {
        // Common Header
        int chunkSize = le.readInt();
        int lineNumber = le.readInt();
        le.skipInt(); // ?? 0xFFFFFFFF

        // Tag Specific
        Namespace namespace = le.readNamespaceRef();
        String name = le.readStringRef();
        le.skipInt(); // flags?
        int attributeCount = le.readInt();
        int idAttribute = (attributeCount >>> 16) - 1; // TODO: readShort
        attributeCount &= 0xFFFF; // TODO: readShort
        int classAttribute = le.readInt();
        int styleAttribute = (classAttribute >>> 16) - 1; // TODO: readShort
        classAttribute = (classAttribute & 0xFFFF) - 1; // TODO: readShort

        Attribute attribs[] = new Attribute[attributeCount];
        for (int i = 0; i < attributeCount; i++) {
            attribs[i] = new Attribute(le);
        }

        String path = getCurrentPath();
        for (BinaryXmlListener listener : listeners) {
            listener.onXmlEntry(path, name, attribs);
        }
        pathStack.push(name);
    }

    private Stack<String> pathStack = new Stack<String>();

    private String getCurrentPath() {
        StringBuilder sb = new StringBuilder();
        sb.append("//");
        for (String path : pathStack) {
            sb.append(path).append("/");
        }
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private void parseTagEnd(BinaryXmlInputStream le) throws IOException {
        // Common Header
        int chunkSize = le.readInt();
        int lineNumber = le.readInt();
        le.skipInt(); // ?? 0xFFFFFFFF

        // Tag Specific
        Namespace namespace = le.readNamespaceRef();
        String name = le.readStringRef();

        pathStack.pop();
        // System.out.printf("</%s%s>%n", namespace, name);
    }

    private void parseText(BinaryXmlInputStream le) throws IOException {
        String text = le.readStringRef();
        le.skipInt(); // ??
        le.skipInt(); // ??

        System.out.printf("# Text \"%s\"%n", text);
    }
}
