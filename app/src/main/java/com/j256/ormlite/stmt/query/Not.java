package com.j256.ormlite.stmt.query;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.stmt.ArgumentHolder;
import java.sql.SQLException;
import java.util.List;

public class Not implements Clause, NeedsFutureClause {
    private Comparison comparison;
    private Exists exists;

    public Not() {
        this.comparison = null;
        this.exists = null;
    }

    public Not(Clause clause) {
        this.comparison = null;
        this.exists = null;
        setMissingClause(clause);
    }

    public void setMissingClause(Clause clause) {
        if (this.comparison != null) {
            throw new IllegalArgumentException("NOT operation already has a comparison set");
        } else if (clause instanceof Comparison) {
            this.comparison = (Comparison) clause;
        } else if (clause instanceof Exists) {
            this.exists = (Exists) clause;
        } else {
            throw new IllegalArgumentException("NOT operation can only work with comparison SQL clauses, not " + clause);
        }
    }

    public void appendSql(DatabaseType databaseType, String tableName, StringBuilder sb, List<ArgumentHolder> selectArgList) throws SQLException {
        if (this.comparison == null && this.exists == null) {
            throw new IllegalStateException("Clause has not been set in NOT operation");
        }
        if (this.comparison == null) {
            sb.append("(NOT ");
            this.exists.appendSql(databaseType, tableName, sb, selectArgList);
        } else {
            sb.append("(NOT ");
            if (tableName != null) {
                databaseType.appendEscapedEntityName(sb, tableName);
                sb.append(Char.DOT);
            }
            databaseType.appendEscapedEntityName(sb, this.comparison.getColumnName());
            sb.append(Char.SPACE);
            this.comparison.appendOperation(sb);
            this.comparison.appendValue(databaseType, sb, selectArgList);
        }
        sb.append(") ");
    }

    public String toString() {
        if (this.comparison == null) {
            return "NOT without comparison";
        }
        return "NOT comparison " + this.comparison;
    }
}
