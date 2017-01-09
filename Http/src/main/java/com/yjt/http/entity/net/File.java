package com.yjt.http.entity.net;

import android.text.TextUtils;

import com.yjt.constant.Regex;

import okhttp3.MediaType;

public class File {

    public java.io.File mFile;
    public String mFileName;
    public MediaType mType;
    private long mFileSize;

    public File(java.io.File file, MediaType mediaType) {
        this.mFile = file;
        this.mFileName = file.getName();
        this.mType = mediaType;
        this.mFileSize = file.length();
    }

    public String getFileName() {
        if (!TextUtils.isEmpty(mFileName)) {
            return mFileName;
        } else {
            return Regex.NONE.getRegext();
        }
    }

    public java.io.File getFile() {
        return mFile;
    }

    public MediaType getMediaType() {
        return mType;
    }

    public long getFileSize() {
        return mFileSize;
    }
}
