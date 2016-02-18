package com.igexin.push.core.a;

import com.igexin.push.core.bean.l;
import java.util.Comparator;

class f implements Comparator {
    final /* synthetic */ e a;

    f(e eVar) {
        this.a = eVar;
    }

    public int a(l lVar, l lVar2) {
        return lVar.d() != lVar2.d() ? lVar.d().compareTo(lVar2.d()) : 0;
    }

    public /* synthetic */ int compare(Object obj, Object obj2) {
        return a((l) obj, (l) obj2);
    }
}
