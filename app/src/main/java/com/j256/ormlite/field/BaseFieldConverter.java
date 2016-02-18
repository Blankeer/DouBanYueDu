package com.j256.ormlite.field;

import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;

public abstract class BaseFieldConverter implements FieldConverter {
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        return javaObject;
    }

    public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        Object value = resultToSqlArg(fieldType, results, columnPos);
        if (value == null) {
            return null;
        }
        return sqlArgToJava(fieldType, value, columnPos);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return sqlArg;
    }

    public boolean isStreamType() {
        return false;
    }
}
