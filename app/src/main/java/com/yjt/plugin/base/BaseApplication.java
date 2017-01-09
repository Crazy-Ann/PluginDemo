package com.yjt.plugin.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.common.collect.Lists;
import com.yjt.http.constant.Constant;
import com.yjt.http.entity.net.Parameter;
import com.yjt.http.net.Configuration;
import com.yjt.http.net.CustomHttpClient;

import okhttp3.Headers;
import okhttp3.Interceptor;

/**
 * 全局变量
 *
 * @author yjt
 */
public class BaseApplication extends Application /*MultiDexApplication*/ {

    private static BaseApplication mApplication;
    private ClassLoader mLoader;

    public static BaseApplication getInstance() {
        return mApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        CustomHttpClient.getInstance().initialize(new Configuration.Builder()
                                                          .setParameters(Lists.<Parameter>newArrayList())
                                                          .setHeaders(new Headers.Builder().build())
                                                          .setTimeout(Constant.HttpTask.REQUEST_TIME_OUT_PERIOD)
                                                          .setInterceptors(Lists.<Interceptor>newArrayList())
                                                          .setDebug(true).build());
    }

    @Override
    public void onCreate() {
//        StrictModeUtil.getInstance().initialize();
        super.onCreate();
        mApplication = this;
    }
}
