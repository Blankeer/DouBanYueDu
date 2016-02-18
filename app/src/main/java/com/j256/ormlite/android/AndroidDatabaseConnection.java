package com.j256.ormlite.android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.alipay.sdk.protocol.h;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.misc.VersionUtils;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.GeneratedKeyHolder;
import java.sql.SQLException;
import java.sql.Savepoint;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class AndroidDatabaseConnection implements DatabaseConnection {
    private static final String ANDROID_VERSION = "VERSION__4.48__";
    private static final String[] NO_STRING_ARGS;
    private static Logger logger;
    private final boolean cancelQueriesEnabled;
    private final SQLiteDatabase db;
    private final boolean readWrite;

    /* renamed from: com.j256.ormlite.android.AndroidDatabaseConnection.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$j256$ormlite$field$SqlType;

        static {
            $SwitchMap$com$j256$ormlite$field$SqlType = new int[SqlType.values().length];
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.LONG_STRING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.CHAR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BOOLEAN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BYTE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.SHORT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.INTEGER.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.LONG.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.FLOAT.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.DOUBLE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BYTE_ARRAY.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.SERIALIZABLE.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.DATE.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BLOB.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BIG_DECIMAL.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.UNKNOWN.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
        }
    }

    private static class OurSavePoint implements Savepoint {
        private String name;

        public OurSavePoint(String name) {
            this.name = name;
        }

        public int getSavepointId() {
            return 0;
        }

        public String getSavepointName() {
            return this.name;
        }
    }

    static {
        logger = LoggerFactory.getLogger(AndroidDatabaseConnection.class);
        NO_STRING_ARGS = new String[0];
        VersionUtils.checkCoreVersusAndroidVersions(ANDROID_VERSION);
    }

    public AndroidDatabaseConnection(SQLiteDatabase db, boolean readWrite) {
        this(db, readWrite, false);
    }

    public AndroidDatabaseConnection(SQLiteDatabase db, boolean readWrite, boolean cancelQueriesEnabled) {
        this.db = db;
        this.readWrite = readWrite;
        this.cancelQueriesEnabled = cancelQueriesEnabled;
        logger.trace("{}: db {} opened, read-write = {}", (Object) this, (Object) db, Boolean.valueOf(readWrite));
    }

    public boolean isAutoCommitSupported() {
        return true;
    }

    public boolean isAutoCommit() throws SQLException {
        try {
            boolean inTransaction = this.db.inTransaction();
            logger.trace("{}: in transaction is {}", (Object) this, Boolean.valueOf(inTransaction));
            return !inTransaction;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("problems getting auto-commit from database", e);
        }
    }

    public void setAutoCommit(boolean autoCommit) {
        if (autoCommit) {
            if (this.db.inTransaction()) {
                this.db.setTransactionSuccessful();
                this.db.endTransaction();
            }
        } else if (!this.db.inTransaction()) {
            this.db.beginTransaction();
        }
    }

    public Savepoint setSavePoint(String name) throws SQLException {
        try {
            this.db.beginTransaction();
            logger.trace("{}: save-point set with name {}", (Object) this, (Object) name);
            return new OurSavePoint(name);
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("problems beginning transaction " + name, e);
        }
    }

    public boolean isReadWrite() {
        return this.readWrite;
    }

    public void commit(Savepoint savepoint) throws SQLException {
        try {
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
            if (savepoint == null) {
                logger.trace("{}: transaction is successfuly ended", (Object) this);
            } else {
                logger.trace("{}: transaction {} is successfuly ended", (Object) this, savepoint.getSavepointName());
            }
        } catch (android.database.SQLException e) {
            if (savepoint == null) {
                throw SqlExceptionUtil.create("problems commiting transaction", e);
            }
            throw SqlExceptionUtil.create("problems commiting transaction " + savepoint.getSavepointName(), e);
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            this.db.endTransaction();
            if (savepoint == null) {
                logger.trace("{}: transaction is ended, unsuccessfuly", (Object) this);
            } else {
                logger.trace("{}: transaction {} is ended, unsuccessfuly", (Object) this, savepoint.getSavepointName());
            }
        } catch (android.database.SQLException e) {
            if (savepoint == null) {
                throw SqlExceptionUtil.create("problems rolling back transaction", e);
            }
            throw SqlExceptionUtil.create("problems rolling back transaction " + savepoint.getSavepointName(), e);
        }
    }

    public int executeStatement(String statementStr, int resultFlags) throws SQLException {
        return AndroidCompiledStatement.execSql(this.db, statementStr, statementStr, NO_STRING_ARGS);
    }

    public CompiledStatement compileStatement(String statement, StatementType type, FieldType[] argFieldTypes, int resultFlags) {
        Object stmt = new AndroidCompiledStatement(statement, this.db, type, this.cancelQueriesEnabled);
        logger.trace("{}: compiled statement got {}: {}", (Object) this, stmt, (Object) statement);
        return stmt;
    }

    public int insert(String statement, Object[] args, FieldType[] argFieldTypes, GeneratedKeyHolder keyHolder) throws SQLException {
        SQLiteStatement stmt = null;
        try {
            stmt = this.db.compileStatement(statement);
            bindArgs(stmt, args, argFieldTypes);
            long rowId = stmt.executeInsert();
            if (keyHolder != null) {
                keyHolder.addKey(Long.valueOf(rowId));
            }
            logger.trace("{}: insert statement is compiled and executed, changed {}: {}", (Object) this, Integer.valueOf(1), (Object) statement);
            if (stmt != null) {
                stmt.close();
            }
            return 1;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("inserting to database failed: " + statement, e);
        } catch (Throwable th) {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public int update(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        return update(statement, args, argFieldTypes, "updated");
    }

    public int delete(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        return update(statement, args, argFieldTypes, "deleted");
    }

    public <T> Object queryForOne(String statement, Object[] args, FieldType[] argFieldTypes, GenericRowMapper<T> rowMapper, ObjectCache objectCache) throws SQLException {
        Cursor cursor = null;
        try {
            Object first;
            cursor = this.db.rawQuery(statement, toStrings(args));
            AndroidDatabaseResults results = new AndroidDatabaseResults(cursor, objectCache);
            logger.trace("{}: queried for one result: {}", (Object) this, (Object) statement);
            if (results.first()) {
                first = rowMapper.mapRow(results);
                if (results.next()) {
                    first = MORE_THAN_ONE;
                    if (cursor != null) {
                        cursor.close();
                    }
                } else if (cursor != null) {
                    cursor.close();
                }
            } else {
                first = null;
                if (cursor != null) {
                    cursor.close();
                }
            }
            return first;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("queryForOne from database failed: " + statement, e);
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long queryForLong(String statement) throws SQLException {
        SQLiteStatement stmt = null;
        try {
            stmt = this.db.compileStatement(statement);
            long result = stmt.simpleQueryForLong();
            logger.trace("{}: query for long simple query returned {}: {}", (Object) this, Long.valueOf(result), (Object) statement);
            if (stmt != null) {
                stmt.close();
            }
            return result;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("queryForLong from database failed: " + statement, e);
        } catch (Throwable th) {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public long queryForLong(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        Cursor cursor = null;
        try {
            long result;
            cursor = this.db.rawQuery(statement, toStrings(args));
            AndroidDatabaseResults results = new AndroidDatabaseResults(cursor, null);
            if (results.first()) {
                result = results.getLong(0);
            } else {
                result = 0;
            }
            logger.trace("{}: query for long raw query returned {}: {}", (Object) this, Long.valueOf(result), (Object) statement);
            if (cursor != null) {
                cursor.close();
            }
            return result;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("queryForLong from database failed: " + statement, e);
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void close() throws SQLException {
        try {
            this.db.close();
            logger.trace("{}: db {} closed", (Object) this, this.db);
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("problems closing the database connection", e);
        }
    }

    public void closeQuietly() {
        try {
            close();
        } catch (SQLException e) {
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            boolean isOpen = this.db.isOpen();
            logger.trace("{}: db {} isOpen returned {}", (Object) this, this.db, Boolean.valueOf(isOpen));
            return !isOpen;
        } catch (android.database.SQLException e) {
            throw SqlExceptionUtil.create("problems detecting if the database is closed", e);
        }
    }

    public boolean isTableExists(String tableName) {
        Cursor cursor = this.db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + tableName + "'", null);
        try {
            boolean result;
            if (cursor.getCount() > 0) {
                result = true;
            } else {
                result = false;
            }
            logger.trace("{}: isTableExists '{}' returned {}", (Object) this, (Object) tableName, Boolean.valueOf(result));
            return result;
        } finally {
            cursor.close();
        }
    }

    private int update(String statement, Object[] args, FieldType[] argFieldTypes, String label) throws SQLException {
        SQLiteStatement stmt = null;
        try {
            int result;
            stmt = this.db.compileStatement(statement);
            bindArgs(stmt, args, argFieldTypes);
            stmt.execute();
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            try {
                stmt = this.db.compileStatement("SELECT CHANGES()");
                result = (int) stmt.simpleQueryForLong();
                if (stmt != null) {
                    stmt.close();
                }
            } catch (android.database.SQLException e) {
                result = 1;
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Throwable th) {
                if (stmt != null) {
                    stmt.close();
                }
            }
            logger.trace("{} statement is compiled and executed, changed {}: {}", (Object) label, Integer.valueOf(result), (Object) statement);
            return result;
        } catch (android.database.SQLException e2) {
            throw SqlExceptionUtil.create("updating database failed: " + statement, e2);
        } catch (Throwable th2) {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void bindArgs(SQLiteStatement stmt, Object[] args, FieldType[] argFieldTypes) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    stmt.bindNull(i + 1);
                } else {
                    SqlType sqlType = argFieldTypes[i].getSqlType();
                    switch (AnonymousClass1.$SwitchMap$com$j256$ormlite$field$SqlType[sqlType.ordinal()]) {
                        case dx.b /*1*/:
                        case dx.c /*2*/:
                        case dx.d /*3*/:
                            stmt.bindString(i + 1, arg.toString());
                            break;
                        case dx.e /*4*/:
                        case dj.f /*5*/:
                        case ci.g /*6*/:
                        case ci.h /*7*/:
                        case h.g /*8*/:
                            stmt.bindLong(i + 1, ((Number) arg).longValue());
                            break;
                        case h.h /*9*/:
                        case h.i /*10*/:
                            stmt.bindDouble(i + 1, ((Number) arg).doubleValue());
                            break;
                        case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                        case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                            stmt.bindBlob(i + 1, (byte[]) arg);
                            break;
                        case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                        case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                        case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                            throw new SQLException("Invalid Android type: " + sqlType);
                        default:
                            throw new SQLException("Unknown sql argument type: " + sqlType);
                    }
                }
            }
        }
    }

    private String[] toStrings(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        String[] strings = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                strings[i] = null;
            } else {
                strings[i] = arg.toString();
            }
        }
        return strings;
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }
}
