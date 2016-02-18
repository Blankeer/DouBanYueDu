package com.j256.ormlite.stmt.query;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.QueryBuilder.InternalQueryBuilderWrapper;
import java.sql.SQLException;
import java.util.List;

public class Exists implements Clause {
    private final InternalQueryBuilderWrapper subQueryBuilder;

    public Exists(InternalQueryBuilderWrapper subQueryBuilder) {
        this.subQueryBuilder = subQueryBuilder;
    }

    public void appendSql(DatabaseType databaseType, String tableName, StringBuilder sb, List<ArgumentHolder> argList) throws SQLException {
        sb.append("EXISTS (");
        this.subQueryBuilder.appendStatementString(sb, argList);
        sb.append(") ");
    }
}
