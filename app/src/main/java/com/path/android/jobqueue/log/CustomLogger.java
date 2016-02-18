package com.path.android.jobqueue.log;

public interface CustomLogger {
    void d(String str, Object... objArr);

    void e(String str, Object... objArr);

    void e(Throwable th, String str, Object... objArr);

    boolean isDebugEnabled();
}
