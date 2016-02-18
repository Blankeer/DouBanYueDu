package com.google.analytics.containertag.proto;

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

public interface Debug {

    public static final class DataLayerEventEvaluationInfo extends ExtendableMessageNano {
        public static final DataLayerEventEvaluationInfo[] EMPTY_ARRAY;
        public ResolvedFunctionCall[] results;
        public RuleEvaluationStepInfo rulesEvaluation;

        static {
            EMPTY_ARRAY = new DataLayerEventEvaluationInfo[0];
        }

        public DataLayerEventEvaluationInfo() {
            this.rulesEvaluation = null;
            this.results = ResolvedFunctionCall.EMPTY_ARRAY;
        }

        public final DataLayerEventEvaluationInfo clear() {
            this.rulesEvaluation = null;
            this.results = ResolvedFunctionCall.EMPTY_ARRAY;
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
            r3 = r6 instanceof com.google.analytics.containertag.proto.Debug.DataLayerEventEvaluationInfo;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Debug.DataLayerEventEvaluationInfo) r0;
            r3 = r5.rulesEvaluation;
            if (r3 != 0) goto L_0x002a;
        L_0x0012:
            r3 = r0.rulesEvaluation;
            if (r3 != 0) goto L_0x0028;
        L_0x0016:
            r3 = r5.results;
            r4 = r0.results;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0028;
        L_0x0020:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x0035;
        L_0x0024:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x0028:
            r1 = r2;
            goto L_0x0004;
        L_0x002a:
            r3 = r5.rulesEvaluation;
            r4 = r0.rulesEvaluation;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0028;
        L_0x0034:
            goto L_0x0016;
        L_0x0035:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0028;
        L_0x003f:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Debug.DataLayerEventEvaluationInfo.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int result = (this.rulesEvaluation == null ? 0 : this.rulesEvaluation.hashCode()) + 527;
            if (this.results == null) {
                result *= 31;
            } else {
                for (int i2 = 0; i2 < this.results.length; i2++) {
                    result = (result * 31) + (this.results[i2] == null ? 0 : this.results[i2].hashCode());
                }
            }
            int i3 = result * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return i3 + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.rulesEvaluation != null) {
                output.writeMessage(1, this.rulesEvaluation);
            }
            if (this.results != null) {
                for (ResolvedFunctionCall element : this.results) {
                    output.writeMessage(2, element);
                }
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.rulesEvaluation != null) {
                size = 0 + CodedOutputByteBufferNano.computeMessageSize(1, this.rulesEvaluation);
            }
            if (this.results != null) {
                for (ResolvedFunctionCall element : this.results) {
                    size += CodedOutputByteBufferNano.computeMessageSize(2, element);
                }
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public DataLayerEventEvaluationInfo mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        this.rulesEvaluation = new RuleEvaluationStepInfo();
                        input.readMessage(this.rulesEvaluation);
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        int i;
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                        if (this.results == null) {
                            i = 0;
                        } else {
                            i = this.results.length;
                        }
                        ResolvedFunctionCall[] newArray = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.results != null) {
                            System.arraycopy(this.results, 0, newArray, 0, i);
                        }
                        this.results = newArray;
                        while (i < this.results.length - 1) {
                            this.results[i] = new ResolvedFunctionCall();
                            input.readMessage(this.results[i]);
                            input.readTag();
                            i++;
                        }
                        this.results[i] = new ResolvedFunctionCall();
                        input.readMessage(this.results[i]);
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

        public static DataLayerEventEvaluationInfo parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (DataLayerEventEvaluationInfo) MessageNano.mergeFrom(new DataLayerEventEvaluationInfo(), data);
        }

        public static DataLayerEventEvaluationInfo parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new DataLayerEventEvaluationInfo().mergeFrom(input);
        }
    }

    public static final class DebugEvents extends ExtendableMessageNano {
        public static final DebugEvents[] EMPTY_ARRAY;
        public EventInfo[] event;

        static {
            EMPTY_ARRAY = new DebugEvents[0];
        }

        public DebugEvents() {
            this.event = EventInfo.EMPTY_ARRAY;
        }

        public final DebugEvents clear() {
            this.event = EventInfo.EMPTY_ARRAY;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof DebugEvents)) {
                return false;
            }
            DebugEvents other = (DebugEvents) o;
            if (Arrays.equals(this.event, other.event)) {
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
            int i = 0;
            int result = 17;
            if (this.event == null) {
                result = 17 * 31;
            } else {
                for (int i2 = 0; i2 < this.event.length; i2++) {
                    result = (result * 31) + (this.event[i2] == null ? 0 : this.event[i2].hashCode());
                }
            }
            int i3 = result * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return i3 + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.event != null) {
                for (EventInfo element : this.event) {
                    output.writeMessage(1, element);
                }
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.event != null) {
                for (EventInfo element : this.event) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public DebugEvents mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        int i;
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.event == null) {
                            i = 0;
                        } else {
                            i = this.event.length;
                        }
                        EventInfo[] newArray = new EventInfo[(i + arrayLength)];
                        if (this.event != null) {
                            System.arraycopy(this.event, 0, newArray, 0, i);
                        }
                        this.event = newArray;
                        while (i < this.event.length - 1) {
                            this.event[i] = new EventInfo();
                            input.readMessage(this.event[i]);
                            input.readTag();
                            i++;
                        }
                        this.event[i] = new EventInfo();
                        input.readMessage(this.event[i]);
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

        public static DebugEvents parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (DebugEvents) MessageNano.mergeFrom(new DebugEvents(), data);
        }

        public static DebugEvents parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new DebugEvents().mergeFrom(input);
        }
    }

    public static final class EventInfo extends ExtendableMessageNano {
        public static final EventInfo[] EMPTY_ARRAY;
        public String containerId;
        public String containerVersion;
        public DataLayerEventEvaluationInfo dataLayerEventResult;
        public int eventType;
        public String key;
        public MacroEvaluationInfo macroResult;

        public interface EventType {
            public static final int DATA_LAYER_EVENT = 1;
            public static final int MACRO_REFERENCE = 2;
        }

        static {
            EMPTY_ARRAY = new EventInfo[0];
        }

        public EventInfo() {
            this.eventType = 1;
            this.containerVersion = Table.STRING_DEFAULT_VALUE;
            this.containerId = Table.STRING_DEFAULT_VALUE;
            this.key = Table.STRING_DEFAULT_VALUE;
            this.macroResult = null;
            this.dataLayerEventResult = null;
        }

        public final EventInfo clear() {
            this.eventType = 1;
            this.containerVersion = Table.STRING_DEFAULT_VALUE;
            this.containerId = Table.STRING_DEFAULT_VALUE;
            this.key = Table.STRING_DEFAULT_VALUE;
            this.macroResult = null;
            this.dataLayerEventResult = null;
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
            r3 = r6 instanceof com.google.analytics.containertag.proto.Debug.EventInfo;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Debug.EventInfo) r0;
            r3 = r5.eventType;
            r4 = r0.eventType;
            if (r3 != r4) goto L_0x0044;
        L_0x0014:
            r3 = r5.containerVersion;
            if (r3 != 0) goto L_0x0046;
        L_0x0018:
            r3 = r0.containerVersion;
            if (r3 != 0) goto L_0x0044;
        L_0x001c:
            r3 = r5.containerId;
            if (r3 != 0) goto L_0x0051;
        L_0x0020:
            r3 = r0.containerId;
            if (r3 != 0) goto L_0x0044;
        L_0x0024:
            r3 = r5.key;
            if (r3 != 0) goto L_0x005c;
        L_0x0028:
            r3 = r0.key;
            if (r3 != 0) goto L_0x0044;
        L_0x002c:
            r3 = r5.macroResult;
            if (r3 != 0) goto L_0x0067;
        L_0x0030:
            r3 = r0.macroResult;
            if (r3 != 0) goto L_0x0044;
        L_0x0034:
            r3 = r5.dataLayerEventResult;
            if (r3 != 0) goto L_0x0072;
        L_0x0038:
            r3 = r0.dataLayerEventResult;
            if (r3 != 0) goto L_0x0044;
        L_0x003c:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x007d;
        L_0x0040:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x0044:
            r1 = r2;
            goto L_0x0004;
        L_0x0046:
            r3 = r5.containerVersion;
            r4 = r0.containerVersion;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0044;
        L_0x0050:
            goto L_0x001c;
        L_0x0051:
            r3 = r5.containerId;
            r4 = r0.containerId;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0044;
        L_0x005b:
            goto L_0x0024;
        L_0x005c:
            r3 = r5.key;
            r4 = r0.key;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0044;
        L_0x0066:
            goto L_0x002c;
        L_0x0067:
            r3 = r5.macroResult;
            r4 = r0.macroResult;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0044;
        L_0x0071:
            goto L_0x0034;
        L_0x0072:
            r3 = r5.dataLayerEventResult;
            r4 = r0.dataLayerEventResult;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0044;
        L_0x007c:
            goto L_0x003c;
        L_0x007d:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0044;
        L_0x0087:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Debug.EventInfo.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int hashCode = (((((((((((this.eventType + 527) * 31) + (this.containerVersion == null ? 0 : this.containerVersion.hashCode())) * 31) + (this.containerId == null ? 0 : this.containerId.hashCode())) * 31) + (this.key == null ? 0 : this.key.hashCode())) * 31) + (this.macroResult == null ? 0 : this.macroResult.hashCode())) * 31) + (this.dataLayerEventResult == null ? 0 : this.dataLayerEventResult.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.eventType != 1) {
                output.writeInt32(1, this.eventType);
            }
            if (!this.containerVersion.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(2, this.containerVersion);
            }
            if (!this.containerId.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(3, this.containerId);
            }
            if (!this.key.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(4, this.key);
            }
            if (this.macroResult != null) {
                output.writeMessage(6, this.macroResult);
            }
            if (this.dataLayerEventResult != null) {
                output.writeMessage(7, this.dataLayerEventResult);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.eventType != 1) {
                size = 0 + CodedOutputByteBufferNano.computeInt32Size(1, this.eventType);
            }
            if (!this.containerVersion.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(2, this.containerVersion);
            }
            if (!this.containerId.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(3, this.containerId);
            }
            if (!this.key.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(4, this.key);
            }
            if (this.macroResult != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(6, this.macroResult);
            }
            if (this.dataLayerEventResult != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(7, this.dataLayerEventResult);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public EventInfo mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        int temp = input.readInt32();
                        if (temp != 1 && temp != 2) {
                            this.eventType = 1;
                            break;
                        }
                        this.eventType = temp;
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        this.containerVersion = input.readString();
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        this.containerId = input.readString();
                        continue;
                    case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                        this.key = input.readString();
                        continue;
                    case HeaderMapDB.FUN_EMPTY_ITERATOR /*50*/:
                        this.macroResult = new MacroEvaluationInfo();
                        input.readMessage(this.macroResult);
                        continue;
                    case Header.LONG_10 /*58*/:
                        this.dataLayerEventResult = new DataLayerEventEvaluationInfo();
                        input.readMessage(this.dataLayerEventResult);
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

        public static EventInfo parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (EventInfo) MessageNano.mergeFrom(new EventInfo(), data);
        }

        public static EventInfo parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new EventInfo().mergeFrom(input);
        }
    }

    public static final class MacroEvaluationInfo extends ExtendableMessageNano {
        public static final MacroEvaluationInfo[] EMPTY_ARRAY;
        public static final Extension<MacroEvaluationInfo> macro;
        public ResolvedFunctionCall result;
        public RuleEvaluationStepInfo rulesEvaluation;

        static {
            EMPTY_ARRAY = new MacroEvaluationInfo[0];
            macro = Extension.create(47497405, new TypeLiteral<MacroEvaluationInfo>() {
            });
        }

        public MacroEvaluationInfo() {
            this.rulesEvaluation = null;
            this.result = null;
        }

        public final MacroEvaluationInfo clear() {
            this.rulesEvaluation = null;
            this.result = null;
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
            r3 = r6 instanceof com.google.analytics.containertag.proto.Debug.MacroEvaluationInfo;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Debug.MacroEvaluationInfo) r0;
            r3 = r5.rulesEvaluation;
            if (r3 != 0) goto L_0x0028;
        L_0x0012:
            r3 = r0.rulesEvaluation;
            if (r3 != 0) goto L_0x0026;
        L_0x0016:
            r3 = r5.result;
            if (r3 != 0) goto L_0x0033;
        L_0x001a:
            r3 = r0.result;
            if (r3 != 0) goto L_0x0026;
        L_0x001e:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x003e;
        L_0x0022:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x0026:
            r1 = r2;
            goto L_0x0004;
        L_0x0028:
            r3 = r5.rulesEvaluation;
            r4 = r0.rulesEvaluation;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0026;
        L_0x0032:
            goto L_0x0016;
        L_0x0033:
            r3 = r5.result;
            r4 = r0.result;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0026;
        L_0x003d:
            goto L_0x001e;
        L_0x003e:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0026;
        L_0x0048:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Debug.MacroEvaluationInfo.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((this.rulesEvaluation == null ? 0 : this.rulesEvaluation.hashCode()) + 527) * 31) + (this.result == null ? 0 : this.result.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.rulesEvaluation != null) {
                output.writeMessage(1, this.rulesEvaluation);
            }
            if (this.result != null) {
                output.writeMessage(3, this.result);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.rulesEvaluation != null) {
                size = 0 + CodedOutputByteBufferNano.computeMessageSize(1, this.rulesEvaluation);
            }
            if (this.result != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(3, this.result);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public MacroEvaluationInfo mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        this.rulesEvaluation = new RuleEvaluationStepInfo();
                        input.readMessage(this.rulesEvaluation);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        this.result = new ResolvedFunctionCall();
                        input.readMessage(this.result);
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

        public static MacroEvaluationInfo parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (MacroEvaluationInfo) MessageNano.mergeFrom(new MacroEvaluationInfo(), data);
        }

        public static MacroEvaluationInfo parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new MacroEvaluationInfo().mergeFrom(input);
        }
    }

    public static final class ResolvedFunctionCall extends ExtendableMessageNano {
        public static final ResolvedFunctionCall[] EMPTY_ARRAY;
        public String associatedRuleName;
        public ResolvedProperty[] properties;
        public Value result;

        static {
            EMPTY_ARRAY = new ResolvedFunctionCall[0];
        }

        public ResolvedFunctionCall() {
            this.properties = ResolvedProperty.EMPTY_ARRAY;
            this.result = null;
            this.associatedRuleName = Table.STRING_DEFAULT_VALUE;
        }

        public final ResolvedFunctionCall clear() {
            this.properties = ResolvedProperty.EMPTY_ARRAY;
            this.result = null;
            this.associatedRuleName = Table.STRING_DEFAULT_VALUE;
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
            r3 = r6 instanceof com.google.analytics.containertag.proto.Debug.ResolvedFunctionCall;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Debug.ResolvedFunctionCall) r0;
            r3 = r5.properties;
            r4 = r0.properties;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x0030;
        L_0x0018:
            r3 = r5.result;
            if (r3 != 0) goto L_0x0032;
        L_0x001c:
            r3 = r0.result;
            if (r3 != 0) goto L_0x0030;
        L_0x0020:
            r3 = r5.associatedRuleName;
            if (r3 != 0) goto L_0x003d;
        L_0x0024:
            r3 = r0.associatedRuleName;
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
            r3 = r5.result;
            r4 = r0.result;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0030;
        L_0x003c:
            goto L_0x0020;
        L_0x003d:
            r3 = r5.associatedRuleName;
            r4 = r0.associatedRuleName;
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
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Debug.ResolvedFunctionCall.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int result = 17;
            if (this.properties == null) {
                result = 17 * 31;
            } else {
                for (int i2 = 0; i2 < this.properties.length; i2++) {
                    result = (result * 31) + (this.properties[i2] == null ? 0 : this.properties[i2].hashCode());
                }
            }
            int hashCode = ((((result * 31) + (this.result == null ? 0 : this.result.hashCode())) * 31) + (this.associatedRuleName == null ? 0 : this.associatedRuleName.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.properties != null) {
                for (ResolvedProperty element : this.properties) {
                    output.writeMessage(1, element);
                }
            }
            if (this.result != null) {
                output.writeMessage(2, this.result);
            }
            if (!this.associatedRuleName.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(3, this.associatedRuleName);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.properties != null) {
                for (ResolvedProperty element : this.properties) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
            }
            if (this.result != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(2, this.result);
            }
            if (!this.associatedRuleName.equals(Table.STRING_DEFAULT_VALUE)) {
                size += CodedOutputByteBufferNano.computeStringSize(3, this.associatedRuleName);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public ResolvedFunctionCall mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        int i;
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.properties == null) {
                            i = 0;
                        } else {
                            i = this.properties.length;
                        }
                        ResolvedProperty[] newArray = new ResolvedProperty[(i + arrayLength)];
                        if (this.properties != null) {
                            System.arraycopy(this.properties, 0, newArray, 0, i);
                        }
                        this.properties = newArray;
                        while (i < this.properties.length - 1) {
                            this.properties[i] = new ResolvedProperty();
                            input.readMessage(this.properties[i]);
                            input.readTag();
                            i++;
                        }
                        this.properties[i] = new ResolvedProperty();
                        input.readMessage(this.properties[i]);
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        this.result = new Value();
                        input.readMessage(this.result);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        this.associatedRuleName = input.readString();
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

        public static ResolvedFunctionCall parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ResolvedFunctionCall) MessageNano.mergeFrom(new ResolvedFunctionCall(), data);
        }

        public static ResolvedFunctionCall parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ResolvedFunctionCall().mergeFrom(input);
        }
    }

    public static final class ResolvedProperty extends ExtendableMessageNano {
        public static final ResolvedProperty[] EMPTY_ARRAY;
        public String key;
        public Value value;

        static {
            EMPTY_ARRAY = new ResolvedProperty[0];
        }

        public ResolvedProperty() {
            this.key = Table.STRING_DEFAULT_VALUE;
            this.value = null;
        }

        public final ResolvedProperty clear() {
            this.key = Table.STRING_DEFAULT_VALUE;
            this.value = null;
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
            r3 = r6 instanceof com.google.analytics.containertag.proto.Debug.ResolvedProperty;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Debug.ResolvedProperty) r0;
            r3 = r5.key;
            if (r3 != 0) goto L_0x0028;
        L_0x0012:
            r3 = r0.key;
            if (r3 != 0) goto L_0x0026;
        L_0x0016:
            r3 = r5.value;
            if (r3 != 0) goto L_0x0033;
        L_0x001a:
            r3 = r0.value;
            if (r3 != 0) goto L_0x0026;
        L_0x001e:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x003e;
        L_0x0022:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x0026:
            r1 = r2;
            goto L_0x0004;
        L_0x0028:
            r3 = r5.key;
            r4 = r0.key;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0026;
        L_0x0032:
            goto L_0x0016;
        L_0x0033:
            r3 = r5.value;
            r4 = r0.value;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0026;
        L_0x003d:
            goto L_0x001e;
        L_0x003e:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x0026;
        L_0x0048:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Debug.ResolvedProperty.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((this.key == null ? 0 : this.key.hashCode()) + 527) * 31) + (this.value == null ? 0 : this.value.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.key.equals(Table.STRING_DEFAULT_VALUE)) {
                output.writeString(1, this.key);
            }
            if (this.value != null) {
                output.writeMessage(2, this.value);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (!this.key.equals(Table.STRING_DEFAULT_VALUE)) {
                size = 0 + CodedOutputByteBufferNano.computeStringSize(1, this.key);
            }
            if (this.value != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(2, this.value);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public ResolvedProperty mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        this.key = input.readString();
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        this.value = new Value();
                        input.readMessage(this.value);
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

        public static ResolvedProperty parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ResolvedProperty) MessageNano.mergeFrom(new ResolvedProperty(), data);
        }

        public static ResolvedProperty parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ResolvedProperty().mergeFrom(input);
        }
    }

    public static final class ResolvedRule extends ExtendableMessageNano {
        public static final ResolvedRule[] EMPTY_ARRAY;
        public ResolvedFunctionCall[] addMacros;
        public ResolvedFunctionCall[] addTags;
        public ResolvedFunctionCall[] negativePredicates;
        public ResolvedFunctionCall[] positivePredicates;
        public ResolvedFunctionCall[] removeMacros;
        public ResolvedFunctionCall[] removeTags;
        public Value result;

        static {
            EMPTY_ARRAY = new ResolvedRule[0];
        }

        public ResolvedRule() {
            this.positivePredicates = ResolvedFunctionCall.EMPTY_ARRAY;
            this.negativePredicates = ResolvedFunctionCall.EMPTY_ARRAY;
            this.addTags = ResolvedFunctionCall.EMPTY_ARRAY;
            this.removeTags = ResolvedFunctionCall.EMPTY_ARRAY;
            this.addMacros = ResolvedFunctionCall.EMPTY_ARRAY;
            this.removeMacros = ResolvedFunctionCall.EMPTY_ARRAY;
            this.result = null;
        }

        public final ResolvedRule clear() {
            this.positivePredicates = ResolvedFunctionCall.EMPTY_ARRAY;
            this.negativePredicates = ResolvedFunctionCall.EMPTY_ARRAY;
            this.addTags = ResolvedFunctionCall.EMPTY_ARRAY;
            this.removeTags = ResolvedFunctionCall.EMPTY_ARRAY;
            this.addMacros = ResolvedFunctionCall.EMPTY_ARRAY;
            this.removeMacros = ResolvedFunctionCall.EMPTY_ARRAY;
            this.result = null;
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
            r3 = r6 instanceof com.google.analytics.containertag.proto.Debug.ResolvedRule;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r6;
            r0 = (com.google.analytics.containertag.proto.Debug.ResolvedRule) r0;
            r3 = r5.positivePredicates;
            r4 = r0.positivePredicates;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x005a;
        L_0x0018:
            r3 = r5.negativePredicates;
            r4 = r0.negativePredicates;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x005a;
        L_0x0022:
            r3 = r5.addTags;
            r4 = r0.addTags;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x005a;
        L_0x002c:
            r3 = r5.removeTags;
            r4 = r0.removeTags;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x005a;
        L_0x0036:
            r3 = r5.addMacros;
            r4 = r0.addMacros;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x005a;
        L_0x0040:
            r3 = r5.removeMacros;
            r4 = r0.removeMacros;
            r3 = java.util.Arrays.equals(r3, r4);
            if (r3 == 0) goto L_0x005a;
        L_0x004a:
            r3 = r5.result;
            if (r3 != 0) goto L_0x005c;
        L_0x004e:
            r3 = r0.result;
            if (r3 != 0) goto L_0x005a;
        L_0x0052:
            r3 = r5.unknownFieldData;
            if (r3 != 0) goto L_0x0067;
        L_0x0056:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x005a:
            r1 = r2;
            goto L_0x0004;
        L_0x005c:
            r3 = r5.result;
            r4 = r0.result;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x005a;
        L_0x0066:
            goto L_0x0052;
        L_0x0067:
            r3 = r5.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x005a;
        L_0x0071:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.containertag.proto.Debug.ResolvedRule.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i;
            int i2 = 0;
            int result = 17;
            if (this.positivePredicates == null) {
                result = 17 * 31;
            } else {
                for (i = 0; i < this.positivePredicates.length; i++) {
                    result = (result * 31) + (this.positivePredicates[i] == null ? 0 : this.positivePredicates[i].hashCode());
                }
            }
            if (this.negativePredicates == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.negativePredicates.length; i++) {
                    result = (result * 31) + (this.negativePredicates[i] == null ? 0 : this.negativePredicates[i].hashCode());
                }
            }
            if (this.addTags == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.addTags.length; i++) {
                    result = (result * 31) + (this.addTags[i] == null ? 0 : this.addTags[i].hashCode());
                }
            }
            if (this.removeTags == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.removeTags.length; i++) {
                    result = (result * 31) + (this.removeTags[i] == null ? 0 : this.removeTags[i].hashCode());
                }
            }
            if (this.addMacros == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.addMacros.length; i++) {
                    result = (result * 31) + (this.addMacros[i] == null ? 0 : this.addMacros[i].hashCode());
                }
            }
            if (this.removeMacros == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.removeMacros.length; i++) {
                    result = (result * 31) + (this.removeMacros[i] == null ? 0 : this.removeMacros[i].hashCode());
                }
            }
            int hashCode = ((result * 31) + (this.result == null ? 0 : this.result.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i2 = this.unknownFieldData.hashCode();
            }
            return hashCode + i2;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.positivePredicates != null) {
                for (ResolvedFunctionCall element : this.positivePredicates) {
                    output.writeMessage(1, element);
                }
            }
            if (this.negativePredicates != null) {
                for (ResolvedFunctionCall element2 : this.negativePredicates) {
                    output.writeMessage(2, element2);
                }
            }
            if (this.addTags != null) {
                for (ResolvedFunctionCall element22 : this.addTags) {
                    output.writeMessage(3, element22);
                }
            }
            if (this.removeTags != null) {
                for (ResolvedFunctionCall element222 : this.removeTags) {
                    output.writeMessage(4, element222);
                }
            }
            if (this.addMacros != null) {
                for (ResolvedFunctionCall element2222 : this.addMacros) {
                    output.writeMessage(5, element2222);
                }
            }
            if (this.removeMacros != null) {
                for (ResolvedFunctionCall element22222 : this.removeMacros) {
                    output.writeMessage(6, element22222);
                }
            }
            if (this.result != null) {
                output.writeMessage(7, this.result);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.positivePredicates != null) {
                for (ResolvedFunctionCall element : this.positivePredicates) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
            }
            if (this.negativePredicates != null) {
                for (ResolvedFunctionCall element2 : this.negativePredicates) {
                    size += CodedOutputByteBufferNano.computeMessageSize(2, element2);
                }
            }
            if (this.addTags != null) {
                for (ResolvedFunctionCall element22 : this.addTags) {
                    size += CodedOutputByteBufferNano.computeMessageSize(3, element22);
                }
            }
            if (this.removeTags != null) {
                for (ResolvedFunctionCall element222 : this.removeTags) {
                    size += CodedOutputByteBufferNano.computeMessageSize(4, element222);
                }
            }
            if (this.addMacros != null) {
                for (ResolvedFunctionCall element2222 : this.addMacros) {
                    size += CodedOutputByteBufferNano.computeMessageSize(5, element2222);
                }
            }
            if (this.removeMacros != null) {
                for (ResolvedFunctionCall element22222 : this.removeMacros) {
                    size += CodedOutputByteBufferNano.computeMessageSize(6, element22222);
                }
            }
            if (this.result != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(7, this.result);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public ResolvedRule mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                int arrayLength;
                int i;
                ResolvedFunctionCall[] newArray;
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.positivePredicates == null) {
                            i = 0;
                        } else {
                            i = this.positivePredicates.length;
                        }
                        newArray = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.positivePredicates != null) {
                            System.arraycopy(this.positivePredicates, 0, newArray, 0, i);
                        }
                        this.positivePredicates = newArray;
                        while (i < this.positivePredicates.length - 1) {
                            this.positivePredicates[i] = new ResolvedFunctionCall();
                            input.readMessage(this.positivePredicates[i]);
                            input.readTag();
                            i++;
                        }
                        this.positivePredicates[i] = new ResolvedFunctionCall();
                        input.readMessage(this.positivePredicates[i]);
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                        if (this.negativePredicates == null) {
                            i = 0;
                        } else {
                            i = this.negativePredicates.length;
                        }
                        newArray = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.negativePredicates != null) {
                            System.arraycopy(this.negativePredicates, 0, newArray, 0, i);
                        }
                        this.negativePredicates = newArray;
                        while (i < this.negativePredicates.length - 1) {
                            this.negativePredicates[i] = new ResolvedFunctionCall();
                            input.readMessage(this.negativePredicates[i]);
                            input.readTag();
                            i++;
                        }
                        this.negativePredicates[i] = new ResolvedFunctionCall();
                        input.readMessage(this.negativePredicates[i]);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 26);
                        if (this.addTags == null) {
                            i = 0;
                        } else {
                            i = this.addTags.length;
                        }
                        newArray = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.addTags != null) {
                            System.arraycopy(this.addTags, 0, newArray, 0, i);
                        }
                        this.addTags = newArray;
                        while (i < this.addTags.length - 1) {
                            this.addTags[i] = new ResolvedFunctionCall();
                            input.readMessage(this.addTags[i]);
                            input.readTag();
                            i++;
                        }
                        this.addTags[i] = new ResolvedFunctionCall();
                        input.readMessage(this.addTags[i]);
                        continue;
                    case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 34);
                        if (this.removeTags == null) {
                            i = 0;
                        } else {
                            i = this.removeTags.length;
                        }
                        newArray = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.removeTags != null) {
                            System.arraycopy(this.removeTags, 0, newArray, 0, i);
                        }
                        this.removeTags = newArray;
                        while (i < this.removeTags.length - 1) {
                            this.removeTags[i] = new ResolvedFunctionCall();
                            input.readMessage(this.removeTags[i]);
                            input.readTag();
                            i++;
                        }
                        this.removeTags[i] = new ResolvedFunctionCall();
                        input.readMessage(this.removeTags[i]);
                        continue;
                    case HeaderMapDB.COMPARATOR_LONG_ARRAY /*42*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 42);
                        if (this.addMacros == null) {
                            i = 0;
                        } else {
                            i = this.addMacros.length;
                        }
                        newArray = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.addMacros != null) {
                            System.arraycopy(this.addMacros, 0, newArray, 0, i);
                        }
                        this.addMacros = newArray;
                        while (i < this.addMacros.length - 1) {
                            this.addMacros[i] = new ResolvedFunctionCall();
                            input.readMessage(this.addMacros[i]);
                            input.readTag();
                            i++;
                        }
                        this.addMacros[i] = new ResolvedFunctionCall();
                        input.readMessage(this.addMacros[i]);
                        continue;
                    case HeaderMapDB.FUN_EMPTY_ITERATOR /*50*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 50);
                        if (this.removeMacros == null) {
                            i = 0;
                        } else {
                            i = this.removeMacros.length;
                        }
                        newArray = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.removeMacros != null) {
                            System.arraycopy(this.removeMacros, 0, newArray, 0, i);
                        }
                        this.removeMacros = newArray;
                        while (i < this.removeMacros.length - 1) {
                            this.removeMacros[i] = new ResolvedFunctionCall();
                            input.readMessage(this.removeMacros[i]);
                            input.readTag();
                            i++;
                        }
                        this.removeMacros[i] = new ResolvedFunctionCall();
                        input.readMessage(this.removeMacros[i]);
                        continue;
                    case Header.LONG_10 /*58*/:
                        this.result = new Value();
                        input.readMessage(this.result);
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

        public static ResolvedRule parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ResolvedRule) MessageNano.mergeFrom(new ResolvedRule(), data);
        }

        public static ResolvedRule parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ResolvedRule().mergeFrom(input);
        }
    }

    public static final class RuleEvaluationStepInfo extends ExtendableMessageNano {
        public static final RuleEvaluationStepInfo[] EMPTY_ARRAY;
        public ResolvedFunctionCall[] enabledFunctions;
        public ResolvedRule[] rules;

        static {
            EMPTY_ARRAY = new RuleEvaluationStepInfo[0];
        }

        public RuleEvaluationStepInfo() {
            this.rules = ResolvedRule.EMPTY_ARRAY;
            this.enabledFunctions = ResolvedFunctionCall.EMPTY_ARRAY;
        }

        public final RuleEvaluationStepInfo clear() {
            this.rules = ResolvedRule.EMPTY_ARRAY;
            this.enabledFunctions = ResolvedFunctionCall.EMPTY_ARRAY;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RuleEvaluationStepInfo)) {
                return false;
            }
            RuleEvaluationStepInfo other = (RuleEvaluationStepInfo) o;
            if (Arrays.equals(this.rules, other.rules) && Arrays.equals(this.enabledFunctions, other.enabledFunctions)) {
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
            if (this.rules == null) {
                result = 17 * 31;
            } else {
                for (i = 0; i < this.rules.length; i++) {
                    result = (result * 31) + (this.rules[i] == null ? 0 : this.rules[i].hashCode());
                }
            }
            if (this.enabledFunctions == null) {
                result *= 31;
            } else {
                for (i = 0; i < this.enabledFunctions.length; i++) {
                    result = (result * 31) + (this.enabledFunctions[i] == null ? 0 : this.enabledFunctions[i].hashCode());
                }
            }
            int i3 = result * 31;
            if (this.unknownFieldData != null) {
                i2 = this.unknownFieldData.hashCode();
            }
            return i3 + i2;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.rules != null) {
                for (ResolvedRule element : this.rules) {
                    output.writeMessage(1, element);
                }
            }
            if (this.enabledFunctions != null) {
                for (ResolvedFunctionCall element2 : this.enabledFunctions) {
                    output.writeMessage(2, element2);
                }
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0;
            if (this.rules != null) {
                for (ResolvedRule element : this.rules) {
                    size += CodedOutputByteBufferNano.computeMessageSize(1, element);
                }
            }
            if (this.enabledFunctions != null) {
                for (ResolvedFunctionCall element2 : this.enabledFunctions) {
                    size += CodedOutputByteBufferNano.computeMessageSize(2, element2);
                }
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public RuleEvaluationStepInfo mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                int arrayLength;
                int i;
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.i /*10*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                        if (this.rules == null) {
                            i = 0;
                        } else {
                            i = this.rules.length;
                        }
                        ResolvedRule[] newArray = new ResolvedRule[(i + arrayLength)];
                        if (this.rules != null) {
                            System.arraycopy(this.rules, 0, newArray, 0, i);
                        }
                        this.rules = newArray;
                        while (i < this.rules.length - 1) {
                            this.rules[i] = new ResolvedRule();
                            input.readMessage(this.rules[i]);
                            input.readTag();
                            i++;
                        }
                        this.rules[i] = new ResolvedRule();
                        input.readMessage(this.rules[i]);
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                        if (this.enabledFunctions == null) {
                            i = 0;
                        } else {
                            i = this.enabledFunctions.length;
                        }
                        ResolvedFunctionCall[] newArray2 = new ResolvedFunctionCall[(i + arrayLength)];
                        if (this.enabledFunctions != null) {
                            System.arraycopy(this.enabledFunctions, 0, newArray2, 0, i);
                        }
                        this.enabledFunctions = newArray2;
                        while (i < this.enabledFunctions.length - 1) {
                            this.enabledFunctions[i] = new ResolvedFunctionCall();
                            input.readMessage(this.enabledFunctions[i]);
                            input.readTag();
                            i++;
                        }
                        this.enabledFunctions[i] = new ResolvedFunctionCall();
                        input.readMessage(this.enabledFunctions[i]);
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

        public static RuleEvaluationStepInfo parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (RuleEvaluationStepInfo) MessageNano.mergeFrom(new RuleEvaluationStepInfo(), data);
        }

        public static RuleEvaluationStepInfo parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new RuleEvaluationStepInfo().mergeFrom(input);
        }
    }
}
