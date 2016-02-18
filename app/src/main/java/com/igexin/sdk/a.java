package com.igexin.sdk;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

class a implements ServiceConnection {
    final /* synthetic */ PushService a;

    a(PushService pushService) {
        this.a = pushService;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        com.igexin.a.a.c.a.b(PushService.a + " bind user process service success");
        this.a.e = true;
    }

    public void onServiceDisconnected(ComponentName componentName) {
        com.igexin.a.a.c.a.b(PushService.a + " user process service onServiceDisconnected");
        this.a.e = false;
        if (this.a.d) {
            com.igexin.a.a.c.a.b(PushService.a + " stop user process service by getui, onServiceDisconnected, not restart");
            this.a.d = false;
            return;
        }
        com.igexin.a.a.c.a.b(PushService.a + " stop user process service by usr, need restart #########");
        this.a.bindService(new Intent(this.a, PushServiceUser.class), this.a.f, 1);
    }
}
