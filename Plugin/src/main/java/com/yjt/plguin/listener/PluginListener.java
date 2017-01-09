package com.yjt.plguin.listener;

import com.yjt.plguin.callback.PluginCallback;
import com.yjt.plguin.config.PluginConfiguration;
import com.yjt.plguin.entity.base.PluginBaseInfo;
import com.yjt.plguin.exception.PluginLoadException;

public interface PluginListener {

    PluginConfiguration getPluginConfiguration();

    PluginLoadListener getPluginLoadListener();

    PluginUpdateListener getPluginUpdateListener();

    PluginInstallListener getPluginInstallListener();

    PluginCallback getPluginCallback();

    Class getClass(Class<? extends PluginBaseInfo> clazz, String className) throws PluginLoadException;

    <B extends PluginBehaviorListener, P extends PluginBaseInfo<B>> B getBehavior(P clazz) throws PluginLoadException;

    <B extends PluginBehaviorListener, P extends PluginBaseInfo<B>> P getPlugin(P clazz);

    void addLoadedPlugin(Class<? extends PluginBehaviorListener> clazz, PluginBaseInfo pluginBaseInfo);
}
