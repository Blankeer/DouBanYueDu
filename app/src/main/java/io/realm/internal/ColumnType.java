package io.realm.internal;

import com.alipay.sdk.protocol.h;
import java.nio.ByteBuffer;
import java.util.Date;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public enum ColumnType {
    BOOLEAN(1),
    INTEGER(0),
    FLOAT(9),
    DOUBLE(10),
    STRING(2),
    BINARY(4),
    DATE(7),
    TABLE(5),
    MIXED(6),
    LINK(12),
    LINK_LIST(13);
    
    private static ColumnType[] byNativeValue;
    private final int nativeValue;

    static {
        byNativeValue = new ColumnType[14];
        ColumnType[] columnTypes = values();
        for (int i = 0; i < columnTypes.length; i++) {
            byNativeValue[columnTypes[i].nativeValue] = columnTypes[i];
        }
    }

    private ColumnType(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    public int getValue() {
        return this.nativeValue;
    }

    public boolean matchObject(Object obj) {
        switch (this.nativeValue) {
            case dx.a /*0*/:
                if ((obj instanceof Long) || (obj instanceof Integer) || (obj instanceof Short) || (obj instanceof Byte)) {
                    return true;
                }
                return false;
            case dx.b /*1*/:
                return obj instanceof Boolean;
            case dx.c /*2*/:
                return obj instanceof String;
            case dx.e /*4*/:
                if ((obj instanceof byte[]) || (obj instanceof ByteBuffer)) {
                    return true;
                }
                return false;
            case dj.f /*5*/:
                if (obj == null || (obj instanceof Object[][])) {
                    return true;
                }
                return false;
            case ci.g /*6*/:
                if ((obj instanceof Mixed) || (obj instanceof Long) || (obj instanceof Integer) || (obj instanceof Short) || (obj instanceof Byte) || (obj instanceof Boolean) || (obj instanceof Float) || (obj instanceof Double) || (obj instanceof String) || (obj instanceof byte[]) || (obj instanceof ByteBuffer) || obj == null || (obj instanceof Object[][]) || (obj instanceof Date)) {
                    return true;
                }
                return false;
            case ci.h /*7*/:
                return obj instanceof Date;
            case h.h /*9*/:
                return obj instanceof Float;
            case h.i /*10*/:
                return obj instanceof Double;
            default:
                throw new RuntimeException(String.format("Invalid index (%d) in ColumnType.", new Object[]{Integer.valueOf(this.nativeValue)}));
        }
    }

    static ColumnType fromNativeValue(int value) {
        if (value >= 0 && value < byNativeValue.length) {
            ColumnType e = byNativeValue[value];
            if (e != null) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid native column type");
    }
}
