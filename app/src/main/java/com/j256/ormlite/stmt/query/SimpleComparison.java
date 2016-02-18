package com.j256.ormlite.stmt.query;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import java.sql.SQLException;
import java.util.List;

public class SimpleComparison extends BaseComparison {
    public static final String EQUAL_TO_OPERATION = "=";
    public static final String GREATER_THAN_EQUAL_TO_OPERATION = ">=";
    public static final String GREATER_THAN_OPERATION = ">";
    public static final String LESS_THAN_EQUAL_TO_OPERATION = "<=";
    public static final String LESS_THAN_OPERATION = "<";
    public static final String LIKE_OPERATION = "LIKE";
    public static final String NOT_EQUAL_TO_OPERATION = "<>";
    private final String operation;

    public /* bridge */ /* synthetic */ void appendSql(DatabaseType x0, String x1, StringBuilder x2, List x3) throws SQLException {
        super.appendSql(x0, x1, x2, x3);
    }

    public /* bridge */ /* synthetic */ void appendValue(DatabaseType x0, StringBuilder x1, List x2) throws SQLException {
        super.appendValue(x0, x1, x2);
    }

    public /* bridge */ /* synthetic */ String getColumnName() {
        return super.getColumnName();
    }

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    public SimpleComparison(String columnName, FieldType fieldType, Object value, String operation) throws SQLException {
        super(columnName, fieldType, value, true);
        this.operation = operation;
    }

    public void appendOperation(StringBuilder sb) {
        sb.append(this.operation);
        sb.append(Char.SPACE);
    }
}
