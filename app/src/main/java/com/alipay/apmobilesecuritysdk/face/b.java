package com.alipay.apmobilesecuritysdk.face;

import com.alipay.apmobilesecuritysdk.d.a;
import com.alipay.apmobilesecuritysdk.face.a.c;
import java.util.HashMap;
import java.util.Map;

public final class b implements Runnable {
    final /* synthetic */ a a;

    public b(a aVar) {
        this.a = aVar;
    }

    public final void run() {
        b bVar;
        while (!this.a.b.isEmpty()) {
            try {
                bVar = (b) this.a.b.pollFirst();
                if (!(bVar == null || bVar.f.f)) {
                    bVar.f.f = true;
                    a.a(bVar.f.e);
                    int i = bVar.a;
                    a.b();
                    Map hashMap = new HashMap();
                    hashMap.put(com.alipay.sdk.cons.b.c, bVar.c);
                    hashMap.put(com.alipay.sdk.cons.b.g, bVar.b);
                    a.a(bVar.f.e);
                    hashMap.put("umid", a.a());
                    hashMap.put("userId", bVar.d);
                    e.a(bVar.f.e, hashMap);
                    if (bVar.e != null) {
                        c cVar = new c(bVar.f);
                        cVar.c = com.alipay.apmobilesecuritysdk.a.a.b(bVar.f.e);
                        cVar.b = com.alipay.apmobilesecuritysdk.a.a.a(bVar.f.e);
                        a.a(bVar.f.e);
                        cVar.a = a.a();
                        cVar.d = com.alipay.apmobilesecuritysdk.f.b.a(bVar.f.e);
                        new StringBuilder("[*]result.apdid     = ").append(cVar.c);
                        new StringBuilder("[*]result.token     = ").append(cVar.b);
                        new StringBuilder("[*]result.umid      = ").append(cVar.a);
                        new StringBuilder("[*]result.clientKey = ").append(cVar.d);
                        a.a aVar = bVar.e;
                    }
                    bVar.f.f = false;
                }
            } catch (Throwable th) {
                this.a.a = null;
            }
        }
        this.a.a = null;
    }
}
