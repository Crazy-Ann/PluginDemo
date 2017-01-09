package com.yjt.http.net.request;

import com.yjt.http.listener.net.OnUpdateProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    private long mTime;
    private RequestBody mRequestBody;
    private OnUpdateProgressListener mListener;

    public ProgressRequestBody(RequestBody body, OnUpdateProgressListener listener) {
        this.mRequestBody = body;
        this.mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return mRequestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mTime = System.currentTimeMillis();
        BufferedSink newSink = Okio.buffer(new CountingSink(sink));
        mRequestBody.writeTo(newSink);
        newSink.flush();
    }

    private final class CountingSink extends ForwardingSink {

        private long mWrittenBytes;
        private long mDataLength;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (mDataLength == 0) {
                mDataLength = contentLength();
            }
            mWrittenBytes += byteCount;
            if (mListener != null) {
                long totalTime = (System.currentTimeMillis() - mTime) / 1000;
                if (totalTime == 0) {
                    totalTime += 1;
                }
                long networkSpeed = mWrittenBytes / totalTime;
                int progress = (int) (mWrittenBytes * 100 / mDataLength);
                boolean done = mWrittenBytes == mDataLength;
                mListener.updateProgress(progress, networkSpeed, done);
            }
        }
    }
}
