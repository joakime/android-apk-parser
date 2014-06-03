package net.erdfelt.android.apk.arsc;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.erdfelt.android.apk.io.ParseException;
import net.erdfelt.android.apk.util.Dumpable;

public class ResTable implements Dumpable {
    // header fields
    private int packageCount;

    private List<ResTablePackage> packages = new ArrayList<ResTablePackage>();

    public void parse(Chunk chunk, ByteBuffer buf) throws ParseException {
        packageCount = buf.getInt();

        for (int i = 0; i < packageCount; i++) {
            ResTablePackage pkg = new ResTablePackage();
            pkg.parse(chunk, buf);
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
