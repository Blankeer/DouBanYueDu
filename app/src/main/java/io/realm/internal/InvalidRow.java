package io.realm.internal;

import java.util.Date;

public enum InvalidRow implements Row {
    INSTANCE;

    public long getColumnCount() {
        throw getStubException();
    }

    public String getColumnName(long columnIndex) {
        throw getStubException();
    }

    public long getColumnIndex(String columnName) {
        throw getStubException();
    }

    public ColumnType getColumnType(long columnIndex) {
        throw getStubException();
    }

    public Table getTable() {
        throw getStubException();
    }

    public long getIndex() {
        throw getStubException();
    }

    public long getLong(long columnIndex) {
        throw getStubException();
    }

    public boolean getBoolean(long columnIndex) {
        throw getStubException();
    }

    public float getFloat(long columnIndex) {
        throw getStubException();
    }

    public double getDouble(long columnIndex) {
        throw getStubException();
    }

    public Date getDate(long columnIndex) {
        throw getStubException();
    }

    public String getString(long columnIndex) {
        throw getStubException();
    }

    public byte[] getBinaryByteArray(long columnIndex) {
        throw getStubException();
    }

    public Mixed getMixed(long columnIndex) {
        throw getStubException();
    }

    public ColumnType getMixedType(long columnIndex) {
        throw getStubException();
    }

    public long getLink(long columnIndex) {
        throw getStubException();
    }

    public boolean isNullLink(long columnIndex) {
        throw getStubException();
    }

    public LinkView getLinkList(long columnIndex) {
        throw getStubException();
    }

    public void setLong(long columnIndex, long value) {
        throw getStubException();
    }

    public void setBoolean(long columnIndex, boolean value) {
        throw getStubException();
    }

    public void setFloat(long columnIndex, float value) {
        throw getStubException();
    }

    public void setDouble(long columnIndex, double value) {
        throw getStubException();
    }

    public void setDate(long columnIndex, Date date) {
        throw getStubException();
    }

    public void setString(long columnIndex, String value) {
        throw getStubException();
    }

    public void setBinaryByteArray(long columnIndex, byte[] data) {
        throw getStubException();
    }

    public void setMixed(long columnIndex, Mixed data) {
        throw getStubException();
    }

    public void setLink(long columnIndex, long value) {
        throw getStubException();
    }

    public void nullifyLink(long columnIndex) {
        throw getStubException();
    }

    public boolean isAttached() {
        throw getStubException();
    }

    public boolean hasColumn(String fieldName) {
        throw getStubException();
    }

    private RuntimeException getStubException() {
        return new IllegalStateException("Object is no longer managed by Realm. Has it been deleted?");
    }
}
