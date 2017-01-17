package com.yjt.engine;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.constant.Constant;
import com.yjt.engine.entity.TestAssetsPluginInfo;
import com.yjt.engine.entity.TestIllegalAssetsPluginInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.utils.FileUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PluginTest {

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
    public void addPlugin() {
        PluginExtraInfo info = Plugin.getInstance().add(new TestAssetsPluginInfo(), Constant.Mode.UPDATE);
        Assert.assertTrue(info.getPluginState() == Constant.State.PLUGIN_UPDATE_SUCCESS);
        Assert.assertTrue(FileUtil.getInstance().exist(info.getPluginPath()));

        info = Plugin.getInstance().add(info, Constant.Mode.LOAD);
        Assert.assertTrue(info.getPluginState() == Constant.State.PLUGIN_LOADED_SUCCESS);
    }

    @Test
    public void addIllegalPlugin() {
        PluginExtraInfo info = Plugin.getInstance().add(new TestIllegalAssetsPluginInfo(), Constant.Mode.UPDATE);
        Assert.assertTrue(info.getPluginState() == Constant.State.LOCAL_AND_REMOTE_PLUGIN_NON_EXISTENT);
        Assert.assertTrue(!FileUtil.getInstance().exist(info.getPluginPath()));
        
        info.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
        info = Plugin.getInstance().add(info, Constant.Mode.LOAD);
        Assert.assertTrue(info.getPluginState() == Constant.State.INITIATION);
    }
}
