package com.yjt.utils.listener.log;

public interface OnLogListener {

    void v(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

    void i(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

    void d(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

    void w(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

    void e(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

    void f(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);
}
