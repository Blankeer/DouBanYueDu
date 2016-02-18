package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class DateStringType extends BaseDateType {
    public static int DEFAULT_WIDTH;
    private static final DateStringType singleTon;

    static {
        DEFAULT_WIDTH = 50;
        singleTon = new DateStringType();
    }

    public static DateStringType getSingleton() {
        return singleTon;
    }

    private DateStringType() {
        super(SqlType.STRING, new Class[0]);
    }

    protected DateStringType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        DateStringFormatConfig formatConfig = BaseDateType.convertDateStringConfig(fieldType, defaultDateFormatConfig);
        try {
            return BaseDateType.normalizeDateString(formatConfig, defaultStr);
        } catch (ParseException e) {
            throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default date-string '" + defaultStr + "' using '" + formatConfig + "'", e);
        }
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        String value = (String) sqlArg;
        DateStringFormatConfig formatConfig = BaseDateType.convertDateStringConfig(fieldType, defaultDateFormatConfig);
        try {
            return BaseDateType.parseDateString(formatConfig, value);
        } catch (ParseException e) {
            throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing date-string '" + value + "' using '" + formatConfig + "'", e);
        }
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) {
        return BaseDateType.convertDateStringConfig(fieldType, defaultDateFormatConfig).getDateFormat().format((Date) obj);
    }

    public Object makeConfigObject(FieldType fieldType) {
        String format = fieldType.getFormat();
        if (format == null) {
            return defaultDateFormatConfig;
        }
        return new DateStringFormatConfig(format);
    }

    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    public Object resultStringToJava(FieldType fieldType, String stringValue, int columnPos) throws SQLException {
        return sqlArgToJava(fieldType, stringValue, columnPos);
    }

    public Class<?> getPrimaryClass() {
        return byte[].class;
    }
}
