package com.j256.ormlite.android;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteAndroidDatabaseType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.BaseConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseConnectionProxyFactory;
import java.sql.SQLException;

public class AndroidConnectionSource extends BaseConnectionSource implements ConnectionSource {
    private static DatabaseConnectionProxyFactory connectionProxyFactory;
    private static final Logger logger;
    private boolean cancelQueriesEnabled;
    private DatabaseConnection connection;
    private final DatabaseType databaseType;
    private final SQLiteOpenHelper helper;
    private volatile boolean isOpen;
    private final SQLiteDatabase sqliteDatabase;

    static {
        logger = LoggerFactory.getLogger(AndroidConnectionSource.class);
    }

    public AndroidConnectionSource(SQLiteOpenHelper helper) {
        this.connection = null;
        this.isOpen = true;
        this.databaseType = new SqliteAndroidDatabaseType();
        this.cancelQueriesEnabled = false;
        this.helper = helper;
        this.sqliteDatabase = null;
    }

    public AndroidConnectionSource(SQLiteDatabase sqliteDatabase) {
        this.connection = null;
        this.isOpen = true;
        this.databaseType = new SqliteAndroidDatabaseType();
        this.cancelQueriesEnabled = false;
        this.helper = null;
        this.sqliteDatabase = sqliteDatabase;
    }

    public DatabaseConnection getReadOnlyConnection() throws SQLException {
        return getReadWriteConnection();
    }

    public DatabaseConnection getReadWriteConnection() throws SQLException {
        DatabaseConnection conn = getSavedConnection();
        if (conn != null) {
            return conn;
        }
        if (this.connection == null) {
            Object db;
            if (this.sqliteDatabase == null) {
                try {
                    db = this.helper.getWritableDatabase();
                } catch (android.database.SQLException e) {
                    throw SqlExceptionUtil.create("Getting a writable database from helper " + this.helper + " failed", e);
                }
            }
            db = this.sqliteDatabase;
            this.connection = new AndroidDatabaseConnection(db, true, this.cancelQueriesEnabled);
            if (connectionProxyFactory != null) {
                this.connection = connectionProxyFactory.createProxy(this.connection);
            }
            logger.trace("created connection {} for db {}, helper {}", this.connection, db, this.helper);
        } else {
            logger.trace("{}: returning read-write connection {}, helper {}", (Object) this, this.connection, this.helper);
        }
        return this.connection;
    }

    public void releaseConnection(DatabaseConnection connection) {
    }

    public boolean saveSpecialConnection(DatabaseConnection connection) throws SQLException {
        return saveSpecial(connection);
    }

    public void clearSpecialConnection(DatabaseConnection connection) {
        clearSpecial(connection, logger);
    }

    public void close() {
        this.isOpen = false;
    }

    public void closeQuietly() {
        close();
    }

    public DatabaseType getDatabaseType() {
        return this.databaseType;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public static void setDatabaseConnectionProxyFactory(DatabaseConnectionProxyFactory connectionProxyFactory) {
        connectionProxyFactory = connectionProxyFactory;
    }

    public boolean isCancelQueriesEnabled() {
        return this.cancelQueriesEnabled;
    }

    public void setCancelQueriesEnabled(boolean cancelQueriesEnabled) {
        this.cancelQueriesEnabled = cancelQueriesEnabled;
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }
}
