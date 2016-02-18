package u.aly;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* compiled from: PropertyValue */
public class bj extends ct<bj, a> {
    public static final Map<a, cv> a;
    private static final dn d;
    private static final dd e;
    private static final dd f;

    /* compiled from: PropertyValue */
    /* renamed from: u.aly.bj.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            a = new int[a.values().length];
            try {
                a[a.STRING_VALUE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                a[a.LONG_VALUE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* compiled from: PropertyValue */
    public enum a implements cq {
        STRING_VALUE((short) 1, "string_value"),
        LONG_VALUE((short) 2, "long_value");
        
        private static final Map<String, a> c;
        private final short d;
        private final String e;

        static {
            c = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                c.put(aVar.b(), aVar);
            }
        }

        public static a a(int i) {
            switch (i) {
                case dx.b /*1*/:
                    return STRING_VALUE;
                case dx.c /*2*/:
                    return LONG_VALUE;
                default:
                    return null;
            }
        }

        public static a b(int i) {
            a a = a(i);
            if (a != null) {
                return a;
            }
            throw new IllegalArgumentException("Field " + i + " doesn't exist!");
        }

        public static a a(String str) {
            return (a) c.get(str);
        }

        private a(short s, String str) {
            this.d = s;
            this.e = str;
        }

        public short a() {
            return this.d;
        }

        public String b() {
            return this.e;
        }
    }

    public /* synthetic */ cq b(int i) {
        return a(i);
    }

    protected /* synthetic */ cq b(short s) {
        return a(s);
    }

    public /* synthetic */ cj g() {
        return a();
    }

    static {
        d = new dn("PropertyValue");
        e = new dd("string_value", dp.i, (short) 1);
        f = new dd("long_value", (byte) 10, (short) 2);
        Map enumMap = new EnumMap(a.class);
        enumMap.put(a.STRING_VALUE, new cv("string_value", (byte) 3, new cw(dp.i)));
        enumMap.put(a.LONG_VALUE, new cv("long_value", (byte) 3, new cw((byte) 10)));
        a = Collections.unmodifiableMap(enumMap);
        cv.a(bj.class, a);
    }

    public bj(a aVar, Object obj) {
        super(aVar, obj);
    }

    public bj(bj bjVar) {
        super(bjVar);
    }

    public bj a() {
        return new bj(this);
    }

    public static bj a(String str) {
        bj bjVar = new bj();
        bjVar.b(str);
        return bjVar;
    }

    public static bj a(long j) {
        bj bjVar = new bj();
        bjVar.b(j);
        return bjVar;
    }

    protected void a(a aVar, Object obj) throws ClassCastException {
        switch (AnonymousClass1.a[aVar.ordinal()]) {
            case dx.b /*1*/:
                if (!(obj instanceof String)) {
                    throw new ClassCastException("Was expecting value of type String for field 'string_value', but got " + obj.getClass().getSimpleName());
                }
            case dx.c /*2*/:
                if (!(obj instanceof Long)) {
                    throw new ClassCastException("Was expecting value of type Long for field 'long_value', but got " + obj.getClass().getSimpleName());
                }
            default:
                throw new IllegalArgumentException("Unknown field id " + aVar);
        }
    }

    protected Object a(di diVar, dd ddVar) throws cp {
        a a = a.a(ddVar.c);
        if (a == null) {
            return null;
        }
        switch (AnonymousClass1.a[a.ordinal()]) {
            case dx.b /*1*/:
                if (ddVar.b == e.b) {
                    return diVar.z();
                }
                dl.a(diVar, ddVar.b);
                return null;
            case dx.c /*2*/:
                if (ddVar.b == f.b) {
                    return Long.valueOf(diVar.x());
                }
                dl.a(diVar, ddVar.b);
                return null;
            default:
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
        }
    }

    protected void c(di diVar) throws cp {
        switch (AnonymousClass1.a[((a) this.c).ordinal()]) {
            case dx.b /*1*/:
                diVar.a((String) this.b);
            case dx.c /*2*/:
                diVar.a(((Long) this.b).longValue());
            default:
                throw new IllegalStateException("Cannot write union with unknown field " + this.c);
        }
    }

    protected Object a(di diVar, short s) throws cp {
        a a = a.a((int) s);
        if (a != null) {
            switch (AnonymousClass1.a[a.ordinal()]) {
                case dx.b /*1*/:
                    return diVar.z();
                case dx.c /*2*/:
                    return Long.valueOf(diVar.x());
                default:
                    throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
        throw new dj("Couldn't find a field with field id " + s);
    }

    protected void d(di diVar) throws cp {
        switch (AnonymousClass1.a[((a) this.c).ordinal()]) {
            case dx.b /*1*/:
                diVar.a((String) this.b);
            case dx.c /*2*/:
                diVar.a(((Long) this.b).longValue());
            default:
                throw new IllegalStateException("Cannot write union with unknown field " + this.c);
        }
    }

    protected dd a(a aVar) {
        switch (AnonymousClass1.a[aVar.ordinal()]) {
            case dx.b /*1*/:
                return e;
            case dx.c /*2*/:
                return f;
            default:
                throw new IllegalArgumentException("Unknown field id " + aVar);
        }
    }

    protected dn c() {
        return d;
    }

    protected a a(short s) {
        return a.b(s);
    }

    public a a(int i) {
        return a.a(i);
    }

    public String d() {
        if (i() == a.STRING_VALUE) {
            return (String) j();
        }
        throw new RuntimeException("Cannot get field 'string_value' because union is currently set to " + a((a) i()).a);
    }

    public void b(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        this.c = a.STRING_VALUE;
        this.b = str;
    }

    public long e() {
        if (i() == a.LONG_VALUE) {
            return ((Long) j()).longValue();
        }
        throw new RuntimeException("Cannot get field 'long_value' because union is currently set to " + a((a) i()).a);
    }

    public void b(long j) {
        this.c = a.LONG_VALUE;
        this.b = Long.valueOf(j);
    }

    public boolean f() {
        return this.c == a.STRING_VALUE;
    }

    public boolean h() {
        return this.c == a.LONG_VALUE;
    }

    public boolean equals(Object obj) {
        if (obj instanceof bj) {
            return a((bj) obj);
        }
        return false;
    }

    public boolean a(bj bjVar) {
        return bjVar != null && i() == bjVar.i() && j().equals(bjVar.j());
    }

    public int b(bj bjVar) {
        int a = ck.a((Comparable) i(), (Comparable) bjVar.i());
        if (a == 0) {
            return ck.a(j(), bjVar.j());
        }
        return a;
    }

    public int hashCode() {
        return 0;
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
            a(new dc(new du((InputStream) objectInputStream)));
        } catch (cp e) {
            throw new IOException(e.getMessage());
        }
    }
}
