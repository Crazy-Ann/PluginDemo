package com.yjt.plguin.exception;

import com.yjt.plguin.BuildConfig;

public class PluginException extends Exception {

    private int mErrorCode;

    public PluginException(String message, int errorCode) {
        super(message);
        this.mErrorCode = errorCode;
    }

    public PluginException(Throwable cause, int errorCode) {
        super(cause);
        this.mErrorCode = errorCode;
    }

    public PluginException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.mErrorCode = errorCode;
    }

    public int getmErrorCode() {
        return mErrorCode;
    }

    @Override
    public String toString() {
        if (BuildConfig.DEBUG) {
            return "PluginException{" + "code = " + mErrorCode + ", message = " + super.toString() + '}';
        } else {
            return null;
        }
    }
}
