package com.igexin.a.a.b.a.a;

import java.util.Comparator;

class f implements Comparator {
    final /* synthetic */ e a;

    f(e eVar) {
        this.a = eVar;
    }

    public int a(g gVar, g gVar2) {
        return gVar == null ? 1 : gVar2 == null ? -1 : ((long) gVar.J) + gVar.H <= ((long) gVar2.J) + gVar2.H ? ((long) gVar.J) + gVar.H < ((long) gVar2.J) + gVar2.H ? -1 : 0 : 1;
    }

    public /* synthetic */ int compare(Object obj, Object obj2) {
        return a((g) obj, (g) obj2);
    }
}
