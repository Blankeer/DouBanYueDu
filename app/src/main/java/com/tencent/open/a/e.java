package com.tencent.open.a;

import android.support.v4.media.TransportMediator;
import android.util.Log;
import com.alipay.sdk.protocol.h;
import u.aly.dx;

/* compiled from: ProGuard */
public final class e extends i {
    public static final e a;

    static {
        a = new e();
    }

    protected void a(int i, Thread thread, long j, String str, String str2, Throwable th) {
        switch (i) {
            case dx.b /*1*/:
                Log.v(str, str2, th);
            case dx.c /*2*/:
                Log.d(str, str2, th);
            case dx.e /*4*/:
                Log.i(str, str2, th);
            case h.g /*8*/:
                Log.w(str, str2, th);
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                Log.e(str, str2, th);
            case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                Log.e(str, str2, th);
            default:
        }
    }
}
