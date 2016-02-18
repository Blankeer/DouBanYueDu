package com.google.analytics.containertag.proto;

import android.support.v4.media.TransportMediator;
import com.alipay.sdk.protocol.h;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.tagmanager.protobuf.nano.CodedInputByteBufferNano;
import com.google.tagmanager.protobuf.nano.CodedOutputByteBufferNano;
import com.google.tagmanager.protobuf.nano.ExtendableMessageNano;
import com.google.tagmanager.protobuf.nano.Extension;
import com.google.tagmanager.protobuf.nano.Extension.TypeLiteral;
import com.google.tagmanager.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.tagmanager.protobuf.nano.MessageNano;
import com.google.tagmanager.protobuf.nano.WireFormatNano;
import io.realm.internal.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public interface Serving {

    public interface ResourceState {
        public static final int LIVE = 2;
        public static final int PREVIEW = 1;
    }

    public interface ResourceType {
        public static final int CLEAR_CACHE = 6;
        public static final int GET_COOKIE = 5;
        public static final int JS_RESOURCE = 1;
        public static final int NS_RESOURCE = 2;
        public static final int PIXEL_COLLECTION = 3;
        public static final int RAW_PROTO = 7;
        public static final int SET_COOKIE = 4;
    }

    public static final class CacheOption extends ExtendableMessageNano {
        public static final CacheOption[] EMPTY_ARRAY;
        public int expirationSeconds;
        public int gcacheExpirationSeconds;
        public int level;

        public interface CacheLevel {
            public static final int NO_CACHE = 1;
            public static final int PRIVATE = 2;
            public static final int PUBLIC = 3;
        }

        static {
            EMPTY_ARRAY = new CacheOption[0];
        }

        public CacheOption() {
            this.level = 1;
            this.expirationSeconds = 0;
            this.gcacheExpirationSeconds = 0;
        }

        public final CacheOption clear() {
            this.level = 1;
            this.expirationSeconds = 0;
            this.gcacheExpirationSeconds = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CacheOption)) {
                return false;
            }
            CacheOption other = (CacheOption) o;
            if (this.level == other.level && this.expirationSeconds == other.expirationSeconds && this.gcacheExpirationSeconds == other.gcacheExpirationSeconds) {
                if (this.unknownFieldData == null) {
                    if (other.unknownFieldData == null) {
                        return true;
                    }
                } else if (this.unknownFieldData.equals(other.unknownFieldData)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return ((((((this.level + 527) * 31) + this.expirationSeconds) * 31) + this.gcacheExpirationSeconds) * 31) + (this.unknownFieldData == null ? 0 : this.unknownFieldData.hashCode());
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.level != 1) {
                output.writeInt32(1, this.level);
            }
            if (this.expirationSeconds != 0) {
                output.writeInt32(2, this.expirationSeconds);
            }
            if (this.gcacheExpirationSeconds != 0) {
                output.writeInt32(3, this.gcacheExpirationSeconds);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.level != 1) {
                size = 0 + CodedOutputByteBufferNano.computeInt32Size(1, this.level);
            }
            if (this.expirationSeconds != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, this.expirationSeconds);
            }
            if (this.gcacheExpirationSeconds != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, this.gcacheExpirationSeconds);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public CacheOption mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        int temp = input.readInt32();
                        if (temp != 1 && temp != 2 && temp != 3) {
                            this.level = 1;
                            break;
                        }
                        this.level = temp;
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                        this.expirationSeconds = input.readInt32();
                        continue;
                    case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                        this.gcacheExpirationSeconds = input.readInt32();
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

        public static CacheOption parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (CacheOption) MessageNano.mergeFrom(new CacheOption(), data);
        }

        public static CacheOption parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new CacheOption().mergeFrom(input);
        }
    }

    public static final class Container extends ExtendableMessageNano {
        public static final Container[] EMPTY_ARRAY;
        public String containerId;
        public Resource jsResource;
        public int state;
        public String version;

        static {
            EMPTY_ARRAY = new Container[0];
        }

        public Container() {
            this.jsResource = null;
            this.containerId = Table.STRING_DEFAULT_VALUE;
            this.state = 1;
            this.version = Table.STRING_DEFAULT_VALUE;
        }

        public final Container clear() {
            this.jsResource = null;
            this.containerId = Table.STRING_DEFAULT_VALUE;
            this.state = 1;
            this.version = Table.STRING_DEFAULT_VALUE;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean equals(java.lang.Object r6) {
            /*
            r5 = this;
            r1 = 1;
            r2 = 0;
            if (r6 != r5) goto L_0x0005;
        L_0x0004:
            return r1;
        L_0x0005:
            r3 = r6 instanceof com.google.analytics.containertag.proto.Serving.Container;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Serving.Container) r0;
            r3 = r5.jsResource;
            if (r3 != 0) goto L_0x0036;
        L_0x0012:
            r3 = r0.jsResource;
            if (r3 != 0) goto L_0x0034;
        L_0x0016:
            r3 = r5.containerId;
            if (r3 != 0) goto L_0x0041;
        L_0x001a:
            r3 = r0.containerId;
            if (r3 != 0) goto L_0x0034;
        L_0x001e:
            r3 = r5.state;
            r4 = r0.state;
            if (r3 != r4) goto L_0x0034;
        L_0x0024:
            r3 = r5.version;
            if (r3 != 0) goto L_0x004c;
        L_0x0028:
            r3 = r0.version;
            if (r3 != 0) goto L_0x0034;
        L_0x002c:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x0057;
        L_0x0030:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x0034:
            r1 = r2;
            goto L_0x0004;
        L_0x0036:
            r3 = r5.jsResource;
            r4 = r0.jsResource;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0034;
        L_0x0040:
            goto L_0x0016;
        L_0x0041:
            r3 = r5.containerId;
            r4 = r0.containerId;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0034;
        L_0x004b:
            goto L_0x001e;
        L_0x004c:
            r3 = r5.version;
            r4 = r0.version;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0034;
        L_0x0056:
            goto L_0x002c;
        L_0x0057:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0034;
        L_0x0061:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Serving.Container.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((((this.jsResource == null ? 0 : this.jsResource.hashCode()) + 527) * 31) + (this.containerId == null ? 0 : this.containerId.hashCode())) * 31) + this.state) * 31) + (this.version == null ? 0 : this.version.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.jsResource != null) {
                output.writeMessage(1, this.jsResource);
            }
            output.writeString(3, this.containerId);
            output.writeInt32(4, this.state);
            if (!this.version.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(5, this.version);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.jsResource != null) {
                size = 0 + CodedOutputByteBufferNano.computeMessageSize(1, this.jsResource);
            }
            size = (size + CodedOutputByteBufferNano.computeStringSize(3, this.containerId)) + CodedOutputByteBufferNano.computeInt32Size(4, this.state);
            if (!this.version.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(5, this.version);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public Container mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        this.jsResource = new Resource();
                        input.readMessage(this.jsResource);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        this.containerId = input.readString();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                        int temp = input.readInt32();
                        if (temp != 1 && temp != 2) {
                            this.state = 1;
                            break;
                        }
                        this.state = temp;
                        continue;
                    case HeaderMapDB.COMPARATOR_LONG_ARRAY /*42*/:
                        this.version = input.readString();
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

        public static Container parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Container) MessageNano.mergeFrom(new Container(), data);
        }

        public static Container parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Container().mergeFrom(input);
        }
    }

    public static final class FunctionCall extends ExtendableMessageNano {
        public static final FunctionCall[] EMPTY_ARRAY;
        public int function;
        public boolean liveOnly;
        public int name;
        public int[] property;
        public boolean serverSide;

        static {
            EMPTY_ARRAY = new FunctionCall[0];
        }

        public FunctionCall() {
            this.property = WireFormatNano.EMPTY_INT_ARRAY;
            this.function = 0;
            this.name = 0;
            this.liveOnly = false;
            this.serverSide = false;
        }

        public final FunctionCall clear() {
            this.property = WireFormatNano.EMPTY_INT_ARRAY;
            this.function = 0;
            this.name = 0;
            this.liveOnly = false;
            this.serverSide = false;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FunctionCall)) {
                return false;
            }
            FunctionCall other = (FunctionCall) o;
            if (Arrays.equals(this.property, other.property) && this.function == other.function && this.name == other.name && this.liveOnly == other.liveOnly && this.serverSide == other.serverSide) {
                if (this.unknownFieldData == null) {
                    if (other.unknownFieldData == null) {
                        return true;
                    }
                } else if (this.unknownFieldData.equals(other.unknownFieldData)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            int i;
            int i2 = 1;
            int result = 17;
            if (this.property == null) {
                result = 17 * 31;
            } else {
                for (int i3 : this.property) {
                    result = (result * 31) + i3;
                }
            }
            int i32 = ((((result * 31) + this.function) * 31) + this.name) * 31;
            if (this.liveOnly) {
                i = 1;
            } else {
                i = 2;
            }
            i = (i32 + i) * 31;
            if (!this.serverSide) {
                i2 = 2;
            }
            return ((i + i2) * 31) + (this.unknownFieldData == null ? 0 : this.unknownFieldData.hashCode());
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.serverSide) {
                output.writeBool(1, this.serverSide);
            }
            output.writeInt32(2, this.function);
            if (this.property != null) {
                for (int element : this.property) {
                    output.writeInt32(3, element);
                }
            }
            if (this.name != 0) {
                output.writeInt32(4, this.name);
            }
            if (this.liveOnly) {
                output.writeBool(6, this.liveOnly);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.serverSide) {
                size = 0 + CodedOutputByteBufferNano.computeBoolSize(1, this.serverSide);
            }
            size += CodedOutputByteBufferNano.computeInt32Size(2, this.function);
            if (this.property != null && this.property.length > 0) {
                int dataSize = 0;
                for (int element : this.property) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element);
                }
                size = (size + dataSize) + (this.property.length * 1);
            }
            if (this.name != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, this.name);
            }
            if (this.liveOnly) {
                size += CodedOutputByteBufferNano.computeBoolSize(6, this.liveOnly);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public FunctionCall mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        this.serverSide = input.readBool();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                        this.function = input.readInt32();
                        continue;
                    case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 24);
                        int i = this.property.length;
                        int[] newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.property, 0, newArray, 0, i);
                        this.property = newArray;
                        while (i < this.property.length - 1) {
                            this.property[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.property[i] = input.readInt32();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                        this.name = input.readInt32();
                        continue;
                    case HeaderMapDB.B_TREE_COMPRESSION_SERIALIZER /*48*/:
                        this.liveOnly = input.readBool();
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

        public static FunctionCall parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (FunctionCall) MessageNano.mergeFrom(new FunctionCall(), data);
        }

        public static FunctionCall parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new FunctionCall().mergeFrom(input);
        }
    }

    public static final class GaExperimentRandom extends ExtendableMessageNano {
        public static final GaExperimentRandom[] EMPTY_ARRAY;
        public String key;
        public long lifetimeInMilliseconds;
        public long maxRandom;
        public long minRandom;
        public boolean retainOriginalValue;

        static {
            EMPTY_ARRAY = new GaExperimentRandom[0];
        }

        public GaExperimentRandom() {
            this.key = Table.STRING_DEFAULT_VALUE;
            this.minRandom = 0;
            this.maxRandom = 2147483647L;
            this.retainOriginalValue = false;
            this.lifetimeInMilliseconds = 0;
        }

        public final GaExperimentRandom clear() {
            this.key = Table.STRING_DEFAULT_VALUE;
            this.minRandom = 0;
            this.maxRandom = 2147483647L;
            this.retainOriginalValue = false;
            this.lifetimeInMilliseconds = 0;
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
            r3 = r9 instanceof com.google.analytics.containertag.proto.Serving.GaExperimentRandom;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r9;
            r0 = (com.google.analytics.containertag.proto.Serving.GaExperimentRandom) r0;
            r3 = r8.key;
            if (r3 != 0) goto L_0x003e;
        L_0x0012:
            r3 = r0.key;
            if (r3 != 0) goto L_0x003c;
        L_0x0016:
            r4 = r8.minRandom;
            r6 = r0.minRandom;
            r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r3 != 0) goto L_0x003c;
        L_0x001e:
            r4 = r8.maxRandom;
            r6 = r0.maxRandom;
            r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r3 != 0) goto L_0x003c;
        L_0x0026:
            r3 = r8.retainOriginalValue;
            r4 = r0.retainOriginalValue;
            if (r3 != r4) goto L_0x003c;
        L_0x002c:
            r4 = r8.lifetimeInMilliseconds;
            r6 = r0.lifetimeInMilliseconds;
            r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r3 != 0) goto L_0x003c;
        L_0x0034:
            r3 = r8.unknownFieldData;
            if (r3 != 0) goto L_0x0049;
        L_0x0038:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x003c:
            r1 = r2;
            goto L_0x0004;
        L_0x003e:
            r3 = r8.key;
            r4 = r0.key;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x003c;
        L_0x0048:
            goto L_0x0016;
        L_0x0049:
            r3 = r8.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x003c;
        L_0x0053:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Serving.GaExperimentRandom.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((((((this.key == null ? 0 : this.key.hashCode()) + 527) * 31) + ((int) (this.minRandom ^ (this.minRandom >>> 32)))) * 31) + ((int) (this.maxRandom ^ (this.maxRandom >>> 32)))) * 31) + (this.retainOriginalValue ? 1 : 2)) * 31) + ((int) (this.lifetimeInMilliseconds ^ (this.lifetimeInMilliseconds >>> 32)))) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.key.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(1, this.key);
            }
            if (this.minRandom != 0) {
                output.writeInt64(2, this.minRandom);
            }
            if (this.maxRandom != 2147483647L) {
                output.writeInt64(3, this.maxRandom);
            }
            if (this.retainOriginalValue) {
                output.writeBool(4, this.retainOriginalValue);
            }
            if (this.lifetimeInMilliseconds != 0) {
                output.writeInt64(5, this.lifetimeInMilliseconds);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (!this.key.equals(Table.STRING_DEFAULT_VALUE)) {
                size = 0 + CodedOutputByteBufferNano.computeStringSize(1, this.key);
            }
            if (this.minRandom != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(2, this.minRandom);
            }
            if (this.maxRandom != 2147483647L) {
                size += CodedOutputByteBufferNano.computeInt64Size(3, this.maxRandom);
            }
            if (this.retainOriginalValue) {
                size += CodedOutputByteBufferNano.computeBoolSize(4, this.retainOriginalValue);
            }
            if (this.lifetimeInMilliseconds != 0) {
                size += CodedOutputByteBufferNano.computeInt64Size(5, this.lifetimeInMilliseconds);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public GaExperimentRandom mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        this.key = input.readString();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                        this.minRandom = input.readInt64();
                        continue;
                    case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                        this.maxRandom = input.readInt64();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                        this.retainOriginalValue = input.readBool();
                        continue;
                    case HeaderMapDB.COMPARATOR_CHAR_ARRAY /*40*/:
                        this.lifetimeInMilliseconds = input.readInt64();
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

        public static GaExperimentRandom parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (GaExperimentRandom) MessageNano.mergeFrom(new GaExperimentRandom(), data);
        }

        public static GaExperimentRandom parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new GaExperimentRandom().mergeFrom(input);
        }
    }

    public static final class GaExperimentSupplemental extends ExtendableMessageNano {
        public static final GaExperimentSupplemental[] EMPTY_ARRAY;
        public GaExperimentRandom[] experimentRandom;
        public Value[] valueToClear;
        public Value[] valueToPush;

        static {
            EMPTY_ARRAY = new GaExperimentSupplemental[0];
        }

        public GaExperimentSupplemental() {
            this.valueToPush = Value.EMPTY_ARRAY;
            this.valueToClear = Value.EMPTY_ARRAY;
            this.experimentRandom = GaExperimentRandom.EMPTY_ARRAY;
        }

        public final GaExperimentSupplemental clear() {
            this.valueToPush = Value.EMPTY_ARRAY;
            this.valueToClear = Value.EMPTY_ARRAY;
            this.experimentRandom = GaExperimentRandom.EMPTY_ARRAY;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof GaExperimentSupplemental)) {
                return false;
            }
            GaExperimentSupplemental other = (GaExperimentSupplemental) o;
            if (Arrays.equals(this.valueToPush, other.valueToPush) && Arrays.equals(this.valueToClear, other.valueToClear) && Arrays.equals(this.experimentRandom, other.experimentRandom)) {
                if (this.unknownFieldData == null) {
                    if (other.unknownFieldData == null) {
                        return true;
                    }
                } else if (this.unknownFieldData.equals(other.unknownFieldData)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            int i;
            int i2 = 0;
            int result = 17;
            if (this.valueToPush == null) {
                result = 17 * 31;
            } else {
                for (i = 0; i < this.valueToPush.length; i++) {
                    result = (result * 31) + (this.valueToPush[i] == null ? 0 : this.valueToPush[i].hashCode());
                }
            }
            if (this.valueToClear == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.valueToClear.length; i++) {
                    result = (result * 31) + (this.valueToClear[i] == null ? 0 : this.valueToClear[i].hashCode());
                }
            }
            if (this.experimentRandom == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.experimentRandom.length; i++) {
                    result = (result * 31) + (this.experimentRandom[i] == null ? 0 : this.experimentRandom[i].hashCode());
                }
            }
            int i3 = result * 31;
            if (this.unknownFieldData != null) {
                i2 = this.unknownFieldData.hashCode();
            }
            return i3 + i2;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.valueToPush != null) {
                for (Value element : this.valueToPush) {
                    output.writeMessage(1, element);
                }
            }
            if (this.valueToClear != null) {
                for (Value element2 : this.valueToClear) {
                    output.writeMessage(2, element2);
                }
            }
            if (this.experimentRandom != null) {
                for (GaExperimentRandom element3 : this.experimentRandom) {
                    output.writeMessage(3, element3);
                }
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.valueToPush != null) {
                for (Value element : this.valueToPush) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
            }
            if (this.valueToClear != null) {
                for (Value element2 : this.valueToClear) {
                    size += CodedOutputByteBufferNano.computeMessageSize(2, element2);
                }
            }
            if (this.experimentRandom != null) {
                for (GaExperimentRandom element3 : this.experimentRandom) {
                    size += CodedOutputByteBufferNano.computeMessageSize(3, element3);
                }
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public GaExperimentSupplemental mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                int arrayLength;
                int i;
                Value[] newArray;
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.valueToPush == null) {
                            i = 0;
                        } else {
                            i = this.valueToPush.length;
                        }
                        newArray = new Value[(i + arrayLength)];
                        if (this.valueToPush != null) {
                            System.arraycopy(this.valueToPush, 0, newArray, 0, i);
                        }
                        this.valueToPush = newArray;
                        while (i < this.valueToPush.length - 1) {
                            this.valueToPush[i] = new Value();
                            input.readMessage(this.valueToPush[i]);
                            input.readTag();
                            i++;
                        }
                        this.valueToPush[i] = new Value();
                        input.readMessage(this.valueToPush[i]);
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                        if (this.valueToClear == null) {
                            i = 0;
                        } else {
                            i = this.valueToClear.length;
                        }
                        newArray = new Value[(i + arrayLength)];
                        if (this.valueToClear != null) {
                            System.arraycopy(this.valueToClear, 0, newArray, 0, i);
                        }
                        this.valueToClear = newArray;
                        while (i < this.valueToClear.length - 1) {
                            this.valueToClear[i] = new Value();
                            input.readMessage(this.valueToClear[i]);
                            input.readTag();
                            i++;
                        }
                        this.valueToClear[i] = new Value();
                        input.readMessage(this.valueToClear[i]);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 26);
                        if (this.experimentRandom == null) {
                            i = 0;
                        } else {
                            i = this.experimentRandom.length;
                        }
                        GaExperimentRandom[] newArray2 = new GaExperimentRandom[(i + arrayLength)];
                        if (this.experimentRandom != null) {
                            System.arraycopy(this.experimentRandom, 0, newArray2, 0, i);
                        }
                        this.experimentRandom = newArray2;
                        while (i < this.experimentRandom.length - 1) {
                            this.experimentRandom[i] = new GaExperimentRandom();
                            input.readMessage(this.experimentRandom[i]);
                            input.readTag();
                            i++;
                        }
                        this.experimentRandom[i] = new GaExperimentRandom();
                        input.readMessage(this.experimentRandom[i]);
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

        public static GaExperimentSupplemental parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (GaExperimentSupplemental) MessageNano.mergeFrom(new GaExperimentSupplemental(), data);
        }

        public static GaExperimentSupplemental parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new GaExperimentSupplemental().mergeFrom(input);
        }
    }

    public static final class Property extends ExtendableMessageNano {
        public static final Property[] EMPTY_ARRAY;
        public int key;
        public int value;

        static {
            EMPTY_ARRAY = new Property[0];
        }

        public Property() {
            this.key = 0;
            this.value = 0;
        }

        public final Property clear() {
            this.key = 0;
            this.value = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Property)) {
                return false;
            }
            Property other = (Property) o;
            if (this.key == other.key && this.value == other.value) {
                if (this.unknownFieldData == null) {
                    if (other.unknownFieldData == null) {
                        return true;
                    }
                } else if (this.unknownFieldData.equals(other.unknownFieldData)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return ((((this.key + 527) * 31) + this.value) * 31) + (this.unknownFieldData == null ? 0 : this.unknownFieldData.hashCode());
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt32(1, this.key);
            output.writeInt32(2, this.value);
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = ((0 + CodedOutputByteBufferNano.computeInt32Size(1, this.key)) + CodedOutputByteBufferNano.computeInt32Size(2, this.value)) + WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public Property mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        this.key = input.readInt32();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                        this.value = input.readInt32();
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

        public static Property parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Property) MessageNano.mergeFrom(new Property(), data);
        }

        public static Property parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Property().mergeFrom(input);
        }
    }

    public static final class Resource extends ExtendableMessageNano {
        public static final Resource[] EMPTY_ARRAY;
        private static final String TEMPLATE_VERSION_SET_DEFAULT = "0";
        public String[] key;
        public CacheOption liveJsCacheOption;
        public FunctionCall[] macro;
        public String malwareScanAuthCode;
        public boolean oBSOLETEEnableAutoEventTracking;
        public FunctionCall[] predicate;
        public String previewAuthCode;
        public Property[] property;
        public float reportingSampleRate;
        public int resourceFormatVersion;
        public Rule[] rule;
        public String[] supplemental;
        public FunctionCall[] tag;
        public String templateVersionSet;
        public String[] usageContext;
        public Value[] value;
        public String version;

        static {
            EMPTY_ARRAY = new Resource[0];
        }

        public Resource() {
            this.supplemental = WireFormatNano.EMPTY_STRING_ARRAY;
            this.key = WireFormatNano.EMPTY_STRING_ARRAY;
            this.value = Value.EMPTY_ARRAY;
            this.property = Property.EMPTY_ARRAY;
            this.macro = FunctionCall.EMPTY_ARRAY;
            this.tag = FunctionCall.EMPTY_ARRAY;
            this.predicate = FunctionCall.EMPTY_ARRAY;
            this.rule = Rule.EMPTY_ARRAY;
            this.previewAuthCode = Table.STRING_DEFAULT_VALUE;
            this.malwareScanAuthCode = Table.STRING_DEFAULT_VALUE;
            this.templateVersionSet = TEMPLATE_VERSION_SET_DEFAULT;
            this.version = Table.STRING_DEFAULT_VALUE;
            this.liveJsCacheOption = null;
            this.reportingSampleRate = 0.0f;
            this.oBSOLETEEnableAutoEventTracking = false;
            this.usageContext = WireFormatNano.EMPTY_STRING_ARRAY;
            this.resourceFormatVersion = 0;
        }

        public final Resource clear() {
            this.supplemental = WireFormatNano.EMPTY_STRING_ARRAY;
            this.key = WireFormatNano.EMPTY_STRING_ARRAY;
            this.value = Value.EMPTY_ARRAY;
            this.property = Property.EMPTY_ARRAY;
            this.macro = FunctionCall.EMPTY_ARRAY;
            this.tag = FunctionCall.EMPTY_ARRAY;
            this.predicate = FunctionCall.EMPTY_ARRAY;
            this.rule = Rule.EMPTY_ARRAY;
            this.previewAuthCode = Table.STRING_DEFAULT_VALUE;
            this.malwareScanAuthCode = Table.STRING_DEFAULT_VALUE;
            this.templateVersionSet = TEMPLATE_VERSION_SET_DEFAULT;
            this.version = Table.STRING_DEFAULT_VALUE;
            this.liveJsCacheOption = null;
            this.reportingSampleRate = 0.0f;
            this.oBSOLETEEnableAutoEventTracking = false;
            this.usageContext = WireFormatNano.EMPTY_STRING_ARRAY;
            this.resourceFormatVersion = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean equals(java.lang.Object r6) {
            /*
            r5 = this;
            r1 = 1;
            r2 = 0;
            if (r6 != r5) goto L_0x0005;
        L_0x0004:
            return r1;
        L_0x0005:
            r3 = r6 instanceof com.google.analytics.containertag.proto.Serving.Resource;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Serving.Resource) r0;
            r3 = r5.supplemental;
            r4 = r0.supplemental;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x0018:
            r3 = r5.key;
            r4 = r0.key;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x0022:
            r3 = r5.value;
            r4 = r0.value;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x002c:
            r3 = r5.property;
            r4 = r0.property;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x0036:
            r3 = r5.macro;
            r4 = r0.macro;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x0040:
            r3 = r5.tag;
            r4 = r0.tag;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x004a:
            r3 = r5.predicate;
            r4 = r0.predicate;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x0054:
            r3 = r5.rule;
            r4 = r0.rule;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x005e:
            r3 = r5.previewAuthCode;
            if (r3 != 0) goto L_0x00af;
        L_0x0062:
            r3 = r0.previewAuthCode;
            if (r3 != 0) goto L_0x00ac;
        L_0x0066:
            r3 = r5.malwareScanAuthCode;
            if (r3 != 0) goto L_0x00ba;
        L_0x006a:
            r3 = r0.malwareScanAuthCode;
            if (r3 != 0) goto L_0x00ac;
        L_0x006e:
            r3 = r5.templateVersionSet;
            if (r3 != 0) goto L_0x00c5;
        L_0x0072:
            r3 = r0.templateVersionSet;
            if (r3 != 0) goto L_0x00ac;
        L_0x0076:
            r3 = r5.version;
            if (r3 != 0) goto L_0x00d0;
        L_0x007a:
            r3 = r0.version;
            if (r3 != 0) goto L_0x00ac;
        L_0x007e:
            r3 = r5.liveJsCacheOption;
            if (r3 != 0) goto L_0x00db;
        L_0x0082:
            r3 = r0.liveJsCacheOption;
            if (r3 != 0) goto L_0x00ac;
        L_0x0086:
            r3 = r5.reportingSampleRate;
            r4 = r0.reportingSampleRate;
            r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
            if (r3 != 0) goto L_0x00ac;
        L_0x008e:
            r3 = r5.oBSOLETEEnableAutoEventTracking;
            r4 = r0.oBSOLETEEnableAutoEventTracking;
            if (r3 != r4) goto L_0x00ac;
        L_0x0094:
            r3 = r5.usageContext;
            r4 = r0.usageContext;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x009e:
            r3 = r5.resourceFormatVersion;
            r4 = r0.resourceFormatVersion;
            if (r3 != r4) goto L_0x00ac;
        L_0x00a4:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x00e6;
        L_0x00a8:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x00ac:
            r1 = r2;
            goto L_0x0004;
        L_0x00af:
            r3 = r5.previewAuthCode;
            r4 = r0.previewAuthCode;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x00b9:
            goto L_0x0066;
        L_0x00ba:
            r3 = r5.malwareScanAuthCode;
            r4 = r0.malwareScanAuthCode;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x00c4:
            goto L_0x006e;
        L_0x00c5:
            r3 = r5.templateVersionSet;
            r4 = r0.templateVersionSet;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x00cf:
            goto L_0x0076;
        L_0x00d0:
            r3 = r5.version;
            r4 = r0.version;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x00da:
            goto L_0x007e;
        L_0x00db:
            r3 = r5.liveJsCacheOption;
            r4 = r0.liveJsCacheOption;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x00e5:
            goto L_0x0086;
        L_0x00e6:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x00ac;
        L_0x00f0:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Serving.Resource.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i;
            int i2 = 0;
            int result = 17;
            if (this.supplemental == null) {
                result = 17 * 31;
            } else {
                for (i = 0; i < this.supplemental.length; i++) {
                    result = (result * 31) + (this.supplemental[i] == null ? 0 : this.supplemental[i].hashCode());
                }
            }
            if (this.key == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.key.length; i++) {
                    result = (result * 31) + (this.key[i] == null ? 0 : this.key[i].hashCode());
                }
            }
            if (this.value == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.value.length; i++) {
                    result = (result * 31) + (this.value[i] == null ? 0 : this.value[i].hashCode());
                }
            }
            if (this.property == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.property.length; i++) {
                    result = (result * 31) + (this.property[i] == null ? 0 : this.property[i].hashCode());
                }
            }
            if (this.macro == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.macro.length; i++) {
                    result = (result * 31) + (this.macro[i] == null ? 0 : this.macro[i].hashCode());
                }
            }
            if (this.tag == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.tag.length; i++) {
                    result = (result * 31) + (this.tag[i] == null ? 0 : this.tag[i].hashCode());
                }
            }
            if (this.predicate == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.predicate.length; i++) {
                    result = (result * 31) + (this.predicate[i] == null ? 0 : this.predicate[i].hashCode());
                }
            }
            if (this.rule == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.rule.length; i++) {
                    result = (result * 31) + (this.rule[i] == null ? 0 : this.rule[i].hashCode());
                }
            }
            result = (((((((((((((result * 31) + (this.previewAuthCode == null ? 0 : this.previewAuthCode.hashCode())) * 31) + (this.malwareScanAuthCode == null ? 0 : this.malwareScanAuthCode.hashCode())) * 31) + (this.templateVersionSet == null ? 0 : this.templateVersionSet.hashCode())) * 31) + (this.version == null ? 0 : this.version.hashCode())) * 31) + (this.liveJsCacheOption == null ? 0 : this.liveJsCacheOption.hashCode())) * 31) + Float.floatToIntBits(this.reportingSampleRate)) * 31) + (this.oBSOLETEEnableAutoEventTracking ? 1 : 2);
            if (this.usageContext == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.usageContext.length; i++) {
                    result = (result * 31) + (this.usageContext[i] == null ? 0 : this.usageContext[i].hashCode());
                }
            }
            int i3 = ((result * 31) + this.resourceFormatVersion) * 31;
            if (this.unknownFieldData != null) {
                i2 = this.unknownFieldData.hashCode();
            }
            return i3 + i2;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.key != null) {
                for (String element : this.key) {
                    output.writeString(1, element);
                }
            }
            if (this.value != null) {
                for (Value element2 : this.value) {
                    output.writeMessage(2, element2);
                }
            }
            if (this.property != null) {
                for (Property element3 : this.property) {
                    output.writeMessage(3, element3);
                }
            }
            if (this.macro != null) {
                for (FunctionCall element4 : this.macro) {
                    output.writeMessage(4, element4);
                }
            }
            if (this.tag != null) {
                for (FunctionCall element42 : this.tag) {
                    output.writeMessage(5, element42);
                }
            }
            if (this.predicate != null) {
                for (FunctionCall element422 : this.predicate) {
                    output.writeMessage(6, element422);
                }
            }
            if (this.rule != null) {
                for (Rule element5 : this.rule) {
                    output.writeMessage(7, element5);
                }
            }
            if (!this.previewAuthCode.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(9, this.previewAuthCode);
            }
            if (!this.malwareScanAuthCode.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(10, this.malwareScanAuthCode);
            }
            if (!this.templateVersionSet.equals(TEMPLATE_VERSION_SET_DEFAULT)) {
                output.writeString(12, this.templateVersionSet);
            }
            if (!this.version.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(13, this.version);
            }
            if (this.liveJsCacheOption != null) {
                output.writeMessage(14, this.liveJsCacheOption);
            }
            if (this.reportingSampleRate != 0.0f) {
                output.writeFloat(15, this.reportingSampleRate);
            }
            if (this.usageContext != null) {
                for (String element6 : this.usageContext) {
                    output.writeString(16, element6);
                }
            }
            if (this.resourceFormatVersion != 0) {
                output.writeInt32(17, this.resourceFormatVersion);
            }
            if (this.oBSOLETEEnableAutoEventTracking) {
                output.writeBool(18, this.oBSOLETEEnableAutoEventTracking);
            }
            if (this.supplemental != null) {
                for (String element62 : this.supplemental) {
                    output.writeString(19, element62);
                }
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int dataSize;
            int size = 0;
            if (this.key != null && this.key.length > 0) {
                dataSize = 0;
                for (String element : this.key) {
                    dataSize += CodedOutputByteBufferNano.computeStringSizeNoTag(element);
                }
                size = (0 + dataSize) + (this.key.length * 1);
            }
            if (this.value != null) {
                for (Value element2 : this.value) {
                    size += CodedOutputByteBufferNano.computeMessageSize(2, element2);
                }
            }
            if (this.property != null) {
                for (Property element3 : this.property) {
                    size += CodedOutputByteBufferNano.computeMessageSize(3, element3);
                }
            }
            if (this.macro != null) {
                for (FunctionCall element4 : this.macro) {
                    size += CodedOutputByteBufferNano.computeMessageSize(4, element4);
                }
            }
            if (this.tag != null) {
                for (FunctionCall element42 : this.tag) {
                    size += CodedOutputByteBufferNano.computeMessageSize(5, element42);
                }
            }
            if (this.predicate != null) {
                for (FunctionCall element422 : this.predicate) {
                    size += CodedOutputByteBufferNano.computeMessageSize(6, element422);
                }
            }
            if (this.rule != null) {
                for (Rule element5 : this.rule) {
                    size += CodedOutputByteBufferNano.computeMessageSize(7, element5);
                }
            }
            if (!this.previewAuthCode.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(9, this.previewAuthCode);
            }
            if (!this.malwareScanAuthCode.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(10, this.malwareScanAuthCode);
            }
            if (!this.templateVersionSet.equals(TEMPLATE_VERSION_SET_DEFAULT)) {
                size += CodedOutputByteBufferNano.computeStringSize(12, this.templateVersionSet);
            }
            if (!this.version.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(13, this.version);
            }
            if (this.liveJsCacheOption != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(14, this.liveJsCacheOption);
            }
            if (this.reportingSampleRate != 0.0f) {
                size += CodedOutputByteBufferNano.computeFloatSize(15, this.reportingSampleRate);
            }
            if (this.usageContext != null && this.usageContext.length > 0) {
                dataSize = 0;
                for (String element6 : this.usageContext) {
                    dataSize += CodedOutputByteBufferNano.computeStringSizeNoTag(element6);
                }
                size = (size + dataSize) + (this.usageContext.length * 2);
            }
            if (this.resourceFormatVersion != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(17, this.resourceFormatVersion);
            }
            if (this.oBSOLETEEnableAutoEventTracking) {
                size += CodedOutputByteBufferNano.computeBoolSize(18, this.oBSOLETEEnableAutoEventTracking);
            }
            if (this.supplemental != null && this.supplemental.length > 0) {
                dataSize = 0;
                for (String element62 : this.supplemental) {
                    dataSize += CodedOutputByteBufferNano.computeStringSizeNoTag(element62);
                }
                size = (size + dataSize) + (this.supplemental.length * 2);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public Resource mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                int arrayLength;
                int i;
                String[] newArray;
                FunctionCall[] newArray2;
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        i = this.key.length;
                        newArray = new String[(i + arrayLength)];
                        System.arraycopy(this.key, 0, newArray, 0, i);
                        this.key = newArray;
                        while (i < this.key.length - 1) {
                            this.key[i] = input.readString();
                            input.readTag();
                            i++;
                        }
                        this.key[i] = input.readString();
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                        if (this.value == null) {
                            i = 0;
                        } else {
                            i = this.value.length;
                        }
                        Value[] newArray3 = new Value[(i + arrayLength)];
                        if (this.value != null) {
                            System.arraycopy(this.value, 0, newArray3, 0, i);
                        }
                        this.value = newArray3;
                        while (i < this.value.length - 1) {
                            this.value[i] = new Value();
                            input.readMessage(this.value[i]);
                            input.readTag();
                            i++;
                        }
                        this.value[i] = new Value();
                        input.readMessage(this.value[i]);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 26);
                        if (this.property == null) {
                            i = 0;
                        } else {
                            i = this.property.length;
                        }
                        Property[] newArray4 = new Property[(i + arrayLength)];
                        if (this.property != null) {
                            System.arraycopy(this.property, 0, newArray4, 0, i);
                        }
                        this.property = newArray4;
                        while (i < this.property.length - 1) {
                            this.property[i] = new Property();
                            input.readMessage(this.property[i]);
                            input.readTag();
                            i++;
                        }
                        this.property[i] = new Property();
                        input.readMessage(this.property[i]);
                        continue;
                    case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 34);
                        if (this.macro == null) {
                            i = 0;
                        } else {
                            i = this.macro.length;
                        }
                        newArray2 = new FunctionCall[(i + arrayLength)];
                        if (this.macro != null) {
                            System.arraycopy(this.macro, 0, newArray2, 0, i);
                        }
                        this.macro = newArray2;
                        while (i < this.macro.length - 1) {
                            this.macro[i] = new FunctionCall();
                            input.readMessage(this.macro[i]);
                            input.readTag();
                            i++;
                        }
                        this.macro[i] = new FunctionCall();
                        input.readMessage(this.macro[i]);
                        continue;
                    case HeaderMapDB.COMPARATOR_LONG_ARRAY /*42*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 42);
                        if (this.tag == null) {
                            i = 0;
                        } else {
                            i = this.tag.length;
                        }
                        newArray2 = new FunctionCall[(i + arrayLength)];
                        if (this.tag != null) {
                            System.arraycopy(this.tag, 0, newArray2, 0, i);
                        }
                        this.tag = newArray2;
                        while (i < this.tag.length - 1) {
                            this.tag[i] = new FunctionCall();
                            input.readMessage(this.tag[i]);
                            input.readTag();
                            i++;
                        }
                        this.tag[i] = new FunctionCall();
                        input.readMessage(this.tag[i]);
                        continue;
                    case HeaderMapDB.FUN_EMPTY_ITERATOR /*50*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 50);
                        if (this.predicate == null) {
                            i = 0;
                        } else {
                            i = this.predicate.length;
                        }
                        newArray2 = new FunctionCall[(i + arrayLength)];
                        if (this.predicate != null) {
                            System.arraycopy(this.predicate, 0, newArray2, 0, i);
                        }
                        this.predicate = newArray2;
                        while (i < this.predicate.length - 1) {
                            this.predicate[i] = new FunctionCall();
                            input.readMessage(this.predicate[i]);
                            input.readTag();
                            i++;
                        }
                        this.predicate[i] = new FunctionCall();
                        input.readMessage(this.predicate[i]);
                        continue;
                    case Header.LONG_10 /*58*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 58);
                        if (this.rule == null) {
                            i = 0;
                        } else {
                            i = this.rule.length;
                        }
                        Rule[] newArray5 = new Rule[(i + arrayLength)];
                        if (this.rule != null) {
                            System.arraycopy(this.rule, 0, newArray5, 0, i);
                        }
                        this.rule = newArray5;
                        while (i < this.rule.length - 1) {
                            this.rule[i] = new Rule();
                            input.readMessage(this.rule[i]);
                            input.readTag();
                            i++;
                        }
                        this.rule[i] = new Rule();
                        input.readMessage(this.rule[i]);
                        continue;
                    case Header.LONG_F4 /*74*/:
                        this.previewAuthCode = input.readString();
                        continue;
                    case Header.BYTE_M1 /*82*/:
                        this.malwareScanAuthCode = input.readString();
                        continue;
                    case Header.FLOAT_1 /*98*/:
                        this.templateVersionSet = input.readString();
                        continue;
                    case Header.DOUBLE_SHORT /*106*/:
                        this.version = input.readString();
                        continue;
                    case Header.ARRAY_FLOAT /*114*/:
                        this.liveJsCacheOption = new CacheOption();
                        input.readMessage(this.liveJsCacheOption);
                        continue;
                    case Header.STRING_0 /*125*/:
                        this.reportingSampleRate = input.readFloat();
                        continue;
                    case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, TransportMediator.KEYCODE_MEDIA_RECORD);
                        i = this.usageContext.length;
                        newArray = new String[(i + arrayLength)];
                        System.arraycopy(this.usageContext, 0, newArray, 0, i);
                        this.usageContext = newArray;
                        while (i < this.usageContext.length - 1) {
                            this.usageContext[i] = input.readString();
                            input.readTag();
                            i++;
                        }
                        this.usageContext[i] = input.readString();
                        continue;
                    case Header.STRING /*136*/:
                        this.resourceFormatVersion = input.readInt32();
                        continue;
                    case 144:
                        this.oBSOLETEEnableAutoEventTracking = input.readBool();
                        continue;
                    case Header.TUPLE5 /*154*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, Header.TUPLE5);
                        i = this.supplemental.length;
                        newArray = new String[(i + arrayLength)];
                        System.arraycopy(this.supplemental, 0, newArray, 0, i);
                        this.supplemental = newArray;
                        while (i < this.supplemental.length - 1) {
                            this.supplemental[i] = input.readString();
                            input.readTag();
                            i++;
                        }
                        this.supplemental[i] = input.readString();
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

        public static Resource parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Resource) MessageNano.mergeFrom(new Resource(), data);
        }

        public static Resource parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Resource().mergeFrom(input);
        }
    }

    public static final class Rule extends ExtendableMessageNano {
        public static final Rule[] EMPTY_ARRAY;
        public int[] addMacro;
        public int[] addMacroRuleName;
        public int[] addTag;
        public int[] addTagRuleName;
        public int[] negativePredicate;
        public int[] positivePredicate;
        public int[] removeMacro;
        public int[] removeMacroRuleName;
        public int[] removeTag;
        public int[] removeTagRuleName;

        static {
            EMPTY_ARRAY = new Rule[0];
        }

        public Rule() {
            this.positivePredicate = WireFormatNano.EMPTY_INT_ARRAY;
            this.negativePredicate = WireFormatNano.EMPTY_INT_ARRAY;
            this.addTag = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeTag = WireFormatNano.EMPTY_INT_ARRAY;
            this.addTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
            this.addMacro = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeMacro = WireFormatNano.EMPTY_INT_ARRAY;
            this.addMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
        }

        public final Rule clear() {
            this.positivePredicate = WireFormatNano.EMPTY_INT_ARRAY;
            this.negativePredicate = WireFormatNano.EMPTY_INT_ARRAY;
            this.addTag = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeTag = WireFormatNano.EMPTY_INT_ARRAY;
            this.addTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeTagRuleName = WireFormatNano.EMPTY_INT_ARRAY;
            this.addMacro = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeMacro = WireFormatNano.EMPTY_INT_ARRAY;
            this.addMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
            this.removeMacroRuleName = WireFormatNano.EMPTY_INT_ARRAY;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Rule)) {
                return false;
            }
            Rule other = (Rule) o;
            if (Arrays.equals(this.positivePredicate, other.positivePredicate) && Arrays.equals(this.negativePredicate, other.negativePredicate) && Arrays.equals(this.addTag, other.addTag) && Arrays.equals(this.removeTag, other.removeTag) && Arrays.equals(this.addTagRuleName, other.addTagRuleName) && Arrays.equals(this.removeTagRuleName, other.removeTagRuleName) && Arrays.equals(this.addMacro, other.addMacro) && Arrays.equals(this.removeMacro, other.removeMacro) && Arrays.equals(this.addMacroRuleName, other.addMacroRuleName) && Arrays.equals(this.removeMacroRuleName, other.removeMacroRuleName)) {
                if (this.unknownFieldData == null) {
                    if (other.unknownFieldData == null) {
                        return true;
                    }
                } else if (this.unknownFieldData.equals(other.unknownFieldData)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            int result = 17;
            if (this.positivePredicate == null) {
                result = 17 * 31;
            } else {
                for (int i : this.positivePredicate) {
                    result = (result * 31) + i;
                }
            }
            if (this.negativePredicate == null) {
                result *= 31;
            } else {
                for (int i2 : this.negativePredicate) {
                    result = (result * 31) + i2;
                }
            }
            if (this.addTag == null) {
                result *= 31;
            } else {
                for (int i22 : this.addTag) {
                    result = (result * 31) + i22;
                }
            }
            if (this.removeTag == null) {
                result *= 31;
            } else {
                for (int i222 : this.removeTag) {
                    result = (result * 31) + i222;
                }
            }
            if (this.addTagRuleName == null) {
                result *= 31;
            } else {
                for (int i2222 : this.addTagRuleName) {
                    result = (result * 31) + i2222;
                }
            }
            if (this.removeTagRuleName == null) {
                result *= 31;
            } else {
                for (int i22222 : this.removeTagRuleName) {
                    result = (result * 31) + i22222;
                }
            }
            if (this.addMacro == null) {
                result *= 31;
            } else {
                for (int i222222 : this.addMacro) {
                    result = (result * 31) + i222222;
                }
            }
            if (this.removeMacro == null) {
                result *= 31;
            } else {
                for (int i2222222 : this.removeMacro) {
                    result = (result * 31) + i2222222;
                }
            }
            if (this.addMacroRuleName == null) {
                result *= 31;
            } else {
                for (int i22222222 : this.addMacroRuleName) {
                    result = (result * 31) + i22222222;
                }
            }
            if (this.removeMacroRuleName == null) {
                result *= 31;
            } else {
                for (int i222222222 : this.removeMacroRuleName) {
                    result = (result * 31) + i222222222;
                }
            }
            return (result * 31) + (this.unknownFieldData == null ? 0 : this.unknownFieldData.hashCode());
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.positivePredicate != null) {
                for (int element : this.positivePredicate) {
                    output.writeInt32(1, element);
                }
            }
            if (this.negativePredicate != null) {
                for (int element2 : this.negativePredicate) {
                    output.writeInt32(2, element2);
                }
            }
            if (this.addTag != null) {
                for (int element22 : this.addTag) {
                    output.writeInt32(3, element22);
                }
            }
            if (this.removeTag != null) {
                for (int element222 : this.removeTag) {
                    output.writeInt32(4, element222);
                }
            }
            if (this.addTagRuleName != null) {
                for (int element2222 : this.addTagRuleName) {
                    output.writeInt32(5, element2222);
                }
            }
            if (this.removeTagRuleName != null) {
                for (int element22222 : this.removeTagRuleName) {
                    output.writeInt32(6, element22222);
                }
            }
            if (this.addMacro != null) {
                for (int element222222 : this.addMacro) {
                    output.writeInt32(7, element222222);
                }
            }
            if (this.removeMacro != null) {
                for (int element2222222 : this.removeMacro) {
                    output.writeInt32(8, element2222222);
                }
            }
            if (this.addMacroRuleName != null) {
                for (int element22222222 : this.addMacroRuleName) {
                    output.writeInt32(9, element22222222);
                }
            }
            if (this.removeMacroRuleName != null) {
                for (int element222222222 : this.removeMacroRuleName) {
                    output.writeInt32(10, element222222222);
                }
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int dataSize;
            int size = 0;
            if (this.positivePredicate != null && this.positivePredicate.length > 0) {
                dataSize = 0;
                for (int element : this.positivePredicate) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element);
                }
                size = (0 + dataSize) + (this.positivePredicate.length * 1);
            }
            if (this.negativePredicate != null && this.negativePredicate.length > 0) {
                dataSize = 0;
                for (int element2 : this.negativePredicate) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element2);
                }
                size = (size + dataSize) + (this.negativePredicate.length * 1);
            }
            if (this.addTag != null && this.addTag.length > 0) {
                dataSize = 0;
                for (int element22 : this.addTag) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element22);
                }
                size = (size + dataSize) + (this.addTag.length * 1);
            }
            if (this.removeTag != null && this.removeTag.length > 0) {
                dataSize = 0;
                for (int element222 : this.removeTag) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element222);
                }
                size = (size + dataSize) + (this.removeTag.length * 1);
            }
            if (this.addTagRuleName != null && this.addTagRuleName.length > 0) {
                dataSize = 0;
                for (int element2222 : this.addTagRuleName) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element2222);
                }
                size = (size + dataSize) + (this.addTagRuleName.length * 1);
            }
            if (this.removeTagRuleName != null && this.removeTagRuleName.length > 0) {
                dataSize = 0;
                for (int element22222 : this.removeTagRuleName) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element22222);
                }
                size = (size + dataSize) + (this.removeTagRuleName.length * 1);
            }
            if (this.addMacro != null && this.addMacro.length > 0) {
                dataSize = 0;
                for (int element222222 : this.addMacro) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element222222);
                }
                size = (size + dataSize) + (this.addMacro.length * 1);
            }
            if (this.removeMacro != null && this.removeMacro.length > 0) {
                dataSize = 0;
                for (int element2222222 : this.removeMacro) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element2222222);
                }
                size = (size + dataSize) + (this.removeMacro.length * 1);
            }
            if (this.addMacroRuleName != null && this.addMacroRuleName.length > 0) {
                dataSize = 0;
                for (int element22222222 : this.addMacroRuleName) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element22222222);
                }
                size = (size + dataSize) + (this.addMacroRuleName.length * 1);
            }
            if (this.removeMacroRuleName != null && this.removeMacroRuleName.length > 0) {
                dataSize = 0;
                for (int element222222222 : this.removeMacroRuleName) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element222222222);
                }
                size = (size + dataSize) + (this.removeMacroRuleName.length * 1);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public Rule mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                int arrayLength;
                int i;
                int[] newArray;
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 8);
                        i = this.positivePredicate.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.positivePredicate, 0, newArray, 0, i);
                        this.positivePredicate = newArray;
                        while (i < this.positivePredicate.length - 1) {
                            this.positivePredicate[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.positivePredicate[i] = input.readInt32();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 16);
                        i = this.negativePredicate.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.negativePredicate, 0, newArray, 0, i);
                        this.negativePredicate = newArray;
                        while (i < this.negativePredicate.length - 1) {
                            this.negativePredicate[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.negativePredicate[i] = input.readInt32();
                        continue;
                    case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 24);
                        i = this.addTag.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.addTag, 0, newArray, 0, i);
                        this.addTag = newArray;
                        while (i < this.addTag.length - 1) {
                            this.addTag[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.addTag[i] = input.readInt32();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 32);
                        i = this.removeTag.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.removeTag, 0, newArray, 0, i);
                        this.removeTag = newArray;
                        while (i < this.removeTag.length - 1) {
                            this.removeTag[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.removeTag[i] = input.readInt32();
                        continue;
                    case HeaderMapDB.COMPARATOR_CHAR_ARRAY /*40*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 40);
                        i = this.addTagRuleName.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.addTagRuleName, 0, newArray, 0, i);
                        this.addTagRuleName = newArray;
                        while (i < this.addTagRuleName.length - 1) {
                            this.addTagRuleName[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.addTagRuleName[i] = input.readInt32();
                        continue;
                    case HeaderMapDB.B_TREE_COMPRESSION_SERIALIZER /*48*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 48);
                        i = this.removeTagRuleName.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.removeTagRuleName, 0, newArray, 0, i);
                        this.removeTagRuleName = newArray;
                        while (i < this.removeTagRuleName.length - 1) {
                            this.removeTagRuleName[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.removeTagRuleName[i] = input.readInt32();
                        continue;
                    case HeaderMapDB.SERIALIZER_KEY_TUPLE6 /*56*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 56);
                        i = this.addMacro.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.addMacro, 0, newArray, 0, i);
                        this.addMacro = newArray;
                        while (i < this.addMacro.length - 1) {
                            this.addMacro[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.addMacro[i] = input.readInt32();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_FAST_FORWARD /*64*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 64);
                        i = this.removeMacro.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.removeMacro, 0, newArray, 0, i);
                        this.removeMacro = newArray;
                        while (i < this.removeMacro.length - 1) {
                            this.removeMacro[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.removeMacro[i] = input.readInt32();
                        continue;
                    case Header.LONG_F3 /*72*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 72);
                        i = this.addMacroRuleName.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.addMacroRuleName, 0, newArray, 0, i);
                        this.addMacroRuleName = newArray;
                        while (i < this.addMacroRuleName.length - 1) {
                            this.addMacroRuleName[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.addMacroRuleName[i] = input.readInt32();
                        continue;
                    case Header.LONG_F7 /*80*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 80);
                        i = this.removeMacroRuleName.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.removeMacroRuleName, 0, newArray, 0, i);
                        this.removeMacroRuleName = newArray;
                        while (i < this.removeMacroRuleName.length - 1) {
                            this.removeMacroRuleName[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.removeMacroRuleName[i] = input.readInt32();
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

        public static Rule parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Rule) MessageNano.mergeFrom(new Rule(), data);
        }

        public static Rule parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Rule().mergeFrom(input);
        }
    }

    public static final class ServingValue extends ExtendableMessageNano {
        public static final ServingValue[] EMPTY_ARRAY;
        public static final Extension<ServingValue> ext;
        public int[] listItem;
        public int macroNameReference;
        public int macroReference;
        public int[] mapKey;
        public int[] mapValue;
        public int tagReference;
        public int[] templateToken;

        static {
            EMPTY_ARRAY = new ServingValue[0];
            ext = Extension.create(Header.FLOAT, new TypeLiteral<ServingValue>() {
            });
        }

        public ServingValue() {
            this.listItem = WireFormatNano.EMPTY_INT_ARRAY;
            this.mapKey = WireFormatNano.EMPTY_INT_ARRAY;
            this.mapValue = WireFormatNano.EMPTY_INT_ARRAY;
            this.macroReference = 0;
            this.templateToken = WireFormatNano.EMPTY_INT_ARRAY;
            this.macroNameReference = 0;
            this.tagReference = 0;
        }

        public final ServingValue clear() {
            this.listItem = WireFormatNano.EMPTY_INT_ARRAY;
            this.mapKey = WireFormatNano.EMPTY_INT_ARRAY;
            this.mapValue = WireFormatNano.EMPTY_INT_ARRAY;
            this.macroReference = 0;
            this.templateToken = WireFormatNano.EMPTY_INT_ARRAY;
            this.macroNameReference = 0;
            this.tagReference = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ServingValue)) {
                return false;
            }
            ServingValue other = (ServingValue) o;
            if (Arrays.equals(this.listItem, other.listItem) && Arrays.equals(this.mapKey, other.mapKey) && Arrays.equals(this.mapValue, other.mapValue) && this.macroReference == other.macroReference && Arrays.equals(this.templateToken, other.templateToken) && this.macroNameReference == other.macroNameReference && this.tagReference == other.tagReference) {
                if (this.unknownFieldData == null) {
                    if (other.unknownFieldData == null) {
                        return true;
                    }
                } else if (this.unknownFieldData.equals(other.unknownFieldData)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            int result = 17;
            if (this.listItem == null) {
                result = 17 * 31;
            } else {
                for (int i : this.listItem) {
                    result = (result * 31) + i;
                }
            }
            if (this.mapKey == null) {
                result *= 31;
            } else {
                for (int i2 : this.mapKey) {
                    result = (result * 31) + i2;
                }
            }
            if (this.mapValue == null) {
                result *= 31;
            } else {
                for (int i22 : this.mapValue) {
                    result = (result * 31) + i22;
                }
            }
            result = (result * 31) + this.macroReference;
            if (this.templateToken == null) {
                result *= 31;
            } else {
                for (int i222 : this.templateToken) {
                    result = (result * 31) + i222;
                }
            }
            return (((((result * 31) + this.macroNameReference) * 31) + this.tagReference) * 31) + (this.unknownFieldData == null ? 0 : this.unknownFieldData.hashCode());
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.listItem != null) {
                for (int element : this.listItem) {
                    output.writeInt32(1, element);
                }
            }
            if (this.mapKey != null) {
                for (int element2 : this.mapKey) {
                    output.writeInt32(2, element2);
                }
            }
            if (this.mapValue != null) {
                for (int element22 : this.mapValue) {
                    output.writeInt32(3, element22);
                }
            }
            if (this.macroReference != 0) {
                output.writeInt32(4, this.macroReference);
            }
            if (this.templateToken != null) {
                for (int element222 : this.templateToken) {
                    output.writeInt32(5, element222);
                }
            }
            if (this.macroNameReference != 0) {
                output.writeInt32(6, this.macroNameReference);
            }
            if (this.tagReference != 0) {
                output.writeInt32(7, this.tagReference);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int dataSize;
            int size = 0;
            if (this.listItem != null && this.listItem.length > 0) {
                dataSize = 0;
                for (int element : this.listItem) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element);
                }
                size = (0 + dataSize) + (this.listItem.length * 1);
            }
            if (this.mapKey != null && this.mapKey.length > 0) {
                dataSize = 0;
                for (int element2 : this.mapKey) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element2);
                }
                size = (size + dataSize) + (this.mapKey.length * 1);
            }
            if (this.mapValue != null && this.mapValue.length > 0) {
                dataSize = 0;
                for (int element22 : this.mapValue) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element22);
                }
                size = (size + dataSize) + (this.mapValue.length * 1);
            }
            if (this.macroReference != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, this.macroReference);
            }
            if (this.templateToken != null && this.templateToken.length > 0) {
                dataSize = 0;
                for (int element222 : this.templateToken) {
                    dataSize += CodedOutputByteBufferNano.computeInt32SizeNoTag(element222);
                }
                size = (size + dataSize) + (this.templateToken.length * 1);
            }
            if (this.macroNameReference != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(6, this.macroNameReference);
            }
            if (this.tagReference != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(7, this.tagReference);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public ServingValue mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                int arrayLength;
                int i;
                int[] newArray;
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 8);
                        i = this.listItem.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.listItem, 0, newArray, 0, i);
                        this.listItem = newArray;
                        while (i < this.listItem.length - 1) {
                            this.listItem[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.listItem[i] = input.readInt32();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 16);
                        i = this.mapKey.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.mapKey, 0, newArray, 0, i);
                        this.mapKey = newArray;
                        while (i < this.mapKey.length - 1) {
                            this.mapKey[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.mapKey[i] = input.readInt32();
                        continue;
                    case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 24);
                        i = this.mapValue.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.mapValue, 0, newArray, 0, i);
                        this.mapValue = newArray;
                        while (i < this.mapValue.length - 1) {
                            this.mapValue[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.mapValue[i] = input.readInt32();
                        continue;
                    case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                        this.macroReference = input.readInt32();
                        continue;
                    case HeaderMapDB.COMPARATOR_CHAR_ARRAY /*40*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 40);
                        i = this.templateToken.length;
                        newArray = new int[(i + arrayLength)];
                        System.arraycopy(this.templateToken, 0, newArray, 0, i);
                        this.templateToken = newArray;
                        while (i < this.templateToken.length - 1) {
                            this.templateToken[i] = input.readInt32();
                            input.readTag();
                            i++;
                        }
                        this.templateToken[i] = input.readInt32();
                        continue;
                    case HeaderMapDB.B_TREE_COMPRESSION_SERIALIZER /*48*/:
                        this.macroNameReference = input.readInt32();
                        continue;
                    case HeaderMapDB.SERIALIZER_KEY_TUPLE6 /*56*/:
                        this.tagReference = input.readInt32();
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

        public static ServingValue parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ServingValue) MessageNano.mergeFrom(new ServingValue(), data);
        }

        public static ServingValue parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ServingValue().mergeFrom(input);
        }
    }

    public static final class Supplemental extends ExtendableMessageNano {
        public static final Supplemental[] EMPTY_ARRAY;
        public GaExperimentSupplemental experimentSupplemental;
        public String name;
        public Value value;

        static {
            EMPTY_ARRAY = new Supplemental[0];
        }

        public Supplemental() {
            this.name = Table.STRING_DEFAULT_VALUE;
            this.value = null;
            this.experimentSupplemental = null;
        }

        public final Supplemental clear() {
            this.name = Table.STRING_DEFAULT_VALUE;
            this.value = null;
            this.experimentSupplemental = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean equals(java.lang.Object r6) {
            /*
            r5 = this;
            r1 = 1;
            r2 = 0;
            if (r6 != r5) goto L_0x0005;
        L_0x0004:
            return r1;
        L_0x0005:
            r3 = r6 instanceof com.google.analytics.containertag.proto.Serving.Supplemental;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Serving.Supplemental) r0;
            r3 = r5.name;
            if (r3 != 0) goto L_0x0030;
        L_0x0012:
            r3 = r0.name;
            if (r3 != 0) goto L_0x002e;
        L_0x0016:
            r3 = r5.value;
            if (r3 != 0) goto L_0x003b;
        L_0x001a:
            r3 = r0.value;
            if (r3 != 0) goto L_0x002e;
        L_0x001e:
            r3 = r5.experimentSupplemental;
            if (r3 != 0) goto L_0x0046;
        L_0x0022:
            r3 = r0.experimentSupplemental;
            if (r3 != 0) goto L_0x002e;
        L_0x0026:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x0051;
        L_0x002a:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x002e:
            r1 = r2;
            goto L_0x0004;
        L_0x0030:
            r3 = r5.name;
            r4 = r0.name;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x002e;
        L_0x003a:
            goto L_0x0016;
        L_0x003b:
            r3 = r5.value;
            r4 = r0.value;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x002e;
        L_0x0045:
            goto L_0x001e;
        L_0x0046:
            r3 = r5.experimentSupplemental;
            r4 = r0.experimentSupplemental;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x002e;
        L_0x0050:
            goto L_0x0026;
        L_0x0051:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x002e;
        L_0x005b:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Serving.Supplemental.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((this.name == null ? 0 : this.name.hashCode()) + 527) * 31) + (this.value == null ? 0 : this.value.hashCode())) * 31) + (this.experimentSupplemental == null ? 0 : this.experimentSupplemental.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.name.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(1, this.name);
            }
            if (this.value != null) {
                output.writeMessage(2, this.value);
            }
            if (this.experimentSupplemental != null) {
                output.writeMessage(3, this.experimentSupplemental);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (!this.name.equals(Table.STRING_DEFAULT_VALUE)) {
                size = 0 + CodedOutputByteBufferNano.computeStringSize(1, this.name);
            }
            if (this.value != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(2, this.value);
            }
            if (this.experimentSupplemental != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(3, this.experimentSupplemental);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public Supplemental mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        this.name = input.readString();
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        this.value = new Value();
                        input.readMessage(this.value);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        this.experimentSupplemental = new GaExperimentSupplemental();
                        input.readMessage(this.experimentSupplemental);
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

        public static Supplemental parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Supplemental) MessageNano.mergeFrom(new Supplemental(), data);
        }

        public static Supplemental parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Supplemental().mergeFrom(input);
        }
    }

    public static final class SupplementedResource extends ExtendableMessageNano {
        public static final SupplementedResource[] EMPTY_ARRAY;
        public String fingerprint;
        public Resource resource;
        public Supplemental[] supplemental;

        static {
            EMPTY_ARRAY = new SupplementedResource[0];
        }

        public SupplementedResource() {
            this.supplemental = Supplemental.EMPTY_ARRAY;
            this.resource = null;
            this.fingerprint = Table.STRING_DEFAULT_VALUE;
        }

        public final SupplementedResource clear() {
            this.supplemental = Supplemental.EMPTY_ARRAY;
            this.resource = null;
            this.fingerprint = Table.STRING_DEFAULT_VALUE;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean equals(java.lang.Object r6) {
            /*
            r5 = this;
            r1 = 1;
            r2 = 0;
            if (r6 != r5) goto L_0x0005;
        L_0x0004:
            return r1;
        L_0x0005:
            r3 = r6 instanceof com.google.analytics.containertag.proto.Serving.SupplementedResource;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Serving.SupplementedResource) r0;
            r3 = r5.supplemental;
            r4 = r0.supplemental;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0030;
        L_0x0018:
            r3 = r5.resource;
            if (r3 != 0) goto L_0x0032;
        L_0x001c:
            r3 = r0.resource;
            if (r3 != 0) goto L_0x0030;
        L_0x0020:
            r3 = r5.fingerprint;
            if (r3 != 0) goto L_0x003d;
        L_0x0024:
            r3 = r0.fingerprint;
            if (r3 != 0) goto L_0x0030;
        L_0x0028:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x0048;
        L_0x002c:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x0030:
            r1 = r2;
            goto L_0x0004;
        L_0x0032:
            r3 = r5.resource;
            r4 = r0.resource;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0030;
        L_0x003c:
            goto L_0x0020;
        L_0x003d:
            r3 = r5.fingerprint;
            r4 = r0.fingerprint;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0030;
        L_0x0047:
            goto L_0x0028;
        L_0x0048:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0030;
        L_0x0052:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Serving.SupplementedResource.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int result = 17;
            if (this.supplemental == null) {
                result = 17 * 31;
            } else {
                for (int i2 = 0; i2 < this.supplemental.length; i2++) {
                    result = (result * 31) + (this.supplemental[i2] == null ? 0 : this.supplemental[i2].hashCode());
                }
            }
            int hashCode = ((((result * 31) + (this.resource == null ? 0 : this.resource.hashCode())) * 31) + (this.fingerprint == null ? 0 : this.fingerprint.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.supplemental != null) {
                for (Supplemental element : this.supplemental) {
                    output.writeMessage(1, element);
                }
            }
            if (this.resource != null) {
                output.writeMessage(2, this.resource);
            }
            if (!this.fingerprint.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(3, this.fingerprint);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.supplemental != null) {
                for (Supplemental element : this.supplemental) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
            }
            if (this.resource != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(2, this.resource);
            }
            if (!this.fingerprint.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(3, this.fingerprint);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public SupplementedResource mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        int i;
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.supplemental == null) {
                            i = 0;
                        } else {
                            i = this.supplemental.length;
                        }
                        Supplemental[] newArray = new Supplemental[(i + arrayLength)];
                        if (this.supplemental != null) {
                            System.arraycopy(this.supplemental, 0, newArray, 0, i);
                        }
                        this.supplemental = newArray;
                        while (i < this.supplemental.length - 1) {
                            this.supplemental[i] = new Supplemental();
                            input.readMessage(this.supplemental[i]);
                            input.readTag();
                            i++;
                        }
                        this.supplemental[i] = new Supplemental();
                        input.readMessage(this.supplemental[i]);
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        this.resource = new Resource();
                        input.readMessage(this.resource);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        this.fingerprint = input.readString();
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

        public static SupplementedResource parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (SupplementedResource) MessageNano.mergeFrom(new SupplementedResource(), data);
        }

        public static SupplementedResource parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new SupplementedResource().mergeFrom(input);
        }
    }
}
