package com.j256.ormlite.logger;

import com.j256.ormlite.logger.Log.Level;

public class LoggerFactory {
    public static final String LOG_TYPE_SYSTEM_PROPERTY = "com.j256.ormlite.logger.type";
    private static LogType logType;

    public enum LogType {
        ANDROID("android.util.Log", "com.j256.ormlite.android.AndroidLog"),
        SLF4J("org.slf4j.LoggerFactory", "com.j256.ormlite.logger.Slf4jLoggingLog"),
        COMMONS_LOGGING("org.apache.commons.logging.LogFactory", "com.j256.ormlite.logger.CommonsLoggingLog"),
        LOG4J2("org.apache.logging.log4j.LogManager", "com.j256.ormlite.logger.Log4j2Log"),
        LOG4J("org.apache.log4j.Logger", "com.j256.ormlite.logger.Log4jLog"),
        LOCAL(LocalLog.class.getName(), LocalLog.class.getName()) {
            public Log createLog(String classLabel) {
                return new LocalLog(classLabel);
            }

            public boolean isAvailable() {
                return true;
            }
        };
        
        private final String detectClassName;
        private final String logClassName;

        private LogType(String detectClassName, String logClassName) {
            this.detectClassName = detectClassName;
            this.logClassName = logClassName;
        }

        public Log createLog(String classLabel) {
            try {
                return createLogFromClassName(classLabel);
            } catch (Exception e) {
                Log log = new LocalLog(classLabel);
                log.log(Level.WARNING, "Unable to call constructor with single String argument for class " + this.logClassName + ", so had to use local log: " + e.getMessage());
                return log;
            }
        }

        public boolean isAvailable() {
            if (!isAvailableTestClass()) {
                return false;
            }
            try {
                createLogFromClassName(getClass().getName()).isLevelEnabled(Level.INFO);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        Log createLogFromClassName(String classLabel) throws Exception {
            return (Log) Class.forName(this.logClassName).getConstructor(new Class[]{String.class}).newInstance(new Object[]{classLabel});
        }

        boolean isAvailableTestClass() {
            try {
                Class.forName(this.detectClassName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private LoggerFactory() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String className) {
        if (logType == null) {
            logType = findLogType();
        }
        return new Logger(logType.createLog(className));
    }

    public static String getSimpleClassName(String className) {
        String[] parts = className.split("\\.");
        return parts.length <= 1 ? className : parts[parts.length - 1];
    }

    private static LogType findLogType() {
        String logTypeString = System.getProperty(LOG_TYPE_SYSTEM_PROPERTY);
        if (logTypeString != null) {
            try {
                return LogType.valueOf(logTypeString);
            } catch (IllegalArgumentException e) {
                new LocalLog(LoggerFactory.class.getName()).log(Level.WARNING, "Could not find valid log-type from system property 'com.j256.ormlite.logger.type', value '" + logTypeString + "'");
            }
        }
        for (LogType logType : LogType.values()) {
            if (logType.isAvailable()) {
                return logType;
            }
        }
        return LogType.LOCAL;
    }
}
