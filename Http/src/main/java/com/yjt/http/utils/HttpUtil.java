package com.yjt.http.utils;

import com.yjt.constant.Regex;
import com.yjt.http.entity.net.Parameter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class HttpUtil {

    private static HttpUtil mHttpUtil;

    private HttpUtil() {
        // cannot be instantiated
    }

    public static synchronized HttpUtil getInstance() {
        if (mHttpUtil == null) {
            mHttpUtil = new HttpUtil();
        }
        return mHttpUtil;
    }

    public String getCompleteUrl(String url, List<Parameter> parameters, boolean isUrlEncode) {
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        if (builder.indexOf(Regex.QUESTION_MARK.getRegext(), 0) < 0 && parameters.size() > 0) {
            builder.append(Regex.QUESTION_MARK.getRegext());
        }
        int flag = 0;
        for (Parameter parameter : parameters) {
            String key = parameter.getKey();
            String value = parameter.getValue();
            if (isUrlEncode) {
                try {
                    key = URLEncoder.encode(key, Regex.UTF_8.getRegext());
                    value = URLEncoder.encode(value, Regex.UTF_8.getRegext());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            builder.append(key).append(Regex.EQUALS.getRegext()).append(value);
            if (++flag != parameters.size()) {
                builder.append(Regex.AND.getRegext());
            }
        }
        return builder.toString();
    }
}

