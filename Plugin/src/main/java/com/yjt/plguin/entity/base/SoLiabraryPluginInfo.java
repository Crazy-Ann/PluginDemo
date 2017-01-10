package com.yjt.plguin.entity.base;

import android.content.Context;

import com.yjt.plguin.constant.Constant;
import com.yjt.plguin.exception.PluginLoadException;
import com.yjt.plguin.listener.PluginBehaviorListener;
import com.yjt.plguin.utils.FileUtil;
import com.yjt.plguin.utils.SoLibraryUtil;
import com.yjt.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * so插件信息（请求任务）
 *
 * @param <B>
 */
public abstract class SoLiabraryPluginInfo<B extends PluginBehaviorListener> extends CommonPluginInfo<B> {

    protected Set<File> mSoLiabraries;

    public SoLiabraryPluginInfo(String externalPath) {
        super(externalPath);
        mSoLiabraries = new HashSet<>();
    }

    public Set<File> getSoLiabraries() {
        return mSoLiabraries;
    }

    @Override
    public PluginBaseInfo loadPlugin(Context context, String packagePath) throws PluginLoadException {
        LogUtil.getInstance().println("Install plugin so libs.");
        File apkFile = new File(packagePath);
        checkApkFile(apkFile);
        try {
            mSoLibraryDir = createSoLibraryDir(apkFile);
        } catch (IOException e) {
            throw new PluginLoadException(e, Constant.LoadException.CREATE_SO_LIBRARY_DIR_FAILED);
        }

        try {
            installSoLib(context, apkFile, mSoLibraryDir);
        } catch (IOException e) {
            throw new PluginLoadException(e, Constant.LoadException.INSTALL_SO_LIBRARY_FAILED);
        }

        super.loadPlugin(context, packagePath);
        return this;
    }

    protected File createSoLibraryDir(File apkFile) throws IOException {
        File file = new File(apkFile.getParentFile(), mPluginConfiguration.getSoLibraryDir());
        FileUtil.getInstance().checkCreateDir(file);
        return file;
    }

    protected void installSoLib(Context context, File apkFile, File soLibDir) throws IOException {
        LogUtil.getInstance().println("Install plugin so libs, destDir = " + soLibDir);

        // TODO: 2016/11/30 Optimize so libs installation.
        File tempDir = new File(soLibDir.getParentFile(), mPluginConfiguration.getTempSoLibraryDir());
        FileUtil.getInstance().checkCreateDir(tempDir);
        Set<String> soList = SoLibraryUtil.getInstance().extractSoLibrary(apkFile, tempDir);

        if (soList != null) {
            for (String soName : soList) {
                File soLib = SoLibraryUtil.getInstance().copySoLibrary(context, tempDir, soName, soLibDir);
                if (soLib != null) {
                    mSoLiabraries.add(soLib);
                }
            }
            FileUtil.getInstance().delete(tempDir);
        }
    }
}
