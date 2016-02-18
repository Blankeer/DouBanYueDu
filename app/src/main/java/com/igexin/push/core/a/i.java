package com.igexin.push.core.a;

import android.text.TextUtils;
import com.igexin.a.a.c.a;
import com.igexin.a.a.d.d;
import com.igexin.push.c.c.k;
import com.igexin.push.config.SDKUrlConfig;
import com.igexin.push.config.l;
import com.igexin.push.core.c.w;
import com.igexin.push.core.f;
import com.igexin.push.core.g;
import com.igexin.push.e.a.c;
import com.igexin.push.f.b;
import com.tencent.connect.common.Constants;

public class i extends a {
    public boolean a(d dVar) {
        return false;
    }

    public boolean a(Object obj) {
        boolean z = false;
        if (obj instanceof k) {
            g.D = 0;
            if (!g.m) {
                w.c();
                if (((k) obj).a) {
                    a.b("loginRsp|" + g.s + "|success");
                    a.b("isCidBroadcast|" + g.n);
                    if (!g.n) {
                        e.a().l();
                        g.n = true;
                    }
                    g.m = true;
                    e.a().m();
                    e.a().g();
                    if (TextUtils.isEmpty(g.z)) {
                        a.b("LoginResultAction device id is empty, get device id from server ++++++++++++");
                        e.a().h();
                    }
                    b.f();
                    try {
                        long currentTimeMillis = System.currentTimeMillis() - g.H;
                        String e = e.a().e("ua");
                        if (e == null || Constants.VIA_TO_TYPE_QQ_GROUP.equals(e)) {
                            z = true;
                        }
                        if (z && l.h && currentTimeMillis - 259200000 > 0) {
                            if (!com.igexin.a.b.a.a(e.a().p()).equals(e.a().n()) || g.H == 0) {
                                g.H = System.currentTimeMillis();
                                e.a().o();
                            }
                        }
                    } catch (Exception e2) {
                    }
                    try {
                        if ((System.currentTimeMillis() - g.G) - com.umeng.analytics.a.g > 0) {
                            e.a().j();
                        }
                    } catch (Exception e3) {
                    }
                    if ((System.currentTimeMillis() - g.F) - com.umeng.analytics.a.g > 0) {
                        if (TextUtils.isEmpty(g.z)) {
                            if (g.av != null) {
                                g.av.t();
                                g.av = null;
                            }
                            g.av = new j(this, 30000);
                            f.a().a(g.av);
                        } else {
                            e.a().i();
                        }
                        g.F = System.currentTimeMillis();
                    }
                    try {
                        if ((System.currentTimeMillis() - g.K) - com.umeng.analytics.a.g > 0) {
                            com.igexin.a.a.b.d.c().a(new c(new com.igexin.push.core.d.g(SDKUrlConfig.getConfigServiceUrl())), false, true);
                        }
                    } catch (Exception e4) {
                    }
                    com.igexin.push.core.c.f.a().b();
                    f.a().h().a();
                } else {
                    a.b("loginRsp|" + g.s + "|failed");
                    a.b("LoginResultAction login failed, clear session or cid");
                    com.igexin.push.core.c.f.a().c();
                    e.a().e();
                    com.igexin.a.a.b.d.c().a((Object) new com.igexin.push.c.b.b());
                    com.igexin.a.a.b.d.c().d();
                }
            }
        }
        return true;
    }
}
