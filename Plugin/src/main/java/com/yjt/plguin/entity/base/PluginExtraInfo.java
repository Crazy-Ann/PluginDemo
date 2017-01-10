package com.yjt.plguin.entity.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yjt.plguin.BuildConfig;
import com.yjt.plguin.constant.Constant;
import com.yjt.plguin.entity.LocalPluginInfo;
import com.yjt.plguin.entity.RemotePluginInfo;
import com.yjt.plguin.exception.PluginUpdateException;
import com.yjt.plguin.exception.PluginRetryException;
import com.yjt.plguin.listener.PluginStateListener;
import com.yjt.plguin.listener.PluginListener;
import com.yjt.utils.LogUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 插件额外信息(请求任务)
 *
 * @param <P>
 */
public abstract class PluginExtraInfo<P extends PluginBaseInfo> {

    protected String mPluginId;
    protected int mRetryTimes;
    protected int mPluginState;
    protected StringBuffer mPluginStateRecord;
    protected String mRemotePluginPath;
    protected String mLocalPluginPath;
    protected boolean isClearLocalPlugins;

    protected P mPlugin;
    protected PluginStateListener mPluginStateListener;
    protected PluginListener mPluginListener;
    protected List<Exception> mExceptions;

    // FOR ASSETS PLUGIN.
    protected int mAssetsVersion;
    protected String mAssetsPath;
    protected boolean isFromAssets;
    protected long mFileSize;

    // FOR ONLINE PLUGIN.
    protected String mDownloadUrl;
    protected boolean isForceUpdate;

    // PLUGIN INFO LIST
    protected List<LocalPluginInfo> mLocalPluginInfos;
    protected List<? extends RemotePluginInfo> mRemotePluginInfos;

    private final byte[] mLock;

    public PluginExtraInfo() {
        this.mRetryTimes = Constant.Configuration.RETRY_MAXIMUM_TIMES_NONE;
        this.mPluginState = Constant.State.INITIATION;
        this.mLock = new byte[0];
        this.mPluginStateRecord = new StringBuffer(String.valueOf(mPluginState));
    }

    @Nullable
    public String getPluginId() {
        return mPluginId == null ? requestPluginId() : mPluginId;
    }

    public void setPluginId(String pluginId) {
        this.mPluginId = pluginId;
    }

    public void setRetryTimes(int retryTimes) {
        if (retryTimes > 0) {
            this.mRetryTimes = retryTimes;
        }
    }

    public void retry() throws PluginRetryException {
        if (--mRetryTimes < 0) {
            throw new PluginRetryException();
        }
    }

    public int getPluginState() {
        synchronized (mLock){
            return mPluginState;
        }
    }

    public PluginExtraInfo switchState(int pluginState) {
        synchronized (mLock){
            this.mPluginState = pluginState;
        }
        return marker(String.valueOf(pluginState));
    }

    public void cancel() {
        synchronized (mLock){
            switchState(Constant.State.CANCELED);
        }
    }

    public boolean isCanceled() {
        return mPluginState == Constant.State.CANCELED;
    }

    public String getPluginStateRecord() {
        return mPluginStateRecord.toString();
    }

    public String getLocalPluginPath() {
        return mLocalPluginPath;
    }

    @Nullable
    public String getPluginPath() {
        if (!TextUtils.isEmpty(mRemotePluginPath)) {
            return mRemotePluginPath;
        }
        return mLocalPluginPath;
    }

    public void setPluginPath(String remotePluginPath) {
        this.mRemotePluginPath = remotePluginPath;
    }

    public void setLocalPluginPath(String localPluginPath) {
        this.mLocalPluginPath = localPluginPath;
    }

    public boolean isClearLocalPlugins() {
        return isClearLocalPlugins;
    }

    public void setClearLocalPlugins(boolean clearLocalPlugins) {
        this.isClearLocalPlugins = clearLocalPlugins;
    }

    public P getPlugin() {
        return mPlugin;
    }

    public void setPlugin(P plugin) {
        this.mPlugin = plugin;
    }

    public PluginStateListener getPluginStateListener() {
        return mPluginStateListener;
    }

    public void setPluginStateListener(PluginStateListener pluginStateListener) {
        this.mPluginStateListener = pluginStateListener;
    }

    public PluginListener getPluginListener() {
        return mPluginListener;
    }

    @Nullable
    public List<Exception> getExceptions() {
        return mExceptions;
    }

    public PluginExtraInfo markException(@NonNull Exception exception) {
        if (mExceptions == null) {
            this.mExceptions = new ArrayList<>();
        }
        this.mExceptions.add(exception);
        return marker(exception.getLocalizedMessage());
    }

    public boolean isFromAssets() {
        return false;
    }

    public String getAssetsPath() {
        return null;
    }

    public int getAssetsVersion() {
        return -1;
    }

    public void fromAssets(String path, int version) {
        this.isFromAssets = true;
        this.mAssetsPath = path;
        this.mAssetsVersion = version;
    }

    @Nullable
    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
    }

    public long getFileSize() {
        return mFileSize;
    }

    public void setFileSize(long fileSize) {
        this.mFileSize = fileSize;
    }

    public boolean isForceUpdate() {
        return isForceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.isForceUpdate = forceUpdate;
    }

    @Nullable
    public List<LocalPluginInfo> getLocalPluginInfos() {
        return mLocalPluginInfos;
    }

    public void setLocalPluginInfos(List<LocalPluginInfo> localPluginInfos) {
        this.mLocalPluginInfos = localPluginInfos;
    }

    @Nullable
    public List<? extends RemotePluginInfo> getRemotePluginInfos() {
        return mRemotePluginInfos;
    }

    public void setRemotePluginInfos(List<? extends RemotePluginInfo> remotePluginInfos) {
        this.mRemotePluginInfos = remotePluginInfos;
    }

    public PluginExtraInfo marker(String log) {
        if (!TextUtils.isEmpty(log)) {
            mPluginStateRecord.append(" --> ").append(log);
        }
        return this;
    }

    public PluginExtraInfo attach(PluginListener pluginListener) {
        mPluginListener = pluginListener;
        return this;
    }

    public void getLocalPluginInfo(@NonNull PluginExtraInfo pluginExtraInfo) {
        String pluginId = getPluginId();
        if (!TextUtils.isEmpty(pluginId)) {
            pluginExtraInfo.setLocalPluginInfos(getLocalPluginInfoById(pluginId));
        }
    }

    protected List<LocalPluginInfo> getLocalPluginInfoById(@NonNull String id) {
        List<LocalPluginInfo> localPluginInfoList = new ArrayList<>();
        String pluginDir = mPluginListener.getPluginInstallListener().getPluginDir(id);
        File file = new File(pluginDir);
        if (!file.exists()) {
            LogUtil.getInstance().println("No local plugin, path = " + file.getAbsolutePath());
            return localPluginInfoList;
        }

        for (String version : file.list()) {
            if (TextUtils.isDigitsOnly(version)) {
                // Version can only be integer.
                if (mPluginListener.getPluginInstallListener().isInstalled(id, version)) {
                    // PluginBaseInfo has been already installed.
                    LocalPluginInfo item = new LocalPluginInfo();
                    item.pluginId = id;
                    item.version = Integer.valueOf(version);
                    item.isValid = true;
                    localPluginInfoList.add(item);
                }
            } else {
                // Delete invalid file.
                delete(new File(pluginDir + File.separator + version));
            }
        }
        Collections.sort(localPluginInfoList);

        // Dump existing plugin versions.
        if (BuildConfig.DEBUG) {
            LogUtil.getInstance().println("Found local plugin \"" + id + "\" :");
            for (LocalPluginInfo item : localPluginInfoList) {
                LogUtil.getInstance().println("Version =  " + item.version + ", path = "
                                                      + mPluginListener.getPluginInstallListener().getInstallPath(id, String.valueOf(item.version)));
            }
        }
        return localPluginInfoList;
    }

    public void onGetRemotePluginFail(@NonNull PluginExtraInfo pluginExtraInfo, PluginUpdateException exception) {
        pluginExtraInfo.setPluginId(getPluginId());
        useLocalAvailablePlugin(pluginExtraInfo);
    }

    public void doUpdateFailPolicy(@NonNull PluginExtraInfo pluginExtraInfo, PluginUpdateException exception) {
        if (!pluginExtraInfo.isForceUpdate()) {
            // Use local installed plugin if the current plugin version is not fore-update.
            useLocalAvailablePlugin(pluginExtraInfo);
        } else {
            // No available plugin.
            pluginExtraInfo.switchState(Constant.State.LOCAL_AND_REMOTE_PLUGIN_NON_EXISTENT);
        }
    }

    protected void useLocalAvailablePlugin(@NonNull PluginExtraInfo pluginExtraInfo) {
        String path = pluginExtraInfo.getLocalPluginPath();
        if (!TextUtils.isEmpty(path)) {
            pluginExtraInfo.setPluginPath(path);
            pluginExtraInfo.switchState(Constant.State.PLUGIN_UPDATE_SUCCESS);
        }
    }

    private static boolean delete(File file) {
        return FileUtils.deleteQuietly(file);
    }

    public abstract List<? extends RemotePluginInfo> requestRemotePluginInfo(Context context) throws Exception;

    public abstract String requestPluginId();

    public abstract boolean requestClearLocalPlugins(Context context);

    public abstract PluginBaseInfo createPlugin(String path);
}
