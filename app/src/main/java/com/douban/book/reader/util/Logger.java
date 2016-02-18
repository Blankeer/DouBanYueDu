package com.douban.book.reader.util;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Key;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static boolean mIsLogEnabled;
    private static final ThreadLocal<SimpleDateFormat> sDateFormatter;
    private static long sLastTimeStamp;
    private static boolean sLogFeedback;

    public static class Feedback {
        public static StringBuilder sContent;

        static {
            sContent = new StringBuilder();
        }

        public static void d(String tag, String msg) {
            sContent.append('\n').append(Logger.getTag(tag)).append(Char.SPACE).append(msg);
        }

        public static void e(String tag, Throwable e) {
            sContent.append('\n').append(Logger.getTag(tag)).append(Char.SPACE).append(Log.getStackTraceString(e));
        }

        public static void clear() {
            sContent.delete(0, sContent.length());
        }

        public static CharSequence get() {
            return sContent;
        }
    }

    static {
        mIsLogEnabled = false;
        sLastTimeStamp = 0;
        sLogFeedback = DebugSwitch.on(Key.APP_DEBUG_SEND_DIAGNOSTIC_REPORT);
        sDateFormatter = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("HH:mm:ss.SSS");
            }
        };
    }

    private static String getTag(String tag) {
        return String.format(":DOUBAN:%s:(%s):%s:", new Object[]{((SimpleDateFormat) sDateFormatter.get()).format(new Date(System.currentTimeMillis())), Thread.currentThread().getName(), tag});
    }

    private static String getMessage(String msg, Object... params) {
        if (params == null || params.length == 0) {
            return msg;
        }
        try {
            return String.format(msg, params);
        } catch (Throwable e) {
            StringBuilder builder = new StringBuilder(msg);
            for (Object param : params) {
                builder.append(", ").append(param);
            }
            builder.append("\n").append(Log.getStackTraceString(e));
            return builder.toString();
        }
    }

    private static String getMessage(Throwable e, String msg, Object... params) {
        return String.format("%s%n%s", new Object[]{getMessage(msg, params), Log.getStackTraceString(e)});
    }

    public static boolean isLogEnabled() {
        return mIsLogEnabled;
    }

    public static void setLogEnabled(boolean enabled) {
        mIsLogEnabled = enabled;
    }

    public static void c(String tag, String msg) {
        Crashlytics.log(String.format("%s | %s", new Object[]{getTag(tag), msg}));
        if (sLogFeedback) {
            Feedback.d(tag, msg);
        }
    }

    public static void c(String tag, String msg, Object... params) {
        String formattedTag = getTag(tag);
        Crashlytics.log(String.format("%s | %s", new Object[]{formattedTag, getMessage(msg, params)}));
        if (sLogFeedback) {
            Feedback.d(tag, formattedMessage);
        }
    }

    public static int i(String tag, String msg, Object... params) {
        return isLogEnabled() ? Log.i(getTag(tag), getMessage(msg, params)) : 0;
    }

    public static int d(String tag, String msg, Object... params) {
        return isLogEnabled() ? Log.d(getTag(tag), getMessage(msg, params)) : 0;
    }

    public static int dc(String tag, String msg, Object... params) {
        c(tag, msg, params);
        return d(tag, msg, params);
    }

    public static int v(String tag, String msg, Object... params) {
        return isLogEnabled() ? Log.v(getTag(tag), getMessage(msg, params)) : 0;
    }

    public static int e(String tag, String msg, Object... params) {
        return isLogEnabled() ? Log.e(getTag(tag), getMessage(msg, params)) : 0;
    }

    public static int w(String tag, String msg, Object... params) {
        return isLogEnabled() ? Log.w(getTag(tag), getMessage(msg, params)) : 0;
    }

    public static int e(String tag, Throwable e) {
        return isLogEnabled() ? Log.e(getTag(tag), Log.getStackTraceString(e)) : 0;
    }

    public static int e(String tag, Throwable e, String msg, Object... params) {
        return isLogEnabled() ? Log.e(getTag(tag), getMessage(e, msg, params)) : 0;
    }

    public static int ec(String tag, Throwable e) {
        Crashlytics.logException(e);
        if (sLogFeedback) {
            Feedback.e(tag, e);
        }
        return e(tag, e);
    }

    public static int ec(String tag, Throwable e, String msg, Object... params) {
        c(tag, msg, params);
        Crashlytics.logException(e);
        if (sLogFeedback) {
            Feedback.e(tag, e);
        }
        return e(tag, e, msg, params);
    }

    public static int t(String tag, String msg, Object... params) {
        if (!isLogEnabled()) {
            return 0;
        }
        long currentTimeStamp = System.nanoTime();
        float diffInMillis = ((float) (currentTimeStamp - sLastTimeStamp)) / 1000000.0f;
        int result = Log.d(getTag(tag), String.format("(+%.3fms) %s", new Object[]{Float.valueOf(diffInMillis), getMessage(msg, params)}));
        sLastTimeStamp = currentTimeStamp;
        return result;
    }

    public static void printStackTrace(String tag) {
        if (isLogEnabled()) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            d(tag, "\n---------------------", new Object[0]);
            for (int i = 3; i < stackTraceElements.length; i++) {
                StackTraceElement element = stackTraceElements[i];
                d(tag, "    " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")", new Object[0]);
            }
            d(tag, "---------------------\n", new Object[0]);
        }
    }
}
