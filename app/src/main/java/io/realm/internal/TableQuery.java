package io.realm.internal;

import java.io.Closeable;
import java.util.Date;

public class TableQuery implements Closeable {
    private static final String DATE_NULL_ERROR_MESSAGE = "Date value in query criteria must not be null.";
    private static final String STRING_NULL_ERROR_MESSAGE = "String value in query criteria must not be null.";
    protected boolean DEBUG;
    private final Context context;
    protected long nativePtr;
    private final TableOrView origin;
    private boolean queryValidated;
    protected final Table table;

    protected static native void nativeClose(long j);

    protected native double nativeAverageDouble(long j, long j2, long j3, long j4, long j5);

    protected native double nativeAverageFloat(long j, long j2, long j3, long j4, long j5);

    protected native double nativeAverageInt(long j, long j2, long j3, long j4, long j5);

    protected native void nativeBeginsWith(long j, long[] jArr, String str, boolean z);

    protected native void nativeBetween(long j, long[] jArr, double d, double d2);

    protected native void nativeBetween(long j, long[] jArr, float f, float f2);

    protected native void nativeBetween(long j, long[] jArr, long j2, long j3);

    protected native void nativeBetweenDateTime(long j, long[] jArr, long j2, long j3);

    protected native void nativeContains(long j, long[] jArr, String str, boolean z);

    protected native long nativeCount(long j, long j2, long j3, long j4);

    protected native void nativeEndGroup(long j);

    protected native void nativeEndsWith(long j, long[] jArr, String str, boolean z);

    protected native void nativeEqual(long j, long[] jArr, double d);

    protected native void nativeEqual(long j, long[] jArr, float f);

    protected native void nativeEqual(long j, long[] jArr, long j2);

    protected native void nativeEqual(long j, long[] jArr, String str, boolean z);

    protected native void nativeEqual(long j, long[] jArr, boolean z);

    protected native void nativeEqualDateTime(long j, long[] jArr, long j2);

    protected native long nativeFind(long j, long j2);

    protected native long nativeFindAll(long j, long j2, long j3, long j4);

    protected native void nativeGreater(long j, long[] jArr, double d);

    protected native void nativeGreater(long j, long[] jArr, float f);

    protected native void nativeGreater(long j, long[] jArr, long j2);

    protected native void nativeGreaterDateTime(long j, long[] jArr, long j2);

    protected native void nativeGreaterEqual(long j, long[] jArr, double d);

    protected native void nativeGreaterEqual(long j, long[] jArr, float f);

    protected native void nativeGreaterEqual(long j, long[] jArr, long j2);

    protected native void nativeGreaterEqualDateTime(long j, long[] jArr, long j2);

    protected native void nativeGroup(long j);

    protected native void nativeIsNull(long j, long j2);

    protected native void nativeLess(long j, long[] jArr, double d);

    protected native void nativeLess(long j, long[] jArr, float f);

    protected native void nativeLess(long j, long[] jArr, long j2);

    protected native void nativeLessDateTime(long j, long[] jArr, long j2);

    protected native void nativeLessEqual(long j, long[] jArr, double d);

    protected native void nativeLessEqual(long j, long[] jArr, float f);

    protected native void nativeLessEqual(long j, long[] jArr, long j2);

    protected native void nativeLessEqualDateTime(long j, long[] jArr, long j2);

    protected native long nativeMaximumDate(long j, long j2, long j3, long j4, long j5);

    protected native double nativeMaximumDouble(long j, long j2, long j3, long j4, long j5);

    protected native float nativeMaximumFloat(long j, long j2, long j3, long j4, long j5);

    protected native long nativeMaximumInt(long j, long j2, long j3, long j4, long j5);

    protected native long nativeMinimumDate(long j, long j2, long j3, long j4, long j5);

    protected native double nativeMinimumDouble(long j, long j2, long j3, long j4, long j5);

    protected native float nativeMinimumFloat(long j, long j2, long j3, long j4, long j5);

    protected native long nativeMinimumInt(long j, long j2, long j3, long j4, long j5);

    protected native void nativeNot(long j);

    protected native void nativeNotEqual(long j, long[] jArr, double d);

    protected native void nativeNotEqual(long j, long[] jArr, float f);

    protected native void nativeNotEqual(long j, long[] jArr, long j2);

    protected native void nativeNotEqual(long j, long[] jArr, String str, boolean z);

    protected native void nativeNotEqualDateTime(long j, long[] jArr, long j2);

    protected native void nativeOr(long j);

    protected native void nativeParent(long j);

    protected native long nativeRemove(long j, long j2, long j3, long j4);

    protected native void nativeSubtable(long j, long j2);

    protected native double nativeSumDouble(long j, long j2, long j3, long j4, long j5);

    protected native double nativeSumFloat(long j, long j2, long j3, long j4, long j5);

    protected native long nativeSumInt(long j, long j2, long j3, long j4, long j5);

    protected native void nativeTableview(long j, long j2);

    protected native String nativeValidateQuery(long j);

    public TableQuery(Context context, Table table, long nativeQueryPtr) {
        this.DEBUG = false;
        this.queryValidated = true;
        if (this.DEBUG) {
            System.err.println("++++++ new TableQuery, ptr= " + nativeQueryPtr);
        }
        this.context = context;
        this.table = table;
        this.nativePtr = nativeQueryPtr;
        this.origin = null;
    }

    public TableQuery(Context context, Table table, long nativeQueryPtr, TableOrView origin) {
        this.DEBUG = false;
        this.queryValidated = true;
        if (this.DEBUG) {
            System.err.println("++++++ new TableQuery, ptr= " + nativeQueryPtr);
        }
        this.context = context;
        this.table = table;
        this.nativePtr = nativeQueryPtr;
        this.origin = origin;
    }

    public void close() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                nativeClose(this.nativePtr);
                if (this.DEBUG) {
                    System.err.println("++++ Query CLOSE, ptr= " + this.nativePtr);
                }
                this.nativePtr = 0;
            }
        }
    }

    protected void finalize() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                this.context.asyncDisposeQuery(this.nativePtr);
                this.nativePtr = 0;
            }
        }
    }

    private void validateQuery() {
        if (!this.queryValidated) {
            String invalidMessage = nativeValidateQuery(this.nativePtr);
            if (invalidMessage.equals(Table.STRING_DEFAULT_VALUE)) {
                this.queryValidated = true;
                return;
            }
            throw new UnsupportedOperationException(invalidMessage);
        }
    }

    public TableQuery tableview(TableView tv) {
        nativeTableview(this.nativePtr, tv.nativePtr);
        return this;
    }

    public TableQuery group() {
        nativeGroup(this.nativePtr);
        return this;
    }

    public TableQuery endGroup() {
        nativeEndGroup(this.nativePtr);
        this.queryValidated = false;
        return this;
    }

    public TableQuery subtable(long columnIndex) {
        nativeSubtable(this.nativePtr, columnIndex);
        this.queryValidated = false;
        return this;
    }

    public TableQuery endSubtable() {
        nativeParent(this.nativePtr);
        this.queryValidated = false;
        return this;
    }

    public TableQuery or() {
        nativeOr(this.nativePtr);
        this.queryValidated = false;
        return this;
    }

    public TableQuery not() {
        nativeNot(this.nativePtr);
        this.queryValidated = false;
        return this;
    }

    public TableQuery equalTo(long[] columnIndexes, long value) {
        nativeEqual(this.nativePtr, columnIndexes, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery notEqualTo(long[] columnIndex, long value) {
        nativeNotEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThan(long[] columnIndex, long value) {
        nativeGreater(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThanOrEqual(long[] columnIndex, long value) {
        nativeGreaterEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThan(long[] columnIndex, long value) {
        nativeLess(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThanOrEqual(long[] columnIndex, long value) {
        nativeLessEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery between(long[] columnIndex, long value1, long value2) {
        nativeBetween(this.nativePtr, columnIndex, value1, value2);
        this.queryValidated = false;
        return this;
    }

    public TableQuery equalTo(long[] columnIndex, float value) {
        nativeEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery notEqualTo(long[] columnIndex, float value) {
        nativeNotEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThan(long[] columnIndex, float value) {
        nativeGreater(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThanOrEqual(long[] columnIndex, float value) {
        nativeGreaterEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThan(long[] columnIndex, float value) {
        nativeLess(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThanOrEqual(long[] columnIndex, float value) {
        nativeLessEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery between(long[] columnIndex, float value1, float value2) {
        nativeBetween(this.nativePtr, columnIndex, value1, value2);
        this.queryValidated = false;
        return this;
    }

    public TableQuery equalTo(long[] columnIndex, double value) {
        nativeEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery notEqualTo(long[] columnIndex, double value) {
        nativeNotEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThan(long[] columnIndex, double value) {
        nativeGreater(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThanOrEqual(long[] columnIndex, double value) {
        nativeGreaterEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThan(long[] columnIndex, double value) {
        nativeLess(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThanOrEqual(long[] columnIndex, double value) {
        nativeLessEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery between(long[] columnIndex, double value1, double value2) {
        nativeBetween(this.nativePtr, columnIndex, value1, value2);
        this.queryValidated = false;
        return this;
    }

    public TableQuery equalTo(long[] columnIndex, boolean value) {
        nativeEqual(this.nativePtr, columnIndex, value);
        this.queryValidated = false;
        return this;
    }

    public TableQuery equalTo(long[] columnIndex, Date value) {
        if (value == null) {
            throw new IllegalArgumentException(DATE_NULL_ERROR_MESSAGE);
        }
        nativeEqualDateTime(this.nativePtr, columnIndex, value.getTime() / 1000);
        this.queryValidated = false;
        return this;
    }

    public TableQuery notEqualTo(long[] columnIndex, Date value) {
        if (value == null) {
            throw new IllegalArgumentException(DATE_NULL_ERROR_MESSAGE);
        }
        nativeNotEqualDateTime(this.nativePtr, columnIndex, value.getTime() / 1000);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThan(long[] columnIndex, Date value) {
        if (value == null) {
            throw new IllegalArgumentException(DATE_NULL_ERROR_MESSAGE);
        }
        nativeGreaterDateTime(this.nativePtr, columnIndex, value.getTime() / 1000);
        this.queryValidated = false;
        return this;
    }

    public TableQuery greaterThanOrEqual(long[] columnIndex, Date value) {
        if (value == null) {
            throw new IllegalArgumentException(DATE_NULL_ERROR_MESSAGE);
        }
        nativeGreaterEqualDateTime(this.nativePtr, columnIndex, value.getTime() / 1000);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThan(long[] columnIndex, Date value) {
        if (value == null) {
            throw new IllegalArgumentException(DATE_NULL_ERROR_MESSAGE);
        }
        nativeLessDateTime(this.nativePtr, columnIndex, value.getTime() / 1000);
        this.queryValidated = false;
        return this;
    }

    public TableQuery lessThanOrEqual(long[] columnIndex, Date value) {
        if (value == null) {
            throw new IllegalArgumentException(DATE_NULL_ERROR_MESSAGE);
        }
        nativeLessEqualDateTime(this.nativePtr, columnIndex, value.getTime() / 1000);
        this.queryValidated = false;
        return this;
    }

    public TableQuery between(long[] columnIndex, Date value1, Date value2) {
        if (value1 == null || value2 == null) {
            throw new IllegalArgumentException("Date values in query criteria must not be null.");
        }
        nativeBetweenDateTime(this.nativePtr, columnIndex, value1.getTime() / 1000, value2.getTime() / 1000);
        this.queryValidated = false;
        return this;
    }

    public TableQuery equalTo(long[] columnIndexes, String value, boolean caseSensitive) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeEqual(this.nativePtr, columnIndexes, value, caseSensitive);
        this.queryValidated = false;
        return this;
    }

    public TableQuery equalTo(long[] columnIndexes, String value) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeEqual(this.nativePtr, columnIndexes, value, true);
        this.queryValidated = false;
        return this;
    }

    public TableQuery notEqualTo(long[] columnIndex, String value, boolean caseSensitive) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeNotEqual(this.nativePtr, columnIndex, value, caseSensitive);
        this.queryValidated = false;
        return this;
    }

    public TableQuery notEqualTo(long[] columnIndex, String value) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeNotEqual(this.nativePtr, columnIndex, value, true);
        this.queryValidated = false;
        return this;
    }

    public TableQuery beginsWith(long[] columnIndices, String value, boolean caseSensitive) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeBeginsWith(this.nativePtr, columnIndices, value, caseSensitive);
        this.queryValidated = false;
        return this;
    }

    public TableQuery beginsWith(long[] columnIndices, String value) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeBeginsWith(this.nativePtr, columnIndices, value, true);
        this.queryValidated = false;
        return this;
    }

    public TableQuery endsWith(long[] columnIndices, String value, boolean caseSensitive) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeEndsWith(this.nativePtr, columnIndices, value, caseSensitive);
        this.queryValidated = false;
        return this;
    }

    public TableQuery endsWith(long[] columnIndices, String value) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeEndsWith(this.nativePtr, columnIndices, value, true);
        this.queryValidated = false;
        return this;
    }

    public TableQuery contains(long[] columnIndices, String value, boolean caseSensitive) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeContains(this.nativePtr, columnIndices, value, caseSensitive);
        this.queryValidated = false;
        return this;
    }

    public TableQuery contains(long[] columnIndices, String value) {
        if (value == null) {
            throw new IllegalArgumentException(STRING_NULL_ERROR_MESSAGE);
        }
        nativeContains(this.nativePtr, columnIndices, value, true);
        this.queryValidated = false;
        return this;
    }

    public long find(long fromTableRow) {
        validateQuery();
        return nativeFind(this.nativePtr, fromTableRow);
    }

    public long find() {
        validateQuery();
        return nativeFind(this.nativePtr, 0);
    }

    public TableView findAll(long start, long end, long limit) {
        validateQuery();
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAll(this.nativePtr, start, end, limit);
        try {
            return new TableView(this.context, this.table, nativeViewPtr, this);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAll() {
        validateQuery();
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAll(this.nativePtr, 0, -1, -1);
        try {
            return new TableView(this.context, this.table, nativeViewPtr, this);
        } catch (RuntimeException e) {
            TableView.nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public long sumInt(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeSumInt(this.nativePtr, columnIndex, start, end, limit);
    }

    public long sumInt(long columnIndex) {
        validateQuery();
        return nativeSumInt(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public long maximumInt(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeMaximumInt(this.nativePtr, columnIndex, start, end, limit);
    }

    public long maximumInt(long columnIndex) {
        validateQuery();
        return nativeMaximumInt(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public long minimumInt(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeMinimumInt(this.nativePtr, columnIndex, start, end, limit);
    }

    public long minimumInt(long columnIndex) {
        validateQuery();
        return nativeMinimumInt(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public double averageInt(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeAverageInt(this.nativePtr, columnIndex, start, end, limit);
    }

    public double averageInt(long columnIndex) {
        validateQuery();
        return nativeAverageInt(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public double sumFloat(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeSumFloat(this.nativePtr, columnIndex, start, end, limit);
    }

    public double sumFloat(long columnIndex) {
        validateQuery();
        return nativeSumFloat(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public float maximumFloat(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeMaximumFloat(this.nativePtr, columnIndex, start, end, limit);
    }

    public float maximumFloat(long columnIndex) {
        validateQuery();
        return nativeMaximumFloat(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public float minimumFloat(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeMinimumFloat(this.nativePtr, columnIndex, start, end, limit);
    }

    public float minimumFloat(long columnIndex) {
        validateQuery();
        return nativeMinimumFloat(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public double averageFloat(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeAverageFloat(this.nativePtr, columnIndex, start, end, limit);
    }

    public double averageFloat(long columnIndex) {
        validateQuery();
        return nativeAverageFloat(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public double sumDouble(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeSumDouble(this.nativePtr, columnIndex, start, end, limit);
    }

    public double sumDouble(long columnIndex) {
        validateQuery();
        return nativeSumDouble(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public double maximumDouble(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeMaximumDouble(this.nativePtr, columnIndex, start, end, limit);
    }

    public double maximumDouble(long columnIndex) {
        validateQuery();
        return nativeMaximumDouble(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public double minimumDouble(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeMinimumDouble(this.nativePtr, columnIndex, start, end, limit);
    }

    public double minimumDouble(long columnIndex) {
        validateQuery();
        return nativeMinimumDouble(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public double averageDouble(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return nativeAverageDouble(this.nativePtr, columnIndex, start, end, limit);
    }

    public double averageDouble(long columnIndex) {
        validateQuery();
        return nativeAverageDouble(this.nativePtr, columnIndex, 0, -1, -1);
    }

    public Date maximumDate(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return new Date(nativeMaximumDate(this.nativePtr, columnIndex, start, end, limit) * 1000);
    }

    public Date maximumDate(long columnIndex) {
        validateQuery();
        return new Date(nativeMaximumDate(this.nativePtr, columnIndex, 0, -1, -1) * 1000);
    }

    public Date minimumDate(long columnIndex, long start, long end, long limit) {
        validateQuery();
        return new Date(nativeMinimumDate(this.nativePtr, columnIndex, start, end, limit) * 1000);
    }

    public Date minimumDate(long columnIndex) {
        validateQuery();
        return new Date(nativeMinimumDate(this.nativePtr, columnIndex, 0, -1, -1) * 1000);
    }

    public TableQuery isNull(long columnIndex) {
        nativeIsNull(this.nativePtr, columnIndex);
        return this;
    }

    public long count(long start, long end, long limit) {
        validateQuery();
        return nativeCount(this.nativePtr, start, end, limit);
    }

    public long count() {
        validateQuery();
        return nativeCount(this.nativePtr, 0, -1, -1);
    }

    public long remove(long start, long end) {
        validateQuery();
        if (this.table.isImmutable()) {
            throwImmutable();
        }
        return nativeRemove(this.nativePtr, start, end, -1);
    }

    public long remove() {
        validateQuery();
        if (this.table.isImmutable()) {
            throwImmutable();
        }
        return nativeRemove(this.nativePtr, 0, -1, -1);
    }

    private void throwImmutable() {
        throw new IllegalStateException("Mutable method call during read transaction.");
    }
}
