package com.crashlytics.android.core;

import io.realm.internal.Table;

final class WireFormat {
    static final int MESSAGE_SET_ITEM = 1;
    static final int MESSAGE_SET_ITEM_END_TAG;
    static final int MESSAGE_SET_ITEM_TAG;
    static final int MESSAGE_SET_MESSAGE = 3;
    static final int MESSAGE_SET_MESSAGE_TAG;
    static final int MESSAGE_SET_TYPE_ID = 2;
    static final int MESSAGE_SET_TYPE_ID_TAG;
    static final int TAG_TYPE_BITS = 3;
    static final int TAG_TYPE_MASK = 7;
    public static final int WIRETYPE_END_GROUP = 4;
    public static final int WIRETYPE_FIXED32 = 5;
    public static final int WIRETYPE_FIXED64 = 1;
    public static final int WIRETYPE_LENGTH_DELIMITED = 2;
    public static final int WIRETYPE_START_GROUP = 3;
    public static final int WIRETYPE_VARINT = 0;

    enum FieldType {
        DOUBLE(JavaType.DOUBLE, WireFormat.WIRETYPE_FIXED64),
        FLOAT(JavaType.FLOAT, WireFormat.WIRETYPE_FIXED32),
        INT64(JavaType.LONG, WireFormat.MESSAGE_SET_TYPE_ID_TAG),
        UINT64(JavaType.LONG, WireFormat.MESSAGE_SET_TYPE_ID_TAG),
        INT32(JavaType.INT, WireFormat.MESSAGE_SET_TYPE_ID_TAG),
        FIXED64(JavaType.LONG, WireFormat.WIRETYPE_FIXED64),
        FIXED32(JavaType.INT, WireFormat.WIRETYPE_FIXED32),
        BOOL(JavaType.BOOLEAN, WireFormat.MESSAGE_SET_TYPE_ID_TAG),
        STRING(JavaType.STRING, WireFormat.WIRETYPE_LENGTH_DELIMITED) {
            public boolean isPackable() {
                return false;
            }
        },
        GROUP(JavaType.MESSAGE, WireFormat.WIRETYPE_START_GROUP) {
            public boolean isPackable() {
                return false;
            }
        },
        MESSAGE(JavaType.MESSAGE, WireFormat.WIRETYPE_LENGTH_DELIMITED) {
            public boolean isPackable() {
                return false;
            }
        },
        BYTES(JavaType.BYTE_STRING, WireFormat.WIRETYPE_LENGTH_DELIMITED) {
            public boolean isPackable() {
                return false;
            }
        },
        UINT32(JavaType.INT, WireFormat.MESSAGE_SET_TYPE_ID_TAG),
        ENUM(JavaType.ENUM, WireFormat.MESSAGE_SET_TYPE_ID_TAG),
        SFIXED32(JavaType.INT, WireFormat.WIRETYPE_FIXED32),
        SFIXED64(JavaType.LONG, WireFormat.WIRETYPE_FIXED64),
        SINT32(JavaType.INT, WireFormat.MESSAGE_SET_TYPE_ID_TAG),
        SINT64(JavaType.LONG, WireFormat.MESSAGE_SET_TYPE_ID_TAG);
        
        private final JavaType javaType;
        private final int wireType;

        private FieldType(JavaType javaType, int wireType) {
            this.javaType = javaType;
            this.wireType = wireType;
        }

        public JavaType getJavaType() {
            return this.javaType;
        }

        public int getWireType() {
            return this.wireType;
        }

        public boolean isPackable() {
            return true;
        }
    }

    enum JavaType {
        INT(Integer.valueOf(WireFormat.MESSAGE_SET_TYPE_ID_TAG)),
        LONG(Long.valueOf(0)),
        FLOAT(Float.valueOf(0.0f)),
        DOUBLE(Double.valueOf(0.0d)),
        BOOLEAN(Boolean.valueOf(false)),
        STRING(Table.STRING_DEFAULT_VALUE),
        BYTE_STRING(ByteString.EMPTY),
        ENUM(null),
        MESSAGE(null);
        
        private final Object defaultDefault;

        private JavaType(Object defaultDefault) {
            this.defaultDefault = defaultDefault;
        }

        Object getDefaultDefault() {
            return this.defaultDefault;
        }
    }

    private WireFormat() {
    }

    static int getTagWireType(int tag) {
        return tag & TAG_TYPE_MASK;
    }

    public static int getTagFieldNumber(int tag) {
        return tag >>> WIRETYPE_START_GROUP;
    }

    static int makeTag(int fieldNumber, int wireType) {
        return (fieldNumber << WIRETYPE_START_GROUP) | wireType;
    }

    static {
        MESSAGE_SET_ITEM_TAG = makeTag(WIRETYPE_FIXED64, WIRETYPE_START_GROUP);
        MESSAGE_SET_ITEM_END_TAG = makeTag(WIRETYPE_FIXED64, WIRETYPE_END_GROUP);
        MESSAGE_SET_TYPE_ID_TAG = makeTag(WIRETYPE_LENGTH_DELIMITED, MESSAGE_SET_TYPE_ID_TAG);
        MESSAGE_SET_MESSAGE_TAG = makeTag(WIRETYPE_START_GROUP, WIRETYPE_LENGTH_DELIMITED);
    }
}
