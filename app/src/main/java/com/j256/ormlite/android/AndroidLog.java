package com.j256.ormlite.android;

import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Log.Level;
import com.j256.ormlite.logger.LoggerFactory;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class AndroidLog implements Log {
    private static final String ALL_LOGS_NAME = "ORMLite";
    private static final int MAX_TAG_LENGTH = 23;
    private static final int REFRESH_LEVEL_CACHE_EVERY = 200;
    private String className;
    private final boolean[] levelCache;
    private volatile int levelCacheC;

    /* renamed from: com.j256.ormlite.android.AndroidLog.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$j256$ormlite$logger$Log$Level;

        static {
            $SwitchMap$com$j256$ormlite$logger$Log$Level = new int[Level.values().length];
            try {
                $SwitchMap$com$j256$ormlite$logger$Log$Level[Level.TRACE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$j256$ormlite$logger$Log$Level[Level.DEBUG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$j256$ormlite$logger$Log$Level[Level.INFO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$j256$ormlite$logger$Log$Level[Level.WARNING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$j256$ormlite$logger$Log$Level[Level.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$j256$ormlite$logger$Log$Level[Level.FATAL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public AndroidLog(String className) {
        this.levelCacheC = 0;
        this.className = LoggerFactory.getSimpleClassName(className);
        int length = this.className.length();
        if (length > MAX_TAG_LENGTH) {
            this.className = this.className.substring(length - 23, length);
        }
        int maxLevel = 0;
        for (Level level : Level.values()) {
            int androidLevel = levelToAndroidLevel(level);
            if (androidLevel > maxLevel) {
                maxLevel = androidLevel;
            }
        }
        this.levelCache = new boolean[(maxLevel + 1)];
        refreshLevelCache();
    }

    public boolean isLevelEnabled(Level level) {
        int i = this.levelCacheC + 1;
        this.levelCacheC = i;
        if (i >= REFRESH_LEVEL_CACHE_EVERY) {
            refreshLevelCache();
            this.levelCacheC = 0;
        }
        int androidLevel = levelToAndroidLevel(level);
        if (androidLevel < this.levelCache.length) {
            return this.levelCache[androidLevel];
        }
        return isLevelEnabledInternal(androidLevel);
    }

    public void log(Level level, String msg) {
        switch (AnonymousClass1.$SwitchMap$com$j256$ormlite$logger$Log$Level[level.ordinal()]) {
            case dx.b /*1*/:
                android.util.Log.v(this.className, msg);
            case dx.c /*2*/:
                android.util.Log.d(this.className, msg);
            case dx.d /*3*/:
                android.util.Log.i(this.className, msg);
            case dx.e /*4*/:
                android.util.Log.w(this.className, msg);
            case dj.f /*5*/:
                android.util.Log.e(this.className, msg);
            case ci.g /*6*/:
                android.util.Log.e(this.className, msg);
            default:
                android.util.Log.i(this.className, msg);
        }
    }

    public void log(Level level, String msg, Throwable t) {
        switch (AnonymousClass1.$SwitchMap$com$j256$ormlite$logger$Log$Level[level.ordinal()]) {
            case dx.b /*1*/:
                android.util.Log.v(this.className, msg, t);
            case dx.c /*2*/:
                android.util.Log.d(this.className, msg, t);
            case dx.d /*3*/:
                android.util.Log.i(this.className, msg, t);
            case dx.e /*4*/:
                android.util.Log.w(this.className, msg, t);
            case dj.f /*5*/:
                android.util.Log.e(this.className, msg, t);
            case ci.g /*6*/:
                android.util.Log.e(this.className, msg, t);
            default:
                android.util.Log.i(this.className, msg, t);
        }
    }

    private void refreshLevelCache() {
        for (Level level : Level.values()) {
            int androidLevel = levelToAndroidLevel(level);
            if (androidLevel < this.levelCache.length) {
                this.levelCache[androidLevel] = isLevelEnabledInternal(androidLevel);
            }
        }
    }

    private boolean isLevelEnabledInternal(int androidLevel) {
        return android.util.Log.isLoggable(this.className, androidLevel) || android.util.Log.isLoggable(ALL_LOGS_NAME, androidLevel);
    }

    private int levelToAndroidLevel(Level level) {
        switch (AnonymousClass1.$SwitchMap$com$j256$ormlite$logger$Log$Level[level.ordinal()]) {
            case dx.b /*1*/:
                return 2;
            case dx.c /*2*/:
                return 3;
            case dx.e /*4*/:
                return 5;
            case dj.f /*5*/:
                return 6;
            case ci.g /*6*/:
                return 6;
            default:
                return 4;
        }
    }
}
