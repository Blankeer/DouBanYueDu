package io.realm.internal.android;

import android.util.Log;
import io.realm.internal.log.Logger;

public class AndroidLogger implements Logger {
    private static final int LOG_ENTRY_MAX_LENGTH = 4000;
    private String logTag;
    private int minimumLogLevel;

    public AndroidLogger() {
        this.minimumLogLevel = 2;
        this.logTag = "REALM";
    }

    public void setTag(String tag) {
        this.logTag = tag;
    }

    public void setMinimumLogLevel(int logLevel) {
        this.minimumLogLevel = logLevel;
    }

    private void log(int logLevel, String message, Throwable t) {
        if (logLevel >= this.minimumLogLevel) {
            if (message == null || message.length() == 0) {
                if (t != null) {
                    message = Log.getStackTraceString(t);
                } else {
                    return;
                }
            } else if (t != null) {
                message = message + "\n" + Log.getStackTraceString(t);
            }
            if (message.length() < LOG_ENTRY_MAX_LENGTH) {
                Log.println(logLevel, this.logTag, message);
            } else {
                logMessageIgnoringLimit(logLevel, this.logTag, message);
            }
        }
    }

    private void logMessageIgnoringLimit(int logLevel, String tag, String message) {
        while (message.length() != 0) {
            int nextNewLineIndex = message.indexOf(10);
            int chunkLength = Math.min(nextNewLineIndex != -1 ? nextNewLineIndex : message.length(), LOG_ENTRY_MAX_LENGTH);
            Log.println(logLevel, tag, message.substring(0, chunkLength));
            if (nextNewLineIndex == -1 || nextNewLineIndex != chunkLength) {
                message = message.substring(chunkLength);
            } else {
                message = message.substring(chunkLength + 1);
            }
        }
    }

    public void v(String message) {
        log(2, message, null);
    }

    public void v(String message, Throwable t) {
        log(2, message, t);
    }

    public void d(String message) {
        log(3, message, null);
    }

    public void d(String message, Throwable t) {
        log(3, message, t);
    }

    public void i(String message) {
        log(4, message, null);
    }

    public void i(String message, Throwable t) {
        log(4, message, t);
    }

    public void w(String message) {
        log(5, message, null);
    }

    public void w(String message, Throwable t) {
        log(5, message, t);
    }

    public void e(String message) {
        log(6, message, null);
    }

    public void e(String message, Throwable t) {
        log(6, message, t);
    }
}
