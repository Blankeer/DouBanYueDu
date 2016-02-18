package com.igexin.push.c.c;

import android.text.TextUtils;
import com.igexin.a.a.b.g;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;

public class o extends e {
    public long a;
    public String b;
    public String c;
    public String d;

    public o() {
        this.b = Table.STRING_DEFAULT_VALUE;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.d = Table.STRING_DEFAULT_VALUE;
        this.i = 9;
    }

    private String a(byte[] bArr, int i, int i2) {
        try {
            return new String(bArr, i, i2, HttpRequest.CHARSET_UTF8);
        } catch (Exception e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public void a(byte[] bArr) {
        int i;
        int i2;
        this.a = g.d(bArr, 0);
        if (bArr.length > 8) {
            i = 9;
            i2 = bArr[8] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            if (i2 > 0) {
                this.b = a(bArr, 9, i2);
                i = i2 + 9;
            }
        } else {
            i = 8;
        }
        if (bArr.length > i) {
            i2 = i + 1;
            i = bArr[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            if (i > 0) {
                this.c = a(bArr, i2, i);
                i += i2;
            } else {
                i = i2;
            }
        }
        if (bArr.length > i) {
            i2 = i + 1;
            i = bArr[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            if (i > 0) {
                this.d = a(bArr, i2, i);
                i += i2;
            }
        }
    }

    public byte[] d() {
        if (TextUtils.isEmpty(this.c) || TextUtils.isEmpty(this.d)) {
            Object bytes = this.b.getBytes();
            byte[] bArr = new byte[((bytes.length + 8) + 1)];
            g.a(this.a, bArr, 0);
            g.c(bytes.length, bArr, 8);
            System.arraycopy(bytes, 0, bArr, 9, bytes.length);
            return bArr;
        }
        bytes = this.b.getBytes();
        Object bytes2 = this.c.getBytes();
        Object bytes3 = this.d.getBytes();
        bArr = new byte[((((bytes.length + 8) + bytes2.length) + bytes3.length) + 3)];
        g.a(this.a, bArr, 0);
        g.c(bytes.length, bArr, 8);
        System.arraycopy(bytes, 0, bArr, 9, bytes.length);
        int length = bytes.length + 9;
        int i = length + 1;
        g.c(bytes2.length, bArr, length);
        System.arraycopy(bytes2, 0, bArr, i, bytes2.length);
        length = bytes2.length + i;
        int i2 = length + 1;
        g.c(bytes3.length, bArr, length);
        System.arraycopy(bytes3, 0, bArr, i2, bytes3.length);
        return bArr;
    }
}
