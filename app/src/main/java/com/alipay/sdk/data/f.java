package com.alipay.sdk.data;

import io.realm.internal.Table;
import org.apache.http.Header;
import org.json.JSONObject;

public final class f {
    public static final int a = 1000;
    public static final int b = 503;
    public int c;
    public String d;
    public long e;
    public String f;
    public String g;
    public String h;
    public JSONObject i;
    public String j;
    public boolean k;
    public a l;
    Header[] m;

    public f() {
        this.c = 0;
        this.d = Table.STRING_DEFAULT_VALUE;
        this.e = 0;
        this.f = Table.STRING_DEFAULT_VALUE;
        this.g = null;
        this.h = null;
        this.i = null;
        this.k = true;
        this.l = null;
        this.m = null;
    }

    private a a() {
        return this.l;
    }

    private void b() {
        this.k = false;
    }

    private int c() {
        return this.c;
    }

    private void a(int i) {
        this.c = i;
    }

    private void a(String str) {
        this.d = str;
    }

    private String d() {
        return this.f;
    }

    private void b(String str) {
        this.f = str;
    }

    private void c(String str) {
        this.g = str;
    }

    private void d(String str) {
        this.h = str;
    }

    private void a(long j) {
        this.e = j;
    }

    private void a(JSONObject jSONObject) {
        this.i = jSONObject;
    }

    private void e(String str) {
        this.j = str;
    }

    private void a(a aVar) {
        this.l = aVar;
    }
}
