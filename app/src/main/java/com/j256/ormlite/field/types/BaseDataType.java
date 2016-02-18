package com.j256.ormlite.field.types;

import com.j256.ormlite.field.BaseFieldConverter;
import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;
import java.lang.reflect.Field;
import java.sql.SQLException;

public abstract class BaseDataType extends BaseFieldConverter implements DataPersister {
    private final Class<?>[] classes;
    private final SqlType sqlType;

    public abstract Object parseDefaultString(FieldType fieldType, String str) throws SQLException;

    public abstract Object resultToSqlArg(FieldType fieldType, DatabaseResults databaseResults, int i) throws SQLException;

    public BaseDataType(SqlType sqlType, Class<?>[] classes) {
        this.sqlType = sqlType;
        this.classes = classes;
    }

    public boolean isValidForField(Field field) {
        if (this.classes.length == 0) {
            return true;
        }
        for (Class<?> clazz : this.classes) {
            if (clazz.isAssignableFrom(field.getType())) {
                return true;
            }
        }
        return false;
    }

    public Class<?> getPrimaryClass() {
        if (this.classes.length == 0) {
            return null;
        }
        return this.classes[0];
    }

    public Object makeConfigObject(FieldType fieldType) throws SQLException {
        return null;
    }

    public SqlType getSqlType() {
        return this.sqlType;
    }

    public Class<?>[] getAssociatedClasses() {
        return this.classes;
    }

    public String[] getAssociatedClassNames() {
        return null;
    }

    public Object convertIdNumber(Number number) {
        return null;
    }

    public boolean isValidGeneratedType() {
        return false;
    }

    public boolean isEscapedDefaultValue() {
        return isEscapedValue();
    }

    public boolean isEscapedValue() {
        return true;
    }

    public boolean isPrimitive() {
        return false;
    }

    public boolean isComparable() {
        return true;
    }

    public boolean isAppropriateId() {
        return true;
    }

    public boolean isArgumentHolderRequired() {
        return false;
    }

    public boolean isSelfGeneratedId() {
        return false;
    }

    public Object generateId() {
        throw new IllegalStateException("Should not have tried to generate this type");
    }

    public int getDefaultWidth() {
        return 0;
    }

    public boolean dataIsEqual(Object fieldObj1, Object fieldObj2) {
        if (fieldObj1 == null) {
            if (fieldObj2 == null) {
                return true;
            }
            return false;
        } else if (fieldObj2 != null) {
            return fieldObj1.equals(fieldObj2);
        } else {
            return false;
        }
    }

    public boolean isValidForVersion() {
        return false;
    }

    public Object moveToNextValue(Object currentValue) {
        return null;
    }

    public Object resultStringToJava(FieldType fieldType, String stringValue, int columnPos) throws SQLException {
        return parseDefaultString(fieldType, stringValue);
    }
}
