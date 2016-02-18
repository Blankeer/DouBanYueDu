package com.douban.book.reader.database;

import android.database.sqlite.SQLiteDatabase;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.Annotation.Column;
import com.douban.book.reader.entity.Bookmark;
import com.douban.book.reader.entity.DbCacheEntity;
import com.douban.book.reader.entity.ShelfItem;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.Logger;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.IOException;
import java.sql.SQLException;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "reader.db";
    private static final int DATABASE_VERSION = 10;
    private static final String TAG;
    static DatabaseHelper sDbHelper;

    public enum StorageTarget {
        FORCE_EXTERNAL,
        FORCE_INTERNAL,
        CHECK_DEBUG_SWITCH
    }

    static {
        TAG = DatabaseHelper.class.getSimpleName();
        sDbHelper = new DatabaseHelper();
        try {
            sDbHelper.getConnectionSource().getReadWriteConnection();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    public static DatabaseHelper getInstance() {
        return sDbHelper;
    }

    public DatabaseHelper() {
        this(StorageTarget.CHECK_DEBUG_SWITCH);
    }

    public DatabaseHelper(StorageTarget target) {
        super(App.get(), getStorageTargetPath(target), null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db, ConnectionSource source) {
        createTable(source, Bookmark.class);
        createTable(source, Annotation.class);
        createTable(source, ShelfItem.class);
        createTable(source, DbCacheEntity.class);
    }

    public void onUpgrade(SQLiteDatabase db, ConnectionSource source, int oldVersion, int newVersion) {
        Logger.dc(TAG, "Updating database. oldVersion=" + oldVersion + " newVersion=" + newVersion, new Object[0]);
        switch (oldVersion) {
            case dx.b /*1*/:
            case dx.c /*2*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
            case dj.f /*5*/:
            case ci.g /*6*/:
            case ci.h /*7*/:
            case h.g /*8*/:
                dropTable(db, "user_info");
                dropTable(db, "book_list");
                dropTable(db, Bookmark.TABLE_NAME);
                dropTable(db, NotificationCompatApi21.CATEGORY_PROGRESS);
                dropTable(db, "position");
                dropTable(db, Annotation.TABLE_NAME);
                dropTable(db, "bucket_reference");
                dropTable(db, "column");
                createTable(source, Bookmark.class);
                createTable(source, Annotation.class);
                createTable(source, ShelfItem.class);
                createTable(source, DbCacheEntity.class);
                break;
            case h.h /*9*/:
                break;
            default:
                return;
        }
        addColumn(db, Annotation.TABLE_NAME, WorksListUri.KEY_ID, "INTEGER DEFAULT 0");
        addColumn(db, Annotation.TABLE_NAME, Column.USER_ID, "INTEGER DEFAULT 0");
        addColumn(db, Annotation.TABLE_NAME, Column.PRIVACY, "CHAR(1) DEFAULT 'X'");
        addColumn(db, Annotation.TABLE_NAME, Column.NOTE_UPDATE_TIME, "VARCHAR");
    }

    public void clearDb() {
        clearTable(Bookmark.class);
        clearTable(Annotation.class);
        clearTable(ShelfItem.class);
        clearTable(DbCacheEntity.class);
    }

    public void moveTo(StorageTarget target) throws IOException {
        if (isInInternalStorage() && target == StorageTarget.FORCE_EXTERNAL) {
            FileUtils.copy(FilePath.internalStorageDatabase(), FilePath.externalStorageDatabase());
            sDbHelper = new DatabaseHelper(StorageTarget.FORCE_EXTERNAL);
        } else if (isInExternalStorage() && target == StorageTarget.FORCE_INTERNAL) {
            FileUtils.copy(FilePath.externalStorageDatabase(), FilePath.internalStorageDatabase());
            sDbHelper = new DatabaseHelper(StorageTarget.FORCE_INTERNAL);
        }
    }

    private boolean isInInternalStorage() {
        return getInstance().getDatabaseName().length() == DATABASE_NAME.length();
    }

    private boolean isInExternalStorage() {
        return !isInInternalStorage();
    }

    private void createTable(ConnectionSource source, Class<?> cls) {
        try {
            TableUtils.createTable(source, (Class) cls);
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    private void dropTable(ConnectionSource source, Class<?> cls) {
        try {
            TableUtils.dropTable(source, (Class) cls, true);
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    private void clearTable(Class<?> cls) {
        try {
            TableUtils.clearTable(getConnectionSource(), (Class) cls);
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    private void dropTable(SQLiteDatabase db, String tableName) {
        db.execSQL(String.format("DROP TABLE IF EXISTS '%s'", new Object[]{tableName}));
    }

    private void addColumn(SQLiteDatabase db, String tableName, String columnName, String description) {
        db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s", new Object[]{tableName, columnName, description}));
    }

    private static String getStorageTargetPath(StorageTarget storageTarget) {
        boolean useExternal = storageTarget == StorageTarget.FORCE_EXTERNAL || DebugSwitch.on(Key.APP_DEBUG_SAVE_DATABASE_TO_SD_CARD);
        if (useExternal) {
            return FilePath.externalStorageDatabase().getPath();
        }
        return DATABASE_NAME;
    }
}
