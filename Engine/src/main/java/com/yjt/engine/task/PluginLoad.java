package com.yjt.engine.task;

import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.listener.PluginListener;

public class PluginLoad extends PluginTask {

    public PluginLoad(PluginListener pluginListener) {
        super(pluginListener);
    }

    @Override
    public void doing(PluginExtraInfo pluginExtraInfo) {
        mPluginListener.getPluginLoadListener().load(pluginExtraInfo);
    }
}
