package io.realm.internal;

import com.alipay.sdk.protocol.h;
import io.realm.exceptions.RealmException;
import io.realm.internal.TableOrView.PivotType;
import io.realm.internal.TableView.Order;
import java.io.Closeable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class Table implements TableOrView, TableSchema, Closeable {
    public static final long INFINITE = -1;
    public static final long INTEGER_DEFAULT_VALUE = 0;
    private static final long NO_PRIMARY_KEY = -2;
    private static final long PRIMARY_KEY_CLASS_COLUMN_INDEX = 0;
    private static final String PRIMARY_KEY_CLASS_COLUMN_NAME = "pk_table";
    private static final long PRIMARY_KEY_FIELD_COLUMN_INDEX = 1;
    private static final String PRIMARY_KEY_FIELD_COLUMN_NAME = "pk_property";
    private static final String PRIMARY_KEY_TABLE_NAME = "pk";
    public static final String STRING_DEFAULT_VALUE = "";
    public static final String TABLE_PREFIX = "class_";
    static AtomicInteger tableCount;
    protected boolean DEBUG;
    private long cachedPrimaryKeyColumnIndex;
    private final Context context;
    private InternalMethods internal;
    protected long nativePtr;
    protected final Object parent;
    protected int tableNo;

    /* renamed from: io.realm.internal.Table.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$realm$internal$ColumnType;

        static {
            $SwitchMap$io$realm$internal$ColumnType = new int[ColumnType.values().length];
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.INTEGER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.BOOLEAN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.FLOAT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.DOUBLE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.DATE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.MIXED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.BINARY.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.TABLE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public class InternalMethods {
        public void insertLong(long columnIndex, long rowIndex, long value) {
            Table.this.checkImmutable();
            Table.this.nativeInsertLong(Table.this.nativePtr, columnIndex, rowIndex, value);
        }

        public void insertDouble(long columnIndex, long rowIndex, double value) {
            Table.this.checkImmutable();
            Table.this.nativeInsertDouble(Table.this.nativePtr, columnIndex, rowIndex, value);
        }

        public void insertFloat(long columnIndex, long rowIndex, float value) {
            Table.this.checkImmutable();
            Table.this.nativeInsertFloat(Table.this.nativePtr, columnIndex, rowIndex, value);
        }

        public void insertBoolean(long columnIndex, long rowIndex, boolean value) {
            Table.this.checkImmutable();
            Table.this.nativeInsertBoolean(Table.this.nativePtr, columnIndex, rowIndex, value);
        }

        public void insertDate(long columnIndex, long rowIndex, Date date) {
            Table.this.checkImmutable();
            Table.this.nativeInsertDate(Table.this.nativePtr, columnIndex, rowIndex, date.getTime() / 1000);
        }

        public void insertString(long columnIndex, long rowIndex, String value) {
            Table.this.checkImmutable();
            Table.this.nativeInsertString(Table.this.nativePtr, columnIndex, rowIndex, value);
        }

        public void insertMixed(long columnIndex, long rowIndex, Mixed data) {
            Table.this.checkImmutable();
            Table.this.nativeInsertMixed(Table.this.nativePtr, columnIndex, rowIndex, data);
        }

        public void insertBinary(long columnIndex, long rowIndex, byte[] data) {
            Table.this.checkImmutable();
            if (data != null) {
                Table.this.nativeInsertByteArray(Table.this.nativePtr, columnIndex, rowIndex, data);
                return;
            }
            throw new IllegalArgumentException("byte[] must not be null. Alternatively insert empty array.");
        }

        public void insertSubtable(long columnIndex, long rowIndex, Object[][] values) {
            Table.this.checkImmutable();
            Table.this.nativeInsertSubtable(Table.this.nativePtr, columnIndex, rowIndex);
            Table.this.insertSubtableValues(rowIndex, columnIndex, values);
        }

        public void insertDone() {
            Table.this.checkImmutable();
            Table.this.nativeInsertDone(Table.this.nativePtr);
        }
    }

    protected static native void nativeClose(long j);

    private native long nativeGetSubtableDuringInsert(long j, long j2, long j3);

    private native void nativeInsertLinkList(long j, long j2, long j3);

    private native void nativeMigratePrimaryKeyTableIfNeeded(long j, long j2);

    private native long nativeSetPrimaryKey(long j, long j2, String str);

    protected native long createNative();

    protected native long nativeAddColumn(long j, int i, String str);

    protected native long nativeAddColumnLink(long j, int i, String str, long j2);

    protected native long nativeAddEmptyRow(long j, long j2);

    protected native void nativeAddInt(long j, long j2, long j3);

    protected native void nativeAddSearchIndex(long j, long j2);

    protected native double nativeAverageDouble(long j, long j2);

    protected native double nativeAverageFloat(long j, long j2);

    protected native double nativeAverageInt(long j, long j2);

    protected native void nativeClear(long j);

    protected native void nativeClearSubtable(long j, long j2, long j3);

    protected native long nativeCountDouble(long j, long j2, double d);

    protected native long nativeCountFloat(long j, long j2, float f);

    protected native long nativeCountLong(long j, long j2, long j3);

    protected native long nativeCountString(long j, long j2, String str);

    protected native long nativeFindAllBool(long j, long j2, boolean z);

    protected native long nativeFindAllDate(long j, long j2, long j3);

    protected native long nativeFindAllDouble(long j, long j2, double d);

    protected native long nativeFindAllFloat(long j, long j2, float f);

    protected native long nativeFindAllInt(long j, long j2, long j3);

    protected native long nativeFindAllString(long j, long j2, String str);

    protected native long nativeFindFirstBool(long j, long j2, boolean z);

    protected native long nativeFindFirstDate(long j, long j2, long j3);

    protected native long nativeFindFirstDouble(long j, long j2, double d);

    protected native long nativeFindFirstFloat(long j, long j2, float f);

    protected native long nativeFindFirstInt(long j, long j2, long j3);

    protected native long nativeFindFirstString(long j, long j2, String str);

    protected native boolean nativeGetBoolean(long j, long j2, long j3);

    protected native byte[] nativeGetByteArray(long j, long j2, long j3);

    protected native long nativeGetColumnCount(long j);

    protected native long nativeGetColumnIndex(long j, String str);

    protected native String nativeGetColumnName(long j, long j2);

    protected native int nativeGetColumnType(long j, long j2);

    protected native long nativeGetDateTime(long j, long j2, long j3);

    protected native long nativeGetDistinctView(long j, long j2);

    protected native double nativeGetDouble(long j, long j2, long j3);

    protected native float nativeGetFloat(long j, long j2, long j3);

    protected native long nativeGetLink(long j, long j2, long j3);

    protected native long nativeGetLinkTarget(long j, long j2);

    protected native long nativeGetLong(long j, long j2, long j3);

    protected native Mixed nativeGetMixed(long j, long j2, long j3);

    protected native int nativeGetMixedType(long j, long j2, long j3);

    protected native String nativeGetName(long j);

    protected native long nativeGetRowPtr(long j, long j2);

    protected native long nativeGetSortedView(long j, long j2, boolean z);

    protected native long nativeGetSortedViewMulti(long j, long[] jArr, boolean[] zArr);

    protected native String nativeGetString(long j, long j2, long j3);

    protected native long nativeGetSubtable(long j, long j2, long j3);

    protected native long nativeGetSubtableSize(long j, long j2, long j3);

    protected native TableSpec nativeGetTableSpec(long j);

    protected native boolean nativeHasSameSchema(long j, long j2);

    protected native boolean nativeHasSearchIndex(long j, long j2);

    protected native void nativeInsertBoolean(long j, long j2, long j3, boolean z);

    protected native void nativeInsertByteArray(long j, long j2, long j3, byte[] bArr);

    protected native void nativeInsertDate(long j, long j2, long j3, long j4);

    protected native void nativeInsertDone(long j);

    protected native void nativeInsertDouble(long j, long j2, long j3, double d);

    protected native void nativeInsertFloat(long j, long j2, long j3, float f);

    protected native void nativeInsertLong(long j, long j2, long j3, long j4);

    protected native void nativeInsertMixed(long j, long j2, long j3, Mixed mixed);

    protected native void nativeInsertString(long j, long j2, long j3, String str);

    protected native void nativeInsertSubtable(long j, long j2, long j3);

    protected native boolean nativeIsNullLink(long j, long j2, long j3);

    protected native boolean nativeIsRootTable(long j);

    protected native boolean nativeIsValid(long j);

    protected native long nativeLowerBoundInt(long j, long j2, long j3);

    protected native long nativeMaximumDate(long j, long j2);

    protected native double nativeMaximumDouble(long j, long j2);

    protected native float nativeMaximumFloat(long j, long j2);

    protected native long nativeMaximumInt(long j, long j2);

    protected native long nativeMinimumDate(long j, long j2);

    protected native double nativeMinimumDouble(long j, long j2);

    protected native float nativeMinimumFloat(long j, long j2);

    protected native long nativeMinimumInt(long j, long j2);

    protected native void nativeMoveLastOver(long j, long j2);

    protected native void nativeNullifyLink(long j, long j2, long j3);

    protected native void nativeOptimize(long j);

    protected native void nativePivot(long j, long j2, long j3, int i, long j4);

    protected native void nativeRemove(long j, long j2);

    protected native void nativeRemoveColumn(long j, long j2);

    protected native void nativeRemoveLast(long j);

    protected native void nativeRemoveSearchIndex(long j, long j2);

    protected native void nativeRenameColumn(long j, long j2, String str);

    protected native String nativeRowToString(long j, long j2);

    protected native void nativeSetBoolean(long j, long j2, long j3, boolean z);

    protected native void nativeSetByteArray(long j, long j2, long j3, byte[] bArr);

    protected native void nativeSetDate(long j, long j2, long j3, long j4);

    protected native void nativeSetDouble(long j, long j2, long j3, double d);

    protected native void nativeSetFloat(long j, long j2, long j3, float f);

    protected native void nativeSetLink(long j, long j2, long j3, long j4);

    protected native void nativeSetLong(long j, long j2, long j3, long j4);

    protected native void nativeSetMixed(long j, long j2, long j3, Mixed mixed);

    protected native void nativeSetString(long j, long j2, long j3, String str);

    protected native long nativeSize(long j);

    protected native double nativeSumDouble(long j, long j2);

    protected native double nativeSumFloat(long j, long j2);

    protected native long nativeSumInt(long j, long j2);

    protected native String nativeToJson(long j);

    protected native String nativeToString(long j, long j2);

    protected native void nativeUpdateFromSpec(long j, TableSpec tableSpec);

    protected native long nativeUpperBoundInt(long j, long j2, long j3);

    protected native long nativeWhere(long j);

    static {
        tableCount = new AtomicInteger(0);
        RealmCore.loadLibrary();
    }

    public Table() {
        this.cachedPrimaryKeyColumnIndex = INFINITE;
        this.DEBUG = false;
        this.internal = new InternalMethods();
        this.parent = null;
        this.context = new Context();
        this.nativePtr = createNative();
        if (this.nativePtr == PRIMARY_KEY_CLASS_COLUMN_INDEX) {
            throw new OutOfMemoryError("Out of native memory.");
        } else if (this.DEBUG) {
            this.tableNo = tableCount.incrementAndGet();
            System.err.println("====== New Tablebase " + this.tableNo + " : ptr = " + this.nativePtr);
        }
    }

    Table(Context context, Object parent, long nativePointer) {
        this.cachedPrimaryKeyColumnIndex = INFINITE;
        this.DEBUG = false;
        this.internal = new InternalMethods();
        this.context = context;
        this.parent = parent;
        this.nativePtr = nativePointer;
        if (this.DEBUG) {
            this.tableNo = tableCount.incrementAndGet();
            System.err.println("===== New Tablebase(ptr) " + this.tableNo + " : ptr = " + this.nativePtr);
        }
    }

    public Table getTable() {
        return this;
    }

    public void close() {
        synchronized (this.context) {
            if (this.nativePtr != PRIMARY_KEY_CLASS_COLUMN_INDEX) {
                nativeClose(this.nativePtr);
                if (this.DEBUG) {
                    tableCount.decrementAndGet();
                    System.err.println("==== CLOSE " + this.tableNo + " ptr= " + this.nativePtr + " remaining " + tableCount.get());
                }
                this.nativePtr = PRIMARY_KEY_CLASS_COLUMN_INDEX;
            }
        }
    }

    protected void finalize() {
        synchronized (this.context) {
            if (this.nativePtr != PRIMARY_KEY_CLASS_COLUMN_INDEX) {
                this.context.asyncDisposeTable(this.nativePtr, this.parent == null);
                this.nativePtr = PRIMARY_KEY_CLASS_COLUMN_INDEX;
            }
        }
        if (this.DEBUG) {
            System.err.println("==== FINALIZE " + this.tableNo + "...");
        }
    }

    public boolean isValid() {
        return this.nativePtr != PRIMARY_KEY_CLASS_COLUMN_INDEX && nativeIsValid(this.nativePtr);
    }

    private void verifyColumnName(String name) {
        if (name.length() > 63) {
            throw new IllegalArgumentException("Column names are currently limited to max 63 characters.");
        }
    }

    public TableSchema getSubtableSchema(long columnIndex) {
        if (nativeIsRootTable(this.nativePtr)) {
            return new SubtableSchema(this.nativePtr, new long[]{columnIndex});
        }
        throw new UnsupportedOperationException("This is a subtable. Can only be called on root table.");
    }

    public long addColumn(ColumnType type, String name) {
        verifyColumnName(name);
        return nativeAddColumn(this.nativePtr, type.getValue(), name);
    }

    public long addColumnLink(ColumnType type, String name, Table table) {
        verifyColumnName(name);
        return nativeAddColumnLink(this.nativePtr, type.getValue(), name, table.nativePtr);
    }

    public void removeColumn(long columnIndex) {
        nativeRemoveColumn(this.nativePtr, columnIndex);
    }

    public void renameColumn(long columnIndex, String newName) {
        verifyColumnName(newName);
        nativeRenameColumn(this.nativePtr, columnIndex, newName);
    }

    public void updateFromSpec(TableSpec tableSpec) {
        checkImmutable();
        nativeUpdateFromSpec(this.nativePtr, tableSpec);
    }

    public long size() {
        return nativeSize(this.nativePtr);
    }

    public boolean isEmpty() {
        return size() == PRIMARY_KEY_CLASS_COLUMN_INDEX;
    }

    public void clear() {
        checkImmutable();
        nativeClear(this.nativePtr);
    }

    public long getColumnCount() {
        return nativeGetColumnCount(this.nativePtr);
    }

    public TableSpec getTableSpec() {
        return nativeGetTableSpec(this.nativePtr);
    }

    public String getColumnName(long columnIndex) {
        return nativeGetColumnName(this.nativePtr, columnIndex);
    }

    public long getColumnIndex(String columnName) {
        if (columnName != null) {
            return nativeGetColumnIndex(this.nativePtr, columnName);
        }
        throw new IllegalArgumentException("Column name can not be null.");
    }

    public ColumnType getColumnType(long columnIndex) {
        return ColumnType.fromNativeValue(nativeGetColumnType(this.nativePtr, columnIndex));
    }

    public void remove(long rowIndex) {
        checkImmutable();
        nativeRemove(this.nativePtr, rowIndex);
    }

    public void removeLast() {
        checkImmutable();
        nativeRemoveLast(this.nativePtr);
    }

    public void moveLastOver(long rowIndex) {
        checkImmutable();
        nativeMoveLastOver(this.nativePtr, rowIndex);
    }

    public long addEmptyRow() {
        checkImmutable();
        if (hasPrimaryKey()) {
            long primaryKeyColumnIndex = getPrimaryKey();
            ColumnType type = getColumnType(primaryKeyColumnIndex);
            switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[type.ordinal()]) {
                case dx.b /*1*/:
                    if (findFirstString(primaryKeyColumnIndex, STRING_DEFAULT_VALUE) != INFINITE) {
                        throwDuplicatePrimaryKeyException(STRING_DEFAULT_VALUE);
                        break;
                    }
                    break;
                case dx.c /*2*/:
                    if (findFirstLong(primaryKeyColumnIndex, PRIMARY_KEY_CLASS_COLUMN_INDEX) != INFINITE) {
                        throwDuplicatePrimaryKeyException(Long.valueOf(PRIMARY_KEY_CLASS_COLUMN_INDEX));
                        break;
                    }
                    break;
                default:
                    throw new RealmException("Cannot check for duplicate rows for unsupported primary key type: " + type);
            }
        }
        return nativeAddEmptyRow(this.nativePtr, PRIMARY_KEY_FIELD_COLUMN_INDEX);
    }

    public long addEmptyRowWithPrimaryKey(Object primaryKeyValue) {
        checkImmutable();
        checkHasPrimaryKey();
        long primaryKeyColumnIndex = getPrimaryKey();
        ColumnType type = getColumnType(primaryKeyColumnIndex);
        long rowIndex;
        switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[type.ordinal()]) {
            case dx.b /*1*/:
                if (primaryKeyValue instanceof String) {
                    if (findFirstString(primaryKeyColumnIndex, (String) primaryKeyValue) != INFINITE) {
                        throwDuplicatePrimaryKeyException(primaryKeyValue);
                    }
                    rowIndex = nativeAddEmptyRow(this.nativePtr, PRIMARY_KEY_FIELD_COLUMN_INDEX);
                    getUncheckedRow(rowIndex).setString(primaryKeyColumnIndex, (String) primaryKeyValue);
                    return rowIndex;
                }
                throw new IllegalArgumentException("Primary key value is not a String: " + primaryKeyValue);
            case dx.c /*2*/:
                try {
                    long pkValue = Long.parseLong(primaryKeyValue.toString());
                    if (findFirstLong(primaryKeyColumnIndex, pkValue) != INFINITE) {
                        throwDuplicatePrimaryKeyException(Long.valueOf(pkValue));
                    }
                    rowIndex = nativeAddEmptyRow(this.nativePtr, PRIMARY_KEY_FIELD_COLUMN_INDEX);
                    getUncheckedRow(rowIndex).setLong(primaryKeyColumnIndex, pkValue);
                    return rowIndex;
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("Primary key value is not a long: " + primaryKeyValue);
                }
            default:
                throw new RealmException("Cannot check for duplicate rows for unsupported primary key type: " + type);
        }
    }

    public long addEmptyRows(long rows) {
        checkImmutable();
        if (rows < PRIMARY_KEY_FIELD_COLUMN_INDEX) {
            throw new IllegalArgumentException("'rows' must be > 0.");
        } else if (!hasPrimaryKey()) {
            return nativeAddEmptyRow(this.nativePtr, rows);
        } else {
            if (rows <= PRIMARY_KEY_FIELD_COLUMN_INDEX) {
                return addEmptyRow();
            }
            throw new RealmException("Multiple empty rows cannot be created if a primary key is defined for the table.");
        }
    }

    public long add(Object... values) {
        long rowIndex = size();
        addAt(rowIndex, values);
        return rowIndex;
    }

    public void addAt(long rowIndex, Object... values) {
        checkImmutable();
        long size = size();
        if (rowIndex > size) {
            throw new IllegalArgumentException("rowIndex " + String.valueOf(rowIndex) + " must be <= table.size() " + String.valueOf(size) + ".");
        }
        int columns = (int) getColumnCount();
        if (columns != values.length) {
            throw new IllegalArgumentException("The number of value parameters (" + String.valueOf(values.length) + ") does not match the number of columns in the table (" + String.valueOf(columns) + ").");
        }
        ColumnType[] colTypes = new ColumnType[columns];
        int columnIndex = 0;
        while (columnIndex < columns) {
            Object value = values[columnIndex];
            ColumnType colType = getColumnType((long) columnIndex);
            colTypes[columnIndex] = colType;
            if (colType.matchObject(value)) {
                columnIndex++;
            } else {
                String providedType;
                if (value == null) {
                    providedType = "null";
                } else {
                    providedType = value.getClass().toString();
                }
                throw new IllegalArgumentException("Invalid argument no " + String.valueOf(columnIndex + 1) + ". Expected a value compatible with column type " + colType + ", but got " + providedType + ".");
            }
        }
        for (long columnIndex2 = PRIMARY_KEY_CLASS_COLUMN_INDEX; columnIndex2 < ((long) columns); columnIndex2 += PRIMARY_KEY_FIELD_COLUMN_INDEX) {
            String value2 = values[(int) columnIndex2];
            switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[colTypes[(int) columnIndex2].ordinal()]) {
                case dx.b /*1*/:
                    checkStringValueIsLegal(columnIndex2, rowIndex, value2);
                    nativeInsertString(this.nativePtr, columnIndex2, rowIndex, value2);
                    break;
                case dx.c /*2*/:
                    long intValue = ((Number) value2).longValue();
                    checkIntValueIsLegal(columnIndex2, rowIndex, intValue);
                    nativeInsertLong(this.nativePtr, columnIndex2, rowIndex, intValue);
                    break;
                case dx.d /*3*/:
                    nativeInsertBoolean(this.nativePtr, columnIndex2, rowIndex, ((Boolean) value2).booleanValue());
                    break;
                case dx.e /*4*/:
                    nativeInsertFloat(this.nativePtr, columnIndex2, rowIndex, ((Float) value2).floatValue());
                    break;
                case dj.f /*5*/:
                    nativeInsertDouble(this.nativePtr, columnIndex2, rowIndex, ((Double) value2).doubleValue());
                    break;
                case ci.g /*6*/:
                    nativeInsertDate(this.nativePtr, columnIndex2, rowIndex, ((Date) value2).getTime() / 1000);
                    break;
                case ci.h /*7*/:
                    nativeInsertMixed(this.nativePtr, columnIndex2, rowIndex, Mixed.mixedValue(value2));
                    break;
                case h.g /*8*/:
                    long j = columnIndex2;
                    long j2 = rowIndex;
                    nativeInsertByteArray(this.nativePtr, j, j2, (byte[]) value2);
                    break;
                case h.h /*9*/:
                    nativeInsertSubtable(this.nativePtr, columnIndex2, rowIndex);
                    insertSubtableValues(rowIndex, columnIndex2, value2);
                    break;
                default:
                    throw new RuntimeException("Unexpected columnType: " + String.valueOf(colTypes[(int) columnIndex2]));
            }
        }
        nativeInsertDone(this.nativePtr);
    }

    private boolean isPrimaryKeyColumn(long columnIndex) {
        return columnIndex == getPrimaryKey();
    }

    private void insertSubtableValues(long rowIndex, long columnIndex, Object value) {
        if (value != null) {
            Table subtable = getSubtableDuringInsert(columnIndex, rowIndex);
            int rows = ((Object[]) value).length;
            for (int i = 0; i < rows; i++) {
                subtable.addAt((long) i, (Object[]) ((Object[]) value)[i]);
            }
        }
    }

    public void insertLinkList(long columnIndex, long rowIndex) {
        nativeInsertLinkList(this.nativePtr, columnIndex, rowIndex);
        getInternalMethods().insertDone();
    }

    public TableView getSortedView(long columnIndex, Order order) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeGetSortedView(this.nativePtr, columnIndex, order == Order.ascending);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView getSortedView(long columnIndex) {
        this.context.executeDelayedDisposal();
        return new TableView(this.context, this, nativeGetSortedView(this.nativePtr, columnIndex, true));
    }

    public TableView getSortedView(long[] columnIndices, boolean[] orders) {
        this.context.executeDelayedDisposal();
        return new TableView(this.context, this, nativeGetSortedViewMulti(this.nativePtr, columnIndices, orders));
    }

    public void set(long rowIndex, Object... values) {
        checkImmutable();
        long size = size();
        if (rowIndex >= size) {
            throw new IllegalArgumentException("rowIndex " + String.valueOf(rowIndex) + " must be < table.size() " + String.valueOf(size) + ".");
        }
        int columns = (int) getColumnCount();
        if (columns != values.length) {
            throw new IllegalArgumentException("The number of value parameters (" + String.valueOf(values.length) + ") does not match the number of columns in the table (" + String.valueOf(columns) + ").");
        }
        int columnIndex = 0;
        while (columnIndex < columns) {
            Object value = values[columnIndex];
            ColumnType colType = getColumnType((long) columnIndex);
            if (colType.matchObject(value)) {
                columnIndex++;
            } else {
                throw new IllegalArgumentException("Invalid argument no " + String.valueOf(columnIndex + 1) + ". Expected a value compatible with column type " + colType + ", but got " + value.getClass() + ".");
            }
        }
        remove(rowIndex);
        addAt(rowIndex, values);
    }

    public InternalMethods getInternalMethods() {
        return this.internal;
    }

    public long getPrimaryKey() {
        if (this.cachedPrimaryKeyColumnIndex >= PRIMARY_KEY_CLASS_COLUMN_INDEX || this.cachedPrimaryKeyColumnIndex == NO_PRIMARY_KEY) {
            return this.cachedPrimaryKeyColumnIndex;
        }
        Table pkTable = getPrimaryKeyTable();
        if (pkTable == null) {
            return NO_PRIMARY_KEY;
        }
        long rowIndex = pkTable.findFirstString(PRIMARY_KEY_CLASS_COLUMN_INDEX, getName());
        if (rowIndex != INFINITE) {
            this.cachedPrimaryKeyColumnIndex = getColumnIndex(pkTable.getUncheckedRow(rowIndex).getString(PRIMARY_KEY_FIELD_COLUMN_INDEX));
        } else {
            this.cachedPrimaryKeyColumnIndex = NO_PRIMARY_KEY;
        }
        return this.cachedPrimaryKeyColumnIndex;
    }

    public boolean isPrimaryKey(long columnIndex) {
        return columnIndex >= PRIMARY_KEY_CLASS_COLUMN_INDEX && columnIndex == getPrimaryKey();
    }

    public boolean hasPrimaryKey() {
        return getPrimaryKey() >= PRIMARY_KEY_CLASS_COLUMN_INDEX;
    }

    void checkStringValueIsLegal(long columnIndex, long rowToUpdate, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Null String is not allowed.");
        } else if (isPrimaryKey(columnIndex)) {
            long rowIndex = findFirstString(columnIndex, value);
            if (rowIndex != rowToUpdate && rowIndex != INFINITE) {
                throwDuplicatePrimaryKeyException(value);
            }
        }
    }

    void checkIntValueIsLegal(long columnIndex, long rowToUpdate, long value) {
        if (isPrimaryKeyColumn(columnIndex)) {
            long rowIndex = findFirstLong(columnIndex, value);
            if (rowIndex != rowToUpdate && rowIndex != INFINITE) {
                throwDuplicatePrimaryKeyException(Long.valueOf(value));
            }
        }
    }

    private void throwDuplicatePrimaryKeyException(Object value) {
        throw new RealmException("Primary key constraint broken. Value already exists: " + value);
    }

    public long getLong(long columnIndex, long rowIndex) {
        return nativeGetLong(this.nativePtr, columnIndex, rowIndex);
    }

    public boolean getBoolean(long columnIndex, long rowIndex) {
        return nativeGetBoolean(this.nativePtr, columnIndex, rowIndex);
    }

    public float getFloat(long columnIndex, long rowIndex) {
        return nativeGetFloat(this.nativePtr, columnIndex, rowIndex);
    }

    public double getDouble(long columnIndex, long rowIndex) {
        return nativeGetDouble(this.nativePtr, columnIndex, rowIndex);
    }

    public Date getDate(long columnIndex, long rowIndex) {
        return new Date(nativeGetDateTime(this.nativePtr, columnIndex, rowIndex) * 1000);
    }

    public String getString(long columnIndex, long rowIndex) {
        return nativeGetString(this.nativePtr, columnIndex, rowIndex);
    }

    public byte[] getBinaryByteArray(long columnIndex, long rowIndex) {
        return nativeGetByteArray(this.nativePtr, columnIndex, rowIndex);
    }

    public Mixed getMixed(long columnIndex, long rowIndex) {
        return nativeGetMixed(this.nativePtr, columnIndex, rowIndex);
    }

    public ColumnType getMixedType(long columnIndex, long rowIndex) {
        return ColumnType.fromNativeValue(nativeGetMixedType(this.nativePtr, columnIndex, rowIndex));
    }

    public long getLink(long columnIndex, long rowIndex) {
        return nativeGetLink(this.nativePtr, columnIndex, rowIndex);
    }

    public Table getLinkTarget(long columnIndex) {
        this.context.executeDelayedDisposal();
        long nativeTablePointer = nativeGetLinkTarget(this.nativePtr, columnIndex);
        try {
            return new Table(this.context, this.parent, nativeTablePointer);
        } catch (RuntimeException e) {
            nativeClose(nativeTablePointer);
            throw e;
        }
    }

    public Table getSubtable(long columnIndex, long rowIndex) {
        this.context.executeDelayedDisposal();
        long nativeSubtablePtr = nativeGetSubtable(this.nativePtr, columnIndex, rowIndex);
        try {
            return new Table(this.context, this, nativeSubtablePtr);
        } catch (RuntimeException e) {
            nativeClose(nativeSubtablePtr);
            throw e;
        }
    }

    private Table getSubtableDuringInsert(long columnIndex, long rowIndex) {
        this.context.executeDelayedDisposal();
        long nativeSubtablePtr = nativeGetSubtableDuringInsert(this.nativePtr, columnIndex, rowIndex);
        try {
            return new Table(this.context, this, nativeSubtablePtr);
        } catch (RuntimeException e) {
            nativeClose(nativeSubtablePtr);
            throw e;
        }
    }

    public long getSubtableSize(long columnIndex, long rowIndex) {
        return nativeGetSubtableSize(this.nativePtr, columnIndex, rowIndex);
    }

    public void clearSubtable(long columnIndex, long rowIndex) {
        checkImmutable();
        nativeClearSubtable(this.nativePtr, columnIndex, rowIndex);
    }

    public UncheckedRow getUncheckedRow(long index) {
        return UncheckedRow.get(this.context, this, index);
    }

    public CheckedRow getCheckedRow(long index) {
        return CheckedRow.get(this.context, this, index);
    }

    public void setLong(long columnIndex, long rowIndex, long value) {
        checkImmutable();
        checkIntValueIsLegal(columnIndex, rowIndex, value);
        nativeSetLong(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setBoolean(long columnIndex, long rowIndex, boolean value) {
        checkImmutable();
        nativeSetBoolean(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setFloat(long columnIndex, long rowIndex, float value) {
        checkImmutable();
        nativeSetFloat(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setDouble(long columnIndex, long rowIndex, double value) {
        checkImmutable();
        nativeSetDouble(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setDate(long columnIndex, long rowIndex, Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Null Date is not allowed.");
        }
        checkImmutable();
        nativeSetDate(this.nativePtr, columnIndex, rowIndex, date.getTime() / 1000);
    }

    public void setString(long columnIndex, long rowIndex, String value) {
        checkImmutable();
        checkStringValueIsLegal(columnIndex, rowIndex, value);
        nativeSetString(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setBinaryByteArray(long columnIndex, long rowIndex, byte[] data) {
        checkImmutable();
        if (data == null) {
            throw new IllegalArgumentException("Null Array");
        }
        nativeSetByteArray(this.nativePtr, columnIndex, rowIndex, data);
    }

    public void setMixed(long columnIndex, long rowIndex, Mixed data) {
        checkImmutable();
        if (data == null) {
            throw new IllegalArgumentException();
        }
        nativeSetMixed(this.nativePtr, columnIndex, rowIndex, data);
    }

    public void setLink(long columnIndex, long rowIndex, long value) {
        checkImmutable();
        nativeSetLink(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void adjust(long columnIndex, long value) {
        checkImmutable();
        nativeAddInt(this.nativePtr, columnIndex, value);
    }

    public void addSearchIndex(long columnIndex) {
        checkImmutable();
        nativeAddSearchIndex(this.nativePtr, columnIndex);
    }

    public void removeSearchIndex(long columnIndex) {
        checkImmutable();
        nativeRemoveSearchIndex(this.nativePtr, columnIndex);
    }

    public void setPrimaryKey(String columnName) {
        Table pkTable = getPrimaryKeyTable();
        if (pkTable == null) {
            throw new RealmException("Primary keys are only supported if Table is part of a Group");
        }
        this.cachedPrimaryKeyColumnIndex = nativeSetPrimaryKey(pkTable.nativePtr, this.nativePtr, columnName);
    }

    private Table getPrimaryKeyTable() {
        Group group = getTableGroup();
        if (group == null) {
            return null;
        }
        Table pkTable = group.getTable(PRIMARY_KEY_TABLE_NAME);
        if (pkTable.getColumnCount() == PRIMARY_KEY_CLASS_COLUMN_INDEX) {
            pkTable.addColumn(ColumnType.STRING, PRIMARY_KEY_CLASS_COLUMN_NAME);
            pkTable.addColumn(ColumnType.STRING, PRIMARY_KEY_FIELD_COLUMN_NAME);
            return pkTable;
        }
        migratePrimaryKeyTableIfNeeded(group, pkTable);
        return pkTable;
    }

    private void migratePrimaryKeyTableIfNeeded(Group group, Table pkTable) {
        nativeMigratePrimaryKeyTableIfNeeded(group.nativePtr, pkTable.nativePtr);
    }

    Group getTableGroup() {
        if (this.parent instanceof Group) {
            return (Group) this.parent;
        }
        if (this.parent instanceof Table) {
            return ((Table) this.parent).getTableGroup();
        }
        return null;
    }

    public boolean hasSearchIndex(long columnIndex) {
        return nativeHasSearchIndex(this.nativePtr, columnIndex);
    }

    public boolean isNullLink(long columnIndex, long rowIndex) {
        return nativeIsNullLink(this.nativePtr, columnIndex, rowIndex);
    }

    public void nullifyLink(long columnIndex, long rowIndex) {
        nativeNullifyLink(this.nativePtr, columnIndex, rowIndex);
    }

    boolean isImmutable() {
        if (this.parent instanceof Table) {
            return ((Table) this.parent).isImmutable();
        }
        return this.parent != null && ((Group) this.parent).immutable;
    }

    void checkImmutable() {
        if (isImmutable()) {
            throwImmutable();
        }
    }

    private void checkHasPrimaryKey() {
        if (!hasPrimaryKey()) {
            throw new IllegalStateException(getName() + " has no primary key defined");
        }
    }

    public long sumLong(long columnIndex) {
        return nativeSumInt(this.nativePtr, columnIndex);
    }

    public long maximumLong(long columnIndex) {
        return nativeMaximumInt(this.nativePtr, columnIndex);
    }

    public long minimumLong(long columnIndex) {
        return nativeMinimumInt(this.nativePtr, columnIndex);
    }

    public double averageLong(long columnIndex) {
        return nativeAverageInt(this.nativePtr, columnIndex);
    }

    public double sumFloat(long columnIndex) {
        return nativeSumFloat(this.nativePtr, columnIndex);
    }

    public float maximumFloat(long columnIndex) {
        return nativeMaximumFloat(this.nativePtr, columnIndex);
    }

    public float minimumFloat(long columnIndex) {
        return nativeMinimumFloat(this.nativePtr, columnIndex);
    }

    public double averageFloat(long columnIndex) {
        return nativeAverageFloat(this.nativePtr, columnIndex);
    }

    public double sumDouble(long columnIndex) {
        return nativeSumDouble(this.nativePtr, columnIndex);
    }

    public double maximumDouble(long columnIndex) {
        return nativeMaximumDouble(this.nativePtr, columnIndex);
    }

    public double minimumDouble(long columnIndex) {
        return nativeMinimumDouble(this.nativePtr, columnIndex);
    }

    public double averageDouble(long columnIndex) {
        return nativeAverageDouble(this.nativePtr, columnIndex);
    }

    public Date maximumDate(long columnIndex) {
        return new Date(nativeMaximumDate(this.nativePtr, columnIndex) * 1000);
    }

    public Date minimumDate(long columnIndex) {
        return new Date(nativeMinimumDate(this.nativePtr, columnIndex) * 1000);
    }

    public long count(long columnIndex, long value) {
        return nativeCountLong(this.nativePtr, columnIndex, value);
    }

    public long count(long columnIndex, float value) {
        return nativeCountFloat(this.nativePtr, columnIndex, value);
    }

    public long count(long columnIndex, double value) {
        return nativeCountDouble(this.nativePtr, columnIndex, value);
    }

    public long count(long columnIndex, String value) {
        return nativeCountString(this.nativePtr, columnIndex, value);
    }

    public TableQuery where() {
        this.context.executeDelayedDisposal();
        long nativeQueryPtr = nativeWhere(this.nativePtr);
        try {
            return new TableQuery(this.context, this, nativeQueryPtr);
        } catch (RuntimeException e) {
            TableQuery.nativeClose(nativeQueryPtr);
            throw e;
        }
    }

    public long findFirstLong(long columnIndex, long value) {
        return nativeFindFirstInt(this.nativePtr, columnIndex, value);
    }

    public long findFirstBoolean(long columnIndex, boolean value) {
        return nativeFindFirstBool(this.nativePtr, columnIndex, value);
    }

    public long findFirstFloat(long columnIndex, float value) {
        return nativeFindFirstFloat(this.nativePtr, columnIndex, value);
    }

    public long findFirstDouble(long columnIndex, double value) {
        return nativeFindFirstDouble(this.nativePtr, columnIndex, value);
    }

    public long findFirstDate(long columnIndex, Date date) {
        if (date == null) {
            throw new IllegalArgumentException("null is not supported");
        }
        return nativeFindFirstDate(this.nativePtr, columnIndex, date.getTime() / 1000);
    }

    public long findFirstString(long columnIndex, String value) {
        if (value != null) {
            return nativeFindFirstString(this.nativePtr, columnIndex, value);
        }
        throw new IllegalArgumentException("null is not supported");
    }

    public TableView findAllLong(long columnIndex, long value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllInt(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllBoolean(long columnIndex, boolean value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllBool(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllFloat(long columnIndex, float value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllFloat(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllDouble(long columnIndex, double value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllDouble(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllDate(long columnIndex, Date date) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllDate(this.nativePtr, columnIndex, date.getTime() / 1000);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllString(long columnIndex, String value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllString(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public long lowerBoundLong(long columnIndex, long value) {
        return nativeLowerBoundInt(this.nativePtr, columnIndex, value);
    }

    public long upperBoundLong(long columnIndex, long value) {
        return nativeUpperBoundInt(this.nativePtr, columnIndex, value);
    }

    public Table pivot(long stringCol, long intCol, PivotType pivotType) {
        if (!getColumnType(stringCol).equals(ColumnType.STRING)) {
            throw new UnsupportedOperationException("Group by column must be of type String");
        } else if (getColumnType(intCol).equals(ColumnType.INTEGER)) {
            Table result = new Table();
            nativePivot(this.nativePtr, stringCol, intCol, pivotType.value, result.nativePtr);
            return result;
        } else {
            throw new UnsupportedOperationException("Aggregation column must be of type Int");
        }
    }

    public TableView getDistinctView(long columnIndex) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeGetDistinctView(this.nativePtr, columnIndex);
        try {
            return new TableView(this.context, this, nativeViewPtr);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public String getName() {
        return nativeGetName(this.nativePtr);
    }

    public void optimize() {
        checkImmutable();
        nativeOptimize(this.nativePtr);
    }

    public String toJson() {
        return nativeToJson(this.nativePtr);
    }

    public String toString() {
        return nativeToString(this.nativePtr, INFINITE);
    }

    public String toString(long maxRows) {
        return nativeToString(this.nativePtr, maxRows);
    }

    public String rowToString(long rowIndex) {
        return nativeRowToString(this.nativePtr, rowIndex);
    }

    public long sync() {
        throw new RuntimeException("Not supported for tables");
    }

    private void throwImmutable() {
        throw new IllegalStateException("Changing Realm data can only be done from inside a transaction.");
    }

    public boolean hasSameSchema(Table table) {
        if (table != null) {
            return nativeHasSameSchema(this.nativePtr, table.nativePtr);
        }
        throw new IllegalArgumentException("The argument cannot be null");
    }
}
