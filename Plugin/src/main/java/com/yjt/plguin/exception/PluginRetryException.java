package com.yjt.plguin.exception;

import com.yjt.plguin.constant.Constant;

public class PluginRetryException extends PluginException {

    public PluginRetryException() {
        super("Retry has already reached the limit of its number of times.", Constant.LoadException.RETRY_ERROR);
    }
}
