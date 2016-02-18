package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.util.UUID;

public class UuidType extends BaseDataType {
    public static int DEFAULT_WIDTH;
    private static final UuidType singleTon;

    static {
        DEFAULT_WIDTH = 48;
        singleTon = new UuidType();
    }

    public static UuidType getSingleton() {
        return singleTon;
    }

    private UuidType() {
        super(SqlType.STRING, new Class[]{UUID.class});
    }

    protected UuidType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        try {
            return UUID.fromString(defaultStr);
        } catch (IllegalArgumentException e) {
            throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default UUID-string '" + defaultStr + "'", e);
        }
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        String uuidStr = (String) sqlArg;
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing UUID-string '" + uuidStr + "'", e);
        }
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) {
        return ((UUID) obj).toString();
    }

    public boolean isValidGeneratedType() {
        return true;
    }

    public boolean isSelfGeneratedId() {
        return true;
    }

    public Object generateId() {
        return UUID.randomUUID();
    }

    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }
}
