package com.yjt.http.net.response;

import okhttp3.Headers;
import okhttp3.Response;

public class ResponseParameter {

    private boolean isNoResponse;
    private boolean isTimeout;
    private int mResponseCode;
    private String mResponseMessage;
    private String mResponseResult;
    private boolean isSuccess;
    private Headers mHeaders;
    private Response mResponse;

    public boolean isNoResponse() {
        return isNoResponse;
    }

    public void setNoResponse(boolean noResponse) {
        isNoResponse = noResponse;
    }

    public boolean isTimeout() {
        return isTimeout;
    }

    public void setTimeout(boolean timeout) {
        isTimeout = timeout;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(int responseCode) {
        this.mResponseCode = responseCode;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.mResponseMessage = responseMessage;
    }

    public String getResponseResult() {
        return mResponseResult;
    }

    public void setResponseResult(String responseResult) {
        this.mResponseResult = responseResult;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Headers headers) {
        this.mHeaders = headers;
    }

    public Response getResponse() {
        return mResponse;
    }

    public void setResponse(Response response) {
        this.mResponse = response;
    }
}
