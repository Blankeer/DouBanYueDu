package com.j256.ormlite.field.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;

public class SerializableType extends BaseDataType {
    private static final SerializableType singleTon;

    static {
        singleTon = new SerializableType();
    }

    public static SerializableType getSingleton() {
        return singleTon;
    }

    private SerializableType() {
        super(SqlType.SERIALIZABLE, new Class[0]);
    }

    protected SerializableType(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        throw new SQLException("Default values for serializable types are not supported");
    }

    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getBytes(columnPos);
    }

    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        Exception e;
        Throwable th;
        byte[] bytes = (byte[]) sqlArg;
        ObjectInputStream objInStream = null;
        try {
            ObjectInputStream objInStream2 = new ObjectInputStream(new ByteArrayInputStream(bytes));
            try {
                Object readObject = objInStream2.readObject();
                if (objInStream2 != null) {
                    try {
                        objInStream2.close();
                    } catch (IOException e2) {
                    }
                }
                return readObject;
            } catch (Exception e3) {
                e = e3;
                objInStream = objInStream2;
                try {
                    throw SqlExceptionUtil.create("Could not read serialized object from byte array: " + Arrays.toString(bytes) + "(len " + bytes.length + ")", e);
                } catch (Throwable th2) {
                    th = th2;
                    if (objInStream != null) {
                        try {
                            objInStream.close();
                        } catch (IOException e4) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                objInStream = objInStream2;
                if (objInStream != null) {
                    objInStream.close();
                }
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            throw SqlExceptionUtil.create("Could not read serialized object from byte array: " + Arrays.toString(bytes) + "(len " + bytes.length + ")", e);
        }
    }

    public Object javaToSqlArg(FieldType fieldType, Object obj) throws SQLException {
        Exception e;
        Throwable th;
        ObjectOutputStream objectOutputStream = null;
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
            try {
                objOutStream.writeObject(obj);
                objOutStream.close();
                objectOutputStream = null;
                Object toByteArray = outStream.toByteArray();
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.close();
                    } catch (IOException e2) {
                    }
                }
                return toByteArray;
            } catch (Exception e3) {
                e = e3;
                objectOutputStream = objOutStream;
                try {
                    throw SqlExceptionUtil.create("Could not write serialized object to byte array: " + obj, e);
                } catch (Throwable th2) {
                    th = th2;
                    if (objectOutputStream != null) {
                        try {
                            objectOutputStream.close();
                        } catch (IOException e4) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                objectOutputStream = objOutStream;
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            throw SqlExceptionUtil.create("Could not write serialized object to byte array: " + obj, e);
        }
    }

    public boolean isValidForField(Field field) {
        return Serializable.class.isAssignableFrom(field.getType());
    }

    public boolean isStreamType() {
        return true;
    }

    public boolean isComparable() {
        return false;
    }

    public boolean isAppropriateId() {
        return false;
    }

    public boolean isArgumentHolderRequired() {
        return true;
    }

    public Object resultStringToJava(FieldType fieldType, String stringValue, int columnPos) throws SQLException {
        throw new SQLException("Serializable type cannot be converted from string to Java");
    }

    public Class<?> getPrimaryClass() {
        return Serializable.class;
    }
}
