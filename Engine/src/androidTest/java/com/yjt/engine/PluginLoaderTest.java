package com.yjt.engine;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.constant.Constant;
import com.yjt.engine.entity.TestAssetsPluginInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.exception.PluginInstallException;
import com.yjt.engine.utils.FileUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class PluginLoaderTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        Plugin.getInstance().initialize(mContext, new PluginConfiguration.Builder()
                .setDebug(BuildConfig.DEBUG)
                .setIgnoreInstalledPlugin(false)
                .setRetryMaxTimes(1)
                .build());
    }

    @After
    public void shutDown() {
        Plugin.releaseInstance();
    }

    @Test
    public void load() throws PluginInstallException, IOException {
        File file = File.createTempFile("app_", ".apk");

        PluginExtraInfo info = new TestAssetsPluginInfo();
        info.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
        Plugin.getInstance().add(info, Constant.Mode.LOAD);
        Assert.assertTrue(info.getPluginState() == Constant.State.INITIATION);

        info.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
        info.setPluginPath(file.getAbsolutePath());
        Plugin.getInstance().add(info, Constant.Mode.LOAD);
        Assert.assertTrue(info.getPluginState() == Constant.State.PLUGIN_LOADED_FAIL);

        FileUtil.getInstance().copyFileFromAsset(mContext, "app.apk", file);
        info.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
        Plugin.getInstance().add(info, Constant.Mode.LOAD);
        Assert.assertTrue(info.getPluginState() == Constant.State.PLUGIN_LOADED_SUCCESS);
    }
}
