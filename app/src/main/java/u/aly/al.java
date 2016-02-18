package u.aly;

import android.content.Context;
import com.alipay.security.mobile.module.deviceinfo.constant.a;
import java.util.Arrays;

/* compiled from: Defcon */
public class al implements w {
    private static final int a = 0;
    private static final int b = 1;
    private static final int c = 2;
    private static final int d = 3;
    private static final long e = 14400000;
    private static final long f = 28800000;
    private static final long g = 86400000;
    private static al j;
    private int h;
    private final long i;

    static {
        j = null;
    }

    public static synchronized al a(Context context) {
        al alVar;
        synchronized (al.class) {
            if (j == null) {
                j = new al();
                j.a(f.a(context).b().a((int) a));
            }
            alVar = j;
        }
        return alVar;
    }

    private al() {
        this.h = a;
        this.i = 60000;
    }

    public bp a(Context context, bp bpVar) {
        if (bpVar == null) {
            return null;
        }
        if (this.h == b) {
            bpVar.a(null);
            return bpVar;
        } else if (this.h == c) {
            bn[] bnVarArr = new bn[b];
            bnVarArr[a] = b(context);
            bpVar.b(Arrays.asList(bnVarArr));
            bpVar.a(null);
            return bpVar;
        } else if (this.h != d) {
            return bpVar;
        } else {
            bpVar.b(null);
            bpVar.a(null);
            return bpVar;
        }
    }

    public bn b(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        bn bnVar = new bn();
        bnVar.a(z.g(context));
        bnVar.a(currentTimeMillis);
        bnVar.b(currentTimeMillis + 60000);
        bnVar.c(60000);
        return bnVar;
    }

    public long a() {
        switch (this.h) {
            case b /*1*/:
                return e;
            case c /*2*/:
                return f;
            case d /*3*/:
                return g;
            default:
                return 0;
        }
    }

    public long b() {
        return this.h == 0 ? 0 : a.b;
    }

    public void a(int i) {
        if (i >= 0 && i <= d) {
            this.h = i;
        }
    }

    public boolean c() {
        return this.h != 0;
    }

    public void a(f.a aVar) {
        a(aVar.a((int) a));
    }
}
