package com.yjt.engine.listener.implement;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yjt.engine.constant.Constant;
import com.yjt.engine.entity.LocalPluginInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.entity.RemotePluginInfo;
import com.yjt.engine.exception.PluginUpdateException;
import com.yjt.engine.exception.PluginCancelException;
import com.yjt.engine.exception.PluginRetryException;
import com.yjt.engine.listener.PluginUpdateListener;
import com.yjt.engine.utils.ApkUtil;
import com.yjt.engine.utils.FileUtil;
import com.yjt.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import moe.studio.downloader.DownloadRequest;
import moe.studio.downloader.SyncDownloadProcessorImpl;
import moe.studio.downloader.core.DownloadListener;
import moe.studio.downloader.core.DownloadProcessor;
import moe.studio.downloader.core.RetryPolicy;


public class PluginUpdater implements PluginUpdateListener {

    private final Context mContext;

    public PluginUpdater(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public PluginExtraInfo updatePlugin(@NonNull PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("Start update, id = " + pluginExtraInfo.getPluginId());
        pluginExtraInfo.marker("Update");
        onPreUpdate(pluginExtraInfo);

        // Request remote plugin.
        requestPlugin(pluginExtraInfo);

        if (pluginExtraInfo.isCanceled()) {
            onCanceled(pluginExtraInfo);
            return pluginExtraInfo;
        }

        if (pluginExtraInfo.getPluginState() == Constant.State.PLUGIN_RELEASE_FROM_ASSETS) {
            // Check capacity.
            try {
                pluginExtraInfo.getPluginListener().getPluginInstallListener().checkCapacity();
            } catch (IOException exception) {
                LogUtil.getInstance().println(exception.toString());
                onError(pluginExtraInfo, new PluginUpdateException(exception, Constant.UpdateException.LACK_SPACE));
                return pluginExtraInfo;
            }

            // Extract plugin from assets.
            File tempFile;
            try {
                LogUtil.getInstance().println("PluginId = " + pluginExtraInfo.getPluginId());
                tempFile = pluginExtraInfo.getPluginListener().getPluginInstallListener().createTempFile(pluginExtraInfo.getPluginId());
                LogUtil.getInstance().println("tempFile = " + tempFile.getAbsolutePath());
            } catch (IOException exception) {
                LogUtil.getInstance().println("Can not get temp file, error = " + exception.getLocalizedMessage());
                LogUtil.getInstance().println(exception.toString());
                onError(pluginExtraInfo, new PluginUpdateException(exception, Constant.UpdateException.EXTRACT_ASSETS_FAILED));
                return pluginExtraInfo;
            }

            int retry = 0;
            pluginExtraInfo.setRetryTimes(pluginExtraInfo.getPluginListener().getPluginConfiguration().getRetryMaxTimes());

            while (true) {
                if (pluginExtraInfo.isCanceled()) {
                    onCanceled(pluginExtraInfo);
                    return pluginExtraInfo;
                }

                try {
                    FileUtil.getInstance().copyFileFromAsset(mContext, pluginExtraInfo.getAssetsPath(), tempFile);
                    LogUtil.getInstance().println("Extract plugin from assets success.");
                    pluginExtraInfo.setPluginPath(tempFile.getAbsolutePath());
                    pluginExtraInfo.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
                    onPostUpdate(pluginExtraInfo);
                    return pluginExtraInfo;

                } catch (IOException e) {
                    LogUtil.getInstance().println(e.toString());
                    try {
                        pluginExtraInfo.retry();
                        LogUtil.getInstance().println("Extract fail, retry " + (retry++));
                        pluginExtraInfo.marker("Retry extract " + retry);
                    } catch (PluginRetryException exception) {
                        LogUtil.getInstance().println("Extract plugin from assets fail, error = " + e.toString());
                        onError(pluginExtraInfo, new PluginUpdateException(e, Constant.UpdateException.EXTRACT_ASSETS_FAILED));
                        return pluginExtraInfo;
                    }
                }
            }
        } else if (pluginExtraInfo.getPluginState() == Constant.State.PLUGIN_DOWNLOAD) {
            // Check capacity.
            try {
                pluginExtraInfo.getPluginListener().getPluginInstallListener().checkCapacity();
            } catch (IOException exception) {
                LogUtil.getInstance().println(exception.toString());
                onError(pluginExtraInfo, new PluginUpdateException(exception, Constant.UpdateException.LACK_SPACE));
                return pluginExtraInfo;
            }

            // Download plugin from online.
            File tempFile;
            try {
                LogUtil.getInstance().println("PluginId = " + pluginExtraInfo.getPluginId());
                tempFile = pluginExtraInfo.getPluginListener().getPluginInstallListener().createTempFile(pluginExtraInfo.getPluginId());
                LogUtil.getInstance().println("tempFile = " + tempFile.getAbsolutePath());
            } catch (IOException exception) {
                LogUtil.getInstance().println("Can not get temp file, error = " + exception.getLocalizedMessage());
                LogUtil.getInstance().println(exception.toString());
                onError(pluginExtraInfo, new PluginUpdateException(exception, Constant.UpdateException.CREATE_TEMP_FILE_FAILED));
                return pluginExtraInfo;
            }

            try {
                downloadPlugin(pluginExtraInfo, tempFile);
                LogUtil.getInstance().println("Download plugin online success.");
                pluginExtraInfo.setPluginPath(tempFile.getAbsolutePath());
                pluginExtraInfo.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
                onPostUpdate(pluginExtraInfo);
                return pluginExtraInfo;
            } catch (PluginUpdateException exception) {
                LogUtil.getInstance().println("Download plugin fail, error = " + exception.getLocalizedMessage());
                LogUtil.getInstance().println(exception.toString());
                pluginExtraInfo.markException(exception);
                onError(pluginExtraInfo, exception);
                return pluginExtraInfo;
            } catch (PluginCancelException exception) {
                onCanceled(pluginExtraInfo);
                return pluginExtraInfo;
            }
        } else {
            onPostUpdate(pluginExtraInfo);
            return pluginExtraInfo;
        }
    }

    private void onPreUpdate(PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("onPreUpdate state = " + pluginExtraInfo.getPluginState());
        pluginExtraInfo.getPluginListener().getPluginCallback().preUpdate(pluginExtraInfo);
    }

    private void onCanceled(PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("onCanceled state = " + pluginExtraInfo.getPluginState());
        pluginExtraInfo.switchState(Constant.State.CANCELED);
        pluginExtraInfo.getPluginListener().getPluginCallback().onCancel(pluginExtraInfo);
    }

    private void onError(PluginExtraInfo pluginExtraInfo, PluginUpdateException exception) {
        LogUtil.getInstance().println("onError state = " + pluginExtraInfo.getPluginState());
        pluginExtraInfo.switchState(Constant.State.UPDATE_PLUGIN_UPDATE_FAIL);
        pluginExtraInfo.markException(exception);
        pluginExtraInfo.doUpdateFailPolicy(pluginExtraInfo, exception);
        onPostUpdate(pluginExtraInfo);
    }

    private void onPostUpdate(PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("onPostUpdate state = " + pluginExtraInfo.getPluginState());
        pluginExtraInfo.getPluginListener().getPluginCallback().postUpdate(pluginExtraInfo);
    }

    public PluginExtraInfo requestPlugin(PluginExtraInfo pluginExtraInfo) {
        LogUtil.getInstance().println("Request remote plugin info.");

        // Check clear existing plugins.
        if (pluginExtraInfo.isClearLocalPlugins()) {
            pluginExtraInfo.getPluginListener().getPluginInstallListener().deletePlugins(pluginExtraInfo.requestPluginId());
        }

        // Get local existing plugin info.
        pluginExtraInfo.getLocalPluginInfo(pluginExtraInfo);
        List<LocalPluginInfo> localPlugins = pluginExtraInfo.getLocalPluginInfos();

        if (localPlugins != null && localPlugins.size() > 0) {
            LocalPluginInfo localPluginInfo = localPlugins.get(0);
            // Getting plugin installed path.
            String installPath = pluginExtraInfo.getPluginListener().getPluginInstallListener().getInstallPath(localPluginInfo.pluginId, String.valueOf(localPluginInfo.version));
            pluginExtraInfo.setPluginPath(installPath);
            pluginExtraInfo.setLocalPluginPath(installPath);
        }

        try {
            // Request remote plugin info.
            List<? extends RemotePluginInfo> remotePluginInfos = pluginExtraInfo.requestRemotePluginInfo(mContext);
            pluginExtraInfo.setRemotePluginInfos(remotePluginInfos);
            pluginExtraInfo.setPluginId(pluginExtraInfo.requestPluginId());
            pluginExtraInfo.setClearLocalPlugins(pluginExtraInfo.requestClearLocalPlugins(mContext));
            if (pluginExtraInfo.isFromAssets()) {
                pluginExtraInfo.fromAssets(pluginExtraInfo.getAssetsPath(), pluginExtraInfo.getAssetsVersion());
            }
            if (TextUtils.isEmpty(pluginExtraInfo.getPluginId())) {
                doUpdatePolicy(Constant.State.ONLINE_PLUGIN_ILLEGAL, pluginExtraInfo);
                return pluginExtraInfo;
            }
            // Success.
            doUpdatePolicy(Constant.State.PLUGIN_DOWNLOAD_SUCCESS, pluginExtraInfo);
        } catch (Exception e) {
            LogUtil.getInstance().println("Request remote plugin info fail, error = " + e.toString());
            LogUtil.getInstance().println(e.toString());
            pluginExtraInfo.switchState(Constant.State.REMOTE_INFO_OBTAIN_FAIL);
            PluginUpdateException exception = new PluginUpdateException(e, Constant.UpdateException.HTTP_REQUEST_FAILED);
            pluginExtraInfo.markException(exception);
            pluginExtraInfo.onGetRemotePluginFail(pluginExtraInfo, exception);
        }
        return pluginExtraInfo;
    }

    private void doUpdatePolicy(int responseCode, @NonNull PluginExtraInfo pluginExtraInfo) {
        if (responseCode == Constant.State.PLUGIN_DOWNLOAD_SUCCESS) {
            if (pluginExtraInfo.isFromAssets()) {
                // Using plugin from assets.
                LogUtil.getInstance().println("Using plugin from assets");
                String apkPath = pluginExtraInfo.getPluginListener().getPluginInstallListener().getInstallPath(pluginExtraInfo.getPluginId(), String.valueOf(pluginExtraInfo.getAssetsVersion()));
                if (pluginExtraInfo.getPluginListener().getPluginInstallListener().isInstalled(apkPath)) {
                    // The current version of plugin has been installed before.
                    pluginExtraInfo.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
                    pluginExtraInfo.setPluginPath(apkPath);
                } else {
                    // Should extract plugin form assets.
                    pluginExtraInfo.switchState(Constant.State.PLUGIN_RELEASE_FROM_ASSETS);
                    LogUtil.getInstance().println("Extract plugin from assets, path = " + pluginExtraInfo.getAssetsPath());
                }
            } else {
                // Using online plugin.
                LogUtil.getInstance().println("Using online plugin.");
                // Calculate the best remote plugin version.
                // (Latest version & APP_BUILD is meet.)
                List<? extends RemotePluginInfo> remotePluginInfos = pluginExtraInfo.getRemotePluginInfos();
                RemotePluginInfo bestPlugin = null;
                int appBuild = Integer.MAX_VALUE;
                PackageInfo localPackageInfo = ApkUtil.getInstance().getLocalPackageInfo(mContext);
                if (pluginExtraInfo.getPluginListener().getPluginConfiguration().isDebug() && localPackageInfo != null) {
                    appBuild = localPackageInfo.versionCode;
                }
                LogUtil.getInstance().println("App build = " + appBuild);

                // Get the best plugin version.
                if (remotePluginInfos != null) {
                    for (RemotePluginInfo pluginInfo : remotePluginInfos) {
                        if (pluginInfo.isValid && pluginInfo.minAppBuild <= appBuild) {
                            bestPlugin = pluginInfo;
                            break;
                        }
                    }
                }

                if (bestPlugin == null) {
                    LogUtil.getInstance().println("No available plugin, abort.");
                    pluginExtraInfo.switchState(Constant.State.LOCAL_AND_REMOTE_PLUGIN_NON_EXISTENT);
                } else {
                    LocalPluginInfo bestLocalPlugin = chooseBestPluginFromLocal(pluginExtraInfo.getLocalPluginInfos(), bestPlugin);
                    if (bestLocalPlugin == null) {
                        // No local best plugin, should download from remote.
                        LogUtil.getInstance().println("Download new plugin, version = " + bestPlugin.version + ", url = " + bestPlugin.downloadUrl);
                        pluginExtraInfo.switchState(Constant.State.PLUGIN_DOWNLOAD);
                        pluginExtraInfo.setDownloadUrl(bestPlugin.downloadUrl);
                        pluginExtraInfo.setFileSize(bestPlugin.fileSize);
                        pluginExtraInfo.setForceUpdate(bestPlugin.isForceUpdate);
                    } else {
                        // The best plugin version has been installed before.
                        LogUtil.getInstance().println("Use local plugin, version = " + bestLocalPlugin.version);
                        pluginExtraInfo.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
                        pluginExtraInfo.setPluginPath(pluginExtraInfo.getPluginListener().getPluginInstallListener().getInstallPath(bestLocalPlugin.pluginId, String.valueOf(bestLocalPlugin.version)));
                    }
                }
            }
        } else if (responseCode == Constant.State.ONLINE_PLUGIN_ILLEGAL) {
            LogUtil.getInstance().println("Request remote plugin info fail, illegal online plugin.");
            pluginExtraInfo.switchState(Constant.State.LOCAL_AND_REMOTE_PLUGIN_NON_EXISTENT);
            pluginExtraInfo.onGetRemotePluginFail(pluginExtraInfo, null);
        }
    }

    @Nullable
    private LocalPluginInfo chooseBestPluginFromLocal(List<LocalPluginInfo> localPlugins, RemotePluginInfo bestPlugin) {
        if (localPlugins == null) {
            return null;
        }

        for (int i = 0; i < localPlugins.size(); i++) {
            LocalPluginInfo item = localPlugins.get(i);
            // Getting the latest version of plugin, which is not disabled.
            if (item.version == bestPlugin.version) {
                return item;
            }
        }
        return null;
    }

    private void downloadPlugin(final PluginExtraInfo pluginExtraInfo, File destFile) throws PluginUpdateException, PluginCancelException {
        // Using FileDownloader to complete the download task.
        final long fileSize = pluginExtraInfo.getFileSize();
        final String[] errors = {};

        DownloadRequest request = new DownloadRequest(pluginExtraInfo.getDownloadUrl())
                .setContentLength(fileSize)
                .setDestFile(destFile)
                .setDeleteDestFileOnFailure(true)
                .setRetryPolicy(new RetryPolicy.RetryPolicyImpl(pluginExtraInfo.getPluginListener().getPluginConfiguration().getRetryMaxTimes()))
                .setListener(new DownloadListener() {

                    @Override
                    public void onComplete(DownloadRequest request) {
                        LogUtil.getInstance().println("Download complete, original fileSize = " + fileSize + ", downloadedSize = " + request.getCurrentBytes());
                    }

                    @Override
                    public void onFailed(DownloadRequest request, int errorCode, String errorMessage) {
                        errors[0] = errorMessage;
                    }

                    @Override
                    public void onProgress(DownloadRequest DownloadInfo, long totalBytes, long downloadedBytes, int progress, long bytesPerSecond) {
                        // notify progress
                        if (fileSize > 0) {
                            LogUtil.getInstance().println("Notify progress  = " + progress);
                            pluginExtraInfo.getPluginListener().getPluginCallback().notifyProgress(pluginExtraInfo, (float) progress / 100F);
                        }
                    }

                    @Override
                    public boolean isCanceled() {
                        return pluginExtraInfo.isCanceled();
                    }

                });

        // Downloading asynchronously.
        DownloadProcessor processor = new SyncDownloadProcessorImpl();
        processor.attach(mContext);
        processor.add(request);

        if (pluginExtraInfo.isCanceled()) {
            throw new PluginCancelException(Constant.UpdateException.DOWNLOAD_CANCELED);
        } else if (!TextUtils.isEmpty(errors[0])) {
            throw new PluginUpdateException(errors[0], Constant.UpdateException.DOWNLOAD_FAILED);
        }
    }
}
