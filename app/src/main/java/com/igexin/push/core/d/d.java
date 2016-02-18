package com.igexin.push.core.d;

import com.igexin.push.core.a.e;
import java.util.TimerTask;

class d extends TimerTask {
    final /* synthetic */ c a;

    d(c cVar) {
        this.a = cVar;
    }

    public void run() {
        e.a().a(this.a.b, this.a.c, this.a.d);
        this.a.c.a(this.a.c.a() + 1);
    }
}
