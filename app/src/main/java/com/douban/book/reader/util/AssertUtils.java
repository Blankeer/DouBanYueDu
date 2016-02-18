package com.douban.book.reader.util;

public class AssertUtils {
    public static void notNull(String msg, Object... args) {
        for (Object object : args) {
            if (object == null) {
                throw new IllegalArgumentException(msg);
            }
        }
    }

    public static void throwIfInterrupted(Object object) throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException(String.format("Interrupted in %s", new Object[]{object.toString()}));
        }
    }
}
