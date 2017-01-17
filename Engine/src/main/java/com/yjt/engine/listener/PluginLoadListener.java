package com.yjt.engine.listener;

import android.support.annotation.NonNull;

import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.exception.PluginLoadException;
import com.yjt.engine.exception.PluginException;

/**
 * 插件加载接口
 */
public interface PluginLoadListener {

    PluginExtraInfo load(@NonNull PluginExtraInfo pluginExtraInfo);

    PluginBaseInfo load(PluginListener manager, PluginBaseInfo pluginBaseInfo) throws PluginException;

    PluginBaseInfo getPlugin(String packageName);

    void putPlugin(String id, PluginBaseInfo pluginBaseInfo);

    Class loadClass(@NonNull PluginBaseInfo pluginBaseInfo, String className) throws PluginLoadException;

    PluginBehaviorListener createBehavior(PluginBaseInfo pluginBaseInfo) throws PluginLoadException;
}
