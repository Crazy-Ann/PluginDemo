package com.yjt.engine.exception;

public class PluginInstallException extends PluginException {

    public PluginInstallException(String message, int errorCode) {
        super(message, errorCode);
    }

    public PluginInstallException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

    public PluginInstallException(String message, Throwable cause, int errorCode) {
        super(message, cause, errorCode);
    }
}
