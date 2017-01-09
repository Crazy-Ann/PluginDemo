package com.yjt.plguin.task;

import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.plguin.listener.PluginListener;

public class PluginLoad extends PluginTask {

    public PluginLoad(PluginListener pluginListener) {
        super(pluginListener);
    }

    @Override
    public void doing(PluginExtraInfo pluginExtraInfo) {
        mPluginListener.getPluginLoadListener().load(pluginExtraInfo);
    }
}
