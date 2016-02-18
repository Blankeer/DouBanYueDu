package u.aly;

import com.douban.book.reader.fragment.GiftMessageEditFragment_;
import com.tencent.open.SocialConstants;

/* compiled from: TApplicationException */
public class ci extends cp {
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    public static final int d = 3;
    public static final int e = 4;
    public static final int f = 5;
    public static final int g = 6;
    public static final int h = 7;
    private static final dn j;
    private static final dd k;
    private static final dd l;
    private static final long m = 1;
    protected int i;

    static {
        j = new dn("TApplicationException");
        k = new dd(GiftMessageEditFragment_.MESSAGE_ARG, dp.i, (short) 1);
        l = new dd(SocialConstants.PARAM_TYPE, (byte) 8, (short) 2);
    }

    public ci() {
        this.i = a;
    }

    public ci(int i) {
        this.i = a;
        this.i = i;
    }

    public ci(int i, String str) {
        super(str);
        this.i = a;
        this.i = i;
    }

    public ci(String str) {
        super(str);
        this.i = a;
    }

    public int a() {
        return this.i;
    }

    public static ci a(di diVar) throws cp {
        diVar.j();
        String str = null;
        int i = a;
        while (true) {
            dd l = diVar.l();
            if (l.b == null) {
                diVar.k();
                return new ci(i, str);
            }
            switch (l.c) {
                case b /*1*/:
                    if (l.b != 11) {
                        dl.a(diVar, l.b);
                        break;
                    }
                    str = diVar.z();
                    break;
                case c /*2*/:
                    if (l.b != 8) {
                        dl.a(diVar, l.b);
                        break;
                    }
                    i = diVar.w();
                    break;
                default:
                    dl.a(diVar, l.b);
                    break;
            }
            diVar.m();
        }
    }

    public void b(di diVar) throws cp {
        diVar.a(j);
        if (getMessage() != null) {
            diVar.a(k);
            diVar.a(getMessage());
            diVar.c();
        }
        diVar.a(l);
        diVar.a(this.i);
        diVar.c();
        diVar.d();
        diVar.b();
    }
}
