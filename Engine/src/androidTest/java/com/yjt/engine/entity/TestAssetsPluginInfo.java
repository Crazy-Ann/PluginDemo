package com.yjt.engine.entity;

import android.content.Context;

import com.yjt.engine.entity.base.AssetsPluginInfo;
import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.entity.request.SoLibraryRequest;


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
