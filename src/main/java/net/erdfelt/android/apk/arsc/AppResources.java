package net.erdfelt.android.apk.arsc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

import net.erdfelt.android.apk.io.IO;
import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.Dumpable;
import net.erdfelt.android.apk.util.FormattedLog;

public class AppResources implements Dumpable {
    private static final FormattedLog LOG = new FormattedLog(AppResources.class);
    private ResStringPool stringPool;
    private List<ResTablePackage> packages = new ArrayList<ResTablePackage>();

    public AppResources(File file) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer bb = in.getChannel().map(MapMode.READ_ONLY, 0, file.length());
            bb.order(ByteOrder.LITTLE_ENDIAN);
            parse(bb);
        } finally {
            IO.close(in);
        }
    }

    public AppResources(InputStream in, int size) throws IOException {
        byte buf[] = new byte[size];
        int length = in.read(buf, 0, size);
        if (length != size) {
            throw new IOException(String.format("Only read %,d bytes of %,d expected bytes", length, size));
        }
        ByteBuffer bb = ByteBuffer.wrap(buf);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        parse(bb);
    }

    /**
     * Parse the entire resources content
     * 
     * @param buf
     *            the resources buffer
     * @throws ParseException
     */
    private void parse(ByteBuffer buf) throws ParseException {
        Chunk mainChunk = new Chunk(buf);
        // expect Table type
        if (mainChunk.type != Chunk.Type.TABLE) {
            throw new ParseException(String.format("Unexpected chunk type: 0x%04X (expected %s:0x%04X)",
                    mainChunk.typeId, mainChunk.type.name(), mainChunk.type.getId()));
        }

        // package count
        int packageCount = buf.getInt();
        int actualPackageCount = 0;

        while (buf.hasRemaining()) {
            Chunk chunk = new Chunk(buf);
            LOG.debug("[0x%04X] Chunk - %s", chunk.location, chunk.type);
            switch (chunk.type) {
            case STRING_POOL:
                stringPool = new ResStringPool();
                stringPool.parse(chunk, buf);
                break;
            case TABLE_PACKAGE:
                actualPackageCount++;
                ResTablePackage pkg = new ResTablePackage();
                pkg.parse(chunk, buf);
                packages.add(pkg);
                break;
            default:
                break;
            }
            // skip to next chunk start location
            chunk.setPositionAfter(buf);
        }

        if (actualPackageCount != packageCount) {
            throw new ParseException("Package count mismatch: expected " + packageCount + ", but only found "
                    + actualPackageCount);
        }
    }

    public void dump(PrintStream out, String indent) {
        out.printf("AppResources%n");
        if (stringPool != null) {
            stringPool.dump(out, indent + "  ");
        }
        for (ResTablePackage pkg : packages) {
            pkg.dump(out, indent + "  ");
        }
    }
}
