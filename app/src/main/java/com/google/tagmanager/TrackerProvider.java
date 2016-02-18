package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.Logger.LogLevel;
import io.realm.internal.Table;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

class TrackerProvider {
    private Context mContext;
    private GoogleAnalytics mGoogleAnalytics;

    /* renamed from: com.google.tagmanager.TrackerProvider.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$tagmanager$Logger$LogLevel;

        static {
            $SwitchMap$com$google$tagmanager$Logger$LogLevel = new int[LogLevel.values().length];
            try {
                $SwitchMap$com$google$tagmanager$Logger$LogLevel[LogLevel.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$tagmanager$Logger$LogLevel[LogLevel.ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$tagmanager$Logger$LogLevel[LogLevel.WARNING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$tagmanager$Logger$LogLevel[LogLevel.INFO.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$tagmanager$Logger$LogLevel[LogLevel.DEBUG.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$tagmanager$Logger$LogLevel[LogLevel.VERBOSE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    static class LoggerImpl implements Logger {
        LoggerImpl() {
        }

        public void error(String message) {
            Log.e(message);
        }

        public void error(Exception exception) {
            Log.e(Table.STRING_DEFAULT_VALUE, exception);
        }

        public void info(String message) {
            Log.i(message);
        }

        public void verbose(String message) {
            Log.v(message);
        }

        public void warn(String message) {
            Log.w(message);
        }

        public Logger.LogLevel getLogLevel() {
            LogLevel logLevel = Log.getLogLevel();
            return logLevel == null ? Logger.LogLevel.ERROR : toAnalyticsLogLevel(logLevel);
        }

        public void setLogLevel(Logger.LogLevel logLevel) {
            Log.w("GA uses GTM logger. Please use TagManager.getLogger().setLogLevel(LogLevel) instead.");
        }

        private static Logger.LogLevel toAnalyticsLogLevel(LogLevel logLevel) {
            switch (AnonymousClass1.$SwitchMap$com$google$tagmanager$Logger$LogLevel[logLevel.ordinal()]) {
                case dx.b /*1*/:
                case dx.c /*2*/:
                    return Logger.LogLevel.ERROR;
                case dx.d /*3*/:
                    return Logger.LogLevel.WARNING;
                case dx.e /*4*/:
                case dj.f /*5*/:
                    return Logger.LogLevel.INFO;
                case ci.g /*6*/:
                    return Logger.LogLevel.VERBOSE;
                default:
                    return Logger.LogLevel.ERROR;
            }
        }
    }

    TrackerProvider(Context context) {
        this.mContext = context;
    }

    @VisibleForTesting
    TrackerProvider(GoogleAnalytics ga) {
        this.mGoogleAnalytics = ga;
        this.mGoogleAnalytics.setLogger(new LoggerImpl());
    }

    public Tracker getTracker(String trackingId) {
        initTrackProviderIfNecessary();
        return this.mGoogleAnalytics.getTracker(trackingId);
    }

    public void close(Tracker tracker) {
        this.mGoogleAnalytics.closeTracker(tracker.getName());
    }

    private synchronized void initTrackProviderIfNecessary() {
        if (this.mGoogleAnalytics == null) {
            this.mGoogleAnalytics = GoogleAnalytics.getInstance(this.mContext);
            this.mGoogleAnalytics.setLogger(new LoggerImpl());
        }
    }
}
