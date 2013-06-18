Android APK Parsing Lib
=======================

Simple class to read/parse the packageName, versionName, & versionCode
information out of a compiled Android APK file.

This is accomplished by decompressing the AndroidManifest.xml file and
then decoding the compiled XML binary file to glean this information.

I've been using this for some automatic versioning of various android
artifacts I have.  I am sharing this codebase under the Apache Source
License 2.0 in hopes that others will find it useful (or educational).

## Usage ##

A SNAPSHOT of the library has been deployed to oss.sonatype.org under
the following `dependency`.

    <dependency>
      <groupId>net.erdfelt.android</groupId>
      <artifactId>apk-parser</artifactId>
      <version>1.0.2</version>
    </dependency>

Once you have that dependency (no other transitive dependencies required)
you can use the following code to get the information you need.

    package net.erdfelt.android.example;

    import java.io.File;
    import net.erdfelt.android.apk.AndroidApk;
    
    public class ApkDump {
        public static void main(String[] args) {
            if(args.length < 1) {
                System.err.println("Usage: ApkDump [apkfile]");
                System.exit(-1);
            }
            File apkfile = new File(args[0]);
            if(!apkfile.exists()) {
                System.err.println("Error: File Not Found: " + apkfile);
                System.exit(-1);
            }
            try {
                AndroidApk apk = new AndroidApk(apkfile);
                System.out.println("APK: " + apkfile);
                System.out.println("  .packageName    = " + apk.getPackageName());
                System.out.println("  .appVersion     = " + apk.getAppVersion());
                System.out.println("  .appVersionCode = " + apk.getAppVersionCode());
            } catch(Throwable t) {
                t.printStackTrace(System.err);
            }
        }
    }

That's it, pretty straightforward, the tricky part was decompiling the binary xml
file, and that should be easy enough to extend for the other xml files in an APK
file (including resources).


