package com.alipay.sdk.authjs;

import com.igexin.sdk.PushBuildConfig;
import org.json.JSONException;
import org.json.JSONObject;
import u.aly.dx;

public final class a {
    public static final String a = "CallInfo";
    public static final String b = "call";
    public static final String c = "callback";
    public static final String d = "bundleName";
    public static final String e = "clientId";
    public static final String f = "param";
    public static final String g = "func";
    public static final String h = "msgType";
    public String i;
    public String j;
    public String k;
    public String l;
    public JSONObject m;
    private boolean n;

    /* renamed from: com.alipay.sdk.authjs.a.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            a = new int[a.a().length];
            try {
                a[a.b - 1] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                a[a.c - 1] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                a[a.d - 1] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum a {
        ;

        public static int[] a() {
            return (int[]) f.clone();
        }

        static {
            a = 1;
            b = 2;
            c = 3;
            d = 4;
            e = 5;
            f = new int[]{a, b, c, d, e};
        }
    }

    private static String a(int i) {
        switch (AnonymousClass1.a[i - 1]) {
            case dx.b /*1*/:
                return "function not found";
            case dx.c /*2*/:
                return "invalid parameter";
            case dx.d /*3*/:
                return "runtime error";
            default:
                return PushBuildConfig.sdk_conf_debug_level;
        }
    }

    private boolean a() {
        return this.n;
    }

    private void a(boolean z) {
        this.n = z;
    }

    public a(String str) {
        this.n = false;
        this.l = str;
    }

    private String b() {
        return this.i;
    }

    private void a(String str) {
        this.i = str;
    }

    private String c() {
        return this.j;
    }

    private void b(String str) {
        this.j = str;
    }

    private String d() {
        return this.k;
    }

    private void c(String str) {
        this.k = str;
    }

    private String e() {
        return this.l;
    }

    private void d(String str) {
        this.l = str;
    }

    private JSONObject f() {
        return this.m;
    }

    private void a(JSONObject jSONObject) {
        this.m = jSONObject;
    }

    private String g() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(e, this.i);
        jSONObject.put(g, this.k);
        jSONObject.put(f, this.m);
        jSONObject.put(h, this.l);
        return jSONObject.toString();
    }
}
