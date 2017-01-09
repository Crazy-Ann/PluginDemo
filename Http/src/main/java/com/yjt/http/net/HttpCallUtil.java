package com.yjt.http.net;

import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;

public class HttpCallUtil {

    private static HttpCallUtil mHttpCallUtil;
    private ConcurrentHashMap<String, Call> mCalls;

    private HttpCallUtil() {
        mCalls = new ConcurrentHashMap<>();
    }

    public static synchronized HttpCallUtil getInstance() {
        if (mHttpCallUtil == null) {
            mHttpCallUtil = new HttpCallUtil();
        }
        return mHttpCallUtil;
    }

    public static void releaseInstance() {
        if (mHttpCallUtil != null) {
            mHttpCallUtil = null;
        }
    }

    public void addCall(String url, Call call) {
        if (call != null && !TextUtils.isEmpty(url)) {
            mCalls.put(url, call);
        }
    }

    public Call getCall(String url) {
        if (!TextUtils.isEmpty(url)) {
            return mCalls.get(url);
        }

        return null;
    }

    public void removeCall(String url) {
        if (!TextUtils.isEmpty(url)) {
            mCalls.remove(url);
        }
    }
}
