package net.erdfelt.android.apk.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input Stream for dealing with Little Endian values.
 */
public class LEInputStream extends FilterInputStream {
    public LEInputStream(InputStream in) {
        super(in);
    }

    /**
     * Read a byte.
     * 
     * @return the byte
     * @throws IOException
     */
    public byte readByte() throws IOException {
        return (byte) read();
    }

    /**
     * Read 16bit integer.
     * 
     * @return the integer value
     * @throws IOException
     */
    public short readShort() throws IOException {
        int value = 0;
        value += (read() & 0xff) << 0;
        value += (read() & 0xff) << 8;
        return (short) value;
    }

    /**
     * Skip a 16bit integer.
     * 
     * @throws IOException
     */
    public void skipShort() throws IOException {
        readShort();
    }

    /**
     * Read 32bit word as integer.
     * 
     * @return the integer value
     * @throws IOException
     */
    public int readInt() throws IOException {
        int value = 0;
        value += (read() & 0xff) << 0;
        value += (read() & 0xff) << 8;
        value += (read() & 0xff) << 16;
        value += (read() & 0xff) << 24;
        return (int) value;
    }

    /**
     * Skip 32bit word.
     * 
     * @throws IOException
     */
    public void skipInt() throws IOException {
        readInt();
    }

    public int unknownInt(String msg) throws IOException {
        int unknown = readInt();
        System.out.printf("Unknown/%s = 0x%08X (%d)%n", msg, unknown, unknown);
        return unknown;
    }

    /**
     * Read an array of 32bit words.
     * 
     * @param length
     *            size of the array (in 32bit word count, not byte count)
     * @return the array of 32bit words
     * @throws IOException
     */
    public int[] readIntArray(int length) throws IOException {
        int arr[] = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = readInt();
        }
        return arr;
    }

    /**
     * Read an array of bytes.
     * 
     * @param length
     *            size of the array (in bytes)
     * @return the array of bytes
     * @throws IOException
     */
    public byte[] readByteArray(int length) throws IOException {
        byte buf[] = new byte[length];
        for (int i = 0; i < length; i++) {
            buf[i] = (byte) read();
        }
        return buf;
    }
    
    /**
     * Read null terminated string
     * 
     * @param length
     *            max size of the string (in bytes)
     * @return the array of bytes
     * @throws IOException
     */
    public String readNullString(int length) throws IOException {
        byte buf[] = new byte[length];
        for (int i = 0; i < length; i++) {
            buf[i] = (byte) read();
            if(buf[i] == 0x00) {
                return new String(buf,0,i,BufUtil.UTF8);
            }
        }
        return new String(buf,0,length,BufUtil.UTF8);
    }
}
