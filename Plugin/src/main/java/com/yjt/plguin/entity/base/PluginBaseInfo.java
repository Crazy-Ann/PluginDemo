package com.yjt.plguin.entity.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.yjt.plguin.config.PluginConfiguration;
import com.yjt.plguin.entity.PluginApk;
import com.yjt.plguin.exception.PluginLoadException;
import com.yjt.plguin.listener.PluginBehaviorListener;
import com.yjt.plguin.listener.PluginListener;

import java.io.File;
import java.util.Set;

public abstract class PluginBaseInfo<B extends PluginBehaviorListener> {

    protected File mOptimizedDexDir;
    protected File mSoLibraryDir;
    protected String mInstallPath;
    protected PluginListener mPluginListener;
    protected PluginConfiguration mPluginConfiguration;
    protected B mPluginBehaviorListener;

    private boolean hasPluginLoaded;
    private final byte[] mLock;
    private final String mExternalPath;
    protected final PluginApk mPluginApk;

    public PluginBaseInfo(String externalPath) {
        this.mInstallPath = externalPath;
        this.mPluginConfiguration = new PluginConfiguration.Builder().build();
        this.hasPluginLoaded = false;
        this.mLock = new byte[]{};
        this.mExternalPath = externalPath;
        this.mPluginApk = new PluginApk();
    }

    public File getOptimizedDexDir() {
        return mOptimizedDexDir;
    }

    public File getSoLibraryDir() {
        return mSoLibraryDir;
    }

    public String getInstallPath() {
        return mInstallPath;
    }

    public void setInstallPath(String installPath) {
        this.mInstallPath = installPath;
    }

    @Nullable
    public B getPluginBehaviorListener() {
        return mPluginBehaviorListener;
    }

    public final void setPluginBehaviorListener(B pluginBehaviorListener) {
        this.mPluginBehaviorListener = pluginBehaviorListener;
    }

    public boolean isHasPluginLoaded() {
        if (hasPluginLoaded) {
            return true;
        }
        synchronized (mLock){
            return hasPluginLoaded;
        }
    }

    public void setPluginLoaded() {
        if (hasPluginLoaded) {
            return;
        }
        synchronized (mLock){
            hasPluginLoaded = true;
        }
    }

    public String getExternalPath() {
        return mExternalPath;
    }

    public PluginApk getPluginApk() {
        return mPluginApk;
    }

    public void setPluginApk(PluginApk pluginApk) {
        pluginApk.set(pluginApk);
    }

    public void setIgnoreDependencies(Set<String> ignoreDependencies) {
        mPluginApk.ignoreDependencies = ignoreDependencies;
    }

    public PluginBaseInfo attach(@NonNull PluginListener pluginListener) {
        this.mPluginListener = pluginListener;
        mPluginConfiguration = pluginListener.getPluginConfiguration();
        return this;
    }

    @WorkerThread
    public abstract B createBehavior(Context context) throws Exception;

    public abstract PluginBaseInfo loadPlugin(Context context, String packagePath) throws PluginLoadException;

    @Override
    public String toString() {
        return "PluginBaseInfo{" +
                "Apk = " + (mPluginApk == null ? "null" : mPluginApk) +
                ", ApkPath = " + mExternalPath +
                '}';
    }
}
