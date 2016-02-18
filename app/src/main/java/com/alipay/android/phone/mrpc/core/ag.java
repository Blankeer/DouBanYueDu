package com.alipay.android.phone.mrpc.core;

import android.os.Looper;
import com.alipay.android.phone.mrpc.core.a.d;
import com.alipay.android.phone.mrpc.core.a.e;
import com.alipay.android.phone.mrpc.core.a.f;
import com.alipay.mobile.framework.service.annotation.a;
import com.alipay.mobile.framework.service.annotation.b;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class ag {
    private static final ThreadLocal<Object> a;
    private static final ThreadLocal<Map<String, Object>> b;
    private byte c;
    private AtomicInteger d;
    private ae e;

    static {
        a = new ThreadLocal();
        b = new ThreadLocal();
    }

    public ag(ae aeVar) {
        this.c = (byte) 0;
        this.e = aeVar;
        this.d = new AtomicInteger();
    }

    public final Object a(Method method, Object[] objArr) {
        boolean z = true;
        boolean z2 = Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper();
        if (z2) {
            throw new IllegalThreadStateException("can't in main thread call rpc .");
        }
        a aVar = (a) method.getAnnotation(a.class);
        if (method.getAnnotation(b.class) == null) {
            z = false;
        }
        Type genericReturnType = method.getGenericReturnType();
        method.getAnnotations();
        a.set(null);
        b.set(null);
        if (aVar == null) {
            throw new IllegalStateException("OperationType must be set.");
        }
        String a = aVar.a();
        int incrementAndGet = this.d.incrementAndGet();
        try {
            if (this.c == null) {
                f eVar = new e(incrementAndGet, a, objArr);
                if (b.get() != null) {
                    eVar.a(b.get());
                }
                byte[] bArr = (byte[]) new q(this.e.a, method, incrementAndGet, a, eVar.a(), z).a();
                b.set(null);
                Object a2 = new d(genericReturnType, bArr).a();
                if (genericReturnType != Void.TYPE) {
                    a.set(a2);
                }
            }
            return a.get();
        } catch (c e) {
            e.a = a;
            throw e;
        }
    }
}
