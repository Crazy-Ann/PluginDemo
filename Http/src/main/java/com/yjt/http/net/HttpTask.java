package com.yjt.http.net;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yjt.http.constant.Constant;
import com.yjt.http.constant.HttpRequestType;
import com.yjt.http.constant.ResponseCode;
import com.yjt.http.listener.net.OnUpdateProgressListener;
import com.yjt.http.net.request.ProgressRequestBody;
import com.yjt.http.net.request.RequestParameter;
import com.yjt.http.net.response.HttpResponse;
import com.yjt.http.net.response.ResponseParameter;
import com.yjt.http.utils.HttpUtil;
import com.yjt.http.utils.JsonFormatUtil;
import com.yjt.utils.LogUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpTask implements Callback, OnUpdateProgressListener {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private HttpRequestType mType;
    private String mUrl;
    private RequestParameter mParameter;
    private OkHttpClient mClient;
    private HttpResponse mResponse;
    private Headers mHeaders;
    private String mHttpTaskKey;

    public HttpTask(HttpRequestType type, String url, RequestParameter parameter, OkHttpClient.Builder builder, HttpResponse response) {
        this.mType = type;
        this.mUrl = url;
        if (parameter == null) {
            this.mParameter = new RequestParameter();
        } else {
            this.mParameter = parameter;
        }
        this.mClient = builder.build();
        this.mResponse = response;
        this.mHttpTaskKey = mParameter.getHttpTaskKey();
        if (TextUtils.isEmpty(mHttpTaskKey)) {
            mHttpTaskKey = Constant.HttpTask.DEFAULT_TASK_KEY;
        }
        HttpTaskUtil.getInstance().addTask(mHttpTaskKey, this);
    }

    public void execute() {
        LogUtil.getInstance().println("---->execute invoked!!");
        if (mParameter.mBuilder != null) {
            mHeaders = mParameter.mBuilder.build();
        }
        if (mResponse != null) {
            mResponse.onStart();
        }
        enqueue();
    }

    private void enqueue() {
        LogUtil.getInstance().println("---->enqueue invoked!!");
        String original = mUrl;
        Request.Builder builder = new Request.Builder();
        switch (mType) {
            case GET:
                mUrl = HttpUtil.getInstance().getCompleteUrl(mUrl, mParameter.getParameters(), mParameter.isUrlEncode());
                builder.get();
                break;
            case POST:
                RequestBody post = mParameter.getRequestBody();
                if (post != null) {
                    builder.post(new ProgressRequestBody(post, this));
                }
                break;
            case PUT:
                RequestBody put = mParameter.getRequestBody();
                if (put != null) {
                    builder.put(new ProgressRequestBody(put, this));
                }
                break;
            case DELETE:
                mUrl = HttpUtil.getInstance().getCompleteUrl(mUrl, mParameter.getParameters(), mParameter.isUrlEncode());
                builder.delete();
                break;
            case HEAD:
                mUrl = HttpUtil.getInstance().getCompleteUrl(mUrl, mParameter.getParameters(), mParameter.isUrlEncode());
                builder.head();
                break;
            case PATCH:
                RequestBody bodyPatch = mParameter.getRequestBody();    
                if (bodyPatch != null) {
                    builder.put(new ProgressRequestBody(bodyPatch, this));
                }
                break;
            default:
                break;
        }
        if (mParameter.mCacheControl != null) {
            builder.cacheControl(mParameter.mCacheControl);
        }
        builder.url(mUrl).tag(original).headers(mHeaders);
        Request request = builder.build();
        LogUtil.getInstance().println("---->original:" + original);
        LogUtil.getInstance().println("---->mUrl:" + mUrl);
        LogUtil.getInstance().println("---->mParameter:" + mParameter.toString());
        LogUtil.getInstance().println("---->header:" + mHeaders.toString());
        Call call = mClient.newCall(request);
        HttpCallUtil.getInstance().addCall(mUrl, call);
        call.enqueue(this);
    }

    @Override
    public void updateProgress(long temp, long total) {
        
    }

    @Override
    public void updateProgress(final int progress, final long speed) {
        LogUtil.getInstance().println("---->updateProgress invoked!!");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mResponse != null) {
                    mResponse.onProgress(progress, speed);
                }
            }
        });
    }

    @Override
    public void updateProgress(final int progress, final long speed, final boolean isDone) {
        LogUtil.getInstance().println("---->updateProgress invoked!!");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mResponse != null) {
                    mResponse.onProgress(progress, speed, isDone);
                }
            }
        });
    }

    @Override
    public void onFailure(Call call, IOException exception) {
        LogUtil.getInstance().println("---->onFailure invoked!!");
        ResponseParameter parameter = new ResponseParameter();
        if (exception instanceof SocketTimeoutException) {
            parameter.setTimeout(true);
        } else if (exception instanceof InterruptedIOException && TextUtils.equals(exception.getMessage(), Constant.HttpTask.TIME_OUT)) {
            parameter.setTimeout(true);
        }
        handleResponse(parameter, null);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        LogUtil.getInstance().println("---->onResponse invoked!!");
        handleResponse(new ResponseParameter(), response);
    }

    private void handleResponse(final ResponseParameter parameter, Response response) {
        LogUtil.getInstance().println("---->handleResponse invoked!!");
        if (response != null) {
            try {
                parameter.setNoResponse(false);
                parameter.setResponseCode(response.code());
                parameter.setResponseMessage(response.message());
                parameter.setSuccess(response.isSuccessful());
                parameter.setResponseResult(response.body().string());
                parameter.setHeaders(response.headers());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            parameter.setNoResponse(true);
            parameter.setResponseCode(Integer.parseInt(ResponseCode.RESPONSE_CODE_UNKNOWN.getContent()));
            if (parameter.isTimeout()) {
                parameter.setResponseMessage(ResponseCode.RESPONSE_MESSAGE_TIME_OUT.getContent());
            } else {
                parameter.setResponseMessage(ResponseCode.RESPONSE_MESSAGE_UNKNOWN.getContent());
            }
        }
        parameter.setResponse(response);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onPostExecute(parameter);
            }
        });
    }

    protected void onPostExecute(ResponseParameter parameter) {
        LogUtil.getInstance().println("---->onPostExecute invoked!!");
        HttpCallUtil.getInstance().removeCall(mUrl);
        if (!HttpTaskUtil.getInstance().contains(mHttpTaskKey)) {
            return;
        }

        if (mResponse != null) {
            mResponse.setHeaders(parameter.getHeaders());
            mResponse.onResponse(parameter.getResponse(), parameter.getResponseResult(), parameter.getHeaders());
            mResponse.onResponse(parameter.getResponseResult(), parameter.getHeaders());
        }
        int code = parameter.getResponseCode();
        String messge = parameter.getResponseMessage();
        LogUtil.getInstance().println("---->code:" + code + ",messge:" + messge + ",parameter:" + parameter.isNoResponse());
        if (!parameter.isNoResponse()) {
            if (parameter.isSuccess()) {
                LogUtil.getInstance().println("---->url=" + mUrl + ",result=" + JsonFormatUtil.formatJson(parameter.getResponseResult()) + ",headers=" + parameter.getHeaders().toString());
                parseResponseBody(parameter, mResponse);
            } else {
                LogUtil.getInstance().println("---->url=" + mUrl + ",response failure code=" + code + ",messge=" + messge);
                if (mResponse != null) {
                    mResponse.onFailed(code, messge);
                }
            }
        } else {
            LogUtil.getInstance().println("---->url=" + mUrl + "\n response failure code=" + code + " msg=" + messge);
            if (mResponse != null) {
                mResponse.onFailed(code, messge);
            }
        }
        if (mResponse != null) {
            mResponse.onEnd();
        }
    }

    private void parseResponseBody(ResponseParameter parameter, HttpResponse response) {
        LogUtil.getInstance().println("---->parseResponseBody invoked!!");
        if (response == null) {
            return;
        }
        String result = parameter.getResponseResult();
        LogUtil.getInstance().println("---->result:" + result);
        LogUtil.getInstance().println("---->mType:" + response.mType);
        if (response.mType == String.class) {
            response.onSuccess(parameter.getHeaders(), result);
            response.onSuccess(result);
            return;
        } else if (response.mType == JSONObject.class) {
            JSONObject object = JSON.parseObject(result);
            if (object != null) {
                response.onSuccess(parameter.getHeaders(), object);
                response.onSuccess(object);
                return;
            }
        } else if (mResponse.mType == JSONArray.class) {
            JSONArray array = JSON.parseArray(result);
            if (array != null) {
                response.onSuccess(parameter.getHeaders(), array);
                response.onSuccess(array);
                return;
            }
        } else {
            Object object = JSON.parseObject(result, mResponse.mType);
            if (object != null) {
                response.onSuccess(parameter.getHeaders(), object);
                response.onSuccess(object);
                return;
            }
        }
        response.onFailed(Integer.parseInt(ResponseCode.RESPONSE_CODE_DATA_PARSE_ERROR.getContent()), ResponseCode.RESPONSE_MESSAGE_DATA_PARSE_ERROR.getContent());
    }
}
