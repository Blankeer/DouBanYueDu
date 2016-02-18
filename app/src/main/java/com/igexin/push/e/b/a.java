package com.igexin.push.e.b;

import com.igexin.a.a.b.d;
import com.igexin.push.core.a.e;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class a extends f {
    private static a b;
    private List a;

    private a() {
        super(360000);
        this.z = true;
        this.a = new ArrayList();
    }

    public static a g() {
        if (b == null) {
            b = new a();
        }
        return b;
    }

    private void h() {
        a(360000, TimeUnit.MILLISECONDS);
    }

    protected void a() {
        e.a().B();
        for (d dVar : this.a) {
            if (dVar.b()) {
                dVar.a();
                dVar.a(System.currentTimeMillis());
            }
        }
        h();
        d.c().a((Object) this);
    }

    public boolean a(d dVar) {
        return (this.a == null || this.a.contains(dVar)) ? false : this.a.add(dVar);
    }

    public int b() {
        return 0;
    }
}
