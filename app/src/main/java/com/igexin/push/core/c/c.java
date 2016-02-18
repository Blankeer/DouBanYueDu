package com.igexin.push.core.c;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.a.a.b.d;
import com.igexin.a.b.a;
import com.igexin.push.core.bean.i;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.open.SocialConstants;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class c implements a {
    private static c a;
    private List b;

    private c() {
        this.b = new CopyOnWriteArrayList();
    }

    private int a(int i) {
        int i2 = 0;
        for (i c : this.b) {
            i2 = c.c() == i ? i2 + 1 : i2;
        }
        return i2;
    }

    public static c a() {
        if (a == null) {
            a = new c();
        }
        return a;
    }

    private static ContentValues b(i iVar) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorksListUri.KEY_ID, Long.valueOf(iVar.a()));
        contentValues.put(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA, a.b(iVar.b().getBytes()));
        contentValues.put(SocialConstants.PARAM_TYPE, Byte.valueOf(iVar.c()));
        contentValues.put("time", Long.valueOf(iVar.d()));
        return contentValues;
    }

    private i b(long j) {
        for (i iVar : this.b) {
            if (iVar.a() == j) {
                return iVar;
            }
        }
        return null;
    }

    public void a(SQLiteDatabase sQLiteDatabase) {
    }

    public void a(i iVar) {
        if (iVar != null && this.b.size() < 47) {
            switch (iVar.c()) {
                case dx.b /*1*/:
                    if (a(1) >= 1) {
                        return;
                    }
                    break;
                case dx.c /*2*/:
                    if (a(2) >= 3) {
                        return;
                    }
                    break;
                case dx.d /*3*/:
                    if (a(3) >= 30) {
                        return;
                    }
                    break;
                case dj.f /*5*/:
                    if (a(5) >= 3) {
                        return;
                    }
                    break;
                case ci.g /*6*/:
                    if (a(6) >= 10) {
                        return;
                    }
                    break;
            }
            this.b.add(iVar);
            d.c().a(new d(this, b(iVar)), false, true);
        }
    }

    public boolean a(long j) {
        i b = b(j);
        if (b == null) {
            return false;
        }
        this.b.remove(b);
        d.c().a(new e(this, b(b), j), true, false);
        return true;
    }

    public List b() {
        return this.b;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(android.database.sqlite.SQLiteDatabase r13) {
        /*
        r12 = this;
        r0 = 0;
        r1 = "select id,data,type,time from ral";
        r2 = 0;
        r0 = r13.rawQuery(r1, r2);	 Catch:{ Exception -> 0x0049, all -> 0x0056 }
        r8 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        if (r0 == 0) goto L_0x0050;
    L_0x000e:
        r1 = r0.moveToNext();	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        if (r1 == 0) goto L_0x0050;
    L_0x0014:
        r1 = 0;
        r2 = r0.getLong(r1);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r4 = new java.lang.String;	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r1 = 1;
        r1 = r0.getBlob(r1);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r1 = com.igexin.a.b.a.c(r1);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r4.<init>(r1);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r1 = 2;
        r1 = r0.getInt(r1);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r5 = (byte) r1;	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r1 = 3;
        r6 = r0.getLong(r1);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r10 = r12.b;	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r1 = new com.igexin.push.core.bean.i;	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r1.<init>(r2, r4, r5, r6);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r10.add(r1);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        r4 = r8 - r6;
        r6 = 86400000; // 0x5265c00 float:7.82218E-36 double:4.2687272E-316;
        r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r1 <= 0) goto L_0x000e;
    L_0x0045:
        r12.a(r2);	 Catch:{ Exception -> 0x0049, all -> 0x0060 }
        goto L_0x000e;
    L_0x0049:
        r1 = move-exception;
        if (r0 == 0) goto L_0x004f;
    L_0x004c:
        r0.close();
    L_0x004f:
        return;
    L_0x0050:
        if (r0 == 0) goto L_0x004f;
    L_0x0052:
        r0.close();
        goto L_0x004f;
    L_0x0056:
        r1 = move-exception;
        r11 = r1;
        r1 = r0;
        r0 = r11;
    L_0x005a:
        if (r1 == 0) goto L_0x005f;
    L_0x005c:
        r1.close();
    L_0x005f:
        throw r0;
    L_0x0060:
        r1 = move-exception;
        r11 = r1;
        r1 = r0;
        r0 = r11;
        goto L_0x005a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.push.core.c.c.b(android.database.sqlite.SQLiteDatabase):void");
    }

    public void c(SQLiteDatabase sQLiteDatabase) {
    }
}
