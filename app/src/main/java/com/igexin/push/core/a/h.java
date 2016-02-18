package com.igexin.push.core.a;

import com.igexin.a.a.c.a;
import com.igexin.a.a.d.d;
import com.igexin.push.config.k;
import com.igexin.push.core.i;

public class h extends a {
    private static final String a;

    static {
        a = k.a;
    }

    public boolean a(d dVar) {
        return false;
    }

    public boolean a(Object obj) {
        if (obj instanceof com.igexin.push.c.c.h) {
            a.b("heartbeatRsp");
            i.a().a(com.igexin.push.core.k.HEARTBEAT_OK);
        }
        return true;
    }
}
