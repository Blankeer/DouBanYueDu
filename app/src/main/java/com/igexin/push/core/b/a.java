package com.igexin.push.core.b;

import android.content.ServiceConnection;
import com.igexin.sdk.aidl.ICACallback;

public class a {
    private String a;
    private ServiceConnection b;
    private ICACallback c;

    public ServiceConnection a() {
        return this.b;
    }

    public void a(ServiceConnection serviceConnection) {
        this.b = serviceConnection;
    }

    public void a(ICACallback iCACallback) {
        this.c = iCACallback;
    }

    public void a(String str) {
        this.a = str;
    }

    public ICACallback b() {
        return this.c;
    }
}
