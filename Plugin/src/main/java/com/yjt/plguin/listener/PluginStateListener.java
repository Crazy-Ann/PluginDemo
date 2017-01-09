package com.yjt.plguin.listener;

import com.yjt.plguin.entity.base.PluginBaseInfo;
import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.plguin.exception.PluginException;

public interface PluginStateListener<B extends PluginBehaviorListener, P extends PluginBaseInfo<B>, R extends PluginExtraInfo<P>> {

    void onPreUpdate(R request);

    void onPostUpdate(R request);

    void onCanceled(R request);

    void onProgress(R request, float progress);

    void onPreLoad(R request);

    void onPostLoaded(R request, P plugin);

    void onGetBehavior(R request, P plugin, B behavior);

    void onFail(R request, PluginException exception);

}
