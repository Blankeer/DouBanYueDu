package com.j256.ormlite.db;

import com.alipay.sdk.protocol.h;
import com.douban.book.reader.constant.Char;
import com.j256.ormlite.field.BaseFieldConverter;
import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.FieldConverter;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public abstract class BaseDatabaseType implements DatabaseType {
    protected static String DEFAULT_SEQUENCE_SUFFIX;
    protected Driver driver;

    /* renamed from: com.j256.ormlite.db.BaseDatabaseType.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$j256$ormlite$field$SqlType;

        static {
            $SwitchMap$com$j256$ormlite$field$SqlType = new int[SqlType.values().length];
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.LONG_STRING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BOOLEAN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.DATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.CHAR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BYTE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BYTE_ARRAY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.SHORT.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.INTEGER.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.LONG.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.FLOAT.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.DOUBLE.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.SERIALIZABLE.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BIG_DECIMAL.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.UNKNOWN.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
        }
    }

    protected static class BooleanNumberFieldConverter extends BaseFieldConverter {
        protected BooleanNumberFieldConverter() {
        }

        public SqlType getSqlType() {
            return SqlType.BOOLEAN;
        }

        public Object parseDefaultString(FieldType fieldType, String defaultStr) {
            return Boolean.parseBoolean(defaultStr) ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0);
        }

        public Object javaToSqlArg(FieldType fieldType, Object obj) {
            return ((Boolean) obj).booleanValue() ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0);
        }

        public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
            return Byte.valueOf(results.getByte(columnPos));
        }

        public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
            return ((Byte) sqlArg).byteValue() == (byte) 1 ? Boolean.valueOf(true) : Boolean.valueOf(false);
        }

        public Object resultStringToJava(FieldType fieldType, String stringValue, int columnPos) {
            return sqlArgToJava(fieldType, Byte.valueOf(Byte.parseByte(stringValue)), columnPos);
        }
    }

    protected abstract String getDriverClassName();

    static {
        DEFAULT_SEQUENCE_SUFFIX = "_id_seq";
    }

    public void loadDriver() throws SQLException {
        String className = getDriverClassName();
        if (className != null) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw SqlExceptionUtil.create("Driver class was not found for " + getDatabaseName() + " database.  Missing jar with class " + className + ".", e);
            }
        }
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void appendColumnArg(String tableName, StringBuilder sb, FieldType fieldType, List<String> additionalArgs, List<String> statementsBefore, List<String> statementsAfter, List<String> queriesAfter) throws SQLException {
        appendEscapedEntityName(sb, fieldType.getColumnName());
        sb.append(Char.SPACE);
        DataPersister dataPersister = fieldType.getDataPersister();
        int fieldWidth = fieldType.getWidth();
        if (fieldWidth == 0) {
            fieldWidth = dataPersister.getDefaultWidth();
        }
        switch (AnonymousClass1.$SwitchMap$com$j256$ormlite$field$SqlType[dataPersister.getSqlType().ordinal()]) {
            case dx.b /*1*/:
                appendStringType(sb, fieldType, fieldWidth);
                break;
            case dx.c /*2*/:
                appendLongStringType(sb, fieldType, fieldWidth);
                break;
            case dx.d /*3*/:
                appendBooleanType(sb, fieldType, fieldWidth);
                break;
            case dx.e /*4*/:
                appendDateType(sb, fieldType, fieldWidth);
                break;
            case dj.f /*5*/:
                appendCharType(sb, fieldType, fieldWidth);
                break;
            case ci.g /*6*/:
                appendByteType(sb, fieldType, fieldWidth);
                break;
            case ci.h /*7*/:
                appendByteArrayType(sb, fieldType, fieldWidth);
                break;
            case h.g /*8*/:
                appendShortType(sb, fieldType, fieldWidth);
                break;
            case h.h /*9*/:
                appendIntegerType(sb, fieldType, fieldWidth);
                break;
            case h.i /*10*/:
                appendLongType(sb, fieldType, fieldWidth);
                break;
            case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                appendFloatType(sb, fieldType, fieldWidth);
                break;
            case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                appendDoubleType(sb, fieldType, fieldWidth);
                break;
            case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                appendSerializableType(sb, fieldType, fieldWidth);
                break;
            case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                appendBigDecimalNumericType(sb, fieldType, fieldWidth);
                break;
            default:
                throw new IllegalArgumentException("Unknown SQL-type " + dataPersister.getSqlType());
        }
        sb.append(Char.SPACE);
        if (fieldType.isGeneratedIdSequence() && !fieldType.isSelfGeneratedId()) {
            configureGeneratedIdSequence(sb, fieldType, statementsBefore, additionalArgs, queriesAfter);
        } else if (fieldType.isGeneratedId() && !fieldType.isSelfGeneratedId()) {
            configureGeneratedId(tableName, sb, fieldType, statementsBefore, statementsAfter, additionalArgs, queriesAfter);
        } else if (fieldType.isId()) {
            configureId(sb, fieldType, statementsBefore, additionalArgs, queriesAfter);
        }
        if (!fieldType.isGeneratedId()) {
            Object defaultValue = fieldType.getDefaultValue();
            if (defaultValue != null) {
                sb.append("DEFAULT ");
                appendDefaultValue(sb, fieldType, defaultValue);
                sb.append(Char.SPACE);
            }
            if (fieldType.isCanBeNull()) {
                appendCanBeNull(sb, fieldType);
            } else {
                sb.append("NOT NULL ");
            }
            if (fieldType.isUnique()) {
                addSingleUnique(sb, fieldType, additionalArgs, statementsAfter);
            }
        }
    }

    protected void appendStringType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        if (isVarcharFieldWidthSupported()) {
            sb.append("VARCHAR(").append(fieldWidth).append(")");
        } else {
            sb.append("VARCHAR");
        }
    }

    protected void appendLongStringType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("TEXT");
    }

    protected void appendDateType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("TIMESTAMP");
    }

    protected void appendBooleanType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("BOOLEAN");
    }

    protected void appendCharType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("CHAR");
    }

    protected void appendByteType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("TINYINT");
    }

    protected void appendShortType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("SMALLINT");
    }

    private void appendIntegerType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("INTEGER");
    }

    protected void appendLongType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("BIGINT");
    }

    private void appendFloatType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("FLOAT");
    }

    private void appendDoubleType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("DOUBLE PRECISION");
    }

    protected void appendByteArrayType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("BLOB");
    }

    protected void appendSerializableType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("BLOB");
    }

    protected void appendBigDecimalNumericType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        sb.append("NUMERIC");
    }

    private void appendDefaultValue(StringBuilder sb, FieldType fieldType, Object defaultValue) {
        if (fieldType.isEscapedDefaultValue()) {
            appendEscapedWord(sb, defaultValue.toString());
        } else {
            sb.append(defaultValue);
        }
    }

    protected void configureGeneratedIdSequence(StringBuilder sb, FieldType fieldType, List<String> list, List<String> list2, List<String> list3) throws SQLException {
        throw new SQLException("GeneratedIdSequence is not supported by database " + getDatabaseName() + " for field " + fieldType);
    }

    protected void configureGeneratedId(String tableName, StringBuilder sb, FieldType fieldType, List<String> list, List<String> list2, List<String> list3, List<String> list4) {
        throw new IllegalStateException("GeneratedId is not supported by database " + getDatabaseName() + " for field " + fieldType);
    }

    protected void configureId(StringBuilder sb, FieldType fieldType, List<String> list, List<String> list2, List<String> list3) {
    }

    public void addPrimaryKeySql(FieldType[] fieldTypes, List<String> additionalArgs, List<String> list, List<String> list2, List<String> list3) {
        StringBuilder sb = null;
        for (FieldType fieldType : fieldTypes) {
            if ((!fieldType.isGeneratedId() || generatedIdSqlAtEnd() || fieldType.isSelfGeneratedId()) && fieldType.isId()) {
                if (sb == null) {
                    sb = new StringBuilder(48);
                    sb.append("PRIMARY KEY (");
                } else {
                    sb.append(',');
                }
                appendEscapedEntityName(sb, fieldType.getColumnName());
            }
        }
        if (sb != null) {
            sb.append(") ");
            additionalArgs.add(sb.toString());
        }
    }

    protected boolean generatedIdSqlAtEnd() {
        return true;
    }

    public void addUniqueComboSql(FieldType[] fieldTypes, List<String> additionalArgs, List<String> list, List<String> list2, List<String> list3) {
        StringBuilder sb = null;
        for (FieldType fieldType : fieldTypes) {
            if (fieldType.isUniqueCombo()) {
                if (sb == null) {
                    sb = new StringBuilder(48);
                    sb.append("UNIQUE (");
                } else {
                    sb.append(',');
                }
                appendEscapedEntityName(sb, fieldType.getColumnName());
            }
        }
        if (sb != null) {
            sb.append(") ");
            additionalArgs.add(sb.toString());
        }
    }

    public void dropColumnArg(FieldType fieldType, List<String> list, List<String> list2) {
    }

    public void appendEscapedWord(StringBuilder sb, String word) {
        sb.append('\'').append(word).append('\'');
    }

    public void appendEscapedEntityName(StringBuilder sb, String name) {
        sb.append('`').append(name).append('`');
    }

    public String generateIdSequenceName(String tableName, FieldType idFieldType) {
        String name = tableName + DEFAULT_SEQUENCE_SUFFIX;
        if (isEntityNamesMustBeUpCase()) {
            return name.toUpperCase();
        }
        return name;
    }

    public String getCommentLinePrefix() {
        return "-- ";
    }

    public FieldConverter getFieldConverter(DataPersister dataPersister) {
        return dataPersister;
    }

    public boolean isIdSequenceNeeded() {
        return false;
    }

    public boolean isVarcharFieldWidthSupported() {
        return true;
    }

    public boolean isLimitSqlSupported() {
        return true;
    }

    public boolean isOffsetSqlSupported() {
        return true;
    }

    public boolean isOffsetLimitArgument() {
        return false;
    }

    public boolean isLimitAfterSelect() {
        return false;
    }

    public void appendLimitValue(StringBuilder sb, long limit, Long offset) {
        sb.append("LIMIT ").append(limit).append(Char.SPACE);
    }

    public void appendOffsetValue(StringBuilder sb, long offset) {
        sb.append("OFFSET ").append(offset).append(Char.SPACE);
    }

    public void appendSelectNextValFromSequence(StringBuilder sb, String sequenceName) {
    }

    public void appendCreateTableSuffix(StringBuilder sb) {
    }

    public boolean isCreateTableReturnsZero() {
        return true;
    }

    public boolean isCreateTableReturnsNegative() {
        return false;
    }

    public boolean isEntityNamesMustBeUpCase() {
        return false;
    }

    public boolean isNestedSavePointsSupported() {
        return true;
    }

    public String getPingStatement() {
        return "SELECT 1";
    }

    public boolean isBatchUseTransaction() {
        return false;
    }

    public boolean isTruncateSupported() {
        return false;
    }

    public boolean isCreateIfNotExistsSupported() {
        return false;
    }

    public boolean isCreateIndexIfNotExistsSupported() {
        return isCreateIfNotExistsSupported();
    }

    public boolean isSelectSequenceBeforeInsert() {
        return false;
    }

    public boolean isAllowGeneratedIdInsertSupported() {
        return true;
    }

    public <T> DatabaseTableConfig<T> extractDatabaseTableConfig(ConnectionSource connectionSource, Class<T> cls) throws SQLException {
        return null;
    }

    public void appendInsertNoColumns(StringBuilder sb) {
        sb.append("() VALUES ()");
    }

    private void appendCanBeNull(StringBuilder sb, FieldType fieldType) {
    }

    private void addSingleUnique(StringBuilder sb, FieldType fieldType, List<String> additionalArgs, List<String> list) {
        StringBuilder alterSb = new StringBuilder();
        alterSb.append(" UNIQUE (");
        appendEscapedEntityName(alterSb, fieldType.getColumnName());
        alterSb.append(")");
        additionalArgs.add(alterSb.toString());
    }
}
