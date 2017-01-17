package com.yjt.engine;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.exception.PluginLoadException;
import com.yjt.engine.utils.CompatUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class PluginCompatTest {

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
    public void compat() {
        Map<String, Integer> dependencies = new HashMap<>();
        dependencies.put("plugin-engine", BuildConfig.VERSION_CODE);
        Exception exception = null;
        try {
            CompatUtil.getInstance().checkCompat(dependencies, null);
        } catch (PluginLoadException e) {
            exception = e;
        }
        Assert.assertEquals(exception, null);

        dependencies.put("plugin-engine", BuildConfig.VERSION_CODE - 1);
        try {
            CompatUtil.getInstance().checkCompat(dependencies, null);
        } catch (PluginLoadException e) {
            exception = e;
        }
        Assert.assertEquals(exception, null);

        dependencies.put("plugin-engine", BuildConfig.VERSION_CODE + 1);
        try {
            CompatUtil.getInstance().checkCompat(dependencies, null);
        } catch (PluginLoadException e) {
            exception = e;
        }
        Assert.assertEquals(exception, null);

        exception = null;
        Set<String> ignores = new HashSet<>();
        ignores.add("support_v4");
        try {
            CompatUtil.getInstance().checkCompat(dependencies, ignores);
        } catch (PluginLoadException e) {
            exception = e;
        }
        Assert.assertEquals(exception, null);

        Plugin.getInstance().registerLibrary("support_v4", 2);
        try {
            CompatUtil.getInstance().checkCompat(dependencies, null);
        } catch (PluginLoadException e) {
            exception = e;
        }
        Assert.assertNotEquals(exception, null);

        exception = null;
        try {
            Plugin.getInstance().registerLibrary("support_v4", 10);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNotEquals(exception, null);

        exception = null;
        dependencies.put("support_v7", 1);
        Plugin.getInstance().registerLibrary("support_v7", 2);
        try {
            CompatUtil.getInstance().checkCompat(dependencies, ignores);
        } catch (PluginLoadException e) {
            exception = e;
        }
        Assert.assertEquals(exception, null);
    }

}
