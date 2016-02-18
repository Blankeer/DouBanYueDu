package com.google.tagmanager.proto;

import com.alipay.sdk.protocol.h;
import com.google.analytics.containertag.proto.Serving.SupplementedResource;
import com.google.tagmanager.protobuf.nano.CodedInputByteBufferNano;
import com.google.tagmanager.protobuf.nano.CodedOutputByteBufferNano;
import com.google.tagmanager.protobuf.nano.ExtendableMessageNano;
import com.google.tagmanager.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.tagmanager.protobuf.nano.MessageNano;
import com.google.tagmanager.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.ArrayList;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public interface Resource {

    public static final class ResourceWithMetadata extends ExtendableMessageNano {
        public static final ResourceWithMetadata[] EMPTY_ARRAY;
        public com.google.analytics.containertag.proto.Serving.Resource resource;
        public SupplementedResource supplementedResource;
        public long timeStamp;

        static {
            EMPTY_ARRAY = new ResourceWithMetadata[0];
        }

        public ResourceWithMetadata() {
            this.timeStamp = 0;
            this.resource = null;
            this.supplementedResource = null;
        }

        public final ResourceWithMetadata clear() {
            this.timeStamp = 0;
            this.resource = null;
            this.supplementedResource = null;
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
            r3 = r9 instanceof com.google.tagmanager.proto.Resource.ResourceWithMetadata;
            if (r3 != 0) goto L_0x000b;
        L_0x0009:
            r1 = r2;
            goto L_0x0004;
        L_0x000b:
            r0 = r9;
            r0 = (com.google.tagmanager.proto.Resource.ResourceWithMetadata) r0;
            r4 = r8.timeStamp;
            r6 = r0.timeStamp;
            r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r3 != 0) goto L_0x002e;
        L_0x0016:
            r3 = r8.resource;
            if (r3 != 0) goto L_0x0030;
        L_0x001a:
            r3 = r0.resource;
            if (r3 != 0) goto L_0x002e;
        L_0x001e:
            r3 = r8.supplementedResource;
            if (r3 != 0) goto L_0x003b;
        L_0x0022:
            r3 = r0.supplementedResource;
            if (r3 != 0) goto L_0x002e;
        L_0x0026:
            r3 = r8.unknownFieldData;
            if (r3 != 0) goto L_0x0046;
        L_0x002a:
            r3 = r0.unknownFieldData;
            if (r3 == 0) goto L_0x0004;
        L_0x002e:
            r1 = r2;
            goto L_0x0004;
        L_0x0030:
            r3 = r8.resource;
            r4 = r0.resource;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x002e;
        L_0x003a:
            goto L_0x001e;
        L_0x003b:
            r3 = r8.supplementedResource;
            r4 = r0.supplementedResource;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x002e;
        L_0x0045:
            goto L_0x0026;
        L_0x0046:
            r3 = r8.unknownFieldData;
            r4 = r0.unknownFieldData;
            r3 = r3.equals(r4);
            if (r3 == 0) goto L_0x002e;
        L_0x0050:
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.tagmanager.proto.Resource.ResourceWithMetadata.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            int i = 0;
            int hashCode = (((((((int) (this.timeStamp ^ (this.timeStamp >>> 32))) + 527) * 31) + (this.resource == null ? 0 : this.resource.hashCode())) * 31) + (this.supplementedResource == null ? 0 : this.supplementedResource.hashCode())) * 31;
            if (this.unknownFieldData != null) {
                i = this.unknownFieldData.hashCode();
            }
            return hashCode + i;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt64(1, this.timeStamp);
            if (this.resource != null) {
                output.writeMessage(2, this.resource);
            }
            if (this.supplementedResource != null) {
                output.writeMessage(3, this.supplementedResource);
            }
            WireFormatNano.writeUnknownFields(this.unknownFieldData, output);
        }

        public int getSerializedSize() {
            int size = 0 + CodedOutputByteBufferNano.computeInt64Size(1, this.timeStamp);
            if (this.resource != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(2, this.resource);
            }
            if (this.supplementedResource != null) {
                size += CodedOutputByteBufferNano.computeMessageSize(3, this.supplementedResource);
            }
            size += WireFormatNano.computeWireSize(this.unknownFieldData);
            this.cachedSize = size;
            return size;
        }

        public ResourceWithMetadata mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case dx.a /*0*/:
                        break;
                    case h.g /*8*/:
                        this.timeStamp = input.readInt64();
                        continue;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        this.resource = new com.google.analytics.containertag.proto.Serving.Resource();
                        input.readMessage(this.resource);
                        continue;
                    case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                        this.supplementedResource = new SupplementedResource();
                        input.readMessage(this.supplementedResource);
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

        public static ResourceWithMetadata parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ResourceWithMetadata) MessageNano.mergeFrom(new ResourceWithMetadata(), data);
        }

        public static ResourceWithMetadata parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ResourceWithMetadata().mergeFrom(input);
        }
    }
}
