package com.alipay.sdk.data;

import android.os.Build;
import android.text.TextUtils;
import com.alipay.sdk.cons.a;
import com.alipay.sdk.encrypt.d;
import com.alipay.sdk.util.c;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.lang.ref.WeakReference;
import java.util.Locale;
import org.json.JSONObject;

public final class e {
    public a a;
    public WeakReference<c> b;
    public boolean c;
    boolean d;
    private JSONObject e;
    private JSONObject f;
    private long g;

    private void a(boolean z) {
        this.d = z;
    }

    private boolean a() {
        return this.d;
    }

    public e(a aVar, JSONObject jSONObject) {
        this(aVar, jSONObject, (byte) 0);
    }

    private e(a aVar, JSONObject jSONObject, byte b) {
        this.b = null;
        this.c = true;
        this.d = true;
        this.a = aVar;
        this.e = jSONObject;
        this.f = null;
        this.b = new WeakReference(null);
    }

    private String b() {
        return this.a.a;
    }

    private void b(boolean z) {
        this.c = z;
    }

    private long c() {
        return this.g;
    }

    private void a(long j) {
        this.g = j;
    }

    private c d() {
        return (c) this.b.get();
    }

    private void a(c cVar) {
        this.b = new WeakReference(cVar);
    }

    private void a(JSONObject jSONObject) {
        this.f = jSONObject;
    }

    public final JSONObject a(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("device", Build.MODEL);
            JSONObject jSONObject3 = new JSONObject();
            jSONObject2 = c.a(jSONObject2, this.f);
            jSONObject2.put(com.alipay.sdk.cons.c.l, this.a.b);
            jSONObject2.put("api_name", this.a.e);
            jSONObject2.put("api_version", this.a.d);
            if (this.e == null) {
                this.e = new JSONObject();
            }
            this.e.put(DoubanAccountOperationFragment_.ACTION_ARG, jSONObject3);
            Object obj = this.a.c;
            if (!TextUtils.isEmpty(obj)) {
                try {
                    String[] split = obj.split("/");
                    jSONObject3.put(SocialConstants.PARAM_TYPE, split[1]);
                    if (split.length > 1) {
                        jSONObject3.put("method", split[2]);
                    }
                } catch (Exception e) {
                }
            }
            this.e.put(HttpRequest.ENCODING_GZIP, this.d);
            if (this.c) {
                jSONObject3 = new JSONObject();
                new StringBuilder("requestData before: ").append(this.e.toString());
                String jSONObject4 = this.e.toString();
                String a = d.a(str, a.c);
                jSONObject4 = com.alipay.sdk.encrypt.e.a(str, jSONObject4);
                jSONObject3.put("req_data", String.format(Locale.getDefault(), "%08X%s%08X%s", new Object[]{Integer.valueOf(a.length()), a, Integer.valueOf(jSONObject4.length()), jSONObject4}));
                jSONObject2.put(com.alipay.sdk.cons.c.g, jSONObject3);
            } else {
                jSONObject2.put(com.alipay.sdk.cons.c.g, this.e);
            }
            jSONObject.put(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA, jSONObject2);
        } catch (Exception e2) {
        }
        new StringBuilder("requestData : ").append(jSONObject.toString());
        return jSONObject;
    }

    private boolean e() {
        return this.c;
    }

    public final String toString() {
        return this.a.toString() + ", requestData = " + c.a(this.e, this.f) + ", timeStamp = " + this.g;
    }

    private a f() {
        return this.a;
    }
}
