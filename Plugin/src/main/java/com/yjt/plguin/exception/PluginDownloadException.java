package com.yjt.plguin.exception;

public class PluginDownloadException extends PluginException {

    public PluginDownloadException(String message, int errorCode) {
        super(message, errorCode);
    }

    public PluginDownloadException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

    public PluginDownloadException(String message, Throwable cause, int errorCode) {
        super(message, cause, errorCode);
    }
}
