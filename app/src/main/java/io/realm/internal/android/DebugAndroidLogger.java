package io.realm.internal.android;

public class DebugAndroidLogger extends AndroidLogger {
    public DebugAndroidLogger() {
        setMinimumLogLevel(2);
    }
}
