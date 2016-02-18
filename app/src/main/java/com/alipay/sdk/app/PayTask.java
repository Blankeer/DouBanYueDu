package com.alipay.sdk.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.alipay.sdk.data.c;
import com.alipay.sdk.data.d;
import com.alipay.sdk.exception.NetErrorException;
import com.alipay.sdk.sys.b;
import com.alipay.sdk.util.h;
import com.alipay.sdk.util.k;
import com.alipay.sdk.widget.a;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import io.realm.internal.Table;
import org.json.JSONObject;

public class PayTask {
    static final Object a;
    private Activity b;
    private a c;

    static {
        a = h.class;
    }

    public PayTask(Activity activity) {
        this.c = null;
        this.b = activity;
    }

    public synchronized String pay(String str) {
        String a;
        h hVar = new h(this.b);
        b.a().a(this.b, d.a());
        if (!str.contains("bizcontext=")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("&bizcontext=\"");
            stringBuilder.append(new com.alipay.sdk.sys.a(this.b).toString());
            stringBuilder.append("\"");
            str = stringBuilder.toString();
        }
        if (!str.contains("paymethod=\"expressGateway\"") && k.b(this.b)) {
            a = hVar.a(str);
            if (!TextUtils.equals(a, h.b)) {
                if (TextUtils.isEmpty(a)) {
                    a = l.a();
                }
                hVar.a = null;
            }
        }
        a = a(str);
        hVar.a = null;
        return a;
    }

    private String a(String str, h hVar) {
        b.a().a(this.b, d.a());
        if (!str.contains("bizcontext=")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("&bizcontext=\"");
            stringBuilder.append(new com.alipay.sdk.sys.a(this.b).toString());
            stringBuilder.append("\"");
            str = stringBuilder.toString();
        }
        if (str.contains("paymethod=\"expressGateway\"")) {
            return a(str);
        }
        if (!k.b(this.b)) {
            return a(str);
        }
        String a = hVar.a(str);
        if (TextUtils.equals(a, h.b)) {
            return a(str);
        }
        if (TextUtils.isEmpty(a)) {
            return l.a();
        }
        return a;
    }

    public String getVersion() {
        return com.alipay.sdk.cons.a.h;
    }

    public boolean checkAccountIfExist() {
        boolean z = false;
        try {
            z = new com.alipay.sdk.net.d().a(this.b, com.alipay.sdk.data.b.a(), true).c.optBoolean("hasAccount", false);
        } catch (Exception e) {
        }
        return z;
    }

    private String a(String str) {
        try {
            if (!(this.b == null || this.b.isFinishing())) {
                this.c = new a(this.b);
                this.c.b();
                b.a().a(this.b, d.a());
            }
        } catch (Exception e) {
            this.c = null;
        }
        return b(str);
    }

    private String b(String str) {
        m mVar;
        int i = 0;
        com.alipay.sdk.tid.a aVar;
        try {
            com.alipay.sdk.protocol.a[] a = com.alipay.sdk.protocol.a.a(com.alipay.sdk.protocol.b.a(new com.alipay.sdk.net.d(new c()).a(this.b, com.alipay.sdk.data.b.a(new c(), str, new JSONObject()), false).c.optJSONObject(com.alipay.sdk.cons.c.c), com.alipay.sdk.cons.c.d));
            for (com.alipay.sdk.protocol.a aVar2 : a) {
                if (aVar2 == com.alipay.sdk.protocol.a.Update) {
                    String[] a2 = com.alipay.sdk.util.a.a(aVar2.h);
                    if (a2.length == 3 && TextUtils.equals(com.alipay.sdk.cons.b.c, a2[0])) {
                        Context context = b.a().a;
                        com.alipay.sdk.tid.b a3 = com.alipay.sdk.tid.b.a();
                        if (!(TextUtils.isEmpty(a2[1]) || TextUtils.isEmpty(a2[2]))) {
                            a3.a = a2[1];
                            a3.b = a2[2];
                            aVar = new com.alipay.sdk.tid.a(context);
                            aVar.a(com.alipay.sdk.util.b.a(context).a(), com.alipay.sdk.util.b.a(context).b(), a3.a, a3.b);
                            aVar.close();
                        }
                    }
                }
            }
            if (this.c != null) {
                this.c.c();
            }
            int length = a.length;
            while (i < length) {
                com.alipay.sdk.protocol.a aVar3 = a[i];
                if (aVar3 == com.alipay.sdk.protocol.a.WapPay) {
                    String a4 = a(aVar3);
                    if (this.c == null) {
                        return a4;
                    }
                    this.c.c();
                    return a4;
                }
                i++;
            }
            if (this.c != null) {
                this.c.c();
                mVar = null;
                if (mVar == null) {
                    mVar = m.a(m.FAILED.g);
                }
                return l.a(mVar.g, mVar.h, Table.STRING_DEFAULT_VALUE);
            }
        } catch (Exception e) {
            aVar.close();
        } catch (NetErrorException e2) {
            mVar = m.a(m.NETWORK_ERROR.g);
            if (this.c != null) {
                this.c.c();
            }
        } catch (Exception e3) {
            if (this.c != null) {
                this.c.c();
                mVar = null;
            }
        } catch (Throwable th) {
            if (this.c != null) {
                this.c.c();
            }
        }
        mVar = null;
        if (mVar == null) {
            mVar = m.a(m.FAILED.g);
        }
        return l.a(mVar.g, mVar.h, Table.STRING_DEFAULT_VALUE);
    }

    private String a(com.alipay.sdk.protocol.a aVar) {
        String[] a = com.alipay.sdk.util.a.a(aVar.h);
        Intent intent = new Intent(this.b, H5PayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ShareUrlEditFragment_.URL_ARG, a[0]);
        if (a.length == 2) {
            bundle.putString("cookie", a[1]);
        }
        intent.putExtras(bundle);
        this.b.startActivity(intent);
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
}
