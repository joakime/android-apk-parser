package net.erdfelt.android.apk;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AndroidApkTest {
    @Test
    public void testBuildInfoPackageName() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("abi-13.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.packageName", "com.erdfelt.android.buildinfo", apk.getPackageName());
    }

    @Test
    public void testBuildInfoAppVersion() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("abi-13.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.appVersion", "1.3", apk.getAppVersion());
    }

    @Test
    public void testBuildInfoMinSdkVersion() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("abi-13.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.minSdkVersion", "8", apk.getMinSdkVersion());
    }

    @Test
    public void testBuildInfoAppVersionCode() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("abi-13.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.appVersionCode", "4", apk.getAppVersionCode());
    }

    @Test
    public void testGestureFunPackageName() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("agf-10.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.packageName", "com.erdfelt.android.gestures", apk.getPackageName());
    }

    @Test
    public void testGestureFunMinSdkVersion() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("agf-10.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.minSdkVersion", "8", apk.getMinSdkVersion());
    }

    @Test
    public void testGestureFunAppVersion() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("agf-10.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.appVersion", "1.0", apk.getAppVersion());
    }

    @Test
    public void testGestureFunAppVersionCode() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("agf-10.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.appVersionCode", "1", apk.getAppVersionCode());
    }

    @Test
    public void testReadFromStream() throws IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("agf-10.apk");
        AndroidApk apk = new AndroidApk(new FileInputStream(apkfile));

        Assert.assertEquals("apk.appVersionCode", "1", apk.getAppVersionCode());
    }

}
