package com.j256.ormlite.stmt.mapped;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;

public class MappedQueryForId<T, ID> extends BaseMappedQuery<T, ID> {
    private final String label;

    protected MappedQueryForId(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes, FieldType[] resultsFieldTypes, String label) {
        super(tableInfo, statement, argFieldTypes, resultsFieldTypes);
        this.label = label;
    }

    public T execute(DatabaseConnection databaseConnection, ID id, ObjectCache objectCache) throws SQLException {
        if (objectCache != null) {
            T result = objectCache.get(this.clazz, id);
            if (result != null) {
                return result;
            }
        }
        Object[] args = new Object[]{convertIdToFieldObject(id)};
        T result2 = databaseConnection.queryForOne(this.statement, args, this.argFieldTypes, this, objectCache);
        if (result2 == null) {
            logger.debug("{} using '{}' and {} args, got no results", this.label, this.statement, Integer.valueOf(args.length));
        } else if (result2 == DatabaseConnection.MORE_THAN_ONE) {
            logger.error("{} using '{}' and {} args, got >1 results", this.label, this.statement, Integer.valueOf(args.length));
            logArgs(args);
            throw new SQLException(this.label + " got more than 1 result: " + this.statement);
        } else {
            logger.debug("{} using '{}' and {} args, got 1 result", this.label, this.statement, Integer.valueOf(args.length));
        }
        logArgs(args);
        return result2;
    }

    public static <T, ID> MappedQueryForId<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo, FieldType idFieldType) throws SQLException {
        if (idFieldType == null) {
            idFieldType = tableInfo.getIdField();
            if (idFieldType == null) {
                throw new SQLException("Cannot query-for-id with " + tableInfo.getDataClass() + " because it doesn't have an id field");
            }
        }
        return new MappedQueryForId(tableInfo, buildStatement(databaseType, tableInfo, idFieldType), new FieldType[]{idFieldType}, tableInfo.getFieldTypes(), "query-for-id");
    }

    protected static <T, ID> String buildStatement(DatabaseType databaseType, TableInfo<T, ID> tableInfo, FieldType idFieldType) {
        StringBuilder sb = new StringBuilder(64);
        BaseMappedStatement.appendTableName(databaseType, sb, "SELECT * FROM ", tableInfo.getTableName());
        BaseMappedStatement.appendWhereFieldEq(databaseType, idFieldType, sb, null);
        return sb.toString();
    }

    private void logArgs(Object[] args) {
        if (args.length > 0) {
            logger.trace("{} arguments: {}", this.label, (Object) args);
        }
    }
}
