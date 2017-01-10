package com.yjt.plguin.entity.base;

import android.content.Context;

import com.yjt.plguin.entity.RemotePluginInfo;

import java.util.List;

/**
 * Assets插件信息（请求任务）
 *
 * @param <P>
 */
public abstract class AssetsPluginInfo<P extends PluginBaseInfo> extends PluginExtraInfo<P> {

    @Override
    public List<? extends RemotePluginInfo> requestRemotePluginInfo(Context context) throws Exception {
        return null;
    }

    @Override
    public boolean requestClearLocalPlugins(Context context) {
        return false;
    }

    @Override
    public boolean isFromAssets() {
        return true;
    }

    @Override
    public abstract String getAssetsPath();

    @Override
    public abstract int getAssetsVersion();
}
