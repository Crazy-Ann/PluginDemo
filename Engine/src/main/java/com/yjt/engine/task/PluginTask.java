package com.yjt.engine.task;

import com.yjt.engine.constant.Constant;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.listener.PluginListener;

public abstract class PluginTask {

    public final PluginListener mPluginListener;

    public PluginTask(PluginListener pluginListener) {
        this.mPluginListener = pluginListener;
    }

    public static PluginTask doing(PluginListener pluginListener, int mode) {
        PluginTask task;
        switch (mode) {
            case Constant.Mode.UPDATE:               // Only update plugin.
                task = new PluginUpdate(pluginListener);
                break;
            case Constant.Mode.LOAD:                 // Only load plugin.
                task = new PluginLoad(pluginListener);
                break;
            case Constant.Mode.UPDATE | Constant.Mode.LOAD:        // Update and load plugin.
            default:
                task = new PluginUpdateAndLoad(pluginListener);
                break;
        }
        return task;
    }

    public abstract void doing(PluginExtraInfo pluginExtraInfo);
}
