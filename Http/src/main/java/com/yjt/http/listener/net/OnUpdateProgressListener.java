package com.yjt.http.listener.net;

public interface OnUpdateProgressListener {

    void updateProgress(long temp, long total);

    void updateProgress(int progress, long speed);

    void updateProgress(int progress, long speed, boolean isDone);
}
