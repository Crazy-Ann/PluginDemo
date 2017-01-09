package com.yjt.http.utils;

import com.yjt.http.listener.net.OnUpdateProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class FileUtil {

    private static FileUtil mFileUtil;

    private FileUtil() { }

    public static synchronized FileUtil getInstance() {
        if (mFileUtil == null) {
            mFileUtil = new FileUtil();
        }
        return mFileUtil;
    }

    public static void releaseInstance() {
        if (mFileUtil != null) {
            mFileUtil = null;
        }
    }

    private void forceMkdir(File directory) throws IOException {
        if (directory != null && directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IOException("File " + directory + " exists and is not a directory. Unable to create directory.");
            }
        } else {
            if (directory != null && !directory.mkdirs()) {
                if (!directory.isDirectory()) {
                    throw new IOException("Unable to create directory " + directory);
                }
            }
        }
    }

    public boolean mkdirs(File directory) {
        try {
            forceMkdir(directory);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveFile(InputStream inputStream, File file) {
        BufferedSink bufferedSink = null;
        FileUtil.getInstance().mkdirs(file.getParentFile());
        try {
            bufferedSink = Okio.buffer(Okio.sink(file));
            bufferedSink.writeAll(Okio.source(inputStream));

            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedSink != null) {
                    bufferedSink.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFile(Response response, File file, OnUpdateProgressListener listener) {
        OutputStream outputStream = null;
        try {
            byte[] buffer = new byte[2048];
            int length;
            long temp = 0;
            outputStream = new FileOutputStream(file);
            if (response != null) {
                FileUtil.getInstance().mkdirs(file.getParentFile());
                while ((length = response.body().byteStream().read(buffer)) != -1) {
                    temp += length;
                    outputStream.write(buffer, 0, length);
                    if (listener != null) {
                        listener.updateProgress(temp, response.body().contentLength());
                    }
                }
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
