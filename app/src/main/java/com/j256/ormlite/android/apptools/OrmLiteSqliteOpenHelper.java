package com.j256.ormlite.android.apptools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfigLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

public abstract class OrmLiteSqliteOpenHelper extends SQLiteOpenHelper {
    protected static Logger logger;
    protected boolean cancelQueriesEnabled;
    protected AndroidConnectionSource connectionSource;
    private volatile boolean isOpen;

    public abstract void onCreate(SQLiteDatabase sQLiteDatabase, ConnectionSource connectionSource);

    public abstract void onUpgrade(SQLiteDatabase sQLiteDatabase, ConnectionSource connectionSource, int i, int i2);

    static {
        logger = LoggerFactory.getLogger(OrmLiteSqliteOpenHelper.class);
    }

    public OrmLiteSqliteOpenHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
        this.connectionSource = new AndroidConnectionSource((SQLiteOpenHelper) this);
        this.isOpen = true;
        logger.trace("{}: constructed connectionSource {}", (Object) this, this.connectionSource);
    }

    public OrmLiteSqliteOpenHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion, int configFileId) {
        this(context, databaseName, factory, databaseVersion, openFileId(context, configFileId));
    }

    public OrmLiteSqliteOpenHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion, File configFile) {
        this(context, databaseName, factory, databaseVersion, openFile(configFile));
    }

    public OrmLiteSqliteOpenHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion, InputStream stream) {
        super(context, databaseName, factory, databaseVersion);
        this.connectionSource = new AndroidConnectionSource((SQLiteOpenHelper) this);
        this.isOpen = true;
        if (stream != null) {
            try {
                DaoManager.addCachedDatabaseConfigs(DatabaseTableConfigLoader.loadDatabaseConfigFromReader(new BufferedReader(new InputStreamReader(stream), CodedOutputStream.DEFAULT_BUFFER_SIZE)));
                try {
                    stream.close();
                } catch (IOException e) {
                }
            } catch (SQLException e2) {
                throw new IllegalStateException("Could not load object config file", e2);
            } catch (Throwable th) {
                try {
                    stream.close();
                } catch (IOException e3) {
                }
            }
        }
    }

    public ConnectionSource getConnectionSource() {
        if (!this.isOpen) {
            logger.warn(new IllegalStateException(), "Getting connectionSource was called after closed");
        }
        return this.connectionSource;
    }

    public final void onCreate(SQLiteDatabase db) {
        ConnectionSource cs = getConnectionSource();
        DatabaseConnection conn = cs.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true, this.cancelQueriesEnabled);
            try {
                cs.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            onCreate(db, cs);
            if (clearSpecial) {
                cs.clearSpecialConnection(conn);
            }
        } catch (Throwable th) {
            if (clearSpecial) {
                cs.clearSpecialConnection(conn);
            }
        }
    }

    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ConnectionSource cs = getConnectionSource();
        DatabaseConnection conn = cs.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true, this.cancelQueriesEnabled);
            try {
                cs.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            onUpgrade(db, cs, oldVersion, newVersion);
            if (clearSpecial) {
                cs.clearSpecialConnection(conn);
            }
        } catch (Throwable th) {
            if (clearSpecial) {
                cs.clearSpecialConnection(conn);
            }
        }
    }

    public void close() {
        super.close();
        this.connectionSource.close();
        this.isOpen = false;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        return DaoManager.createDao(getConnectionSource(), (Class) clazz);
    }

    public <D extends RuntimeExceptionDao<T, ?>, T> D getRuntimeExceptionDao(Class<T> clazz) {
        try {
            return new RuntimeExceptionDao(getDao(clazz));
        } catch (SQLException e) {
            throw new RuntimeException("Could not create RuntimeExcepitionDao for class " + clazz, e);
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }

    private static InputStream openFileId(Context context, int fileId) {
        InputStream stream = context.getResources().openRawResource(fileId);
        if (stream != null) {
            return stream;
        }
        throw new IllegalStateException("Could not find object config file with id " + fileId);
    }

    private static InputStream openFile(File configFile) {
        if (configFile == null) {
            return null;
        }
        try {
            return new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Could not open config file " + configFile, e);
        }
    }
}
