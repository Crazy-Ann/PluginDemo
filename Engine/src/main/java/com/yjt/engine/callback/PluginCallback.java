package com.yjt.engine.callback;

import android.support.annotation.Nullable;

import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.exception.PluginException;
import com.yjt.engine.listener.PluginBehaviorListener;
import com.yjt.engine.listener.PluginStateListener;

public class PluginCallback {

    @Nullable
    protected PluginStateListener getPluginListener(PluginExtraInfo pluginExtraInfo) {
        return pluginExtraInfo.getPluginStateListener();
    }

    public void onCancel(PluginExtraInfo pluginExtraInfo) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onCanceled(pluginExtraInfo);
        }
    }

    public void notifyProgress(PluginExtraInfo pluginExtraInfo, float progress) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onProgress(pluginExtraInfo, progress);
        }
    }

    public void preUpdate(PluginExtraInfo pluginExtraInfo) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onPreUpdate(pluginExtraInfo);
        }
    }

    public void postUpdate(PluginExtraInfo pluginExtraInfo) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onPostUpdate(pluginExtraInfo);
        }
    }

    public void preLoad(PluginExtraInfo pluginExtraInfo) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onPreLoad(pluginExtraInfo);
        }
    }

    public void postLoad(PluginExtraInfo pluginExtraInfo, PluginBaseInfo pluginBaseInfo) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onPostLoaded(pluginExtraInfo, pluginBaseInfo);
        }
    }

    public void loadSuccess(PluginExtraInfo pluginExtraInfo, PluginBaseInfo pluginBaseInfo, PluginBehaviorListener pluginBehaviorListener) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onGetBehavior(pluginExtraInfo, pluginBaseInfo, pluginBehaviorListener);
        }
    }

    public void loadFail(PluginExtraInfo pluginExtraInfo, PluginException exception) {
        PluginStateListener listener = getPluginListener(pluginExtraInfo);
        if (listener != null) {
            listener.onFail(pluginExtraInfo, exception);
        }
    }
}
