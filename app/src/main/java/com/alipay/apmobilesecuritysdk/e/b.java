package com.alipay.apmobilesecuritysdk.e;

import com.alipay.security.mobile.module.a.b.c;
import io.realm.internal.Table;

public final class b {
    public String a;
    public String b;
    public String c;
    public String d;

    public b(c cVar, String str) {
        this.a = Table.STRING_DEFAULT_VALUE;
        this.b = Table.STRING_DEFAULT_VALUE;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.d = Table.STRING_DEFAULT_VALUE;
        this.a = cVar.a;
        this.b = str;
        this.c = cVar.b;
        this.d = cVar.e;
    }

    public b(String str, String str2, String str3, String str4) {
        this.a = Table.STRING_DEFAULT_VALUE;
        this.b = Table.STRING_DEFAULT_VALUE;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.d = Table.STRING_DEFAULT_VALUE;
        this.a = str;
        this.b = str2;
        this.c = str3;
        this.d = str4;
    }

    private String a() {
        return this.a;
    }

    private String b() {
        return this.b;
    }

    private String c() {
        return this.c;
    }

    private String d() {
        return this.d;
    }
}
