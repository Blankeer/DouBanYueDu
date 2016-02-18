package com.igexin.push.d;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.igexin.push.core.d;
import com.igexin.sdk.aidl.c;

class f implements ServiceConnection {
    final /* synthetic */ c a;

    f(c cVar) {
        this.a = cVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (this.a.b == d.prepare) {
            this.a.e.a(c.a(iBinder));
            a aVar = new a();
            aVar.a(com.igexin.push.core.c.connectASNL);
            this.a.a(aVar);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        if (this.a.b == d.passive) {
            com.igexin.push.core.f.a().g().b(true);
            this.a.c();
        }
    }
}
