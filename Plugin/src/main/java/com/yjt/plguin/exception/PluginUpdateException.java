package com.yjt.plguin.exception;

public class PluginUpdateException extends PluginException {

    public PluginUpdateException(String message, int errorCode) {
        super(message, errorCode);
    }

    public PluginUpdateException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

    public PluginUpdateException(String message, Throwable cause, int errorCode) {
        super(message, cause, errorCode);
    }
}
