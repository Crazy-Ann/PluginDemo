/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.yjt.engine.entity;

import android.support.annotation.NonNull;

/**
 * 本地插件信息
 */
public class LocalPluginInfo implements Comparable<LocalPluginInfo> {

    public String pluginId;
    public int version;
    public boolean isValid;

    @Override
    public int compareTo(@NonNull LocalPluginInfo another) {
        return another.version - this.version;
    }
}
