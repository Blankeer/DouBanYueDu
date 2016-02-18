package u.aly;

import com.alipay.sdk.protocol.h;
import com.igexin.download.Downloads;
import com.sina.weibo.sdk.component.ShareRequestParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* compiled from: UMEnvelope */
public class br implements Serializable, Cloneable, cj<br, e> {
    private static final int A = 3;
    public static final Map<e, cv> k;
    private static final dn l;
    private static final dd m;
    private static final dd n;
    private static final dd o;
    private static final dd p;
    private static final dd q;
    private static final dd r;
    private static final dd s;
    private static final dd t;
    private static final dd u;
    private static final dd v;
    private static final Map<Class<? extends dq>, dr> w;
    private static final int x = 0;
    private static final int y = 1;
    private static final int z = 2;
    private byte B;
    private e[] C;
    public String a;
    public String b;
    public String c;
    public int d;
    public int e;
    public int f;
    public ByteBuffer g;
    public String h;
    public String i;
    public int j;

    /* compiled from: UMEnvelope */
    private static class b implements dr {
        private b() {
        }

        public /* synthetic */ dq b() {
            return a();
        }

        public a a() {
            return new a();
        }
    }

    /* compiled from: UMEnvelope */
    private static class d implements dr {
        private d() {
        }

        public /* synthetic */ dq b() {
            return a();
        }

        public c a() {
            return new c();
        }
    }

    /* compiled from: UMEnvelope */
    public enum e implements cq {
        VERSION((short) 1, ShareRequestParam.REQ_PARAM_VERSION),
        ADDRESS((short) 2, "address"),
        SIGNATURE((short) 3, "signature"),
        SERIAL_NUM((short) 4, "serial_num"),
        TS_SECS((short) 5, "ts_secs"),
        LENGTH((short) 6, "length"),
        ENTITY((short) 7, Downloads.COLUMN_APP_DATA),
        GUID((short) 8, "guid"),
        CHECKSUM((short) 9, Keys.checksum),
        CODEX((short) 10, "codex");
        
        private static final Map<String, e> k;
        private final short l;
        private final String m;

        static {
            k = new HashMap();
            Iterator it = EnumSet.allOf(e.class).iterator();
            while (it.hasNext()) {
                e eVar = (e) it.next();
                k.put(eVar.b(), eVar);
            }
        }

        public static e a(int i) {
            switch (i) {
                case br.y /*1*/:
                    return VERSION;
                case br.z /*2*/:
                    return ADDRESS;
                case br.A /*3*/:
                    return SIGNATURE;
                case dx.e /*4*/:
                    return SERIAL_NUM;
                case dj.f /*5*/:
                    return TS_SECS;
                case ci.g /*6*/:
                    return LENGTH;
                case ci.h /*7*/:
                    return ENTITY;
                case h.g /*8*/:
                    return GUID;
                case h.h /*9*/:
                    return CHECKSUM;
                case h.i /*10*/:
                    return CODEX;
                default:
                    return null;
            }
        }

        public static e b(int i) {
            e a = a(i);
            if (a != null) {
                return a;
            }
            throw new IllegalArgumentException("Field " + i + " doesn't exist!");
        }

        public static e a(String str) {
            return (e) k.get(str);
        }

        private e(short s, String str) {
            this.l = s;
            this.m = str;
        }

        public short a() {
            return this.l;
        }

        public String b() {
            return this.m;
        }
    }

    /* compiled from: UMEnvelope */
    private static class a extends ds<br> {
        private a() {
        }

        public /* synthetic */ void a(di diVar, cj cjVar) throws cp {
            b(diVar, (br) cjVar);
        }

        public /* synthetic */ void b(di diVar, cj cjVar) throws cp {
            a(diVar, (br) cjVar);
        }

        public void a(di diVar, br brVar) throws cp {
            diVar.j();
            while (true) {
                dd l = diVar.l();
                if (l.b == null) {
                    diVar.k();
                    if (!brVar.o()) {
                        throw new dj("Required field 'serial_num' was not found in serialized data! Struct: " + toString());
                    } else if (!brVar.r()) {
                        throw new dj("Required field 'ts_secs' was not found in serialized data! Struct: " + toString());
                    } else if (brVar.u()) {
                        brVar.I();
                        return;
                    } else {
                        throw new dj("Required field 'length' was not found in serialized data! Struct: " + toString());
                    }
                }
                switch (l.c) {
                    case br.y /*1*/:
                        if (l.b != dp.i) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.a = diVar.z();
                        brVar.a(true);
                        break;
                    case br.z /*2*/:
                        if (l.b != dp.i) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.b = diVar.z();
                        brVar.b(true);
                        break;
                    case br.A /*3*/:
                        if (l.b != dp.i) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.c = diVar.z();
                        brVar.c(true);
                        break;
                    case dx.e /*4*/:
                        if (l.b != (byte) 8) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.d = diVar.w();
                        brVar.d(true);
                        break;
                    case dj.f /*5*/:
                        if (l.b != (byte) 8) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.e = diVar.w();
                        brVar.e(true);
                        break;
                    case ci.g /*6*/:
                        if (l.b != (byte) 8) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.f = diVar.w();
                        brVar.f(true);
                        break;
                    case ci.h /*7*/:
                        if (l.b != dp.i) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.g = diVar.A();
                        brVar.g(true);
                        break;
                    case h.g /*8*/:
                        if (l.b != dp.i) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.h = diVar.z();
                        brVar.h(true);
                        break;
                    case h.h /*9*/:
                        if (l.b != dp.i) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.i = diVar.z();
                        brVar.i(true);
                        break;
                    case h.i /*10*/:
                        if (l.b != (byte) 8) {
                            dl.a(diVar, l.b);
                            break;
                        }
                        brVar.j = diVar.w();
                        brVar.j(true);
                        break;
                    default:
                        dl.a(diVar, l.b);
                        break;
                }
                diVar.m();
            }
        }

        public void b(di diVar, br brVar) throws cp {
            brVar.I();
            diVar.a(br.l);
            if (brVar.a != null) {
                diVar.a(br.m);
                diVar.a(brVar.a);
                diVar.c();
            }
            if (brVar.b != null) {
                diVar.a(br.n);
                diVar.a(brVar.b);
                diVar.c();
            }
            if (brVar.c != null) {
                diVar.a(br.o);
                diVar.a(brVar.c);
                diVar.c();
            }
            diVar.a(br.p);
            diVar.a(brVar.d);
            diVar.c();
            diVar.a(br.q);
            diVar.a(brVar.e);
            diVar.c();
            diVar.a(br.r);
            diVar.a(brVar.f);
            diVar.c();
            if (brVar.g != null) {
                diVar.a(br.s);
                diVar.a(brVar.g);
                diVar.c();
            }
            if (brVar.h != null) {
                diVar.a(br.t);
                diVar.a(brVar.h);
                diVar.c();
            }
            if (brVar.i != null) {
                diVar.a(br.u);
                diVar.a(brVar.i);
                diVar.c();
            }
            if (brVar.H()) {
                diVar.a(br.v);
                diVar.a(brVar.j);
                diVar.c();
            }
            diVar.d();
            diVar.b();
        }
    }

    /* compiled from: UMEnvelope */
    private static class c extends dt<br> {
        private c() {
        }

        public void a(di diVar, br brVar) throws cp {
            do doVar = (do) diVar;
            doVar.a(brVar.a);
            doVar.a(brVar.b);
            doVar.a(brVar.c);
            doVar.a(brVar.d);
            doVar.a(brVar.e);
            doVar.a(brVar.f);
            doVar.a(brVar.g);
            doVar.a(brVar.h);
            doVar.a(brVar.i);
            BitSet bitSet = new BitSet();
            if (brVar.H()) {
                bitSet.set(br.x);
            }
            doVar.a(bitSet, br.y);
            if (brVar.H()) {
                doVar.a(brVar.j);
            }
        }

        public void b(di diVar, br brVar) throws cp {
            do doVar = (do) diVar;
            brVar.a = doVar.z();
            brVar.a(true);
            brVar.b = doVar.z();
            brVar.b(true);
            brVar.c = doVar.z();
            brVar.c(true);
            brVar.d = doVar.w();
            brVar.d(true);
            brVar.e = doVar.w();
            brVar.e(true);
            brVar.f = doVar.w();
            brVar.f(true);
            brVar.g = doVar.A();
            brVar.g(true);
            brVar.h = doVar.z();
            brVar.h(true);
            brVar.i = doVar.z();
            brVar.i(true);
            if (doVar.b(br.y).get(br.x)) {
                brVar.j = doVar.w();
                brVar.j(true);
            }
        }
    }

    public /* synthetic */ cq b(int i) {
        return f(i);
    }

    public /* synthetic */ cj g() {
        return a();
    }

    static {
        l = new dn("UMEnvelope");
        m = new dd(ShareRequestParam.REQ_PARAM_VERSION, dp.i, (short) 1);
        n = new dd("address", dp.i, (short) 2);
        o = new dd("signature", dp.i, (short) 3);
        p = new dd("serial_num", (byte) 8, (short) 4);
        q = new dd("ts_secs", (byte) 8, (short) 5);
        r = new dd("length", (byte) 8, (short) 6);
        s = new dd(Downloads.COLUMN_APP_DATA, dp.i, (short) 7);
        t = new dd("guid", dp.i, (short) 8);
        u = new dd(Keys.checksum, dp.i, (short) 9);
        v = new dd("codex", (byte) 8, (short) 10);
        w = new HashMap();
        w.put(ds.class, new b());
        w.put(dt.class, new d());
        Map enumMap = new EnumMap(e.class);
        enumMap.put(e.VERSION, new cv(ShareRequestParam.REQ_PARAM_VERSION, (byte) 1, new cw(dp.i)));
        enumMap.put(e.ADDRESS, new cv("address", (byte) 1, new cw(dp.i)));
        enumMap.put(e.SIGNATURE, new cv("signature", (byte) 1, new cw(dp.i)));
        enumMap.put(e.SERIAL_NUM, new cv("serial_num", (byte) 1, new cw((byte) 8)));
        enumMap.put(e.TS_SECS, new cv("ts_secs", (byte) 1, new cw((byte) 8)));
        enumMap.put(e.LENGTH, new cv("length", (byte) 1, new cw((byte) 8)));
        enumMap.put(e.ENTITY, new cv(Downloads.COLUMN_APP_DATA, (byte) 1, new cw((byte) dp.i, true)));
        enumMap.put(e.GUID, new cv("guid", (byte) 1, new cw(dp.i)));
        enumMap.put(e.CHECKSUM, new cv(Keys.checksum, (byte) 1, new cw(dp.i)));
        enumMap.put(e.CODEX, new cv("codex", (byte) 2, new cw((byte) 8)));
        k = Collections.unmodifiableMap(enumMap);
        cv.a(br.class, k);
    }

    public br() {
        this.B = (byte) 0;
        e[] eVarArr = new e[y];
        eVarArr[x] = e.CODEX;
        this.C = eVarArr;
    }

    public br(String str, String str2, String str3, int i, int i2, int i3, ByteBuffer byteBuffer, String str4, String str5) {
        this();
        this.a = str;
        this.b = str2;
        this.c = str3;
        this.d = i;
        d(true);
        this.e = i2;
        e(true);
        this.f = i3;
        f(true);
        this.g = byteBuffer;
        this.h = str4;
        this.i = str5;
    }

    public br(br brVar) {
        this.B = (byte) 0;
        e[] eVarArr = new e[y];
        eVarArr[x] = e.CODEX;
        this.C = eVarArr;
        this.B = brVar.B;
        if (brVar.e()) {
            this.a = brVar.a;
        }
        if (brVar.i()) {
            this.b = brVar.b;
        }
        if (brVar.l()) {
            this.c = brVar.c;
        }
        this.d = brVar.d;
        this.e = brVar.e;
        this.f = brVar.f;
        if (brVar.y()) {
            this.g = ck.d(brVar.g);
        }
        if (brVar.B()) {
            this.h = brVar.h;
        }
        if (brVar.E()) {
            this.i = brVar.i;
        }
        this.j = brVar.j;
    }

    public br a() {
        return new br(this);
    }

    public void b() {
        this.a = null;
        this.b = null;
        this.c = null;
        d(false);
        this.d = x;
        e(false);
        this.e = x;
        f(false);
        this.f = x;
        this.g = null;
        this.h = null;
        this.i = null;
        j(false);
        this.j = x;
    }

    public String c() {
        return this.a;
    }

    public br a(String str) {
        this.a = str;
        return this;
    }

    public void d() {
        this.a = null;
    }

    public boolean e() {
        return this.a != null;
    }

    public void a(boolean z) {
        if (!z) {
            this.a = null;
        }
    }

    public String f() {
        return this.b;
    }

    public br b(String str) {
        this.b = str;
        return this;
    }

    public void h() {
        this.b = null;
    }

    public boolean i() {
        return this.b != null;
    }

    public void b(boolean z) {
        if (!z) {
            this.b = null;
        }
    }

    public String j() {
        return this.c;
    }

    public br c(String str) {
        this.c = str;
        return this;
    }

    public void k() {
        this.c = null;
    }

    public boolean l() {
        return this.c != null;
    }

    public void c(boolean z) {
        if (!z) {
            this.c = null;
        }
    }

    public int m() {
        return this.d;
    }

    public br a(int i) {
        this.d = i;
        d(true);
        return this;
    }

    public void n() {
        this.B = cg.b(this.B, (int) x);
    }

    public boolean o() {
        return cg.a(this.B, (int) x);
    }

    public void d(boolean z) {
        this.B = cg.a(this.B, (int) x, z);
    }

    public int p() {
        return this.e;
    }

    public br c(int i) {
        this.e = i;
        e(true);
        return this;
    }

    public void q() {
        this.B = cg.b(this.B, (int) y);
    }

    public boolean r() {
        return cg.a(this.B, (int) y);
    }

    public void e(boolean z) {
        this.B = cg.a(this.B, (int) y, z);
    }

    public int s() {
        return this.f;
    }

    public br d(int i) {
        this.f = i;
        f(true);
        return this;
    }

    public void t() {
        this.B = cg.b(this.B, (int) z);
    }

    public boolean u() {
        return cg.a(this.B, (int) z);
    }

    public void f(boolean z) {
        this.B = cg.a(this.B, (int) z, z);
    }

    public byte[] v() {
        a(ck.c(this.g));
        return this.g == null ? null : this.g.array();
    }

    public ByteBuffer w() {
        return this.g;
    }

    public br a(byte[] bArr) {
        a(bArr == null ? (ByteBuffer) null : ByteBuffer.wrap(bArr));
        return this;
    }

    public br a(ByteBuffer byteBuffer) {
        this.g = byteBuffer;
        return this;
    }

    public void x() {
        this.g = null;
    }

    public boolean y() {
        return this.g != null;
    }

    public void g(boolean z) {
        if (!z) {
            this.g = null;
        }
    }

    public String z() {
        return this.h;
    }

    public br d(String str) {
        this.h = str;
        return this;
    }

    public void A() {
        this.h = null;
    }

    public boolean B() {
        return this.h != null;
    }

    public void h(boolean z) {
        if (!z) {
            this.h = null;
        }
    }

    public String C() {
        return this.i;
    }

    public br e(String str) {
        this.i = str;
        return this;
    }

    public void D() {
        this.i = null;
    }

    public boolean E() {
        return this.i != null;
    }

    public void i(boolean z) {
        if (!z) {
            this.i = null;
        }
    }

    public int F() {
        return this.j;
    }

    public br e(int i) {
        this.j = i;
        j(true);
        return this;
    }

    public void G() {
        this.B = cg.b(this.B, (int) A);
    }

    public boolean H() {
        return cg.a(this.B, (int) A);
    }

    public void j(boolean z) {
        this.B = cg.a(this.B, (int) A, z);
    }

    public e f(int i) {
        return e.a(i);
    }

    public void a(di diVar) throws cp {
        ((dr) w.get(diVar.D())).b().b(diVar, this);
    }

    public void b(di diVar) throws cp {
        ((dr) w.get(diVar.D())).b().a(diVar, this);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("UMEnvelope(");
        stringBuilder.append("version:");
        if (this.a == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.a);
        }
        stringBuilder.append(", ");
        stringBuilder.append("address:");
        if (this.b == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.b);
        }
        stringBuilder.append(", ");
        stringBuilder.append("signature:");
        if (this.c == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.c);
        }
        stringBuilder.append(", ");
        stringBuilder.append("serial_num:");
        stringBuilder.append(this.d);
        stringBuilder.append(", ");
        stringBuilder.append("ts_secs:");
        stringBuilder.append(this.e);
        stringBuilder.append(", ");
        stringBuilder.append("length:");
        stringBuilder.append(this.f);
        stringBuilder.append(", ");
        stringBuilder.append("entity:");
        if (this.g == null) {
            stringBuilder.append("null");
        } else {
            ck.a(this.g, stringBuilder);
        }
        stringBuilder.append(", ");
        stringBuilder.append("guid:");
        if (this.h == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.h);
        }
        stringBuilder.append(", ");
        stringBuilder.append("checksum:");
        if (this.i == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.i);
        }
        if (H()) {
            stringBuilder.append(", ");
            stringBuilder.append("codex:");
            stringBuilder.append(this.j);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public void I() throws cp {
        if (this.a == null) {
            throw new dj("Required field 'version' was not present! Struct: " + toString());
        } else if (this.b == null) {
            throw new dj("Required field 'address' was not present! Struct: " + toString());
        } else if (this.c == null) {
            throw new dj("Required field 'signature' was not present! Struct: " + toString());
        } else if (this.g == null) {
            throw new dj("Required field 'entity' was not present! Struct: " + toString());
        } else if (this.h == null) {
            throw new dj("Required field 'guid' was not present! Struct: " + toString());
        } else if (this.i == null) {
            throw new dj("Required field 'checksum' was not present! Struct: " + toString());
        }
    }

    private void a(ObjectOutputStream objectOutputStream) throws IOException {
        try {
            b(new dc(new du((OutputStream) objectOutputStream)));
        } catch (cp e) {
            throw new IOException(e.getMessage());
        }
    }

    private void a(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        try {
            this.B = (byte) 0;
            a(new dc(new du((InputStream) objectInputStream)));
        } catch (cp e) {
            throw new IOException(e.getMessage());
        }
    }
}
