package com.igexin.push.b;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class b extends SQLiteOpenHelper {
    SQLiteDatabase a;

    public b(Context context) {
        super(context, "pushsdk.db", null, 2);
        this.a = null;
    }

    private String a(String[] strArr, String[] strArr2, int i) {
        int i2 = 0;
        StringBuffer stringBuffer = new StringBuffer(" ");
        if (strArr.length == 1) {
            for (int i3 = 0; i3 < i; i3++) {
                stringBuffer.append(strArr[0] + " = '" + strArr2[i3] + "'");
                if (i3 < i - 1) {
                    stringBuffer.append(" or ");
                }
            }
        } else {
            while (i2 < i) {
                stringBuffer.append(strArr[i2] + " = '" + strArr2[i2] + "'");
                if (i2 < i - 1) {
                    stringBuffer.append(" and ");
                }
                i2++;
            }
        }
        return stringBuffer.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.database.Cursor a(java.lang.String r10, java.lang.String[] r11, java.lang.String[] r12, java.lang.String[] r13, java.lang.String r14) {
        /*
        r9 = this;
        r1 = 1;
        r8 = 0;
        r0 = r9.getReadableDatabase();
        r9.a = r0;
        r0 = r9.a;
        r0.beginTransaction();
        if (r11 != 0) goto L_0x0027;
    L_0x000f:
        r0 = r9.a;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r1 = r10;
        r2 = r13;
        r7 = r14;
        r0 = r0.query(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
    L_0x001c:
        r1 = r9.a;	 Catch:{ Exception -> 0x0083, all -> 0x007c }
        r1.setTransactionSuccessful();	 Catch:{ Exception -> 0x0083, all -> 0x007c }
        r1 = r9.a;
        r1.endTransaction();
    L_0x0026:
        return r0;
    L_0x0027:
        r0 = r11.length;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        if (r0 != r1) goto L_0x0062;
    L_0x002a:
        r0 = r12.length;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        if (r0 != r1) goto L_0x0050;
    L_0x002d:
        r0 = r9.a;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r1.<init>();	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r2 = 0;
        r2 = r11[r2];	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r1 = r1.append(r2);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r2 = "= ?";
        r1 = r1.append(r2);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r3 = r1.toString();	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r5 = 0;
        r6 = 0;
        r1 = r10;
        r2 = r13;
        r4 = r12;
        r7 = r14;
        r0 = r0.query(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        goto L_0x001c;
    L_0x0050:
        r0 = r9.a;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r1 = r12.length;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r3 = r9.a(r11, r12, r1);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r1 = r10;
        r2 = r13;
        r7 = r14;
        r0 = r0.query(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        goto L_0x001c;
    L_0x0062:
        r0 = r9.a;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r1 = r11.length;	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r3 = r9.a(r11, r12, r1);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r1 = r10;
        r2 = r13;
        r7 = r14;
        r0 = r0.query(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0074, all -> 0x007c }
        goto L_0x001c;
    L_0x0074:
        r0 = move-exception;
        r0 = r8;
    L_0x0076:
        r1 = r9.a;
        r1.endTransaction();
        goto L_0x0026;
    L_0x007c:
        r0 = move-exception;
        r1 = r9.a;
        r1.endTransaction();
        throw r0;
    L_0x0083:
        r1 = move-exception;
        goto L_0x0076;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.push.b.b.a(java.lang.String, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String):android.database.Cursor");
    }

    public void a(String str, ContentValues contentValues) {
        this.a = getWritableDatabase();
        this.a.beginTransaction();
        try {
            this.a.insert(str, null, contentValues);
            this.a.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            this.a.endTransaction();
        }
    }

    public void a(String str, ContentValues contentValues, String[] strArr, String[] strArr2) {
        this.a = getWritableDatabase();
        this.a.beginTransaction();
        if (strArr == null) {
            try {
                this.a.update(str, contentValues, null, null);
            } catch (Exception e) {
                this.a.endTransaction();
                return;
            } catch (Throwable th) {
                this.a.endTransaction();
            }
        } else if (strArr.length != 1) {
            this.a.update(str, contentValues, a(strArr, strArr2, strArr.length), null);
        } else if (strArr2.length == 1) {
            this.a.update(str, contentValues, strArr[0] + "='" + strArr2[0] + "'", null);
        } else {
            this.a.update(str, contentValues, a(strArr, strArr2, strArr2.length), null);
        }
        this.a.setTransactionSuccessful();
        this.a.endTransaction();
    }

    public void a(String str, String str2) {
        this.a = getWritableDatabase();
        this.a.delete(str, str2, null);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.execSQL("create table if not exists config (id integer primary key,value text)");
            sQLiteDatabase.execSQL("create table if not exists runtime (id integer primary key,value text)");
            sQLiteDatabase.execSQL("create table if not exists message (id integer primary key autoincrement,messageid text,taskid text,appid text,info text,msgextra blob,key text,status integer,createtime integer)");
            sQLiteDatabase.execSQL("create table if not exists ral (id integer primary key,data text,type integer,time integer)");
            sQLiteDatabase.execSQL("create table if not exists ca (pkgname text primary key,signature text,permissions text, accesstoken blob, expire integer)");
            sQLiteDatabase.execSQL("create table if not exists bi(id integer primary key autoincrement, start_service_count integer, login_count integer, loginerror_nonetwork_count integer, loginerror_timeout_count integer, loginerror_connecterror_count integer, loginerror_other_count integer, online_time long, network_time long, running_time long, create_time text, type integer)");
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            sQLiteDatabase.endTransaction();
        }
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        onUpgrade(sQLiteDatabase, i2, i);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        try {
            sQLiteDatabase.execSQL("drop table if exists config");
        } catch (Exception e) {
        }
        try {
            sQLiteDatabase.execSQL("drop table if exists runtime");
        } catch (Exception e2) {
        }
        try {
            sQLiteDatabase.execSQL("drop table if exists message");
        } catch (Exception e3) {
        }
        try {
            sQLiteDatabase.execSQL("drop table if exists ral");
        } catch (Exception e4) {
        }
        try {
            sQLiteDatabase.execSQL("drop table if exists ca");
        } catch (Exception e5) {
        }
        try {
            sQLiteDatabase.execSQL("drop table if exists bi");
        } catch (Exception e6) {
        }
        onCreate(sQLiteDatabase);
    }
}
