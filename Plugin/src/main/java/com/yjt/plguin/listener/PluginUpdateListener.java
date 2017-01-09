package com.yjt.plguin.listener;

import android.support.annotation.NonNull;

import com.yjt.plguin.entity.base.PluginExtraInfo;

public interface PluginUpdateListener {

    PluginExtraInfo updatePlugin(@NonNull PluginExtraInfo pluginExtraInfo);
}
