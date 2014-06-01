package net.erdfelt.android.apk.arsc;

import java.io.File;
import java.io.IOException;

import net.erdfelt.android.apk.logging.Logging;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Ignore;
import org.junit.Test;

public class AppResourcesTest {
    static {
        Logging.config();
    }
    
    @Test
    public void testLoadABI() throws IOException {
        File file = MavenTestingUtils.getTestResourceFile("abi-resources.arsc");
        AppResources arsc = new AppResources(file);
    }
    
    @Test
    @Ignore
    public void testLoadAGF() throws IOException {
        File file = MavenTestingUtils.getTestResourceFile("agf-resources.arsc");
        AppResources arsc = new AppResources(file);
    }
    
    @Test
    @Ignore
    public void testLoadApi() throws IOException {
        File file = MavenTestingUtils.getTestResourceFile("api-resources.arsc");
        AppResources arsc = new AppResources(file);
    }
}
