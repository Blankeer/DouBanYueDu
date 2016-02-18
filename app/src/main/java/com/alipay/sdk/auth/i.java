package com.alipay.sdk.auth;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import com.alipay.sdk.data.e;
import com.alipay.sdk.exception.NetErrorException;
import com.alipay.sdk.net.d;
import com.alipay.sdk.protocol.a;
import com.alipay.sdk.protocol.b;
import com.alipay.sdk.protocol.c;
import com.sina.weibo.sdk.constant.WBConstants;

final class i implements Runnable {
    final /* synthetic */ d a;
    final /* synthetic */ Activity b;
    final /* synthetic */ e c;
    final /* synthetic */ APAuthInfo d;

    i(d dVar, Activity activity, e eVar, APAuthInfo aPAuthInfo) {
        this.a = dVar;
        this.b = activity;
        this.c = eVar;
        this.d = aPAuthInfo;
    }

    public final void run() {
        c cVar = null;
        try {
            cVar = this.a.a(this.b, this.c, false);
        } catch (NetErrorException e) {
        }
        try {
            if (h.c != null) {
                h.c.c();
                h.c = null;
            }
            if (cVar == null) {
                h.d = this.d.getRedirectUri() + "?resultCode=202";
                h.a(this.b, h.d);
                if (h.c != null) {
                    h.c.c();
                    return;
                }
                return;
            }
            for (a aVar : a.a(b.a(cVar.c.optJSONObject(com.alipay.sdk.cons.c.c), com.alipay.sdk.cons.c.d))) {
                if (aVar == a.WapPay) {
                    h.d = com.alipay.sdk.util.a.a(aVar.h)[0];
                    break;
                }
            }
            if (TextUtils.isEmpty(h.d)) {
                h.d = this.d.getRedirectUri() + "?resultCode=202";
                h.a(this.b, h.d);
                if (h.c != null) {
                    h.c.c();
                    return;
                }
                return;
            }
            Intent intent = new Intent(this.b, AuthActivity.class);
            intent.putExtra(com.alipay.sdk.cons.c.g, h.d);
            intent.putExtra(WBConstants.SSO_REDIRECT_URL, this.d.getRedirectUri());
            this.b.startActivity(intent);
            if (h.c != null) {
                h.c.c();
            }
        } catch (Exception e2) {
            if (h.c != null) {
                h.c.c();
            }
        } catch (Throwable th) {
            if (h.c != null) {
                h.c.c();
            }
        }
    }
}
