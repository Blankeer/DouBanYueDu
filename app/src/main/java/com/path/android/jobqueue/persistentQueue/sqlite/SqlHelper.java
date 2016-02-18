package com.path.android.jobqueue.persistentQueue.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.path.android.jobqueue.log.JqLog;

public class SqlHelper {
    String FIND_BY_ID_QUERY;
    final int columnCount;
    private SQLiteStatement countStatement;
    final SQLiteDatabase db;
    private SQLiteStatement deleteStatement;
    private SQLiteStatement insertOrReplaceStatement;
    private SQLiteStatement insertStatement;
    private SQLiteStatement nextJobDelayedUntilWithNetworkStatement;
    private SQLiteStatement nextJobDelayedUntilWithoutNetworkStatement;
    private SQLiteStatement onJobFetchedForRunningStatement;
    final String primaryKeyColumnName;
    final long sessionId;
    final String tableName;

    public static class Order {
        final Property property;
        final Type type;

        public enum Type {
            ASC,
            DESC
        }

        public Order(Property property, Type type) {
            this.property = property;
            this.type = type;
        }
    }

    public static class Property {
        public final int columnIndex;
        final String columnName;
        final String type;

        public Property(String columnName, String type, int columnIndex) {
            this.columnName = columnName;
            this.type = type;
            this.columnIndex = columnIndex;
        }
    }

    public SqlHelper(SQLiteDatabase db, String tableName, String primaryKeyColumnName, int columnCount, long sessionId) {
        this.db = db;
        this.tableName = tableName;
        this.columnCount = columnCount;
        this.primaryKeyColumnName = primaryKeyColumnName;
        this.sessionId = sessionId;
        this.FIND_BY_ID_QUERY = "SELECT * FROM " + tableName + " WHERE " + DbOpenHelper.ID_COLUMN.columnName + " = ?";
    }

    public static String create(String tableName, Property primaryKey, Property... properties) {
        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        builder.append(tableName).append(" (");
        builder.append(primaryKey.columnName).append(" ");
        builder.append(primaryKey.type);
        builder.append("  primary key autoincrement ");
        for (Property property : properties) {
            builder.append(", `").append(property.columnName).append("` ").append(property.type);
        }
        builder.append(" );");
        JqLog.d(builder.toString(), new Object[0]);
        return builder.toString();
    }

    public static String drop(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public SQLiteStatement getInsertStatement() {
        if (this.insertStatement == null) {
            StringBuilder builder = new StringBuilder("INSERT INTO ").append(this.tableName);
            builder.append(" VALUES (");
            for (int i = 0; i < this.columnCount; i++) {
                if (i != 0) {
                    builder.append(",");
                }
                builder.append("?");
            }
            builder.append(")");
            this.insertStatement = this.db.compileStatement(builder.toString());
        }
        return this.insertStatement;
    }

    public SQLiteStatement getCountStatement() {
        if (this.countStatement == null) {
            this.countStatement = this.db.compileStatement("SELECT COUNT(*) FROM " + this.tableName + " WHERE " + DbOpenHelper.RUNNING_SESSION_ID_COLUMN.columnName + " != ?");
        }
        return this.countStatement;
    }

    public SQLiteStatement getInsertOrReplaceStatement() {
        if (this.insertOrReplaceStatement == null) {
            StringBuilder builder = new StringBuilder("INSERT OR REPLACE INTO ").append(this.tableName);
            builder.append(" VALUES (");
            for (int i = 0; i < this.columnCount; i++) {
                if (i != 0) {
                    builder.append(",");
                }
                builder.append("?");
            }
            builder.append(")");
            this.insertOrReplaceStatement = this.db.compileStatement(builder.toString());
        }
        return this.insertOrReplaceStatement;
    }

    public SQLiteStatement getDeleteStatement() {
        if (this.deleteStatement == null) {
            this.deleteStatement = this.db.compileStatement("DELETE FROM " + this.tableName + " WHERE " + this.primaryKeyColumnName + " = ?");
        }
        return this.deleteStatement;
    }

    public SQLiteStatement getOnJobFetchedForRunningStatement() {
        if (this.onJobFetchedForRunningStatement == null) {
            this.onJobFetchedForRunningStatement = this.db.compileStatement("UPDATE " + this.tableName + " SET " + DbOpenHelper.RUN_COUNT_COLUMN.columnName + " = ? , " + DbOpenHelper.RUNNING_SESSION_ID_COLUMN.columnName + " = ? " + " WHERE " + this.primaryKeyColumnName + " = ? ");
        }
        return this.onJobFetchedForRunningStatement;
    }

    public SQLiteStatement getNextJobDelayedUntilWithNetworkStatement() {
        if (this.nextJobDelayedUntilWithNetworkStatement == null) {
            this.nextJobDelayedUntilWithNetworkStatement = this.db.compileStatement("SELECT " + DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnName + " FROM " + this.tableName + " WHERE " + DbOpenHelper.RUNNING_SESSION_ID_COLUMN.columnName + " != " + this.sessionId + " ORDER BY " + DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnName + " ASC" + " LIMIT 1");
        }
        return this.nextJobDelayedUntilWithNetworkStatement;
    }

    public SQLiteStatement getNextJobDelayedUntilWithoutNetworkStatement() {
        if (this.nextJobDelayedUntilWithoutNetworkStatement == null) {
            this.nextJobDelayedUntilWithoutNetworkStatement = this.db.compileStatement("SELECT " + DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnName + " FROM " + this.tableName + " WHERE " + DbOpenHelper.RUNNING_SESSION_ID_COLUMN.columnName + " != " + this.sessionId + " AND " + DbOpenHelper.REQUIRES_NETWORK_COLUMN.columnName + " != 1" + " ORDER BY " + DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnName + " ASC" + " LIMIT 1");
        }
        return this.nextJobDelayedUntilWithoutNetworkStatement;
    }

    public String createSelect(String where, Integer limit, Order... orders) {
        StringBuilder builder = new StringBuilder("SELECT * FROM ");
        builder.append(this.tableName);
        if (where != null) {
            builder.append(" WHERE ").append(where);
        }
        boolean first = true;
        for (Order order : orders) {
            if (first) {
                builder.append(" ORDER BY ");
            } else {
                builder.append(",");
            }
            first = false;
            builder.append(order.property.columnName).append(" ").append(order.type);
        }
        if (limit != null) {
            builder.append(" LIMIT ").append(limit);
        }
        return builder.toString();
    }

    public void truncate() {
        this.db.execSQL("DELETE FROM job_holder");
        vacuum();
    }

    public void vacuum() {
        this.db.execSQL("VACUUM");
    }

    public void resetDelayTimesTo(long newDelayTime) {
        this.db.execSQL("UPDATE job_holder SET " + DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnName + "=?", new Object[]{Long.valueOf(newDelayTime)});
    }
}
