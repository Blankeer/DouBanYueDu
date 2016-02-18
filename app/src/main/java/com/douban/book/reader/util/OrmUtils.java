package com.douban.book.reader.util;

import android.content.ContentValues;
import android.database.Cursor;
import com.douban.book.reader.database.AndroidDao;
import com.douban.book.reader.database.DatabaseHelper;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ReferenceObjectCache;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import io.realm.internal.Table;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Date;

public class OrmUtils {
    public static ContentValues getContentValues(Object obj) throws IllegalAccessException {
        ContentValues contentValues = new ContentValues();
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseField.class)) {
                DatabaseField databaseField = (DatabaseField) field.getAnnotation(DatabaseField.class);
                if (!databaseField.generatedId()) {
                    String columnName = databaseField.columnName();
                    if (Table.STRING_DEFAULT_VALUE.equals(columnName)) {
                        columnName = field.getName();
                    }
                    Date value = field.get(obj);
                    Class<?> type = field.getType();
                    String typeName = type.getSimpleName();
                    if (type == String.class) {
                        contentValues.put(columnName, (String) value);
                    } else if (type == Byte.class || typeName.equals("byte")) {
                        contentValues.put(columnName, (Byte) value);
                    } else if (type == Short.class || typeName.equals("short")) {
                        contentValues.put(columnName, (Short) value);
                    } else if (type == Integer.class || typeName.equals("int")) {
                        contentValues.put(columnName, (Integer) value);
                    } else if (type == Long.class || typeName.equals("long")) {
                        contentValues.put(columnName, (Long) value);
                    } else if (type == Float.class || typeName.equals("float")) {
                        contentValues.put(columnName, (Float) value);
                    } else if (type == Double.class || typeName.equals("double")) {
                        contentValues.put(columnName, (Double) value);
                    } else if (type == Boolean.class || typeName.equals("boolean")) {
                        contentValues.put(columnName, (Boolean) value);
                    } else if (type == byte[].class) {
                        contentValues.put(columnName, (byte[]) value);
                    } else if (type == Date.class) {
                        contentValues.put(columnName, Long.valueOf(value.getTime()));
                    }
                }
            }
        }
        return contentValues;
    }

    public static <T> T objectFromCursor(Cursor cursor, Class<T> cls) throws IllegalAccessException, InstantiationException {
        T obj = cls.newInstance();
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(DatabaseField.class)) {
                String columnName = ((DatabaseField) field.getAnnotation(DatabaseField.class)).columnName();
                if (Table.STRING_DEFAULT_VALUE.equals(columnName)) {
                    columnName = field.getName();
                }
                Object value = null;
                Class<?> type = field.getType();
                String typeName = type.getSimpleName();
                if (type == String.class) {
                    value = CursorUtils.getString(cursor, columnName);
                } else if (type == Byte.class || typeName.equals("byte")) {
                    throw new UnsupportedOperationException();
                } else if (type == Short.class || typeName.equals("short")) {
                    value = Short.valueOf(CursorUtils.getShort(cursor, columnName));
                } else if (type == Integer.class || typeName.equals("int")) {
                    value = Integer.valueOf(CursorUtils.getInt(cursor, columnName));
                } else if (type == Long.class || typeName.equals("long")) {
                    value = Long.valueOf(CursorUtils.getLong(cursor, columnName));
                } else if (type == Float.class || typeName.equals("float")) {
                    value = Float.valueOf(CursorUtils.getFloat(cursor, columnName));
                } else if (type == Double.class || typeName.equals("double")) {
                    value = Double.valueOf(CursorUtils.getDouble(cursor, columnName));
                } else if (type == Boolean.class || typeName.equals("boolean")) {
                    value = Boolean.valueOf(CursorUtils.getBoolean(cursor, columnName));
                } else if (type == byte[].class) {
                    value = CursorUtils.getBytes(cursor, columnName);
                } else if (type == Date.class) {
                    value = new Date(CursorUtils.getLong(cursor, columnName));
                }
                field.set(obj, value);
            }
        }
        return obj;
    }

    public static <T> AndroidDao<T, Object> getDao(Class<T> cls) {
        try {
            ConnectionSource conn = DatabaseHelper.getInstance().getConnectionSource();
            AndroidDao<T, Object> dao = (AndroidDao) DaoManager.lookupDao(conn, (Class) cls);
            if (dao == null) {
                dao = (AndroidDao) DaoManager.createDao(conn, (Class) cls);
            }
            if (dao.getObjectCache() == null) {
                dao.setObjectCache(new ReferenceObjectCache(true));
            }
            return dao;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
