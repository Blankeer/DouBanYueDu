package com.j256.ormlite.stmt.query;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.NullArgHolder;
import java.sql.SQLException;
import java.util.List;

public class SetValue extends BaseComparison {
    private static final ArgumentHolder nullValue;

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

    static {
        nullValue = new NullArgHolder();
    }

    public SetValue(String columnName, FieldType fieldType, Object value) throws SQLException {
        if (value == null) {
            value = nullValue;
        }
        super(columnName, fieldType, value, false);
    }

    public void appendOperation(StringBuilder sb) {
        sb.append("= ");
    }
}
