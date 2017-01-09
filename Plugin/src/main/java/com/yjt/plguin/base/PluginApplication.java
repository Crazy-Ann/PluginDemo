package com.yjt.plguin.base;

import android.app.Application;
import android.content.Context;

import com.yjt.plguin.listener.PluginBehaviorListener;

public abstract class PluginApplication extends Application {

    protected Context mContext;

    public void setAppContext(Context context) {
        mContext = context;
    }

    public abstract PluginBehaviorListener getPluginBehaviorListener();
}
