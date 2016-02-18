package com.tencent.wxop.stat;

import android.content.Context;

final class m implements Runnable {
    final /* synthetic */ f bN;
    final /* synthetic */ Context e;

    m(Context context) {
        this.e = context;
        this.bN = null;
    }

    public final void run() {
        try {
            e.a(this.e, false, this.bN);
        } catch (Throwable th) {
            e.aV.b(th);
        }
    }
}
