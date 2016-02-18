package com.igexin.push.e.b;

import com.igexin.push.core.a.e;
import com.igexin.push.core.bean.PushTaskBean;

public class b extends f {
    private PushTaskBean a;
    private String b;

    public b(PushTaskBean pushTaskBean, String str, long j) {
        super(j);
        this.z = false;
        this.a = pushTaskBean;
        this.b = str;
    }

    protected void a() {
        e.a().b(this.a, this.b);
    }

    public int b() {
        return 0;
    }

    public void c() {
        super.c();
    }
}
