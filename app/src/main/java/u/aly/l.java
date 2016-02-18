package u.aly;

import android.content.Context;
import com.umeng.analytics.f;
import com.umeng.analytics.g;

/* compiled from: CacheService */
public final class l implements p {
    private static l c;
    private p a;
    private Context b;

    /* compiled from: CacheService */
    /* renamed from: u.aly.l.1 */
    class AnonymousClass1 extends g {
        final /* synthetic */ q a;
        final /* synthetic */ l b;

        AnonymousClass1(l lVar, q qVar) {
            this.b = lVar;
            this.a = qVar;
        }

        public void a() {
            this.b.a.a(this.a);
        }
    }

    private l(Context context) {
        this.b = context.getApplicationContext();
        this.a = new k(this.b);
    }

    public static synchronized l a(Context context) {
        l lVar;
        synchronized (l.class) {
            if (c == null && context != null) {
                c = new l(context);
            }
            lVar = c;
        }
        return lVar;
    }

    public void a(p pVar) {
        this.a = pVar;
    }

    public void a(q qVar) {
        f.b(new AnonymousClass1(this, qVar));
    }

    public void b(q qVar) {
        this.a.b(qVar);
    }

    public void a() {
        f.b(new g() {
            final /* synthetic */ l a;

            {
                this.a = r1;
            }

            public void a() {
                this.a.a.a();
            }
        });
    }

    public void b() {
        f.b(new g() {
            final /* synthetic */ l a;

            {
                this.a = r1;
            }

            public void a() {
                this.a.a.b();
            }
        });
    }

    public void c() {
        f.c(new g() {
            final /* synthetic */ l a;

            {
                this.a = r1;
            }

            public void a() {
                this.a.a.c();
            }
        });
    }
}
