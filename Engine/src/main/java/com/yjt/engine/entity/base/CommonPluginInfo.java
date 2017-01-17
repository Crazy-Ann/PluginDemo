package com.yjt.engine.entity.base;

import android.content.Context;

import com.yjt.engine.BuildConfig;
import com.yjt.engine.constant.Constant;
import com.yjt.engine.exception.PluginLoadException;
import com.yjt.engine.listener.PluginBehaviorListener;
import com.yjt.engine.utils.ApkUtil;
import com.yjt.engine.utils.FileUtil;
import com.yjt.utils.LogUtil;

import java.io.File;
import java.io.IOException;

/**
 * 普通插件信息（请求任务）
 *
 * @param <B>
 */
public abstract class CommonPluginInfo<B extends PluginBehaviorListener> extends PluginBaseInfo<B> {

    public CommonPluginInfo(String externalPath) {
        super(externalPath);
    }

    @Override
    public PluginBaseInfo loadPlugin(Context context, String packagePath) throws PluginLoadException {
        LogUtil.getInstance().println("Create plugin package entity.");
        File apkFile = new File(packagePath);
        checkApkFile(apkFile);
        try {
            mOptimizedDexDir = createOptimizedDexDir(apkFile);
        } catch (IOException e) {
            throw new PluginLoadException(e, Constant.LoadException.CREATE_OPTIMIZED_DEX_DIR_FAILED);
        }

        if (BuildConfig.DEBUG) {
            LogUtil.getInstance().println("Create ClassLoader :");
            LogUtil.getInstance().println("installPath = " + packagePath);
            LogUtil.getInstance().println("mOptDexDir = " + mOptimizedDexDir.getAbsolutePath());
            LogUtil.getInstance().println("mSoLibDir = " + (mSoLibraryDir == null ? "null" : mSoLibraryDir.getAbsolutePath()));
            if (mSoLibraryDir != null) {
                FileUtil.getInstance().dumpFiles(mSoLibraryDir);
            }
        }

        mPluginApk.classLoader = ApkUtil.getInstance().createClassLoader(
                context,
                packagePath,
                mOptimizedDexDir.getAbsolutePath(),
                mSoLibraryDir == null ? null : mSoLibraryDir.getAbsolutePath(),
                false);
        mPluginApk.assetManager = ApkUtil.getInstance().createAssetManager(packagePath);
        mPluginApk.resources = ApkUtil.getInstance().createResources(context, mPluginApk.assetManager);

        setPluginLoaded();
        return this;
    }

    protected void checkApkFile(File apkFile) throws PluginLoadException {
        if (apkFile == null || !apkFile.exists()) {
            LogUtil.getInstance().println("Apk file not exist.");
            throw new PluginLoadException("Apk file not exist.", Constant.LoadException.FILE_NOT_FOUND);
        }

        if (!apkFile.getAbsolutePath().trim().startsWith("/data/")) {
            String warn = "Apk file seems to locate in external path (not executable), " +
                    "path = " + apkFile.getAbsolutePath();
            LogUtil.getInstance().println(warn);

            if (BuildConfig.DEBUG) {
                throw new RuntimeException(warn);
            }
        }
    }

    protected File createOptimizedDexDir(File apkFile) throws IOException {
        File file = new File(apkFile.getParentFile(), mPluginConfiguration.getOptimizedDexDir());
        FileUtil.getInstance().checkCreateDir(file);
        return file;
    }
}
