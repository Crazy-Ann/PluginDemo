package com.yjt.http.net.request;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.yjt.constant.Regex;
import com.yjt.http.entity.net.Parameter;
import com.yjt.http.listener.net.OnHttpRequestTaskListener;
import com.yjt.http.net.CustomHttpClient;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RequestParameter {

    public final Headers.Builder mBuilder = new Headers.Builder();
    private final List<Parameter> mParameters = Lists.newArrayList();
    private final List<Parameter> mFiles = Lists.newArrayList();

    public OnHttpRequestTaskListener mListener;
    private String mHttpTaskKey;
    private RequestBody mRequestBody;
    private boolean isJsonType;
    private JSONObject mJsonObject;
    //    private boolean isFormType;
//    private String mFormData;
    private boolean isUrlEncode = true;
    public CacheControl mCacheControl;

    public RequestParameter() {
        this(null);
    }

    public RequestParameter(OnHttpRequestTaskListener listener) {
        this.mListener = listener;
        initialize();
    }

    public List<Parameter> getParameters() {
        return mParameters;
    }

    public RequestBody getRequestBody() {
        RequestBody body = null;
        if (isJsonType) {
            String json;
            if (mJsonObject == null) {
                JSONObject object = new JSONObject();
                for (Parameter parameter : mParameters) {
                    object.put(parameter.getKey(), parameter.getValue());
                }
                json = object.toJSONString();
            } else {
                json = mJsonObject.toJSONString();
            }
//            body = RequestBody.create(MediaType.parse(Regex.JSON_TYPE.getRegext()), json);
            body = RequestBody.create(MediaType.parse(Regex.DEFAULT_TYPE.getRegext()), json);
        } else if (mRequestBody != null) {
            body = mRequestBody;
        } else if (mFiles != null && mFiles.size() > 0) {
            boolean hasData = false;
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (Parameter parameter : mParameters) {
                String key = parameter.getKey();
                String value = parameter.getValue();
                builder.addFormDataPart(key, value);
                hasData = true;
            }
            for (Parameter parameter : mFiles) {
                String key = parameter.getKey();
                com.yjt.http.entity.net.File file = parameter.getFile();
                if (file != null) {
                    hasData = true;
                    builder.addFormDataPart(key, file.getFileName(), RequestBody.create(file.getMediaType(), file.getFile()));
                }
            }
            if (hasData) {
                body = builder.build();
            }
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Parameter parameter : mParameters) {
                builder.add(parameter.getKey(), parameter.getValue());
            }
            body = builder.build();
        }
        return body;
    }

    public void setRequestBody(RequestBody mRequestBody) {
        this.mRequestBody = mRequestBody;
    }

    public void setRequestBody(MediaType mediaType, String string) {
        setRequestBody(RequestBody.create(mediaType, string));
    }

    public void setRequestBody(String mediaType, String string) {
        setRequestBody(MediaType.parse(mediaType), string);
    }

    public void setRequestBody(String string) {
        setRequestBody(MediaType.parse(Regex.STRING_TYPE.getRegext()), string);
    }

    public boolean isJsonType() {
        return isJsonType;
    }

    public void setJsonType(boolean jsonType) {
        isJsonType = jsonType;
    }

//    public void setFormType(boolean formType) {
//        isFormType = formType;
//    }
//
//    public void setFormData(String formData) {
//        isFormType = true;
//        this.mFormData = formData;
//    }

    public boolean isUrlEncode() {
        return isUrlEncode;
    }

    public void setUrlEncode(boolean urlEncode) {
        isUrlEncode = urlEncode;
    }

    public void setJsonObject(JSONObject object) {
        isJsonType = true;
        this.mJsonObject = object;
    }

    private void initialize() {
        mBuilder.add(Regex.CHARSET.getRegext(), Regex.UTF_8.getRegext());
        List<Parameter> parameters = CustomHttpClient.getInstance().getParameters();
        if (parameters != null && parameters.size() > 0) {
            mParameters.addAll(parameters);
        }

        Headers headers = CustomHttpClient.getInstance().getHeaders();
        if (headers != null && headers.size() > 0) {
            for (int i = 0; i < headers.size(); i++) {
                mBuilder.add(headers.name(i), headers.value(i));
            }
        }

        if (mListener != null) {
            mHttpTaskKey = mListener.getHttpTaskKey();
        }
    }

    public String getHttpTaskKey() {
        return mHttpTaskKey;
    }

    /********************** Header *************************/

    public void addHeader(String line) {
        mBuilder.add(line);
    }

    public void addHeader(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            value = Regex.NONE.getRegext();
        }
        if (!TextUtils.isEmpty(key)) {
            mBuilder.add(key, value);
        }
    }

    public void addHeader(String key, int value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, long value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, float value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, double value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, boolean value) {
        addHeader(key, String.valueOf(value));
    }

    /********************** Parameter *************************/

    public void addFormDataParameter(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            value = Regex.NONE.getRegext();
        }
        Parameter part = new Parameter(key, value);
        if (!TextUtils.isEmpty(key) && !mParameters.contains(part)) {
            mParameters.add(part);
        }
    }

    public void addFormDataParameter(String key, long value) {
        addFormDataParameter(key, String.valueOf(value));
    }

    public void addFormDataParameter(String key, float value) {
        addFormDataParameter(key, String.valueOf(value));
    }

    public void addFormDataParameter(String key, double value) {
        addFormDataParameter(key, String.valueOf(value));
    }

    public void addFormDataParameter(String key, boolean value) {
        addFormDataParameter(key, String.valueOf(value));
    }

    public void addFormDataParameter(String key, File file, String contentType) {
        if (isFileAvalable(file)) {
            MediaType mediaType = MediaType.parse(contentType);
            addFormDataParameter(key, new com.yjt.http.entity.net.File(file, mediaType));
        }
    }

    public void addFormDataParameter(String key, File file, MediaType mediaType) {
        if (isFileAvalable(file)) {
            addFormDataParameter(key, new com.yjt.http.entity.net.File(file, mediaType));
        }
    }


    public void addFormDataParameter(String key, com.yjt.http.entity.net.File file) {
        if (!TextUtils.isEmpty(key) && file != null) {
            if (isFileAvalable(file.getFile())) {
                mFiles.add(new Parameter(key, file));
            }
        }
    }

    public void addFormDataParameter(String key, File file) {
        if (isFileAvalable(file)) {
            boolean isPng = file.getName().toLowerCase().lastIndexOf(Regex.PNG.getRegext()) > 0;
            if (isPng) {
                addFormDataParameter(key, file, Regex.PNG_TYPE.getRegext());
                return;
            }
            boolean isJpg = file.getName().toLowerCase().lastIndexOf(Regex.JPG.getRegext()) > 0 || file.getName().toLowerCase().lastIndexOf(Regex.JPEG.getRegext()) > 0;
            if (isJpg) {
                addFormDataParameter(key, file, Regex.JPEG_TYPE.getRegext());
                return;
            }

            if (!isPng && !isJpg) {
                addFormDataParameter(key, new com.yjt.http.entity.net.File(file, null));
            }
        }
    }

    public void addFormDataParameter1(String key, List<File> files) {
        for (File file : files) {
            if (isFileAvalable(file)) {
                addFormDataParameter(key, file);
            }
        }
    }

    public void addFormDataParameter2(String key, List<com.yjt.http.entity.net.File> files) {
        for (com.yjt.http.entity.net.File file : files) {
            addFormDataParameter(key, file);
        }
    }

    public void addFormDataParameter(String key, List<File> files, MediaType mediaType) {
        for (File file : files) {
            if (isFileAvalable(file)) {
                addFormDataParameter(key, new com.yjt.http.entity.net.File(file, mediaType));
            }
        }
    }

    public void addFormDataParameters(List<Parameter> parameters) {
        this.mParameters.addAll(parameters);
    }

    private boolean isFileAvalable(File file) {
        return !(file == null || !file.exists() || file.length() == 0);
    }

    @Override
    public String toString() {
//        if (BuildConfig.DEBUG) {
        StringBuilder result = new StringBuilder();
        for (Parameter parameter : mParameters) {
            String key = parameter.getKey();
            String value = parameter.getValue();
            if (result.length() > 0) {
                result.append(Regex.AND.getRegext());
            }
            result.append(key);
            result.append(Regex.EQUALS.getRegext());
            result.append(value);
        }
        for (Parameter parameter : mFiles) {
            String key = parameter.getKey();
            if (result.length() > 0) {
                result.append(Regex.AND.getRegext());
            }
            result.append(key);
            result.append(Regex.EQUALS.getRegext());
            result.append("FILE");
        }
        if (mJsonObject != null) {
            result.append(mJsonObject.toJSONString());
        }
        return result.toString();
//        } else {
//            return super.toString();
//        }
    }
}
