package com.igexin.push.core.bean;

public class m extends BaseAction {
    private String a;
    private String b;
    private String c;
    private String d;
    private boolean e;

    public m() {
        this.d = null;
        this.e = false;
    }

    public String a() {
        return this.d;
    }

    public void a(String str) {
        this.d = str;
    }

    public void a(boolean z) {
        this.e = z;
    }

    public void b(String str) {
        this.a = str;
    }

    public boolean b() {
        return this.e;
    }

    public String c() {
        return this.a;
    }

    public void c(String str) {
        this.b = str;
    }

    public String d() {
        return this.b;
    }

    public void d(String str) {
        this.c = str;
    }

    public String e() {
        return this.c;
    }
}
