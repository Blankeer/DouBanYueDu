package org.mapdb;

import android.support.v4.media.TransportMediator;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class DataInput2 extends InputStream implements DataInput {
    public ByteBuffer buf;
    public int pos;

    public DataInput2(ByteBuffer buf, int pos) {
        this.buf = buf;
        this.pos = pos;
    }

    public DataInput2(byte[] b) {
        this(ByteBuffer.wrap(b), 0);
    }

    public void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        ByteBuffer clone = this.buf.duplicate();
        clone.position(this.pos);
        this.pos += len;
        clone.get(b, off, len);
    }

    public int skipBytes(int n) throws IOException {
        this.pos += n;
        return n;
    }

    public boolean readBoolean() throws IOException {
        ByteBuffer byteBuffer = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        return byteBuffer.get(i) == (byte) 1;
    }

    public byte readByte() throws IOException {
        ByteBuffer byteBuffer = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        return byteBuffer.get(i);
    }

    public int readUnsignedByte() throws IOException {
        ByteBuffer byteBuffer = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        return byteBuffer.get(i) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
    }

    public short readShort() throws IOException {
        short ret = this.buf.getShort(this.pos);
        this.pos += 2;
        return ret;
    }

    public int readUnsignedShort() throws IOException {
        ByteBuffer byteBuffer = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        int i2 = (byteBuffer.get(i) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8;
        ByteBuffer byteBuffer2 = this.buf;
        int i3 = this.pos;
        this.pos = i3 + 1;
        return i2 | (byteBuffer2.get(i3) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
    }

    public char readChar() throws IOException {
        return (char) readInt();
    }

    public int readInt() throws IOException {
        int ret = this.buf.getInt(this.pos);
        this.pos += 4;
        return ret;
    }

    public long readLong() throws IOException {
        long ret = this.buf.getLong(this.pos);
        this.pos += 8;
        return ret;
    }

    public float readFloat() throws IOException {
        float ret = this.buf.getFloat(this.pos);
        this.pos += 4;
        return ret;
    }

    public double readDouble() throws IOException {
        double ret = this.buf.getDouble(this.pos);
        this.pos += 8;
        return ret;
    }

    public String readLine() throws IOException {
        return readUTF();
    }

    public String readUTF() throws IOException {
        return SerializerBase.deserializeString(this, unpackInt(this));
    }

    public int read() throws IOException {
        return readUnsignedByte();
    }

    public static int unpackInt(DataInput is) throws IOException {
        int result = 0;
        for (int offset = 0; offset < 32; offset += 7) {
            int b = is.readUnsignedByte();
            result |= (b & TransportMediator.KEYCODE_MEDIA_PAUSE) << offset;
            if ((b & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 0) {
                return result;
            }
        }
        throw new AssertionError("Malformed int.");
    }

    public static long unpackLong(DataInput in) throws IOException {
        long result = 0;
        for (int offset = 0; offset < 64; offset += 7) {
            long b = (long) in.readUnsignedByte();
            result |= (127 & b) << offset;
            if ((128 & b) == 0) {
                return result;
            }
        }
        throw new AssertionError("Malformed long.");
    }
}
