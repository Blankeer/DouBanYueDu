package io.realm.internal;

public interface TableSchema {
    long addColumn(ColumnType columnType, String str);

    TableSchema getSubtableSchema(long j);

    void removeColumn(long j);

    void renameColumn(long j, String str);
}
