package com.yjt.plguin.listener.implement;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yjt.plguin.config.PluginConfiguration;
import com.yjt.plguin.constant.Constant;
import com.yjt.plguin.entity.PluginApk;
import com.yjt.plguin.exception.PluginInstallException;
import com.yjt.plguin.listener.PluginInstallListener;
import com.yjt.plguin.utils.ApkUtil;
import com.yjt.plguin.utils.FileUtil;
import com.yjt.plguin.utils.ManifestUtil;
import com.yjt.plguin.utils.SignatureUtil;
import com.yjt.utils.LogUtil;

import java.io.File;
import java.io.IOException;


public class PluginInstaller implements PluginInstallListener {

    private final File mPluginDir;
    private final File mTempDir;
    private final Context mContext;
    private final PluginConfiguration mPluginConfiguration;

    public PluginInstaller(Context context, PluginConfiguration pluginConfiguration) {
        this.mContext = context.getApplicationContext();
        this.mPluginConfiguration = pluginConfiguration;
        this.mPluginDir = mContext.getDir(mPluginConfiguration.getPluginDir(), Context.MODE_PRIVATE);
        File cache = mContext.getExternalCacheDir();
        if (cache == null || cache.getFreeSpace() < Constant.Space.PLUGIN_MINIMUM_REQUIRED_CAPACITY) {
            cache = mContext.getCacheDir();
        }
        this.mTempDir = cache;
    }


    private boolean isDebug() {
        return mPluginConfiguration.isDebug();
    }

    @Override
    public boolean checkSafety(String apkPath) {
        if (!FileUtil.getInstance().exist(apkPath)) {
            LogUtil.getInstance().println("PluginBaseInfo not found, path = " + String.valueOf(apkPath));
            return false;
        }

        if (mPluginConfiguration.isDebug()) {
            LogUtil.getInstance().println("Debug mode, skip validation, path = " + apkPath);
            return true;
        }

        Signature[] pluginSignatures = SignatureUtil.getInstance().getSignatures(mContext, apkPath);
        if (pluginSignatures == null) {
            LogUtil.getInstance().println("Can not get plugin's signatures , path = " + apkPath);
            return false;
        }

        if (mPluginConfiguration.isDebug()) {
            LogUtil.getInstance().println("Dump plugin signatures:");
            SignatureUtil.getInstance().printSignature(pluginSignatures);
        }

        if (mPluginConfiguration.useCustomSignature()) {
            // Check if the plugin's signatures are the same with the given one.
            if (!SignatureUtil.getInstance().isSignaturesSame(mPluginConfiguration.getCustomSignature(), pluginSignatures)) {
                LogUtil.getInstance().println("PluginBaseInfo's signatures are different, path = " + apkPath);
                return false;
            }
        } else {
            // Check if the plugin's signatures are the same with current app.
            Signature[] mainSignatures = SignatureUtil.getInstance().getSignatures(mContext);
            if (!SignatureUtil.getInstance().isSignaturesSame(mainSignatures, pluginSignatures)) {
                LogUtil.getInstance().println("PluginBaseInfo's signatures differ from the app's.");
                return false;
            }
        }
        LogUtil.getInstance().println("Check plugin's signatures success, path = " + apkPath);
        return true;
    }

    @Override
    public boolean checkSafety(String apkPath, boolean deleteIfInvalid) {
        if (checkSafety(apkPath)) {
            return true;
        }

        if (deleteIfInvalid) {
            delete(apkPath);
        }
        return false;
    }

    @Override
    public boolean checkSafety(String pluginId, String version, boolean deleteIfInvalid) {
        if (checkSafety(getInstallPath(pluginId, version))) {
            return true;
        }

        if (deleteIfInvalid) {
            delete(pluginId, version);
        }
        return false;
    }

    @Override
    public void delete(String apkPath) {
        FileUtil.getInstance().delete(apkPath);
    }

    @Override
    public void delete(String pluginId, String version) {
        FileUtil.getInstance().delete(getInstallPath(pluginId, version));
    }

    @Override
    public void deletePlugins(String pluginId) {
        File file = new File(getPluginDir(pluginId));
        if (!file.exists()) {
            LogUtil.getInstance().println("Delete fail, dir not found, path = " + file.getAbsolutePath());
            return;
        }
        FileUtil.getInstance().delete(file);
    }

    @Override
    public void checkCapacity() throws IOException {
        if (mPluginDir.getFreeSpace() < Constant.Space.PLUGIN_MINIMUM_REQUIRED_CAPACITY) {
            throw new IOException("No enough capacity.");
        }
    }

    @Override
    public File createTempFile(String prefix) throws IOException {
        return File.createTempFile(prefix, mPluginConfiguration.getTempFileSuffix(), mTempDir);
    }

    @Override
    public String getPluginDir() {
        return mPluginDir.getAbsolutePath();
    }

    @Override
    public String getPluginDir(@NonNull String pluginId) {
        return getPluginDir() + File.separator + pluginId;
    }

    @Override
    public String getInstallPath(String pluginId, String version) {
        return getPluginDir() + File.separator + pluginId + File.separator + version
                + File.separator + mPluginConfiguration.getPluginName();
    }

    @Nullable
    @Override
    public String getInstallPath(String apkPath) {
        PackageInfo packageInfo = getPackageInfo(apkPath);
        if (packageInfo == null) {
            try {
                PluginApk apk = ManifestUtil.getInstance().parse(new File(apkPath));
                return getInstallPath(apk.packageName, apk.versionCode);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return getInstallPath(packageInfo.packageName, String.valueOf(packageInfo.versionCode));
    }

    @Override
    public boolean isInstalled(String apkPath) {
        if (mPluginConfiguration.isIgnoreInstalledPlugin()) {
            // Force to use external plugin by ignoring installed one.
            return false;
        }

        PackageInfo packageInfo = getPackageInfo(apkPath);

        if (packageInfo == null) {
            try {
                PluginApk apk = ManifestUtil.getInstance().parse(new File(apkPath));
                return checkSafety(apk.packageName, apk.versionCode, true);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return checkSafety(packageInfo.packageName, String.valueOf(packageInfo.versionCode), true);
        }
    }

    @Override
    public boolean isInstalled(String pluginId, String version) {
        if (mPluginConfiguration.isIgnoreInstalledPlugin()) {
            // Force to use external plugin by ignoring installed one.
            return false;
        }
        return checkSafety(pluginId, version, true);
    }

    @Override
    public String install(String apkPath) throws PluginInstallException {
        LogUtil.getInstance().println("Install plugin, path = " + apkPath);
        File apkFile = new File(apkPath);

        if (!apkFile.exists()) {
            LogUtil.getInstance().println("PluginBaseInfo path not exist");
            throw new PluginInstallException("PluginBaseInfo file not exist.", Constant.InstallException.FILE_NOT_FOUND);
        }

        // Check plugin's signatures.
        LogUtil.getInstance().println("Check plugin's signatures.");
        if (!checkSafety(apkPath, true)) {
            LogUtil.getInstance().println("Check plugin's signatures failed.");
            throw new PluginInstallException("Check plugin's signatures fail.", Constant.InstallException.SIGNATURE_ERROR);
        }

        // Get install path.（"<id>/<version>/base-1.apk"）
        String installPath = getInstallPath(apkPath);
        if (TextUtils.isEmpty(installPath)) {
            throw new PluginInstallException("Can not get install path.", Constant.InstallException.OBTAIN_INSTALL_PATH_FAILED);
        }
        LogUtil.getInstance().println("Install path = " + installPath);

        // Install plugin file to install path.
        // Check if the plugin has already been installed.
        File destApk = new File(installPath);
        if (destApk.exists()) {
            if (!mPluginConfiguration.isIgnoreInstalledPlugin() && checkSafety(destApk.getAbsolutePath(), true)) {
                LogUtil.getInstance().println("PluginBaseInfo has been already installed.");
                return installPath;
            }
            LogUtil.getInstance().println("Ignore installed plugin.");
        }

        LogUtil.getInstance().println("Install plugin, from = " + apkPath + ", to = " + installPath);

        if (apkFile.renameTo(destApk)) {
            LogUtil.getInstance().println("Rename success.");
            return installPath;
        }

        try {
            checkCapacity();
        } catch (IOException e) {
            e.printStackTrace();
            throw new PluginInstallException(e, Constant.InstallException.LACK_SPACE);
        }

        try {
            LogUtil.getInstance().println("Rename fail, try copy file.");
            FileUtil.getInstance().copyFile(apkFile, destApk);
        } catch (IOException e) {
            e.printStackTrace();
            throw new PluginInstallException(e, Constant.InstallException.INSTALL_FAILED);
        }
        return installPath;
    }

    @Override
    public PackageInfo getPackageInfo(String apkPath) {
        return ApkUtil.getInstance().getPackageInfo(mContext, apkPath);
    }
}
