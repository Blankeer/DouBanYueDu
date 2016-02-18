package com.alipay.apmobilesecuritysdk.face;

import android.content.Context;
import io.realm.internal.Table;
import java.util.LinkedList;
import java.util.Map;

public final class a {
    private static a c;
    private static Object d;
    public Thread a;
    public LinkedList<b> b;
    private Context e;
    private volatile boolean f;

    public interface a {
        void a();
    }

    private class b {
        int a;
        String b;
        String c;
        String d;
        a e;
        final /* synthetic */ a f;

        public b(a aVar, String str, String str2, String str3) {
            this.f = aVar;
            this.a = 0;
            this.d = str3;
            if (com.alipay.security.mobile.module.commonutils.a.a(str)) {
                this.b = com.alipay.apmobilesecuritysdk.d.b.a(aVar.e);
            } else {
                this.b = str;
            }
            new StringBuilder("Utdid = ").append(this.b);
            this.c = str2;
            this.e = null;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void a() {
            /*
            r4 = this;
            r3 = 0;
            r0 = r4.f;
            r0 = r0.f;
            if (r0 == 0) goto L_0x000a;
        L_0x0009:
            return;
        L_0x000a:
            r0 = r4.f;
            r1 = 1;
            r0.f = r1;
            r0 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0 = r0.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            com.alipay.apmobilesecuritysdk.d.a.a(r0);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0 = r4.a;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            com.alipay.apmobilesecuritysdk.d.a.b();	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.<init>();	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = "tid";
            r2 = r4.c;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.put(r1, r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = "utdid";
            r2 = r4.b;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.put(r1, r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = "umid";
            r2 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = r2.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            com.alipay.apmobilesecuritysdk.d.a.a(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = com.alipay.apmobilesecuritysdk.d.a.a();	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.put(r1, r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = "userId";
            r2 = r4.d;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.put(r1, r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r1.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            com.alipay.apmobilesecuritysdk.face.e.a(r1, r0);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0 = r4.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            if (r0 == 0) goto L_0x00c3;
        L_0x0057:
            r0 = new com.alipay.apmobilesecuritysdk.face.a$c;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.<init>(r1);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r1.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = com.alipay.apmobilesecuritysdk.a.a.b(r1);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.c = r1;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r1.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = com.alipay.apmobilesecuritysdk.a.a.a(r1);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.b = r1;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r1.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            com.alipay.apmobilesecuritysdk.d.a.a(r1);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = com.alipay.apmobilesecuritysdk.d.a.a();	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.a = r1;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r4.f;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = r1.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = com.alipay.apmobilesecuritysdk.f.b.a(r1);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0.d = r1;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = "[*]result.apdid     = ";
            r1.<init>(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = r0.c;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1.append(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = "[*]result.token     = ";
            r1.<init>(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = r0.b;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1.append(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = "[*]result.umid      = ";
            r1.<init>(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = r0.a;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1.append(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r2 = "[*]result.clientKey = ";
            r1.<init>(r2);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0 = r0.d;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r1.append(r0);	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
            r0 = r4.e;	 Catch:{ Throwable -> 0x00ca, all -> 0x00d2 }
        L_0x00c3:
            r0 = r4.f;
            r0.f = r3;
            goto L_0x0009;
        L_0x00ca:
            r0 = move-exception;
            r0 = r4.f;
            r0.f = r3;
            goto L_0x0009;
        L_0x00d2:
            r0 = move-exception;
            r1 = r4.f;
            r1.f = r3;
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.alipay.apmobilesecuritysdk.face.a.b.a():void");
        }
    }

    public class c {
        public String a;
        public String b;
        public String c;
        public String d;
        final /* synthetic */ a e;

        public c(a aVar) {
            this.e = aVar;
        }
    }

    static {
        d = new Object();
    }

    private a(Context context) {
        this.f = false;
        this.b = new LinkedList();
        this.e = context;
    }

    public static a a(Context context) {
        a aVar;
        synchronized (d) {
            if (c == null) {
                c = new a(context);
            }
            aVar = c;
        }
        return aVar;
    }

    private String a() {
        return com.alipay.apmobilesecuritysdk.a.a.a(this.e);
    }

    private void a(Map<String, String> map) {
        String a = com.alipay.security.mobile.module.commonutils.a.a(map, com.alipay.sdk.cons.b.g, Table.STRING_DEFAULT_VALUE);
        String a2 = com.alipay.security.mobile.module.commonutils.a.a(map, com.alipay.sdk.cons.b.c, Table.STRING_DEFAULT_VALUE);
        String a3 = com.alipay.security.mobile.module.commonutils.a.a(map, "userId", Table.STRING_DEFAULT_VALUE);
        com.alipay.security.mobile.module.a.a.a.a("https://mobilegw.alipay.com/mgw.htm");
        this.b.addLast(new b(this, a, a2, a3));
        if (this.a == null) {
            this.a = new Thread(new b(this));
            this.a.setUncaughtExceptionHandler(new c(this));
            this.a.start();
        }
    }

    private c b() {
        c cVar = new c(this);
        try {
            long currentTimeMillis = System.currentTimeMillis();
            cVar.b = com.alipay.apmobilesecuritysdk.a.a.a(this.e);
            new StringBuilder("getLocalApdidToken spend ").append(System.currentTimeMillis() - currentTimeMillis).append(" ms");
            cVar.c = com.alipay.apmobilesecuritysdk.a.a.b(this.e);
            com.alipay.apmobilesecuritysdk.d.a.a(this.e);
            cVar.a = com.alipay.apmobilesecuritysdk.d.a.a();
            cVar.d = com.alipay.apmobilesecuritysdk.f.b.a(this.e);
        } catch (Throwable th) {
        }
        return cVar;
    }
}
