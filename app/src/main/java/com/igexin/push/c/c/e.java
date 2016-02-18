package com.igexin.push.c.c;

import android.support.v4.media.TransportMediator;
import com.igexin.a.a.d.a;
import io.fabric.sdk.android.services.network.HttpRequest;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public abstract class e extends a {
    public int i;
    public byte j;

    protected int a(String str) {
        return str.equals(HttpRequest.CHARSET_UTF8) ? 1 : str.equals("UTF-16") ? 2 : str.equals("UTF-16BE") ? 16 : str.equals("UTF-16LE") ? 17 : str.equals("GBK") ? 25 : str.equals("GB2312") ? 26 : str.equals("GB18030") ? 27 : str.equals("ISO-8859-1") ? 33 : 1;
    }

    protected String a(byte b) {
        switch (b & 63) {
            case dx.b /*1*/:
                return HttpRequest.CHARSET_UTF8;
            case dx.c /*2*/:
                return "UTF-16";
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                return "UTF-16BE";
            case R.styleable.StickyListHeadersListView_android_choiceMode /*17*/:
                return "UTF-16LE";
            case HeaderMapDB.TUPLE2_COMPARATOR_STATIC /*25*/:
                return "GBK";
            case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                return "GB2312";
            case HeaderMapDB.TUPLE4_COMPARATOR_STATIC /*27*/:
                return "GB18030";
            case HeaderMapDB.HASHER_BASIC /*33*/:
                return "ISO-8859-1";
            default:
                return HttpRequest.CHARSET_UTF8;
        }
    }

    public abstract void a(byte[] bArr);

    public int b() {
        return this.i;
    }

    public abstract byte[] d();
}
