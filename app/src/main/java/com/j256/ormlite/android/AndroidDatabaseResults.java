package com.j256.ormlite.android;

import android.database.Cursor;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteAndroidDatabaseType;
import com.j256.ormlite.support.DatabaseResults;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AndroidDatabaseResults implements DatabaseResults {
    private static final int MIN_NUM_COLUMN_NAMES_MAP = 8;
    private static final DatabaseType databaseType;
    private final Map<String, Integer> columnNameMap;
    private final String[] columnNames;
    private final Cursor cursor;
    private final ObjectCache objectCache;

    static {
        databaseType = new SqliteAndroidDatabaseType();
    }

    public AndroidDatabaseResults(Cursor cursor, ObjectCache objectCache) {
        this.cursor = cursor;
        this.columnNames = cursor.getColumnNames();
        if (this.columnNames.length >= MIN_NUM_COLUMN_NAMES_MAP) {
            this.columnNameMap = new HashMap();
            for (int i = 0; i < this.columnNames.length; i++) {
                this.columnNameMap.put(this.columnNames[i], Integer.valueOf(i));
            }
        } else {
            this.columnNameMap = null;
        }
        this.objectCache = objectCache;
    }

    @Deprecated
    public AndroidDatabaseResults(Cursor cursor, boolean firstCall, ObjectCache objectCache) {
        this(cursor, objectCache);
    }

    public int getColumnCount() {
        return this.cursor.getColumnCount();
    }

    public String[] getColumnNames() {
        int colN = getColumnCount();
        String[] columnNames = new String[colN];
        for (int colC = 0; colC < colN; colC++) {
            columnNames[colC] = this.cursor.getColumnName(colC);
        }
        return columnNames;
    }

    public boolean first() {
        return this.cursor.moveToFirst();
    }

    public boolean next() {
        return this.cursor.moveToNext();
    }

    public boolean last() {
        return this.cursor.moveToLast();
    }

    public boolean previous() {
        return this.cursor.moveToPrevious();
    }

    public boolean moveRelative(int offset) {
        return this.cursor.move(offset);
    }

    public boolean moveAbsolute(int position) {
        return this.cursor.moveToPosition(position);
    }

    public int getCount() {
        return this.cursor.getCount();
    }

    public int getPosition() {
        return this.cursor.getPosition();
    }

    public int findColumn(String columnName) throws SQLException {
        int index = lookupColumn(columnName);
        if (index >= 0) {
            return index;
        }
        StringBuilder sb = new StringBuilder(columnName.length() + 4);
        databaseType.appendEscapedEntityName(sb, columnName);
        index = lookupColumn(sb.toString());
        if (index >= 0) {
            return index;
        }
        throw new SQLException("Unknown field '" + columnName + "' from the Android sqlite cursor, not in:" + Arrays.toString(this.cursor.getColumnNames()));
    }

    public String getString(int columnIndex) {
        return this.cursor.getString(columnIndex);
    }

    public boolean getBoolean(int columnIndex) {
        if (this.cursor.isNull(columnIndex) || this.cursor.getShort(columnIndex) == (short) 0) {
            return false;
        }
        return true;
    }

    public char getChar(int columnIndex) throws SQLException {
        String string = this.cursor.getString(columnIndex);
        if (string == null || string.length() == 0) {
            return '\u0000';
        }
        if (string.length() == 1) {
            return string.charAt(0);
        }
        throw new SQLException("More than 1 character stored in database column: " + columnIndex);
    }

    public byte getByte(int columnIndex) {
        return (byte) getShort(columnIndex);
    }

    public byte[] getBytes(int columnIndex) {
        return this.cursor.getBlob(columnIndex);
    }

    public short getShort(int columnIndex) {
        return this.cursor.getShort(columnIndex);
    }

    public int getInt(int columnIndex) {
        return this.cursor.getInt(columnIndex);
    }

    public long getLong(int columnIndex) {
        return this.cursor.getLong(columnIndex);
    }

    public float getFloat(int columnIndex) {
        return this.cursor.getFloat(columnIndex);
    }

    public double getDouble(int columnIndex) {
        return this.cursor.getDouble(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new SQLException("Android does not support timestamp.  Use JAVA_DATE_LONG or JAVA_DATE_STRING types");
    }

    public InputStream getBlobStream(int columnIndex) {
        return new ByteArrayInputStream(this.cursor.getBlob(columnIndex));
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new SQLException("Android does not support BigDecimal type.  Use BIG_DECIMAL or BIG_DECIMAL_STRING types");
    }

    public boolean wasNull(int columnIndex) {
        return this.cursor.isNull(columnIndex);
    }

    public ObjectCache getObjectCache() {
        return this.objectCache;
    }

    public void close() {
        this.cursor.close();
    }

    public void closeQuietly() {
        close();
    }

    public Cursor getRawCursor() {
        return this.cursor;
    }

    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
    }

    private int lookupColumn(String columnName) {
        if (this.columnNameMap == null) {
            for (int i = 0; i < this.columnNames.length; i++) {
                if (this.columnNames[i].equals(columnName)) {
                    return i;
                }
            }
            return -1;
        }
        Integer index = (Integer) this.columnNameMap.get(columnName);
        if (index == null) {
            return -1;
        }
        return index.intValue();
    }
}
