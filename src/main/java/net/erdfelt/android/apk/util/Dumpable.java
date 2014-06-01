package net.erdfelt.android.apk.util;

import java.io.PrintStream;

public interface Dumpable {
    void dump(PrintStream out, String indent);
}
