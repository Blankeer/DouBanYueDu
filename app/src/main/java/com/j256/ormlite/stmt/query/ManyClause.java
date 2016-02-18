package com.j256.ormlite.stmt.query;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.stmt.ArgumentHolder;
import java.sql.SQLException;
import java.util.List;

public class ManyClause implements Clause, NeedsFutureClause {
    public static final String AND_OPERATION = "AND";
    public static final String OR_OPERATION = "OR";
    private final Clause first;
    private final String operation;
    private final Clause[] others;
    private Clause second;
    private final int startOthersAt;

    public ManyClause(Clause first, String operation) {
        this.first = first;
        this.second = null;
        this.others = null;
        this.startOthersAt = 0;
        this.operation = operation;
    }

    public ManyClause(Clause first, Clause second, Clause[] others, String operation) {
        this.first = first;
        this.second = second;
        this.others = others;
        this.startOthersAt = 0;
        this.operation = operation;
    }

    public ManyClause(Clause[] others, String operation) {
        this.first = others[0];
        if (others.length < 2) {
            this.second = null;
            this.startOthersAt = others.length;
        } else {
            this.second = others[1];
            this.startOthersAt = 2;
        }
        this.others = others;
        this.operation = operation;
    }

    public void appendSql(DatabaseType databaseType, String tableName, StringBuilder sb, List<ArgumentHolder> selectArgList) throws SQLException {
        sb.append("(");
        this.first.appendSql(databaseType, tableName, sb, selectArgList);
        if (this.second != null) {
            sb.append(this.operation);
            sb.append(Char.SPACE);
            this.second.appendSql(databaseType, tableName, sb, selectArgList);
        }
        if (this.others != null) {
            for (int i = this.startOthersAt; i < this.others.length; i++) {
                sb.append(this.operation);
                sb.append(Char.SPACE);
                this.others[i].appendSql(databaseType, tableName, sb, selectArgList);
            }
        }
        sb.append(") ");
    }

    public void setMissingClause(Clause right) {
        this.second = right;
    }
}
