package com.yjt.engine.exception;

public class PluginLoadException extends PluginException {

    public PluginLoadException(String message, int errorCode) {
        super(message, errorCode);
    }

    public PluginLoadException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

    public PluginLoadException(String message, Throwable cause, int errorCode) {
        super(message, cause, errorCode);
    }
}
