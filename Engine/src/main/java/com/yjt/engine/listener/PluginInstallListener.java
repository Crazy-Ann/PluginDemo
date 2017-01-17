package com.yjt.engine.listener;

import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yjt.engine.exception.PluginInstallException;

import java.io.File;
import java.io.IOException;

/**
 * 插件安装接口
 */
public interface PluginInstallListener {

    boolean checkSafety(String apkPath);

    boolean checkSafety(String apkPath, boolean deleteIfInvalid);

    boolean checkSafety(String pluginId, String version, boolean deleteIfInvalid);

    void delete(String apkPath);

    void delete(String pluginId, String version);

    void deletePlugins(String pluginId);

    void checkCapacity() throws IOException;

    File createTempFile(String prefix) throws IOException;

    String getPluginDir();

    String getPluginDir(@NonNull String pluginId);

    String getInstallPath(String pluginId, String version);

    @Nullable
    String getInstallPath(String apkPath);

    boolean isInstalled(String apkPath);

    boolean isInstalled(String pluginId, String version);

    String install(String apkPath) throws PluginInstallException;

    PackageInfo getPackageInfo(String apkPath);
}
