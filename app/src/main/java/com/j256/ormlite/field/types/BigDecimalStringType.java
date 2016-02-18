package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.math.BigDecimal;
import java.sql.SQLException;

public class BigDecimalStringType extends BaseDataType {
    public static int DEFAULT_WIDTH;
    private static final BigDecimalStringType singleTon;

    static {
        DEFAULT_WIDTH = SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
        singleTon = new BigDecimalStringType();
    }

    public static BigDecimalStringType getSingleton() {
        return singleTon;
    }

    private BigDecimalStringType() {
        super(SqlType.STRING, new Class[]{BigDecimal.class});
    }

    protected BigDecimalStringType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        try {
            return new BigDecimal(defaultStr);
        } catch (IllegalArgumentException e) {
            throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default BigDecimal string '" + defaultStr + "'", e);
        }
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        try {
            return new BigDecimal((String) sqlArg);
        } catch (IllegalArgumentException e) {
            throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing BigDecimal string '" + sqlArg + "'", e);
        }
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) {
        return ((BigDecimal) obj).toString();
    }

    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    public boolean isAppropriateId() {
        return false;
    }
}
