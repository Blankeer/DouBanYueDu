package com.j256.ormlite.stmt.query;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.ArgumentHolder;
import java.sql.SQLException;
import java.util.List;

public class SetExpression extends BaseComparison {
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

    public SetExpression(String columnName, FieldType fieldType, String string) throws SQLException {
        super(columnName, fieldType, string, true);
    }

    public void appendOperation(StringBuilder sb) {
        sb.append("= ");
    }

    protected void appendArgOrValue(DatabaseType databaseType, FieldType fieldType, StringBuilder sb, List<ArgumentHolder> list, Object argOrValue) {
        sb.append(argOrValue).append(Char.SPACE);
    }
}
