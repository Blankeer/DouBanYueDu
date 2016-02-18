package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.support.DatabaseResults;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.sql.SQLException;

public class StringType extends BaseDataType {
    public static int DEFAULT_WIDTH;
    private static final StringType singleTon;

    static {
        DEFAULT_WIDTH = SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
        singleTon = new StringType();
    }

    public static StringType getSingleton() {
        return singleTon;
    }

    private StringType() {
        super(SqlType.STRING, new Class[]{String.class});
    }

    protected StringType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) {
        return defaultStr;
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getString(columnPos);
    }

    public int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }
}
