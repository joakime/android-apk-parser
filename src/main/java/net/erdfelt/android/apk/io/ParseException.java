package net.erdfelt.android.apk.io;

import java.io.IOException;

@SuppressWarnings("serial")
public class ParseException extends IOException {
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(String message) {
        super(message);
    }
}
