package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.math.BigInteger;
import java.sql.SQLException;

public class BigIntegerType extends BaseDataType {
    public static int DEFAULT_WIDTH;
    private static final BigIntegerType singleTon;

    static {
        DEFAULT_WIDTH = SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
        singleTon = new BigIntegerType();
    }

    public static BigIntegerType getSingleton() {
        return singleTon;
    }

    protected BigIntegerType() {
        super(SqlType.STRING, new Class[]{BigInteger.class});
    }

    protected BigIntegerType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        try {
            return new BigInteger(defaultStr);
        } catch (IllegalArgumentException e) {
            throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default BigInteger string '" + defaultStr + "'", e);
        }
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        try {
            return new BigInteger((String) sqlArg);
        } catch (IllegalArgumentException e) {
            throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing BigInteger string '" + sqlArg + "'", e);
        }
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) {
        return ((BigInteger) obj).toString();
    }

    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    public boolean isAppropriateId() {
        return false;
    }
}
