package com.igexin.push.core.d;

import com.igexin.push.c.c.a;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.g;
import java.util.Timer;
import java.util.TimerTask;

class e extends TimerTask {
    final /* synthetic */ PushTaskBean a;
    final /* synthetic */ a b;
    final /* synthetic */ c c;

    e(c cVar, PushTaskBean pushTaskBean, a aVar) {
        this.c = cVar;
        this.a = pushTaskBean;
        this.b = aVar;
    }

    public void run() {
        if (g.ak.containsKey(this.a.getTaskId())) {
            ((Timer) g.ak.get(this.a.getTaskId())).cancel();
            g.ak.remove(this.a.getTaskId());
        }
        this.c.a(this.a, this.b);
        this.b.b(this.b.c() + 1);
    }
}
