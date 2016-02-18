package io.realm.internal;

import java.util.Date;

public interface Row {
    byte[] getBinaryByteArray(long j);

    boolean getBoolean(long j);

    long getColumnCount();

    long getColumnIndex(String str);

    String getColumnName(long j);

    ColumnType getColumnType(long j);

    Date getDate(long j);

    double getDouble(long j);

    float getFloat(long j);

    long getIndex();

    long getLink(long j);

    LinkView getLinkList(long j);

    long getLong(long j);

    Mixed getMixed(long j);

    ColumnType getMixedType(long j);

    String getString(long j);

    Table getTable();

    boolean hasColumn(String str);

    boolean isAttached();

    boolean isNullLink(long j);

    void nullifyLink(long j);

    void setBinaryByteArray(long j, byte[] bArr);

    void setBoolean(long j, boolean z);

    void setDate(long j, Date date);

    void setDouble(long j, double d);

    void setFloat(long j, float f);

    void setLink(long j, long j2);

    void setLong(long j, long j2);

    void setMixed(long j, Mixed mixed);

    void setString(long j, String str);
}
