package com.yjt.engine.listener;

import com.yjt.engine.callback.PluginCallback;
import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.exception.PluginLoadException;

/**
 * 插件核心接口
 */
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
