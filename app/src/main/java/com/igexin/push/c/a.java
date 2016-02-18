package com.igexin.push.c;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.igexin.a.a.b.a.a.g;
import com.igexin.a.a.b.c;
import com.igexin.a.a.b.f;
import com.igexin.a.a.d.a.b;

public class a implements b {
    public ConnectivityManager a;
    public Context b;

    public a(Context context, ConnectivityManager connectivityManager) {
        this.a = connectivityManager;
        this.b = context;
    }

    public f a(String str, Integer num, c cVar) {
        if (!str.startsWith("socket")) {
            return str.startsWith("disConnect") ? new com.igexin.a.a.b.a.a.c(str) : null;
        } else {
            if (this.a == null) {
                return null;
            }
            NetworkInfo activeNetworkInfo = this.a.getActiveNetworkInfo();
            return (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) ? null : new g(str, cVar);
        }
    }
}
