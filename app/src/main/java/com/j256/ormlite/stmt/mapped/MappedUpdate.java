package com.j256.ormlite.stmt.mapped;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;

public class MappedUpdate<T, ID> extends BaseMappedStatement<T, ID> {
    private final FieldType versionFieldType;
    private final int versionFieldTypeIndex;

    private MappedUpdate(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes, FieldType versionFieldType, int versionFieldTypeIndex) {
        super(tableInfo, statement, argFieldTypes);
        this.versionFieldType = versionFieldType;
        this.versionFieldTypeIndex = versionFieldTypeIndex;
    }

    public static <T, ID> MappedUpdate<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo) throws SQLException {
        FieldType idField = tableInfo.getIdField();
        if (idField == null) {
            throw new SQLException("Cannot update " + tableInfo.getDataClass() + " because it doesn't have an id field");
        }
        StringBuilder sb = new StringBuilder(64);
        BaseMappedStatement.appendTableName(databaseType, sb, "UPDATE ", tableInfo.getTableName());
        boolean first = true;
        int argFieldC = 0;
        FieldType versionFieldType = null;
        int versionFieldTypeIndex = -1;
        for (FieldType fieldType : tableInfo.getFieldTypes()) {
            FieldType fieldType2;
            if (isFieldUpdatable(fieldType2, idField)) {
                if (fieldType2.isVersion()) {
                    versionFieldType = fieldType2;
                    versionFieldTypeIndex = argFieldC;
                }
                argFieldC++;
            }
        }
        argFieldC++;
        if (versionFieldType != null) {
            argFieldC++;
        }
        FieldType[] argFieldTypes = new FieldType[argFieldC];
        FieldType[] arr$ = tableInfo.getFieldTypes();
        int len$ = arr$.length;
        int i$ = 0;
        int argFieldC2 = 0;
        while (i$ < len$) {
            fieldType2 = arr$[i$];
            if (isFieldUpdatable(fieldType2, idField)) {
                if (first) {
                    sb.append("SET ");
                    first = false;
                } else {
                    sb.append(", ");
                }
                BaseMappedStatement.appendFieldColumnName(databaseType, sb, fieldType2, null);
                argFieldC = argFieldC2 + 1;
                argFieldTypes[argFieldC2] = fieldType2;
                sb.append("= ?");
            } else {
                argFieldC = argFieldC2;
            }
            i$++;
            argFieldC2 = argFieldC;
        }
        sb.append(Char.SPACE);
        BaseMappedStatement.appendWhereFieldEq(databaseType, idField, sb, null);
        argFieldC = argFieldC2 + 1;
        argFieldTypes[argFieldC2] = idField;
        if (versionFieldType != null) {
            sb.append(" AND ");
            BaseMappedStatement.appendFieldColumnName(databaseType, sb, versionFieldType, null);
            sb.append("= ?");
            argFieldC2 = argFieldC + 1;
            argFieldTypes[argFieldC] = versionFieldType;
            argFieldC = argFieldC2;
        }
        return new MappedUpdate(tableInfo, sb.toString(), argFieldTypes, versionFieldType, versionFieldTypeIndex);
    }

    public int update(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
        try {
            if (this.argFieldTypes.length <= 1) {
                return 0;
            }
            Object args = getFieldObjects(data);
            Object newVersion = null;
            if (this.versionFieldType != null) {
                newVersion = this.versionFieldType.moveToNextValue(this.versionFieldType.extractJavaFieldValue(data));
                args[this.versionFieldTypeIndex] = this.versionFieldType.convertJavaFieldToSqlArgValue(newVersion);
            }
            int rowC = databaseConnection.update(this.statement, args, this.argFieldTypes);
            if (rowC > 0) {
                if (newVersion != null) {
                    this.versionFieldType.assignField(data, newVersion, false, null);
                }
                if (objectCache != null) {
                    T cachedData = objectCache.get(this.clazz, this.idField.extractJavaFieldValue(data));
                    if (!(cachedData == null || cachedData == data)) {
                        for (FieldType fieldType : this.tableInfo.getFieldTypes()) {
                            if (fieldType != this.idField) {
                                fieldType.assignField(cachedData, fieldType.extractJavaFieldValue(data), false, objectCache);
                            }
                        }
                    }
                }
            }
            logger.debug("update data with statement '{}' and {} args, changed {} rows", this.statement, Integer.valueOf(args.length), Integer.valueOf(rowC));
            if (args.length <= 0) {
                return rowC;
            }
            logger.trace("update arguments: {}", args);
            return rowC;
        } catch (SQLException e) {
            throw SqlExceptionUtil.create("Unable to run update stmt on object " + data + ": " + this.statement, e);
        }
    }

    private static boolean isFieldUpdatable(FieldType fieldType, FieldType idField) {
        if (fieldType == idField || fieldType.isForeignCollection() || fieldType.isReadOnly()) {
            return false;
        }
        return true;
    }
}
