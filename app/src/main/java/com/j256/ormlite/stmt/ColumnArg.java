package com.j256.ormlite.stmt;

public class ColumnArg {
    private final String columnName;
    private final String tableName;

    public ColumnArg(String columnName) {
        this.tableName = null;
        this.columnName = columnName;
    }

    public ColumnArg(String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }
}
