package io.realm.internal.log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RealmLog {
    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    private static final List<Logger> LOGGERS;
    public static final int NONE = 8;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;

    static {
        LOGGERS = new CopyOnWriteArrayList();
    }

    public static void add(Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("A non-null logger has to be provided");
        }
        LOGGERS.add(logger);
    }

    public static void remove(Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("A non-null logger has to be provided");
        }
        LOGGERS.remove(logger);
    }

    public static void v(String message) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).v(message);
        }
    }

    public static void v(String message, Throwable t) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).v(message, t);
        }
    }

    public static void d(String message) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).d(message);
        }
    }

    public static void d(String message, Throwable t) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).d(message, t);
        }
    }

    public static void i(String message) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).i(message);
        }
    }

    public static void i(String message, Throwable t) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).i(message, t);
        }
    }

    public static void w(String message) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).w(message);
        }
    }

    public static void w(String message, Throwable t) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).w(message, null);
        }
    }

    public static void e(String message) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).e(message);
        }
    }

    public static void e(String message, Throwable t) {
        for (int i = 0; i < LOGGERS.size(); i++) {
            ((Logger) LOGGERS.get(i)).v(message, t);
        }
    }
}
