package net.erdfelt.android.apk;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.input.SwappedDataInputStream;

public class BinaryXmlParser {

    public static final int         AXML_FILE        = 0x00080003;
    public static final int         RESOURCE_IDS     = 0x00080180;

    public static final int         STRING_TABLE     = 0x001C0001;

    public static final int         DOC_START        = 0x00100100;
    public static final int         DOC_END          = 0x00100104;

    public static final int         NS_START         = 0x00100100;
    public static final int         NS_END           = 0x00100101;
    public static final int         TAG_START        = 0x00100102;
    public static final int         TAG_END          = 0x00100103;

    public static final int         XML_TEXT         = 0x00100104;
    private static final int        ATTRIBUTE_LENGTH = 5;

    private List<BinaryXmlListener> listeners        = new ArrayList<BinaryXmlListener>();

    public void addListener(BinaryXmlListener listener) {
        listeners.add(listener);
    }

    public synchronized void parse(InputStream in) throws IOException {
        SwappedDataInputStream le = new SwappedDataInputStream(in);

        try {
            boolean done = false;
            while (!done) {
                done = parseChunk(le);
            }
        } catch (EOFException e) {
            // Reached end of stream.
        }
    }

    private StringsChunk stringsChunk = new StringsChunk();

    private boolean parseChunk(SwappedDataInputStream le) throws IOException {
        int chunkId = le.readInt();
        // System.out.printf("CHUNK - 0x%08X%n", chunkId);
        switch (chunkId) {
            case AXML_FILE:
                skipInt(le); // Chunk Size
                break;
            case STRING_TABLE:
                stringsChunk.parse(le);
                break;
            case RESOURCE_IDS:
                parseResourceIds(le);
                break;
            case NS_START:
                parseNamespaceStart(le);
                break;
            case NS_END:
                parseNamespaceEnd(le);
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
            default:
                System.out.printf("Unknown Chunk ID 0x%08X (%d)%n", chunkId, chunkId);
                for (int i = 0; i < 8; i++) {
                    unknownInt(le, "debug");
                }
                return true;
        }
        return false;
    }

    private void parseText(SwappedDataInputStream le) throws IOException {
        int nameIdx = le.readInt();
        skipInt(le); // ??
        skipInt(le); // ??

        String name = stringsChunk.getString(nameIdx);
        System.out.printf("# Text \"%s\"%n", name);
    }

    private void parseTagEnd(SwappedDataInputStream le) throws IOException {
        // Common Header
        int chunkSize = le.readInt();
        int lineNumber = le.readInt();
        skipInt(le); // ?? 0xFFFFFFFF

        // Tag Specific
        int namespaceUriIdx = le.readInt();
        int nameIdx = le.readInt();

        String namespace = stringsChunk.getString(namespaceUriIdx);
        String name = stringsChunk.getString(nameIdx);
        System.out.printf("</%s%s>%n", ns(namespace), name);
    }

    private String ns(String namespace) {
        if (namespace == null) {
            return "";
        }
        return namespace + ":";
    }

    private void parseTag(SwappedDataInputStream le) throws IOException {
        // Common Header
        int chunkSize = le.readInt();
        int lineNumber = le.readInt();
        skipInt(le); // ?? 0xFFFFFFFF

        // Tag Specific
        int namespaceUriIdx = le.readInt();
        int nameIdx = le.readInt();
        skipInt(le); // flags?
        int attributeCount = le.readInt();
        int idAttribute = (attributeCount >>> 16) - 1; // TODO: readShort
        attributeCount &= 0xFFFF;  // TODO: readShort
        int classAttribute = le.readInt();
        int styleAttribute = (classAttribute >>> 16) - 1; // TODO: readShort
        classAttribute = (classAttribute & 0xFFFF) - 1; // TODO: readShort
        
        Attribute attribs[] = new Attribute[attributeCount];
        for(int i=0; i<attributeCount; i++) {
            attribs[i] = new Attribute(le, stringsChunk);
        }
        
        String namespace = stringsChunk.getString(namespaceUriIdx);
        String name = stringsChunk.getString(nameIdx);
        System.out.printf("<%s%s ", ns(namespace), name);
        for(Attribute attrib: attribs) {
            System.out.print(attrib);
            System.out.print(' ');
        }
        System.out.println(">");
    }

    private Stack<Namespace> namespaceStack = new Stack<Namespace>();

    private void parseNamespaceStart(SwappedDataInputStream le) throws IOException {
        // Common Header
        int chunkSize = le.readInt();
        int lineNumber = le.readInt();
        skipInt(le); // ?? 0xFFFFFFFF

        // Specific Header
        int prefixIdx = le.readInt();
        int uriIdx = le.readInt();

        String prefix = stringsChunk.getString(prefixIdx);
        String uri = stringsChunk.getString(uriIdx);

        System.out.printf("Namespace: %s:%s%n", prefix, uri);

        namespaceStack.push(new Namespace(prefix, uri));
    }

    private void parseNamespaceEnd(SwappedDataInputStream le) throws IOException {
        // Common Header
        int chunkSize = le.readInt();
        int lineNumber = le.readInt();
        skipInt(le); // ?? 0xFFFFFFFF

        // Specific Header
        int prefixIdx = le.readInt();
        int uriIdx = le.readInt();

        namespaceStack.pop();
    }

    private int[] resourceIds;

    private void parseResourceIds(SwappedDataInputStream le) throws IOException {
        int chunkSize = le.readInt();
        resourceIds = readIntArray(le, (chunkSize / 4) - 2);
    }

    private void skipInt(SwappedDataInputStream le) throws IOException {
        le.readInt();
    }

    private int[] readIntArray(SwappedDataInputStream le, int len) throws IOException {
        int arr[] = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = le.readInt();
        }
        return arr;
    }

    private String readUtf16(SwappedDataInputStream le, int len) throws IOException {
        byte buf[] = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            buf[(i * 2) + 1] = le.readByte();
            buf[(i * 2)] = le.readByte();
        }
        int ntz = le.readShort();
        if (ntz != 0) {
            throw new IOException("Improperly formatted UTF-16 (LE) String, missing Null Terminated Ending");
        }
        return new String(buf, "UTF-16");
    }

    protected static int unknownInt(SwappedDataInputStream le, String msg) throws IOException {
        int unknown = le.readInt();
        System.out.printf("Unknown/%s = 0x%08X (%d)%n", msg, unknown, unknown);
        return unknown;
    }
}
