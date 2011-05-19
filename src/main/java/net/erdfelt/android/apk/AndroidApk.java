package net.erdfelt.android.apk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.jetty.toolchain.test.IO;

public class AndroidApk {
    private String appVersion;
    private String appVersionCode;
    private String packageName;

    public AndroidApk(File apkfile) throws ZipException, IOException {
        ZipFile zip = new ZipFile(apkfile);
        ZipEntry manifestEntry = zip.getEntry("AndroidManifest.xml");

        InputStream in = null;
        try {
            in = zip.getInputStream(manifestEntry);
            BinaryXmlParser parser = new BinaryXmlParser();
            parser.addListener(new BinaryXmlDump());
            parser.parse(in);
        } finally {
            IO.close(in);
        }
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public String getPackageName() {
        return packageName;
    }
}
