package net.erdfelt.android.apk.arsc;

import java.nio.ByteBuffer;

import net.erdfelt.android.apk.io.ParseException;

public interface Res {
    void parseHeader(ByteBuffer buf) throws ParseException;

    void parseData(ByteBuffer buf) throws ParseException;
}
