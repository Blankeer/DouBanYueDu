package com.igexin.push.c.c;

import com.igexin.a.a.b.g;

public class b {
    public int a;
    public byte b;
    public byte c;
    public byte[] d;

    public void a(byte[] bArr) {
        if (bArr == null) {
            this.a = 0;
            return;
        }
        this.d = bArr;
        this.a = bArr.length;
    }

    public byte[] a() {
        if (this.d == null) {
            return null;
        }
        byte[] bArr = new byte[(this.a + 3)];
        g.b(this.a, bArr, 0);
        g.c(this.b, bArr, 2);
        g.a(this.d, 0, bArr, 3, this.d.length);
        return bArr;
    }
}
