package com.igexin.push.core.d;

import android.util.Base64;
import com.igexin.a.a.c.a;
import com.igexin.push.config.n;
import com.igexin.push.e.a.b;

public class f extends b {
    public f(String str) {
        super(str);
        a();
    }

    public void a() {
    }

    public void a(Exception exception) {
        com.igexin.push.core.c.f.a().d(System.currentTimeMillis());
        a.b("-> get idc config " + exception.toString());
    }

    public void a(byte[] bArr) {
        if (bArr != null) {
            try {
                String str = new String(com.igexin.a.b.a.c(Base64.decode(bArr, 0)));
                a.b("-> get idc config server resp data : " + str);
                if (str != null) {
                    com.igexin.push.config.a.a().a(str);
                    n.a(str);
                    com.igexin.push.core.c.f.a().d(0);
                }
            } catch (Exception e) {
                com.igexin.push.core.c.f.a().d(System.currentTimeMillis());
                throw e;
            }
        }
    }

    public int b() {
        return 0;
    }
}
