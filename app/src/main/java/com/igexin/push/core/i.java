package com.igexin.push.core;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.douban.book.reader.helper.AppUri;
import com.igexin.push.config.l;
import com.umeng.analytics.a;
import u.aly.dx;

public class i {
    private static i e;
    public long a;
    private l b;
    private long c;
    private ConnectivityManager d;

    private i() {
        this.a = 240000;
        this.b = l.DETECT;
        this.c = 0;
        this.d = f.a().j();
    }

    public static i a() {
        if (e == null) {
            e = new i();
        }
        return e;
    }

    public long a(long j, long j2) {
        return j > j2 ? j : j2;
    }

    public void a(long j) {
        this.a = j;
    }

    public void a(k kVar) {
        switch (j.b[this.b.ordinal()]) {
            case dx.b /*1*/:
                switch (j.a[kVar.ordinal()]) {
                    case dx.b /*1*/:
                        a(b(this.a + 60000, 420000));
                        a(l.DETECT);
                    case dx.c /*2*/:
                    case dx.d /*3*/:
                        this.c++;
                        if (this.c >= 2) {
                            a(a(this.a - 60000, 240000));
                            a(l.STABLE);
                        }
                    case dx.e /*4*/:
                        a(240000);
                        a(l.DETECT);
                    default:
                }
            case dx.c /*2*/:
                switch (j.a[kVar.ordinal()]) {
                    case dx.b /*1*/:
                        a(l.STABLE);
                    case dx.c /*2*/:
                    case dx.d /*3*/:
                        a(a(this.a - 60000, 240000));
                        this.c++;
                        if (this.c >= 2) {
                            a(240000);
                            a(l.PENDING);
                        }
                    case dx.e /*4*/:
                        a(240000);
                        a(l.DETECT);
                    default:
                }
            case dx.d /*3*/:
                switch (j.a[kVar.ordinal()]) {
                    case dx.b /*1*/:
                        a(240000);
                        a(l.DETECT);
                    case dx.c /*2*/:
                    case dx.d /*3*/:
                        a(l.PENDING);
                    case dx.e /*4*/:
                        a(240000);
                        a(l.DETECT);
                    default:
                }
            default:
        }
    }

    public void a(l lVar) {
        this.b = lVar;
        this.c = 0;
    }

    public long b() {
        long j = this.a;
        if (l.d > 0) {
            j = (long) (l.d * AppUri.OPEN_URL);
        }
        NetworkInfo activeNetworkInfo = this.d.getActiveNetworkInfo();
        return (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) ? a.h : !g.m ? a.h : !f.a().g().a() ? a.h : j;
    }

    public long b(long j, long j2) {
        return j < j2 ? j : j2;
    }
}
