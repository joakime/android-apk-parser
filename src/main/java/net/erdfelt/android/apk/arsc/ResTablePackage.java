package net.erdfelt.android.apk.arsc;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.erdfelt.android.apk.io.BufUtil;
import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.Dumpable;

public class ResTablePackage implements Res, Dumpable {
    private static final Logger LOG = Logger.getLogger(ResTablePackage.class.getName());
    
    private Res resource;
    private int packageId;
    private String packageName;
    private int typeStrings;
    private int lastPublicType;
    private int keyStrings;
    private int lastPublicKey;

    public void parseData(ByteBuffer buf) throws ParseException {
    }

    public void parseHeader(ByteBuffer buf) throws ParseException {
        this.packageId = buf.getInt();
        debug("packageName.pos = 0x%08X", buf.position());
        this.packageName = BufUtil.readLZString(buf, 128);
        this.typeStrings = buf.getInt();
        this.lastPublicType = buf.getInt();
        this.keyStrings = buf.getInt();
        this.lastPublicKey = buf.getInt();
    }

    public void parse(ByteBuffer buf) throws ParseException {
        this.resource = AppResources.parseChunk(buf, this);
    }

    public int getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getTypeStrings() {
        return typeStrings;
    }

    public int getLastPublicType() {
        return lastPublicType;
    }

    public int getKeyStrings() {
        return keyStrings;
    }

    public int getLastPublicKey() {
        return lastPublicKey;
    }

    public void dump(PrintStream out, String indent) {
        out.printf("%s PACKAGE [ name           = %s %n", indent, packageName);
        out.printf("%s           packageId      = %,d %n", indent, packageId);
        out.printf("%s           typeStrings    = %,d %n", indent, typeStrings);
        out.printf("%s           lastPublicType = %,d %n", indent, lastPublicType);
        out.printf("%s           keyStrings     = %,d %n", indent, keyStrings);
        out.printf("%s           lastPublicKey  = %,d ]%n", indent, lastPublicKey);

        if (resource instanceof Dumpable) {
            ((Dumpable) resource).dump(out, indent + "  ");
        }
    }
    
    private static void debug(String format, Object... args) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format(format, args));
        }
    }
}