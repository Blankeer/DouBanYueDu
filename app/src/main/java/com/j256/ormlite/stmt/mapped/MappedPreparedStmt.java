package com.j256.ormlite.stmt.mapped;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.logger.Log.Level;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;

public class MappedPreparedStmt<T, ID> extends BaseMappedQuery<T, ID> implements PreparedQuery<T>, PreparedDelete<T>, PreparedUpdate<T> {
    private final ArgumentHolder[] argHolders;
    private final Long limit;
    private final StatementType type;

    public MappedPreparedStmt(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes, FieldType[] resultFieldTypes, ArgumentHolder[] argHolders, Long limit, StatementType type) {
        super(tableInfo, statement, argFieldTypes, resultFieldTypes);
        this.argHolders = argHolders;
        this.limit = limit;
        this.type = type;
    }

    public CompiledStatement compile(DatabaseConnection databaseConnection, StatementType type) throws SQLException {
        return compile(databaseConnection, type, -1);
    }

    public CompiledStatement compile(DatabaseConnection databaseConnection, StatementType type, int resultFlags) throws SQLException {
        if (this.type == type) {
            return assignStatementArguments(databaseConnection.compileStatement(this.statement, type, this.argFieldTypes, resultFlags));
        }
        throw new SQLException("Could not compile this " + this.type + " statement since the caller is expecting a " + type + " statement.  Check your QueryBuilder methods.");
    }

    public String getStatement() {
        return this.statement;
    }

    public StatementType getType() {
        return this.type;
    }

    public void setArgumentHolderValue(int index, Object value) throws SQLException {
        if (index < 0) {
            throw new SQLException("argument holder index " + index + " must be >= 0");
        } else if (this.argHolders.length <= index) {
            throw new SQLException("argument holder index " + index + " is not valid, only " + this.argHolders.length + " in statement (index starts at 0)");
        } else {
            this.argHolders[index].setValue(value);
        }
    }

    private CompiledStatement assignStatementArguments(CompiledStatement stmt) throws SQLException {
        boolean ok = false;
        try {
            if (this.limit != null) {
                stmt.setMaxRows(this.limit.intValue());
            }
            Object argValues = null;
            if (logger.isLevelEnabled(Level.TRACE) && this.argHolders.length > 0) {
                argValues = new Object[this.argHolders.length];
            }
            for (int i = 0; i < this.argHolders.length; i++) {
                SqlType sqlType;
                Object argValue = this.argHolders[i].getSqlArgValue();
                FieldType fieldType = this.argFieldTypes[i];
                if (fieldType == null) {
                    sqlType = this.argHolders[i].getSqlType();
                } else {
                    sqlType = fieldType.getSqlType();
                }
                stmt.setObject(i, argValue, sqlType);
                if (argValues != null) {
                    argValues[i] = argValue;
                }
            }
            logger.debug("prepared statement '{}' with {} args", this.statement, Integer.valueOf(this.argHolders.length));
            if (argValues != null) {
                logger.trace("prepared statement arguments: {}", argValues);
            }
            ok = true;
            return stmt;
        } finally {
            if (!ok) {
                stmt.close();
            }
        }
    }
}
