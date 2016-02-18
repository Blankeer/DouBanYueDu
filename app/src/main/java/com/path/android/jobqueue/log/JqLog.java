package com.path.android.jobqueue.log;

public class JqLog {
    private static CustomLogger customLogger;

    static {
        customLogger = new CustomLogger() {
            public boolean isDebugEnabled() {
                return false;
            }

            public void d(String text, Object... args) {
            }

            public void e(Throwable t, String text, Object... args) {
            }

            public void e(String text, Object... args) {
            }
        };
    }

    public static void setCustomLogger(CustomLogger customLogger) {
        customLogger = customLogger;
    }

    public static boolean isDebugEnabled() {
        return customLogger.isDebugEnabled();
    }

    public static void d(String text, Object... args) {
        customLogger.d(text, args);
    }

    public static void e(Throwable t, String text, Object... args) {
        customLogger.e(t, text, args);
    }

    public static void e(String text, Object... args) {
        customLogger.e(text, args);
    }
}
