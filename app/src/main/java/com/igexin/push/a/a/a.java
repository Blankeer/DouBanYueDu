package com.igexin.push.a.a;

import android.os.Message;
import com.igexin.push.core.f;
import com.igexin.push.e.b.d;

public class a implements d {
    private long a;

    public a() {
        this.a = 0;
    }

    public void a() {
        Message message = new Message();
        message.what = com.igexin.push.core.a.j;
        f.a().a(message);
    }

    public void a(long j) {
        this.a = j;
    }

    public boolean b() {
        return System.currentTimeMillis() - this.a > 360000;
    }
}
