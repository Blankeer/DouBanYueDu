package com.alipay.android.phone.mrpc.core;

import com.douban.book.reader.helper.WorksListUri;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;

public final class v extends aa {
    String a;
    byte[] b;
    String c;
    ArrayList<Header> d;
    boolean e;
    private Map<String, String> h;

    public v(String str) {
        this.a = str;
        this.d = new ArrayList();
        this.h = new HashMap();
        this.c = HttpRequest.CONTENT_TYPE_FORM;
    }

    private void a(boolean z) {
        this.e = z;
    }

    private void a(byte[] bArr) {
        this.b = bArr;
    }

    private String b() {
        return this.a;
    }

    private void b(String str) {
        this.c = str;
    }

    private byte[] c() {
        return this.b;
    }

    private String d() {
        return this.c;
    }

    private ArrayList<Header> e() {
        return this.d;
    }

    private boolean f() {
        return this.e;
    }

    public final String a(String str) {
        return this.h == null ? null : (String) this.h.get(str);
    }

    public final void a(String str, String str2) {
        if (this.h == null) {
            this.h = new HashMap();
        }
        this.h.put(str, str2);
    }

    public final void a(Header header) {
        this.d.add(header);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        v vVar = (v) obj;
        if (this.b == null) {
            if (vVar.b != null) {
                return false;
            }
        } else if (!this.b.equals(vVar.b)) {
            return false;
        }
        return this.a == null ? vVar.a == null : this.a.equals(vVar.a);
    }

    public final int hashCode() {
        int i = 1;
        if (this.h != null && this.h.containsKey(WorksListUri.KEY_ID)) {
            i = ((String) this.h.get(WorksListUri.KEY_ID)).hashCode() + 31;
        }
        return (this.a == null ? 0 : this.a.hashCode()) + (i * 31);
    }

    public final String toString() {
        return String.format("Url : %s,HttpHeader: %s", new Object[]{this.a, this.d});
    }
}
