package com.umeng.analytics.social;

import com.douban.book.reader.constant.Char;
import io.realm.internal.Table;

/* compiled from: UMResult */
public class d {
    private int a;
    private String b;
    private String c;
    private Exception d;

    public d(int i) {
        this.a = -1;
        this.b = Table.STRING_DEFAULT_VALUE;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.d = null;
        this.a = i;
    }

    public d(int i, Exception exception) {
        this.a = -1;
        this.b = Table.STRING_DEFAULT_VALUE;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.d = null;
        this.a = i;
        this.d = exception;
    }

    public Exception a() {
        return this.d;
    }

    public int b() {
        return this.a;
    }

    public void a(int i) {
        this.a = i;
    }

    public String c() {
        return this.b;
    }

    public void a(String str) {
        this.b = str;
    }

    public String d() {
        return this.c;
    }

    public void b(String str) {
        this.c = str;
    }

    public String toString() {
        return "status=" + this.a + Char.CRLF + "msg:  " + this.b + Char.CRLF + "data:  " + this.c;
    }
}
