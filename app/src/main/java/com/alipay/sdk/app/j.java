package com.alipay.sdk.app;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final class j implements OnClickListener {
    final /* synthetic */ h a;

    j(h hVar) {
        this.a = hVar;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.a.a.cancel();
        this.a.b.a.e = false;
        l.a = l.a();
        this.a.b.a.finish();
    }
}
