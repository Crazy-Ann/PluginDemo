package com.yjt.plguin.utils;

import android.content.Context;
import android.support.compat.BuildConfig;
import android.text.TextUtils;

import com.yjt.utils.LogUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    private static FileUtil mInstance;

    private FileUtil() {
        // cannot be instantiated
    }

    public static synchronized FileUtil getInstance() {
        if (mInstance == null) {
            mInstance = new FileUtil();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public void closeQuietly(Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }

    public boolean delete(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return delete(new File(path));
    }

    public boolean delete(File file) {
        return FileUtils.deleteQuietly(file);
    }

    public boolean exist(String path) {
        return !TextUtils.isEmpty(path) && (new File(path).exists());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void checkCreateFile(File file) throws IOException {
        if (file == null) {
            throw new IOException("File is null.");
        }
        if (file.exists()) {
            delete(file);
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.createNewFile()) {
            throw new IOException("Create file fail, file already exists.");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void checkCreateDir(File file) throws IOException {
        if (file == null) {
            throw new IOException("Dir is null.");
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                return;
            }
            if (!delete(file)) {
                throw new IOException("Fail to delete existing file, file = " + file.getAbsolutePath());
            }
            file.mkdir();
        } else {
            file.mkdirs();
        }
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException("Fail to create dir, dir = " + file.getAbsolutePath());
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile == null) {
            throw new IOException("Source file is null.");
        }
        if (destFile == null) {
            throw new IOException("Dest file is null.");
        }
        if (!sourceFile.exists()) {
            throw new IOException("Source file not found.");
        }

        checkCreateFile(destFile);
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(destFile);
            FileDescriptor fd = ((FileOutputStream) out).getFD();
            out = new BufferedOutputStream(out);
            IOUtils.copy(in, out);
            out.flush();
            fd.sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    public void copyFileFromAsset(Context context, String pathAssets, File destFile)
            throws IOException {
        if (TextUtils.isEmpty(pathAssets)) {
            throw new IOException("Asset path is empty.");
        }

        checkCreateFile(destFile);
        InputStream in = null;
        OutputStream out = null;

        try {
            in = context.getAssets().open(pathAssets);
            out = new FileOutputStream(destFile);
            FileDescriptor fd = ((FileOutputStream) out).getFD();
            out = new BufferedOutputStream(out);
            IOUtils.copy(in, out);
            out.flush();
            fd.sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    public void dumpFiles(File file) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        boolean isDirectory = file.isDirectory();
        LogUtil.getInstance().println("path = " + file.getAbsolutePath() + ", isDir = " + isDirectory);
        if (isDirectory) {
            File[] childFiles = file.listFiles();
            if (childFiles != null && childFiles.length > 0) {
                for (File childFile : childFiles) {
                    dumpFiles(childFile);
                }
            }
        }
    }
}
