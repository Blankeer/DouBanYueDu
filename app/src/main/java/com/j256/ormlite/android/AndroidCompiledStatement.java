package com.j256.ormlite.android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.alipay.sdk.protocol.h;
import com.j256.ormlite.android.compat.ApiCompatibility;
import com.j256.ormlite.android.compat.ApiCompatibility.CancellationHook;
import com.j256.ormlite.android.compat.ApiCompatibilityUtils;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class AndroidCompiledStatement implements CompiledStatement {
    private static final String[] NO_STRING_ARGS;
    private static final ApiCompatibility apiCompatibility;
    private static Logger logger;
    private List<Object> args;
    private final boolean cancelQueriesEnabled;
    private CancellationHook cancellationHook;
    private Cursor cursor;
    private final SQLiteDatabase db;
    private Integer max;
    private final String sql;
    private final StatementType type;

    /* renamed from: com.j256.ormlite.android.AndroidCompiledStatement.1 */
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
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.DATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BOOLEAN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.CHAR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BYTE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.SHORT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.INTEGER.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.LONG.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.FLOAT.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.DOUBLE.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.BYTE_ARRAY.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$j256$ormlite$field$SqlType[SqlType.SERIALIZABLE.ordinal()] = 13;
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

    static {
        logger = LoggerFactory.getLogger(AndroidCompiledStatement.class);
        NO_STRING_ARGS = new String[0];
        apiCompatibility = ApiCompatibilityUtils.getCompatibility();
    }

    public AndroidCompiledStatement(String sql, SQLiteDatabase db, StatementType type, boolean cancelQueriesEnabled) {
        this.sql = sql;
        this.db = db;
        this.type = type;
        this.cancelQueriesEnabled = cancelQueriesEnabled;
    }

    public int getColumnCount() throws SQLException {
        return getCursor().getColumnCount();
    }

    public String getColumnName(int column) throws SQLException {
        return getCursor().getColumnName(column);
    }

    public DatabaseResults runQuery(ObjectCache objectCache) throws SQLException {
        if (this.type.isOkForQuery()) {
            return new AndroidDatabaseResults(getCursor(), objectCache);
        }
        throw new IllegalArgumentException("Cannot call query on a " + this.type + " statement");
    }

    public int runUpdate() throws SQLException {
        if (this.type.isOkForUpdate()) {
            String finalSql;
            if (this.max == null) {
                finalSql = this.sql;
            } else {
                finalSql = this.sql + " " + this.max;
            }
            return execSql(this.db, "runUpdate", finalSql, getArgArray());
        }
        throw new IllegalArgumentException("Cannot call update on a " + this.type + " statement");
    }

    public int runExecute() throws SQLException {
        if (this.type.isOkForExecute()) {
            return execSql(this.db, "runExecute", this.sql, getArgArray());
        }
        throw new IllegalArgumentException("Cannot call execute on a " + this.type + " statement");
    }

    public void close() throws SQLException {
        if (this.cursor != null) {
            try {
                this.cursor.close();
            } catch (android.database.SQLException e) {
                throw SqlExceptionUtil.create("Problems closing Android cursor", e);
            }
        }
        this.cancellationHook = null;
    }

    public void closeQuietly() {
        try {
            close();
        } catch (SQLException e) {
        }
    }

    public void cancel() {
        if (this.cancellationHook != null) {
            this.cancellationHook.cancel();
        }
    }

    public void setObject(int parameterIndex, Object obj, SqlType sqlType) throws SQLException {
        isInPrep();
        if (this.args == null) {
            this.args = new ArrayList();
        }
        if (obj == null) {
            this.args.add(parameterIndex, null);
            return;
        }
        switch (AnonymousClass1.$SwitchMap$com$j256$ormlite$field$SqlType[sqlType.ordinal()]) {
            case dx.b /*1*/:
            case dx.c /*2*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
            case dj.f /*5*/:
            case ci.g /*6*/:
            case ci.h /*7*/:
            case h.g /*8*/:
            case h.h /*9*/:
            case h.i /*10*/:
            case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                this.args.add(parameterIndex, obj.toString());
            case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
            case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                this.args.add(parameterIndex, obj);
            case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
            case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                throw new SQLException("Invalid Android type: " + sqlType);
            default:
                throw new SQLException("Unknown sql argument type: " + sqlType);
        }
    }

    public void setMaxRows(int max) throws SQLException {
        isInPrep();
        this.max = Integer.valueOf(max);
    }

    public void setQueryTimeout(long millis) {
    }

    public Cursor getCursor() throws SQLException {
        if (this.cursor == null) {
            String finalSql = null;
            try {
                Object finalSql2;
                if (this.max == null) {
                    finalSql2 = this.sql;
                } else {
                    finalSql2 = this.sql + " " + this.max;
                }
                if (this.cancelQueriesEnabled) {
                    this.cancellationHook = apiCompatibility.createCancellationHook();
                }
                this.cursor = apiCompatibility.rawQuery(this.db, finalSql2, getStringArray(), this.cancellationHook);
                this.cursor.moveToFirst();
                logger.trace("{}: started rawQuery cursor for: {}", (Object) this, finalSql2);
            } catch (android.database.SQLException e) {
                throw SqlExceptionUtil.create("Problems executing Android query: " + finalSql, e);
            }
        }
        return this.cursor;
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }

    static int execSql(SQLiteDatabase db, String label, String finalSql, Object[] argArray) throws SQLException {
        try {
            int result;
            db.execSQL(finalSql, argArray);
            SQLiteStatement stmt = null;
            try {
                stmt = db.compileStatement("SELECT CHANGES()");
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
            logger.trace("executing statement {} changed {} rows: {}", (Object) label, Integer.valueOf(result), (Object) finalSql);
            return result;
        } catch (android.database.SQLException e2) {
            throw SqlExceptionUtil.create("Problems executing " + label + " Android statement: " + finalSql, e2);
        }
    }

    private void isInPrep() throws SQLException {
        if (this.cursor != null) {
            throw new SQLException("Query already run. Cannot add argument values.");
        }
    }

    private Object[] getArgArray() {
        if (this.args == null) {
            return NO_STRING_ARGS;
        }
        return this.args.toArray(new Object[this.args.size()]);
    }

    private String[] getStringArray() {
        if (this.args == null) {
            return NO_STRING_ARGS;
        }
        return (String[]) this.args.toArray(new String[this.args.size()]);
    }
}
