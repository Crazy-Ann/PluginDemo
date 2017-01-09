package com.yjt.plguin.listener;

import android.support.annotation.NonNull;

import com.yjt.plguin.entity.base.PluginBaseInfo;
import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.plguin.exception.PluginLoadException;
import com.yjt.plguin.exception.PluginException;

public interface PluginLoadListener {

    PluginExtraInfo load(@NonNull PluginExtraInfo pluginExtraInfo);

    PluginBaseInfo load(PluginListener manager, PluginBaseInfo pluginBaseInfo) throws PluginException;

    PluginBaseInfo getPlugin(String packageName);

    void putPlugin(String id, PluginBaseInfo pluginBaseInfo);

    Class loadClass(@NonNull PluginBaseInfo pluginBaseInfo, String className) throws PluginLoadException;

    PluginBehaviorListener createBehavior(PluginBaseInfo pluginBaseInfo) throws PluginLoadException;
}
