package com.alipay.sdk.protocol;

import android.text.TextUtils;
import com.alipay.sdk.cons.a;
import com.alipay.sdk.cons.c;
import com.douban.book.reader.constant.Constants;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import io.realm.internal.Table;
import org.json.JSONObject;

public final class b {
    String a;
    String b;
    String c;
    String d;
    String e;
    boolean f;
    boolean g;
    boolean h;
    String i;
    String j;
    String k;
    JSONObject l;

    private b(String str) {
        this.g = true;
        this.h = true;
        this.a = str;
    }

    private JSONObject b() {
        return this.l;
    }

    private String c() {
        return this.k;
    }

    private String d() {
        return this.i;
    }

    private String e() {
        return this.j;
    }

    private static b a(JSONObject jSONObject) {
        String str;
        String str2;
        String str3;
        String str4;
        boolean z;
        String str5 = null;
        boolean z2 = true;
        if (jSONObject == null || !jSONObject.has(SelectCountryActivity.EXTRA_COUNTRY_NAME)) {
            str = null;
        } else {
            str = jSONObject.optString(SelectCountryActivity.EXTRA_COUNTRY_NAME);
        }
        if (jSONObject == null || !jSONObject.has(c.f)) {
            str2 = null;
        } else {
            str2 = jSONObject.optString(c.f);
        }
        if (jSONObject == null || !jSONObject.has(c.g)) {
            str3 = null;
        } else {
            str3 = jSONObject.optString(c.g);
        }
        if (jSONObject == null || !jSONObject.has(c.h)) {
            str4 = null;
        } else {
            str4 = jSONObject.optString(c.h);
        }
        if (jSONObject != null && jSONObject.has(c.i)) {
            str5 = jSONObject.optString(c.i);
        }
        if (jSONObject == null || !jSONObject.has(c.j)) {
            z = true;
        } else {
            z = jSONObject.optBoolean(c.j, true);
        }
        boolean z3 = (jSONObject == null || !jSONObject.has(Constants.API_SCHEME)) ? true : !jSONObject.optBoolean(Constants.API_SCHEME);
        if (jSONObject != null && jSONObject.has(c.k)) {
            z2 = jSONObject.optBoolean(c.k);
        }
        String str6 = Table.STRING_DEFAULT_VALUE;
        if (jSONObject != null && jSONObject.has(c.l)) {
            str6 = jSONObject.optString(c.l);
        }
        String str7 = Table.STRING_DEFAULT_VALUE;
        if (jSONObject != null && jSONObject.has(c.m)) {
            str7 = jSONObject.optString(c.m);
        }
        String str8 = Table.STRING_DEFAULT_VALUE;
        if (jSONObject != null && jSONObject.has(c.n)) {
            str8 = jSONObject.optString(c.n);
        }
        return a(str, str2, str3, str4, str5, z, z3, z2, str6, str7, str8, jSONObject);
    }

    public static b a(JSONObject jSONObject, String str) {
        String str2 = null;
        boolean z = true;
        JSONObject optJSONObject = jSONObject.optJSONObject(str);
        String optString = (optJSONObject == null || !optJSONObject.has(SelectCountryActivity.EXTRA_COUNTRY_NAME)) ? null : optJSONObject.optString(SelectCountryActivity.EXTRA_COUNTRY_NAME);
        String optString2 = (optJSONObject == null || !optJSONObject.has(c.f)) ? null : optJSONObject.optString(c.f);
        String optString3 = (optJSONObject == null || !optJSONObject.has(c.g)) ? null : optJSONObject.optString(c.g);
        String optString4 = (optJSONObject == null || !optJSONObject.has(c.h)) ? null : optJSONObject.optString(c.h);
        if (optJSONObject != null && optJSONObject.has(c.i)) {
            str2 = optJSONObject.optString(c.i);
        }
        boolean optBoolean = (optJSONObject == null || !optJSONObject.has(c.j)) ? true : optJSONObject.optBoolean(c.j, true);
        boolean z2 = (optJSONObject == null || !optJSONObject.has(Constants.API_SCHEME)) ? true : !optJSONObject.optBoolean(Constants.API_SCHEME);
        if (optJSONObject != null && optJSONObject.has(c.k)) {
            z = optJSONObject.optBoolean(c.k);
        }
        String str3 = Table.STRING_DEFAULT_VALUE;
        if (optJSONObject != null && optJSONObject.has(c.l)) {
            str3 = optJSONObject.optString(c.l);
        }
        String str4 = Table.STRING_DEFAULT_VALUE;
        if (optJSONObject != null && optJSONObject.has(c.m)) {
            str4 = optJSONObject.optString(c.m);
        }
        String str5 = Table.STRING_DEFAULT_VALUE;
        if (optJSONObject != null && optJSONObject.has(c.n)) {
            str5 = optJSONObject.optString(c.n);
        }
        return a(optString, optString2, optString3, optString4, str2, optBoolean, z2, z, str3, str4, str5, optJSONObject);
    }

    private static b a(String str, String str2, String str3, String str4, String str5, boolean z, boolean z2, boolean z3, String str6, String str7, String str8, JSONObject jSONObject) {
        String str9 = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        b bVar = new b(str);
        bVar.a = str;
        if (!TextUtils.isEmpty(str2)) {
            str9 = str2.trim();
        }
        bVar.b = str9;
        bVar.c = str3;
        bVar.d = str4;
        bVar.e = str5;
        bVar.f = z;
        bVar.g = z2;
        bVar.h = z3;
        bVar.i = str6;
        bVar.j = str7;
        bVar.k = str8;
        bVar.l = jSONObject;
        return bVar;
    }

    private static b a(String str, a aVar) {
        return a(str, aVar.i, aVar.j, aVar.k, aVar.l, aVar.m, aVar.n, aVar.o, aVar.p, aVar.q, aVar.r, aVar.s);
    }

    private String f() {
        return this.a;
    }

    private String g() {
        if (TextUtils.isEmpty(this.b)) {
            this.b = a.b;
        }
        return this.b;
    }

    private String h() {
        return this.c;
    }

    public final JSONObject a() {
        try {
            return new JSONObject(this.c);
        } catch (Exception e) {
            return null;
        }
    }

    private String i() {
        return this.d;
    }

    private String j() {
        return this.e;
    }

    private boolean k() {
        return this.f;
    }

    private boolean l() {
        return this.g;
    }

    private boolean m() {
        return this.h;
    }
}
