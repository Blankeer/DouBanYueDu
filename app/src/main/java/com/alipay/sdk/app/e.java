package com.alipay.sdk.app;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final class e implements OnClickListener {
    final /* synthetic */ c a;

    e(c cVar) {
        this.a = cVar;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.a.a.cancel();
        this.a.b.a.d = false;
        l.a = l.a();
        this.a.b.a.finish();
    }
}
