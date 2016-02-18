package com.google.analytics.midtier.proto.containertag;

import android.support.v4.media.TransportMediator;
import com.alipay.sdk.protocol.h;
import com.google.tagmanager.protobuf.nano.CodedInputByteBufferNano;
import com.google.tagmanager.protobuf.nano.CodedOutputByteBufferNano;
import com.google.tagmanager.protobuf.nano.ExtendableMessageNano;
import com.google.tagmanager.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.tagmanager.protobuf.nano.MessageNano;
import com.google.tagmanager.protobuf.nano.WireFormatNano;
import io.realm.internal.Table;
import java.io.IOException;
import java.util.ArrayList;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public interface TypeSystem {

    public static final class Value extends ExtendableMessageNano {
        public static final Value[] EMPTY_ARRAY;
        public boolean boolean_;
        public boolean containsReferences;
        public int[] escaping;
        public String functionId;
        public long integer;
        public Value[] listItem;
        public String macroReference;
        public Value[] mapKey;
        public Value[] mapValue;
        public String string;
        public String tagReference;
        public Value[] templateToken;
        public int type;

        public interface Escaping {
            public static final int CONVERT_JS_VALUE_TO_EXPRESSION = 16;
            public static final int ESCAPE_CSS_STRING = 10;
            public static final int ESCAPE_HTML = 1;
            public static final int ESCAPE_HTML_ATTRIBUTE = 3;
            public static final int ESCAPE_HTML_ATTRIBUTE_NOSPACE = 4;
            public static final int ESCAPE_HTML_RCDATA = 2;
            public static final int ESCAPE_JS_REGEX = 9;
            public static final int ESCAPE_JS_STRING = 7;
            public static final int ESCAPE_JS_VALUE = 8;
            public static final int ESCAPE_URI = 12;
            public static final int FILTER_CSS_VALUE = 11;
            public static final int FILTER_HTML_ATTRIBUTES = 6;
            public static final int FILTER_HTML_ELEMENT_NAME = 5;
            public static final int FILTER_NORMALIZE_URI = 14;
            public static final int NORMALIZE_URI = 13;
            public static final int NO_AUTOESCAPE = 15;
            public static final int TEXT = 17;
        }

        public interface Type {
            public static final int BOOLEAN = 8;
            public static final int FUNCTION_ID = 5;
            public static final int INTEGER = 6;
            public static final int LIST = 2;
            public static final int MACRO_REFERENCE = 4;
            public static final int MAP = 3;
            public static final int STRING = 1;
            public static final int TAG_REFERENCE = 9;
            public static final int TEMPLATE = 7;
        }

        static {
            EMPTY_ARRAY = new Value[0];
        }

        public Value() {
            this.type = 1;
            this.string = Table.STRING_DEFAULT_VALUE;
            this.listItem = EMPTY_ARRAY;
            this.mapKey = EMPTY_ARRAY;
            this.mapValue = EMPTY_ARRAY;
            this.macroReference = Table.STRING_DEFAULT_VALUE;
            this.functionId = Table.STRING_DEFAULT_VALUE;
            this.integer = 0;
            this.boolean_ = false;
            this.templateToken = EMPTY_ARRAY;
            this.tagReference = Table.STRING_DEFAULT_VALUE;
            this.escaping = WireFormatNano.EMPTY_INT_ARRAY;
            this.containsReferences = false;
        }

        public final Value clear() {
            this.type = 1;
            this.string = Table.STRING_DEFAULT_VALUE;
            this.listItem = EMPTY_ARRAY;
            this.mapKey = EMPTY_ARRAY;
            this.mapValue = EMPTY_ARRAY;
            this.macroReference = Table.STRING_DEFAULT_VALUE;
            this.functionId = Table.STRING_DEFAULT_VALUE;
            this.integer = 0;
            this.boolean_ = false;
            this.templateToken = EMPTY_ARRAY;
            this.tagReference = Table.STRING_DEFAULT_VALUE;
            this.escaping = WireFormatNano.EMPTY_INT_ARRAY;
            this.containsReferences = false;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean equals(java.lang.Object r9) {
            /*
            r8 = this;
            r1 = 1;
            r2 = 0;
            if (r9 != r8) goto L_0x0005;
        L_0x0004:
            return r1;
        L_0x0005:
            r3 = r9 instanceof com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r9;
            r0 = (com.google.analytics.midtier.proto.containertag.TypeSystem.Value) r0;
            r3 = r8.type;
            r4 = r0.type;
            if (r3 != r4) goto L_0x0082;
        L_0x0014:
            r3 = r8.string;
            if (r3 != 0) goto L_0x0084;
        L_0x0018:
            r3 = r0.string;
            if (r3 != 0) goto L_0x0082;
        L_0x001c:
            r3 = r8.listItem;
            r4 = r0.listItem;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0082;
        L_0x0026:
            r3 = r8.mapKey;
            r4 = r0.mapKey;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0082;
        L_0x0030:
            r3 = r8.mapValue;
            r4 = r0.mapValue;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0082;
        L_0x003a:
            r3 = r8.macroReference;
            if (r3 != 0) goto L_0x008f;
        L_0x003e:
            r3 = r0.macroReference;
            if (r3 != 0) goto L_0x0082;
        L_0x0042:
            r3 = r8.functionId;
            if (r3 != 0) goto L_0x009a;
        L_0x0046:
            r3 = r0.functionId;
            if (r3 != 0) goto L_0x0082;
        L_0x004a:
            r4 = r8.integer;
            r6 = r0.integer;
            r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r3 != 0) goto L_0x0082;
        L_0x0052:
            r3 = r8.boolean_;
            r4 = r0.boolean_;
            if (r3 != r4) goto L_0x0082;
        L_0x0058:
            r3 = r8.templateToken;
            r4 = r0.templateToken;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0082;
        L_0x0062:
            r3 = r8.tagReference;
            if (r3 != 0) goto L_0x00a5;
        L_0x0066:
            r3 = r0.tagReference;
            if (r3 != 0) goto L_0x0082;
        L_0x006a:
            r3 = r8.escaping;
            r4 = r0.escaping;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0082;
        L_0x0074:
            r3 = r8.containsReferences;
            r4 = r0.containsReferences;
            if (r3 != r4) goto L_0x0082;
        L_0x007a:
            r3 = r8.unknownFieldData;
            if (r3 != 0) goto L_0x00b0;
        L_0x007e:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x0082:
            r1 = r2;
            goto L_0x0004;
        L_0x0084:
            r3 = r8.string;
            r4 = r0.string;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0082;
        L_0x008e:
            goto L_0x001c;
        L_0x008f:
            r3 = r8.macroReference;
            r4 = r0.macroReference;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0082;
        L_0x0099:
            goto L_0x0042;
        L_0x009a:
            r3 = r8.functionId;
            r4 = r0.functionId;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0082;
        L_0x00a4:
            goto L_0x004a;
        L_0x00a5:
            r3 = r8.tagReference;
            r4 = r0.tagReference;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0082;
        L_0x00af:
            goto L_0x006a;
        L_0x00b0:
            r3 = r8.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0082;
        L_0x00ba:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.midtier.proto.containertag.TypeSystem.Value.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i;
            int i2;
            int i3 = 1;
            int i4 = 0;
            int result = ((this.type + 527) * 31) + (this.string == null ? 0 : this.string.hashCode());
            if (this.listItem == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.listItem.length; i++) {
                    result = (result * 31) + (this.listItem[i] == null ? 0 : this.listItem[i].hashCode());
                }
            }
            if (this.mapKey == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.mapKey.length; i++) {
                    result = (result * 31) + (this.mapKey[i] == null ? 0 : this.mapKey[i].hashCode());
                }
            }
            if (this.mapValue == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.mapValue.length; i++) {
                    result = (result * 31) + (this.mapValue[i] == null ? 0 : this.mapValue[i].hashCode());
                }
            }
            int hashCode = ((((((result * 31) + (this.macroReference == null ? 0 : this.macroReference.hashCode())) * 31) + (this.functionId == null ? 0 : this.functionId.hashCode())) * 31) + ((int) (this.integer ^ (this.integer >>> 32)))) * 31;
            if (this.boolean_) {
                i2 = 1;
            } else {
                i2 = 2;
            }
            result = hashCode + i2;
            if (this.templateToken == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.templateToken.length; i++) {
                    result = (result * 31) + (this.templateToken[i] == null ? 0 : this.templateToken[i].hashCode());
                }
            }
            result = (result * 31) + (this.tagReference == null ? 0 : this.tagReference.hashCode());
            if (this.escaping == null) {
                result *= 31;
            } else {
                for (int hashCode2 : this.escaping) {
                    result = (result * 31) + hashCode2;
                }
            }
            i2 = result * 31;
            if (!this.containsReferences) {
                i3 = 2;
            }
            i2 = (i2 + i3) * 31;
            if (this.unknownFieldData != null) {
                i4 = this.unknownFieldData.hashCode();
            }
            return i2 + i4;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt32(1, this.type);
            if (!this.string.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(2, this.string);
            }
            if (this.listItem != null) {
                for (Value element : this.listItem) {
                    output.writeMessage(3, element);
                }
            }
            if (this.mapKey != null) {
                for (Value element2 : this.mapKey) {
                    output.writeMessage(4, element2);
                }
            }
            if (this.mapValue != null) {
                for (Value element22 : this.mapValue) {
                    output.writeMessage(5, element22);
                }
            }
            if (!this.macroReference.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(6, this.macroReference);
            }
            if (!this.functionId.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(7, this.functionId);
            }
            if (this.integer != 0) {
                output.writeInt64(8, this.integer);
            }
            if (this.containsReferences) {
                output.writeBool(9, this.containsReferences);
            }
            if (this.escaping != null && this.escaping.length > 0) {
                for (int element3 : this.escaping) {
                    output.writeInt32(10, element3);
                }
            }
            if (this.templateToken != null) {
                for (Value element222 : this.templateToken) {
                    output.writeMessage(11, element222);
                }
            }
            if (this.boolean_) {
                output.writeBool(12, this.boolean_);
            }
            if (!this.tagReference.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(13, this.tagReference);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0 + CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            if (!this.string.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(2, this.string);
            }
            if (this.listItem != null) {
                for (Value element : this.listItem) {
                    size += CodedOutputByteBufferNano.computeMessageSize(3, element);
                }
            }
            if (this.mapKey != null) {
                for (Value element2 : this.mapKey) {
                    size += CodedOutputByteBufferNano.computeMessageSize(4, element2);
                }
            }
            if (this.mapValue != null) {
                for (Value element22 : this.mapValue) {
                    size += CodedOutputByteBufferNano.computeMessageSize(5, element22);
                }
            }
            if (!this.macroReference.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(6, this.macroReference);
            }
            if (!this.functionId.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(7, this.functionId);
            }
            if (this.integer != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(8, this.integer);
            }
            if (this.containsReferences) {
                size += CodedOutputByteBufferNano.computeBoolSize(9, this.containsReferences);
            }
            if (this.escaping != null && this.escaping.length > 0) {
                int dataSize = 0;
                for (int element3 : this.escaping) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element3);
                }
                size = (size + dataSize) + (this.escaping.length * 1);
            }
            if (this.templateToken != null) {
                for (Value element222 : this.templateToken) {
                    size += CodedOutputByteBufferNano.computeMessageSize(11, element222);
                }
            }
            if (this.boolean_) {
                size += CodedOutputByteBufferNano.computeBoolSize(12, this.boolean_);
            }
            if (!this.tagReference.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(13, this.tagReference);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public Value mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                int arrayLength;
                int i;
                Value[] newArray;
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        int temp = input.readInt32();
                        if (temp != 1 && temp != 2 && temp != 3 && temp != 4 && temp != 5 && temp != 6 && temp != 7 && temp != 8 && temp != 9) {
                            this.type = 1;
                            break;
                        }
                        this.type = temp;
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        this.string = input.readString();
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 26);
                        if (this.listItem == null) {
                            i = 0;
                        } else {
                            i = this.listItem.length;
                        }
                        newArray = new Value[(i + arrayLength)];
                        if (this.listItem != null) {
                            System.arraycopy(this.listItem, 0, newArray, 0, i);
                        }
                        this.listItem = newArray;
                        while (i < this.listItem.length - 1) {
                            this.listItem[i] = new Value();
                            input.readMessage(this.listItem[i]);
                            input.readTag();
                            i++;
                        }
                        this.listItem[i] = new Value();
                        input.readMessage(this.listItem[i]);
                        continue;
                    case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 34);
                        if (this.mapKey == null) {
                            i = 0;
                        } else {
                            i = this.mapKey.length;
                        }
                        newArray = new Value[(i + arrayLength)];
                        if (this.mapKey != null) {
                            System.arraycopy(this.mapKey, 0, newArray, 0, i);
                        }
                        this.mapKey = newArray;
                        while (i < this.mapKey.length - 1) {
                            this.mapKey[i] = new Value();
                            input.readMessage(this.mapKey[i]);
                            input.readTag();
                            i++;
                        }
                        this.mapKey[i] = new Value();
                        input.readMessage(this.mapKey[i]);
                        continue;
                    case HeaderMapDB.COMPARATOR_LONG_ARRAY /*42*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 42);
                        if (this.mapValue == null) {
                            i = 0;
                        } else {
                            i = this.mapValue.length;
                        }
                        newArray = new Value[(i + arrayLength)];
                        if (this.mapValue != null) {
                            System.arraycopy(this.mapValue, 0, newArray, 0, i);
                        }
                        this.mapValue = newArray;
                        while (i < this.mapValue.length - 1) {
                            this.mapValue[i] = new Value();
                            input.readMessage(this.mapValue[i]);
                            input.readTag();
                            i++;
                        }
                        this.mapValue[i] = new Value();
                        input.readMessage(this.mapValue[i]);
                        continue;
                    case HeaderMapDB.FUN_EMPTY_ITERATOR /*50*/:
                        this.macroReference = input.readString();
                        continue;
                    case Header.LONG_10 /*58*/:
                        this.functionId = input.readString();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_FAST_FORWARD /*64*/:
                        this.integer = input.readInt64();
                        continue;
                    case Header.LONG_F3 /*72*/:
                        this.containsReferences = input.readBool();
                        continue;
                    case Header.LONG_F7 /*80*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 80);
                        i = this.escaping.length;
                        int[] newArray2 = new int[(i + arrayLength)];
                        System.arraycopy(this.escaping, 0, newArray2, 0, i);
                        this.escaping = newArray2;
                        while (i < this.escaping.length - 1) {
                            this.escaping[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.escaping[i] = input.readInt32();
                        continue;
                    case Header.SHORT_M1 /*90*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 90);
                        if (this.templateToken == null) {
                            i = 0;
                        } else {
                            i = this.templateToken.length;
                        }
                        newArray = new Value[(i + arrayLength)];
                        if (this.templateToken != null) {
                            System.arraycopy(this.templateToken, 0, newArray, 0, i);
                        }
                        this.templateToken = newArray;
                        while (i < this.templateToken.length - 1) {
                            this.templateToken[i] = new Value();
                            input.readMessage(this.templateToken[i]);
                            input.readTag();
                            i++;
                        }
                        this.templateToken[i] = new Value();
                        input.readMessage(this.templateToken[i]);
                        continue;
                    case Header.FLOAT_M1 /*96*/:
                        this.boolean_ = input.readBool();
                        continue;
                    case Header.DOUBLE_SHORT /*106*/:
                        this.tagReference = input.readString();
                        continue;
                    default:
                        if (this.unknownFieldData == null) {
                            this.unknownFieldData = new ArrayList();
                        }
                        if (!WireFormatNano.storeUnknownField(this.unknownFieldData, input, tag)) {
                            break;
                        }
                        continue;
                }
                return this;
            }
        }

        public static Value parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Value) MessageNano.mergeFrom(new Value(), data);
        }

        public static Value parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Value().mergeFrom(input);
        }
    }
}
