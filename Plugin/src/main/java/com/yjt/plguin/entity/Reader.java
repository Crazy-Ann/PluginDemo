package com.yjt.plguin.entity;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class Reader {

    private InputStream mInputStream;
    private boolean mBigEndian;
    private int mPosition;

    public Reader() { }

    public Reader(InputStream stream, boolean bigEndian) {
        reset(stream, bigEndian);
    }

    public final void reset(InputStream stream, boolean bigEndian) {
        mInputStream = stream;
        mBigEndian = bigEndian;
        mPosition = 0;
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public void setInputStream(InputStream mInputStream) {
        this.mInputStream = mInputStream;
    }

    public boolean ismBigEndian() {
        return mBigEndian;
    }

    public void setBigEndian(boolean mBigEndian) {
        this.mBigEndian = mBigEndian;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public final int readByte() throws IOException {
        return readInt(1);
    }

    public final int readInt() throws IOException {
        return readInt(4);
    }

    public final int[] readIntArray(int length) throws IOException {
        int[] array = new int[length];
        readIntArray(array, 0, length);
        return array;
    }

    public final void readIntArray(int[] array, int offset, int length) throws IOException {
        for (; length > 0; length -= 1) {
            array[offset++] = readInt();
        }
    }

    public final void skip(int bytes) throws IOException {
        if (bytes <= 0) {
            return;
        }
        long skipped = mInputStream.skip(bytes);
        mPosition += skipped;
        if (skipped != bytes) {
            throw new EOFException();
        }
    }

    public final void skipInt() throws IOException {
        skip(4);
    }

    public final int readShort() throws IOException {
        return readInt(2);
    }

    public final int readInt(int length) throws IOException {
        if (length < 0 || length > 4) {
            throw new IllegalArgumentException();
        }
        int result = 0;
        if (mBigEndian) {
            for (int i = (length - 1) * 8; i >= 0; i -= 8) {
                int b = mInputStream.read();
                if (b == -1) {
                    throw new EOFException();
                }
                mPosition += 1;
                result |= (b << i);
            }
        } else {
            length *= 8;
            for (int i = 0; i != length; i += 8) {
                int b = mInputStream.read();
                if (b == -1) {
                    throw new EOFException();
                }
                mPosition += 1;
                result |= (b << i);
            }
        }
        return result;
    }

    public final int available() throws IOException {
        return mInputStream.available();
    }

    public final void close() {
        if (mInputStream == null) {
            return;
        }
        try {
            mInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reset(null, false);
    }
}
