package com.alipay.sdk.widget;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnCancelListener;

final class b implements Runnable {
    final /* synthetic */ boolean a;
    final /* synthetic */ OnCancelListener b;
    final /* synthetic */ CharSequence c;
    final /* synthetic */ a d;

    b(a aVar, CharSequence charSequence) {
        this.d = aVar;
        this.a = false;
        this.b = null;
        this.c = charSequence;
    }

    public final void run() {
        if (this.d.b == null) {
            this.d.b = new ProgressDialog(this.d.a);
        }
        this.d.b.setCancelable(this.a);
        this.d.b.setOnCancelListener(this.b);
        this.d.b.setMessage(this.c);
        try {
            this.d.b.show();
        } catch (Exception e) {
            this.d.b = null;
        }
    }
}
