package com.yjt.engine;

import android.support.annotation.NonNull;

import com.yjt.engine.callback.PluginCallback;
import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.constant.Constant;
import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.exception.PluginLoadException;
import com.yjt.engine.listener.PluginBehaviorListener;
import com.yjt.engine.listener.PluginInstallListener;
import com.yjt.engine.listener.PluginListener;
import com.yjt.engine.listener.PluginLoadListener;
import com.yjt.engine.listener.PluginUpdateListener;
import com.yjt.engine.task.PluginTask;
import com.yjt.utils.LogUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginWrapper implements PluginListener {

    private final PluginLoadListener mPluginLoadListener;
    private final PluginUpdateListener mPluginUpdateListener;
    private final PluginInstallListener mPluginInstallListener;
    private final PluginConfiguration mPluginConfiguration;
    private final PluginCallback mPluginCallback;

    private Map<Class<? extends PluginBehaviorListener>, PluginBaseInfo> mLoadedPlugins;

    public PluginWrapper(PluginLoadListener pluginLoadListener, PluginUpdateListener pluginUpdateListener, PluginInstallListener pluginInstallListener, PluginConfiguration pluginConfiguration, PluginCallback pluginCallback) {
        this.mPluginLoadListener = pluginLoadListener;
        this.mPluginUpdateListener = pluginUpdateListener;
        this.mPluginInstallListener = pluginInstallListener;
        this.mPluginConfiguration = pluginConfiguration;
        this.mPluginCallback = pluginCallback;
    }

    public PluginExtraInfo add(@NonNull PluginExtraInfo pluginExtraInfo, int mode) {
        if (pluginExtraInfo.getPluginListener() == null) {
            pluginExtraInfo.attach(this);
        }
        return add(pluginExtraInfo, PluginTask.doing(this, mode));
    }

    public PluginExtraInfo add(@NonNull PluginExtraInfo pluginExtraInfo, @NonNull PluginTask task) {
        if (pluginExtraInfo.getPluginListener() == null) {
            pluginExtraInfo.attach(this);
        }
        LogUtil.getInstance().println("request id = " + pluginExtraInfo.getPluginId() + ", state log = " + pluginExtraInfo.getPluginStateRecord());
        task.doing(pluginExtraInfo);
        return pluginExtraInfo;
    }

    @Override
    public PluginConfiguration getPluginConfiguration() {
        return mPluginConfiguration;
    }

    @Override
    public PluginLoadListener getPluginLoadListener() {
        return mPluginLoadListener;
    }

    @Override
    public PluginUpdateListener getPluginUpdateListener() {
        return mPluginUpdateListener;
    }

    @Override
    public PluginInstallListener getPluginInstallListener() {
        return mPluginInstallListener;
    }

    @Override
    public PluginCallback getPluginCallback() {
        return mPluginCallback;
    }

    @Override
    public Class getClass(Class<? extends PluginBaseInfo> clazz, String className) throws PluginLoadException {
        if (mLoadedPlugins == null || mLoadedPlugins == Collections.EMPTY_MAP) {
            return null;
        }

        PluginBaseInfo pluginBaseInfo = mLoadedPlugins.get(clazz);
        if (pluginBaseInfo == null) {
            throw new PluginLoadException("Plugin has not yet been loaded.", Constant.LoadException.PLUGIN_NOT_LOADED);
        }
        return mPluginLoadListener.loadClass(pluginBaseInfo, className);
    }

    @Override
    public <B extends PluginBehaviorListener, P extends PluginBaseInfo<B>> B getBehavior(P clazz) throws PluginLoadException {
        if (mLoadedPlugins == null || mLoadedPlugins == Collections.EMPTY_MAP) {
            return null;
        }

        PluginBaseInfo pluginBaseInfo = mLoadedPlugins.get(clazz);

        if (pluginBaseInfo != null) {
            PluginBehaviorListener pluginBehaviorListener = pluginBaseInfo.getPluginBehaviorListener();
            if (pluginBehaviorListener == null) {
                return (B) pluginBehaviorListener;
            }
        }
        throw new PluginLoadException("Plugin has not yet been loaded.", Constant.LoadException.PLUGIN_NOT_LOADED);
    }

    @Override
    public <B extends PluginBehaviorListener, P extends PluginBaseInfo<B>> P getPlugin(P clazz) {
        return mLoadedPlugins == null || mLoadedPlugins == Collections.EMPTY_MAP ? null : (P) mLoadedPlugins.get(clazz);
    }

    @Override
    public void addLoadedPlugin(Class<? extends PluginBehaviorListener> clazz, PluginBaseInfo pluginBaseInfo) {
        if (mLoadedPlugins == null || mLoadedPlugins == Collections.EMPTY_MAP) {
            mLoadedPlugins = new HashMap<>();
        }
        mLoadedPlugins.put(clazz, pluginBaseInfo);
    }
}
