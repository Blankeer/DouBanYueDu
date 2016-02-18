package com.igexin.push.d;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.igexin.push.c.c.e;
import com.igexin.push.c.c.l;
import com.igexin.push.core.d;
import com.igexin.push.core.f;
import com.igexin.sdk.aidl.c;
import java.util.List;

class g implements ServiceConnection {
    final /* synthetic */ b a;
    final /* synthetic */ String b;
    final /* synthetic */ c c;

    g(c cVar, b bVar, String str) {
        this.c = cVar;
        this.a = bVar;
        this.b = str;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (this.c.b == d.active) {
            try {
                this.a.a(c.a(iBinder));
                this.c.g.put(this.b, this.a);
                if (this.a.c().onASNLConnected(this.a.a(), this.a.b(), this.b, 0) == -1) {
                    this.c.g.remove(this.b);
                } else if (com.igexin.push.core.g.m) {
                    this.a.c().onASNLNetworkConnected();
                }
            } catch (Exception e) {
                this.c.g.remove(this.b);
            }
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        if (this.c.b == d.active) {
            b bVar = (b) this.c.g.get(this.b);
            this.c.g.remove(this.b);
            List a = this.c.c(bVar.e());
            if (a.size() != 0) {
                for (int i = 0; i < a.size(); i++) {
                    String str = (String) a.get(i);
                    if (str.startsWith("S-")) {
                        e lVar = new l();
                        lVar.a = Long.valueOf(str.substring(2)).longValue();
                        f.a().g().a("S-" + String.valueOf(lVar.a), lVar, true);
                    }
                    this.c.h.remove(str);
                }
            }
        }
    }
}
