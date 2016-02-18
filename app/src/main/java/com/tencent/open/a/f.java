package com.tencent.open.a;

import android.os.Environment;
import android.text.TextUtils;
import com.tencent.open.a.d.a;
import com.tencent.open.a.d.c;
import com.tencent.open.a.d.d;
import com.tencent.open.utils.Global;
import java.io.File;

/* compiled from: ProGuard */
public class f {
    public static f a;
    protected static final b c;
    protected a b;

    static {
        a = null;
        c = new b(c(), c.r, c.l, c.m, c.g, (long) c.n, 10, c.j, c.s);
    }

    public static f a() {
        if (a == null) {
            synchronized (f.class) {
                if (a == null) {
                    a = new f();
                }
            }
        }
        return a;
    }

    private f() {
        this.b = new a(c);
    }

    protected void a(int i, String str, String str2, Throwable th) {
        e.a.b(i, Thread.currentThread(), System.currentTimeMillis(), str, str2, th);
        if (a.a(c.b, i) && this.b != null) {
            this.b.b(i, Thread.currentThread(), System.currentTimeMillis(), str, str2, th);
        }
    }

    public static final void a(String str, String str2) {
        a().a(1, str, str2, null);
    }

    public static final void b(String str, String str2) {
        a().a(2, str, str2, null);
    }

    public static final void a(String str, String str2, Throwable th) {
        a().a(2, str, str2, th);
    }

    public static final void c(String str, String str2) {
        a().a(4, str, str2, null);
    }

    public static final void d(String str, String str2) {
        a().a(8, str, str2, null);
    }

    public static final void e(String str, String str2) {
        a().a(16, str, str2, null);
    }

    public static final void b(String str, String str2, Throwable th) {
        a().a(16, str, str2, th);
    }

    public static void b() {
        synchronized (f.class) {
            a().d();
            if (a != null) {
                a = null;
            }
        }
    }

    protected static File c() {
        Object obj;
        String packageName = Global.getPackageName();
        if (TextUtils.isEmpty(packageName)) {
            packageName = "default";
        }
        String str = c.h + File.separator + packageName;
        d b = c.b();
        if (b == null || b.c() <= c.k) {
            obj = null;
        } else {
            obj = 1;
        }
        if (obj != null) {
            return new File(Environment.getExternalStorageDirectory(), str);
        }
        return new File(Global.getFilesDir(), str);
    }

    protected void d() {
        if (this.b != null) {
            this.b.a();
            this.b.b();
            this.b = null;
        }
    }
}
