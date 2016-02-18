package com.alipay.sdk.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import com.alipay.sdk.data.c;
import com.alipay.sdk.data.d;
import com.alipay.sdk.data.e;
import com.alipay.sdk.exception.NetErrorException;
import com.alipay.sdk.sys.a;
import com.alipay.sdk.sys.b;
import com.alipay.sdk.util.h;
import com.alipay.sdk.util.k;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import io.realm.internal.Table;
import org.json.JSONObject;

public class AuthTask {
    static final Object a;
    private static final int b = 73;
    private Activity c;

    static {
        a = h.class;
    }

    public AuthTask(Activity activity) {
        this.c = activity;
    }

    public synchronized String auth(String str) {
        String a;
        if (!str.contains("bizcontext=")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("&bizcontext=\"");
            stringBuilder.append(new a(this.c).toString());
            stringBuilder.append("\"");
            str = stringBuilder.toString();
        }
        Context context = this.c;
        if (a(context)) {
            a = new h(context).a(str);
            if (!TextUtils.equals(a, h.b)) {
                if (TextUtils.isEmpty(a)) {
                    a = l.a();
                }
            }
        }
        a = b(context, str);
        return a;
    }

    private String a(Activity activity, String str) {
        if (!a((Context) activity)) {
            return b(activity, str);
        }
        String a = new h(activity).a(str);
        if (TextUtils.equals(a, h.b)) {
            return b(activity, str);
        }
        if (TextUtils.isEmpty(a)) {
            return l.a();
        }
        return a;
    }

    private String b(Activity activity, String str) {
        com.alipay.sdk.widget.a aVar;
        e a;
        int i;
        m mVar;
        if (activity != null) {
            try {
                if (!activity.isFinishing()) {
                    aVar = new com.alipay.sdk.widget.a(activity);
                    aVar.b();
                    b.a().a(this.c, d.a());
                    a = com.alipay.sdk.data.b.a(new c(), str.toString(), new JSONObject());
                    a.a.b = "com.alipay.mobilecashier";
                    a.a.e = "com.alipay.mcpay";
                    a.a.d = "4.0.3";
                    a.a.c = "/cashier/main";
                    com.alipay.sdk.protocol.c a2 = new com.alipay.sdk.net.d(new c()).a((Context) activity, a, false);
                    if (aVar != null) {
                        aVar.c();
                        Object obj = null;
                    }
                    for (com.alipay.sdk.protocol.a aVar2 : com.alipay.sdk.protocol.a.a(com.alipay.sdk.protocol.b.a(a2.c.optJSONObject(com.alipay.sdk.cons.c.c), com.alipay.sdk.cons.c.d))) {
                        if (aVar2 == com.alipay.sdk.protocol.a.WapPay) {
                            return a(aVar2);
                        }
                    }
                    mVar = null;
                    if (mVar == null) {
                        mVar = m.a(m.FAILED.g);
                    }
                    return l.a(mVar.g, mVar.h, Table.STRING_DEFAULT_VALUE);
                }
            } catch (Exception e) {
                aVar = null;
            }
        }
        aVar = null;
        b.a().a(this.c, d.a());
        a = com.alipay.sdk.data.b.a(new c(), str.toString(), new JSONObject());
        a.a.b = "com.alipay.mobilecashier";
        a.a.e = "com.alipay.mcpay";
        a.a.d = "4.0.3";
        a.a.c = "/cashier/main";
        try {
            com.alipay.sdk.protocol.c a22 = new com.alipay.sdk.net.d(new c()).a((Context) activity, a, false);
            if (aVar != null) {
                aVar.c();
                Object obj2 = null;
            }
            for (i = 0; i < r4; i++) {
                if (aVar2 == com.alipay.sdk.protocol.a.WapPay) {
                    return a(aVar2);
                }
            }
            mVar = null;
        } catch (NetErrorException e2) {
            r1 = m.a(m.NETWORK_ERROR.g);
            m a3;
            if (aVar != null) {
                aVar.c();
                mVar = a3;
            } else {
                mVar = a3;
            }
        } catch (Exception e3) {
            if (aVar != null) {
                aVar.c();
                mVar = null;
            } else {
                mVar = null;
            }
        } catch (Throwable th) {
            Throwable th2 = th;
            r1 = aVar;
            Throwable th3 = th2;
            com.alipay.sdk.widget.a aVar3;
            if (aVar3 != null) {
                aVar3.c();
            }
        }
        if (mVar == null) {
            mVar = m.a(m.FAILED.g);
        }
        return l.a(mVar.g, mVar.h, Table.STRING_DEFAULT_VALUE);
    }

    private String a(com.alipay.sdk.protocol.a aVar) {
        String[] a = com.alipay.sdk.util.a.a(aVar.h);
        Bundle bundle = new Bundle();
        bundle.putString(ShareUrlEditFragment_.URL_ARG, a[0]);
        Intent intent = new Intent(this.c, H5AuthActivity.class);
        intent.putExtras(bundle);
        this.c.startActivity(intent);
        synchronized (a) {
            try {
                a.wait();
            } catch (InterruptedException e) {
                return l.a();
            }
        }
        String str = l.a;
        if (TextUtils.isEmpty(str)) {
            return l.a();
        }
        return str;
    }

    private static boolean a(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(k.b, TransportMediator.FLAG_KEY_MEDIA_NEXT);
            if (packageInfo != null && packageInfo.versionCode >= b) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
