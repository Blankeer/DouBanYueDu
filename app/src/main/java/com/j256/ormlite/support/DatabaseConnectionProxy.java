package com.j256.ormlite.support;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import java.sql.SQLException;
import java.sql.Savepoint;

public class DatabaseConnectionProxy implements DatabaseConnection {
    private final DatabaseConnection proxy;

    public DatabaseConnectionProxy(DatabaseConnection proxy) {
        this.proxy = proxy;
    }

    public boolean isAutoCommitSupported() throws SQLException {
        if (this.proxy == null) {
            return false;
        }
        return this.proxy.isAutoCommitSupported();
    }

    public boolean isAutoCommit() throws SQLException {
        if (this.proxy == null) {
            return false;
        }
        return this.proxy.isAutoCommit();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (this.proxy != null) {
            this.proxy.setAutoCommit(autoCommit);
        }
    }

    public Savepoint setSavePoint(String name) throws SQLException {
        if (this.proxy == null) {
            return null;
        }
        return this.proxy.setSavePoint(name);
    }

    public void commit(Savepoint savePoint) throws SQLException {
        if (this.proxy != null) {
            this.proxy.commit(savePoint);
        }
    }

    public void rollback(Savepoint savePoint) throws SQLException {
        if (this.proxy != null) {
            this.proxy.rollback(savePoint);
        }
    }

    public int executeStatement(String statementStr, int resultFlags) throws SQLException {
        if (this.proxy == null) {
            return 0;
        }
        return this.proxy.executeStatement(statementStr, resultFlags);
    }

    public CompiledStatement compileStatement(String statement, StatementType type, FieldType[] argFieldTypes, int resultFlags) throws SQLException {
        if (this.proxy == null) {
            return null;
        }
        return this.proxy.compileStatement(statement, type, argFieldTypes, resultFlags);
    }

    public int insert(String statement, Object[] args, FieldType[] argfieldTypes, GeneratedKeyHolder keyHolder) throws SQLException {
        if (this.proxy == null) {
            return 0;
        }
        return this.proxy.insert(statement, args, argfieldTypes, keyHolder);
    }

    public int update(String statement, Object[] args, FieldType[] argfieldTypes) throws SQLException {
        if (this.proxy == null) {
            return 0;
        }
        return this.proxy.update(statement, args, argfieldTypes);
    }

    public int delete(String statement, Object[] args, FieldType[] argfieldTypes) throws SQLException {
        if (this.proxy == null) {
            return 0;
        }
        return this.proxy.delete(statement, args, argfieldTypes);
    }

    public <T> Object queryForOne(String statement, Object[] args, FieldType[] argfieldTypes, GenericRowMapper<T> rowMapper, ObjectCache objectCache) throws SQLException {
        if (this.proxy == null) {
            return null;
        }
        return this.proxy.queryForOne(statement, args, argfieldTypes, rowMapper, objectCache);
    }

    public long queryForLong(String statement) throws SQLException {
        if (this.proxy == null) {
            return 0;
        }
        return this.proxy.queryForLong(statement);
    }

    public long queryForLong(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        if (this.proxy == null) {
            return 0;
        }
        return this.proxy.queryForLong(statement, args, argFieldTypes);
    }

    public void close() throws SQLException {
        if (this.proxy != null) {
            this.proxy.close();
        }
    }

    public void closeQuietly() {
        if (this.proxy != null) {
            this.proxy.closeQuietly();
        }
    }

    public boolean isClosed() throws SQLException {
        if (this.proxy == null) {
            return true;
        }
        return this.proxy.isClosed();
    }

    public boolean isTableExists(String tableName) throws SQLException {
        if (this.proxy == null) {
            return false;
        }
        return this.proxy.isTableExists(tableName);
    }
}
