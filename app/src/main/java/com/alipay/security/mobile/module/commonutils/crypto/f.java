package com.alipay.security.mobile.module.commonutils.crypto;

import android.support.v4.media.TransportMediator;
import com.douban.book.reader.constant.Char;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.OutputStream;

public final class f {
    protected final byte[] a;
    protected final byte[] b;

    public f() {
        this.a = new byte[]{(byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102};
        this.b = new byte[TransportMediator.FLAG_KEY_MEDIA_NEXT];
        a();
    }

    private void a() {
        for (int i = 0; i < this.a.length; i++) {
            this.b[this.a[i]] = (byte) i;
        }
        this.b[65] = this.b[97];
        this.b[66] = this.b[98];
        this.b[67] = this.b[99];
        this.b[68] = this.b[100];
        this.b[69] = this.b[Header.FLOAT];
        this.b[70] = this.b[Header.DOUBLE_M1];
    }

    private static boolean a(char c) {
        return c == '\n' || c == Char.CARRIAGE_RETURN || c == '\t' || c == Char.SPACE;
    }

    public final int a(String str, OutputStream outputStream) {
        int i = 0;
        int length = str.length();
        while (length > 0 && a(str.charAt(length - 1))) {
            length--;
        }
        int i2 = 0;
        while (i < length) {
            int i3 = i;
            while (i3 < length && a(str.charAt(i3))) {
                i3++;
            }
            i = i3 + 1;
            byte b = this.b[str.charAt(i3)];
            while (i < length && a(str.charAt(i))) {
                i++;
            }
            i3 = i + 1;
            outputStream.write(this.b[str.charAt(i)] | (b << 4));
            i2++;
            i = i3;
        }
        return i2;
    }

    public final int a(byte[] bArr, int i, int i2, OutputStream outputStream) {
        for (int i3 = i; i3 < i + i2; i3++) {
            int i4 = bArr[i3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            outputStream.write(this.a[i4 >>> 4]);
            outputStream.write(this.a[i4 & 15]);
        }
        return i2 * 2;
    }

    public final int a(byte[] bArr, int i, OutputStream outputStream) {
        int i2 = 0;
        int i3 = i + 0;
        while (i3 > 0 && a((char) bArr[i3 - 1])) {
            i3--;
        }
        int i4 = 0;
        while (i2 < i3) {
            int i5 = i2;
            while (i5 < i3 && a((char) bArr[i5])) {
                i5++;
            }
            i2 = i5 + 1;
            byte b = this.b[bArr[i5]];
            while (i2 < i3 && a((char) bArr[i2])) {
                i2++;
            }
            i5 = i2 + 1;
            outputStream.write(this.b[bArr[i2]] | (b << 4));
            i4++;
            i2 = i5;
        }
        return i4;
    }
}
