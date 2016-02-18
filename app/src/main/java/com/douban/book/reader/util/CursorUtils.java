package com.douban.book.reader.util;

import android.database.Cursor;
import com.j256.ormlite.stmt.query.SimpleComparison;
import u.aly.dx;

public class CursorUtils {
    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }

    public static short getShort(Cursor cursor, String columnName) {
        return cursor.getShort(cursor.getColumnIndex(columnName));
    }

    public static float getFloat(Cursor cursor, String columnName) {
        return cursor.getFloat(cursor.getColumnIndex(columnName));
    }

    public static double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndex(columnName));
    }

    public static boolean getBoolean(Cursor cursor, String columnName) {
        if (getInt(cursor, columnName) == 0) {
            return false;
        }
        return true;
    }

    public static byte[] getBytes(Cursor cursor, String columnName) {
        return cursor.getBlob(cursor.getColumnIndex(columnName));
    }

    public static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public static String dumpCurrentRow(Cursor cursor) {
        StringBuilder builder = new StringBuilder();
        builder.append("Row ").append(cursor.getPosition()).append(" of ").append(cursor.getCount()).append(": ");
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            builder.append(cursor.getColumnName(i)).append(SimpleComparison.EQUAL_TO_OPERATION);
            switch (cursor.getType(i)) {
                case dx.a /*0*/:
                    builder.append("null");
                    break;
                case dx.b /*1*/:
                    builder.append(cursor.getInt(i));
                    break;
                case dx.c /*2*/:
                    builder.append(cursor.getFloat(i));
                    break;
                case dx.d /*3*/:
                    builder.append(cursor.getString(i));
                    break;
                case dx.e /*4*/:
                    builder.append("blob");
                    break;
                default:
                    builder.append("<unknown value type>");
                    break;
            }
            builder.append("; ");
        }
        return builder.toString();
    }
}
