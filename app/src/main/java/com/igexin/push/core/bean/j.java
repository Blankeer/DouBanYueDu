package com.igexin.push.core.bean;

import io.realm.internal.Table;

public class j extends BaseAction {
    private String a;
    private String b;
    private String c;
    private String d;

    public j() {
        this.a = Table.STRING_DEFAULT_VALUE;
        this.b = Table.STRING_DEFAULT_VALUE;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.d = "false";
    }

    public String a() {
        return this.a;
    }

    public void a(String str) {
        this.a = str;
    }

    public String b() {
        return this.b;
    }

    public void b(String str) {
        this.b = str;
    }

    public String c() {
        return this.c;
    }

    public void c(String str) {
        this.c = str;
    }

    public String d() {
        return this.d;
    }

    public void d(String str) {
        this.d = str;
    }
}
