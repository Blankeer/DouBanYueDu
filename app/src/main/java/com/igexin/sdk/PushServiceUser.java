package com.igexin.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.igexin.a.a.c.a;
import com.igexin.sdk.aidl.e;

public class PushServiceUser extends Service {
    private static String a;
    private final e b;

    static {
        a = "GeTuiService";
    }

    public PushServiceUser() {
        this.b = new b(this);
    }

    public IBinder onBind(Intent intent) {
        return this.b;
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onLowMemory() {
        a.b(a + " PushServiceUser Low Memory!");
        super.onLowMemory();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        return 1;
    }
}
