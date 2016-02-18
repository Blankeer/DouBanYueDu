package com.alipay.sdk.data;

import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public final class c {
    public static final String a = "application/octet-stream;binary/octet-stream";
    public Header[] b;
    public String c;
    public String d;

    public c() {
        this.b = null;
        this.c = null;
        this.d = null;
    }

    private void a(Header[] headerArr) {
        this.b = headerArr;
    }

    private Header[] b() {
        return this.b;
    }

    public final ArrayList<BasicHeader> a() {
        if (this.b == null || this.b.length == 0) {
            return null;
        }
        ArrayList<BasicHeader> arrayList = new ArrayList();
        for (Header header : this.b) {
            arrayList.add(new BasicHeader(header.getName(), header.getValue()));
        }
        return arrayList;
    }

    private String c() {
        return this.c;
    }

    private void a(String str) {
        this.c = str;
    }

    private String d() {
        return this.d;
    }

    private void b(String str) {
        this.d = str;
    }

    private void e() {
        this.d = null;
        this.c = null;
    }
}
