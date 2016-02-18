package com.igexin.push.c.c;

import android.support.v4.media.TransportMediator;
import com.igexin.a.a.b.g;

public class m extends e {
    public boolean a;
    public boolean b;
    public String c;
    public String d;
    public long e;

    public m() {
        this.i = 37;
    }

    public void a(byte[] bArr) {
        int i = 1;
        boolean z = false;
        byte b = bArr[0];
        this.a = (b & 64) != 0;
        if ((b & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0) {
            z = true;
        }
        this.b = z;
        if (this.b) {
            this.c = a(b);
            int b2 = g.b(bArr, 1);
            i = (b2 + 2) + 1;
            try {
                this.d = new String(bArr, 3, b2, this.c);
            } catch (Exception e) {
            }
        }
        if (bArr.length > i) {
            this.e = g.d(bArr, i);
            b2 = i + 8;
        }
    }

    public byte[] d() {
        int i;
        byte[] bytes;
        int i2;
        byte a;
        int i3;
        byte[] bArr;
        int i4 = this.a ? (byte) 64 : 0;
        if (this.b) {
            byte b = (byte) (i4 | TransportMediator.FLAG_KEY_MEDIA_NEXT);
            i = 3;
            try {
                bytes = this.d.getBytes(this.c);
                try {
                    i = bytes.length;
                    i2 = i + 3;
                } catch (Exception e) {
                    i2 = i;
                    i = 0;
                    a = (byte) (b | a(this.c));
                    i3 = i2;
                    i2 = a;
                    bArr = new byte[(i3 + 8)];
                    i2 = g.c(i2, bArr, 0);
                    if (this.b) {
                        i2 = g.b(i, bArr, i2);
                        if (bytes != null) {
                            i4 = g.a(bytes, 0, bArr, i2, i) + i2;
                            i4 += g.a(this.e, bArr, i4);
                            return bArr;
                        }
                    }
                    i4 = i2;
                    i4 += g.a(this.e, bArr, i4);
                    return bArr;
                }
            } catch (Exception e2) {
                bytes = null;
                i2 = i;
                i = 0;
                a = (byte) (b | a(this.c));
                i3 = i2;
                i2 = a;
                bArr = new byte[(i3 + 8)];
                i2 = g.c(i2, bArr, 0);
                if (this.b) {
                    i2 = g.b(i, bArr, i2);
                    if (bytes != null) {
                        i4 = g.a(bytes, 0, bArr, i2, i) + i2;
                        i4 += g.a(this.e, bArr, i4);
                        return bArr;
                    }
                }
                i4 = i2;
                i4 += g.a(this.e, bArr, i4);
                return bArr;
            }
            a = (byte) (b | a(this.c));
            i3 = i2;
            i2 = a;
        } else {
            i3 = 1;
            i = 0;
            int i5 = i4;
            bytes = null;
            i2 = i5;
        }
        bArr = new byte[(i3 + 8)];
        i2 = g.c(i2, bArr, 0);
        if (this.b) {
            i2 = g.b(i, bArr, i2);
            if (bytes != null) {
                i4 = g.a(bytes, 0, bArr, i2, i) + i2;
                i4 += g.a(this.e, bArr, i4);
                return bArr;
            }
        }
        i4 = i2;
        i4 += g.a(this.e, bArr, i4);
        return bArr;
    }
}
