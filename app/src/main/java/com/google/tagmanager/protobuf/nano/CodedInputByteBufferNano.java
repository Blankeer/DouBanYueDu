package com.google.tagmanager.protobuf.nano;

import android.support.v4.media.TransportMediator;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.IOException;
import u.aly.dj;
import u.aly.dx;

public final class CodedInputByteBufferNano {
    private static final int DEFAULT_RECURSION_LIMIT = 64;
    private static final int DEFAULT_SIZE_LIMIT = 67108864;
    private final byte[] buffer;
    private int bufferPos;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int bufferStart;
    private int currentLimit;
    private int lastTag;
    private int recursionDepth;
    private int recursionLimit;
    private int sizeLimit;

    public static CodedInputByteBufferNano newInstance(byte[] buf) {
        return newInstance(buf, 0, buf.length);
    }

    public static CodedInputByteBufferNano newInstance(byte[] buf, int off, int len) {
        return new CodedInputByteBufferNano(buf, off, len);
    }

    public int readTag() throws IOException {
        if (isAtEnd()) {
            this.lastTag = 0;
            return 0;
        }
        this.lastTag = readRawVarint32();
        if (this.lastTag != 0) {
            return this.lastTag;
        }
        throw InvalidProtocolBufferNanoException.invalidTag();
    }

    public void checkLastTagWas(int value) throws InvalidProtocolBufferNanoException {
        if (this.lastTag != value) {
            throw InvalidProtocolBufferNanoException.invalidEndTag();
        }
    }

    public boolean skipField(int tag) throws IOException {
        switch (WireFormatNano.getTagWireType(tag)) {
            case dx.a /*0*/:
                readInt32();
                return true;
            case dx.b /*1*/:
                readRawLittleEndian64();
                return true;
            case dx.c /*2*/:
                skipRawBytes(readRawVarint32());
                return true;
            case dx.d /*3*/:
                skipMessage();
                checkLastTagWas(WireFormatNano.makeTag(WireFormatNano.getTagFieldNumber(tag), 4));
                return true;
            case dx.e /*4*/:
                return false;
            case dj.f /*5*/:
                readRawLittleEndian32();
                return true;
            default:
                throw InvalidProtocolBufferNanoException.invalidWireType();
        }
    }

    public void skipMessage() throws IOException {
        int tag;
        do {
            tag = readTag();
            if (tag == 0) {
                return;
            }
        } while (skipField(tag));
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readRawLittleEndian64());
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readRawLittleEndian32());
    }

    public long readUInt64() throws IOException {
        return readRawVarint64();
    }

    public long readInt64() throws IOException {
        return readRawVarint64();
    }

    public int readInt32() throws IOException {
        return readRawVarint32();
    }

    public long readFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public boolean readBool() throws IOException {
        return readRawVarint32() != 0;
    }

    public String readString() throws IOException {
        int size = readRawVarint32();
        if (size > this.bufferSize - this.bufferPos || size <= 0) {
            return new String(readRawBytes(size), HttpRequest.CHARSET_UTF8);
        }
        String result = new String(this.buffer, this.bufferPos, size, HttpRequest.CHARSET_UTF8);
        this.bufferPos += size;
        return result;
    }

    public void readGroup(MessageNano msg, int fieldNumber) throws IOException {
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
        }
        this.recursionDepth++;
        msg.mergeFrom(this);
        checkLastTagWas(WireFormatNano.makeTag(fieldNumber, 4));
        this.recursionDepth--;
    }

    public void readMessage(MessageNano msg) throws IOException {
        int length = readRawVarint32();
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferNanoException.recursionLimitExceeded();
        }
        int oldLimit = pushLimit(length);
        this.recursionDepth++;
        msg.mergeFrom(this);
        checkLastTagWas(0);
        this.recursionDepth--;
        popLimit(oldLimit);
    }

    public byte[] readBytes() throws IOException {
        int size = readRawVarint32();
        if (size > this.bufferSize - this.bufferPos || size <= 0) {
            return readRawBytes(size);
        }
        byte[] result = new byte[size];
        System.arraycopy(this.buffer, this.bufferPos, result, 0, size);
        this.bufferPos += size;
        return result;
    }

    public int readUInt32() throws IOException {
        return readRawVarint32();
    }

    public int readEnum() throws IOException {
        return readRawVarint32();
    }

    public int readSFixed32() throws IOException {
        return readRawLittleEndian32();
    }

    public long readSFixed64() throws IOException {
        return readRawLittleEndian64();
    }

    public int readSInt32() throws IOException {
        return decodeZigZag32(readRawVarint32());
    }

    public long readSInt64() throws IOException {
        return decodeZigZag64(readRawVarint64());
    }

    public int readRawVarint32() throws IOException {
        byte tmp = readRawByte();
        if (tmp >= null) {
            return tmp;
        }
        int result = tmp & TransportMediator.KEYCODE_MEDIA_PAUSE;
        tmp = readRawByte();
        if (tmp >= null) {
            return result | (tmp << 7);
        }
        result |= (tmp & TransportMediator.KEYCODE_MEDIA_PAUSE) << 7;
        tmp = readRawByte();
        if (tmp >= null) {
            return result | (tmp << 14);
        }
        result |= (tmp & TransportMediator.KEYCODE_MEDIA_PAUSE) << 14;
        tmp = readRawByte();
        if (tmp >= null) {
            return result | (tmp << 21);
        }
        result |= (tmp & TransportMediator.KEYCODE_MEDIA_PAUSE) << 21;
        tmp = readRawByte();
        result |= tmp << 28;
        if (tmp >= null) {
            return result;
        }
        for (int i = 0; i < 5; i++) {
            if (readRawByte() >= null) {
                return result;
            }
        }
        throw InvalidProtocolBufferNanoException.malformedVarint();
    }

    public long readRawVarint64() throws IOException {
        long result = 0;
        for (int shift = 0; shift < DEFAULT_RECURSION_LIMIT; shift += 7) {
            byte b = readRawByte();
            result |= ((long) (b & TransportMediator.KEYCODE_MEDIA_PAUSE)) << shift;
            if ((b & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 0) {
                return result;
            }
        }
        throw InvalidProtocolBufferNanoException.malformedVarint();
    }

    public int readRawLittleEndian32() throws IOException {
        return (((readRawByte() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) | ((readRawByte() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8)) | ((readRawByte() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 16)) | ((readRawByte() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 24);
    }

    public long readRawLittleEndian64() throws IOException {
        return (((((((((long) readRawByte()) & 255) | ((((long) readRawByte()) & 255) << 8)) | ((((long) readRawByte()) & 255) << 16)) | ((((long) readRawByte()) & 255) << 24)) | ((((long) readRawByte()) & 255) << 32)) | ((((long) readRawByte()) & 255) << 40)) | ((((long) readRawByte()) & 255) << 48)) | ((((long) readRawByte()) & 255) << 56);
    }

    public static int decodeZigZag32(int n) {
        return (n >>> 1) ^ (-(n & 1));
    }

    public static long decodeZigZag64(long n) {
        return (n >>> 1) ^ (-(1 & n));
    }

    private CodedInputByteBufferNano(byte[] buffer, int off, int len) {
        this.currentLimit = AdvancedShareActionProvider.WEIGHT_MAX;
        this.recursionLimit = DEFAULT_RECURSION_LIMIT;
        this.sizeLimit = DEFAULT_SIZE_LIMIT;
        this.buffer = buffer;
        this.bufferStart = off;
        this.bufferSize = off + len;
        this.bufferPos = off;
    }

    public int setRecursionLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
        }
        int oldLimit = this.recursionLimit;
        this.recursionLimit = limit;
        return oldLimit;
    }

    public int setSizeLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
        }
        int oldLimit = this.sizeLimit;
        this.sizeLimit = limit;
        return oldLimit;
    }

    public void resetSizeCounter() {
    }

    public int pushLimit(int byteLimit) throws InvalidProtocolBufferNanoException {
        if (byteLimit < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        }
        byteLimit += this.bufferPos;
        int oldLimit = this.currentLimit;
        if (byteLimit > oldLimit) {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        this.currentLimit = byteLimit;
        recomputeBufferSizeAfterLimit();
        return oldLimit;
    }

    private void recomputeBufferSizeAfterLimit() {
        this.bufferSize += this.bufferSizeAfterLimit;
        int bufferEnd = this.bufferSize;
        if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.bufferSize -= this.bufferSizeAfterLimit;
            return;
        }
        this.bufferSizeAfterLimit = 0;
    }

    public void popLimit(int oldLimit) {
        this.currentLimit = oldLimit;
        recomputeBufferSizeAfterLimit();
    }

    public int getBytesUntilLimit() {
        if (this.currentLimit == AdvancedShareActionProvider.WEIGHT_MAX) {
            return -1;
        }
        return this.currentLimit - this.bufferPos;
    }

    public boolean isAtEnd() {
        return this.bufferPos == this.bufferSize;
    }

    public int getPosition() {
        return this.bufferPos - this.bufferStart;
    }

    public byte[] getData(int offset, int length) {
        if (length == 0) {
            return WireFormatNano.EMPTY_BYTES;
        }
        byte[] copy = new byte[length];
        System.arraycopy(this.buffer, this.bufferStart + offset, copy, 0, length);
        return copy;
    }

    public void rewindToPosition(int position) {
        if (position > this.bufferPos - this.bufferStart) {
            throw new IllegalArgumentException("Position " + position + " is beyond current " + (this.bufferPos - this.bufferStart));
        } else if (position < 0) {
            throw new IllegalArgumentException("Bad position " + position);
        } else {
            this.bufferPos = this.bufferStart + position;
        }
    }

    public byte readRawByte() throws IOException {
        if (this.bufferPos == this.bufferSize) {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
        byte[] bArr = this.buffer;
        int i = this.bufferPos;
        this.bufferPos = i + 1;
        return bArr[i];
    }

    public byte[] readRawBytes(int size) throws IOException {
        if (size < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        } else if (this.bufferPos + size > this.currentLimit) {
            skipRawBytes(this.currentLimit - this.bufferPos);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        } else if (size <= this.bufferSize - this.bufferPos) {
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, this.bufferPos, bytes, 0, size);
            this.bufferPos += size;
            return bytes;
        } else {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
    }

    public void skipRawBytes(int size) throws IOException {
        if (size < 0) {
            throw InvalidProtocolBufferNanoException.negativeSize();
        } else if (this.bufferPos + size > this.currentLimit) {
            skipRawBytes(this.currentLimit - this.bufferPos);
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        } else if (size <= this.bufferSize - this.bufferPos) {
            this.bufferPos += size;
        } else {
            throw InvalidProtocolBufferNanoException.truncatedMessage();
        }
    }
}
