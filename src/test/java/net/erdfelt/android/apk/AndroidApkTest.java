package net.erdfelt.android.apk;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Assert;
import org.junit.Test;

public class AndroidApkTest {
    @Test
    public void testBuildInfoPackageName() throws ZipException, IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("abi-13.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.packageName", "com.erdfelt.android.buildinfo", apk.getPackageName());
    }

    @Test
    public void testBuildInfoAppVersion() throws ZipException, IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("abi-13.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.appVersion", "1.3-SNAPSHOT", apk.getAppVersion());
    }

    @Test
    public void testBuildInfoAppVersionCode() throws ZipException, IOException {
        File apkfile = MavenTestingUtils.getTestResourceFile("abi-13.apk");
        AndroidApk apk = new AndroidApk(apkfile);

        Assert.assertEquals("apk.appVersionCode", "13", apk.getAppVersionCode());
    }
}
