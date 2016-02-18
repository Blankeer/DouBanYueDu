package com.j256.ormlite.stmt.query;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.stmt.ArgumentHolder;
import java.util.List;

public class Raw implements Clause {
    private final ArgumentHolder[] args;
    private final String statement;

    public Raw(String statement, ArgumentHolder[] args) {
        this.statement = statement;
        this.args = args;
    }

    public void appendSql(DatabaseType databaseType, String tableName, StringBuilder sb, List<ArgumentHolder> argList) {
        sb.append(this.statement);
        sb.append(Char.SPACE);
        for (ArgumentHolder arg : this.args) {
            argList.add(arg);
        }
    }
}
