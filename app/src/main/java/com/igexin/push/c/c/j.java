package com.igexin.push.c.c;

public class j extends e {
    public byte a;
    public Object b;

    public void a(byte[] bArr) {
    }

    public byte[] d() {
        Object obj = null;
        if (this.a == (byte) 1) {
            obj = ((String) this.b).getBytes();
        } else if (!(this.a == (byte) 2 || this.a == 3 || this.a != 4)) {
            obj = ((String) this.b).getBytes();
        }
        Object obj2 = new byte[(obj.length + 2)];
        obj2[0] = this.a;
        obj2[1] = (byte) obj.length;
        System.arraycopy(obj, 0, obj2, 2, obj.length);
        return obj2;
    }
}
