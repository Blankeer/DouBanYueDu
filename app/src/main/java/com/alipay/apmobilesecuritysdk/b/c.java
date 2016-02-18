package com.alipay.apmobilesecuritysdk.b;

import com.alipay.security.mobile.module.commonutils.d;

public final class c implements Runnable {
    final /* synthetic */ b a;

    public c(b bVar) {
        this.a = bVar;
    }

    public final void run() {
        try {
            this.a.a();
        } catch (Throwable th) {
            d.a(th);
        }
    }
}
