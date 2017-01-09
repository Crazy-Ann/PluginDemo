package com.yjt.plguin.listener.implement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yjt.plguin.base.PluginApplication;
import com.yjt.plguin.constant.Constant;
import com.yjt.plguin.entity.base.PluginBaseInfo;
import com.yjt.plguin.entity.PluginApk;
import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.plguin.exception.PluginLoadException;
import com.yjt.plguin.exception.PluginException;
import com.yjt.plguin.exception.PluginInstallException;
import com.yjt.plguin.exception.PluginRetryException;
import com.yjt.plguin.listener.PluginBehaviorListener;
import com.yjt.plguin.listener.PluginLoadListener;
import com.yjt.plguin.listener.PluginListener;
import com.yjt.plguin.utils.ApkUtil;
import com.yjt.plguin.utils.CompatUtil;
import com.yjt.plguin.utils.FileUtil;
import com.yjt.plguin.utils.ManifestUtil;
import com.yjt.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginLoader implements PluginLoadListener {

    private final Context mContext;
    private final Map<String, PluginBaseInfo> mPackageHolder;

    public PluginLoader(Context context) {
        mContext = context.getApplicationContext();
        mPackageHolder = new HashMap<>();
    }

    @Override
    public PluginExtraInfo load(@NonNull PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("Loading plugin, id = " + pluginExtraInfo.getPluginId());
        pluginExtraInfo.marker("Load");

        onPreLoad(pluginExtraInfo);

        if (pluginExtraInfo.isCanceled()) {
            onCanceled(pluginExtraInfo);
            return pluginExtraInfo;
        }

        if (pluginExtraInfo.getPluginState() == Constant.State.PLUGIN_UPDATE_SUCCESS) {
            String path = pluginExtraInfo.getPluginPath();
            if (!TextUtils.isEmpty(path)) {
                // PluginBaseInfo was updated, start to load plugin.
                int retry = 0;
                pluginExtraInfo.setRetryTimes(pluginExtraInfo.getPluginListener().getPluginConfiguration().getRetryMaxTimes());
                while (true) {
                    if (pluginExtraInfo.isCanceled()) {
                        onCanceled(pluginExtraInfo);
                        return pluginExtraInfo;
                    }
                    try {
                        pluginExtraInfo.setPlugin(load(pluginExtraInfo.getPluginListener(), pluginExtraInfo.createPlugin(path).attach(pluginExtraInfo.getPluginListener())));
                        LogUtil.getInstance().println("Load plugin success, path = " + path);
                        pluginExtraInfo.switchState(Constant.State.PLUGIN_LOADED_SUCCESS);
                        onPostLoad(pluginExtraInfo);
                        return pluginExtraInfo;

                    } catch (PluginLoadException | PluginInstallException exception1) {
                        LogUtil.getInstance().println(exception1.toString());
                        try {
                            pluginExtraInfo.retry();
                            LogUtil.getInstance().println("Load fail, retry " + (retry++));
                            pluginExtraInfo.marker("Retry load " + retry);
                        } catch (PluginRetryException exception2) {
                            LogUtil.getInstance().println("Load plugin fail, error = " + exception1.toString());
                            onError(pluginExtraInfo, exception2);
                            return pluginExtraInfo;
                        }
                    }
                }
            } else {
                // Should not have this state.
                pluginExtraInfo.switchState(Constant.State.INITIATION);
                onPostLoad(pluginExtraInfo);
                return pluginExtraInfo;
            }
        } else {
            onPostLoad(pluginExtraInfo);
            return pluginExtraInfo;
        }
    }

    @Override
    public PluginBaseInfo load(PluginListener pluginListener, PluginBaseInfo pluginBaseInfo) throws PluginInstallException, PluginLoadException {
        String apkPath = pluginBaseInfo.getExternalPath();
        File apk = new File(apkPath);
        LogUtil.getInstance().println("Loading pluginBaseInfo, path = " + apkPath);

        if (!apk.exists()) {
            throw new PluginInstallException("Apk file not exist.", Constant.InstallException.FILE_NOT_FOUND);
        }

        PluginApk pluginApk;
        try {
            pluginApk = ManifestUtil.getInstance().parse(new File(apkPath));
            pluginBaseInfo.setPluginApk(pluginApk);

            CompatUtil.getInstance().checkCompat(pluginBaseInfo.getPluginApk().dependencies, pluginBaseInfo.getPluginApk().ignoreDependencies);
            LogUtil.getInstance().println("Check pluginBaseInfo dependency compat success.");

            if (TextUtils.isEmpty(pluginApk.packageName)) {
                throw new IOException("Can not get pluginBaseInfo's pkg name.");
            }
            if (TextUtils.isEmpty(pluginApk.versionCode)) {
                throw new IOException("Can not get pluginBaseInfo's version code.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new PluginInstallException("Can not get target pluginBaseInfo's packageInfo.", Constant.InstallException.OBTAIN_PACKAGE_INFO_FAILED);
        }

        // Check if the current version has been installed before.
        if (pluginListener.getPluginInstallListener().isInstalled(pluginApk.packageName, pluginApk.versionCode)) {
            String installPath = pluginListener.getPluginInstallListener().getInstallPath(pluginApk.packageName, pluginApk.versionCode);

            if (FileUtil.getInstance().exist(installPath)) {
                LogUtil.getInstance().println("The current version has been installed before.");
                pluginBaseInfo.setInstallPath(installPath);
                PluginBaseInfo loaded = getPlugin(pluginApk.packageName);

                if (loaded != null) {
                    // The current pluginBaseInfo has been loaded.
                    LogUtil.getInstance().println("The current pluginBaseInfo has been loaded, id = " + pluginApk.packageName);
                    return loaded;
                }

                // Load pluginBaseInfo from installed path.
                LogUtil.getInstance().println("Load pluginBaseInfo from installed path.");
                pluginBaseInfo = pluginBaseInfo.loadPlugin(mContext, installPath);
                putPlugin(pluginApk.packageName, pluginBaseInfo);
                return pluginBaseInfo;
            }
        }

        // The current pluginBaseInfo version is not yet installed.
        LogUtil.getInstance().println("PluginBaseInfo not installed, load it from target path.");
        PluginBaseInfo loaded = getPlugin(pluginApk.packageName);

        if (loaded != null) {
            LogUtil.getInstance().println("The current pluginBaseInfo has been loaded, id = " + pluginApk.packageName);
            return loaded;
        }

        LogUtil.getInstance().println("Load pluginBaseInfo from dest path.");

        // Install the dest file into inner install dir.
        String install = pluginListener.getPluginInstallListener().install(apkPath);
        pluginBaseInfo.setInstallPath(install);

        pluginBaseInfo = pluginBaseInfo.loadPlugin(mContext, install);
        putPlugin(pluginApk.packageName, pluginBaseInfo);

        // Delete temp file.
        if (apkPath.endsWith(pluginListener.getPluginConfiguration().getTempFileSuffix())) {
            FileUtil.getInstance().delete(apkPath);
        }

        return pluginBaseInfo;
    }

    @Override
    public PluginBaseInfo getPlugin(String packageName) {
        PluginBaseInfo pluginBaseInfo = mPackageHolder.get(packageName);
        if (pluginBaseInfo != null && !pluginBaseInfo.isHasPluginLoaded()) {
            return null;
        }
        return pluginBaseInfo;
    }

    @Override
    public synchronized void putPlugin(String id, PluginBaseInfo pluginBaseInfo) {
        if (pluginBaseInfo != null && pluginBaseInfo.isHasPluginLoaded()) {
            mPackageHolder.put(id, pluginBaseInfo);
        }
    }

    @Override
    public Class loadClass(@NonNull PluginBaseInfo pluginBaseInfo, String className) throws PluginLoadException {
        if (!pluginBaseInfo.isHasPluginLoaded()) {
            throw new PluginLoadException("Plug is not yet loaded.", Constant.LoadException.PLUGIN_NOT_LOADED);
        }
        try {
            return ApkUtil.getInstance().loadClass(pluginBaseInfo.getPluginApk().classLoader, className);
        } catch (Exception e) {
            throw new PluginLoadException(e, Constant.LoadException.OBTAIN_PLUGIN_CLASS_FAILED);
        }
    }

    @Override
    public PluginBehaviorListener createBehavior(PluginBaseInfo pluginBaseInfo) throws PluginLoadException {
        try {
            PluginApk pluginApk = pluginBaseInfo.getPluginApk();
            if (!TextUtils.isEmpty(pluginApk.application)) {
                // Create pluginBaseInfo's behavior via Manifest entry (PluginApplication).
                Class entry = loadClass(pluginBaseInfo, pluginApk.application);
                if (PluginApplication.class.isAssignableFrom(entry)) {
                    PluginApplication application = (PluginApplication) entry.newInstance();
                    application.setAppContext(mContext);
                    return application.getPluginBehaviorListener();
                } else {
                    LogUtil.getInstance().println("PluginBaseInfo's application can not assign to PluginApp.");
                    throw new PluginLoadException("PluginBaseInfo's application can not assign to PluginApp.", Constant.LoadException.OBTAIN_MANIFEST_BEHAVIOR_FAILED);
                }
            } else {
                LogUtil.getInstance().println("Cat not find pluginBaseInfo's app.");
                throw new PluginLoadException("Cat not find pluginBaseInfo's app.", Constant.LoadException.OBTAIN_MANIFEST_BEHAVIOR_FAILED);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new PluginLoadException(e, Constant.LoadException.OBTAIN_MANIFEST_BEHAVIOR_FAILED);
        }
    }

    private void onPreLoad(PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("onPreLoad state = " + pluginExtraInfo.getPluginState());
        pluginExtraInfo.getPluginListener().getPluginCallback().preLoad(pluginExtraInfo);
    }

    private void onError(PluginExtraInfo pluginExtraInfo, PluginException exception) {
        LogUtil.getInstance().println("onError state = " + pluginExtraInfo.getPluginState());
        pluginExtraInfo.switchState(Constant.State.PLUGIN_LOADED_FAIL);
        pluginExtraInfo.markException(exception);
        onPostLoad(pluginExtraInfo);
    }

    private void onCanceled(PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("onCanceled state = " + pluginExtraInfo.getPluginState());
        pluginExtraInfo.switchState(Constant.State.CANCELED);
        pluginExtraInfo.getPluginListener().getPluginCallback().onCancel(pluginExtraInfo);
    }

    private void onPostLoad(PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("onPostLoad state = " + pluginExtraInfo.getPluginState());

        if (pluginExtraInfo.getPluginState() == Constant.State.PLUGIN_LOADED_SUCCESS) {
            PluginBaseInfo pluginBaseInfo = pluginExtraInfo.getPlugin();
            if (pluginBaseInfo != null) {
                pluginExtraInfo.getPluginListener().getPluginCallback().postLoad(pluginExtraInfo, pluginBaseInfo);
                onLoadSuccess(pluginExtraInfo, pluginBaseInfo);
                return;
            } else {
                pluginExtraInfo.switchState(Constant.State.INITIATION);
            }
        }
        pluginExtraInfo.getPluginListener().getPluginCallback().loadFail(pluginExtraInfo, new PluginException("Can not get plugin instance, see request's state & exceptions", Constant.LoadException.CREATE_PLUGIN_FAILED));
    }

    private void onLoadSuccess(PluginExtraInfo pluginExtraInfo, PluginBaseInfo pluginBaseInfo) {
        LogUtil.getInstance().println("onLoadSuccess state = " + pluginExtraInfo.getPluginState());
        LogUtil.getInstance().println("Create behavior.");
        try {
            PluginBehaviorListener listener = pluginBaseInfo.createBehavior(mContext);
            if (listener == null) {
                listener = createBehavior(pluginBaseInfo);
            }
            // Create invocation proxy for behavior.
            listener = CustomProxy.getProxy(PluginBehaviorListener.class, listener);
            pluginBaseInfo.setPluginBehaviorListener(listener);
            pluginExtraInfo.getPluginListener().getPluginCallback().loadSuccess(pluginExtraInfo, pluginBaseInfo, listener);
        } catch (Exception e) {
            LogUtil.getInstance().println("Create behavior fail.");
            e.printStackTrace();
            PluginException exception = new PluginException(e, Constant.LoadException.OBTAIN_PLUGIN_BEHAVIOR_FAILED);
            pluginExtraInfo.markException(exception);
            pluginExtraInfo.getPluginListener().getPluginCallback().loadFail(pluginExtraInfo, exception);
        }
    }
}
