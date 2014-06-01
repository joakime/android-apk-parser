package net.erdfelt.android.apk.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LeanConsoleHandler extends Handler {
    public static Handler createWithLevel(Level level) {
        LeanConsoleHandler handler = new LeanConsoleHandler();
        handler.setLevel(level);
        return handler;
    }

    @Override
    public void close() throws SecurityException {
        /* nothing to do here */
    }

    @Override
    public void flush() {
        /* nothing to do here */
    }

    @Override
    public void publish(LogRecord record) {
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(record.getLevel().getName()).append("] ");
        String logname = record.getLoggerName();
        int idx = logname.lastIndexOf('.');
        if (idx > 0) {
            logname = logname.substring(idx + 1);
        }
        buf.append(logname);
        buf.append(": ");
        buf.append(record.getMessage());

        System.out.println(buf.toString());
        if (record.getThrown() != null) {
            record.getThrown().printStackTrace(System.out);
        }
    }
}
