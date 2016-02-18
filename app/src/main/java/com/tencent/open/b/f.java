package com.tencent.open.b;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.tencent.open.utils.Global;

/* compiled from: ProGuard */
public class f extends SQLiteOpenHelper {
    protected static final String[] a;
    protected static f b;

    static {
        a = new String[]{"key"};
    }

    public static synchronized f a() {
        f fVar;
        synchronized (f.class) {
            if (b == null) {
                b = new f(Global.getContext());
            }
            fVar = b;
        }
        return fVar;
    }

    public f(Context context) {
        super(context, "sdk_report.db", null, 2);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS via_cgi_report( _id INTEGER PRIMARY KEY,key TEXT,type TEXT,blob BLOB);");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS via_cgi_report");
        onCreate(sQLiteDatabase);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.util.List<java.io.Serializable> a(java.lang.String r12) {
        /*
        r11 = this;
        r9 = 0;
        monitor-enter(r11);
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x00ab }
        r0.<init>();	 Catch:{ all -> 0x00ab }
        r8 = java.util.Collections.synchronizedList(r0);	 Catch:{ all -> 0x00ab }
        r0 = android.text.TextUtils.isEmpty(r12);	 Catch:{ all -> 0x00ab }
        if (r0 == 0) goto L_0x0014;
    L_0x0011:
        r0 = r8;
    L_0x0012:
        monitor-exit(r11);
        return r0;
    L_0x0014:
        r0 = r11.getReadableDatabase();	 Catch:{ all -> 0x00ab }
        if (r0 != 0) goto L_0x001c;
    L_0x001a:
        r0 = r8;
        goto L_0x0012;
    L_0x001c:
        r10 = 0;
        r1 = "via_cgi_report";
        r2 = 0;
        r3 = "type = ?";
        r4 = 1;
        r4 = new java.lang.String[r4];	 Catch:{ Exception -> 0x00df, all -> 0x00b8 }
        r5 = 0;
        r4[r5] = r12;	 Catch:{ Exception -> 0x00df, all -> 0x00b8 }
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r3 = r0.query(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x00df, all -> 0x00b8 }
        if (r3 == 0) goto L_0x0067;
    L_0x0031:
        r1 = r3.getCount();	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
        if (r1 <= 0) goto L_0x0067;
    L_0x0037:
        r3.moveToFirst();	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
    L_0x003a:
        r1 = "blob";
        r1 = r3.getColumnIndex(r1);	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
        r1 = r3.getBlob(r1);	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
        r4 = new java.io.ByteArrayInputStream;	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
        r4.<init>(r1);	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
        r2 = new java.io.ObjectInputStream;	 Catch:{ Exception -> 0x0078, all -> 0x0087 }
        r2.<init>(r4);	 Catch:{ Exception -> 0x0078, all -> 0x0087 }
        r1 = r2.readObject();	 Catch:{ Exception -> 0x00e4, all -> 0x00e2 }
        r1 = (java.io.Serializable) r1;	 Catch:{ Exception -> 0x00e4, all -> 0x00e2 }
        if (r2 == 0) goto L_0x0059;
    L_0x0056:
        r2.close();	 Catch:{ IOException -> 0x00cf }
    L_0x0059:
        r4.close();	 Catch:{ IOException -> 0x00d1 }
    L_0x005c:
        if (r1 == 0) goto L_0x0061;
    L_0x005e:
        r8.add(r1);	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
    L_0x0061:
        r1 = r3.moveToNext();	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
        if (r1 != 0) goto L_0x003a;
    L_0x0067:
        if (r3 == 0) goto L_0x006c;
    L_0x0069:
        r3.close();	 Catch:{ all -> 0x00ab }
    L_0x006c:
        if (r9 == 0) goto L_0x0071;
    L_0x006e:
        r10.close();	 Catch:{ IOException -> 0x00ae }
    L_0x0071:
        if (r0 == 0) goto L_0x0076;
    L_0x0073:
        r0.close();	 Catch:{ all -> 0x00ab }
    L_0x0076:
        r0 = r8;
        goto L_0x0012;
    L_0x0078:
        r1 = move-exception;
        r1 = r9;
    L_0x007a:
        if (r1 == 0) goto L_0x007f;
    L_0x007c:
        r1.close();	 Catch:{ IOException -> 0x00d3 }
    L_0x007f:
        r4.close();	 Catch:{ IOException -> 0x0084 }
        r1 = r9;
        goto L_0x005c;
    L_0x0084:
        r1 = move-exception;
        r1 = r9;
        goto L_0x005c;
    L_0x0087:
        r1 = move-exception;
        r2 = r9;
    L_0x0089:
        if (r2 == 0) goto L_0x008e;
    L_0x008b:
        r2.close();	 Catch:{ IOException -> 0x00d5 }
    L_0x008e:
        r4.close();	 Catch:{ IOException -> 0x00d7 }
    L_0x0091:
        throw r1;	 Catch:{ Exception -> 0x0092, all -> 0x00d9 }
    L_0x0092:
        r1 = move-exception;
        r1 = r3;
    L_0x0094:
        r2 = "openSDK_LOG.ReportDatabaseHelper";
        r3 = "getReportItemFromDB has exception.";
        com.tencent.open.a.f.e(r2, r3);	 Catch:{ all -> 0x00db }
        if (r1 == 0) goto L_0x00a0;
    L_0x009d:
        r1.close();	 Catch:{ all -> 0x00ab }
    L_0x00a0:
        if (r9 == 0) goto L_0x00a5;
    L_0x00a2:
        r10.close();	 Catch:{ IOException -> 0x00b3 }
    L_0x00a5:
        if (r0 == 0) goto L_0x0076;
    L_0x00a7:
        r0.close();	 Catch:{ all -> 0x00ab }
        goto L_0x0076;
    L_0x00ab:
        r0 = move-exception;
        monitor-exit(r11);
        throw r0;
    L_0x00ae:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ all -> 0x00ab }
        goto L_0x0071;
    L_0x00b3:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ all -> 0x00ab }
        goto L_0x00a5;
    L_0x00b8:
        r1 = move-exception;
        r3 = r9;
    L_0x00ba:
        if (r3 == 0) goto L_0x00bf;
    L_0x00bc:
        r3.close();	 Catch:{ all -> 0x00ab }
    L_0x00bf:
        if (r9 == 0) goto L_0x00c4;
    L_0x00c1:
        r10.close();	 Catch:{ IOException -> 0x00ca }
    L_0x00c4:
        if (r0 == 0) goto L_0x00c9;
    L_0x00c6:
        r0.close();	 Catch:{ all -> 0x00ab }
    L_0x00c9:
        throw r1;	 Catch:{ all -> 0x00ab }
    L_0x00ca:
        r2 = move-exception;
        r2.printStackTrace();	 Catch:{ all -> 0x00ab }
        goto L_0x00c4;
    L_0x00cf:
        r2 = move-exception;
        goto L_0x0059;
    L_0x00d1:
        r2 = move-exception;
        goto L_0x005c;
    L_0x00d3:
        r1 = move-exception;
        goto L_0x007f;
    L_0x00d5:
        r2 = move-exception;
        goto L_0x008e;
    L_0x00d7:
        r2 = move-exception;
        goto L_0x0091;
    L_0x00d9:
        r1 = move-exception;
        goto L_0x00ba;
    L_0x00db:
        r2 = move-exception;
        r3 = r1;
        r1 = r2;
        goto L_0x00ba;
    L_0x00df:
        r1 = move-exception;
        r1 = r9;
        goto L_0x0094;
    L_0x00e2:
        r1 = move-exception;
        goto L_0x0089;
    L_0x00e4:
        r1 = move-exception;
        r1 = r2;
        goto L_0x007a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.open.b.f.a(java.lang.String):java.util.List<java.io.Serializable>");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void a(java.lang.String r9, java.util.List<java.io.Serializable> r10) {
        /*
        r8 = this;
        r2 = 0;
        r1 = 20;
        monitor-enter(r8);
        r0 = r10.size();	 Catch:{ all -> 0x008f }
        if (r0 != 0) goto L_0x000c;
    L_0x000a:
        monitor-exit(r8);
        return;
    L_0x000c:
        if (r0 > r1) goto L_0x0064;
    L_0x000e:
        r4 = r0;
    L_0x000f:
        r0 = android.text.TextUtils.isEmpty(r9);	 Catch:{ all -> 0x008f }
        if (r0 != 0) goto L_0x000a;
    L_0x0015:
        r8.b(r9);	 Catch:{ all -> 0x008f }
        r5 = r8.getWritableDatabase();	 Catch:{ all -> 0x008f }
        if (r5 == 0) goto L_0x000a;
    L_0x001e:
        r5.beginTransaction();	 Catch:{ all -> 0x008f }
        r6 = new android.content.ContentValues;	 Catch:{ Exception -> 0x007d }
        r6.<init>();	 Catch:{ Exception -> 0x007d }
        r0 = 0;
        r3 = r0;
    L_0x0028:
        if (r3 >= r4) goto L_0x0092;
    L_0x002a:
        r0 = r10.get(r3);	 Catch:{ Exception -> 0x007d }
        r0 = (java.io.Serializable) r0;	 Catch:{ Exception -> 0x007d }
        if (r0 == 0) goto L_0x005d;
    L_0x0032:
        r1 = "type";
        r6.put(r1, r9);	 Catch:{ Exception -> 0x007d }
        r7 = new java.io.ByteArrayOutputStream;	 Catch:{ Exception -> 0x007d }
        r1 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        r7.<init>(r1);	 Catch:{ Exception -> 0x007d }
        r1 = new java.io.ObjectOutputStream;	 Catch:{ IOException -> 0x0066, all -> 0x0073 }
        r1.<init>(r7);	 Catch:{ IOException -> 0x0066, all -> 0x0073 }
        r1.writeObject(r0);	 Catch:{ IOException -> 0x00b6, all -> 0x00b3 }
        if (r1 == 0) goto L_0x004b;
    L_0x0048:
        r1.close();	 Catch:{ IOException -> 0x00a9 }
    L_0x004b:
        r7.close();	 Catch:{ IOException -> 0x00ab }
    L_0x004e:
        r0 = "blob";
        r1 = r7.toByteArray();	 Catch:{ Exception -> 0x007d }
        r6.put(r0, r1);	 Catch:{ Exception -> 0x007d }
        r0 = "via_cgi_report";
        r1 = 0;
        r5.insert(r0, r1, r6);	 Catch:{ Exception -> 0x007d }
    L_0x005d:
        r6.clear();	 Catch:{ Exception -> 0x007d }
        r0 = r3 + 1;
        r3 = r0;
        goto L_0x0028;
    L_0x0064:
        r4 = r1;
        goto L_0x000f;
    L_0x0066:
        r0 = move-exception;
        r0 = r2;
    L_0x0068:
        if (r0 == 0) goto L_0x006d;
    L_0x006a:
        r0.close();	 Catch:{ IOException -> 0x00ad }
    L_0x006d:
        r7.close();	 Catch:{ IOException -> 0x0071 }
        goto L_0x004e;
    L_0x0071:
        r0 = move-exception;
        goto L_0x004e;
    L_0x0073:
        r0 = move-exception;
    L_0x0074:
        if (r2 == 0) goto L_0x0079;
    L_0x0076:
        r2.close();	 Catch:{ IOException -> 0x00af }
    L_0x0079:
        r7.close();	 Catch:{ IOException -> 0x00b1 }
    L_0x007c:
        throw r0;	 Catch:{ Exception -> 0x007d }
    L_0x007d:
        r0 = move-exception;
        r0 = "openSDK_LOG.ReportDatabaseHelper";
        r1 = "saveReportItemToDB has exception.";
        com.tencent.open.a.f.e(r0, r1);	 Catch:{ all -> 0x009f }
        r5.endTransaction();	 Catch:{ all -> 0x008f }
        if (r5 == 0) goto L_0x000a;
    L_0x008a:
        r5.close();	 Catch:{ all -> 0x008f }
        goto L_0x000a;
    L_0x008f:
        r0 = move-exception;
        monitor-exit(r8);
        throw r0;
    L_0x0092:
        r5.setTransactionSuccessful();	 Catch:{ Exception -> 0x007d }
        r5.endTransaction();	 Catch:{ all -> 0x008f }
        if (r5 == 0) goto L_0x000a;
    L_0x009a:
        r5.close();	 Catch:{ all -> 0x008f }
        goto L_0x000a;
    L_0x009f:
        r0 = move-exception;
        r5.endTransaction();	 Catch:{ all -> 0x008f }
        if (r5 == 0) goto L_0x00a8;
    L_0x00a5:
        r5.close();	 Catch:{ all -> 0x008f }
    L_0x00a8:
        throw r0;	 Catch:{ all -> 0x008f }
    L_0x00a9:
        r0 = move-exception;
        goto L_0x004b;
    L_0x00ab:
        r0 = move-exception;
        goto L_0x004e;
    L_0x00ad:
        r0 = move-exception;
        goto L_0x006d;
    L_0x00af:
        r1 = move-exception;
        goto L_0x0079;
    L_0x00b1:
        r1 = move-exception;
        goto L_0x007c;
    L_0x00b3:
        r0 = move-exception;
        r2 = r1;
        goto L_0x0074;
    L_0x00b6:
        r0 = move-exception;
        r0 = r1;
        goto L_0x0068;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.open.b.f.a(java.lang.String, java.util.List):void");
    }

    public synchronized void b(String str) {
        if (!TextUtils.isEmpty(str)) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            if (writableDatabase != null) {
                try {
                    writableDatabase.delete("via_cgi_report", "type = ?", new String[]{str});
                    if (writableDatabase != null) {
                        writableDatabase.close();
                    }
                } catch (Exception e) {
                    com.tencent.open.a.f.e("openSDK_LOG.ReportDatabaseHelper", "clearReportItem has exception.");
                    if (writableDatabase != null) {
                        writableDatabase.close();
                    }
                } catch (Throwable th) {
                    if (writableDatabase != null) {
                        writableDatabase.close();
                    }
                }
            }
        }
    }
}
