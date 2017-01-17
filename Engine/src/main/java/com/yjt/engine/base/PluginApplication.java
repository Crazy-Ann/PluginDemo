package com.yjt.engine.base;

import android.app.Application;
import android.content.Context;

import com.yjt.engine.listener.PluginBehaviorListener;

public abstract class PluginApplication extends Application {

    protected Context mContext;

    public void setAppContext(Context context) {
        mContext = context;
    }

    public abstract PluginBehaviorListener getPluginBehaviorListener();
}
