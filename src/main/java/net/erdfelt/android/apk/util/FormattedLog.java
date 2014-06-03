package net.erdfelt.android.apk.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FormattedLog {
    private final Logger log;

    public FormattedLog(Class<?> clazz) {
        this.log = Logger.getLogger(clazz.getName());
    }

    public void info(String format, Object... args) {
        if (log.isLoggable(Level.INFO)) {
            log.info(String.format(format, args));
        }
    }

    public void debug(String format, Object... args) {
        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format(format, args));
        }
    }

    public void warn(String format, Object... args) {
        log.warning(String.format(format, args));
    }
}
