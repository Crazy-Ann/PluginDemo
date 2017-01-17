package com.yjt.engine;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.constant.Constant;
import com.yjt.engine.entity.TestAssetsPluginInfo;
import com.yjt.engine.entity.TestRemotePluginInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.listener.implement.PluginUpdater;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PluginUpdateTest {

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
    public void update() {
        PluginUpdater updater = new PluginUpdater(mContext);

        PluginExtraInfo info = new TestRemotePluginInfo();
        info.attach(Plugin.getInstance());
        info.setClearLocalPlugins(true);
        updater.requestPlugin(info);
        Assert.assertEquals(info.getPluginState(), Constant.State.PLUGIN_DOWNLOAD);

        info = new TestAssetsPluginInfo();
        info.attach(Plugin.getInstance());
        info.setPluginId(info.requestPluginId());
        info.setClearLocalPlugins(true);
        updater.requestPlugin(info);
        Assert.assertEquals(info.getPluginState(), Constant.State.PLUGIN_RELEASE_FROM_ASSETS);

        info = new TestAssetsPluginInfo();
        Plugin.getInstance().add(info, Constant.Mode.LOAD | Constant.Mode.UPDATE);
        Assert.assertEquals(info.getPluginState(), Constant.State.PLUGIN_LOADED_SUCCESS);

        info = new TestRemotePluginInfo();
        info.attach(Plugin.getInstance());
        info.setClearLocalPlugins(false);
        updater.requestPlugin(info);
        Assert.assertEquals(info.getPluginState(), Constant.State.PLUGIN_UPDATE_SUCCESS);

        info = new TestAssetsPluginInfo();
        info.attach(Plugin.getInstance());
        info.setClearLocalPlugins(false);
        updater.requestPlugin(info);
        Assert.assertEquals(info.getPluginState(), Constant.State.PLUGIN_UPDATE_SUCCESS);


    }
}
