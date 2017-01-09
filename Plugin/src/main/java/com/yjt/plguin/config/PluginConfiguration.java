package com.yjt.plguin.config;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yjt.plguin.BuildConfig;
import com.yjt.plguin.constant.Constant;

public final class PluginConfiguration {

    private final int mRetryMaxTimes;
    private final String mPluginDir;
    private final String mOptimizedDexDir;
    private final String mSoLibraryDir;
    private final String mTempSoLibraryDir;
    private final String mTempFileSuffix;
    private final String mPluginName;
    private final String mCustomSignature;
    private final boolean isDebug;
    private final boolean isIgnoreInstalledPlugin;

    public PluginConfiguration(int retryMaxTimes, String pluginDir, String optimizedDexDir, String soLibraryDir, String tempSoLibraryDir, String tempFileSuffix, String pluginName, String customSignature, boolean isDebug, boolean isIgnoreInstalledPlugin) {
        this.mRetryMaxTimes = retryMaxTimes;
        this.mPluginDir = pluginDir;
        this.mOptimizedDexDir = optimizedDexDir;
        this.mSoLibraryDir = soLibraryDir;
        this.mTempSoLibraryDir = tempSoLibraryDir;
        this.mTempFileSuffix = tempFileSuffix;
        this.mPluginName = pluginName;
        this.mCustomSignature = customSignature;
        this.isDebug = isDebug;
        this.isIgnoreInstalledPlugin = isIgnoreInstalledPlugin;
    }

    public int getRetryMaxTimes() {
        return mRetryMaxTimes;
    }

    public String getPluginDir() {
        return mPluginDir;
    }

    public String getOptimizedDexDir() {
        return mOptimizedDexDir;
    }

    public String getSoLibraryDir() {
        return mSoLibraryDir;
    }

    public String getTempSoLibraryDir() {
        return mTempSoLibraryDir;
    }

    public String getTempFileSuffix() {
        return mTempFileSuffix;
    }

    public String getPluginName() {
        return mPluginName;
    }

    public String getCustomSignature() {
        return mCustomSignature;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isIgnoreInstalledPlugin() {
        return isIgnoreInstalledPlugin;
    }

    public boolean useCustomSignature() {
        return !TextUtils.isEmpty(mCustomSignature);
    }

    public static class Builder {

        private int mRetryMaxTimes = Constant.Configuration.RETRY_MAXIMUM_TIMES;
        private String mPluginDir = Constant.Configuration.PLUGIN_DIR;
        private String mOptimizedDexDir = Constant.Configuration.OPTIMIZED_DEX_DIR;
        private String mSoLibraryDir = Constant.Configuration.SO_LIBRARY_DIR;
        private String mTempSoLibraryDir = Constant.Configuration.TEMP_SO_LIBRARY_DIR;
        private String mTempFileSuffix = Constant.Configuration.TEMP_FILE_SUFFIX;
        private String mPluginName = Constant.Configuration.PLUGIN_NAME;
        private String mCustomSignature;
        private boolean isDebug = BuildConfig.DEBUG;
        private boolean isIgnoreInstalledPlugin = false;

        public Builder setRetryMaxTimes(int retryMaxTimes) {
            if (retryMaxTimes > 0) {
                this.mRetryMaxTimes = retryMaxTimes;
            }
            return this;
        }

        public Builder setPluginDir(@NonNull String pluginDir) {
            this.mPluginDir = pluginDir;
            return this;
        }

        public Builder setOptimizedDexDir(@NonNull String optimizedDexDir) {
            this.mOptimizedDexDir = optimizedDexDir;
            return this;
        }

        public Builder setSoLibraryDir(@NonNull String soLibraryDir) {
            this.mSoLibraryDir = soLibraryDir;
            return this;
        }

        public Builder setTempSoLibraryDir(@NonNull String tempSoLibraryDir) {
            this.mTempSoLibraryDir = tempSoLibraryDir;
            return this;
        }

        public Builder setTempFileSuffix(String tempFileSuffix) {
            this.mTempFileSuffix = tempFileSuffix;
            return this;
        }

        public Builder setPluginName(@NonNull String pluginName) {
            this.mPluginName = pluginName;
            return this;
        }

        public Builder setCustomSignature(String customSignature) {
            this.mCustomSignature = customSignature;
            return this;
        }

        public Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        public Builder setIgnoreInstalledPlugin(boolean isIgnoreInstalledPlugin) {
            this.isIgnoreInstalledPlugin = isIgnoreInstalledPlugin;
            return this;
        }

        public PluginConfiguration build() {
            return new PluginConfiguration(
                    mRetryMaxTimes, mPluginDir, mOptimizedDexDir, mSoLibraryDir, mTempSoLibraryDir, mTempFileSuffix, mPluginName, mCustomSignature, isDebug, isIgnoreInstalledPlugin);
        }
    }
}
