package com.igexin.push.a.a;

import com.igexin.push.core.a.e;
import com.igexin.push.core.g;
import com.igexin.push.e.b.d;
import com.tencent.connect.common.Constants;
import com.umeng.analytics.a;

public class b implements d {
    private long a;

    public b() {
        this.a = 0;
    }

    public void a() {
        e.a().z();
        e.a().r();
        e.a().s();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - g.J > a.h) {
            Object obj = 1;
            if (e.a().a(currentTimeMillis)) {
                if (Constants.VIA_TO_TYPE_QQ_GROUP.equals(e.a().e("ccs"))) {
                    obj = null;
                }
            }
            if (obj != null) {
                g.J = currentTimeMillis;
                e.a().y();
            }
        }
        e.a().A();
    }

    public void a(long j) {
        this.a = j;
    }

    public boolean b() {
        return System.currentTimeMillis() - this.a > a.h;
    }
}
