package com.yjt.http.constant;

public enum ResponseCode {

    RESPONSE_CODE_1000("1000"),
    RESPONSE_MESSAGE_1000("下载失败"),
    RESPONSE_CODE_DATA_PARSE_ERROR("9998"),
    RESPONSE_MESSAGE_DATA_PARSE_ERROR("数据解析异常"),
    RESPONSE_CODE_UNKNOWN("9999"),
    RESPONSE_MESSAGE_UNKNOWN("服务器繁忙"),//未知错误
    RESPONSE_MESSAGE_TIME_OUT("服务器连接超时");

    private String mContent;

    ResponseCode(String content) {
        this.mContent = content;
    }

    public String getContent() {
        return mContent;
    }

}
