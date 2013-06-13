package net.erdfelt.android.apk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.erdfelt.android.apk.io.IO;
import net.erdfelt.android.apk.xml.Attribute;
import net.erdfelt.android.apk.xml.BinaryXmlListener;
import net.erdfelt.android.apk.xml.BinaryXmlParser;

public class AndroidApk {
    private String appVersion;
    private String appVersionCode;
    private String packageName;

    private class ManifestListener implements BinaryXmlListener {
        public void onXmlEntry(String path, String name, Attribute... attrs) {
            if ("//".equals(path) && "manifest".equals(name)) {
                for (Attribute attrib : attrs) {
                    if ("package".equals(attrib.getName())) {
                        packageName = attrib.getValue();
                    } else if ("versionName".equals(attrib.getName())) {
                        appVersion = attrib.getValue();
                    } else if ("versionCode".equals(attrib.getName())) {
                        appVersionCode = attrib.getValue();
                    }
                }
            }
        }
    }

    public AndroidApk(File apkfile) throws ZipException, IOException {
        ZipFile zip = new ZipFile(apkfile);
        ZipEntry manifestEntry = zip.getEntry("AndroidManifest.xml");

        InputStream in = null;
        try {
            in = zip.getInputStream(manifestEntry);
            BinaryXmlParser parser = new BinaryXmlParser();
            // parser.addListener(new BinaryXmlDump());
            parser.addListener(new ManifestListener());
            parser.parse(in);
        } finally {
            IO.close(in);
            try {
                if(zip != null) {
                    zip.close();
                }
            } catch(IOException ignore) {
                /* ignore */
            }
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
