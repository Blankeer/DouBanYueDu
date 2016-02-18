package com.alipay.sdk.protocol;

import android.text.TextUtils;
import org.json.JSONObject;

public enum a {
    WapPay("js://wappay"),
    DownLoad("js://download"),
    Submit("submit"),
    Confirm("js://confirm"),
    Alert("js://alert"),
    Update("js://update"),
    Exit("js://exit");
    
    public String h;
    String i;
    String j;
    String k;
    String l;
    boolean m;
    boolean n;
    boolean o;
    String p;
    String q;
    String r;
    JSONObject s;
    private String t;
    private JSONObject u;

    private a(String str) {
        this.t = str;
    }

    private JSONObject a() {
        return this.s;
    }

    private String b() {
        return this.r;
    }

    private String c() {
        return this.p;
    }

    private String d() {
        return this.q;
    }

    private String e() {
        return this.h;
    }

    private String f() {
        return this.i;
    }

    private JSONObject g() {
        return this.u;
    }

    private String h() {
        return this.k;
    }

    private String i() {
        return this.l;
    }

    private boolean j() {
        return this.m;
    }

    private boolean k() {
        return this.n;
    }

    private boolean l() {
        return this.o;
    }

    private String m() {
        return this.j;
    }

    private static String[] a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return str.split(";");
    }

    public static a[] a(b bVar) {
        if (bVar != null) {
            String[] strArr;
            Object obj = bVar.a;
            if (TextUtils.isEmpty(obj)) {
                strArr = null;
            } else {
                strArr = obj.split(";");
            }
            if (strArr == null) {
                return new a[]{Submit};
            }
            a[] aVarArr = new a[strArr.length];
            int length = strArr.length;
            int i = 0;
            int i2 = 0;
            while (i < length) {
                String str = strArr[i];
                a aVar = Submit;
                for (a aVar2 : values()) {
                    if (str.startsWith(aVar2.t)) {
                        break;
                    }
                }
                a aVar22 = aVar;
                aVar22.h = str;
                if (TextUtils.isEmpty(bVar.b)) {
                    bVar.b = com.alipay.sdk.cons.a.b;
                }
                aVar22.i = bVar.b;
                aVar22.u = bVar.a();
                aVar22.j = bVar.c;
                aVar22.k = bVar.d;
                aVar22.l = bVar.e;
                aVar22.m = bVar.f;
                aVar22.n = bVar.g;
                aVar22.o = bVar.h;
                aVar22.p = bVar.i;
                aVar22.q = bVar.j;
                aVar22.r = bVar.k;
                aVar22.s = bVar.l;
                aVarArr[i2] = aVar22;
                i++;
                i2++;
            }
            return aVarArr;
        }
        return new a[]{Submit};
    }
}
