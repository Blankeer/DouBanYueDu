package io.realm.internal;

import java.util.Date;

public interface TableOrView {
    public static final int NO_MATCH = -1;

    public enum PivotType {
        COUNT(0),
        SUM(1),
        AVG(2),
        MIN(3),
        MAX(4);
        
        final int value;

        private PivotType(int value) {
            this.value = value;
        }
    }

    void adjust(long j, long j2);

    double averageDouble(long j);

    double averageFloat(long j);

    double averageLong(long j);

    void clear();

    void clearSubtable(long j, long j2);

    void close();

    long count(long j, String str);

    TableView findAllBoolean(long j, boolean z);

    TableView findAllDate(long j, Date date);

    TableView findAllDouble(long j, double d);

    TableView findAllFloat(long j, float f);

    TableView findAllLong(long j, long j2);

    TableView findAllString(long j, String str);

    long findFirstBoolean(long j, boolean z);

    long findFirstDate(long j, Date date);

    long findFirstDouble(long j, double d);

    long findFirstFloat(long j, float f);

    long findFirstLong(long j, long j2);

    long findFirstString(long j, String str);

    byte[] getBinaryByteArray(long j, long j2);

    boolean getBoolean(long j, long j2);

    long getColumnCount();

    long getColumnIndex(String str);

    String getColumnName(long j);

    ColumnType getColumnType(long j);

    Date getDate(long j, long j2);

    double getDouble(long j, long j2);

    float getFloat(long j, long j2);

    long getLink(long j, long j2);

    long getLong(long j, long j2);

    Mixed getMixed(long j, long j2);

    ColumnType getMixedType(long j, long j2);

    String getString(long j, long j2);

    Table getSubtable(long j, long j2);

    long getSubtableSize(long j, long j2);

    Table getTable();

    boolean isEmpty();

    boolean isNullLink(long j, long j2);

    long lowerBoundLong(long j, long j2);

    Date maximumDate(long j);

    double maximumDouble(long j);

    float maximumFloat(long j);

    long maximumLong(long j);

    Date minimumDate(long j);

    double minimumDouble(long j);

    float minimumFloat(long j);

    long minimumLong(long j);

    void nullifyLink(long j, long j2);

    Table pivot(long j, long j2, PivotType pivotType);

    void remove(long j);

    void removeLast();

    String rowToString(long j);

    void setBinaryByteArray(long j, long j2, byte[] bArr);

    void setBoolean(long j, long j2, boolean z);

    void setDate(long j, long j2, Date date);

    void setDouble(long j, long j2, double d);

    void setFloat(long j, long j2, float f);

    void setLink(long j, long j2, long j3);

    void setLong(long j, long j2, long j3);

    void setMixed(long j, long j2, Mixed mixed);

    void setString(long j, long j2, String str);

    long size();

    double sumDouble(long j);

    double sumFloat(long j);

    long sumLong(long j);

    long sync();

    String toJson();

    String toString();

    String toString(long j);

    long upperBoundLong(long j, long j2);

    TableQuery where();
}
