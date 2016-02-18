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

public class PushManagerReceiver extends BroadcastReceiver {
    private static String a;

    static {
        a = "PushSdk";
    }

    public void onReceive(Context context, Intent intent) {
        String e = e.a().e("ss");
        if (e == null || !e.equals(Constants.VIA_TO_TYPE_QQ_GROUP) || new d(context).c()) {
            if (PushConsts.ACTION_BROADCAST_PUSHMANAGER.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    return;
                }
                if (extras.containsKey("verifyCode")) {
                    String verifyCode = PushManager.getInstance().getVerifyCode();
                    if (verifyCode == null || verifyCode.equals(extras.getString("verifyCode"))) {
                        Intent intent2 = new Intent(context.getApplicationContext(), PushService.class);
                        intent2.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.ACTION_BROADCAST_PUSHMANAGER);
                        intent2.putExtra("bundle", intent.getExtras());
                        context.getApplicationContext().startService(intent2);
                        return;
                    }
                    a.b(a + " bundle verifyCode : " + extras.getString("verifyCode") + " != " + verifyCode);
                    return;
                }
                a.b(a + " bundle not contains verifyCode, ignore broadcast ####");
            }
        }
    }
}
