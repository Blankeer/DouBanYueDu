package com.j256.ormlite.stmt.query;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.SelectArg;
import java.sql.SQLException;
import java.util.List;

abstract class BaseComparison implements Comparison {
    private static final String NUMBER_CHARACTERS = "0123456789.-+";
    protected final String columnName;
    protected final FieldType fieldType;
    private final Object value;

    public abstract void appendOperation(StringBuilder stringBuilder);

    protected BaseComparison(String columnName, FieldType fieldType, Object value, boolean isComparison) throws SQLException {
        if (!isComparison || fieldType == null || fieldType.isComparable()) {
            this.columnName = columnName;
            this.fieldType = fieldType;
            this.value = value;
            return;
        }
        throw new SQLException("Field '" + columnName + "' is of data type " + fieldType.getDataPersister() + " which can not be compared");
    }

    public void appendSql(DatabaseType databaseType, String tableName, StringBuilder sb, List<ArgumentHolder> argList) throws SQLException {
        if (tableName != null) {
            databaseType.appendEscapedEntityName(sb, tableName);
            sb.append(Char.DOT);
        }
        databaseType.appendEscapedEntityName(sb, this.columnName);
        sb.append(Char.SPACE);
        appendOperation(sb);
        appendValue(databaseType, sb, argList);
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void appendValue(DatabaseType databaseType, StringBuilder sb, List<ArgumentHolder> argList) throws SQLException {
        appendArgOrValue(databaseType, this.fieldType, sb, argList, this.value);
    }

    protected void appendArgOrValue(DatabaseType databaseType, FieldType fieldType, StringBuilder sb, List<ArgumentHolder> argList, Object argOrValue) throws SQLException {
        boolean appendSpace = true;
        if (argOrValue == null) {
            throw new SQLException("argument for '" + fieldType.getFieldName() + "' is null");
        }
        ArgumentHolder argHolder;
        if (argOrValue instanceof ArgumentHolder) {
            sb.append('?');
            argHolder = (ArgumentHolder) argOrValue;
            argHolder.setMetaInfo(this.columnName, fieldType);
            argList.add(argHolder);
        } else if (argOrValue instanceof ColumnArg) {
            ColumnArg columnArg = (ColumnArg) argOrValue;
            String tableName = columnArg.getTableName();
            if (tableName != null) {
                databaseType.appendEscapedEntityName(sb, tableName);
                sb.append(Char.DOT);
            }
            databaseType.appendEscapedEntityName(sb, columnArg.getColumnName());
        } else if (fieldType.isArgumentHolderRequired()) {
            sb.append('?');
            argHolder = new SelectArg();
            argHolder.setMetaInfo(this.columnName, fieldType);
            argHolder.setValue(argOrValue);
            argList.add(argHolder);
        } else if (fieldType.isForeign() && fieldType.getType().isAssignableFrom(argOrValue.getClass())) {
            FieldType idFieldType = fieldType.getForeignIdField();
            appendArgOrValue(databaseType, idFieldType, sb, argList, idFieldType.extractJavaFieldValue(argOrValue));
            appendSpace = false;
        } else if (fieldType.isEscapedValue()) {
            databaseType.appendEscapedWord(sb, fieldType.convertJavaFieldToSqlArgValue(argOrValue).toString());
        } else if (fieldType.isForeign()) {
            String value = fieldType.convertJavaFieldToSqlArgValue(argOrValue).toString();
            if (value.length() <= 0 || NUMBER_CHARACTERS.indexOf(value.charAt(0)) >= 0) {
                sb.append(value);
            } else {
                throw new SQLException("Foreign field " + fieldType + " does not seem to be producing a numerical value '" + value + "'. Maybe you are passing the wrong object to comparison: " + this);
            }
        } else {
            sb.append(fieldType.convertJavaFieldToSqlArgValue(argOrValue));
        }
        if (appendSpace) {
            sb.append(Char.SPACE);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.columnName).append(Char.SPACE);
        appendOperation(sb);
        sb.append(Char.SPACE);
        sb.append(this.value);
        return sb.toString();
    }
}
