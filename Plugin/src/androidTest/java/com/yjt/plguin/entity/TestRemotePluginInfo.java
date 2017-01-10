package com.yjt.plguin.entity;

import android.content.Context;
import android.support.annotation.Nullable;

import com.yjt.plguin.entity.base.PluginBaseInfo;
import com.yjt.plguin.entity.base.PluginExtraInfo;
import com.yjt.plguin.entity.request.SoLibraryRequest;

import java.util.ArrayList;
import java.util.List;

public class TestRemotePluginInfo extends PluginExtraInfo<SoLibraryRequest> {

    @Override
    public List<? extends RemotePluginInfo> requestRemotePluginInfo(Context context) throws Exception {
        List<RemotePluginInfo> infos = new ArrayList<>();

        RemotePluginInfo info = new RemotePluginInfo();
        info.downloadUrl = "www.baidu.com";
        info.isValid = true;
        info.fileSize = 65536;
        info.isForceUpdate = true;
        info.minAppBuild = 0;
        info.pluginId = "com.hynet.mergepay.zhongxin";
        info.version = 1;
        infos.add(info);

        return infos;
    }

    @Nullable
    @Override
    public String getPluginId() {
        return "com.hynet.mergepay.zhongxin";
    }

    @Override
    public String requestPluginId() {
        return "com.hynet.mergepay.zhongxin";
    }

    @Override
    public boolean requestClearLocalPlugins(Context context) {
        return true;
    }

    @Override
    public PluginBaseInfo createPlugin(String path) {
        return new SoLibraryRequest(path);
    }
}
