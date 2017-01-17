package com.yjt.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yjt.engine.callback.PluginCallback;
import com.yjt.engine.callback.PluginCallbackHandler;
import com.yjt.engine.config.PluginConfiguration;
import com.yjt.engine.entity.base.PluginBaseInfo;
import com.yjt.engine.entity.base.PluginExtraInfo;
import com.yjt.engine.entity.TaskState;
import com.yjt.engine.exception.PluginLoadException;
import com.yjt.engine.listener.PluginBehaviorListener;
import com.yjt.engine.listener.PluginInstallListener;
import com.yjt.engine.listener.PluginListener;
import com.yjt.engine.listener.PluginLoadListener;
import com.yjt.engine.listener.PluginUpdateListener;
import com.yjt.engine.listener.implement.PluginInstaller;
import com.yjt.engine.listener.implement.PluginLoader;
import com.yjt.engine.listener.implement.PluginUpdater;
import com.yjt.engine.task.PluginTask;
import com.yjt.engine.utils.CompatUtil;
import com.yjt.engine.utils.FileUtil;
import com.yjt.utils.LogUtil;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Plugin extends PluginWrapper {

    private static Plugin mInstance;

    private boolean hasIntialized;
    private final byte[] mLock = new byte[0];

    private PluginWrapper mPluginWrapper;
    private PluginCallback mPluginCallback;
    private ExecutorService mExecutorService;
    private Map<Class<? extends PluginExtraInfo>, TaskState> mTaskStates;

    private Plugin() {
        // cannot be instantiated
        super(null, null, null, null, null);
    }

    public static synchronized Plugin getInstance() {
        if (mInstance == null) {
            mInstance = new Plugin();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public void initialize(Context context) {
        if (!hasIntialized) {
            synchronized (mLock){
                if (!hasIntialized) {
                    hasIntialized = true;
                    PluginConfiguration configuration = new PluginConfiguration.Builder()
                            .setDebug(BuildConfig.DEBUG)
                            .setIgnoreInstalledPlugin(BuildConfig.DEBUG)
                            .build();
                    PluginLoadListener pluginLoadListener = new PluginLoader(context);
                    PluginUpdateListener pluginUpdateListener = new PluginUpdater(context);
                    PluginInstallListener pluginInstallListener = new PluginInstaller(context, configuration);
                    mPluginCallback = new PluginCallbackHandler(new Handler(Looper.getMainLooper()));
                    mExecutorService = Executors.newSingleThreadExecutor();
                    mPluginWrapper = new PluginWrapper(pluginLoadListener, pluginUpdateListener, pluginInstallListener, configuration, new PluginCallback());
                    printDebugInfo();
                    return;
                }
            }
        }
        throw new RuntimeException("Plugin has already been initialized.");
    }

    public void initialize(Context context, @NonNull PluginConfiguration configuration) {
        if (!hasIntialized) {
            synchronized (mLock){
                if (!hasIntialized) {
                    hasIntialized = true;
                    PluginLoadListener pluginLoadListener = new PluginLoader(context);
                    PluginUpdateListener pluginUpdateListener = new PluginUpdater(context);
                    PluginInstallListener pluginInstallListener = new PluginInstaller(context, configuration);
                    mPluginCallback = new PluginCallbackHandler(new Handler(Looper.getMainLooper()));
                    mExecutorService = Executors.newSingleThreadExecutor();
                    mPluginWrapper = new PluginWrapper(pluginLoadListener, pluginUpdateListener, pluginInstallListener, configuration, new PluginCallback());
                    printDebugInfo();
                    return;
                }
            }
        }
        throw new RuntimeException("Plugin has already been initialized.");
    }

    public void initialize(Context context, @NonNull PluginConfiguration configuration, @NonNull Handler callbackHandler, @NonNull ExecutorService executorService) {
        if (!hasIntialized) {
            synchronized (mLock){
                if (!hasIntialized) {
                    hasIntialized = true;
                    PluginLoadListener pluginLoadListener = new PluginLoader(context);
                    PluginUpdateListener pluginUpdateListener = new PluginUpdater(context);
                    PluginInstallListener pluginInstallListener = new PluginInstaller(context, configuration);
                    mPluginCallback = new PluginCallbackHandler(callbackHandler);
                    mExecutorService = executorService;
                    mPluginWrapper = new PluginWrapper(pluginLoadListener, pluginUpdateListener, pluginInstallListener, configuration, new PluginCallback());
                    printDebugInfo();
                    return;
                }
            }
        }
        throw new RuntimeException("Plugin has already been initialized.");
    }

    public PluginExtraInfo add(@NonNull PluginExtraInfo pluginExtraInfo, int mode) {
        return add(pluginExtraInfo, PluginTask.doing(mPluginWrapper, mode));
    }

    public PluginExtraInfo add(@NonNull PluginExtraInfo pluginExtraInfo, @NonNull PluginTask task) {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        PluginListener pluginListener = pluginExtraInfo.getPluginListener();
        return mPluginWrapper.add(pluginExtraInfo.attach(pluginListener == null ? mPluginWrapper : pluginListener), task);
    }

    public TaskState addAsync(@NonNull PluginExtraInfo pluginExtraInfo, int mode) {
        return addAsync(pluginExtraInfo, PluginTask.doing(this, mode));
    }

    public TaskState addAsync(@NonNull final PluginExtraInfo pluginExtraInfo, @NonNull final PluginTask task) {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }

        if (mTaskStates == null || mTaskStates == Collections.EMPTY_MAP) {
            mTaskStates = new HashMap();
        }
        TaskState taskState = mTaskStates.get(pluginExtraInfo.getClass());

        // Cancel if exist.
        if (taskState != null) {
            taskState.cancel();
        }

        pluginExtraInfo.attach(this);
        Future<PluginExtraInfo> future = mExecutorService.submit(new Callable<PluginExtraInfo>() {
            @Override
            public PluginExtraInfo call() throws Exception {
                return add(pluginExtraInfo, task);
            }
        });

        taskState = new TaskState(pluginExtraInfo, future);
        mTaskStates.put(pluginExtraInfo.getClass(), taskState);
        return taskState;
    }

    public PluginWrapper getPluginWrapper() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper;
    }

    @Nullable
    public TaskState getRequestState(Class<? extends PluginExtraInfo> clazz) {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mTaskStates == null || mTaskStates == Collections.EMPTY_MAP ? null : mTaskStates.get(clazz);
    }

    private void printDebugInfo() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }

        if (BuildConfig.DEBUG) {
            LogUtil.getInstance().println("Plugin init");
            LogUtil.getInstance().println("Debug Mode = " + mPluginWrapper.getPluginConfiguration().isDebug());
            LogUtil.getInstance().println("Ignore Installed Plugin = " + mPluginWrapper.getPluginConfiguration().isIgnoreInstalledPlugin());
            LogUtil.getInstance().println("Use custom signature = " + mPluginWrapper.getPluginConfiguration().useCustomSignature());
            FileUtil.getInstance().dumpFiles(new File(mPluginWrapper.getPluginInstallListener().getPluginDir()));
        }
    }

    @Override
    public Class getClass(Class<? extends PluginBaseInfo> clazz, String className) throws PluginLoadException {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper.getClass(clazz, className);
    }

    @Override
    public <B extends PluginBehaviorListener, P extends PluginBaseInfo<B>> B getBehavior(P clazz) throws PluginLoadException {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper.getBehavior(clazz);
    }

    @Override
    public <B extends PluginBehaviorListener, P extends PluginBaseInfo<B>> P getPlugin(P clazz) {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper.getPlugin(clazz);
    }

    @Override
    public void addLoadedPlugin(Class<? extends PluginBehaviorListener> clazz, PluginBaseInfo pluginBaseInfo) {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        mPluginWrapper.addLoadedPlugin(clazz, pluginBaseInfo);
    }

    @Override
    public PluginConfiguration getPluginConfiguration() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper.getPluginConfiguration();
    }

    @Override
    public PluginLoadListener getPluginLoadListener() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper.getPluginLoadListener();
    }

    @Override
    public PluginUpdateListener getPluginUpdateListener() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper.getPluginUpdateListener();
    }

    @Override
    public PluginInstallListener getPluginInstallListener() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginWrapper.getPluginInstallListener();
    }

    @Override
    public PluginCallback getPluginCallback() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mPluginCallback;
    }

    public ExecutorService getExecutorService() {
        if (!hasIntialized) {
            throw new RuntimeException("Plugin has not yet been initialize.");
        }
        return mExecutorService;
    }

    public void registerLibrary(String name, int version) {
        CompatUtil.getInstance().registerLibrary(name, version);
    }
}
