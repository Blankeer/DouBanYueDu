package com.alipay.sdk.net;

import android.content.Context;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import com.alipay.sdk.data.b;
import com.alipay.sdk.data.c;
import com.alipay.sdk.data.e;
import com.alipay.sdk.data.f;
import com.alipay.sdk.encrypt.a;
import com.alipay.sdk.exception.NetErrorException;
import com.douban.book.reader.entity.Annotation.Column;
import com.douban.book.reader.helper.AppUri;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Random;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.json.JSONException;
import org.json.JSONObject;
import u.aly.dx;

public final class d {
    private int a;
    private c b;

    public d(c cVar) {
        this.a = 0;
        this.b = cVar;
    }

    public d() {
        this.a = 0;
    }

    public final com.alipay.sdk.protocol.c a(Context context, e eVar, boolean z) throws NetErrorException {
        f fVar = new f();
        JSONObject a = a(context, eVar, fVar);
        if (a.optBoolean(HttpRequest.ENCODING_GZIP)) {
            JSONObject optJSONObject = a.optJSONObject(com.alipay.sdk.cons.c.c);
            if (optJSONObject != null && optJSONObject.has("quickpay")) {
                try {
                    byte[] a2 = b.a(a.a(optJSONObject.optString("quickpay")));
                    if (TextUtils.equals(com.alipay.sdk.encrypt.c.a(a2), a.optString("md5"))) {
                        a.put(com.alipay.sdk.cons.c.c, new JSONObject(new String(a2, "utf-8")));
                    }
                } catch (Exception e) {
                }
            }
        } else {
            fVar.k = false;
        }
        new StringBuilder("responsestring decoded ").append(a);
        com.alipay.sdk.protocol.c cVar = new com.alipay.sdk.protocol.c(eVar, fVar);
        cVar.a(a);
        if (!z) {
            com.alipay.sdk.protocol.e eVar2 = new com.alipay.sdk.protocol.e();
            com.alipay.sdk.protocol.c a3 = com.alipay.sdk.protocol.e.a(cVar);
            if (a3 != null) {
                cVar = a3;
            }
            f fVar2 = cVar.b;
            JSONObject jSONObject = cVar.c;
            com.alipay.sdk.data.a aVar = cVar.a.a;
            com.alipay.sdk.data.a aVar2 = cVar.b.l;
            if (TextUtils.isEmpty(aVar2.c)) {
                aVar2.c = aVar.c;
            }
            if (TextUtils.isEmpty(aVar2.d)) {
                aVar2.d = aVar.d;
            }
            if (TextUtils.isEmpty(aVar2.b)) {
                aVar2.b = aVar.b;
            }
            if (TextUtils.isEmpty(aVar2.a)) {
                aVar2.a = aVar.a;
            }
            String str = SettingsJsonConstants.SESSION_KEY;
            JSONObject optJSONObject2 = jSONObject.optJSONObject("reflected_data");
            if (optJSONObject2 != null) {
                new StringBuilder("session = ").append(optJSONObject2.optString(str, Table.STRING_DEFAULT_VALUE));
                cVar.b.i = optJSONObject2;
            } else if (jSONObject.has(str)) {
                optJSONObject2 = new JSONObject();
                try {
                    optJSONObject2.put(str, jSONObject.optString(str));
                    CharSequence charSequence = com.alipay.sdk.tid.b.a().a;
                    if (!TextUtils.isEmpty(charSequence)) {
                        optJSONObject2.put(com.alipay.sdk.cons.b.c, charSequence);
                    }
                    fVar2.i = optJSONObject2;
                } catch (JSONException e2) {
                }
            }
            fVar2.f = jSONObject.optString("end_code", Constants.VIA_RESULT_SUCCESS);
            fVar2.j = jSONObject.optString(Column.USER_ID, Table.STRING_DEFAULT_VALUE);
            str = jSONObject.optString("result");
            try {
                str = URLDecoder.decode(jSONObject.optString("result"), HttpRequest.CHARSET_UTF8);
            } catch (UnsupportedEncodingException e3) {
            }
            fVar2.g = str;
            fVar2.h = jSONObject.optString("memo", Table.STRING_DEFAULT_VALUE);
        }
        return cVar;
    }

    private JSONObject a(Context context, e eVar, f fVar) throws NetErrorException {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            switch (random.nextInt(3)) {
                case dx.a /*0*/:
                    stringBuilder.append(String.valueOf((char) ((int) Math.round((Math.random() * 25.0d) + 65.0d))));
                    break;
                case dx.b /*1*/:
                    stringBuilder.append(String.valueOf((char) ((int) Math.round((Math.random() * 25.0d) + 97.0d))));
                    break;
                case dx.c /*2*/:
                    stringBuilder.append(String.valueOf(new Random().nextInt(10)));
                    break;
                default:
                    break;
            }
        }
        String stringBuilder2 = stringBuilder.toString();
        try {
            String a = a(context, eVar.a.a, eVar.a(stringBuilder2).toString(), (c) eVar.b.get(), fVar);
            fVar.e = Calendar.getInstance().getTimeInMillis();
            JSONObject a2;
            if (eVar.c) {
                a2 = a(a, fVar);
                if (fVar.c != AppUri.OPEN_URL || this.a >= 3) {
                    this.a = 0;
                    return new JSONObject(com.alipay.sdk.encrypt.e.b(stringBuilder2, a2.optString("res_data")));
                }
                this.a++;
                return a(context, eVar, fVar);
            }
            a2 = a(a, fVar);
            new StringBuilder("respData:").append(a2.toString());
            return a2;
        } catch (NetErrorException e) {
            throw e;
        } catch (Exception e2) {
            throw new NetErrorException();
        }
    }

    private static String a(String str) {
        InputStream fileInputStream;
        Throwable th;
        String str2 = null;
        try {
            fileInputStream = new FileInputStream(str);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                char[] cArr = new char[AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT];
                StringBuilder stringBuilder = new StringBuilder();
                while (true) {
                    int read = bufferedReader.read(cArr);
                    if (read <= 0) {
                        break;
                    }
                    stringBuilder.append(cArr, 0, read);
                }
                bufferedReader.close();
                str2 = stringBuilder.toString();
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            } catch (Exception e2) {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e3) {
                    }
                }
                return str2;
            } catch (Throwable th2) {
                th = th2;
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (Exception e5) {
            Object obj = str2;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            return str2;
        } catch (Throwable th3) {
            Throwable th4 = th3;
            fileInputStream = str2;
            th = th4;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            throw th;
        }
        return str2;
    }

    private JSONObject a(Context context, e eVar, f fVar, String str, String str2) throws JSONException, NetErrorException {
        JSONObject a = a(str2, fVar);
        if (fVar.c != AppUri.OPEN_URL || this.a >= 3) {
            this.a = 0;
            return new JSONObject(com.alipay.sdk.encrypt.e.b(str, a.optString("res_data")));
        }
        this.a++;
        return a(context, eVar, fVar);
    }

    private static JSONObject a(f fVar, String str) throws JSONException {
        JSONObject a = a(str, fVar);
        new StringBuilder("respData:").append(a.toString());
        return a;
    }

    private String a(Context context, String str, String str2, c cVar, f fVar) throws NetErrorException {
        try {
            if (c.a == null) {
                c.a = new a(context, str);
            } else if (!TextUtils.equals(str, c.a.a)) {
                c.a.a = str;
            }
            HttpResponse a = cVar != null ? c.a.a(str2, cVar) : c.a.a(str2, null);
            StatusLine statusLine = a.getStatusLine();
            fVar.c = statusLine.getStatusCode();
            fVar.d = statusLine.getReasonPhrase();
            c cVar2 = this.b;
            Header[] headers = a.getHeaders("Msp-Param");
            if (cVar2 != null && headers.length > 0) {
                cVar2.b = headers;
            }
            String a2 = c.a(a);
            c.a = null;
            return a2;
        } catch (Exception e) {
            throw new NetErrorException();
        } catch (Throwable th) {
            c.a = null;
        }
    }

    private static JSONObject a(String str, f fVar) throws JSONException {
        JSONObject optJSONObject = new JSONObject(str).optJSONObject(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA);
        if (optJSONObject != null) {
            fVar.c = optJSONObject.optInt(SelectCountryActivity.EXTRA_COUNTRY_CODE, f.b);
            fVar.d = optJSONObject.optString("error_msg", Table.STRING_DEFAULT_VALUE);
            optJSONObject = optJSONObject.optJSONObject(com.alipay.sdk.cons.c.g);
            if (optJSONObject != null) {
                if (fVar.c == AppUri.OPEN_URL) {
                    String optString = optJSONObject.optString("public_key");
                    if (!TextUtils.isEmpty(optString)) {
                        com.alipay.sdk.sys.b.a().b().a(optString);
                    }
                }
                com.alipay.sdk.data.a aVar = new com.alipay.sdk.data.a();
                aVar.c = optJSONObject.optString("next_api_name");
                aVar.d = optJSONObject.optString("next_api_version");
                aVar.b = optJSONObject.optString("next_namespace");
                aVar.a = optJSONObject.optString("next_request_url");
                fVar.l = aVar;
                return optJSONObject;
            }
            int i = fVar.c;
        } else {
            fVar.c = f.b;
            fVar.d = Table.STRING_DEFAULT_VALUE;
        }
        return null;
    }

    private static void a(JSONObject jSONObject) {
        String optString = jSONObject.optString("public_key");
        if (!TextUtils.isEmpty(optString)) {
            com.alipay.sdk.sys.b.a().b().a(optString);
        }
    }
}
