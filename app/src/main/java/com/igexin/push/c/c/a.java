package com.igexin.push.c.c;

import android.support.v4.media.TransportMediator;
import com.igexin.a.a.b.g;
import com.igexin.download.Downloads;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;

public class a extends e {
    public int a;
    public int b;
    public Object c;
    public String d;
    public String e;
    private int f;
    private int g;

    public a() {
        this.f = 0;
        this.g = 0;
        this.e = HttpRequest.CHARSET_UTF8;
        this.i = 28;
    }

    public int a() {
        return this.f;
    }

    public void a(int i) {
        this.f = i;
    }

    public void a(byte[] bArr) {
        this.a = g.b(bArr, 0);
        this.b = bArr[2] & Downloads.STATUS_RUNNING;
        this.e = a(bArr[2]);
        int i = 3;
        int i2 = 0;
        while (true) {
            i2 |= bArr[i] & TransportMediator.KEYCODE_MEDIA_PAUSE;
            if ((bArr[i] & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 0) {
                break;
            }
            i2 <<= 7;
            i++;
        }
        i++;
        if (i2 > 0) {
            if (this.b == Downloads.STATUS_RUNNING) {
                this.c = new byte[i2];
                System.arraycopy(bArr, i, this.c, 0, i2);
            } else {
                try {
                    this.c = new String(bArr, i, i2, this.e);
                } catch (Exception e) {
                }
            }
        }
        i2 += i;
        int i3 = bArr[i2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
        i2++;
        if (bArr.length > i2) {
            try {
                this.d = new String(bArr, i2, i3, this.e);
            } catch (Exception e2) {
            }
            i2 += i3;
        }
    }

    public void b(int i) {
        this.g = i;
    }

    public int c() {
        return this.g;
    }

    public byte[] d() {
        int i = 0;
        try {
            byte[] bytes = this.d.getBytes(this.e);
            r3 = !Table.STRING_DEFAULT_VALUE.equals(this.c) ? this.b == Downloads.STATUS_RUNNING ? (byte[]) this.c : ((String) this.c).getBytes(this.e) : null;
            if (r3 != null) {
                i = r3.length;
            }
            byte[] a = g.a(i);
            byte[] bArr = new byte[(((a.length + 4) + i) + bytes.length)];
            try {
                int b = g.b(this.a, bArr, 0);
                b += g.c(this.b | a(this.e), bArr, b);
                b += g.a(a, 0, bArr, b, a.length);
                if (i > 0) {
                    b += g.a(r3, 0, bArr, b, i);
                }
                b += g.c(bytes.length, bArr, b);
                b += g.a(bytes, 0, bArr, b, bytes.length);
                return bArr;
            } catch (Exception e) {
                return bArr;
            }
        } catch (Exception e2) {
            return null;
        }
    }
}
