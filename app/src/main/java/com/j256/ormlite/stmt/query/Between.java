package com.j256.ormlite.stmt.query;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.ArgumentHolder;
import java.sql.SQLException;
import java.util.List;

public class Between extends BaseComparison {
    private Object high;
    private Object low;

    public /* bridge */ /* synthetic */ void appendSql(DatabaseType x0, String x1, StringBuilder x2, List x3) throws SQLException {
        super.appendSql(x0, x1, x2, x3);
    }

    public /* bridge */ /* synthetic */ String getColumnName() {
        return super.getColumnName();
    }

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    public Between(String columnName, FieldType fieldType, Object low, Object high) throws SQLException {
        super(columnName, fieldType, null, true);
        this.low = low;
        this.high = high;
    }

    public void appendOperation(StringBuilder sb) {
        sb.append("BETWEEN ");
    }

    public void appendValue(DatabaseType databaseType, StringBuilder sb, List<ArgumentHolder> argList) throws SQLException {
        if (this.low == null) {
            throw new IllegalArgumentException("BETWEEN low value for '" + this.columnName + "' is null");
        } else if (this.high == null) {
            throw new IllegalArgumentException("BETWEEN high value for '" + this.columnName + "' is null");
        } else {
            appendArgOrValue(databaseType, this.fieldType, sb, argList, this.low);
            sb.append("AND ");
            appendArgOrValue(databaseType, this.fieldType, sb, argList, this.high);
        }
    }
}
