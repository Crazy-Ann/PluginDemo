package com.yjt.plguin;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yjt.plguin.config.PluginConfiguration;
import com.yjt.plguin.exception.PluginInstallException;
import com.yjt.plguin.listener.implement.PluginInstaller;
import com.yjt.plguin.utils.FileUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class PluginInstallTest {

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
    public void install() throws PluginInstallException, IOException {
        File file = File.createTempFile("plugin_", ".apk", new File(System.getProperty("java.io.tmpdir", ".")));
        FileUtil.getInstance().copyFileFromAsset(mContext, "app.apk", file);
        Assert.assertTrue(file.exists());

        PluginInstaller installer = new PluginInstaller(mContext, Plugin.getInstance().getPluginConfiguration());
//        Assert.assertEquals(installer.install(file.getAbsolutePath()), new File(installer.getPluginDir(), "com.yjt.plugin.test/1/plugin-engine.apk").getAbsolutePath());
//        Assert.assertEquals(installer.install(file.getAbsolutePath()), new File(installer.getPluginDir(), "moe.studio.plugin.fresco/1/plugin-engine.apk").getAbsolutePath());
        Assert.assertEquals(installer.install(file.getAbsolutePath()), new File(installer.getPluginDir(), "com.hynet.mergepay.zhongxin/1/plugin-engine.apk").getAbsolutePath());
    }
}
