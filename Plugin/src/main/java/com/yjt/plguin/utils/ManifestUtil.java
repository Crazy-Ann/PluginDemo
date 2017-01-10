package com.yjt.plguin.utils;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;

import com.yjt.plguin.BuildConfig;
import com.yjt.plguin.constant.Constant;
import com.yjt.plguin.entity.PluginApk;
import com.yjt.plguin.listener.implement.CustomXmlResourceParser;
import com.yjt.utils.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ManifestUtil {

    private static ManifestUtil mInstance;

    private ManifestUtil() {
        // cannot be instantiated
    }

    public static synchronized ManifestUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ManifestUtil();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    @NonNull
    public PluginApk parse(File apk) throws IOException {
        if (!apk.exists()) {
            throw new IOException("Apk not found.");
        }

        String namespace = null;
        String packageName = null;
        String versionCode = null;
        String versionName = null;
        String application = Application.class.getName();
        Map<String, Integer> dependencies = new HashMap<>();
        PluginApk pluginApk = new PluginApk();

        try {
            ZipFile zipFile = new ZipFile(apk, ZipFile.OPEN_READ);
            ZipEntry manifestXmlEntry = zipFile.getEntry(Constant.Manifest.DEFAULT_MANIFEST_NAME);
            String manifestXml = getManifestXmlFromAPK(zipFile, manifestXmlEntry);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(manifestXml));
            int eventType = parser.getEventType();
            do {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG: {
                        String tag = parser.getName();
                        if ("manifest".equals(tag)) {
                            namespace = parser.getNamespace("android");
                            packageName = parser.getAttributeValue(null, "package");
                            versionCode = parser.getAttributeValue(namespace, "versionCode");
                            versionName = parser.getAttributeValue(namespace, "versionName");
                        } else if ("meta-data".equals(tag)) {
                            LogUtil.getInstance().println("Parse meta-data");
                            String name = parser.getAttributeValue(namespace, "name");
                            String value = parser.getAttributeValue(namespace, "value");
                            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value)
                                    && name.startsWith(BuildConfig.PREFIX)) {
                                computeDependency(name, value, dependencies);
                            }
                        } else if ("exported-fragment".equals(tag)) {
                        } else if ("exported-service".equals(tag)) {
                        } else if ("uses-library".equals(tag)) {
                        } else if ("application".equals(tag)) {
                            String name = parser.getAttributeValue(namespace, "name");
                            if (name != null) {
                                name = computeName(name, packageName);
                                if (!TextUtils.isEmpty(name)) {
                                    application = name;
                                }
                            }
                        } else if ("activity".equals(tag)) {
                            LogUtil.getInstance().println("Parse activity");
                        } else if ("receiver".equals(tag)) {
                        } else if ("service".equals(tag)) {
                        } else if ("provider".equals(tag)) {
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        break;
                    }
                }
                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        LogUtil.getInstance().println("Parse manifest, namespace = " + namespace
                                              + ", package = " + packageName
                                              + ", application = " + application
                                              + ", versionName = " + versionName
                                              + ", versionCode = " + versionCode);

        pluginApk.application = application;
        pluginApk.packageName = packageName;
        pluginApk.versionName = versionName;
        pluginApk.versionCode = versionCode;
        pluginApk.dependencies = dependencies;

        return pluginApk;
    }

    @Nullable
    private String computeName(String nameOrig, String pkgName) {
        if (nameOrig == null) {
            return null;
        }

        if (nameOrig.startsWith(".")) {
            return pkgName + nameOrig;
        } else if (!nameOrig.contains(".")) {
            return pkgName + '.' + nameOrig;
        } else {
            return nameOrig;
        }
    }

    private void computeDependency(String name, String value,
                                   Map<String, Integer> dependencies) {
        String library = name.substring(name.indexOf(BuildConfig.PREFIX) + BuildConfig.PREFIX.length(), name.lastIndexOf(BuildConfig.SUFFIX));
        Integer version = Integer.valueOf(value);
        dependencies.put(library, version);
    }

    public String getManifestXmlFromAPK(String apkPath) {
        ZipFile file = null;
        String rs = null;
        try {
            File apkFile = new File(apkPath);
            file = new ZipFile(apkFile, ZipFile.OPEN_READ);
            ZipEntry entry = file.getEntry(Constant.Manifest.DEFAULT_MANIFEST_NAME);
            rs = getManifestXmlFromAPK(file, entry);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return rs;
    }

    public String getManifestXmlFromAPK(ZipFile file, ZipEntry entry) {
        StringBuilder xmlBuilder = new StringBuilder(100);
        try {
            CustomXmlResourceParser.getInstance().open(file.getInputStream(entry));
            StringBuilder builder = new StringBuilder(10);
            final String indentStep = " ";
            int type;
            while ((type = CustomXmlResourceParser.getInstance().next()) != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT: {
                        log(xmlBuilder, "<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        log(false, xmlBuilder, "%s<%s%s", builder, getNamespacePrefix(CustomXmlResourceParser.getInstance().getPrefix()), CustomXmlResourceParser.getInstance().getName());
                        builder.append(indentStep);
                        int namespaceCountBefore = CustomXmlResourceParser.getInstance().getNamespaceCount(CustomXmlResourceParser.getInstance().getDepth() - 1);
                        int namespaceCount = CustomXmlResourceParser.getInstance().getNamespaceCount(CustomXmlResourceParser.getInstance().getDepth());
                        for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
                            log(xmlBuilder, "%sxmlns:%s=\"%s\"", i == namespaceCountBefore ? "  " : builder,
                                CustomXmlResourceParser.getInstance().getNamespacePrefix(i), CustomXmlResourceParser.getInstance().getNamespaceUri(i));
                        }
                        for (int i = 0, size = CustomXmlResourceParser.getInstance().getAttributeCount(); i != size; ++i) {
                            log(false, xmlBuilder, "%s%s%s=\"%s\"", " ", getNamespacePrefix(CustomXmlResourceParser.getInstance().getAttributePrefix(i)),
                                CustomXmlResourceParser.getInstance().getAttributeName(i), getAttributeValue(CustomXmlResourceParser.getInstance(), i));
                        }
                        // log("%s>",sb);
                        log(xmlBuilder, ">");
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        builder.setLength(builder.length() - indentStep.length());
                        log(xmlBuilder, "%s</%s%s>", builder, getNamespacePrefix(CustomXmlResourceParser.getInstance().getPrefix()), CustomXmlResourceParser.getInstance().getName());
                        break;
                    }
                    case XmlPullParser.TEXT: {
                        log(xmlBuilder, "%s%s", builder, CustomXmlResourceParser.getInstance().getText());
                        break;
                    }
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            CustomXmlResourceParser.getInstance().close();
        }
        return xmlBuilder.toString();
    }

    private String getNamespacePrefix(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return "";
        }
        return prefix + ":";
    }

    private String getAttributeValue(CustomXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        if (type == TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        }
        if (type == TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type == TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        }
        if (type == TypedValue.TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        }
        if (type == TypedValue.TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data)) + Constant.Manifest.DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type == TypedValue.TYPE_FRACTION) {
            return Float.toString(complexToFloat(data)) + Constant.Manifest.FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        if (type == 0x07) {
            return String.format("@%s%08X", getPackage(data), data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);
    }

    private String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }

    private void log(StringBuilder xmlSb, String format, Object... arguments) {
        log(true, xmlSb, format, arguments);
    }

    private void log(boolean newLine, StringBuilder xmlSb, String format, Object... arguments) {
        // System.out.printf(format,arguments);
        // if(newLine) System.out.println();
        xmlSb.append(String.format(format, arguments));
        if (newLine) xmlSb.append("\n");
    }

    private float complexToFloat(int complex) {
        return (float) (complex & 0xFFFFFF00) * Constant.Manifest.RADIX_MULTS[(complex >> 4) & 3];
    }
}
