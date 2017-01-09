package com.yjt.plguin.task;

import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.plguin.listener.PluginListener;

public class PluginUpdateAndLoad extends PluginTask {

    public PluginUpdateAndLoad(PluginListener pluginListener) {
        super(pluginListener);
    }

    @Override
    public void doing(PluginExtraInfo pluginExtraInfo) {
        new PluginUpdate(mPluginListener).doing(pluginExtraInfo);
        new PluginLoad(mPluginListener).doing(pluginExtraInfo);
    }
}
