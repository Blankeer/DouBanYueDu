package com.j256.ormlite.stmt;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.stmt.query.Clause;
import com.j256.ormlite.stmt.query.SetExpression;
import com.j256.ormlite.stmt.query.SetValue;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder<T, ID> extends StatementBuilder<T, ID> {
    private List<Clause> updateClauseList;

    public UpdateBuilder(DatabaseType databaseType, TableInfo<T, ID> tableInfo, Dao<T, ID> dao) {
        super(databaseType, tableInfo, dao, StatementType.UPDATE);
        this.updateClauseList = null;
    }

    public PreparedUpdate<T> prepare() throws SQLException {
        return super.prepareStatement(null);
    }

    public StatementBuilder<T, ID> updateColumnValue(String columnName, Object value) throws SQLException {
        FieldType fieldType = verifyColumnName(columnName);
        if (fieldType.isForeignCollection()) {
            throw new SQLException("Can't update foreign colletion field: " + columnName);
        }
        addUpdateColumnToList(columnName, new SetValue(columnName, fieldType, value));
        return this;
    }

    public StatementBuilder<T, ID> updateColumnExpression(String columnName, String expression) throws SQLException {
        FieldType fieldType = verifyColumnName(columnName);
        if (fieldType.isForeignCollection()) {
            throw new SQLException("Can't update foreign colletion field: " + columnName);
        }
        addUpdateColumnToList(columnName, new SetExpression(columnName, fieldType, expression));
        return this;
    }

    public void escapeColumnName(StringBuilder sb, String columnName) {
        this.databaseType.appendEscapedEntityName(sb, columnName);
    }

    public String escapeColumnName(String columnName) {
        StringBuilder sb = new StringBuilder(columnName.length() + 4);
        this.databaseType.appendEscapedEntityName(sb, columnName);
        return sb.toString();
    }

    public void escapeValue(StringBuilder sb, String value) {
        this.databaseType.appendEscapedWord(sb, value);
    }

    public String escapeValue(String value) {
        StringBuilder sb = new StringBuilder(value.length() + 4);
        this.databaseType.appendEscapedWord(sb, value);
        return sb.toString();
    }

    public int update() throws SQLException {
        return this.dao.update(prepare());
    }

    @Deprecated
    public void clear() {
        reset();
    }

    public void reset() {
        super.reset();
        this.updateClauseList = null;
    }

    protected void appendStatementStart(StringBuilder sb, List<ArgumentHolder> argList) throws SQLException {
        if (this.updateClauseList == null || this.updateClauseList.isEmpty()) {
            throw new IllegalArgumentException("UPDATE statements must have at least one SET column");
        }
        sb.append("UPDATE ");
        this.databaseType.appendEscapedEntityName(sb, this.tableInfo.getTableName());
        sb.append(" SET ");
        boolean first = true;
        for (Clause clause : this.updateClauseList) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            clause.appendSql(this.databaseType, null, sb, argList);
        }
    }

    protected void appendStatementEnd(StringBuilder sb, List<ArgumentHolder> list) {
    }

    private void addUpdateColumnToList(String columnName, Clause clause) {
        if (this.updateClauseList == null) {
            this.updateClauseList = new ArrayList();
        }
        this.updateClauseList.add(clause);
    }
}
