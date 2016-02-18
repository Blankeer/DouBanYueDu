package com.igexin.push.config;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.a.a.b.d;

public class a implements com.igexin.push.core.c.a {
    private static a a;

    public static a a() {
        if (a == null) {
            a = new a();
        }
        return a;
    }

    private void a(SQLiteDatabase sQLiteDatabase, int i) {
        sQLiteDatabase.delete(StatConstant.SP_KEY_CONFIG, "id = ?", new String[]{String.valueOf(i)});
    }

    private void a(SQLiteDatabase sQLiteDatabase, int i, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorksListUri.KEY_ID, Integer.valueOf(i));
        contentValues.put(Column.VALUE, str);
        sQLiteDatabase.replace(StatConstant.SP_KEY_CONFIG, null, contentValues);
    }

    private void a(SQLiteDatabase sQLiteDatabase, int i, byte[] bArr) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorksListUri.KEY_ID, Integer.valueOf(i));
        contentValues.put(Column.VALUE, bArr);
        sQLiteDatabase.replace(StatConstant.SP_KEY_CONFIG, null, contentValues);
    }

    public void a(SQLiteDatabase sQLiteDatabase) {
    }

    public void a(String str) {
        d.c().a(new h(this, str), true, false);
    }

    public void b() {
        d.c().a(new b(this), false, true);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(android.database.sqlite.SQLiteDatabase r7) {
        /*
        r6 = this;
        r1 = 0;
        r0 = "select id, value from config order by id";
        r2 = 0;
        r0 = r7.rawQuery(r0, r2);	 Catch:{ Exception -> 0x02b0, all -> 0x02ad }
        if (r0 == 0) goto L_0x028e;
    L_0x000a:
        r2 = r0.moveToNext();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 == 0) goto L_0x028e;
    L_0x0010:
        r2 = 0;
        r3 = 1;
        r4 = r0.getInt(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = 20;
        if (r4 == r2) goto L_0x0026;
    L_0x001a:
        r2 = 21;
        if (r4 == r2) goto L_0x0026;
    L_0x001e:
        r2 = 22;
        if (r4 == r2) goto L_0x0026;
    L_0x0022:
        r2 = 24;
        if (r4 != r2) goto L_0x008b;
    L_0x0026:
        r2 = r0.getBlob(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r3 = r1;
    L_0x002b:
        switch(r4) {
            case 1: goto L_0x002f;
            case 2: goto L_0x0097;
            case 3: goto L_0x00b7;
            case 4: goto L_0x00cd;
            case 5: goto L_0x00e1;
            case 6: goto L_0x00f5;
            case 7: goto L_0x0109;
            case 8: goto L_0x011d;
            case 9: goto L_0x0131;
            case 10: goto L_0x0145;
            case 11: goto L_0x0159;
            case 12: goto L_0x016d;
            case 13: goto L_0x0181;
            case 14: goto L_0x0195;
            case 15: goto L_0x01a9;
            case 16: goto L_0x01c7;
            case 17: goto L_0x01e5;
            case 18: goto L_0x01f9;
            case 19: goto L_0x020d;
            case 20: goto L_0x0221;
            case 21: goto L_0x0241;
            case 22: goto L_0x0252;
            case 23: goto L_0x0263;
            case 24: goto L_0x0277;
            default: goto L_0x002e;
        };	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
    L_0x002e:
        goto L_0x000a;
    L_0x002f:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 == 0) goto L_0x0092;
    L_0x0037:
        r2 = r1;
    L_0x0038:
        r2 = r2.intValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.a = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x003f:
        r2 = move-exception;
    L_0x0040:
        if (r0 == 0) goto L_0x0045;
    L_0x0042:
        r0.close();
    L_0x0045:
        r0 = "2.6.1.0";
        r2 = com.igexin.push.core.g.M;
        r0 = r0.equals(r2);
        if (r0 != 0) goto L_0x02ac;
    L_0x004f:
        r0 = com.igexin.push.config.l.t;
        if (r0 == 0) goto L_0x029a;
    L_0x0053:
        r0 = com.igexin.push.config.l.t;
        r0 = r0.b();
        r0 = r0.size();
        if (r0 <= 0) goto L_0x0295;
    L_0x005f:
        r0 = com.igexin.push.config.l.t;
        r0 = r0.b();
        r0 = r0.keySet();
        r2 = r0.iterator();
    L_0x006d:
        r0 = r2.hasNext();
        if (r0 == 0) goto L_0x0295;
    L_0x0073:
        r0 = com.igexin.push.config.l.t;
        r0 = r0.b();
        r3 = r2.next();
        r0 = r0.get(r3);
        r0 = (com.igexin.push.core.bean.e) r0;
        r0 = r0.c();
        com.igexin.push.f.b.a(r0);
        goto L_0x006d;
    L_0x008b:
        r2 = r0.getString(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r3 = r2;
        r2 = r1;
        goto L_0x002b;
    L_0x0092:
        r2 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x0038;
    L_0x0097:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 == 0) goto L_0x00b2;
    L_0x009f:
        r2 = r1;
    L_0x00a0:
        r2 = r2.intValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.b = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x00a8:
        r1 = move-exception;
        r5 = r1;
        r1 = r0;
        r0 = r5;
    L_0x00ac:
        if (r1 == 0) goto L_0x00b1;
    L_0x00ae:
        r1.close();
    L_0x00b1:
        throw r0;
    L_0x00b2:
        r2 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x00a0;
    L_0x00b7:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 == 0) goto L_0x00c8;
    L_0x00bf:
        r2 = r1;
    L_0x00c0:
        r2 = r2.longValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.c = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x00c8:
        r2 = java.lang.Long.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x00c0;
    L_0x00cd:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x00d5:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.f = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x00e1:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x00e9:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.g = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x00f5:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x00fd:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.h = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0109:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0111:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.i = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x011d:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0125:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.j = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0131:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0139:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.k = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0145:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x014d:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.n = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0159:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0161:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.o = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x016d:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0175:
        r2 = java.lang.Long.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.longValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.p = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0181:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0189:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.l = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0195:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x019d:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.m = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x01a9:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x01b1:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 == 0) goto L_0x01c2;
    L_0x01b9:
        r2 = r1;
    L_0x01ba:
        r2 = r2.intValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.d = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x01c2:
        r2 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x01ba;
    L_0x01c7:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x01cf:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 == 0) goto L_0x01e0;
    L_0x01d7:
        r2 = r1;
    L_0x01d8:
        r2 = r2.intValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.e = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x01e0:
        r2 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x01d8;
    L_0x01e5:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x01ed:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.q = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x01f9:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0201:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.r = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x020d:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x0215:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.s = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0221:
        if (r2 == 0) goto L_0x000a;
    L_0x0223:
        r3 = new java.lang.String;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r4 = com.igexin.push.core.g.B;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = com.igexin.a.a.a.a.a(r2, r4);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r3.<init>(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r3 == 0) goto L_0x000a;
    L_0x0230:
        r2 = com.igexin.push.core.a.e.a();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r4 = new org.json.JSONObject;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r4.<init>(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.a(r4);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.t = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0241:
        if (r2 == 0) goto L_0x000a;
    L_0x0243:
        r3 = new java.lang.String;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r4 = com.igexin.push.core.g.B;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = com.igexin.a.a.a.a.a(r2, r4);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r3.<init>(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.u = r3;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0252:
        if (r2 == 0) goto L_0x000a;
    L_0x0254:
        r3 = new java.lang.String;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r4 = com.igexin.push.core.g.B;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = com.igexin.a.a.a.a.a(r2, r4);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r3.<init>(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.v = r3;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0263:
        r2 = "null";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        if (r2 != 0) goto L_0x000a;
    L_0x026b:
        r2 = java.lang.Boolean.valueOf(r3);	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        r2 = r2.booleanValue();	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        com.igexin.push.config.l.w = r2;	 Catch:{ Exception -> 0x003f, all -> 0x00a8 }
        goto L_0x000a;
    L_0x0277:
        if (r2 == 0) goto L_0x000a;
    L_0x0279:
        r3 = new java.lang.String;	 Catch:{ Exception -> 0x028b, all -> 0x00a8 }
        r4 = com.igexin.push.core.g.B;	 Catch:{ Exception -> 0x028b, all -> 0x00a8 }
        r2 = com.igexin.a.a.a.a.a(r2, r4);	 Catch:{ Exception -> 0x028b, all -> 0x00a8 }
        r3.<init>(r2);	 Catch:{ Exception -> 0x028b, all -> 0x00a8 }
        if (r3 == 0) goto L_0x000a;
    L_0x0286:
        com.igexin.push.config.n.a(r3);	 Catch:{ Exception -> 0x028b, all -> 0x00a8 }
        goto L_0x000a;
    L_0x028b:
        r2 = move-exception;
        goto L_0x000a;
    L_0x028e:
        if (r0 == 0) goto L_0x0045;
    L_0x0290:
        r0.close();
        goto L_0x0045;
    L_0x0295:
        com.igexin.push.config.l.t = r1;
        r6.h();
    L_0x029a:
        r0 = com.igexin.push.core.c.f.a();
        r1 = "2.6.1.0";
        r0.c(r1);
        r0 = com.igexin.push.core.c.f.a();
        r2 = 0;
        r0.g(r2);
    L_0x02ac:
        return;
    L_0x02ad:
        r0 = move-exception;
        goto L_0x00ac;
    L_0x02b0:
        r0 = move-exception;
        r0 = r1;
        goto L_0x0040;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.push.config.a.b(android.database.sqlite.SQLiteDatabase):void");
    }

    public void c() {
        d.c().a(new c(this), false, true);
    }

    public void c(SQLiteDatabase sQLiteDatabase) {
        a(sQLiteDatabase, 1, String.valueOf(l.a));
        a(sQLiteDatabase, 2, String.valueOf(l.b));
        a(sQLiteDatabase, 3, String.valueOf(l.c));
        a(sQLiteDatabase, 4, String.valueOf(l.f));
        a(sQLiteDatabase, 5, String.valueOf(l.g));
        a(sQLiteDatabase, 6, String.valueOf(l.h));
        a(sQLiteDatabase, 7, String.valueOf(l.i));
        a(sQLiteDatabase, 8, String.valueOf(l.j));
        a(sQLiteDatabase, 9, String.valueOf(l.k));
        a(sQLiteDatabase, 10, String.valueOf(l.n));
        a(sQLiteDatabase, 11, String.valueOf(l.o));
        a(sQLiteDatabase, 12, String.valueOf(l.p));
        a(sQLiteDatabase, 13, String.valueOf(l.l));
        a(sQLiteDatabase, 14, String.valueOf(l.m));
        a(sQLiteDatabase, 15, String.valueOf(l.d));
        a(sQLiteDatabase, 3, String.valueOf(l.c));
        a(sQLiteDatabase, 17, String.valueOf(l.q));
        a(sQLiteDatabase, 18, String.valueOf(l.r));
        a(sQLiteDatabase, 19, String.valueOf(l.s));
    }

    public void d() {
        d.c().a(new d(this), false, true);
    }

    public void e() {
        d.c().a(new e(this), false, true);
    }

    public void f() {
        d.c().a(new f(this), false, true);
    }

    public void g() {
        d.c().a(new g(this), true, false);
    }

    public void h() {
        d.c().a(new i(this), true, false);
    }
}
