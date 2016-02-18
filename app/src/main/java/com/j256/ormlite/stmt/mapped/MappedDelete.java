package com.j256.ormlite.stmt.mapped;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;

public class MappedDelete<T, ID> extends BaseMappedStatement<T, ID> {
    private MappedDelete(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes) {
        super(tableInfo, statement, argFieldTypes);
    }

    public static <T, ID> MappedDelete<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo) throws SQLException {
        FieldType idField = tableInfo.getIdField();
        if (idField == null) {
            throw new SQLException("Cannot delete from " + tableInfo.getDataClass() + " because it doesn't have an id field");
        }
        StringBuilder sb = new StringBuilder(64);
        BaseMappedStatement.appendTableName(databaseType, sb, "DELETE FROM ", tableInfo.getTableName());
        BaseMappedStatement.appendWhereFieldEq(databaseType, idField, sb, null);
        return new MappedDelete(tableInfo, sb.toString(), new FieldType[]{idField});
    }

    public int delete(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
        try {
            Object args = getFieldObjects(data);
            int rowC = databaseConnection.delete(this.statement, args, this.argFieldTypes);
            logger.debug("delete data with statement '{}' and {} args, changed {} rows", this.statement, Integer.valueOf(args.length), Integer.valueOf(rowC));
            if (args.length > 0) {
                logger.trace("delete arguments: {}", args);
            }
            if (rowC > 0 && objectCache != null) {
                objectCache.remove(this.clazz, this.idField.extractJavaFieldToSqlArgValue(data));
            }
            return rowC;
        } catch (SQLException e) {
            throw SqlExceptionUtil.create("Unable to run delete stmt on object " + data + ": " + this.statement, e);
        }
    }

    public int deleteById(DatabaseConnection databaseConnection, ID id, ObjectCache objectCache) throws SQLException {
        try {
            Object args = new Object[]{convertIdToFieldObject(id)};
            int rowC = databaseConnection.delete(this.statement, args, this.argFieldTypes);
            logger.debug("delete data with statement '{}' and {} args, changed {} rows", this.statement, Integer.valueOf(args.length), Integer.valueOf(rowC));
            if (args.length > 0) {
                logger.trace("delete arguments: {}", args);
            }
            if (rowC > 0 && objectCache != null) {
                objectCache.remove(this.clazz, id);
            }
            return rowC;
        } catch (SQLException e) {
            throw SqlExceptionUtil.create("Unable to run deleteById stmt on id " + id + ": " + this.statement, e);
        }
    }
}
