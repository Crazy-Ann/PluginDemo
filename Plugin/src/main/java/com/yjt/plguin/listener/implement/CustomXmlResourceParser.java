package com.yjt.plguin.listener.implement;


import android.content.res.XmlResourceParser;
import android.util.TypedValue;

import com.yjt.plguin.constant.Constant;
import com.yjt.plguin.entity.Reader;
import com.yjt.plguin.entity.NamespaceStack;
import com.yjt.plguin.entity.StringBlock;
import com.yjt.utils.LogUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Dmitry Skiba Binary xml files parser. Parser has only two states: (1) Operational state, which parser obtains
 *         after first successful call to next() and retains until open(), close(), or failed call to next(). (2) Closed state,
 *         which parser obtains after open(), close(), or failed call to next(). In this state methods return invalid values or
 *         throw exceptions.
 */
public class CustomXmlResourceParser implements XmlResourceParser {

    private Reader mReader;
    private boolean isCloseable = false;

    private StringBlock mStringBlock;
    private int[] mResourceIds;
    private NamespaceStack mNamespaces = new NamespaceStack();

    private boolean mDecreaseDepth;

    private int mEvent;
    private int mLineNumber;
    private int mName;
    private int mNamespaceUri;
    private int[] mAttributes;
    private int mIdAttribute;
    private int mClassAttribute;
    private int mStyleAttribute;

    private static CustomXmlResourceParser mInstance;

    private CustomXmlResourceParser() {
        // cannot be instantiated
        resetEventInfo();
    }

    public static synchronized CustomXmlResourceParser getInstance() {
        if (mInstance == null) {
            mInstance = new CustomXmlResourceParser();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    @Override
    public void close() {
        if (!isCloseable) {
            return;
        }
        isCloseable = false;
        mReader.close();
        mReader = null;
        mStringBlock = null;
        mResourceIds = null;
        mNamespaces.reset();
        resetEventInfo();
    }

    @Override
    public int getAttributeNameResource(int index) {
        int name = mAttributes[getAttributeOffset(index) + Constant.Manifest.ATTRIBUTE_IX_NAME];
        if (mResourceIds == null || name < 0 || name >= mResourceIds.length) {
            return 0;
        }
        return mResourceIds[name];
    }

    @Override
    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return 0;
    }

    @Override
    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        int index = findAttribute(namespace, attribute);
        if (index == Constant.Manifest.DEFAULT_VALUE) {
            return defaultValue;
        }
        return getAttributeBooleanValue(index, defaultValue);
    }

    @Override
    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        int index = findAttribute(namespace, attribute);
        if (index == Constant.Manifest.DEFAULT_VALUE) {
            return defaultValue;
        }
        return getAttributeResourceValue(index, defaultValue);
    }

    @Override
    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        int index = findAttribute(namespace, attribute);
        if (index == Constant.Manifest.DEFAULT_VALUE) {
            return defaultValue;
        }
        return getAttributeIntValue(index, defaultValue);
    }

    @Override
    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        int index = findAttribute(namespace, attribute);
        if (index == Constant.Manifest.DEFAULT_VALUE) {
            return defaultValue;
        }
        return getAttributeUnsignedIntValue(index, defaultValue);
    }

    @Override
    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        int index = findAttribute(namespace, attribute);
        if (index == Constant.Manifest.DEFAULT_VALUE) {
            return defaultValue;
        }
        return getAttributeFloatValue(index, defaultValue);
    }

    @Override
    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return 0;
    }

    @Override
    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return getAttributeIntValue(index, defaultValue ? 1 : 0) != 0;
    }

    @Override
    public int getAttributeResourceValue(int index, int defaultValue) {
        int offset = getAttributeOffset(index);
        int valueType = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_TYPE];
        if (valueType == TypedValue.TYPE_REFERENCE) {
            return mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_DATA];
        }
        return defaultValue;
    }

    @Override
    public int getAttributeIntValue(int index, int defaultValue) {
        int offset = getAttributeOffset(index);
        int valueType = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_TYPE];
        if (valueType >= TypedValue.TYPE_FIRST_INT && valueType <= TypedValue.TYPE_LAST_INT) {
            return mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_DATA];
        }
        return defaultValue;
    }

    @Override
    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return getAttributeIntValue(index, defaultValue);
    }

    @Override
    public float getAttributeFloatValue(int index, float defaultValue) {
        int offset = getAttributeOffset(index);
        int valueType = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_TYPE];
        if (valueType == TypedValue.TYPE_FLOAT) {
            return Float.intBitsToFloat(mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_DATA]);
        }
        return defaultValue;
    }

    @Override
    public String getIdAttribute() {
        if (mIdAttribute == Constant.Manifest.DEFAULT_VALUE) {
            return null;
        }
        return mStringBlock.getString(mAttributes[getAttributeOffset(mIdAttribute) + Constant.Manifest.ATTRIBUTE_IX_VALUE_STRING]);
    }

    @Override
    public String getClassAttribute() {
        if (mClassAttribute == Constant.Manifest.DEFAULT_VALUE) {
            return null;
        }
        return mStringBlock.getString(mAttributes[getAttributeOffset(mClassAttribute) + Constant.Manifest.ATTRIBUTE_IX_VALUE_STRING]);
    }

    @Override
    public int getIdAttributeResourceValue(int defaultValue) {
        if (mIdAttribute == Constant.Manifest.DEFAULT_VALUE) {
            return defaultValue;
        }
        int offset = getAttributeOffset(mIdAttribute);
        int valueType = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_TYPE];
        if (valueType != TypedValue.TYPE_REFERENCE) {
            return defaultValue;
        }
        return mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_DATA];
    }

    @Override
    public int getStyleAttribute() {
        if (mStyleAttribute == Constant.Manifest.DEFAULT_VALUE) {
            return 0;
        }
        return mAttributes[getAttributeOffset(mStyleAttribute) + Constant.Manifest.ATTRIBUTE_IX_VALUE_DATA];
    }

    @Override
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        throw new XmlPullParserException(Constant.Manifest.METHOD_NOT_SUPPORTED);
    }

    @Override
    public boolean getFeature(String name) {
        return false;
    }

    @Override
    public void setProperty(String name, Object value) throws XmlPullParserException {
        throw new XmlPullParserException(Constant.Manifest.METHOD_NOT_SUPPORTED);
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public void setInput(java.io.Reader in) throws XmlPullParserException {
        throw new XmlPullParserException(Constant.Manifest.METHOD_NOT_SUPPORTED);
    }

    @Override
    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        throw new XmlPullParserException(Constant.Manifest.METHOD_NOT_SUPPORTED);
    }

    @Override
    public String getInputEncoding() {
        return null;
    }

    @Override
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        throw new XmlPullParserException(Constant.Manifest.METHOD_NOT_SUPPORTED);
    }

    @Override
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return mNamespaces.getAccumulatedCount(depth);
    }

    @Override
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        return mStringBlock.getString(mNamespaces.getPrefix(pos));
    }

    @Override
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        return mStringBlock.getString(mNamespaces.getUri(pos));
    }

    @Override
    public String getNamespace(String prefix) {
        throw new RuntimeException(Constant.Manifest.METHOD_NOT_SUPPORTED);
    }

    @Override
    public int getDepth() {
        return mNamespaces.getDepth() - 1;
    }

    @Override
    public String getPositionDescription() {
        return "XML line #" + getLineNumber();
    }

    @Override
    public int getLineNumber() {
        return mLineNumber;
    }

    @Override
    public int getColumnNumber() {
        return Constant.Manifest.DEFAULT_VALUE;
    }

    @Override
    public boolean isWhitespace() throws XmlPullParserException {
        return false;
    }

    @Override
    public String getText() {
        if (mName == Constant.Manifest.DEFAULT_VALUE || mEvent != TEXT) {
            return null;
        }
        return mStringBlock.getString(mName);
    }

    @Override
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        String text = getText();
        if (text == null) {
            return null;
        }
        holderForStartAndLength[0] = 0;
        holderForStartAndLength[1] = text.length();
        char[] chars = new char[text.length()];
        text.getChars(0, text.length(), chars, 0);
        return chars;
    }

    @Override
    public String getNamespace() {
        return mStringBlock.getString(mNamespaceUri);
    }

    @Override
    public String getName() {
        if (mName == Constant.Manifest.DEFAULT_VALUE || (mEvent != START_TAG && mEvent != END_TAG)) {
            return null;
        }
        return mStringBlock.getString(mName);
    }

    @Override
    public String getPrefix() {
        return mStringBlock.getString(mNamespaces.findPrefix(mNamespaceUri));
    }

    @Override
    public boolean isEmptyElementTag() throws XmlPullParserException {
        return false;
    }

    @Override
    public int getAttributeCount() {
        if (mEvent != START_TAG) {
            return Constant.Manifest.DEFAULT_VALUE;
        }
        return mAttributes.length / Constant.Manifest.ATTRIBUTE_LENGHT;
    }

    @Override
    public String getAttributeNamespace(int index) {
        int offset = getAttributeOffset(index);
        int namespace = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_NAMESPACE_URI];
        if (namespace == Constant.Manifest.DEFAULT_VALUE) {
            return "";
        }
        return mStringBlock.getString(namespace);
    }

    @Override
    public String getAttributeName(int index) {
        int offset = getAttributeOffset(index);
        int name = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_NAME];
        if (name == Constant.Manifest.DEFAULT_VALUE) {
            return "";
        }
        return mStringBlock.getString(name);
    }

    @Override
    public String getAttributePrefix(int index) {
        int offset = getAttributeOffset(index);
        int uri = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_NAMESPACE_URI];
        int prefix = mNamespaces.findPrefix(uri);
        if (prefix == Constant.Manifest.DEFAULT_VALUE) {
            return "";
        }
        return mStringBlock.getString(prefix);
    }

    @Override
    public String getAttributeType(int index) {
        return "CDATA";
    }

    @Override
    public boolean isAttributeDefault(int index) {
        return false;
    }

    @Override
    public String getAttributeValue(int index) {
        int offset = getAttributeOffset(index);
        int valueType = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_TYPE];
        if (valueType == TypedValue.TYPE_STRING) {
            int valueString = mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_STRING];
            return mStringBlock.getString(valueString);
        }
        return "";
        // return TypedValue.coerceToString(valueType,mAttributes[offset + Constant.Manifest.ATTRIBUTE_IX_VALUE_DATA]);
    }

    @Override
    public String getAttributeValue(String namespace, String attribute) {
        int index = findAttribute(namespace, attribute);
        if (index == Constant.Manifest.DEFAULT_VALUE) {
            return null;
        }
        return getAttributeValue(index);
    }

    @Override
    public int getEventType() throws XmlPullParserException {
        return mEvent;
    }

    @Override
    public int next() throws XmlPullParserException, IOException {
        if (mReader == null) {
            throw new XmlPullParserException("Parser is not opened.", this, null);
        }
        try {
            doNext();
            return mEvent;
        } catch (IOException exception) {
            close();
            throw exception;
        }
    }

    @Override
    public int nextToken() throws XmlPullParserException, IOException {
        return next();
    }

    @Override
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        if (type != getEventType() || (namespace != null && !namespace.equals(getNamespace()))
                || (name != null && !name.equals(getName()))) {
            throw new XmlPullParserException(TYPES[type] + " is expected.", this, null);
        }
    }

    @Override
    public String nextText() throws XmlPullParserException, IOException {
        if (getEventType() != START_TAG) {
            throw new XmlPullParserException("Parser must be on START_TAG to read next text.", this, null);
        }
        int eventType = next();
        if (eventType == TEXT) {
            String result = getText();
            eventType = next();
            if (eventType != END_TAG) {
                throw new XmlPullParserException("Event TEXT must be immediately followed by END_TAG.", this, null);
            }
            return result;
        } else if (eventType == END_TAG) {
            return "";
        } else {
            throw new XmlPullParserException("Parser must be on START_TAG or TEXT to read text.", this, null);
        }
    }

    @Override
    public int nextTag() throws XmlPullParserException, IOException {
        int eventType = next();
        if (eventType == TEXT && isWhitespace()) {
            eventType = next();
        }
        if (eventType != START_TAG && eventType != END_TAG) {
            throw new XmlPullParserException("Expected start or end tag.", this, null);
        }
        return eventType;
    }

    public int getAttributeValueType(int index) {
        return mAttributes[getAttributeOffset(index) + Constant.Manifest.ATTRIBUTE_IX_VALUE_TYPE];
    }

    public int getAttributeValueData(int index) {
        int offset = getAttributeOffset(index);
        return mAttributes[getAttributeOffset(index) + Constant.Manifest.ATTRIBUTE_IX_VALUE_DATA];
    }

    private int getAttributeOffset(int index) {
        if (mEvent != START_TAG) {
            throw new IndexOutOfBoundsException("Current event is not START_TAG.");
        }
        int offset = index * 5;
        if (offset >= mAttributes.length) {
            throw new IndexOutOfBoundsException("Invalid attribute index (" + index + ").");
        }
        return offset;
    }

    private int findAttribute(String namespace, String attribute) {
        if (mStringBlock == null || attribute == null) {
            return Constant.Manifest.DEFAULT_VALUE;
        }
        int name = mStringBlock.find(attribute);
        if (name == Constant.Manifest.DEFAULT_VALUE) {
            return Constant.Manifest.DEFAULT_VALUE;
        }
        int uri = (namespace != null) ? mStringBlock.find(namespace) : Constant.Manifest.DEFAULT_VALUE;
        for (int o = 0; o != mAttributes.length; ++o) {
            if (name == mAttributes[o + Constant.Manifest.ATTRIBUTE_IX_NAME]
                    && (uri == Constant.Manifest.DEFAULT_VALUE || uri == mAttributes[o + Constant.Manifest.ATTRIBUTE_IX_NAMESPACE_URI])) {
                return o / Constant.Manifest.ATTRIBUTE_LENGHT;
            }
        }
        return Constant.Manifest.DEFAULT_VALUE;
    }

    public void open(InputStream stream) {
        close();
        if (stream != null) {
            mReader = new Reader(stream, false);
        }
    }

    public final void readCheckType(Reader reader, int expectedType) throws IOException {
        int type = reader.readInt();
        LogUtil.getInstance().println("type:" + type);
        LogUtil.getInstance().println("expectedType:" + expectedType);
        if (type != expectedType) {
            throw new IOException("Expected chunk of type 0x" + Integer.toHexString(expectedType) + ", read 0x" + Integer.toHexString(type) + ".");
        }
    }

    private void doNext() throws IOException {
        // Delayed initialization.
        if (mStringBlock == null) {
            readCheckType(mReader, Constant.Manifest.CHUNK_AXML_FILE);
                /* chunkSize */
            mReader.skipInt();
            mStringBlock = StringBlock.getInstance().read(mReader);
            mNamespaces.increaseDepth();
            isCloseable = true;
        }

        if (mEvent == END_DOCUMENT) {
            return;
        }

        int event = mEvent;
        resetEventInfo();

        while (true) {
            if (mDecreaseDepth) {
                mDecreaseDepth = false;
                mNamespaces.decreaseDepth();
            }
            // Fake END_DOCUMENT event.
            if (event == END_TAG && mNamespaces.getDepth() == 1 && mNamespaces.getCurrentCount() == 0) {
                mEvent = END_DOCUMENT;
                break;
            }
            int chunkType;
            if (event == START_DOCUMENT) {
                // Fake event, see CHUNK_XML_START_TAG handler.
                chunkType = Constant.Manifest.CHUNK_XML_START_TAG;
            } else {
                chunkType = mReader.readInt();
            }
            if (chunkType == Constant.Manifest.CHUNK_RESOURCEIDS) {
                int chunkSize = mReader.readInt();
                if (chunkSize < 8 || (chunkSize % 4) != 0) {
                    throw new IOException("Invalid resource ids size (" + chunkSize + ").");
                }
                mResourceIds = mReader.readIntArray(chunkSize / 4 - 2);
                continue;
            }
            if (chunkType < Constant.Manifest.CHUNK_XML_FIRST || chunkType > Constant.Manifest.CHUNK_XML_LAST) {
                throw new IOException("Invalid chunk type (" + chunkType + ").");
            }
            // Fake START_DOCUMENT event.
            if (chunkType == Constant.Manifest.CHUNK_XML_START_TAG && event == Constant.Manifest.DEFAULT_VALUE) {
                mEvent = START_DOCUMENT;
                break;
            }
            // Common header.
                /* chunkSize */
            mReader.skipInt();
            int lineNumber = mReader.readInt();
                /* 0xFFFFFFFF */
            mReader.skipInt();
            if (chunkType == Constant.Manifest.CHUNK_XML_START_NAMESPACE || chunkType == Constant.Manifest.CHUNK_XML_END_NAMESPACE) {
                if (chunkType == Constant.Manifest.CHUNK_XML_START_NAMESPACE) {
                    int prefix = mReader.readInt();
                    int uri = mReader.readInt();
                    mNamespaces.push(prefix, uri);
                } else {
                        /* prefix */
                    mReader.skipInt();
                        /* uri */
                    mReader.skipInt();
                    mNamespaces.pop();
                }
                continue;
            }
            mLineNumber = lineNumber;
            if (chunkType == Constant.Manifest.CHUNK_XML_START_TAG) {
                mNamespaceUri = mReader.readInt();
                mName = mReader.readInt();
                    /* flags? */
                mReader.skipInt();
                int attributeCount = mReader.readInt();
                mIdAttribute = (attributeCount >>> 16) - 1;
                attributeCount &= 0xFFFF;
                mClassAttribute = mReader.readInt();
                mStyleAttribute = (mClassAttribute >>> 16) - 1;
                mClassAttribute = (mClassAttribute & 0xFFFF) - 1;
                mAttributes = mReader.readIntArray(attributeCount * Constant.Manifest.ATTRIBUTE_LENGHT);
                for (int i = Constant.Manifest.ATTRIBUTE_IX_VALUE_TYPE; i < mAttributes.length; ) {
                    mAttributes[i] = (mAttributes[i] >>> 24);
                    i += Constant.Manifest.ATTRIBUTE_LENGHT;
                }
                mNamespaces.increaseDepth();
                mEvent = START_TAG;
                break;
            }
            if (chunkType == Constant.Manifest.CHUNK_XML_END_TAG) {
                mNamespaceUri = mReader.readInt();
                mName = mReader.readInt();
                mEvent = END_TAG;
                mDecreaseDepth = true;
                break;
            }
            if (chunkType == Constant.Manifest.CHUNK_XML_TEXT) {
                mName = mReader.readInt();
                mReader.skipInt();
                mReader.skipInt();
                mEvent = TEXT;
                break;
            }
        }
    }

    private void resetEventInfo() {
        mEvent = Constant.Manifest.DEFAULT_VALUE;
        mLineNumber = Constant.Manifest.DEFAULT_VALUE;
        mName = Constant.Manifest.DEFAULT_VALUE;
        mNamespaceUri = Constant.Manifest.DEFAULT_VALUE;
        mAttributes = null;
        mIdAttribute = Constant.Manifest.DEFAULT_VALUE;
        mClassAttribute = Constant.Manifest.DEFAULT_VALUE;
        mStyleAttribute = Constant.Manifest.DEFAULT_VALUE;
    }
}
