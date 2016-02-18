package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;

public class ByteObjectType extends BaseDataType {
    private static final ByteObjectType singleTon;

    static {
        singleTon = new ByteObjectType();
    }

    public static ByteObjectType getSingleton() {
        return singleTon;
    }

    private ByteObjectType() {
        super(SqlType.BYTE, new Class[]{Byte.class});
    }

    protected ByteObjectType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return Byte.valueOf(Byte.parseByte(defaultStr));
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return Byte.valueOf(results.getByte(columnPos));
    }

    public boolean isEscapedValue() {
        return false;
    }
}
