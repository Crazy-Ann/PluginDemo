package com.yjt.engine.entity;

import com.yjt.engine.entity.base.AssetsPluginInfo;
import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.entity.request.SoLibraryRequest;


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
