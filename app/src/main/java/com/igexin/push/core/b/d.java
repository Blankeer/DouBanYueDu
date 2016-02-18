package com.igexin.push.core.b;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.push.core.f;
import com.igexin.sdk.aidl.a;

class d implements ServiceConnection {
    final /* synthetic */ c a;

    d(c cVar) {
        this.a = cVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        b.a().a(componentName.getPackageName()).a(a.a(iBinder));
        Message message = new Message();
        Intent intent = new Intent();
        intent.putExtra("pkgname", componentName.getPackageName());
        String a = this.a.a(componentName.getPackageName());
        if (a == null) {
            intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "disconnected");
        } else if (e.a().a(componentName.getPackageName(), a)) {
            intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "connected");
        } else {
            intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "disconnected");
        }
        message.what = com.igexin.push.core.a.d;
        message.obj = intent;
        f.a().a(message);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        b.a().a(componentName.getPackageName()).a(null);
    }
}
