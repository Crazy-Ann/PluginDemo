package com.yjt.plguin.entity.request;

import android.annotation.SuppressLint;
import android.content.Context;

import com.yjt.plguin.entity.base.SoLiabraryPluginInfo;
import com.yjt.plguin.listener.SoLibraryBehaviorListener;
import com.yjt.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SoLibraryRequest extends SoLiabraryPluginInfo<SoLibraryBehaviorListener> {

    public SoLibraryRequest(String externalPath) {
        super(externalPath);
    }

    @Override
    public SoLibraryBehaviorListener createBehavior(Context context) throws Exception {
        return new SoLibraryBehaviorListener() {

            private final byte[] mLock = new byte[0];
            private boolean hasLoaded;

            @Override
            @SuppressLint("UnsafeDynamicallyLoadedCode")
            public void loadSoLibraryy() {
                if (!hasLoaded) {
                    synchronized (mLock){
                        if (!hasLoaded) {
                            for (File item : mSoLiabraries) {
                                LogUtil.getInstance().println("Load share library, path = " + item.getAbsolutePath());
                                System.load(item.getAbsolutePath());
                            }
                            hasLoaded = true;
                            return;
                        }
                    }
                }
                LogUtil.getInstance().println("Libraries have already been loaded once.");
            }

            @Override
            public Set<File> getSoLibrary() {
                return mSoLiabraries;
            }
        };
    }

    @Override
    protected void installSoLib(Context context, File apkFile, File soLibDir) throws IOException {
        super.installSoLib(context, apkFile, soLibDir);
        mSoLiabraries = null;
    }
}
