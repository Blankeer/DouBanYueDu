package u.aly;

import android.support.v4.media.TransportMediator;
import com.alipay.sdk.protocol.h;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dc.a;

/* compiled from: TProtocolUtil */
public class dl {
    private static int a;

    static {
        a = AdvancedShareActionProvider.WEIGHT_MAX;
    }

    public static void a(int i) {
        a = i;
    }

    public static void a(di diVar, byte b) throws cp {
        a(diVar, b, a);
    }

    public static void a(di diVar, byte b, int i) throws cp {
        int i2 = 0;
        if (i <= 0) {
            throw new cp("Maximum skip depth exceeded");
        }
        switch (b) {
            case dx.c /*2*/:
                diVar.t();
            case dx.d /*3*/:
                diVar.u();
            case dx.e /*4*/:
                diVar.y();
            case ci.g /*6*/:
                diVar.v();
            case h.g /*8*/:
                diVar.w();
            case h.i /*10*/:
                diVar.x();
            case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                diVar.A();
            case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                diVar.j();
                while (true) {
                    dd l = diVar.l();
                    if (l.b == null) {
                        diVar.k();
                        return;
                    } else {
                        a(diVar, l.b, i - 1);
                        diVar.m();
                    }
                }
            case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                df n = diVar.n();
                while (i2 < n.c) {
                    a(diVar, n.a, i - 1);
                    a(diVar, n.b, i - 1);
                    i2++;
                }
                diVar.o();
            case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                dm r = diVar.r();
                while (i2 < r.b) {
                    a(diVar, r.a, i - 1);
                    i2++;
                }
                diVar.s();
            case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                de p = diVar.p();
                while (i2 < p.b) {
                    a(diVar, p.a, i - 1);
                    i2++;
                }
                diVar.q();
            default:
        }
    }

    public static dk a(byte[] bArr, dk dkVar) {
        if (bArr[0] > 16) {
            return new a();
        }
        if (bArr.length <= 1 || (bArr[1] & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 0) {
            return dkVar;
        }
        return new a();
    }
}
