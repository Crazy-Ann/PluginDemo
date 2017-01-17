package com.yjt.engine.entity;

import com.yjt.engine.constant.Constant;
import com.yjt.engine.listener.implement.CustomXmlResourceParser;

import java.io.IOException;

/**
 * @author Dmitry Skiba Block of strings, used in binary xml and arsc.
 */
public class StringBlock {

    private int[] mStringOffsets;
    private int[] mStrings;
    private int[] mStyleOffsets;
    private int[] mStyles;

    private static StringBlock mInstance;

    private StringBlock() {
        // cannot be instantiated
    }

    public static synchronized StringBlock getInstance() {
        if (mInstance == null) {
            mInstance = new StringBlock();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public StringBlock read(Reader reader) throws IOException {
        CustomXmlResourceParser.getInstance().readCheckType(reader, Constant.Manifest.CHUNK_TYPE);
        int chunkSize = reader.readInt();
        int stringCount = reader.readInt();
        int styleOffsetCount = reader.readInt();
        reader.readInt();
        int stringsOffset = reader.readInt();
        int stylesOffset = reader.readInt();

        mStringOffsets = reader.readIntArray(stringCount);
        if (styleOffsetCount != 0) {
            mStyleOffsets = reader.readIntArray(styleOffsetCount);
        }
        {
            int size = ((stylesOffset == 0) ? chunkSize : stylesOffset) - stringsOffset;
            if ((size % 4) != 0) {
                throw new IOException("String data size is not multiple of 4 (" + size + ").");
            }
            mStrings = reader.readIntArray(size / 4);
        }
        if (stylesOffset != 0) {
            int size = (chunkSize - stylesOffset);
            if ((size % 4) != 0) {
                throw new IOException("Style data size is not multiple of 4 (" + size + ").");
            }
            mStyles = reader.readIntArray(size / 4);
        }
        return mInstance;
    }

    public int getCount() {
        return mStringOffsets != null ? mStringOffsets.length : 0;
    }

    public String getString(int index) {
        if (index < 0 || mStringOffsets == null || index >= mStringOffsets.length) {
            return null;
        }
        int offset = mStringOffsets[index];
        int length = getShort(mStrings, offset);
        StringBuilder result = new StringBuilder(length);
        for (; length != 0; length -= 1) {
            offset += 2;
            result.append((char) getShort(mStrings, offset));
        }
        return result.toString();
    }

    public CharSequence get(int index) {
        return getString(index);
    }

    public String getHTML(int index) {
        String raw = getString(index);
        if (raw == null) {
            return raw;
        }
        int[] style = getStyle(index);
        if (style == null) {
            return raw;
        }
        StringBuilder html = new StringBuilder(raw.length() + 32);
        int offset = 0;
        while (true) {
            int i = -1;
            for (int j = 0; j != style.length; j += 3) {
                if (style[j + 1] == -1) {
                    continue;
                }
                if (i == -1 || style[i + 1] > style[j + 1]) {
                    i = j;
                }
            }
            int start = ((i != -1) ? style[i + 1] : raw.length());
            for (int j = 0; j != style.length; j += 3) {
                int end = style[j + 2];
                if (end == -1 || end >= start) {
                    continue;
                }
                if (offset <= end) {
                    html.append(raw, offset, end + 1);
                    offset = end + 1;
                }
                style[j + 2] = -1;
                html.append('<');
                html.append('/');
                html.append(getString(style[j]));
                html.append('>');
            }
            if (offset < start) {
                html.append(raw, offset, start);
                offset = start;
            }
            if (i == -1) {
                break;
            }
            html.append('<');
            html.append(getString(style[i]));
            html.append('>');
            style[i + 1] = -1;
        }
        return html.toString();
    }

    public int find(String string) {
        if (string == null) {
            return -1;
        }
        for (int i = 0; i != mStringOffsets.length; ++i) {
            int offset = mStringOffsets[i];
            int length = getShort(mStrings, offset);
            if (length != string.length()) {
                continue;
            }
            int j = 0;
            for (; j != length; ++j) {
                offset += 2;
                if (string.charAt(j) != getShort(mStrings, offset)) {
                    break;
                }
            }
            if (j == length) {
                return i;
            }
        }
        return -1;
    }

    private int[] getStyle(int index) {
        if (mStyleOffsets == null || mStyles == null || index >= mStyleOffsets.length) {
            return null;
        }
        int offset = mStyleOffsets[index] / 4;
        int style[];
        {
            int count = 0;
            for (int i = offset; i < mStyles.length; ++i) {
                if (mStyles[i] == -1) {
                    break;
                }
                count += 1;
            }
            if (count == 0 || (count % 3) != 0) {
                return null;
            }
            style = new int[count];
        }
        for (int i = offset, j = 0; i < mStyles.length; ) {
            if (mStyles[i] == -1) {
                break;
            }
            style[j++] = mStyles[i++];
        }
        return style;
    }

    private static final int getShort(int[] array, int offset) {
        int value = array[offset / 4];
        if ((offset % 4) / 2 == 0) {
            return (value & 0xFFFF);
        } else {
            return (value >>> 16);
        }
    }
}
