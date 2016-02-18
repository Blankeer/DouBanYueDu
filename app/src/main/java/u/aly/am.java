package u.aly;

import android.content.Context;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.b;
import com.umeng.analytics.h;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import u.aly.f.a;

/* compiled from: ImLatent */
public class am implements w {
    private static am l;
    private final long a;
    private final long b;
    private final int c;
    private final int d;
    private h e;
    private aa f;
    private long g;
    private int h;
    private long i;
    private long j;
    private Context k;

    static {
        l = null;
    }

    public static synchronized am a(Context context, aa aaVar) {
        am amVar;
        synchronized (am.class) {
            if (l == null) {
                l = new am(context, aaVar);
                l.a(f.a(context).b());
            }
            amVar = l;
        }
        return amVar;
    }

    private am(Context context, aa aaVar) {
        this.a = 1296000000;
        this.b = 129600000;
        this.c = 1800000;
        this.d = AbstractSpiCall.DEFAULT_TIMEOUT;
        this.g = 1296000000;
        this.h = AbstractSpiCall.DEFAULT_TIMEOUT;
        this.i = 0;
        this.j = 0;
        this.k = context;
        this.e = h.a(context);
        this.f = aaVar;
    }

    public boolean a() {
        if (this.e.g() || this.f.f()) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis() - this.f.o();
        if (currentTimeMillis > this.g) {
            this.i = (long) b.a(this.h, c.a(this.k));
            this.j = currentTimeMillis;
            return true;
        } else if (currentTimeMillis <= 129600000) {
            return false;
        } else {
            this.i = 0;
            this.j = currentTimeMillis;
            return true;
        }
    }

    public long b() {
        return this.i;
    }

    public long c() {
        return this.j;
    }

    public void a(a aVar) {
        this.g = aVar.a(1296000000);
        int b = aVar.b(0);
        if (b != 0) {
            this.h = b;
        } else if (AnalyticsConfig.sLatentWindow <= 0 || AnalyticsConfig.sLatentWindow > 1800000) {
            this.h = AbstractSpiCall.DEFAULT_TIMEOUT;
        } else {
            this.h = AnalyticsConfig.sLatentWindow;
        }
    }
}
