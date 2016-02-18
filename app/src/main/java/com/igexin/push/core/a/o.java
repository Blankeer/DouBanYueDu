package com.igexin.push.core.a;

import android.text.TextUtils;
import com.igexin.a.a.c.a;
import com.igexin.a.a.d.d;
import com.igexin.push.c.c.e;
import com.igexin.push.config.k;
import com.igexin.push.core.c.f;
import com.igexin.push.core.g;

public class o extends a {
    private static final String a;

    static {
        a = k.a + "_RegisterResultAction";
    }

    public boolean a(d dVar) {
        return false;
    }

    public boolean a(Object obj) {
        boolean z = false;
        if (obj instanceof com.igexin.push.c.c.o) {
            com.igexin.push.c.c.o oVar = (com.igexin.push.c.c.o) obj;
            g.D = 0;
            a.b("register resp |" + oVar.a + "|" + g.r);
            a.b("register resp cid = " + oVar.c + " device id = " + oVar.d);
            if (oVar.a != g.r) {
                g.n = false;
                a.b(a + " change session : from [" + g.r + "] to [" + oVar.a + "]");
                a.b(a + " change cid : from [" + g.s + "] to [" + oVar.c + "]");
                if (TextUtils.isEmpty(oVar.c) || TextUtils.isEmpty(oVar.d)) {
                    f.a().a(oVar.a);
                } else {
                    f.a().a(oVar.c, oVar.d, oVar.a);
                }
                g.F = 0;
                z = true;
            }
            a.b("loginReqAfterRegister|new session:" + g.r + ", cid :" + g.s + ", devId :" + g.z);
            e c = e.a().c();
            com.igexin.push.core.f.a().g().a("S-" + c.a, c, true);
            if (z) {
                return true;
            }
        }
        return true;
    }
}
