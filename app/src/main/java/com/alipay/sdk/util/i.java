package com.alipay.sdk.util;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.alipay.android.app.IAlixPay.Stub;

final class i implements ServiceConnection {
    final /* synthetic */ h a;

    i(h hVar) {
        this.a = hVar;
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        this.a.c = null;
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this.a.d) {
            this.a.c = Stub.asInterface(iBinder);
            this.a.d.notify();
        }
    }
}
