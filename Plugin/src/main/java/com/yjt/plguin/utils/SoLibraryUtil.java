package com.yjt.plguin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.yjt.plguin.BuildConfig;
import com.yjt.plguin.constant.Constant;
import com.yjt.utils.LogUtil;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SoLibraryUtil {

    private static SoLibraryUtil mInstance;

    private SoLibraryUtil() {
        // cannot be instantiated
    }

    public static synchronized SoLibraryUtil getInstance() {
        if (mInstance == null) {
            mInstance = new SoLibraryUtil();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public Set<String> extractSoLibrary(File apkFile, File destDir) throws IOException {
        if (apkFile == null || !apkFile.exists()) {
            throw new FileNotFoundException("Apk file not found.");
        }

        HashSet<String> result = new HashSet<>(4);
        FileUtil.getInstance().checkCreateDir(destDir);
        LogUtil.getInstance().println("copy so file to " + destDir.getAbsolutePath() + ", apk = " + apkFile.getName());

        ZipFile zipFile = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            zipFile = new ZipFile(apkFile);
            ZipEntry zipEntry;
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                zipEntry = (ZipEntry) entries.nextElement();
                String relativePath = zipEntry.getName();

                if (relativePath == null || relativePath.contains("../")) {
                    // Abort zip file injection hack.
                    continue;
                }

                if (!relativePath.startsWith("lib" + File.separator)) {
                    LogUtil.getInstance().println("not lib dir entry, skip " + relativePath);
                    continue;
                }

                if (zipEntry.isDirectory()) {
                    File folder = new File(destDir, relativePath);
                    LogUtil.getInstance().println("create dir " + folder.getAbsolutePath());
                    FileUtil.getInstance().checkCreateDir(folder);
                } else {
                    File SoLibraryFile = new File(destDir, relativePath);
                    LogUtil.getInstance().println("unzip SoLibrary file " + SoLibraryFile.getAbsolutePath());
                    FileUtil.getInstance().checkCreateFile(SoLibraryFile);

                    byte[] buffer = new byte[Constant.Space.IO_BUFFER_CAPACITY];
                    out = new FileOutputStream(SoLibraryFile);
                    FileDescriptor fd = ((FileOutputStream) out).getFD();
                    out = new BufferedOutputStream(out);
                    in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    int count;
                    while ((count = in.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                    out.flush();
                    fd.sync();

                    result.add(SoLibraryFile.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Unzip SoLibrarys fail.", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (BuildConfig.DEBUG) {
            for (String item : result) {
                LogUtil.getInstance().println(item);
            }
        }
        return result;
    }

    public File copySoLibrary(Context context, File sourceDir, String SoLibraryName, File destDir) throws IOException {
        File matchSoLibrary = null;
        List<String> capableAbis = getCapableAbis(context);

        if (capableAbis != null) {
            for (String abi : capableAbis) {
                LogUtil.getInstance().println("Try install SoLibrary, supported abi = " + abi);
                String name = "lib" + File.separator + abi + File.separator + SoLibraryName;
                File sourceFile = new File(sourceDir, name);
                if (sourceFile.exists()) {
                    File destFile = new File(destDir, SoLibraryName);
                    if (sourceFile.renameTo(destFile)) {
                        LogUtil.getInstance().println("Rename SoLibrary, from = " + sourceFile.getAbsolutePath()
                                                              + ", to = " + destFile.getAbsolutePath());
                    } else {
                        throw new IOException("Rename SoLibrary fail.");
                    }
                    matchSoLibrary = destFile;
                    break;
                }
            }
        } else {
            LogUtil.getInstance().println("Cpu abis is null.");
        }

        if (matchSoLibrary == null) {
            LogUtil.getInstance().println("Can not install " + SoLibraryName + ", NO_MATCHING_ABIS");
        }
        return matchSoLibrary;
    }

    public List<String> getCapableAbis(Context context) {
        ApplicationInfo appInfo = ApkUtil.getInstance().getApplicationInfo(context);
        Set<String> apkAbis = new HashSet<>();
        List<String> buildAbis = new ArrayList<>();

        if (appInfo != null) {
            String apkPath = appInfo.sourceDir;
            ZipFile zipFile = null;

            try {
                zipFile = new ZipFile(apkPath);
                ZipEntry zipEntry;
                Enumeration entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    zipEntry = (ZipEntry) entries.nextElement();
                    String path = zipEntry.getName();

                    if (path == null || path.contains("../")) {
                        continue;
                    }

                    String startSymbol = "lib" + File.separator;
                    String endSymbol = String.valueOf(File.separator);

                    if (!path.startsWith(startSymbol)) {
                        continue;
                    }

                    int start = path.indexOf(startSymbol) + startSymbol.length();
                    int end = path.indexOf(endSymbol, startSymbol.length());

                    if (end > start && end < path.length()) {
                        apkAbis.add(path.substring(start, end));
                    } else {
                        LogUtil.getInstance().println("Substring bounded, length = " + path.length()
                                                              + ", start = " + start + ", end = " + end);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = Build.SUPPORTED_ABIS;
            if (abis != null) {
                Collections.addAll(buildAbis, abis);
            } else {
                LogUtil.getInstance().println("Cpu abis is null.");
            }

        } else {
            buildAbis.add(Build.CPU_ABI);
            buildAbis.add(Build.CPU_ABI2);
        }

        LogUtil.getInstance().println("Build cpu abis = " + buildAbis);
        LogUtil.getInstance().println("Apk cpu abis = " + apkAbis);

        if (apkAbis.size() > 0) {
            Iterator<String> iterator = buildAbis.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (!apkAbis.contains(next)) {
                    iterator.remove();
                }
            }
        }

        LogUtil.getInstance().println("Capable cpu abis = " + buildAbis);
        return buildAbis;
    }
}
