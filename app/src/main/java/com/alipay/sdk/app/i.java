package com.alipay.sdk.app;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final class i implements OnClickListener {
    final /* synthetic */ h a;

    i(h hVar) {
        this.a = hVar;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.a.b.a.e = true;
        this.a.a.proceed();
        dialogInterface.dismiss();
    }
}
