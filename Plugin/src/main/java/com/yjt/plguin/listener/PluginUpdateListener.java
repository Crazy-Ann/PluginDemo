package com.yjt.plguin.listener;

import android.support.annotation.NonNull;

import com.yjt.plguin.entity.base.PluginExtraInfo;

/**
 * 插件加更新口
 */
public interface PluginUpdateListener {

    PluginExtraInfo updatePlugin(@NonNull PluginExtraInfo pluginExtraInfo);
}
