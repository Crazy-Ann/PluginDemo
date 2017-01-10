package com.yjt.plguin.entity;

import com.yjt.plguin.entity.base.AssetsPluginInfo;
import com.yjt.plguin.entity.base.PluginBaseInfo;
import com.yjt.plguin.entity.request.SoLibraryRequest;


public class TestIllegalAssetsPluginInfo extends AssetsPluginInfo<SoLibraryRequest> {

    @Override
    public String getAssetsPath() {
        return null;
    }

    @Override
    public int getAssetsVersion() {
        return 0;
    }

    @Override
    public String requestPluginId() {
        return null;
    }

    @Override
    public PluginBaseInfo createPlugin(String path) {
        return null;
    }
    
}
