package com.igexin.push.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

public class n extends BroadcastReceiver {
    private static n a;

    private n() {
    }

    public static n a() {
        if (a == null) {
            a = new n();
        }
        return a;
    }

    public void onReceive(Context context, Intent intent) {
        if (f.a() != null) {
            Message message = new Message();
            message.what = a.e;
            message.obj = intent;
            f.a().a(message);
        }
    }
}
