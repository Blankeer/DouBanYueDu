package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EnumStringType extends BaseEnumType {
    public static int DEFAULT_WIDTH;
    private static final EnumStringType singleTon;

    static {
        DEFAULT_WIDTH = 100;
        singleTon = new EnumStringType();
    }

    public static EnumStringType getSingleton() {
        return singleTon;
    }

    private EnumStringType() {
        super(SqlType.STRING, new Class[]{Enum.class});
    }

    protected EnumStringType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        if (fieldType == null) {
            return sqlArg;
        }
        String value = (String) sqlArg;
        Map<String, Enum<?>> enumStringMap = (Map) fieldType.getDataTypeConfigObj();
        if (enumStringMap == null) {
            return BaseEnumType.enumVal(fieldType, value, null, fieldType.getUnknownEnumVal());
        }
        return BaseEnumType.enumVal(fieldType, value, (Enum) enumStringMap.get(value), fieldType.getUnknownEnumVal());
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return defaultStr;
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) {
        return ((Enum) obj).name();
    }

    public Object makeConfigObject(FieldType fieldType) throws SQLException {
        Map<String, Enum<?>> enumStringMap = new HashMap();
        Enum[] constants = (Enum[]) ((Enum[]) fieldType.getType().getEnumConstants());
        if (constants == null) {
            throw new SQLException("Field " + fieldType + " improperly configured as type " + this);
        }
        for (Enum<?> enumVal : constants) {
            enumStringMap.put(enumVal.name(), enumVal);
        }
        return enumStringMap;
    }

    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    public Object resultStringToJava(FieldType fieldType, String stringValue, int columnPos) throws SQLException {
        return sqlArgToJava(fieldType, stringValue, columnPos);
    }
}
