package com.j256.ormlite.db;

import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.FieldConverter;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.DateStringType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.sql.SQLException;
import u.aly.dx;

public class SqliteAndroidDatabaseType extends BaseSqliteDatabaseType {

    /* renamed from: com.j256.ormlite.db.SqliteAndroidDatabaseType.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$j256$ormlite$field$SqlType;

        static {
            $SwitchMap$com$j256$ormlite$field$SqlType = new int[SqlType.values().length];
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.DATE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public void loadDriver() {
    }

    public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
        return true;
    }

    protected String getDriverClassName() {
        return null;
    }

    public String getDatabaseName() {
        return "Android SQLite";
    }

    protected void appendDateType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        appendStringType(sb, fieldType, fieldWidth);
    }

    protected void appendBooleanType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        appendShortType(sb, fieldType, fieldWidth);
    }

    public FieldConverter getFieldConverter(DataPersister dataPersister) {
        switch (AnonymousClass1.$SwitchMap$com$j256$ormlite$field$SqlType[dataPersister.getSqlType().ordinal()]) {
            case dx.b /*1*/:
                return DateStringType.getSingleton();
            default:
                return super.getFieldConverter(dataPersister);
        }
    }

    public boolean isNestedSavePointsSupported() {
        return false;
    }

    public boolean isBatchUseTransaction() {
        return true;
    }

    public <T> DatabaseTableConfig<T> extractDatabaseTableConfig(ConnectionSource connectionSource, Class<T> clazz) throws SQLException {
        return DatabaseTableConfigUtil.fromClass(connectionSource, clazz);
    }
}
