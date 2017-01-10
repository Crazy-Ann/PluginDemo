package com.yjt.plguin.entity;

import android.content.Context;

import com.yjt.plguin.entity.base.AssetsPluginInfo;
import com.yjt.plguin.entity.base.PluginBaseInfo;
import com.yjt.plguin.entity.request.SoLibraryRequest;


public class TestAssetsPluginInfo extends AssetsPluginInfo<SoLibraryRequest> {

    @Override
    public String getAssetsPath() {
        return "app.apk";
    }

    @Override
    public int getAssetsVersion() {
        return 1;
    }

    @Override
    public String requestPluginId() {
        return "com.hynet.mergepay.zhongxin";
    }

    @Override
    public PluginBaseInfo createPlugin(String path) {
        return new SoLibraryRequest(path);
    }

    @Override
    public boolean requestClearLocalPlugins(Context context) {
        return true;
    }
}
