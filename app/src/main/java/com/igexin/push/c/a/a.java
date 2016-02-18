package com.igexin.push.c.a;

import com.igexin.a.a.b.c;
import com.igexin.a.a.b.e;
import com.igexin.a.a.b.f;
import com.igexin.push.c.c.b;
import com.igexin.push.c.c.h;
import com.igexin.push.c.c.k;
import com.igexin.push.c.c.m;
import com.igexin.push.c.c.n;
import com.igexin.push.c.c.o;
import u.aly.dj;

public class a extends c {
    a(String str, c cVar) {
        super(str, true);
        a(cVar);
    }

    public Object a(f fVar, e eVar, Object obj) {
        if (obj instanceof com.igexin.push.c.c.e) {
            com.igexin.push.c.c.e eVar2 = (com.igexin.push.c.c.e) obj;
            b bVar = new b();
            bVar.b = (byte) eVar2.i;
            bVar.a(eVar2.d());
            bVar.c = eVar2.j;
            return bVar;
        } else if (!(obj instanceof com.igexin.push.c.c.e[])) {
            return null;
        } else {
            com.igexin.push.c.c.e[] eVarArr = (com.igexin.push.c.c.e[]) obj;
            Object obj2 = new b[eVarArr.length];
            for (int i = 0; i < eVarArr.length; i++) {
                obj2[i] = new b();
                obj2[i].b = (byte) eVarArr[i].i;
                obj2[i].a(eVarArr[i].d());
            }
            return obj2;
        }
    }

    public com.igexin.a.a.d.a.f b(f fVar, e eVar, Object obj) {
        com.igexin.push.c.c.e eVar2 = null;
        if (obj == null) {
            return null;
        }
        if (obj instanceof h) {
            return (com.igexin.a.a.d.a.f) obj;
        }
        b bVar = (b) obj;
        switch (bVar.b) {
            case dj.f /*5*/:
                eVar2 = new k();
                break;
            case com.alipay.sdk.protocol.h.h /*9*/:
                eVar2 = new o();
                break;
            case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                eVar2 = new n();
                break;
            case HeaderMapDB.FUN_COMPARATOR_REVERSE /*28*/:
                eVar2 = new com.igexin.push.c.c.a();
                break;
            case HeaderMapDB.HASHER_LONG_ARRAY /*37*/:
                eVar2 = new m();
                break;
        }
        if (eVar2 != null) {
            eVar2.a(bVar.d);
        }
        return eVar2;
    }

    public /* synthetic */ Object c(f fVar, e eVar, Object obj) {
        return b(fVar, eVar, obj);
    }
}
