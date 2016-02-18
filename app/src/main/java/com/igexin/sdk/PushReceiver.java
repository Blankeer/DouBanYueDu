package com.igexin.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.a.c.a;
import com.igexin.push.core.a.e;
import com.igexin.sdk.a.d;
import com.tencent.connect.common.Constants;

public class PushReceiver extends BroadcastReceiver {
    private static String a;

    static {
        a = "PushSdk";
    }

    public void onReceive(Context context, Intent intent) {
        String e = e.a().e("ss");
        if (e == null || !e.equals(Constants.VIA_TO_TYPE_QQ_GROUP) || new d(context).c()) {
            String verifyCode;
            e = intent.getAction();
            if (PushConsts.ACTION_BROADCAST_PUSHMANAGER.equals(e)) {
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    return;
                }
                if (extras.containsKey("verifyCode")) {
                    verifyCode = PushManager.getInstance().getVerifyCode();
                    if (verifyCode == null || verifyCode.equals(extras.getString("verifyCode"))) {
                        Intent intent2 = new Intent(context.getApplicationContext(), PushService.class);
                        intent2.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.ACTION_BROADCAST_PUSHMANAGER);
                        intent2.putExtra("bundle", intent.getExtras());
                        context.getApplicationContext().startService(intent2);
                    } else {
                        a.b(a + " bundle verifyCode : " + extras.getString("verifyCode") + " != " + verifyCode);
                        return;
                    }
                }
                a.b(a + " bundle not contains verifyCode, ignore broadcast ####");
                return;
            }
            if (PushConsts.ACTION_BROADCAST_REFRESHLS.equals(e)) {
                String stringExtra = intent.getStringExtra("callback_pkgname");
                verifyCode = intent.getStringExtra("callback_classname");
                Intent intent3 = new Intent(context.getApplicationContext(), PushService.class);
                intent3.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.ACTION_BROADCAST_REFRESHLS);
                intent3.putExtra("callback_pkgname", stringExtra);
                intent3.putExtra("callback_classname", verifyCode);
                context.getApplicationContext().startService(intent3);
            }
            if (PushConsts.ACTION_BROADCAST_TO_BOOT.equals(e)) {
                context.startService(new Intent(context.getApplicationContext(), PushService.class));
            } else if (PushConsts.ACTION_BROADCAST_NETWORK_CHANGE.equals(e)) {
                r0 = new Intent(context.getApplicationContext(), PushService.class);
                r0.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.ACTION_BROADCAST_NETWORK_CHANGE);
                context.startService(r0);
            } else if (PushConsts.ACTION_BROADCAST_USER_PRESENT.equals(e)) {
                r0 = new Intent(context.getApplicationContext(), PushService.class);
                r0.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.ACTION_BROADCAST_USER_PRESENT);
                context.startService(r0);
            }
        }
    }
}
