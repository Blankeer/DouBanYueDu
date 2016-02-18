package com.igexin.push.c.c;

import com.igexin.a.a.b.g;

public class l extends e {
    public long a;

    public l() {
        this.i = 36;
        this.j = (byte) 52;
    }

    public void a(byte[] bArr) {
        this.a = g.d(bArr, 0);
    }

    public byte[] d() {
        byte[] bArr = new byte[8];
        g.a(this.a, bArr, 0);
        return bArr;
    }
}
