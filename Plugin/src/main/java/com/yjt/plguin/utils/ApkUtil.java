package com.yjt.plguin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.yjt.plguin.constant.Constant;
import com.yjt.plguin.exception.PluginLoadException;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class ApkUtil {

    private static ApkUtil mInstance;

    private ApkUtil() {
        // cannot be instantiated
    }

    public static synchronized ApkUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ApkUtil();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public DexClassLoader createClassLoader(Context context, String dexPath,
                                            String optimizedDir, String nativeLibDir,
                                            boolean isInDependent) throws PluginLoadException {
        ClassLoader parentClassLoader;
        if (isInDependent) {
            // Separate the new ClassLoader from current app, thus the class loaded by this
            // new ClassLoader will deadly incompatible from the current app.
            parentClassLoader = ClassLoader.getSystemClassLoader().getParent();
        } else {
            // Use the current app's ClassLoader as the new ClassLoader's parent.
            // In this case, the class loaded by the new ClassLoader must regard the
            // "Parent Delegation Model" of ClassLoader.
            parentClassLoader = context.getClassLoader();
        }

        try {
            // TODO: 2016/11/30 Adding MultiDex support for plugin.
            return new DexClassLoader(dexPath, optimizedDir, nativeLibDir, parentClassLoader);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new PluginLoadException(e, Constant.LoadException.CREATE_CLASS_LOADER_FAILED);
        }
    }

    public AssetManager createAssetManager(String dexPath) throws PluginLoadException {
        try {
            // TODO: 2016/11/25 We may need to support different api levels here.
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new PluginLoadException(e, Constant.LoadException.CREATE_ASSET_MANAGER_FAILED);
        }
    }

    public Resources createResources(Context context, AssetManager assetManager) {
        Resources superRes = context.getResources();
        return new Resources(assetManager, superRes.getDisplayMetrics(),
                             superRes.getConfiguration());
    }

    public Class<?> loadClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        return loadClass(classLoader, className, true);
    }

    public Class<?> loadClass(ClassLoader classLoader, String className, boolean shouldInitialize) throws ClassNotFoundException {
        return Class.forName(className, shouldInitialize, classLoader);
    }

    @Nullable
    public PackageInfo getLocalPackageInfo(Context context) {
        return getLocalPackageInfo(context, 0);
    }

    @Nullable
    public PackageInfo getLocalPackageInfo(Context context, int flag) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), flag);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public PackageInfo getPackageInfo(Context context, String apkPath) {
        return getPackageInfo(context, apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
    }

    @Nullable
    public PackageInfo getPackageInfo(Context context, String apkPath, int flag) {
        return context.getPackageManager().getPackageArchiveInfo(apkPath, flag);
    }

    @Nullable
    public ApplicationInfo getApplicationInfo(Context context) {
        PackageManager pm;
        String packageName;
        try {
            pm = context.getPackageManager();
            packageName = context.getPackageName();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
        if (pm == null || packageName == null) {
            // This is most likely a mock context, so just return without patching.
            return null;
        }

        ApplicationInfo info = null;
        try {
            info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }
}
