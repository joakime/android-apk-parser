package net.erdfelt.android.apk.arsc;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.Dumpable;

public class ResTable implements Res, Dumpable {
    // header fields
    private int packageCount;

    private List<ResTablePackage> packages = new ArrayList<ResTablePackage>();

    public void parseHeader(ByteBuffer buf) throws ParseException {
        packageCount = buf.getInt();
    }

    public void parseData(ByteBuffer buf) throws ParseException {
        for (int i = 0; i < packageCount; i++) {
            ResTablePackage pkg = new ResTablePackage();
            pkg.parse(buf);
            packages.add(pkg);
        }
    }

    public void dump(PrintStream out, String indent) {
        out.print(indent);
        out.printf("TABLE [ packageCount = %,d ]%n", packageCount);
        for (ResTablePackage pkg : packages) {
            if (pkg instanceof Dumpable) {
                ((Dumpable) pkg).dump(out, indent + "  ");
            }
        }
    }
}
