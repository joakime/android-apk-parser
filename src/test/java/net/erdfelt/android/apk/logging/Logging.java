package net.erdfelt.android.apk.logging;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

import org.eclipse.jetty.toolchain.test.IO;

public class Logging {
    public static void config() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource("logging.properties");
        if (url != null) {
            InputStream in = null;
            try {
                in = url.openStream();
                LogManager.getLogManager().readConfiguration(in);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            } finally {
                IO.close(in);
            }
        }

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
    }
}
