package com.yjt.plguin.listener.implement;

import android.support.annotation.Nullable;

import com.yjt.plguin.BuildConfig;
import com.yjt.utils.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.reflect.Proxy.newProxyInstance;

public class CustomProxy<T> implements InvocationHandler {

    private static CustomProxy mInstance;
    private final T mTarget;

    private CustomProxy(T target) {
        // cannot be instantiated
        this.mTarget = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogUtil.getInstance().println("Proxy invoke, class = " + proxy.getClass().getName() + ", method = " + method.getName());
        try {
            return method.invoke(mTarget, args);

        } catch (Throwable e) {
            LogUtil.getInstance().println("Invoke plugin method failed.");
            if (BuildConfig.DEBUG) {
                throw new RuntimeException(e);
            }
        }
        // The plugin behavior interface is not capable with the Impl of the plugin.
        // Return the default value of the given type as to abort crash here.
        return getDefaultValue(method.getReturnType());
    }

    private static Object getDefaultValue(Class<?> type) {
        // Check primitive type.
        if (type == Boolean.class || type == boolean.class) {
            return false;
        } else if (type == Byte.class || type == byte.class) {
            return 0;
        } else if (type == Character.class || type == char.class) {
            return '\u0000';
        } else if (type == Short.class || type == short.class) {
            return 0;
        } else if (type == Integer.class || type == int.class) {
            return 0;
        } else if (type == Long.class || type == long.class) {
            return 0L;
        } else if (type == Float.class || type == float.class) {
            return 0.0F;
        } else if (type == Double.class || type == double.class) {
            return 0.0D;
        } else if (type == Void.class || type == void.class) {
            return null;
        }
        return null;
    }

    @Nullable
    public static <T> T getProxy(Class<T> itf, T t) throws Exception {
        try {
            Class<?> clazz = t.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();

            if (interfaces != null && interfaces.length > 0) {
                for (Class item : interfaces) {
                    if (itf.isAssignableFrom(item)) {
                        return (T) newProxyInstance(clazz.getClassLoader(), new Class[]{item}, new CustomProxy<>(t));
                    }
                }
            }
            LogUtil.getInstance().println("Can not find proxy interface.");
            throw new Exception("Can not find proxy interface.");
        } catch (Throwable e) {
            LogUtil.getInstance().println("Create interface proxy failed.");
            throw new Exception("Create interface proxy failed.", e);
        }
    }
}
