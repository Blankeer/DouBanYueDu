package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class DateTimeType extends BaseDataType {
    private static final String[] associatedClassNames;
    private static Class<?> dateTimeClass;
    private static Method getMillisMethod;
    private static Constructor<?> millisConstructor;
    private static final DateTimeType singleTon;

    static {
        singleTon = new DateTimeType();
        dateTimeClass = null;
        getMillisMethod = null;
        millisConstructor = null;
        associatedClassNames = new String[]{"org.joda.time.DateTime"};
    }

    private DateTimeType() {
        super(SqlType.LONG, new Class[0]);
    }

    protected DateTimeType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public static DateTimeType getSingleton() {
        return singleTon;
    }

    public String[] getAssociatedClassNames() {
        return associatedClassNames;
    }

    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        try {
            Method method = getMillisMethod();
            if (javaObject == null) {
                return null;
            }
            return method.invoke(javaObject, new Object[0]);
        } catch (Exception e) {
            throw SqlExceptionUtil.create("Could not use reflection to get millis from Joda DateTime: " + javaObject, e);
        }
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return Long.valueOf(Long.parseLong(defaultStr));
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return Long.valueOf(results.getLong(columnPos));
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        try {
            return getConstructor().newInstance(new Object[]{(Long) sqlArg});
        } catch (Exception e) {
            throw SqlExceptionUtil.create("Could not use reflection to construct a Joda DateTime", e);
        }
    }

    public boolean isEscapedValue() {
        return false;
    }

    public boolean isAppropriateId() {
        return false;
    }

    public Class<?> getPrimaryClass() {
        try {
            return getDateTimeClass();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Method getMillisMethod() throws Exception {
        if (getMillisMethod == null) {
            getMillisMethod = getDateTimeClass().getMethod("getMillis", new Class[0]);
        }
        return getMillisMethod;
    }

    private Constructor<?> getConstructor() throws Exception {
        if (millisConstructor == null) {
            millisConstructor = getDateTimeClass().getConstructor(new Class[]{Long.TYPE});
        }
        return millisConstructor;
    }

    private Class<?> getDateTimeClass() throws ClassNotFoundException {
        if (dateTimeClass == null) {
            dateTimeClass = Class.forName("org.joda.time.DateTime");
        }
        return dateTimeClass;
    }
}
