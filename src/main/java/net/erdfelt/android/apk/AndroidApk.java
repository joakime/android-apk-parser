package net.erdfelt.android.apk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import net.erdfelt.android.apk.io.IO;
import net.erdfelt.android.apk.util.FormattedLog;
import net.erdfelt.android.apk.xml.Attribute;
import net.erdfelt.android.apk.xml.BinaryXmlListener;
import net.erdfelt.android.apk.xml.BinaryXmlParser;

public class AndroidApk {
    private static final FormattedLog LOG = new FormattedLog(AndroidApk.class);
    
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
        InputStream in = null;
        try {
            in = new FileInputStream(apkfile);
            readZip(in);
        } finally {
            IO.close(in);
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
        readZip(apkfileInputStream);
    }

    private void readZip(InputStream in) throws IOException, FileNotFoundException {
        ZipInputStream zis = null;
        
        try {
            zis = new ZipInputStream(in);
            
            boolean foundManifest = false;
            ZipEntry ze;
            while (((ze = zis.getNextEntry()) != null))
            {
                String name = ze.getName();
                LOG.debug("Entry: %s",name);
                if(name.equalsIgnoreCase("AndroidManifest.xml"))
                {
                    foundManifest = true;
                    parseXmlEntry(ze, zis, new ManifestListener());
                } else if(name.equalsIgnoreCase("resources.arsc"))
                {
                    // TODO parseArscEntry(ze, zis, new ResourceListener());
                }
            }
            
            if (!foundManifest) {
                throw new FileNotFoundException("Cannot find AndroidManifest.xml in apk");
            }
        } finally {
            IO.close(zis);
        }
    }

    private void parseXmlEntry(ZipEntry ze, InputStream in, BinaryXmlListener listener) throws IOException {
        BinaryXmlParser parser = new BinaryXmlParser();
        // parser.addListener(new BinaryXmlDump());
        parser.addListener(listener);
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
