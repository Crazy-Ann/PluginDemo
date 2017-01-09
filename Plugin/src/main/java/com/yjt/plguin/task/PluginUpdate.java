package com.yjt.plguin.task;

import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.plguin.listener.PluginListener;

public class PluginUpdate extends PluginTask {

    public PluginUpdate(PluginListener pluginListener) {
        super(pluginListener);
    }

    @Override
    public void doing(PluginExtraInfo pluginExtraInfo) {
        mPluginListener.getPluginUpdateListener().updatePlugin(pluginExtraInfo);
    }
}
