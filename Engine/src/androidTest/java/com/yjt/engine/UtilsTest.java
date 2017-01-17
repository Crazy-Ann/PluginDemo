package com.yjt.engine;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.entity.PluginApk;
import com.yjt.engine.utils.FileUtil;
import com.yjt.engine.utils.ManifestUtil;
import com.yjt.engine.utils.SoLibraryUtil;
import com.yjt.utils.LogUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        Plugin.getInstance().initialize(mContext, new PluginConfiguration.Builder()
                .setDebug(BuildConfig.DEBUG)
                .setIgnoreInstalledPlugin(false)
                .build());
    }

    @After
    public void shutDown() {
        Plugin.releaseInstance();
    }

    @Test
    public void file() throws IOException {
        File cacheDir = mContext.getExternalCacheDir();
        for (int i = 0; i < 20; i++) {
            FileUtil.getInstance().delete(new File(cacheDir, "extract_asset" + File.separator + "test_" + i));
//            FileUtil.getInstance().copyFileFromAsset(mContext, "test.apk", new File(cacheDir, "extract_asset" + File.separator + "test_" + i));
        }
        for (int i = 0; i < 20; i++) {
            FileUtil.getInstance().delete(new File(cacheDir, "copy_file" + File.separator + "test_" + i));
//            FileUtil.getInstance().copyFile(new File(cacheDir, "extract_asset" + File.separator + "test_" + i), new File(cacheDir, "copy_file" + File.separator + "test_" + i));
        }
    }

    @Test
    public void soLibrary() throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir", "."));
        File testApk = File.createTempFile("plugin_test_", "test.apk", tmpDir);
        FileUtil.getInstance().copyFileFromAsset(mContext, "fresco.apk", testApk);
        LogUtil.getInstance().println("Get test apk, path = " + testApk);
        Set<String> soLibs = SoLibraryUtil.getInstance().extractSoLibrary(testApk, new File(tmpDir, "extract_solib"));
        Assert.assertTrue(soLibs.contains("libimagepipeline.so"));
    }

    @Test
    public void menifast() throws IOException {
        String tmpDirPath = System.getProperty("java.io.tmpdir", ".");
        File tmpDir = new File(tmpDirPath);
        LogUtil.getInstance().println("Get system temp dir, path = " + tmpDirPath);

        File testApk = File.createTempFile("plugin_test_", "test.apk", tmpDir);
        FileUtil.getInstance().copyFileFromAsset(mContext, "test.apk", testApk);
        Assert.assertTrue(testApk.exists());

        PluginApk apk = ManifestUtil.getInstance().parse(testApk);
        Assert.assertEquals(apk.application, "moe.studio.plugin.fresco.TestApplication");
        Assert.assertEquals(apk.packageName, "moe.studio.plugin.fresco");
        Assert.assertEquals(apk.versionName, "1.0");
        Assert.assertEquals(apk.versionCode, "1");
        Assert.assertEquals(apk.application, "moe.studio.plugin.fresco.TestApplication");
        Assert.assertTrue(apk.dependencies.get("plugin") == 400);
        Assert.assertTrue(apk.dependencies.get("support_v4") == 23);
        Assert.assertTrue(apk.dependencies.get("plugin_v7") == 23);
        Assert.assertTrue(apk.dependencies.get("test_dependency") == 300);
    }
}
