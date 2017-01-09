package com.yjt.http.net.response;

import okhttp3.Headers;
import okhttp3.Response;

public class DownloadResponse extends HttpResponse {

    @Override
    public void onStart() {

    }

    @Override
    public void onProgress(int progress, long speed) {

    }

    @Override
    public void onProgress(int progress, long speed, boolean isDone) {

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onResponse(Response httpResponse, String response, Headers headers) {

    }

    @Override
    public void onResponse(String response, Headers headers) {

    }

    @Override
    public void onSuccess(Headers headers, Object o) {

    }

    @Override
    public void onSuccess(Object o) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailed(int code, String message) {

    }

    @Override
    public void onFailed() {

    }
}
