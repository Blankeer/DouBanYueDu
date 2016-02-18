package com.igexin.push.core.b;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.igexin.a.a.b.d;
import com.igexin.push.core.c.a;
import com.igexin.push.core.g;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class e implements a {
    private static e a;
    private Map b;
    private Map c;

    public e() {
        this.b = new HashMap();
        this.c = new HashMap();
    }

    private ContentValues a(h hVar) {
        if (hVar == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgname", hVar.a());
        contentValues.put("signature", hVar.d());
        contentValues.put("permissions", a(hVar.e()));
        if (hVar.b() == null || hVar.b().equals(Table.STRING_DEFAULT_VALUE)) {
            contentValues.put("accesstoken", Table.STRING_DEFAULT_VALUE);
        } else {
            contentValues.put("accesstoken", com.igexin.a.a.a.a.b(hVar.b().getBytes(), g.B));
            b(hVar.b(), hVar.a());
        }
        contentValues.put("expire", Long.valueOf(hVar.c()));
        return contentValues;
    }

    public static e a() {
        if (a == null) {
            a = new e();
        }
        return a;
    }

    private void a(ContentValues contentValues) {
        if (contentValues != null) {
            d.c().a(new f(this, contentValues), false, true);
        }
    }

    private void b() {
        d.c().a(new g(this), false, true);
    }

    private void b(String str, String str2) {
        this.c.put(str, str2);
    }

    private List c() {
        List arrayList = new ArrayList();
        h hVar = new h();
        List arrayList2 = new ArrayList();
        arrayList2.add(i.START_SERVICE);
        arrayList2.add(i.STOP_SERVICE);
        arrayList2.add(i.IS_STARTED);
        arrayList2.add(i.SET_SILENTTIME);
        hVar.a("com.igexin.pushmanager");
        hVar.c("308202133082017ca00302010202045080e7f1300d06092a864886f70d0101050500304e310b300906035504061302636e310b300906035504081302636e310b300906035504071302636e310b3009060355040a1302636e310b3009060355040b1302636e310b300906035504031302636e301e170d3132313031393035343130355a170d3232313031373035343130355a304e310b300906035504061302636e310b300906035504081302636e310b300906035504071302636e310b3009060355040a1302636e310b3009060355040b1302636e310b300906035504031302636e30819f300d06092a864886f70d010101050003818d0030818902818100805aee69ca3415ca32130b233fc07ad6eb666dcfe119efad8e5d0e4d51e175c6468a3869a5c131c342e5261a93f3bc30303ae0f23a3824d28df692092f8cf72ba7f2251f005ebfb1c1b210dc377aacf2168809f07e8d6756e6214c0288314388a2ead4a4453d358aa8cb1e2f02d1604c63cd0d075a558c718c43e3922f5198b50203010001300d06092a864886f70d0101050500038181004a4dc5634909f61710cf35229a63d7b8d2bfd89891d6ada1704b6c614d694cce35383cfb1fd8fed192dea23552413e74a9e1ff6e280246a6e30178a9b221b2dfee032cfc6acf660d62b514df92bbcf23e992a0543003705c679ba2fbae5acad0d89c6e44ee1cb05085d300ae60b7318472579007bde0e09ad75675a26a2f1c85");
        hVar.a(arrayList2);
        arrayList.add(hVar);
        return arrayList;
    }

    public h a(String str) {
        return (h) this.b.get(str);
    }

    public String a(List list) {
        String str = Table.STRING_DEFAULT_VALUE;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == i.START_SERVICE) {
                str = str + Constants.VIA_RESULT_SUCCESS;
            } else if (list.get(i) == i.STOP_SERVICE) {
                str = str + Constants.VIA_TO_TYPE_QQ_GROUP;
            } else if (list.get(i) == i.IS_STARTED) {
                str = str + Constants.VIA_SSO_LOGIN;
            } else if (list.get(i) == i.SET_SILENTTIME) {
                str = str + Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP;
            }
            if (i != list.size() - 1) {
                str = str + ",";
            }
        }
        return str;
    }

    public Map a(Map map, Map map2) {
        for (Entry entry : map.entrySet()) {
            map2.put(entry.getKey().toString(), (h) entry.getValue());
        }
        return map2;
    }

    public void a(SQLiteDatabase sQLiteDatabase) {
    }

    public void a(String str, h hVar) {
        this.b.put(str, hVar);
        a(a(hVar));
    }

    public void a(Map map) {
        Map a = a(this.b, new HashMap());
        b();
        this.b.clear();
        this.c.clear();
        for (Entry entry : map.entrySet()) {
            String obj = entry.getKey().toString();
            h hVar = (h) entry.getValue();
            h hVar2 = (h) a.get(obj);
            if (hVar2 != null) {
                hVar.b(hVar2.b());
                hVar.a(hVar2.c());
            }
            a(obj, hVar);
        }
    }

    public boolean a(String str, String str2) {
        h a = a(str);
        return a != null && a.d().equals(str2);
    }

    public String b(String str) {
        return (String) this.c.get(str);
    }

    public void b(SQLiteDatabase sQLiteDatabase) {
        Cursor rawQuery;
        Throwable th;
        Cursor cursor = null;
        try {
            rawQuery = sQLiteDatabase.rawQuery("select * from ca order by pkgname", null);
            if (rawQuery != null) {
                try {
                    if (rawQuery.getCount() > 0) {
                        while (rawQuery.moveToNext()) {
                            String str;
                            h hVar = new h();
                            hVar.a(rawQuery.getString(rawQuery.getColumnIndex("pkgname")));
                            hVar.c(rawQuery.getString(rawQuery.getColumnIndex("signature")));
                            hVar.a(c(rawQuery.getString(rawQuery.getColumnIndex("permissions"))));
                            byte[] blob = rawQuery.getBlob(rawQuery.getColumnIndex("accesstoken"));
                            if (blob != null) {
                                str = new String(com.igexin.a.a.a.a.a(blob, g.B));
                                b(str, hVar.a());
                            } else {
                                str = null;
                            }
                            hVar.b(str);
                            hVar.a(rawQuery.getLong(rawQuery.getColumnIndex("expire")));
                            this.b.put(hVar.a(), hVar);
                        }
                    } else {
                        List c = c();
                        for (int i = 0; i < c.size(); i++) {
                            h hVar2 = (h) c.get(i);
                            a(hVar2.a(), hVar2);
                        }
                    }
                } catch (Exception e) {
                    cursor = rawQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    throw th;
                }
            }
            if (rawQuery != null) {
                rawQuery.close();
            }
        } catch (Exception e2) {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th3) {
            Throwable th4 = th3;
            rawQuery = null;
            th = th4;
            if (rawQuery != null) {
                rawQuery.close();
            }
            throw th;
        }
    }

    public List c(String str) {
        List arrayList = new ArrayList();
        String[] split = str.split(",");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals(Constants.VIA_RESULT_SUCCESS)) {
                arrayList.add(i.START_SERVICE);
            } else if (split[i].equals(Constants.VIA_TO_TYPE_QQ_GROUP)) {
                arrayList.add(i.STOP_SERVICE);
            } else if (split[i].equals(Constants.VIA_SSO_LOGIN)) {
                arrayList.add(i.IS_STARTED);
            } else if (split[i].equals(Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP)) {
                arrayList.add(i.SET_SILENTTIME);
            }
        }
        return arrayList;
    }

    public void c(SQLiteDatabase sQLiteDatabase) {
    }
}
