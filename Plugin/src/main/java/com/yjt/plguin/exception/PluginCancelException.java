package com.yjt.plguin.exception;

public class PluginCancelException extends PluginException {

    public PluginCancelException(int errorCode) {
        super("Operation was canceled.", errorCode);
    }

    public PluginCancelException(Throwable cause, int errorCode) {
        super(cause, errorCode);
    }

}
