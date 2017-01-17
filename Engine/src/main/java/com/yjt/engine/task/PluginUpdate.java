package com.yjt.engine.task;

import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.listener.PluginListener;

public class PluginUpdate extends PluginTask {

    public PluginUpdate(PluginListener pluginListener) {
        super(pluginListener);
    }

    @Override
    public void doing(PluginExtraInfo pluginExtraInfo) {
        mPluginListener.getPluginUpdateListener().updatePlugin(pluginExtraInfo);
    }
}
