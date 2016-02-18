package com.alipay.sdk.util;

import android.util.Log;
import com.alipay.sdk.net.b;
import com.alipay.sdk.net.c;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

final class e implements Runnable {
    final /* synthetic */ String a;

    e(String str) {
        this.a = str;
    }

    public final void run() {
        synchronized (d.b) {
            try {
                List arrayList = new ArrayList(1);
                arrayList.add(new BasicNameValuePair("errorTag", this.a));
                d.c.setEntity(new UrlEncodedFormEntity(arrayList));
                b.a();
                b.a();
                Log.d("Http Post Response:", c.a(b.b.a(d.c)));
            } catch (Throwable th) {
            }
        }
    }
}
