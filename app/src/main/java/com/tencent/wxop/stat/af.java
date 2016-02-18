package com.tencent.wxop.stat;

import android.content.Context;
import com.douban.book.reader.helper.AppUri;
import com.tencent.wxop.stat.b.l;
import java.util.Timer;
import java.util.TimerTask;

public class af {
    private static volatile af dd;
    private Timer dc;
    private Context h;

    static {
        dd = null;
    }

    private af(Context context) {
        this.dc = null;
        this.h = null;
        this.h = context.getApplicationContext();
        this.dc = new Timer(false);
    }

    public static af Y(Context context) {
        if (dd == null) {
            synchronized (af.class) {
                if (dd == null) {
                    dd = new af(context);
                }
            }
        }
        return dd;
    }

    public final void ah() {
        if (c.j() == d.PERIOD) {
            long u = (long) ((c.u() * 60) * AppUri.OPEN_URL);
            if (c.k()) {
                l.av().b("setupPeriodTimer delay:" + u);
            }
            TimerTask agVar = new ag(this);
            if (this.dc != null) {
                if (c.k()) {
                    l.av().b("setupPeriodTimer schedule delay:" + u);
                }
                this.dc.schedule(agVar, u);
            } else if (c.k()) {
                l.av().c("setupPeriodTimer schedule timer == null");
            }
        }
    }
}
