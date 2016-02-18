package com.alipay.sdk.widget;

import android.app.Activity;
import android.app.ProgressDialog;

public final class a {
    Activity a;
    ProgressDialog b;

    public a(Activity activity) {
        this.a = activity;
    }

    public final boolean a() {
        return this.b != null && this.b.isShowing();
    }

    public final void b() {
        c();
        Runnable bVar = new b(this, "\u6b63\u5728\u52a0\u8f7d");
        if (this.a != null && !this.a.isFinishing()) {
            this.a.runOnUiThread(bVar);
        }
    }

    private void a(CharSequence charSequence) {
        c();
        Runnable bVar = new b(this, charSequence);
        if (this.a != null && !this.a.isFinishing()) {
            this.a.runOnUiThread(bVar);
        }
    }

    public final void c() {
        Runnable cVar = new c(this);
        if (this.a != null && !this.a.isFinishing()) {
            this.a.runOnUiThread(cVar);
        }
    }
}
