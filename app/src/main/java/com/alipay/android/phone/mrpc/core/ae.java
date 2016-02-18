package com.alipay.android.phone.mrpc.core;

import java.lang.reflect.Proxy;

public final class ae {
    n a;
    ag b;

    public ae(n nVar) {
        this.a = nVar;
        this.b = new ag(this);
    }

    private n a() {
        return this.a;
    }

    private <T> T a(Class<T> cls) {
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new af(this.a, cls, this.b));
    }
}
