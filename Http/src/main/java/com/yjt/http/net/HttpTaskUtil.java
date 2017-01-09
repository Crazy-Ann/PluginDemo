package com.yjt.http.net;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpTaskUtil {

    private static HttpTaskUtil mHttpTaskUtil;
    private Map<String, List<HttpTask>> mHttpTasks;

    private HttpTaskUtil() {
        mHttpTasks = new ConcurrentHashMap<>();
    }

    public static synchronized HttpTaskUtil getInstance() {
        if (mHttpTaskUtil == null) {
            mHttpTaskUtil = new HttpTaskUtil();
        }
        return mHttpTaskUtil;
    }

    public static void releaseInstance() {
        if (mHttpTaskUtil != null) {
            mHttpTaskUtil = null;
        }
    }

    public void removeTask(String key) {
        if (mHttpTasks.containsKey(key)) {
            mHttpTasks.remove(key);
        }
    }

    public void addTask(String key, HttpTask task) {
        if (mHttpTasks.containsKey(key)) {
            List<HttpTask> tasks = mHttpTasks.get(key);
            tasks.add(task);
            mHttpTasks.put(key, tasks);
        } else {
            List<HttpTask> tasks = Lists.newArrayList();
            tasks.add(task);
            mHttpTasks.put(key, tasks);
        }
    }

    public boolean contains(String key) {
        return mHttpTasks.containsKey(key);
    }
}
