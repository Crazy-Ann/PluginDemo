package com.yjt.http.entity.net;

import android.text.TextUtils;

import com.yjt.constant.Regex;

public final class Parameter {

    private String mKey;
    private String mValue;
    private File mFile;

    public Parameter(String key, String value) {
        setKey(key);
        setValue(value);
    }

    public Parameter(String key, File file) {
        setKey(key);
        this.mFile = file;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    public File getFile() {
        return mFile;
    }

    public void setKey(String key) {
        if (TextUtils.isEmpty(key)) {
            this.mKey = Regex.NONE.getRegext();
        } else {
            this.mKey = key;
        }
    }

    public void setValue(String value) {
        if (TextUtils.isEmpty(value)) {
            this.mValue = Regex.NONE.getRegext();
        } else {
            this.mValue = value;
        }
    }

    public void setFile(File file) {
        this.mFile = file;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Parameter)) {
            Parameter parameter = (Parameter) obj;
            if (TextUtils.equals(parameter.getKey(), getKey()) && TextUtils.equals(parameter.getValue(), getValue())) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}
