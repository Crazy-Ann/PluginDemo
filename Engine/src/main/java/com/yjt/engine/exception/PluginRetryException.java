package com.yjt.engine.exception;

import com.yjt.engine.constant.Constant;

public class PluginRetryException extends PluginException {

    public PluginRetryException() {
        super("Retry has already reached the limit of its number of times.", Constant.LoadException.RETRY_ERROR);
    }
}
