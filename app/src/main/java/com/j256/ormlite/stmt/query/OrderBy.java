package com.j256.ormlite.stmt.query;

public class OrderBy {
    private final boolean ascending;
    private final String columnName;

    public OrderBy(String columnName, boolean ascending) {
        this.columnName = columnName;
        this.ascending = ascending;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public boolean isAscending() {
        return this.ascending;
    }
}
