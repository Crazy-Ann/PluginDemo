package com.yjt.engine.entity;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.yjt.engine.BuildConfig;

import java.util.Map;
import java.util.Set;

import dalvik.system.DexClassLoader;

public class PluginApk {

    public String application;
    public String packageName;
    public String versionCode;
    public String versionName;

    public PackageInfo packageInfo;
    public Resources resources;
    public AssetManager assetManager;
    public DexClassLoader classLoader;
    public Map<String, Integer> dependencies;
    public Set<String> ignoreDependencies;

    public void set(@NonNull PluginApk apk) {
        this.application = this.application == null ? apk.application : this.application;
        this.packageName = this.packageName == null ? apk.packageName : this.packageName;
        this.versionCode = this.versionCode == null ? apk.versionCode : this.versionCode;
        this.versionName = this.versionName == null ? apk.versionName : this.versionName;
        this.packageInfo = this.packageInfo == null ? apk.packageInfo : this.packageInfo;
        this.resources = this.resources == null ? apk.resources : this.resources;
        this.assetManager = this.assetManager == null ? apk.assetManager : this.assetManager;
        this.classLoader = this.classLoader == null ? apk.classLoader : this.classLoader;
        this.dependencies = this.dependencies == null ? apk.dependencies : this.dependencies;
        this.ignoreDependencies = this.ignoreDependencies == null ? apk.ignoreDependencies : this.ignoreDependencies;
    }

    @Override
    public String toString() {
        if (BuildConfig.DEBUG) {
            return "PluginApk{" +
                    "application = " + application +
                    ", packageName = " + packageName +
                    ", versionCode = " + versionCode +
                    ", versionName = " + versionName +
                    ", packageInfo = " + packageInfo +
                    ", resources = " + resources +
                    ", assetManage = " + assetManager +
                    ", classLoader = " + classLoader +
                    '}';
        } else {
            return null;
        }
    }
}
