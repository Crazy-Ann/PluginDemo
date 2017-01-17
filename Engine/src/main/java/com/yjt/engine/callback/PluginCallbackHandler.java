package com.yjt.engine.callback;

import android.os.Handler;

import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.exception.PluginException;
import com.yjt.engine.listener.PluginBehaviorListener;

public class PluginCallbackHandler extends PluginCallback {

    private Handler mHandler;

    public PluginCallbackHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCancel(final PluginExtraInfo pluginExtraInfo) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.onCancel(pluginExtraInfo);
            }
        });
    }

    @Override
    public void notifyProgress(final PluginExtraInfo pluginExtraInfo, final float progress) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.notifyProgress(pluginExtraInfo, progress);
            }
        });
    }

    @Override
    public void preUpdate(final PluginExtraInfo pluginExtraInfo) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.preUpdate(pluginExtraInfo);
            }
        });
    }

    @Override
    public void postUpdate(final PluginExtraInfo pluginExtraInfo) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.postUpdate(pluginExtraInfo);
            }
        });
    }

    @Override
    public void preLoad(final PluginExtraInfo pluginExtraInfo) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.preLoad(pluginExtraInfo);
            }
        });
    }

    @Override
    public void postLoad(final PluginExtraInfo pluginExtraInfo, final PluginBaseInfo pluginBaseInfo) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.postLoad(pluginExtraInfo, pluginBaseInfo);
            }
        });
    }

    @Override
    public void loadSuccess(final PluginExtraInfo pluginExtraInfo, final PluginBaseInfo pluginBaseInfo, final PluginBehaviorListener pluginBehaviorListener) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.loadSuccess(pluginExtraInfo, pluginBaseInfo, pluginBehaviorListener);
            }
        });
    }

    @Override
    public void loadFail(final PluginExtraInfo pluginExtraInfo, final PluginException exception) {
        if (getPluginListener(pluginExtraInfo) == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PluginCallbackHandler.super.loadFail(pluginExtraInfo, exception);
            }
        });
    }
}
