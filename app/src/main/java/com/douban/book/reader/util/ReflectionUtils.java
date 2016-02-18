package com.douban.book.reader.util;

public class ReflectionUtils {
    public static boolean isInstanceOf(Class<?> cls, Class<?> type) {
        while (cls != null) {
            if (cls == type) {
                return true;
            }
            for (Class<?> ifs : cls.getInterfaces()) {
                if (ifs == type) {
                    return true;
                }
            }
            cls = cls.getSuperclass();
        }
        return false;
    }

    public static boolean isInstanceOf(Object obj, Class<?> type) {
        return obj != null && isInstanceOf(obj.getClass(), (Class) type);
    }
}
