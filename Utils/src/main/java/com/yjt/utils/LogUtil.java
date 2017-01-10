package com.yjt.utils;

import android.text.TextUtils;

public class LogUtil/* implements OnLogListener */ {

    private static LogUtil mInstance;

    private LogUtil() {
        // cannot be instantiated
    }

    public static synchronized LogUtil getInstance() {
        if (mInstance == null) {
            mInstance = new LogUtil();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

//    public static void open(boolean isLoadLib, int level, int mode, String cacheDir, String logDir, String nameprefix) {
//        if (isLoadLib) {
//            System.loadLibrary("marsxlog");
//        }
//        appenderOpen(level, mode, cacheDir, logDir, nameprefix);
//    }
//
//    private static String decryptTag(String tag) {
//        return tag;
//    }


    public void v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg);
    }

    public void v(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg, t);
    }

    public void d(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg);
    }

    public void d(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg, t);
    }

    public void i(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg);
    }

    public void i(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.i(tag, msg, t);
    }

    public void w(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg);
    }

    public void w(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.w(tag, msg, t);
    }

    public void e(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg);
    }

    public void e(String tag, String msg, Throwable t) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(tag, msg, t);
    }

    public void println(String content) {
//        if (BuildConfig.DEBUG) {
            if (!TextUtils.isEmpty(content)) {
                System.out.println(content);
            }
//        }
    }

//    public void v(String tag, final String format, final Object... obj) {
//        v(tag, Regex.NONE.getRegext(), Regex.NONE.getRegext(), 0, Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(), obj == null ? format : String.format(format, obj));
//    }
//
//    public void d(String tag, final String format, final Object... obj) {
//        d(tag, Regex.NONE.getRegext(), Regex.NONE.getRegext(), 0, Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(), obj == null ? format : String.format(format, obj));
//    }
//
//    public void i(String tag, final String format, final Object... obj) {
//        i(tag, Regex.NONE.getRegext(), Regex.NONE.getRegext(), 0, Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(), obj == null ? format : String.format(format, obj));
//    }
//
//    public void w(String tag, final String format, final Object... obj) {
//        w(tag, Regex.NONE.getRegext(), Regex.NONE.getRegext(), 0, Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(), obj == null ? format : String.format(format, obj));
//    }
//
//    public void e(String tag, final String format, final Object... obj) {
//        e(tag, Regex.NONE.getRegext(), Regex.NONE.getRegext(), 0, Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(), obj == null ? format : String.format(format, obj));
//    }
//
//    public void printErrorStackTrace(String tag, Throwable error, final String format, final Object... obj) {
//        e(tag, Regex.NONE.getRegext(), Regex.NONE.getRegext(), 0, Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(), obj == null ? format : String.format(format, obj) + android.util.Log.getStackTraceString(error));
//    }
//
//    public void f(String tag, final String format, final Object... obj) {
//        f(tag, Regex.NONE.getRegext(), Regex.NONE.getRegext(), 0, Process.myPid(), Thread.currentThread().getId(), Looper.getMainLooper().getThread().getId(), obj == null ? format : String.format(format, obj));
//    }
//
//    @Override
//    public void v(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
//        logWrite2(Constant.LOG_LEVEL.VERBOSE, decryptTag(tag), filename, funcname, line, pid, tid, maintid, log);
//    }
//
//    @Override
//    public void d(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
//        logWrite2(Constant.LOG_LEVEL.DEBUG, decryptTag(tag), filename, funcname, line, pid, tid, maintid, log);
//    }
//
//    @Override
//    public void i(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
//        logWrite2(Constant.LOG_LEVEL.INFO, decryptTag(tag), filename, funcname, line, pid, tid, maintid, log);
//    }
//
//    @Override
//    public void w(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
//        logWrite2(Constant.LOG_LEVEL.WARNING, decryptTag(tag), filename, funcname, line, pid, tid, maintid, log);
//    }
//
//    @Override
//    public void e(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
//        logWrite2(Constant.LOG_LEVEL.ERROR, decryptTag(tag), filename, funcname, line, pid, tid, maintid, log);
//    }
//
//    @Override
//    public void f(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
//        logWrite2(Constant.LOG_LEVEL.FATAL, decryptTag(tag), filename, funcname, line, pid, tid, maintid, log);
//    }
//
//    static class LogInfo {
//        public int level;
//        public String tag;
//        public String filename;
//        public String funcname;
//        public int line;
//        public long pid;
//        public long tid;
//        public long maintid;
//    }
//
//    public static native void logWrite(XLoggerInfo logInfo, String log);
//
//    public static native void logWrite2(int level, String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
//
//    public static native void setLogLevel(int logLevel);
//
//    public static native void setAppenderMode(int mode);
//
//    public static native void setConsoleLogOpen(boolean isOpen);    //set whether the console prints log
//
//    public static native void setErrLogOpen(boolean isOpen);    //set whether the  prints err log into a separate file
//
//    public static native void appenderOpen(int level, int mode, String cacheDir, String logDir, String nameprefix);
//
//    public native int getLogLevel();
//
//    public native void appenderClose();
//
//    public native void appenderFlush(boolean isSync);
}
