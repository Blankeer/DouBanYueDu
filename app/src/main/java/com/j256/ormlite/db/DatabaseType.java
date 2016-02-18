package com.j256.ormlite.db;

import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.FieldConverter;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseType {
    void addPrimaryKeySql(FieldType[] fieldTypeArr, List<String> list, List<String> list2, List<String> list3, List<String> list4) throws SQLException;

    void addUniqueComboSql(FieldType[] fieldTypeArr, List<String> list, List<String> list2, List<String> list3, List<String> list4) throws SQLException;

    void appendColumnArg(String str, StringBuilder stringBuilder, FieldType fieldType, List<String> list, List<String> list2, List<String> list3, List<String> list4) throws SQLException;

    void appendCreateTableSuffix(StringBuilder stringBuilder);

    void appendEscapedEntityName(StringBuilder stringBuilder, String str);

    void appendEscapedWord(StringBuilder stringBuilder, String str);

    void appendInsertNoColumns(StringBuilder stringBuilder);

    void appendLimitValue(StringBuilder stringBuilder, long j, Long l);

    void appendOffsetValue(StringBuilder stringBuilder, long j);

    void appendSelectNextValFromSequence(StringBuilder stringBuilder, String str);

    void dropColumnArg(FieldType fieldType, List<String> list, List<String> list2);

    <T> DatabaseTableConfig<T> extractDatabaseTableConfig(ConnectionSource connectionSource, Class<T> cls) throws SQLException;

    String generateIdSequenceName(String str, FieldType fieldType);

    String getCommentLinePrefix();

    String getDatabaseName();

    FieldConverter getFieldConverter(DataPersister dataPersister);

    String getPingStatement();

    boolean isAllowGeneratedIdInsertSupported();

    boolean isBatchUseTransaction();

    boolean isCreateIfNotExistsSupported();

    boolean isCreateIndexIfNotExistsSupported();

    boolean isCreateTableReturnsNegative();

    boolean isCreateTableReturnsZero();

    boolean isDatabaseUrlThisType(String str, String str2);

    boolean isEntityNamesMustBeUpCase();

    boolean isIdSequenceNeeded();

    boolean isLimitAfterSelect();

    boolean isLimitSqlSupported();

    boolean isNestedSavePointsSupported();

    boolean isOffsetLimitArgument();

    boolean isOffsetSqlSupported();

    boolean isSelectSequenceBeforeInsert();

    boolean isTruncateSupported();

    boolean isVarcharFieldWidthSupported();

    void loadDriver() throws SQLException;

    void setDriver(Driver driver);
}
