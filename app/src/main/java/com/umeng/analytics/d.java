package com.umeng.analytics;

import android.content.Context;
import android.text.TextUtils;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Map;
import u.aly.ad;
import u.aly.ag;
import u.aly.bt;
import u.aly.l;
import u.aly.n;
import u.aly.o;
import u.aly.v;
import u.aly.x;
import u.aly.z;

/* compiled from: InternalAgent */
public class d implements v {
    private Context a;
    private c b;
    private n c;
    private ad d;
    private z e;
    private o f;
    private l g;
    private boolean h;

    /* compiled from: InternalAgent */
    /* renamed from: com.umeng.analytics.d.1 */
    class AnonymousClass1 extends g {
        final /* synthetic */ Context a;
        final /* synthetic */ d b;

        AnonymousClass1(d dVar, Context context) {
            this.b = dVar;
            this.a = context;
        }

        public void a() {
            this.b.f(this.a.getApplicationContext());
        }
    }

    /* compiled from: InternalAgent */
    /* renamed from: com.umeng.analytics.d.2 */
    class AnonymousClass2 extends g {
        final /* synthetic */ Context a;
        final /* synthetic */ d b;

        AnonymousClass2(d dVar, Context context) {
            this.b = dVar;
            this.a = context;
        }

        public void a() {
            this.b.g(this.a.getApplicationContext());
        }
    }

    /* compiled from: InternalAgent */
    /* renamed from: com.umeng.analytics.d.3 */
    class AnonymousClass3 extends g {
        final /* synthetic */ String a;
        final /* synthetic */ String b;
        final /* synthetic */ d c;

        AnonymousClass3(d dVar, String str, String str2) {
            this.c = dVar;
            this.a = str;
            this.b = str2;
        }

        public void a() {
            this.c.f.a(this.a, this.b);
        }
    }

    /* compiled from: InternalAgent */
    /* renamed from: com.umeng.analytics.d.4 */
    class AnonymousClass4 extends g {
        final /* synthetic */ String a;
        final /* synthetic */ String b;
        final /* synthetic */ d c;

        AnonymousClass4(d dVar, String str, String str2) {
            this.c = dVar;
            this.a = str;
            this.b = str2;
        }

        public void a() {
            this.c.f.b(this.a, this.b);
        }
    }

    /* compiled from: InternalAgent */
    /* renamed from: com.umeng.analytics.d.5 */
    class AnonymousClass5 extends g {
        final /* synthetic */ String a;
        final /* synthetic */ HashMap b;
        final /* synthetic */ String c;
        final /* synthetic */ d d;

        AnonymousClass5(d dVar, String str, HashMap hashMap, String str2) {
            this.d = dVar;
            this.a = str;
            this.b = hashMap;
            this.c = str2;
        }

        public void a() {
            this.d.f.a(this.a, this.b, this.c);
        }
    }

    /* compiled from: InternalAgent */
    /* renamed from: com.umeng.analytics.d.6 */
    class AnonymousClass6 extends g {
        final /* synthetic */ String a;
        final /* synthetic */ String b;
        final /* synthetic */ d c;

        AnonymousClass6(d dVar, String str, String str2) {
            this.c = dVar;
            this.a = str;
            this.b = str2;
        }

        public void a() {
            this.c.f.c(this.a, this.b);
        }
    }

    /* compiled from: InternalAgent */
    /* renamed from: com.umeng.analytics.d.7 */
    class AnonymousClass7 extends g {
        final /* synthetic */ String a;
        final /* synthetic */ String b;
        final /* synthetic */ d c;

        AnonymousClass7(d dVar, String str, String str2) {
            this.c = dVar;
            this.a = str;
            this.b = str2;
        }

        public void a() {
            String[] a = e.a(this.c.a);
            if (a == null || !this.a.equals(a[0]) || !this.b.equals(a[1])) {
                boolean e = this.c.a().e(this.c.a);
                l.a(this.c.a).c();
                if (e) {
                    this.c.a().f(this.c.a);
                }
                e.a(this.c.a, this.a, this.b);
            }
        }
    }

    d() {
        this.a = null;
        this.c = new n();
        this.d = new ad();
        this.e = new z();
        this.h = false;
        this.c.a((v) this);
    }

    private void e(Context context) {
        if (!this.h) {
            this.a = context.getApplicationContext();
            this.f = new o(this.a);
            this.g = l.a(this.a);
            this.h = true;
        }
    }

    void a(String str) {
        if (!AnalyticsConfig.ACTIVITY_DURATION_OPEN) {
            try {
                this.d.a(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void b(String str) {
        if (!AnalyticsConfig.ACTIVITY_DURATION_OPEN) {
            try {
                this.d.b(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void a(c cVar) {
        this.b = cVar;
    }

    public void a(int i) {
        AnalyticsConfig.mVerticalType = i;
    }

    public void a(String str, String str2) {
        AnalyticsConfig.mWrapperType = str;
        AnalyticsConfig.mWrapperVersion = str2;
    }

    void a(Context context) {
        if (context == null) {
            bt.b(a.e, "unexpected null context in onResume");
            return;
        }
        if (AnalyticsConfig.ACTIVITY_DURATION_OPEN) {
            this.d.a(context.getClass().getName());
        }
        try {
            if (!this.h) {
                e(context);
            }
            f.a(new AnonymousClass1(this, context));
        } catch (Exception e) {
            bt.b(a.e, "Exception occurred in Mobclick.onResume(). ", e);
        }
    }

    void b(Context context) {
        if (context == null) {
            bt.b(a.e, "unexpected null context in onPause");
            return;
        }
        if (AnalyticsConfig.ACTIVITY_DURATION_OPEN) {
            this.d.b(context.getClass().getName());
        }
        try {
            if (!this.h) {
                e(context);
            }
            f.a(new AnonymousClass2(this, context));
        } catch (Exception e) {
            bt.b(a.e, "Exception occurred in Mobclick.onRause(). ", e);
        }
    }

    public z a() {
        return this.e;
    }

    public void a(Context context, String str, HashMap<String, Object> hashMap) {
        try {
            if (!this.h) {
                e(context);
            }
            this.f.a(str, (Map) hashMap);
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    void a(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            if (context == null) {
                bt.b(a.e, "unexpected null context in reportError");
                return;
            }
            try {
                if (!this.h) {
                    e(context);
                }
                this.g.a(new ag(str).a(false));
            } catch (Exception e) {
                bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
            }
        }
    }

    void a(Context context, Throwable th) {
        if (context != null && th != null) {
            try {
                if (!this.h) {
                    e(context);
                }
                this.g.a(new ag(th).a(false));
            } catch (Exception e) {
                bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
            }
        }
    }

    private void f(Context context) {
        this.e.c(context);
        if (this.b != null) {
            this.b.a();
        }
    }

    private void g(Context context) {
        this.e.d(context);
        this.d.a(context);
        if (this.b != null) {
            this.b.b();
        }
        this.g.b();
    }

    void c(Context context) {
        try {
            if (!this.h) {
                e(context);
            }
            this.g.a();
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    public void a(Context context, String str, String str2, long j, int i) {
        try {
            if (!this.h) {
                e(context);
            }
            this.f.a(str, str2, j, i);
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    void a(Context context, String str, Map<String, Object> map, long j) {
        try {
            if (!this.h) {
                e(context);
            }
            this.f.a(str, (Map) map, j);
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    void a(Context context, String str, String str2) {
        try {
            if (!this.h) {
                e(context);
            }
            f.a(new AnonymousClass3(this, str, str2));
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    void b(Context context, String str, String str2) {
        try {
            f.a(new AnonymousClass4(this, str, str2));
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    void a(Context context, String str, HashMap<String, Object> hashMap, String str2) {
        try {
            if (!this.h) {
                e(context);
            }
            f.a(new AnonymousClass5(this, str, hashMap, str2));
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    void c(Context context, String str, String str2) {
        try {
            f.a(new AnonymousClass6(this, str, str2));
        } catch (Exception e) {
            bt.b(a.e, Table.STRING_DEFAULT_VALUE, e);
        }
    }

    void d(Context context) {
        try {
            this.d.a();
            g(context);
            x.a(context).edit().commit();
            f.a();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void a(Throwable th) {
        try {
            this.d.a();
            if (this.a != null) {
                if (!(th == null || this.g == null)) {
                    this.g.b(new ag(th));
                }
                g(this.a);
                x.a(this.a).edit().commit();
            }
            f.a();
        } catch (Exception e) {
            bt.b(a.e, "Exception in onAppCrash", e);
        }
    }

    void b(String str, String str2) {
        try {
            f.a(new AnonymousClass7(this, str, str2));
        } catch (Exception e) {
            bt.b(a.e, " Excepthon  in  onProfileSignIn", e);
        }
    }

    void b() {
        try {
            f.a(new g() {
                final /* synthetic */ d a;

                {
                    this.a = r1;
                }

                public void a() {
                    String[] a = e.a(this.a.a);
                    if (a != null && !TextUtils.isEmpty(a[0]) && !TextUtils.isEmpty(a[1])) {
                        boolean e = this.a.a().e(this.a.a);
                        l.a(this.a.a).c();
                        if (e) {
                            this.a.a().f(this.a.a);
                        }
                        e.b(this.a.a);
                    }
                }
            });
        } catch (Exception e) {
            bt.b(a.e, " Excepthon  in  onProfileSignOff", e);
        }
    }
}
