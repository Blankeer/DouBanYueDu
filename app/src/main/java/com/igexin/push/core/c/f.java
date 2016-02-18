package com.igexin.push.core.c;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.a.a.a.a;
import com.igexin.a.a.b.d;
import com.igexin.push.config.k;
import com.igexin.push.core.g;
import com.igexin.push.f.b;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dx;

public class f implements a {
    private static final String a;
    private static f b;
    private Map c;

    static {
        a = k.a + "_RuntimeDataManager";
    }

    private f() {
        this.c = new HashMap();
    }

    public static f a() {
        if (b == null) {
            b = new f();
        }
        return b;
    }

    private void a(SQLiteDatabase sQLiteDatabase, int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorksListUri.KEY_ID, Integer.valueOf(i));
        contentValues.put(Column.VALUE, str);
        sQLiteDatabase.replace("runtime", null, contentValues);
    }

    private void a(SQLiteDatabase sQLiteDatabase, int i, byte[] bArr) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorksListUri.KEY_ID, Integer.valueOf(i));
        contentValues.put(Column.VALUE, bArr);
        sQLiteDatabase.replace("runtime", null, contentValues);
    }

    private boolean e() {
        return d.c().a(new o(this), false, true);
    }

    private byte[] e(String str) {
        return com.igexin.push.f.d.a(str.getBytes());
    }

    private void f() {
        b.a();
        String c = b.c();
        if (c == null || c.length() <= 5) {
            b.e();
        }
    }

    private String g() {
        String str = Table.STRING_DEFAULT_VALUE;
        Random random = new Random(Math.abs(new Random().nextLong()));
        for (int i = 0; i < 15; i++) {
            str = str + random.nextInt(10);
        }
        return str;
    }

    public void a(SQLiteDatabase sQLiteDatabase) {
    }

    public boolean a(int i) {
        g.U = i;
        d.c().a(new n(this), false, true);
        return true;
    }

    public boolean a(long j) {
        g.a(j);
        d.c().a(new q(this), false, true);
        return true;
    }

    public boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        g.x = str;
        return d.c().a(new p(this), false, true);
    }

    public boolean a(String str, String str2, long j) {
        g.r = j;
        if (TextUtils.isEmpty(g.z)) {
            g.z = str2;
        }
        g.s = str;
        return e();
    }

    public boolean a(boolean z) {
        if (g.N == z) {
            return false;
        }
        g.N = z;
        d.c().a(new k(this), false, true);
        return true;
    }

    public void b() {
        d.c().a(new g(this), false, true);
    }

    public void b(SQLiteDatabase sQLiteDatabase) {
        Cursor rawQuery;
        Throwable th;
        long parseLong;
        String b;
        Cursor cursor = null;
        try {
            rawQuery = sQLiteDatabase.rawQuery("select id, value from runtime order by id", null);
            if (rawQuery != null) {
                while (rawQuery.moveToNext()) {
                    String string;
                    byte[] bArr;
                    int i = rawQuery.getInt(0);
                    if (i != 1 && i != 14 && i != 19 && i != 20) {
                        try {
                            string = rawQuery.getString(1);
                            bArr = null;
                        } catch (Exception e) {
                        } catch (Throwable th2) {
                            Throwable th3 = th2;
                            cursor = rawQuery;
                            th = th3;
                            break;
                        }
                    }
                    bArr = rawQuery.getBlob(1);
                    string = null;
                    switch (i) {
                        case dx.b /*1*/:
                            string = new String(a.a(bArr, g.B));
                            if (string != null) {
                                try {
                                    if (!string.equals("null")) {
                                        parseLong = Long.parseLong(string);
                                        g.r = parseLong;
                                        break;
                                    }
                                } catch (Exception e2) {
                                    g.r = 0;
                                    break;
                                } catch (Throwable th22) {
                                    Throwable th32 = th22;
                                    cursor = rawQuery;
                                    th = th32;
                                    break;
                                }
                            }
                            parseLong = 0;
                            g.r = parseLong;
                        case dx.c /*2*/:
                            if (string.equals("null")) {
                                string = null;
                            }
                            g.z = string;
                            break;
                        case dx.d /*3*/:
                            if (string.equals("null")) {
                                string = null;
                            }
                            g.A = string;
                            break;
                        case dx.e /*4*/:
                            g.l = string.equals("null") ? true : Boolean.parseBoolean(string);
                            break;
                        case ci.g /*6*/:
                            g.F = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case ci.h /*7*/:
                            g.G = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case h.g /*8*/:
                            g.H = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case h.h /*9*/:
                            g.P = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case h.i /*10*/:
                            g.Q = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                            g.K = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                            g.L = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                            if (string.equals("null")) {
                                string = null;
                            }
                            g.M = string;
                            break;
                        case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                            g.au = new String(a.a(bArr, g.B));
                            break;
                        case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                            if (!string.equals("null")) {
                                g.N = Boolean.parseBoolean(string);
                                break;
                            }
                            break;
                        case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                            g.O = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        case R.styleable.StickyListHeadersListView_android_choiceMode /*17*/:
                            if (string.equals("null")) {
                                string = null;
                            }
                            g.S = string;
                            break;
                        case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                            g.U = string.equals("null") ? 0 : Integer.parseInt(string);
                            break;
                        case Encoder.LINE_GROUPS /*19*/:
                            string = new String(a.a(bArr, g.B));
                            if (string.equals("null")) {
                                string = null;
                            }
                            g.x = string;
                            break;
                        case R.styleable.StickyListHeadersListView_android_fastScrollAlwaysVisible /*20*/:
                            string = new String(a.a(bArr, g.B));
                            if (string.equals("null")) {
                                string = null;
                            }
                            g.t = string;
                            g.s = string;
                            break;
                        case R.styleable.StickyListHeadersListView_android_requiresFadingEdge /*21*/:
                            g.aw = string.equals("null") ? 0 : Long.parseLong(string);
                            break;
                        default:
                            break;
                    }
                }
            }
            if (rawQuery != null) {
                rawQuery.close();
            }
        } catch (Exception e3) {
            rawQuery = null;
            if (rawQuery != null) {
                rawQuery.close();
            }
            if (g.r == 0) {
                parseLong = b.d();
                if (parseLong != 0) {
                    g.r = parseLong;
                    a(sQLiteDatabase, 1, com.igexin.push.f.d.a(String.valueOf(parseLong).getBytes()));
                }
            }
            if (g.s == null) {
                b = b.b();
                if (b != null) {
                    g.t = b;
                    g.s = b;
                    a(sQLiteDatabase, 20, com.igexin.push.f.d.a(String.valueOf(g.s).getBytes()));
                }
            }
            g.t = com.igexin.a.b.a.a(String.valueOf(g.r));
            g.a(g.r);
            a(sQLiteDatabase, 20, com.igexin.push.f.d.a(String.valueOf(g.s).getBytes()));
            if ("cfcd208495d565ef66e7dff9f98764da".equals(g.s)) {
                if (g.r == 0) {
                    g.t = null;
                    g.s = null;
                    g.r = 0;
                } else {
                    a().a(g.r);
                    g.t = g.s;
                    b.f();
                }
            }
            g.au = com.igexin.a.b.a.a(32);
            a(sQLiteDatabase, 14, com.igexin.push.f.d.a(g.au.getBytes()));
            b = b.c();
            g.z = b;
            a(sQLiteDatabase, 2, String.valueOf(g.z));
            if (g.A == null) {
                b = g.u;
                if (b == null) {
                    b = "V" + g();
                }
                g.A = "A-" + b + "-" + System.currentTimeMillis();
                a(sQLiteDatabase, 3, String.valueOf(g.A));
            }
        } catch (Throwable th4) {
            th = th4;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        if (g.r == 0) {
            parseLong = b.d();
            if (parseLong != 0) {
                g.r = parseLong;
                a(sQLiteDatabase, 1, com.igexin.push.f.d.a(String.valueOf(parseLong).getBytes()));
            }
        }
        if (g.s == null) {
            b = b.b();
            if (b != null) {
                g.t = b;
                g.s = b;
                a(sQLiteDatabase, 20, com.igexin.push.f.d.a(String.valueOf(g.s).getBytes()));
            }
        }
        if (g.s == null && g.r != 0) {
            g.t = com.igexin.a.b.a.a(String.valueOf(g.r));
            g.a(g.r);
            a(sQLiteDatabase, 20, com.igexin.push.f.d.a(String.valueOf(g.s).getBytes()));
        }
        if ("cfcd208495d565ef66e7dff9f98764da".equals(g.s)) {
            if (g.r == 0) {
                a().a(g.r);
                g.t = g.s;
                b.f();
            } else {
                g.t = null;
                g.s = null;
                g.r = 0;
            }
        }
        if (TextUtils.isEmpty(g.au) || "null".equals(g.au)) {
            g.au = com.igexin.a.b.a.a(32);
            a(sQLiteDatabase, 14, com.igexin.push.f.d.a(g.au.getBytes()));
        }
        b = b.c();
        if (g.z == null && b != null && b.length() > 5) {
            g.z = b;
            a(sQLiteDatabase, 2, String.valueOf(g.z));
        }
        if (g.A == null) {
            b = g.u;
            if (b == null) {
                b = "V" + g();
            }
            g.A = "A-" + b + "-" + System.currentTimeMillis();
            a(sQLiteDatabase, 3, String.valueOf(g.A));
        }
    }

    public boolean b(long j) {
        if (g.P == j) {
            return false;
        }
        g.P = j;
        d.c().a(new s(this), false, true);
        return true;
    }

    public boolean b(String str) {
        g.z = str;
        d.c().a(new r(this), false, true);
        return true;
    }

    public void c(SQLiteDatabase sQLiteDatabase) {
        a(sQLiteDatabase, 1, a.b(String.valueOf(g.r).getBytes(), g.B));
        a(sQLiteDatabase, 4, String.valueOf(g.l));
        a(sQLiteDatabase, 8, String.valueOf(g.H));
        a(sQLiteDatabase, 7, String.valueOf(g.G));
        a(sQLiteDatabase, 6, String.valueOf(g.F));
        a(sQLiteDatabase, 9, String.valueOf(g.P));
        a(sQLiteDatabase, 10, String.valueOf(g.Q));
        a(sQLiteDatabase, 3, String.valueOf(g.A));
        a(sQLiteDatabase, 11, String.valueOf(g.K));
        a(sQLiteDatabase, 12, String.valueOf(g.L));
        a(sQLiteDatabase, 20, a.b(String.valueOf(g.s).getBytes(), g.B));
        a(sQLiteDatabase, 2, String.valueOf(g.z));
    }

    public boolean c() {
        g.r = 0;
        g.s = null;
        return e();
    }

    public boolean c(long j) {
        if (g.L == j) {
            return false;
        }
        g.L = j;
        d.c().a(new t(this), false, true);
        return true;
    }

    public boolean c(String str) {
        if (str == null || str.equals(g.M)) {
            return false;
        }
        g.M = str;
        d.c().a(new j(this), false, true);
        return true;
    }

    public Map d() {
        return this.c;
    }

    public boolean d(long j) {
        g.aw = j;
        com.igexin.a.a.c.a.b(a + " save idc config failed time : " + j);
        d.c().a(new u(this, j), false, true);
        return true;
    }

    public boolean d(String str) {
        if (g.S == str) {
            return false;
        }
        g.S = str;
        d.c().a(new m(this), false, true);
        return true;
    }

    public boolean e(long j) {
        if (g.Q == j) {
            return false;
        }
        g.Q = j;
        d.c().a(new v(this), false, true);
        return true;
    }

    public boolean f(long j) {
        if (g.G == j) {
            return false;
        }
        g.G = j;
        d.c().a(new h(this), false, true);
        return true;
    }

    public boolean g(long j) {
        if (g.K == j) {
            return false;
        }
        g.K = j;
        d.c().a(new i(this), false, true);
        return true;
    }

    public boolean h(long j) {
        if (g.O == j) {
            return false;
        }
        g.O = j;
        d.c().a(new l(this), false, true);
        return true;
    }
}
