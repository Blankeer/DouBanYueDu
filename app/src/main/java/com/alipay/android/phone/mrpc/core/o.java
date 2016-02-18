package com.alipay.android.phone.mrpc.core;

import android.content.Context;
import java.lang.reflect.Proxy;

public final class o extends ad {
    Context a;

    public o(Context context) {
        this.a = context;
    }

    public final <T> T a(Class<T> cls, e eVar) {
        ae aeVar = new ae(new p(this, eVar));
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new af(aeVar.a, cls, aeVar.b));
    }
}
