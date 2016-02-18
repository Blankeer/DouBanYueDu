package com.igexin.push.core.bean;

import com.igexin.push.core.f;
import com.igexin.push.core.g;

public class k extends BaseAction {
    private String a;
    private boolean b;
    private boolean c;
    private String d;

    public String a() {
        return this.a;
    }

    public void a(String str) {
        this.a = str;
    }

    public void a(boolean z) {
        this.b = z;
    }

    public String b() {
        return this.d;
    }

    public void b(String str) {
        this.d = str;
    }

    public void b(boolean z) {
        this.c = z;
    }

    public String c() {
        String str = this.a;
        if (this.b) {
            str = str.indexOf("?") > 0 ? str + "&cid=" + g.s : str + "?cid=" + g.s;
        }
        if (!this.c) {
            return str;
        }
        String m = f.a().m();
        return m != null ? str.indexOf("?") > 0 ? str + "&nettype=" + m : str + "?nettype=" + m : str;
    }
}
