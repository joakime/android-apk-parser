package net.erdfelt.android.apk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.input.SwappedDataInputStream;

public class StringsChunk {
    public static class Style {
        private Style(SwappedDataInputStream le) throws IOException {
            tagNameIndex = le.readInt();
            tagStartIndex = le.readInt();
            tagEndIndex = le.readInt();
        }

        int tagNameIndex;
        int tagStartIndex;
        int tagEndIndex;
    }

    private int[]    stringOffsets;
    private String[] strings;
    private int[]    styleOffsets;
    private Style[]  styles;

    public StringsChunk() {
        this.strings = new String[0];
        this.styles = new Style[0];
    }

    public void parse(SwappedDataInputStream le) throws IOException {
        int chunkSize = le.readInt();
        int stringCount = le.readInt();
        int styleCount = le.readInt();
        le.readInt(); // ??
        int offsetStrings = le.readInt();
        int offsetStyles = le.readInt();

        System.out.printf("Chunk Size = %d bytes (%d words)%n", chunkSize, (chunkSize / 4));
        System.out.printf("String Count = %d%n", stringCount);
        System.out.printf("Style Count = %d%n", styleCount);
        System.out.printf("Offset Strings = %d%n", offsetStrings);
        System.out.printf("Offset Styles = %d%n", offsetStyles);

        this.stringOffsets = readIntArray(le, stringCount);
        this.styleOffsets = readIntArray(le, styleCount);

        int bufsize = chunkSize - offsetStrings;
        if (offsetStyles > 0) {
            bufsize = offsetStyles - offsetStrings;
        }
        byte rawstrings[] = new byte[bufsize];
        le.readFully(rawstrings);
        System.out.printf("Strings Raw Byte Count = %d%n", bufsize);
        System.out.printf("Leftover = (%d - %d) = %d%n", chunkSize, bufsize, (chunkSize - bufsize));

        this.strings = new String[stringCount];
        this.styles = new Style[styleCount];

        for (int i = 0; i < stringCount; i++) {
            this.strings[i] = asRaw(rawstrings, stringOffsets[i]);
            System.out.printf("Strings[%d] = \"%s\"%n", i, this.strings[i]);
        }
    }

    private String asRaw(byte[] buf, int offset) throws UnsupportedEncodingException {
        if (offset < 0) {
            return null;
        }
        int len = getShort(buf, offset);
        byte chars[] = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            chars[(i * 2) + 1] = buf[offset + 2 + (i * 2)];
            chars[(i * 2)] = buf[offset + 2 + (i * 2) + 1];
        }
        return new String(chars, "UTF-16");
    }

    private int getShort(byte[] buf, int offset) {
        return (int) buf[offset + 1] << 8 & 0xff00 | buf[offset] & 0xff;
    }

    private int[] readIntArray(SwappedDataInputStream le, int len) throws IOException {
        int arr[] = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = le.readInt();
        }
        return arr;
    }

    public String getString(int index) {
        if (index == (-1)) {
            return null;
        }
        return this.strings[index];
    }
}
