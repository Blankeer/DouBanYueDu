package com.sina.weibo.sdk.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {
    public static Object getProperty(Object owner, String fieldName) throws Exception {
        return owner.getClass().getField(fieldName).get(owner);
    }

    public static Object getStaticProperty(String className, String fieldName) throws Exception {
        Class ownerClass = Class.forName(className);
        return ownerClass.getField(fieldName).get(ownerClass);
    }

    public static Object invokeMethod(Object owner, String methodName, Object[] args) throws Exception {
        Class ownerClass = owner.getClass();
        Class[] argsClass = new Class[args.length];
        int j = args.length;
        for (int i = 0; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }
        return ownerClass.getMethod(methodName, argsClass).invoke(owner, args);
    }

    public static Object invokeStaticMethod(String className, String methodName, Object[] args) throws Exception {
        Class ownerClass = Class.forName(className);
        Class[] argsClass = new Class[args.length];
        int j = args.length;
        for (int i = 0; i < j; i++) {
            argsClass[i] = args[i].getClass();
        }
        return ownerClass.getMethod(methodName, argsClass).invoke(null, args);
    }

    public static Object newInstance(String className, Class<?>[] parameterTypes, Object[] args) throws Exception {
        return Class.forName(className).getConstructor(parameterTypes).newInstance(args);
    }

    public static boolean isInstance(Object obj, Class cls) {
        return cls.isInstance(obj);
    }

    public static Object getByArray(Object array, int index) {
        return Array.get(array, index);
    }

    public static void invokeVoidMethod(Object owner, String methodName, boolean property) {
        try {
            owner.getClass().getMethod(methodName, new Class[]{Boolean.TYPE}).invoke(owner, new Object[]{Boolean.valueOf(property)});
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e2) {
        } catch (IllegalArgumentException e3) {
        } catch (IllegalAccessException e4) {
        } catch (InvocationTargetException e5) {
        }
    }

    public static Object invokeMethod(Object ownerObj, String methodName, Class<?>[] parameterTypes, Object[] params) {
        try {
            return ownerObj.getClass().getMethod(methodName, parameterTypes).invoke(ownerObj, params);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return null;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return null;
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
            return null;
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    public static Object invokeParamsMethod(Object ownerObj, String methodName, Class<?>[] parameterTypes, Object[] params) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = ownerObj.getClass().getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(ownerObj, params);
    }

    public static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes, Object[] params) {
        Object obj = null;
        try {
            obj = Class.forName(className).getMethod(methodName, parameterTypes).invoke(null, params);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (IllegalArgumentException e4) {
            e4.printStackTrace();
        } catch (IllegalAccessException e5) {
            e5.printStackTrace();
        } catch (InvocationTargetException e6) {
            e6.printStackTrace();
        }
        return obj;
    }

    public static Object invokeStaticMethod(Class c, String methodName, Class<?>[] parameterTypes, Object[] params) {
        Object obj = null;
        try {
            obj = c.getMethod(methodName, parameterTypes).invoke(null, params);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
        return obj;
    }
}
