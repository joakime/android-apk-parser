package net.erdfelt.android.apk;

import net.erdfelt.android.apk.io.IO;
import net.erdfelt.android.apk.xml.Attribute;
import net.erdfelt.android.apk.xml.BinaryXmlListener;
import net.erdfelt.android.apk.xml.BinaryXmlParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class AndroidApk {
    private String appVersion;
    private String appVersionCode;
    private String packageName;
    private String minSdkVersion;
    private String targetSdkVersion;
    private String maxSdkVersion;

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

            if ("uses-sdk".equals(name)) {
                for (Attribute attrib : attrs) {
                    if ("minSdkVersion".equals(attrib.getName())) {
                        minSdkVersion = attrib.getValue();
                    } else if ("targetSdkVersion".equals(attrib.getName())) {
                        targetSdkVersion = attrib.getValue();
                    } else if ("maxSdkVersion".equals(attrib.getName())) {
                        maxSdkVersion = attrib.getValue();
                    }
                }
            }
        }
    }

    public AndroidApk(File apkfile) throws ZipException, IOException {
        ZipFile zip = null;
        InputStream in = null;
        try {
            zip = new ZipFile(apkfile);
            ZipEntry manifestEntry = zip.getEntry("AndroidManifest.xml");
            if (manifestEntry == null) {
                throw new FileNotFoundException("Cannot find AndroidManifest.xml in apk");
            }

            in = zip.getInputStream(manifestEntry);
            parseStream(in);
        } finally {
            IO.close(in);
            try {
                if (zip != null) {
                    zip.close();
                }
            } catch (IOException ignore) {
                /* ignore */
            }
        }
    }

    /**
     * Takes as an input APK as a stream. At the end, the stream is closed.
     * 
     * @param apkfileInputStream
     *            apk file stream
     * @throws IOException
     *             in case of error of reading/parsing data
     */
    public AndroidApk(InputStream apkfileInputStream) throws IOException {
        InputStream in = null;
        try {
            final ZipInputStream is = new ZipInputStream(apkfileInputStream);
            ZipEntry ze;
            while (((ze = is.getNextEntry()) != null) && !ze.getName().endsWith("AndroidManifest.xml")) {
                // continue
            }
            in = is;
            if (ze == null) {
                throw new FileNotFoundException("Cannot find AndroidManifest.xml in apk");
            }

            parseStream(in);
        } finally {
            IO.close(in);
        }
    }

    private void parseStream(InputStream in) throws IOException {
        BinaryXmlParser parser = new BinaryXmlParser();
        // parser.addListener(new BinaryXmlDump());
        parser.addListener(new ManifestListener());
        parser.parse(in);
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

    public String getMinSdkVersion() {
        return minSdkVersion;
    }

    public String getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public String getMaxSdkVersion() {
        return maxSdkVersion;
    }

}
