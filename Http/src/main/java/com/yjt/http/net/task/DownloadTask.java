package com.yjt.http.net.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.yjt.http.constant.ResponseCode;
import com.yjt.http.listener.net.OnUpdateProgressListener;
import com.yjt.http.net.CustomHttpClient;
import com.yjt.http.net.response.DownloadResponse;
import com.yjt.http.utils.FileUtil;
import com.tencent.mars.xlog.XLog;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadTask extends AsyncTask<Void, Long, Boolean> implements OnUpdateProgressListener {

    private long mPreviousTime;
    private String mUrl;
    private File mFile;
    private DownloadResponse mResponse;
    private OkHttpClient mClient;

    public DownloadTask(String url, File file, DownloadResponse response) {
        this.mUrl = url;
        this.mFile = file;
        this.mResponse = response;
        this.mClient = CustomHttpClient.getInstance().getHttpClientBuilder().build();
        FileUtil.getInstance().mkdirs(file.getParentFile());
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPreviousTime = System.currentTimeMillis();
        if (mResponse != null) {
            mResponse.onStart();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean isSuccess = false;
        XLog.getInstance().println("---->mUrl:" + mUrl);
        XLog.getInstance().println("---->mFile:" + mFile);
        try {
            if (!TextUtils.isEmpty(mUrl) && mFile != null) {
                Response response = mClient.newCall(new Request.Builder().url(mUrl).build()).execute();
//                FileUtil.getInstance().saveFile(response.body().byteStream(), mFile);
                FileUtil.getInstance().saveFile(response, mFile, this);
                if (response.body().contentLength() == mFile.length()) {
                    isSuccess = true;
                }
                XLog.getInstance().println("---->body:" + response.body().contentLength());
                XLog.getInstance().println("---->mFile:" + mFile.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (mResponse != null && values != null && values.length >= 2) {
            long currentTime = (System.currentTimeMillis() - mPreviousTime) / 1000;
            if (currentTime == 0) {
                currentTime += 1;
            }
            mResponse.onProgress((int) (values[0] * 100 / values[1]), values[0] / currentTime);
        }
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mResponse != null) {
            mResponse.onEnd();
            if (isSuccess) {
                mResponse.onSuccess();
            } else {
                mResponse.onFailed(Integer.parseInt(ResponseCode.RESPONSE_CODE_1000.getContent()), ResponseCode.RESPONSE_MESSAGE_1000.getContent());
            }
        }
    }

    @Override
    public void updateProgress(long temp, long total) {
        publishProgress(temp, total);
    }

    @Override
    public void updateProgress(int progress, long speed) {

    }

    @Override
    public void updateProgress(int progress, long speed, boolean isDone) {

    }
}
