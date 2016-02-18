package io.realm.internal;

import com.alipay.sdk.protocol.h;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class Mixed {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int BINARY_TYPE_BYTE_ARRAY = 0;
    public static final int BINARY_TYPE_BYTE_BUFFER = 1;
    private Object value;

    /* renamed from: io.realm.internal.Mixed.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$realm$internal$ColumnType;

        static {
            $SwitchMap$io$realm$internal$ColumnType = new int[ColumnType.values().length];
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.BINARY.ordinal()] = Mixed.BINARY_TYPE_BYTE_BUFFER;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.BOOLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.DATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.DOUBLE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.FLOAT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.INTEGER.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.STRING.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.TABLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.MIXED.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    static {
        $assertionsDisabled = !Mixed.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public Mixed(long value) {
        this.value = Long.valueOf(value);
    }

    public Mixed(float value) {
        this.value = Float.valueOf(value);
    }

    public Mixed(double value) {
        this.value = Double.valueOf(value);
    }

    public Mixed(ColumnType columnType) {
        if (columnType == null || columnType == ColumnType.TABLE) {
            throw new AssertionError();
        }
        this.value = null;
    }

    public Mixed(boolean value) {
        this.value = value ? Boolean.TRUE : Boolean.FALSE;
    }

    public Mixed(Date value) {
        if ($assertionsDisabled || value != null) {
            this.value = value;
            return;
        }
        throw new AssertionError();
    }

    public Mixed(String value) {
        if ($assertionsDisabled || value != null) {
            this.value = value;
            return;
        }
        throw new AssertionError();
    }

    public Mixed(ByteBuffer value) {
        if ($assertionsDisabled || value != null) {
            this.value = value;
            return;
        }
        throw new AssertionError();
    }

    public Mixed(byte[] value) {
        if ($assertionsDisabled || value != null) {
            this.value = value;
            return;
        }
        throw new AssertionError();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return $assertionsDisabled;
        }
        Mixed mixed = (Mixed) obj;
        if (this.value.getClass() != mixed.value.getClass()) {
            return $assertionsDisabled;
        }
        if (this.value instanceof byte[]) {
            return Arrays.equals((byte[]) this.value, (byte[]) mixed.value);
        }
        if (!(this.value instanceof ByteBuffer)) {
            return this.value.equals(mixed.value);
        }
        return ((ByteBuffer) this.value).compareTo((ByteBuffer) mixed.value) == 0 ? BINARY_TYPE_BYTE_BUFFER : $assertionsDisabled;
    }

    public int hashCode() {
        if (this.value instanceof byte[]) {
            return Arrays.hashCode((byte[]) this.value);
        }
        return this.value.hashCode();
    }

    public ColumnType getType() {
        if (this.value == null) {
            return ColumnType.TABLE;
        }
        if (this.value instanceof String) {
            return ColumnType.STRING;
        }
        if (this.value instanceof Long) {
            return ColumnType.INTEGER;
        }
        if (this.value instanceof Float) {
            return ColumnType.FLOAT;
        }
        if (this.value instanceof Double) {
            return ColumnType.DOUBLE;
        }
        if (this.value instanceof Date) {
            return ColumnType.DATE;
        }
        if (this.value instanceof Boolean) {
            return ColumnType.BOOLEAN;
        }
        if ((this.value instanceof ByteBuffer) || (this.value instanceof byte[])) {
            return ColumnType.BINARY;
        }
        throw new IllegalStateException("Unknown column type!");
    }

    public static Mixed mixedValue(Object value) {
        if (value instanceof String) {
            return new Mixed((String) value);
        }
        if (value instanceof Long) {
            return new Mixed(((Long) value).longValue());
        }
        if (value instanceof Integer) {
            return new Mixed(((Integer) value).longValue());
        }
        if (value instanceof Boolean) {
            return new Mixed(((Boolean) value).booleanValue());
        }
        if (value instanceof Float) {
            return new Mixed(((Float) value).floatValue());
        }
        if (value instanceof Double) {
            return new Mixed(((Double) value).doubleValue());
        }
        if (value instanceof Date) {
            return new Mixed((Date) value);
        }
        if (value instanceof ByteBuffer) {
            return new Mixed((ByteBuffer) value);
        }
        if (value instanceof byte[]) {
            return new Mixed((byte[]) value);
        }
        if (value instanceof Mixed) {
            return (Mixed) value;
        }
        throw new IllegalArgumentException("The value is of unsupported type: " + value.getClass());
    }

    public long getLongValue() {
        if (this.value instanceof Long) {
            return ((Long) this.value).longValue();
        }
        throw new IllegalMixedTypeException("Can't get a long from a Mixed containing a " + getType());
    }

    public boolean getBooleanValue() {
        if (this.value instanceof Boolean) {
            return ((Boolean) this.value).booleanValue();
        }
        throw new IllegalMixedTypeException("Can't get a boolean from a Mixed containing a " + getType());
    }

    public float getFloatValue() {
        if (this.value instanceof Float) {
            return ((Float) this.value).floatValue();
        }
        throw new IllegalMixedTypeException("Can't get a float from a Mixed containing a " + getType());
    }

    public double getDoubleValue() {
        if (this.value instanceof Double) {
            return ((Double) this.value).doubleValue();
        }
        throw new IllegalMixedTypeException("Can't get a double from a Mixed containing a " + getType());
    }

    public String getStringValue() {
        if (this.value instanceof String) {
            return (String) this.value;
        }
        throw new IllegalMixedTypeException("Can't get a String from a Mixed containing a " + getType());
    }

    public Date getDateValue() {
        if (this.value instanceof Date) {
            return (Date) this.value;
        }
        throw new IllegalMixedTypeException("Can't get a Date from a Mixed containing a " + getType());
    }

    protected long getDateTimeValue() {
        return getDateValue().getTime();
    }

    public ByteBuffer getBinaryValue() {
        if (this.value instanceof ByteBuffer) {
            return (ByteBuffer) this.value;
        }
        throw new IllegalMixedTypeException("Can't get a ByteBuffer from a Mixed containing a " + getType());
    }

    public byte[] getBinaryByteArray() {
        if (this.value instanceof byte[]) {
            return (byte[]) this.value;
        }
        throw new IllegalMixedTypeException("Can't get a byte[] from a Mixed containing a " + getType());
    }

    public int getBinaryType() {
        if (this.value instanceof byte[]) {
            return BINARY_TYPE_BYTE_ARRAY;
        }
        if (this.value instanceof ByteBuffer) {
            return BINARY_TYPE_BYTE_BUFFER;
        }
        return -1;
    }

    public Object getValue() {
        return this.value;
    }

    public String getReadableValue() {
        try {
            switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[getType().ordinal()]) {
                case BINARY_TYPE_BYTE_BUFFER /*1*/:
                    return "Binary";
                case dx.c /*2*/:
                    return String.valueOf(getBooleanValue());
                case dx.d /*3*/:
                    return String.valueOf(getDateValue());
                case dx.e /*4*/:
                    return String.valueOf(getDoubleValue());
                case dj.f /*5*/:
                    return String.valueOf(getFloatValue());
                case ci.g /*6*/:
                    return String.valueOf(getLongValue());
                case ci.h /*7*/:
                    return String.valueOf(getStringValue());
                case h.g /*8*/:
                    return "Subtable";
            }
        } catch (Exception e) {
        }
        return "ERROR";
    }
}
