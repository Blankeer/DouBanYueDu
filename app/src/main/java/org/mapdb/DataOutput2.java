package org.mapdb;

import android.support.v4.media.TransportMediator;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public final class DataOutput2 extends OutputStream implements DataOutput {
    static final /* synthetic */ boolean $assertionsDisabled;
    public byte[] buf;
    public volatile int pos;

    static {
        $assertionsDisabled = !DataOutput2.class.desiredAssertionStatus();
    }

    public DataOutput2() {
        this.pos = 0;
        this.buf = new byte[16];
    }

    public DataOutput2(byte[] buf) {
        this.pos = 0;
        this.buf = buf;
    }

    public byte[] copyBytes() {
        return Arrays.copyOf(this.buf, this.pos);
    }

    public void ensureAvail(int n) {
        if (this.pos + n >= this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, Math.max(this.pos + n, this.buf.length * 2));
        }
    }

    public void write(int b) throws IOException {
        ensureAvail(1);
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) b;
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        ensureAvail(len);
        System.arraycopy(b, off, this.buf, this.pos, len);
        this.pos += len;
    }

    public void writeBoolean(boolean v) throws IOException {
        int i = 1;
        ensureAvail(1);
        byte[] bArr = this.buf;
        int i2 = this.pos;
        this.pos = i2 + 1;
        if (!v) {
            i = 0;
        }
        bArr[i2] = (byte) i;
    }

    public void writeByte(int v) throws IOException {
        ensureAvail(1);
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) v;
    }

    public void writeShort(int v) throws IOException {
        ensureAvail(2);
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((v >> 8) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) (v & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
    }

    public void writeChar(int v) throws IOException {
        writeInt(v);
    }

    public void writeInt(int v) throws IOException {
        ensureAvail(4);
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((v >> 24) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((v >> 16) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((v >> 8) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) (v & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
    }

    public void writeLong(long v) throws IOException {
        ensureAvail(8);
        byte[] bArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) ((v >> 56) & 255));
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) ((v >> 48) & 255));
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) ((v >> 40) & 255));
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) ((v >> 32) & 255));
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) ((v >> 24) & 255));
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) ((v >> 16) & 255));
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) ((v >> 8) & 255));
        bArr = this.buf;
        i = this.pos;
        this.pos = i + 1;
        bArr[i] = (byte) ((int) (255 & v));
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeBytes(String s) throws IOException {
        writeUTF(s);
    }

    public void writeChars(String s) throws IOException {
        writeUTF(s);
    }

    public void writeUTF(String s) throws IOException {
        int len = s.length();
        packInt(this, len);
        for (int i = 0; i < len; i++) {
            packInt(this, s.charAt(i));
        }
    }

    public static void packLong(DataOutput out, long value) throws IOException {
        if ($assertionsDisabled || value >= 0) {
            while ((-128 & value) != 0) {
                out.write((((int) value) & TransportMediator.KEYCODE_MEDIA_PAUSE) | TransportMediator.FLAG_KEY_MEDIA_NEXT);
                value >>>= 7;
            }
            out.write((byte) ((int) value));
            return;
        }
        throw new AssertionError("negative value: " + value);
    }

    public static void packInt(DataOutput in, int value) throws IOException {
        if ($assertionsDisabled || value >= 0) {
            while ((value & -128) != 0) {
                in.write((value & TransportMediator.KEYCODE_MEDIA_PAUSE) | TransportMediator.FLAG_KEY_MEDIA_NEXT);
                value >>>= 7;
            }
            in.write((byte) value);
            return;
        }
        throw new AssertionError("negative value: " + value);
    }
}
