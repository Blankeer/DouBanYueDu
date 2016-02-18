package org.json.alipay;

import com.alipay.sdk.protocol.h;
import com.douban.book.reader.constant.Char;
import com.tencent.connect.common.Constants;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public class c {
    public static final Object b;
    public Map a;

    private static final class a {
        private a() {
        }

        protected final Object clone() {
            return this;
        }

        public final boolean equals(Object obj) {
            return obj == null || obj == this;
        }

        public final String toString() {
            return "null";
        }
    }

    static {
        b = new a();
    }

    public c() {
        this.a = new HashMap();
    }

    public c(String str) {
        this(new d(str));
    }

    public c(Map map) {
        if (map == null) {
            map = new HashMap();
        }
        this.a = map;
    }

    public c(d dVar) {
        this();
        if (dVar.c() != '{') {
            throw dVar.a("A JSONObject text must begin with '{'");
        }
        while (true) {
            switch (dVar.c()) {
                case dx.a /*0*/:
                    throw dVar.a("A JSONObject text must end with '}'");
                case Header.STRING_0 /*125*/:
                    return;
                default:
                    dVar.a();
                    String obj = dVar.d().toString();
                    char c = dVar.c();
                    if (c == '=') {
                        if (dVar.b() != '>') {
                            dVar.a();
                        }
                    } else if (c != ':') {
                        throw dVar.a("Expected a ':' after a key");
                    }
                    Object d = dVar.d();
                    if (obj == null) {
                        throw new a("Null key.");
                    }
                    if (d != null) {
                        b(d);
                        this.a.put(obj, d);
                    } else {
                        this.a.remove(obj);
                    }
                    switch (dVar.c()) {
                        case HeaderMapDB.COMPARATOR_COMPARABLE_ARRAY /*44*/:
                        case Header.LONG_11 /*59*/:
                            if (dVar.c() != '}') {
                                dVar.a();
                            } else {
                                return;
                            }
                        case Header.STRING_0 /*125*/:
                            return;
                        default:
                            throw dVar.a("Expected a ',' or '}'");
                    }
            }
        }
    }

    static String a(Object obj) {
        if (obj == null || obj.equals(null)) {
            return "null";
        }
        if (!(obj instanceof Number)) {
            return ((obj instanceof Boolean) || (obj instanceof c) || (obj instanceof b)) ? obj.toString() : obj instanceof Map ? new c((Map) obj).toString() : obj instanceof Collection ? new b((Collection) obj).toString() : obj.getClass().isArray() ? new b(obj).toString() : b(obj.toString());
        } else {
            obj = (Number) obj;
            if (obj == null) {
                throw new a("Null pointer");
            }
            b(obj);
            String obj2 = obj.toString();
            if (obj2.indexOf(46) <= 0 || obj2.indexOf(Header.FLOAT) >= 0 || obj2.indexOf(69) >= 0) {
                return obj2;
            }
            while (obj2.endsWith(Constants.VIA_RESULT_SUCCESS)) {
                obj2 = obj2.substring(0, obj2.length() - 1);
            }
            return obj2.endsWith(".") ? obj2.substring(0, obj2.length() - 1) : obj2;
        }
    }

    public static String b(String str) {
        int i = 0;
        if (str == null || str.length() == 0) {
            return "\"\"";
        }
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length + 4);
        stringBuffer.append('\"');
        int i2 = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case h.g /*8*/:
                    stringBuffer.append("\\b");
                    break;
                case h.h /*9*/:
                    stringBuffer.append("\\t");
                    break;
                case h.i /*10*/:
                    stringBuffer.append("\\n");
                    break;
                case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                    stringBuffer.append("\\f");
                    break;
                case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                    stringBuffer.append("\\r");
                    break;
                case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
                case Header.SHORT_1 /*92*/:
                    stringBuffer.append('\\');
                    stringBuffer.append(charAt);
                    break;
                case HeaderMapDB.SERIALIZER_COMPRESSION_WRAPPER /*47*/:
                    if (i2 == 60) {
                        stringBuffer.append('\\');
                    }
                    stringBuffer.append(charAt);
                    break;
                default:
                    if (charAt >= Char.SPACE && ((charAt < '\u0080' || charAt >= '\u00a0') && (charAt < '\u2000' || charAt >= '\u2100'))) {
                        stringBuffer.append(charAt);
                        break;
                    }
                    String str2 = "000" + Integer.toHexString(charAt);
                    stringBuffer.append("\\u" + str2.substring(str2.length() - 4));
                    break;
                    break;
            }
            i++;
            char c = charAt;
        }
        stringBuffer.append('\"');
        return stringBuffer.toString();
    }

    private static void b(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Double) {
            if (((Double) obj).isInfinite() || ((Double) obj).isNaN()) {
                throw new a("JSON does not allow non-finite numbers.");
            }
        } else if (!(obj instanceof Float)) {
        } else {
            if (((Float) obj).isInfinite() || ((Float) obj).isNaN()) {
                throw new a("JSON does not allow non-finite numbers.");
            }
        }
    }

    private boolean c(String str) {
        return this.a.containsKey(str);
    }

    public final Object a(String str) {
        Object obj = str == null ? null : this.a.get(str);
        if (obj != null) {
            return obj;
        }
        throw new a("JSONObject[" + b(str) + "] not found.");
    }

    public final Iterator a() {
        return this.a.keySet().iterator();
    }

    public String toString() {
        try {
            Iterator a = a();
            StringBuffer stringBuffer = new StringBuffer("{");
            while (a.hasNext()) {
                if (stringBuffer.length() > 1) {
                    stringBuffer.append(',');
                }
                Object next = a.next();
                stringBuffer.append(b(next.toString()));
                stringBuffer.append(':');
                stringBuffer.append(a(this.a.get(next)));
            }
            stringBuffer.append('}');
            return stringBuffer.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
