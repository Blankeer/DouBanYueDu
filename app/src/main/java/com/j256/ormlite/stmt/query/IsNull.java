package com.j256.ormlite.stmt.query;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.ArgumentHolder;
import java.sql.SQLException;
import java.util.List;

public class IsNull extends BaseComparison {
    public /* bridge */ /* synthetic */ void appendSql(DatabaseType x0, String x1, StringBuilder x2, List x3) throws SQLException {
        super.appendSql(x0, x1, x2, x3);
    }

    public /* bridge */ /* synthetic */ String getColumnName() {
        return super.getColumnName();
    }

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    public IsNull(String columnName, FieldType fieldType) throws SQLException {
        super(columnName, fieldType, null, true);
    }

    public void appendOperation(StringBuilder sb) {
        sb.append("IS NULL ");
    }

    public void appendValue(DatabaseType databaseType, StringBuilder sb, List<ArgumentHolder> list) {
    }
}
