package com.alipay.android.phone.mrpc.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class af implements InvocationHandler {
    protected n a;
    protected Class<?> b;
    protected ag c;

    public af(n nVar, Class<?> cls, ag agVar) {
        this.a = nVar;
        this.b = cls;
        this.c = agVar;
    }

    public final Object invoke(Object obj, Method method, Object[] objArr) {
        ag agVar = this.c;
        Class cls = this.b;
        return agVar.a(method, objArr);
    }
}
