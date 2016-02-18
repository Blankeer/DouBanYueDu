package com.tencent.open.web.security;

import android.webkit.WebView;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.open.a.a;
import com.tencent.open.a.f;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ProGuard */
public class c extends a {
    private String d;

    public c(WebView webView, long j, String str, String str2) {
        super(webView, j, str);
        this.d = str2;
    }

    public void a(Object obj) {
        f.b("openSDK_LOG.SL", "-->onComplete, result: " + obj);
    }

    public void a() {
        f.b("openSDK_LOG.SL", "-->onNoMatchMethod...");
    }

    public void a(String str) {
        f.b("openSDK_LOG.SL", "-->onCustomCallback, js: " + str);
        JSONObject jSONObject = new JSONObject();
        int i = 0;
        if (!com.tencent.open.c.c.a) {
            i = -4;
        }
        try {
            jSONObject.put("result", i);
            jSONObject.put("sn", this.b);
            jSONObject.put(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA, str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        b(jSONObject.toString());
    }

    private void b(String str) {
        WebView webView = (WebView) this.a.get();
        if (webView != null) {
            StringBuffer stringBuffer = new StringBuffer("javascript:");
            stringBuffer.append("if(!!").append(this.d).append("){");
            stringBuffer.append(this.d);
            stringBuffer.append("(");
            stringBuffer.append(str);
            stringBuffer.append(")}");
            String stringBuffer2 = stringBuffer.toString();
            f.b("openSDK_LOG", "-->callback, callback: " + stringBuffer2);
            webView.loadUrl(stringBuffer2);
        }
    }
}
