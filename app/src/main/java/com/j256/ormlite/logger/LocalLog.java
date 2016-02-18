package com.j256.ormlite.logger;

import android.support.v4.media.TransportMediator;
import com.douban.book.reader.constant.Char;
import com.j256.ormlite.logger.Log.Level;
import com.j256.ormlite.stmt.query.SimpleComparison;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class LocalLog implements Log {
    private static final Level DEFAULT_LEVEL;
    public static final String LOCAL_LOG_FILE_PROPERTY = "com.j256.ormlite.logger.file";
    public static final String LOCAL_LOG_LEVEL_PROPERTY = "com.j256.ormlite.logger.level";
    public static final String LOCAL_LOG_PROPERTIES_FILE = "/ormliteLocalLog.properties";
    private static final List<PatternLevel> classLevels;
    private static final ThreadLocal<DateFormat> dateFormatThreadLocal;
    private static PrintStream printStream;
    private final String className;
    private final Level level;

    private static class PatternLevel {
        Level level;
        Pattern pattern;

        public PatternLevel(Pattern pattern, Level level) {
            this.pattern = pattern;
            this.level = level;
        }
    }

    static {
        DEFAULT_LEVEL = Level.DEBUG;
        dateFormatThreadLocal = new ThreadLocal<DateFormat>() {
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
            }
        };
        classLevels = readLevelResourceFile(LocalLog.class.getResourceAsStream(LOCAL_LOG_PROPERTIES_FILE));
        openLogFile(System.getProperty(LOCAL_LOG_FILE_PROPERTY));
    }

    public LocalLog(String className) {
        this.className = LoggerFactory.getSimpleClassName(className);
        Level level = null;
        if (classLevels != null) {
            for (PatternLevel patternLevel : classLevels) {
                if (patternLevel.pattern.matcher(className).matches() && (level == null || patternLevel.level.ordinal() < level.ordinal())) {
                    level = patternLevel.level;
                }
            }
        }
        if (level == null) {
            String levelName = System.getProperty(LOCAL_LOG_LEVEL_PROPERTY);
            if (levelName == null) {
                level = DEFAULT_LEVEL;
            } else {
                try {
                    level = Level.valueOf(levelName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Level '" + levelName + "' was not found", e);
                }
            }
        }
        this.level = level;
    }

    public static void openLogFile(String logPath) {
        if (logPath == null) {
            printStream = System.out;
            return;
        }
        try {
            printStream = new PrintStream(new File(logPath));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Log file " + logPath + " was not found", e);
        }
    }

    public boolean isLevelEnabled(Level level) {
        return this.level.isEnabled(level);
    }

    public void log(Level level, String msg) {
        printMessage(level, msg, null);
    }

    public void log(Level level, String msg, Throwable throwable) {
        printMessage(level, msg, throwable);
    }

    void flush() {
        printStream.flush();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.List<com.j256.ormlite.logger.LocalLog.PatternLevel> readLevelResourceFile(java.io.InputStream r5) {
        /*
        r1 = 0;
        if (r5 == 0) goto L_0x000a;
    L_0x0003:
        r1 = configureClassLevels(r5);	 Catch:{ IOException -> 0x000b }
        r5.close();	 Catch:{ IOException -> 0x002f }
    L_0x000a:
        return r1;
    L_0x000b:
        r0 = move-exception;
        r2 = java.lang.System.err;	 Catch:{ all -> 0x002a }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x002a }
        r3.<init>();	 Catch:{ all -> 0x002a }
        r4 = "IO exception reading the log properties file '/ormliteLocalLog.properties': ";
        r3 = r3.append(r4);	 Catch:{ all -> 0x002a }
        r3 = r3.append(r0);	 Catch:{ all -> 0x002a }
        r3 = r3.toString();	 Catch:{ all -> 0x002a }
        r2.println(r3);	 Catch:{ all -> 0x002a }
        r5.close();	 Catch:{ IOException -> 0x0028 }
        goto L_0x000a;
    L_0x0028:
        r2 = move-exception;
        goto L_0x000a;
    L_0x002a:
        r2 = move-exception;
        r5.close();	 Catch:{ IOException -> 0x0031 }
    L_0x002e:
        throw r2;
    L_0x002f:
        r2 = move-exception;
        goto L_0x000a;
    L_0x0031:
        r3 = move-exception;
        goto L_0x002e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.j256.ormlite.logger.LocalLog.readLevelResourceFile(java.io.InputStream):java.util.List<com.j256.ormlite.logger.LocalLog$PatternLevel>");
    }

    private static List<PatternLevel> configureClassLevels(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        List<PatternLevel> list = new ArrayList();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return list;
            }
            if (!(line.length() == 0 || line.charAt(0) == '#')) {
                String[] parts = line.split(SimpleComparison.EQUAL_TO_OPERATION);
                if (parts.length != 2) {
                    System.err.println("Line is not in the format of 'pattern = level': " + line);
                } else {
                    try {
                        list.add(new PatternLevel(Pattern.compile(parts[0].trim()), Level.valueOf(parts[1].trim())));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Level '" + parts[1] + "' was not found");
                    }
                }
            }
        }
    }

    private void printMessage(Level level, String message, Throwable throwable) {
        if (isLevelEnabled(level)) {
            StringBuilder sb = new StringBuilder(TransportMediator.FLAG_KEY_MEDIA_NEXT);
            sb.append(((DateFormat) dateFormatThreadLocal.get()).format(new Date()));
            sb.append(" [").append(level.name()).append("] ");
            sb.append(this.className).append(Char.SPACE);
            sb.append(message);
            printStream.println(sb.toString());
            if (throwable != null) {
                throwable.printStackTrace(printStream);
            }
        }
    }
}
