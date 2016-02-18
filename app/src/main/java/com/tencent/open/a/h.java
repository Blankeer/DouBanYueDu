package com.tencent.open.a;

import android.support.v4.media.TransportMediator;
import android.text.format.Time;
import android.util.Log;
import com.douban.book.reader.constant.Char;
import u.aly.dx;

/* compiled from: ProGuard */
public final class h {
    public static final h a;

    static {
        a = new h();
    }

    public final String a(int i) {
        switch (i) {
            case dx.b /*1*/:
                return "V";
            case dx.c /*2*/:
                return "D";
            case dx.e /*4*/:
                return "I";
            case com.alipay.sdk.protocol.h.g /*8*/:
                return "W";
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                return "E";
            case TransportMediator.FLAG_KEY_MEDIA_STOP /*32*/:
                return "A";
            default:
                return "-";
        }
    }

    public String a(int i, Thread thread, long j, String str, String str2, Throwable th) {
        long j2 = j % 1000;
        Time time = new Time();
        time.set(j);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(a(i)).append(Char.SLASH).append(time.format("%Y-%m-%d %H:%M:%S")).append(Char.DOT);
        if (j2 < 10) {
            stringBuilder.append("00");
        } else if (j2 < 100) {
            stringBuilder.append('0');
        }
        stringBuilder.append(j2).append(Char.SPACE).append('[');
        if (thread == null) {
            stringBuilder.append("N/A");
        } else {
            stringBuilder.append(thread.getName());
        }
        stringBuilder.append(']').append('[').append(str).append(']').append(Char.SPACE).append(str2).append('\n');
        if (th != null) {
            stringBuilder.append("* Exception : \n").append(Log.getStackTraceString(th)).append('\n');
        }
        return stringBuilder.toString();
    }
}
