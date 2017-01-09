package com.yjt.plguin.entity;

import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.utils.LogUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TaskState {

    private final PluginExtraInfo mPluginExtraInfo;
    private final Future<PluginExtraInfo> mFuture;

    public TaskState(PluginExtraInfo pluginExtraInfo, Future<PluginExtraInfo> future) {
        this.mPluginExtraInfo = pluginExtraInfo;
        this.mFuture = future;
    }

    public PluginExtraInfo getPluginExtraInfo() {
        return mPluginExtraInfo;
    }

    public PluginExtraInfo getFutureRequest(long timeout) {
        PluginExtraInfo pluginExtraInfo;
        try {
            pluginExtraInfo = mFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            LogUtil.getInstance().println("Get future request fail, error = " + exception.getMessage());
            LogUtil.getInstance().println(exception.toString());
            pluginExtraInfo = mPluginExtraInfo.markException(exception);
        }
        return pluginExtraInfo;
    }

    public void cancel() {
        mPluginExtraInfo.cancel();
        mFuture.cancel(true);
    }
}
