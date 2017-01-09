package com.yjt.utils;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectUtil {

    private static ReflectUtil mInstance;

    private ReflectUtil() { }

    public static synchronized ReflectUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ReflectUtil();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public void setFieldValue(Class ownerClass, Object instance, String fieldName, Object value) throws IllegalAccessException {
        for (Field field : ownerClass.getFields()) {
            if (TextUtils.equals(field.getName(), fieldName)) {
                field.set(instance, value);
                LogUtil.getInstance().println("-->:" + fieldName + " = " + getFieldValue(ownerClass, instance, fieldName));
            }
        }
    }

    private Object getFieldValue(Class ownerClass, Object instance, String fieldName) {
        try {
            return ownerClass.getField(fieldName).get(instance);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object invokeMethod(Object owner
            , String methodName
            , Class[] methodParameterTypes
            , Object[] methodParameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return owner.getClass().getMethod(methodName, methodParameterTypes).invoke(owner, methodParameters);
    }

    public Object invokeMethod(String className
            , Class[] constructorParameterTypes
            , Object[] constructorParameters
            , String methodName
            , Class[] methodParameterTypes
            , Object[] methodParameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        Class<?> ownerClass = Class.forName(className);
        return ownerClass.getMethod(methodName, methodParameterTypes).invoke(ownerClass.getConstructor(constructorParameterTypes).newInstance(constructorParameters), methodParameters);
    }

    public Object invokeStaticMethod(String className
            , String methodName
            , Class[] methodParameterTypes
            , Object[] methodParameters) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return Class.forName(className).getMethod(methodName, methodParameterTypes).invoke(null, methodParameters);
    }

    public Type getGenericSuperclassType(Class<?> subclass) {
        return getGenericSuperclassTypes(0, subclass);
    }

    private Type getGenericSuperclassTypes(int index, Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) superclass).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new RuntimeException("Index outof bounds");
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return params[index];
    }
}
