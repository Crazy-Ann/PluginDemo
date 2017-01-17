package com.yjt.engine.task;

import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.listener.PluginListener;

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
