package com.j256.ormlite.stmt;

import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;
import java.sql.SQLException;

public interface PreparedStmt<T> extends GenericRowMapper<T> {
    CompiledStatement compile(DatabaseConnection databaseConnection, StatementType statementType) throws SQLException;

    CompiledStatement compile(DatabaseConnection databaseConnection, StatementType statementType, int i) throws SQLException;

    String getStatement() throws SQLException;

    StatementType getType();

    void setArgumentHolderValue(int i, Object obj) throws SQLException;
}
