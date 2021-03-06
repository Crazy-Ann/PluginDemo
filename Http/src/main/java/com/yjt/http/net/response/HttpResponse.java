package com.yjt.http.net.response;

import com.yjt.utils.ReflectUtil;

import java.lang.reflect.Type;

import okhttp3.Headers;
import okhttp3.Response;

public abstract class HttpResponse<T> {

    public Type mType;
    public Headers mHeaders;

    public HttpResponse() {
        mType = ReflectUtil.getInstance().getGenericSuperclassType(getClass());
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Headers headers) {
        this.mHeaders = headers;
    }

    public abstract void onStart();

    public abstract void onProgress(int progress, long speed);

    public abstract void onProgress(int progress, long speed, boolean isDone);

    public abstract void onEnd();

    public abstract void onResponse(Response httpResponse, String response, Headers headers);

    public abstract void onResponse(String response, Headers headers);

    public abstract void onSuccess(Headers headers, T t);

    public abstract void onSuccess(T t);

    public abstract void onSuccess();

    public abstract void onFailed(int code, String message);

    public abstract void onFailed();
}
