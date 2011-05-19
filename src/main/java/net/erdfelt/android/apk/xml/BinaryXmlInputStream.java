package net.erdfelt.android.apk.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.erdfelt.android.apk.io.LEInputStream;

public class BinaryXmlInputStream extends LEInputStream {
    /**
     * String lookup table.
     * 
     * @see BinaryXmlInputStream#getString(int)
     * @see BinaryXmlInputStream#readStringRef()
     */
    private String[]               stringtable  = new String[0];

    /**
     * Namespace References Key=uri, Value=Namespace
     */
    private Map<String, Namespace> namespaceMap = new HashMap<String, Namespace>();

    public BinaryXmlInputStream(InputStream in) {
        super(in);
    }

    private int copyShort(byte[] buf, int offset) {
        int value = 0;
        value += (buf[offset + 1] << 8) & 0xff00;
        value |= (buf[offset]) & 0xff;
        return value;
        // return (int) buf[offset + 1] << 8 & 0xff00 | buf[offset] & 0xff;
    }

    private String copyUTF16(byte[] buf, int offset) throws UnsupportedEncodingException {
        if (offset < 0) {
            return null;
        }
        int len = copyShort(buf, offset);
        byte chars[] = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            chars[(i * 2) + 1] = buf[offset + 2 + (i * 2)];
            chars[(i * 2)] = buf[offset + 2 + (i * 2) + 1];
        }
        return new String(chars, "UTF-16");
    }

    public String getString(int index) {
        if (index < 0) {
            return null;
        }
        return stringtable[index];
    }

    public int readChunkId() throws IOException {
        int id = readInt();
        // System.out.printf("CHUNK - 0x%08X%n", chunkId);
        return id;
    }

    public int readChunkSize() throws IOException {
        int size = readInt();
        // System.out.printf("Chunk Size - %,d%n", size);
        return size;
    }

    @SuppressWarnings("unused")
    public void readNamespaceDef(boolean addDef) throws IOException {
        // Common Header
        int chunkSize = readChunkSize();
        int lineNumber = readInt();
        skipInt(); // ?? 0xFFFFFFFF

        // Specific Header
        String prefix = readStringRef();
        String uri = readStringRef();
        if (addDef) {
            Namespace namespace = new Namespace(prefix, uri);
            this.namespaceMap.put(uri, namespace);
        } else {
            this.namespaceMap.remove(uri);
        }
    }

    public Namespace readNamespaceRef() throws IOException {
        String uri = readStringRef();
        if (uri == null) {
            return Namespace.EMPTY;
        }
        return namespaceMap.get(uri);
    }

    public String readStringRef() throws IOException {
        int index = readInt();
        return getString(index);
    }

    @SuppressWarnings("unused")
    public void readStringTableDef() throws IOException {
        int chunkSize = readInt();
        int stringCount = readInt();
        int styleCount = readInt();
        skipInt(); // ??
        int offsetStrings = readInt();
        int offsetStyles = readInt();

        // System.out.printf("Chunk Size = %d bytes (%d words)%n", chunkSize, (chunkSize / 4));
        // System.out.printf("String Count = %d%n", stringCount);
        // System.out.printf("Style Count = %d%n", styleCount);
        // System.out.printf("Offset Strings = %d%n", offsetStrings);
        // System.out.printf("Offset Styles = %d%n", offsetStyles);

        int stringOffsets[] = readIntArray(stringCount);
        int styleOffsets[] = readIntArray(styleCount);

        int bufsize = chunkSize - offsetStrings;
        if (offsetStyles > 0) {
            bufsize = offsetStyles - offsetStrings;
        }
        byte rawstrings[] = readByteArray(bufsize);

        this.stringtable = new String[stringCount];
        // this.styletable = new Style[styleCount];
        // TODO: load style table!?

        for (int i = 0; i < stringCount; i++) {
            this.stringtable[i] = copyUTF16(rawstrings, stringOffsets[i]);
            // System.out.printf("Strings[%d] = \"%s\"%n", i, this.stringtable[i]);
        }
    }
}
