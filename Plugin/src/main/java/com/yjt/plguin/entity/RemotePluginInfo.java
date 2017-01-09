/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.yjt.plguin.entity;

import android.support.annotation.NonNull;

public class RemotePluginInfo implements Comparable<RemotePluginInfo> {

    public String pluginId;
    public int version;
    public String downloadUrl;
    public long fileSize;
    public boolean isValid;
    public boolean isForceUpdate;
    public int minAppBuild;

    @Override
    public int compareTo(@NonNull RemotePluginInfo another) {
        // MAX TO MIN.
        int compare = this.version - another.version;
        return -compare;

    }
}
