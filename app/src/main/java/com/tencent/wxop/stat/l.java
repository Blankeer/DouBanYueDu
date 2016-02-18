package com.tencent.wxop.stat;

import android.content.Context;

final class l implements Runnable {
    final /* synthetic */ f bN;
    final /* synthetic */ Context e;

    l(Context context) {
        this.e = context;
        this.bN = null;
    }

    public final void run() {
        if (this.e == null) {
            e.aV.error("The Context of StatService.onResume() can not be null!");
        } else {
            e.a(this.e, com.tencent.wxop.stat.b.l.B(this.e), this.bN);
        }
    }
}
