package com.igexin.download;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import com.douban.book.reader.fragment.WorksListFragment_;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.Locale;

public class g extends Thread {
    private Context a;
    private DownloadInfo b;

    public g(Context context, DownloadInfo downloadInfo) {
        this.a = context;
        this.b = downloadInfo;
    }

    private String a() {
        String str = this.b.mUserAgent;
        if (str != null) {
            return str != null ? "AndroidDownloadManager" : str;
        } else {
            if (str != null) {
            }
        }
    }

    private String a(String str) {
        try {
            String toLowerCase = str.trim().toLowerCase(Locale.ENGLISH);
            int indexOf = toLowerCase.indexOf(59);
            return indexOf != -1 ? toLowerCase.substring(0, indexOf) : toLowerCase;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void a(int i, boolean z, int i2, int i3, boolean z2, String str, String str2, String str3) {
        b(i, z, i2, i3, z2, str, str2, str3);
    }

    private void b(int i, boolean z, int i2, int i3, boolean z2, String str, String str2, String str3) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(i));
        contentValues.put(Downloads._DATA, str);
        if (str2 != null) {
            contentValues.put(WorksListFragment_.URI_ARG, str2);
        }
        contentValues.put(Downloads.COLUMN_MIME_TYPE, str3);
        contentValues.put(Downloads.COLUMN_LAST_MODIFICATION, Long.valueOf(System.currentTimeMillis()));
        contentValues.put("method", Integer.valueOf((i3 << 28) + i2));
        if (!z) {
            contentValues.put("numfailed", Integer.valueOf(0));
        } else if (z2) {
            contentValues.put("numfailed", Integer.valueOf(1));
        } else {
            contentValues.put("numfailed", Integer.valueOf(this.b.mNumFailed + 1));
        }
        this.a.getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) this.b.mId), contentValues, null, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
        r33 = this;
        r2 = 10;
        android.os.Process.setThreadPriority(r2);
        r20 = 491; // 0x1eb float:6.88E-43 double:2.426E-321;
        r19 = 0;
        r18 = 0;
        r0 = r33;
        r2 = r0.b;
        r0 = r2.mRedirectCount;
        r17 = r0;
        r16 = 0;
        r15 = 0;
        r5 = 0;
        r0 = r33;
        r2 = r0.b;
        r2 = r2.mMimeType;
        r0 = r33;
        r13 = r0.a(r2);
        r14 = 0;
        r12 = 0;
        r3 = 0;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = com.igexin.download.Downloads.a;
        r2 = r2.append(r4);
        r4 = "/";
        r2 = r2.append(r4);
        r0 = r33;
        r4 = r0.b;
        r4 = r4.mId;
        r2 = r2.append(r4);
        r2 = r2.toString();
        r27 = android.net.Uri.parse(r2);
        r11 = 0;
        r7 = 0;
        r10 = 0;
        r6 = 0;
        r4 = 0;
        r9 = 0;
        r2 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = new byte[r2];	 Catch:{ FileNotFoundException -> 0x1325, RuntimeException -> 0x0823, ClassNotFoundException -> 0x08d3, IllegalAccessException -> 0x0983, InvocationTargetException -> 0x0a33, all -> 0x0ae3 }
        r28 = r0;
        r23 = 0;
        r0 = r33;
        r2 = r0.a;	 Catch:{ FileNotFoundException -> 0x1325, RuntimeException -> 0x0823, ClassNotFoundException -> 0x08d3, IllegalAccessException -> 0x0983, InvocationTargetException -> 0x0a33, all -> 0x0ae3 }
        r8 = "power";
        r2 = r2.getSystemService(r8);	 Catch:{ FileNotFoundException -> 0x1325, RuntimeException -> 0x0823, ClassNotFoundException -> 0x08d3, IllegalAccessException -> 0x0983, InvocationTargetException -> 0x0a33, all -> 0x0ae3 }
        r2 = (android.os.PowerManager) r2;	 Catch:{ FileNotFoundException -> 0x1325, RuntimeException -> 0x0823, ClassNotFoundException -> 0x08d3, IllegalAccessException -> 0x0983, InvocationTargetException -> 0x0a33, all -> 0x0ae3 }
        r8 = 1;
        r21 = "GexinSdkDownloadService";
        r0 = r21;
        r21 = r2.newWakeLock(r8, r0);	 Catch:{ FileNotFoundException -> 0x1325, RuntimeException -> 0x0823, ClassNotFoundException -> 0x08d3, IllegalAccessException -> 0x0983, InvocationTargetException -> 0x0a33, all -> 0x0ae3 }
        r21.acquire();	 Catch:{ FileNotFoundException -> 0x1335, RuntimeException -> 0x1201, ClassNotFoundException -> 0x1086, IllegalAccessException -> 0x0f0b, InvocationTargetException -> 0x0d90, all -> 0x0bcb }
        r0 = r33;
        r2 = r0.b;	 Catch:{ FileNotFoundException -> 0x1335, RuntimeException -> 0x1201, ClassNotFoundException -> 0x1086, IllegalAccessException -> 0x0f0b, InvocationTargetException -> 0x0d90, all -> 0x0bcb }
        r8 = r2.mFileName;	 Catch:{ FileNotFoundException -> 0x1335, RuntimeException -> 0x1201, ClassNotFoundException -> 0x1086, IllegalAccessException -> 0x0f0b, InvocationTargetException -> 0x0d90, all -> 0x0bcb }
        if (r8 == 0) goto L_0x14dc;
    L_0x0077:
        r2 = com.igexin.download.h.a(r8);	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x1099, IllegalAccessException -> 0x0f1e, InvocationTargetException -> 0x0da3, all -> 0x0be3 }
        if (r2 != 0) goto L_0x0131;
    L_0x007d:
        r3 = 492; // 0x1ec float:6.9E-43 double:2.43E-321;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r9 = 0;
        r0 = r33;
        r2 = r0.b;	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x10ab, IllegalAccessException -> 0x0f30, InvocationTargetException -> 0x0db5, all -> 0x0bfa }
        r10 = r2.mMimeType;	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x10ab, IllegalAccessException -> 0x0f30, InvocationTargetException -> 0x0db5, all -> 0x0bfa }
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x10ab, IllegalAccessException -> 0x0f30, InvocationTargetException -> 0x0db5, all -> 0x0bfa }
        r0 = r33;
        r2 = r0.b;
        r4 = 0;
        r2.mHasActiveThread = r4;
        if (r21 == 0) goto L_0x009b;
    L_0x0098:
        r21.release();
    L_0x009b:
        if (r12 == 0) goto L_0x00ae;
    L_0x009d:
        r2 = r12.getClass();
        r4 = "close";
        r5 = 0;
        r2 = r2.getMethod(r4, r5);	 Catch:{ SecurityException -> 0x14ac, NoSuchMethodException -> 0x14a9, IllegalArgumentException -> 0x14a6, IllegalAccessException -> 0x14a3, InvocationTargetException -> 0x14a0 }
        r4 = 0;
        r4 = new java.lang.Object[r4];	 Catch:{ SecurityException -> 0x14ac, NoSuchMethodException -> 0x14a9, IllegalArgumentException -> 0x14a6, IllegalAccessException -> 0x14a3, InvocationTargetException -> 0x14a0 }
        r2.invoke(r12, r4);	 Catch:{ SecurityException -> 0x14ac, NoSuchMethodException -> 0x14a9, IllegalArgumentException -> 0x14a6, IllegalAccessException -> 0x14a3, InvocationTargetException -> 0x14a0 }
    L_0x00ae:
        if (r14 == 0) goto L_0x00b3;
    L_0x00b0:
        r14.close();	 Catch:{ IOException -> 0x0b97 }
    L_0x00b3:
        if (r8 == 0) goto L_0x00c4;
    L_0x00b5:
        r2 = com.igexin.download.Downloads.isStatusError(r3);
        if (r2 == 0) goto L_0x00d4;
    L_0x00bb:
        r2 = new java.io.File;
        r2.<init>(r8);
        r2.delete();
        r8 = 0;
    L_0x00c4:
        r2 = r33;
        r4 = r19;
        r5 = r18;
        r6 = r17;
        r7 = r15;
        r9 = r16;
        r10 = r13;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
    L_0x00d3:
        return;
    L_0x00d4:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x00c4;
    L_0x00da:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x149d }
        r5 = r2.getMethods();	 Catch:{ Exception -> 0x149d }
        r2 = 0;
        r4 = 0;
    L_0x00e6:
        r6 = r5.length;	 Catch:{ Exception -> 0x149d }
        if (r4 >= r6) goto L_0x00f9;
    L_0x00e9:
        r6 = r5[r4];	 Catch:{ Exception -> 0x149d }
        r6 = r6.getName();	 Catch:{ Exception -> 0x149d }
        r7 = "setPermissions";
        r6 = r6.endsWith(r7);	 Catch:{ Exception -> 0x149d }
        if (r6 == 0) goto L_0x012e;
    L_0x00f7:
        r2 = r5[r4];	 Catch:{ Exception -> 0x149d }
    L_0x00f9:
        if (r2 == 0) goto L_0x011e;
    L_0x00fb:
        r4 = 0;
        r5 = 4;
        r5 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x149d }
        r6 = 0;
        r5[r6] = r8;	 Catch:{ Exception -> 0x149d }
        r6 = 1;
        r7 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x149d }
        r5[r6] = r7;	 Catch:{ Exception -> 0x149d }
        r6 = 2;
        r7 = -1;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x149d }
        r5[r6] = r7;	 Catch:{ Exception -> 0x149d }
        r6 = 3;
        r7 = -1;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x149d }
        r5[r6] = r7;	 Catch:{ Exception -> 0x149d }
        r2.invoke(r4, r5);	 Catch:{ Exception -> 0x149d }
    L_0x011e:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x012c, SyncFailedException -> 0x149a, IOException -> 0x1497, RuntimeException -> 0x1494 }
        r4 = 1;
        r2.<init>(r8, r4);	 Catch:{ FileNotFoundException -> 0x012c, SyncFailedException -> 0x149a, IOException -> 0x1497, RuntimeException -> 0x1494 }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x012c, SyncFailedException -> 0x149a, IOException -> 0x1497, RuntimeException -> 0x1494 }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x012c, SyncFailedException -> 0x149a, IOException -> 0x1497, RuntimeException -> 0x1494 }
        goto L_0x00c4;
    L_0x012c:
        r2 = move-exception;
        goto L_0x00c4;
    L_0x012e:
        r4 = r4 + 1;
        goto L_0x00e6;
    L_0x0131:
        r2 = new java.io.File;	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x1099, IllegalAccessException -> 0x0f1e, InvocationTargetException -> 0x0da3, all -> 0x0be3 }
        r2.<init>(r8);	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x1099, IllegalAccessException -> 0x0f1e, InvocationTargetException -> 0x0da3, all -> 0x0be3 }
        r3 = r2.exists();	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x1099, IllegalAccessException -> 0x0f1e, InvocationTargetException -> 0x0da3, all -> 0x0be3 }
        if (r3 == 0) goto L_0x14dc;
    L_0x013c:
        r22 = r2.length();	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x1099, IllegalAccessException -> 0x0f1e, InvocationTargetException -> 0x0da3, all -> 0x0be3 }
        r4 = 0;
        r3 = (r22 > r4 ? 1 : (r22 == r4 ? 0 : -1));
        if (r3 != 0) goto L_0x14d9;
    L_0x0146:
        r2.delete();	 Catch:{ FileNotFoundException -> 0x1346, RuntimeException -> 0x1212, ClassNotFoundException -> 0x1099, IllegalAccessException -> 0x0f1e, InvocationTargetException -> 0x0da3, all -> 0x0be3 }
        r5 = 0;
    L_0x014a:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x1356, RuntimeException -> 0x1222, ClassNotFoundException -> 0x10bb, IllegalAccessException -> 0x0f40, InvocationTargetException -> 0x0dc5, all -> 0x0c0f }
        r3 = 1;
        r2.<init>(r5, r3);	 Catch:{ FileNotFoundException -> 0x1356, RuntimeException -> 0x1222, ClassNotFoundException -> 0x10bb, IllegalAccessException -> 0x0f40, InvocationTargetException -> 0x0dc5, all -> 0x0c0f }
        r0 = r22;
        r0 = (int) r0;
        r23 = r0;
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x1367, RuntimeException -> 0x1233, ClassNotFoundException -> 0x10ce, IllegalAccessException -> 0x0f53, InvocationTargetException -> 0x0dd8, all -> 0x0c27 }
        r3 = r3.mTotalBytes;	 Catch:{ FileNotFoundException -> 0x1367, RuntimeException -> 0x1233, ClassNotFoundException -> 0x10ce, IllegalAccessException -> 0x0f53, InvocationTargetException -> 0x0dd8, all -> 0x0c27 }
        r4 = -1;
        if (r3 == r4) goto L_0x14d6;
    L_0x015e:
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x1367, RuntimeException -> 0x1233, ClassNotFoundException -> 0x10ce, IllegalAccessException -> 0x0f53, InvocationTargetException -> 0x0dd8, all -> 0x0c27 }
        r3 = r3.mTotalBytes;	 Catch:{ FileNotFoundException -> 0x1367, RuntimeException -> 0x1233, ClassNotFoundException -> 0x10ce, IllegalAccessException -> 0x0f53, InvocationTargetException -> 0x0dd8, all -> 0x0c27 }
        r4 = java.lang.Integer.toString(r3);	 Catch:{ FileNotFoundException -> 0x1367, RuntimeException -> 0x1233, ClassNotFoundException -> 0x10ce, IllegalAccessException -> 0x0f53, InvocationTargetException -> 0x0dd8, all -> 0x0c27 }
    L_0x0168:
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x1367, RuntimeException -> 0x1233, ClassNotFoundException -> 0x10ce, IllegalAccessException -> 0x0f53, InvocationTargetException -> 0x0dd8, all -> 0x0c27 }
        r8 = r3.mETag;	 Catch:{ FileNotFoundException -> 0x1367, RuntimeException -> 0x1233, ClassNotFoundException -> 0x10ce, IllegalAccessException -> 0x0f53, InvocationTargetException -> 0x0dd8, all -> 0x0c27 }
        r11 = 1;
        r14 = r5;
    L_0x0170:
        r24 = 0;
        r3 = "android.net.http.AndroidHttpClient";
        r3 = java.lang.Class.forName(r3);	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r10 = r3.getMethods();	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r3 = 0;
        r5 = 0;
    L_0x017e:
        r0 = r10.length;	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r22 = r0;
        r0 = r22;
        if (r5 >= r0) goto L_0x0199;
    L_0x0185:
        r22 = r10[r5];	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r22 = r22.getName();	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r26 = "newInstance";
        r0 = r22;
        r1 = r26;
        r22 = r0.endsWith(r1);	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        if (r22 == 0) goto L_0x02ba;
    L_0x0197:
        r3 = r10[r5];	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
    L_0x0199:
        if (r3 == 0) goto L_0x14d2;
    L_0x019b:
        r5 = 0;
        r10 = 1;
        r10 = new java.lang.Object[r10];	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r22 = 0;
        r26 = r33.a();	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r10[r22] = r26;	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
        r22 = r3.invoke(r5, r10);	 Catch:{ FileNotFoundException -> 0x1378, RuntimeException -> 0x1244, ClassNotFoundException -> 0x10e1, IllegalAccessException -> 0x0f66, InvocationTargetException -> 0x0deb, all -> 0x0c3c }
    L_0x01ab:
        if (r2 == 0) goto L_0x14cf;
    L_0x01ad:
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x1389, RuntimeException -> 0x1255, ClassNotFoundException -> 0x10f4, IllegalAccessException -> 0x0f79, InvocationTargetException -> 0x0dfe, all -> 0x0c51 }
        r3 = r3.mDestination;	 Catch:{ FileNotFoundException -> 0x1389, RuntimeException -> 0x1255, ClassNotFoundException -> 0x10f4, IllegalAccessException -> 0x0f79, InvocationTargetException -> 0x0dfe, all -> 0x0c51 }
        if (r3 != 0) goto L_0x14cf;
    L_0x01b5:
        r2.close();	 Catch:{ IOException -> 0x02be }
        r2 = 0;
        r12 = r2;
    L_0x01ba:
        r29 = new org.apache.http.client.methods.HttpGet;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r33;
        r2 = r0.b;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r2 = r2.mUri;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r29;
        r0.<init>(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r33;
        r2 = r0.b;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r2 = r2.mCookies;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r2 == 0) goto L_0x01dc;
    L_0x01cf:
        r2 = "Cookie";
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3 = r3.mCookies;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r29;
        r0.addHeader(r2, r3);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x01dc:
        r0 = r33;
        r2 = r0.b;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r2 = r2.mReferer;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r2 == 0) goto L_0x01f1;
    L_0x01e4:
        r2 = "Referer";
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3 = r3.mReferer;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r29;
        r0.addHeader(r2, r3);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x01f1:
        if (r11 == 0) goto L_0x021e;
    L_0x01f3:
        if (r8 == 0) goto L_0x01fc;
    L_0x01f5:
        r2 = "If-Match";
        r0 = r29;
        r0.addHeader(r2, r8);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x01fc:
        r2 = "Range";
        r3 = new java.lang.StringBuilder;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3.<init>();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r5 = "bytes=";
        r3 = r3.append(r5);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r23;
        r3 = r3.append(r0);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r5 = "-";
        r3 = r3.append(r5);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3 = r3.toString();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r29;
        r0.addHeader(r2, r3);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x021e:
        r3 = 0;
        r2 = r22.getClass();	 Catch:{ IllegalArgumentException -> 0x02c6, Exception -> 0x02d9 }
        r5 = "execute";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ NoSuchMethodException -> 0x02c2 }
        r26 = 0;
        r30 = org.apache.http.client.methods.HttpUriRequest.class;
        r10[r26] = r30;	 Catch:{ NoSuchMethodException -> 0x02c2 }
        r2 = r2.getMethod(r5, r10);	 Catch:{ NoSuchMethodException -> 0x02c2 }
        r5 = 1;
        r5 = new java.lang.Object[r5];	 Catch:{ NoSuchMethodException -> 0x02c2 }
        r10 = 0;
        r5[r10] = r29;	 Catch:{ NoSuchMethodException -> 0x02c2 }
        r0 = r22;
        r2 = r2.invoke(r0, r5);	 Catch:{ NoSuchMethodException -> 0x02c2 }
        r2 = (org.apache.http.HttpResponse) r2;	 Catch:{ NoSuchMethodException -> 0x02c2 }
        r10 = r2;
    L_0x0241:
        r2 = r10.getStatusLine();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r2 = r2.getStatusCode();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3 = 503; // 0x1f7 float:7.05E-43 double:2.485E-321;
        if (r2 != r3) goto L_0x0335;
    L_0x024d:
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3 = r3.mNumFailed;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r5 = 5;
        if (r3 >= r5) goto L_0x0335;
    L_0x0256:
        r3 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r4 = 1;
        r2 = "Retry-After";
        r2 = r10.getFirstHeader(r2);	 Catch:{ FileNotFoundException -> 0x13af, RuntimeException -> 0x1272, ClassNotFoundException -> 0x1115, IllegalAccessException -> 0x0f9a, InvocationTargetException -> 0x0e1f, all -> 0x0c79 }
        if (r2 == 0) goto L_0x0331;
    L_0x0261:
        r2 = r2.getValue();	 Catch:{ NumberFormatException -> 0x032d }
        r18 = java.lang.Integer.parseInt(r2);	 Catch:{ NumberFormatException -> 0x032d }
        if (r18 >= 0) goto L_0x030c;
    L_0x026b:
        r2 = 0;
    L_0x026c:
        r5 = r2;
    L_0x026d:
        r29.abort();	 Catch:{ FileNotFoundException -> 0x13c0, RuntimeException -> 0x127e, ClassNotFoundException -> 0x1121, IllegalAccessException -> 0x0fa6, InvocationTargetException -> 0x0e2b, all -> 0x0c8a }
        r2 = r11;
        r8 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r11 = r14;
    L_0x0279:
        r0 = r33;
        r12 = r0.b;
        r13 = 0;
        r12.mHasActiveThread = r13;
        if (r21 == 0) goto L_0x0285;
    L_0x0282:
        r21.release();
    L_0x0285:
        if (r22 == 0) goto L_0x029a;
    L_0x0287:
        r12 = r22.getClass();
        r13 = "close";
        r14 = 0;
        r12 = r12.getMethod(r13, r14);	 Catch:{ SecurityException -> 0x1486, NoSuchMethodException -> 0x1483, IllegalArgumentException -> 0x1480, IllegalAccessException -> 0x147d, InvocationTargetException -> 0x147a }
        r13 = 0;
        r13 = new java.lang.Object[r13];	 Catch:{ SecurityException -> 0x1486, NoSuchMethodException -> 0x1483, IllegalArgumentException -> 0x1480, IllegalAccessException -> 0x147d, InvocationTargetException -> 0x147a }
        r0 = r22;
        r12.invoke(r0, r13);	 Catch:{ SecurityException -> 0x1486, NoSuchMethodException -> 0x1483, IllegalArgumentException -> 0x1480, IllegalAccessException -> 0x147d, InvocationTargetException -> 0x147a }
    L_0x029a:
        if (r8 == 0) goto L_0x029f;
    L_0x029c:
        r8.close();	 Catch:{ IOException -> 0x0b9d }
    L_0x029f:
        if (r11 == 0) goto L_0x14af;
    L_0x02a1:
        r8 = com.igexin.download.Downloads.isStatusError(r3);
        if (r8 == 0) goto L_0x0757;
    L_0x02a7:
        if (r2 != 0) goto L_0x14af;
    L_0x02a9:
        r2 = new java.io.File;
        r2.<init>(r11);
        r2.delete();
        r11 = 0;
        r8 = r11;
    L_0x02b3:
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x00d3;
    L_0x02ba:
        r5 = r5 + 1;
        goto L_0x017e;
    L_0x02be:
        r3 = move-exception;
        r12 = r2;
        goto L_0x01ba;
    L_0x02c2:
        r2 = move-exception;
        r10 = r3;
        goto L_0x0241;
    L_0x02c6:
        r2 = move-exception;
        r4 = 1;
        r3 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        r29.abort();	 Catch:{ FileNotFoundException -> 0x13af, RuntimeException -> 0x1272, ClassNotFoundException -> 0x1115, IllegalAccessException -> 0x0f9a, InvocationTargetException -> 0x0e1f, all -> 0x0c79 }
        r2 = r11;
        r8 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r11 = r14;
        goto L_0x0279;
    L_0x02d9:
        r2 = move-exception;
        r0 = r33;
        r2 = r0.a;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r2 = com.igexin.download.h.a(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r2 != 0) goto L_0x02f7;
    L_0x02e4:
        r3 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r4 = r19;
    L_0x02e8:
        r29.abort();	 Catch:{ FileNotFoundException -> 0x13af, RuntimeException -> 0x1272, ClassNotFoundException -> 0x1115, IllegalAccessException -> 0x0f9a, InvocationTargetException -> 0x0e1f, all -> 0x0c79 }
        r2 = r11;
        r8 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r11 = r14;
        goto L_0x0279;
    L_0x02f7:
        r0 = r33;
        r2 = r0.b;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r2 = r2.mNumFailed;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3 = 5;
        if (r2 >= r3) goto L_0x0307;
    L_0x0300:
        r3 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r19 = 1;
        r4 = r19;
        goto L_0x02e8;
    L_0x0307:
        r3 = 495; // 0x1ef float:6.94E-43 double:2.446E-321;
        r4 = r19;
        goto L_0x02e8;
    L_0x030c:
        r2 = 30;
        r0 = r18;
        if (r0 >= r2) goto L_0x0322;
    L_0x0312:
        r18 = 30;
    L_0x0314:
        r2 = com.igexin.download.h.a;	 Catch:{ NumberFormatException -> 0x032d }
        r5 = 31;
        r2 = r2.nextInt(r5);	 Catch:{ NumberFormatException -> 0x032d }
        r2 = r2 + r18;
        r2 = r2 * 1000;
        goto L_0x026c;
    L_0x0322:
        r2 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
        r0 = r18;
        if (r0 <= r2) goto L_0x0314;
    L_0x0329:
        r18 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
        goto L_0x0314;
    L_0x032d:
        r2 = move-exception;
        r2.printStackTrace();	 Catch:{ FileNotFoundException -> 0x13af, RuntimeException -> 0x1272, ClassNotFoundException -> 0x1115, IllegalAccessException -> 0x0f9a, InvocationTargetException -> 0x0e1f, all -> 0x0c79 }
    L_0x0331:
        r5 = r18;
        goto L_0x026d;
    L_0x0335:
        r3 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        if (r2 == r3) goto L_0x0345;
    L_0x0339:
        r3 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        if (r2 == r3) goto L_0x0345;
    L_0x033d:
        r3 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
        if (r2 == r3) goto L_0x0345;
    L_0x0341:
        r3 = 307; // 0x133 float:4.3E-43 double:1.517E-321;
        if (r2 != r3) goto L_0x03a9;
    L_0x0345:
        r3 = 5;
        r0 = r17;
        if (r0 < r3) goto L_0x035e;
    L_0x034a:
        r3 = 497; // 0x1f1 float:6.96E-43 double:2.456E-321;
        r29.abort();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x112b, IllegalAccessException -> 0x0fb0, InvocationTargetException -> 0x0e35, all -> 0x0c99 }
        r2 = r11;
        r8 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r11 = r14;
        goto L_0x0279;
    L_0x035e:
        r3 = "Location";
        r3 = r10.getFirstHeader(r3);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r3 == 0) goto L_0x03a9;
    L_0x0366:
        r2 = new java.net.URI;	 Catch:{ URISyntaxException -> 0x0394 }
        r0 = r33;
        r4 = r0.b;	 Catch:{ URISyntaxException -> 0x0394 }
        r4 = r4.mUri;	 Catch:{ URISyntaxException -> 0x0394 }
        r2.<init>(r4);	 Catch:{ URISyntaxException -> 0x0394 }
        r4 = new java.net.URI;	 Catch:{ URISyntaxException -> 0x0394 }
        r3 = r3.getValue();	 Catch:{ URISyntaxException -> 0x0394 }
        r4.<init>(r3);	 Catch:{ URISyntaxException -> 0x0394 }
        r2 = r2.resolve(r4);	 Catch:{ URISyntaxException -> 0x0394 }
        r9 = r2.toString();	 Catch:{ URISyntaxException -> 0x0394 }
        r6 = r17 + 1;
        r3 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r29.abort();	 Catch:{ FileNotFoundException -> 0x13cf, RuntimeException -> 0x1288, ClassNotFoundException -> 0x1139, IllegalAccessException -> 0x0fbe, InvocationTargetException -> 0x0e43, all -> 0x0cac }
        r2 = r11;
        r8 = r12;
        r10 = r13;
        r7 = r15;
        r5 = r18;
        r4 = r19;
        r11 = r14;
        goto L_0x0279;
    L_0x0394:
        r2 = move-exception;
        r3 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        r29.abort();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x112b, IllegalAccessException -> 0x0fb0, InvocationTargetException -> 0x0e35, all -> 0x0c99 }
        r2 = r11;
        r8 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r11 = r14;
        goto L_0x0279;
    L_0x03a9:
        if (r11 != 0) goto L_0x03af;
    L_0x03ab:
        r3 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r2 != r3) goto L_0x03b5;
    L_0x03af:
        if (r11 == 0) goto L_0x03fc;
    L_0x03b1:
        r3 = 206; // 0xce float:2.89E-43 double:1.02E-321;
        if (r2 == r3) goto L_0x03fc;
    L_0x03b5:
        r3 = com.igexin.download.Downloads.isStatusError(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r3 == 0) goto L_0x03d1;
    L_0x03bb:
        r3 = r11;
    L_0x03bc:
        r29.abort();	 Catch:{ FileNotFoundException -> 0x13de, RuntimeException -> 0x1292, ClassNotFoundException -> 0x1143, IllegalAccessException -> 0x0fc8, InvocationTargetException -> 0x0e4d, all -> 0x0cbb }
        r4 = 1;
        r8 = r12;
        r10 = r13;
        r11 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r32 = r3;
        r3 = r2;
        r2 = r32;
        goto L_0x0279;
    L_0x03d1:
        r3 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        if (r2 < r3) goto L_0x03dd;
    L_0x03d5:
        r3 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        if (r2 >= r3) goto L_0x03dd;
    L_0x03d9:
        r2 = 493; // 0x1ed float:6.91E-43 double:2.436E-321;
        r3 = r11;
        goto L_0x03bc;
    L_0x03dd:
        if (r11 == 0) goto L_0x03f8;
    L_0x03df:
        r3 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r2 != r3) goto L_0x03f8;
    L_0x03e3:
        r20 = 412; // 0x19c float:5.77E-43 double:2.036E-321;
        r11 = 0;
        r2 = new java.io.File;	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r2.<init>(r14);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r3 = r2.exists();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r3 == 0) goto L_0x03f4;
    L_0x03f1:
        r2.delete();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x03f4:
        r3 = r11;
        r2 = r20;
        goto L_0x03bc;
    L_0x03f8:
        r2 = 494; // 0x1ee float:6.92E-43 double:2.44E-321;
        r3 = r11;
        goto L_0x03bc;
    L_0x03fc:
        if (r11 != 0) goto L_0x14c9;
    L_0x03fe:
        r2 = "Accept-Ranges";
        r2 = r10.getFirstHeader(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r2 == 0) goto L_0x0409;
    L_0x0406:
        r2.getValue();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x0409:
        r2 = "Content-Disposition";
        r2 = r10.getFirstHeader(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r2 == 0) goto L_0x14c6;
    L_0x0411:
        r5 = r2.getValue();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x0415:
        r2 = "Content-Location";
        r2 = r10.getFirstHeader(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r2 == 0) goto L_0x0421;
    L_0x041d:
        r6 = r2.getValue();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
    L_0x0421:
        if (r13 != 0) goto L_0x14c3;
    L_0x0423:
        r2 = "Content-Type";
        r2 = r10.getFirstHeader(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        if (r2 == 0) goto L_0x14c3;
    L_0x042b:
        r2 = r2.getValue();	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r0 = r33;
        r13 = r0.a(r2);	 Catch:{ FileNotFoundException -> 0x139c, RuntimeException -> 0x1264, ClassNotFoundException -> 0x1105, IllegalAccessException -> 0x0f8a, InvocationTargetException -> 0x0e0f, all -> 0x0c64 }
        r7 = r13;
    L_0x0436:
        r2 = "ETag";
        r2 = r10.getFirstHeader(r2);	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r2 == 0) goto L_0x14bf;
    L_0x043e:
        r2 = r2.getValue();	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r26 = r2;
    L_0x0444:
        r2 = "Transfer-Encoding";
        r2 = r10.getFirstHeader(r2);	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r2 == 0) goto L_0x14bc;
    L_0x044c:
        r2 = r2.getValue();	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
    L_0x0450:
        if (r2 != 0) goto L_0x14b9;
    L_0x0452:
        r3 = "Content-Length";
        r3 = r10.getFirstHeader(r3);	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r3 == 0) goto L_0x14b9;
    L_0x045a:
        r4 = r3.getValue();	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r13 = r4;
    L_0x045f:
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r3 = r3.mNoIntegrity;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r3 != 0) goto L_0x0487;
    L_0x0467:
        if (r13 != 0) goto L_0x0487;
    L_0x0469:
        if (r2 == 0) goto L_0x0473;
    L_0x046b:
        r3 = "chunked";
        r2 = r2.equalsIgnoreCase(r3);	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r2 != 0) goto L_0x0487;
    L_0x0473:
        r3 = 411; // 0x19b float:5.76E-43 double:2.03E-321;
        r29.abort();	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1163, IllegalAccessException -> 0x0fe8, InvocationTargetException -> 0x0e6d, all -> 0x0ce4 }
        r2 = r11;
        r8 = r12;
        r10 = r7;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r11 = r14;
        r7 = r15;
        goto L_0x0279;
    L_0x0487:
        r0 = r33;
        r2 = r0.a;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r3 = r3.mUri;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r0 = r33;
        r4 = r0.b;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r4 = r4.mHint;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r0 = r33;
        r8 = r0.b;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r8 = r8.mDestination;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r13 == 0) goto L_0x04bf;
    L_0x049f:
        r9 = java.lang.Integer.parseInt(r13);	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
    L_0x04a3:
        r2 = com.igexin.download.h.a(r2, r3, r4, r5, r6, r7, r8, r9);	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r3 = r2.a;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r3 != 0) goto L_0x04c1;
    L_0x04ab:
        r3 = r2.c;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r29.abort();	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1163, IllegalAccessException -> 0x0fe8, InvocationTargetException -> 0x0e6d, all -> 0x0ce4 }
        r2 = r11;
        r8 = r12;
        r10 = r7;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r11 = r14;
        r7 = r15;
        goto L_0x0279;
    L_0x04bf:
        r9 = 0;
        goto L_0x04a3;
    L_0x04c1:
        r14 = r2.a;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r12 = r2.b;	 Catch:{ FileNotFoundException -> 0x13f2, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r3 = new android.content.ContentValues;	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r3.<init>();	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r2 = "_data";
        r3.put(r2, r14);	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        if (r26 == 0) goto L_0x04d8;
    L_0x04d1:
        r2 = "etag";
        r0 = r26;
        r3.put(r2, r0);	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
    L_0x04d8:
        if (r7 == 0) goto L_0x04df;
    L_0x04da:
        r2 = "mimetype";
        r3.put(r2, r7);	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
    L_0x04df:
        r2 = -1;
        if (r13 == 0) goto L_0x04e6;
    L_0x04e2:
        r2 = java.lang.Integer.parseInt(r13);	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
    L_0x04e6:
        r4 = "total_bytes";
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r3.put(r4, r2);	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r0 = r33;
        r2 = r0.a;	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r2 = r2.getContentResolver();	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r4 = 0;
        r5 = 0;
        r0 = r27;
        r2.update(r0, r3, r4, r5);	 Catch:{ FileNotFoundException -> 0x1405, RuntimeException -> 0x12a1, ClassNotFoundException -> 0x1153, IllegalAccessException -> 0x0fd8, InvocationTargetException -> 0x0e5d, all -> 0x0ccf }
        r2 = r12;
        r5 = r14;
        r14 = r13;
    L_0x0501:
        r3 = r10.getEntity();	 Catch:{ IOException -> 0x0568 }
        r26 = r3.getContent();	 Catch:{ IOException -> 0x0568 }
        r13 = r23;
        r3 = r11;
        r4 = r15;
        r10 = r24;
    L_0x050f:
        r0 = r26;
        r1 = r28;
        r8 = r0.read(r1);	 Catch:{ IOException -> 0x059c }
        r6 = -1;
        if (r8 != r6) goto L_0x0622;
    L_0x051a:
        r6 = new android.content.ContentValues;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6.<init>();	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r8 = "current_bytes";
        r9 = java.lang.Integer.valueOf(r23);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6.put(r8, r9);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        if (r14 != 0) goto L_0x0533;
    L_0x052a:
        r8 = "total_bytes";
        r9 = java.lang.Integer.valueOf(r23);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6.put(r8, r9);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
    L_0x0533:
        r0 = r33;
        r8 = r0.a;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r8 = r8.getContentResolver();	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r9 = 0;
        r10 = 0;
        r0 = r27;
        r8.update(r0, r6, r9, r10);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        if (r14 == 0) goto L_0x0745;
    L_0x0544:
        r6 = java.lang.Integer.parseInt(r14);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r0 = r23;
        if (r0 == r6) goto L_0x0745;
    L_0x054c:
        r0 = r33;
        r6 = r0.a;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6 = com.igexin.download.h.a(r6);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        if (r6 != 0) goto L_0x05f2;
    L_0x0556:
        r6 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r8 = r2;
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r4;
        r2 = r3;
        r5 = r18;
        r4 = r19;
        r3 = r6;
        r6 = r17;
        goto L_0x0279;
    L_0x0568:
        r3 = move-exception;
        r0 = r33;
        r3 = r0.a;	 Catch:{ FileNotFoundException -> 0x1418, RuntimeException -> 0x12af, ClassNotFoundException -> 0x1171, IllegalAccessException -> 0x0ff6, InvocationTargetException -> 0x0e7b, all -> 0x0cf7 }
        r3 = com.igexin.download.h.a(r3);	 Catch:{ FileNotFoundException -> 0x1418, RuntimeException -> 0x12af, ClassNotFoundException -> 0x1171, IllegalAccessException -> 0x0ff6, InvocationTargetException -> 0x0e7b, all -> 0x0cf7 }
        if (r3 != 0) goto L_0x0587;
    L_0x0573:
        r3 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r4 = r19;
    L_0x0577:
        r29.abort();	 Catch:{ FileNotFoundException -> 0x142b, RuntimeException -> 0x12be, ClassNotFoundException -> 0x1182, IllegalAccessException -> 0x1007, InvocationTargetException -> 0x0e8c, all -> 0x0d0a }
        r8 = r2;
        r10 = r7;
        r9 = r16;
        r6 = r17;
        r7 = r15;
        r2 = r11;
        r11 = r5;
        r5 = r18;
        goto L_0x0279;
    L_0x0587:
        r0 = r33;
        r3 = r0.b;	 Catch:{ FileNotFoundException -> 0x1418, RuntimeException -> 0x12af, ClassNotFoundException -> 0x1171, IllegalAccessException -> 0x0ff6, InvocationTargetException -> 0x0e7b, all -> 0x0cf7 }
        r3 = r3.mNumFailed;	 Catch:{ FileNotFoundException -> 0x1418, RuntimeException -> 0x12af, ClassNotFoundException -> 0x1171, IllegalAccessException -> 0x0ff6, InvocationTargetException -> 0x0e7b, all -> 0x0cf7 }
        r4 = 5;
        if (r3 >= r4) goto L_0x0597;
    L_0x0590:
        r3 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r19 = 1;
        r4 = r19;
        goto L_0x0577;
    L_0x0597:
        r3 = 495; // 0x1ef float:6.94E-43 double:2.446E-321;
        r4 = r19;
        goto L_0x0577;
    L_0x059c:
        r6 = move-exception;
        r6 = new android.content.ContentValues;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6.<init>();	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r8 = "current_bytes";
        r9 = java.lang.Integer.valueOf(r23);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6.put(r8, r9);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r0 = r33;
        r8 = r0.a;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r8 = r8.getContentResolver();	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r9 = 0;
        r10 = 0;
        r0 = r27;
        r8.update(r0, r6, r9, r10);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r0 = r33;
        r6 = r0.a;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6 = com.igexin.download.h.a(r6);	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        if (r6 != 0) goto L_0x05dd;
    L_0x05c4:
        r8 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r6 = r19;
    L_0x05c8:
        r29.abort();	 Catch:{ FileNotFoundException -> 0x1450, RuntimeException -> 0x12db, ClassNotFoundException -> 0x11a1, IllegalAccessException -> 0x1026, InvocationTargetException -> 0x0eab, all -> 0x0d2c }
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r4;
        r5 = r18;
        r4 = r6;
        r6 = r17;
        r32 = r2;
        r2 = r3;
        r3 = r8;
        r8 = r32;
        goto L_0x0279;
    L_0x05dd:
        r0 = r33;
        r6 = r0.b;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6 = r6.mNumFailed;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r8 = 5;
        if (r6 >= r8) goto L_0x05ed;
    L_0x05e6:
        r8 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r19 = 1;
        r6 = r19;
        goto L_0x05c8;
    L_0x05ed:
        r8 = 495; // 0x1ef float:6.94E-43 double:2.446E-321;
        r6 = r19;
        goto L_0x05c8;
    L_0x05f2:
        r0 = r33;
        r6 = r0.b;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r6 = r6.mNumFailed;	 Catch:{ FileNotFoundException -> 0x143c, RuntimeException -> 0x12cb, ClassNotFoundException -> 0x118f, IllegalAccessException -> 0x1014, InvocationTargetException -> 0x0e99, all -> 0x0d19 }
        r8 = 5;
        if (r6 >= r8) goto L_0x0610;
    L_0x05fb:
        r8 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r6 = 1;
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r4;
        r5 = r18;
        r4 = r6;
        r6 = r17;
        r32 = r2;
        r2 = r3;
        r3 = r8;
        r8 = r32;
        goto L_0x0279;
    L_0x0610:
        r6 = 495; // 0x1ef float:6.94E-43 double:2.446E-321;
        r8 = r2;
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r4;
        r2 = r3;
        r5 = r18;
        r4 = r19;
        r3 = r6;
        r6 = r17;
        goto L_0x0279;
    L_0x0622:
        r6 = 1;
    L_0x0623:
        if (r2 != 0) goto L_0x062c;
    L_0x0625:
        r4 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0694 }
        r9 = 1;
        r4.<init>(r5, r9);	 Catch:{ IOException -> 0x0694 }
        r2 = r4;
    L_0x062c:
        r4 = 0;
        r0 = r28;
        r2.write(r0, r4, r8);	 Catch:{ IOException -> 0x0694 }
        r0 = r33;
        r4 = r0.b;	 Catch:{ IOException -> 0x0694 }
        r4 = r4.mDestination;	 Catch:{ IOException -> 0x0694 }
        if (r4 != 0) goto L_0x063e;
    L_0x063a:
        r2.close();	 Catch:{ IOException -> 0x0b9a }
        r2 = 0;
    L_0x063e:
        r12 = r23 + r8;
        r8 = java.lang.System.currentTimeMillis();	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r4 = r12 - r13;
        r15 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        if (r4 <= r15) goto L_0x14b2;
    L_0x064a:
        r24 = r8 - r10;
        r30 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r24 > r30 ? 1 : (r24 == r30 ? 0 : -1));
        if (r4 <= 0) goto L_0x14b2;
    L_0x0652:
        r4 = new android.content.ContentValues;	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r4.<init>();	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r10 = "current_bytes";
        r11 = java.lang.Integer.valueOf(r12);	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r4.put(r10, r11);	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r0 = r33;
        r10 = r0.a;	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r10 = r10.getContentResolver();	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r11 = 0;
        r13 = 0;
        r0 = r27;
        r10.update(r0, r4, r11, r13);	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r4 = r12;
    L_0x0670:
        r0 = r33;
        r10 = r0.b;	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        monitor-enter(r10);	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r0 = r33;
        r11 = r0.b;	 Catch:{ all -> 0x06e7 }
        r11 = r11.mControl;	 Catch:{ all -> 0x06e7 }
        r13 = 1;
        if (r11 != r13) goto L_0x06ca;
    L_0x067e:
        r4 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        r29.abort();	 Catch:{ all -> 0x1489 }
        monitor-exit(r10);	 Catch:{ all -> 0x1489 }
        r8 = r2;
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r6;
        r2 = r3;
        r5 = r18;
        r6 = r17;
        r3 = r4;
        r4 = r19;
        goto L_0x0279;
    L_0x0694:
        r4 = move-exception;
        r4 = r2;
        r2 = android.os.Environment.getExternalStorageState();	 Catch:{ FileNotFoundException -> 0x1463, RuntimeException -> 0x12fa, ClassNotFoundException -> 0x11c3, IllegalAccessException -> 0x1048, InvocationTargetException -> 0x0ecd, all -> 0x0d50 }
        r9 = "mounted";
        r2 = r2.equals(r9);	 Catch:{ FileNotFoundException -> 0x1463, RuntimeException -> 0x12fa, ClassNotFoundException -> 0x11c3, IllegalAccessException -> 0x1048, InvocationTargetException -> 0x0ecd, all -> 0x0d50 }
        if (r2 != 0) goto L_0x06a7;
    L_0x06a2:
        r2 = 1;
    L_0x06a3:
        r3 = r2;
        r2 = r4;
        goto L_0x0623;
    L_0x06a7:
        r0 = r33;
        r2 = r0.a;	 Catch:{ FileNotFoundException -> 0x1463, RuntimeException -> 0x12fa, ClassNotFoundException -> 0x11c3, IllegalAccessException -> 0x1048, InvocationTargetException -> 0x0ecd, all -> 0x0d50 }
        r24 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = r24;
        r2 = com.igexin.download.h.a(r2, r0);	 Catch:{ FileNotFoundException -> 0x1463, RuntimeException -> 0x12fa, ClassNotFoundException -> 0x11c3, IllegalAccessException -> 0x1048, InvocationTargetException -> 0x0ecd, all -> 0x0d50 }
        if (r2 != 0) goto L_0x14b6;
    L_0x06b5:
        r2 = 492; // 0x1ec float:6.9E-43 double:2.43E-321;
        r8 = r4;
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r6;
        r5 = r18;
        r4 = r19;
        r6 = r17;
        r32 = r3;
        r3 = r2;
        r2 = r32;
        goto L_0x0279;
    L_0x06ca:
        monitor-exit(r10);	 Catch:{ all -> 0x06e7 }
        r0 = r33;
        r10 = r0.b;	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r10 = r10.mStatus;	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11b1, IllegalAccessException -> 0x1036, InvocationTargetException -> 0x0ebb, all -> 0x0d3d }
        r11 = 490; // 0x1ea float:6.87E-43 double:2.42E-321;
        if (r10 != r11) goto L_0x073e;
    L_0x06d5:
        r4 = 490; // 0x1ea float:6.87E-43 double:2.42E-321;
        r8 = r2;
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r6;
        r2 = r3;
        r5 = r18;
        r6 = r17;
        r3 = r4;
        r4 = r19;
        goto L_0x0279;
    L_0x06e7:
        r4 = move-exception;
        r8 = r20;
    L_0x06ea:
        monitor-exit(r10);	 Catch:{ all -> 0x1491 }
        throw r4;	 Catch:{ FileNotFoundException -> 0x06ec, RuntimeException -> 0x12ea, ClassNotFoundException -> 0x11d5, IllegalAccessException -> 0x105a, InvocationTargetException -> 0x0edf, all -> 0x0d63 }
    L_0x06ec:
        r4 = move-exception;
        r11 = r3;
        r12 = r22;
        r13 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r4 = r19;
        r2 = r21;
        r5 = r18;
        r7 = r6;
        r6 = r17;
    L_0x06fe:
        r3 = 492; // 0x1ec float:6.9E-43 double:2.43E-321;
        r0 = r33;
        r14 = r0.b;
        r15 = 0;
        r14.mHasActiveThread = r15;
        if (r2 == 0) goto L_0x070c;
    L_0x0709:
        r2.release();
    L_0x070c:
        if (r12 == 0) goto L_0x071f;
    L_0x070e:
        r2 = r12.getClass();
        r14 = "close";
        r15 = 0;
        r2 = r2.getMethod(r14, r15);	 Catch:{ SecurityException -> 0x1322, NoSuchMethodException -> 0x131f, IllegalArgumentException -> 0x131c, IllegalAccessException -> 0x1319, InvocationTargetException -> 0x1316 }
        r14 = 0;
        r14 = new java.lang.Object[r14];	 Catch:{ SecurityException -> 0x1322, NoSuchMethodException -> 0x131f, IllegalArgumentException -> 0x131c, IllegalAccessException -> 0x1319, InvocationTargetException -> 0x1316 }
        r2.invoke(r12, r14);	 Catch:{ SecurityException -> 0x1322, NoSuchMethodException -> 0x131f, IllegalArgumentException -> 0x131c, IllegalAccessException -> 0x1319, InvocationTargetException -> 0x1316 }
    L_0x071f:
        if (r13 == 0) goto L_0x0724;
    L_0x0721:
        r13.close();	 Catch:{ IOException -> 0x0ba0 }
    L_0x0724:
        if (r8 == 0) goto L_0x0737;
    L_0x0726:
        r2 = com.igexin.download.Downloads.isStatusError(r3);
        if (r2 == 0) goto L_0x07c4;
    L_0x072c:
        if (r11 != 0) goto L_0x0737;
    L_0x072e:
        r2 = new java.io.File;
        r2.<init>(r8);
        r2.delete();
        r8 = 0;
    L_0x0737:
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x00d3;
    L_0x073e:
        r10 = r8;
        r13 = r4;
        r23 = r12;
        r4 = r6;
        goto L_0x050f;
    L_0x0745:
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r8 = r2;
        r10 = r7;
        r11 = r5;
        r9 = r16;
        r7 = r4;
        r2 = r3;
        r5 = r18;
        r4 = r19;
        r3 = r6;
        r6 = r17;
        goto L_0x0279;
    L_0x0757:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x14af;
    L_0x075d:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x1477 }
        r12 = r2.getMethods();	 Catch:{ Exception -> 0x1477 }
        r2 = 0;
        r8 = 0;
    L_0x0769:
        r13 = r12.length;	 Catch:{ Exception -> 0x1477 }
        if (r8 >= r13) goto L_0x077c;
    L_0x076c:
        r13 = r12[r8];	 Catch:{ Exception -> 0x1477 }
        r13 = r13.getName();	 Catch:{ Exception -> 0x1477 }
        r14 = "setPermissions";
        r13 = r13.endsWith(r14);	 Catch:{ Exception -> 0x1477 }
        if (r13 == 0) goto L_0x07b1;
    L_0x077a:
        r2 = r12[r8];	 Catch:{ Exception -> 0x1477 }
    L_0x077c:
        if (r2 == 0) goto L_0x07a1;
    L_0x077e:
        r8 = 0;
        r12 = 4;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x1477 }
        r13 = 0;
        r12[r13] = r11;	 Catch:{ Exception -> 0x1477 }
        r13 = 1;
        r14 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1477 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1477 }
        r13 = 2;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1477 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1477 }
        r13 = 3;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1477 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1477 }
        r2.invoke(r8, r12);	 Catch:{ Exception -> 0x1477 }
    L_0x07a1:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x07b4, SyncFailedException -> 0x07b8, IOException -> 0x07bc, RuntimeException -> 0x07c0 }
        r8 = 1;
        r2.<init>(r11, r8);	 Catch:{ FileNotFoundException -> 0x07b4, SyncFailedException -> 0x07b8, IOException -> 0x07bc, RuntimeException -> 0x07c0 }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x07b4, SyncFailedException -> 0x07b8, IOException -> 0x07bc, RuntimeException -> 0x07c0 }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x07b4, SyncFailedException -> 0x07b8, IOException -> 0x07bc, RuntimeException -> 0x07c0 }
        r8 = r11;
        goto L_0x02b3;
    L_0x07b1:
        r8 = r8 + 1;
        goto L_0x0769;
    L_0x07b4:
        r2 = move-exception;
        r8 = r11;
        goto L_0x02b3;
    L_0x07b8:
        r2 = move-exception;
        r8 = r11;
        goto L_0x02b3;
    L_0x07bc:
        r2 = move-exception;
        r8 = r11;
        goto L_0x02b3;
    L_0x07c0:
        r2 = move-exception;
        r8 = r11;
        goto L_0x02b3;
    L_0x07c4:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x0737;
    L_0x07ca:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x1313 }
        r12 = r2.getMethods();	 Catch:{ Exception -> 0x1313 }
        r2 = 0;
        r11 = 0;
    L_0x07d6:
        r13 = r12.length;	 Catch:{ Exception -> 0x1313 }
        if (r11 >= r13) goto L_0x07e9;
    L_0x07d9:
        r13 = r12[r11];	 Catch:{ Exception -> 0x1313 }
        r13 = r13.getName();	 Catch:{ Exception -> 0x1313 }
        r14 = "setPermissions";
        r13 = r13.endsWith(r14);	 Catch:{ Exception -> 0x1313 }
        if (r13 == 0) goto L_0x0820;
    L_0x07e7:
        r2 = r12[r11];	 Catch:{ Exception -> 0x1313 }
    L_0x07e9:
        if (r2 == 0) goto L_0x080e;
    L_0x07eb:
        r11 = 0;
        r12 = 4;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x1313 }
        r13 = 0;
        r12[r13] = r8;	 Catch:{ Exception -> 0x1313 }
        r13 = 1;
        r14 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1313 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1313 }
        r13 = 2;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1313 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1313 }
        r13 = 3;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1313 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1313 }
        r2.invoke(r11, r12);	 Catch:{ Exception -> 0x1313 }
    L_0x080e:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x081d, SyncFailedException -> 0x1310, IOException -> 0x130d, RuntimeException -> 0x130a }
        r11 = 1;
        r2.<init>(r8, r11);	 Catch:{ FileNotFoundException -> 0x081d, SyncFailedException -> 0x1310, IOException -> 0x130d, RuntimeException -> 0x130a }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x081d, SyncFailedException -> 0x1310, IOException -> 0x130d, RuntimeException -> 0x130a }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x081d, SyncFailedException -> 0x1310, IOException -> 0x130d, RuntimeException -> 0x130a }
        goto L_0x0737;
    L_0x081d:
        r2 = move-exception;
        goto L_0x0737;
    L_0x0820:
        r11 = r11 + 1;
        goto L_0x07d6;
    L_0x0823:
        r2 = move-exception;
        r21 = r3;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r14;
    L_0x0834:
        r3 = 491; // 0x1eb float:6.88E-43 double:2.426E-321;
        r0 = r33;
        r2 = r0.b;
        r13 = 0;
        r2.mHasActiveThread = r13;
        if (r21 == 0) goto L_0x0842;
    L_0x083f:
        r21.release();
    L_0x0842:
        if (r22 == 0) goto L_0x0857;
    L_0x0844:
        r2 = r22.getClass();
        r13 = "close";
        r14 = 0;
        r2 = r2.getMethod(r13, r14);	 Catch:{ SecurityException -> 0x11fe, NoSuchMethodException -> 0x11fb, IllegalArgumentException -> 0x11f8, IllegalAccessException -> 0x11f5, InvocationTargetException -> 0x11f2 }
        r13 = 0;
        r13 = new java.lang.Object[r13];	 Catch:{ SecurityException -> 0x11fe, NoSuchMethodException -> 0x11fb, IllegalArgumentException -> 0x11f8, IllegalAccessException -> 0x11f5, InvocationTargetException -> 0x11f2 }
        r0 = r22;
        r2.invoke(r0, r13);	 Catch:{ SecurityException -> 0x11fe, NoSuchMethodException -> 0x11fb, IllegalArgumentException -> 0x11f8, IllegalAccessException -> 0x11f5, InvocationTargetException -> 0x11f2 }
    L_0x0857:
        if (r12 == 0) goto L_0x085c;
    L_0x0859:
        r12.close();	 Catch:{ IOException -> 0x0ba3 }
    L_0x085c:
        if (r8 == 0) goto L_0x086f;
    L_0x085e:
        r2 = com.igexin.download.Downloads.isStatusError(r3);
        if (r2 == 0) goto L_0x0876;
    L_0x0864:
        if (r11 != 0) goto L_0x086f;
    L_0x0866:
        r2 = new java.io.File;
        r2.<init>(r8);
        r2.delete();
        r8 = 0;
    L_0x086f:
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x00d3;
    L_0x0876:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x086f;
    L_0x087c:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x11ef }
        r12 = r2.getMethods();	 Catch:{ Exception -> 0x11ef }
        r2 = 0;
        r11 = 0;
    L_0x0888:
        r13 = r12.length;	 Catch:{ Exception -> 0x11ef }
        if (r11 >= r13) goto L_0x089b;
    L_0x088b:
        r13 = r12[r11];	 Catch:{ Exception -> 0x11ef }
        r13 = r13.getName();	 Catch:{ Exception -> 0x11ef }
        r14 = "setPermissions";
        r13 = r13.endsWith(r14);	 Catch:{ Exception -> 0x11ef }
        if (r13 == 0) goto L_0x08d0;
    L_0x0899:
        r2 = r12[r11];	 Catch:{ Exception -> 0x11ef }
    L_0x089b:
        if (r2 == 0) goto L_0x08c0;
    L_0x089d:
        r11 = 0;
        r12 = 4;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x11ef }
        r13 = 0;
        r12[r13] = r8;	 Catch:{ Exception -> 0x11ef }
        r13 = 1;
        r14 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x11ef }
        r12[r13] = r14;	 Catch:{ Exception -> 0x11ef }
        r13 = 2;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x11ef }
        r12[r13] = r14;	 Catch:{ Exception -> 0x11ef }
        r13 = 3;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x11ef }
        r12[r13] = r14;	 Catch:{ Exception -> 0x11ef }
        r2.invoke(r11, r12);	 Catch:{ Exception -> 0x11ef }
    L_0x08c0:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x08ce, SyncFailedException -> 0x11ec, IOException -> 0x11e9, RuntimeException -> 0x11e6 }
        r11 = 1;
        r2.<init>(r8, r11);	 Catch:{ FileNotFoundException -> 0x08ce, SyncFailedException -> 0x11ec, IOException -> 0x11e9, RuntimeException -> 0x11e6 }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x08ce, SyncFailedException -> 0x11ec, IOException -> 0x11e9, RuntimeException -> 0x11e6 }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x08ce, SyncFailedException -> 0x11ec, IOException -> 0x11e9, RuntimeException -> 0x11e6 }
        goto L_0x086f;
    L_0x08ce:
        r2 = move-exception;
        goto L_0x086f;
    L_0x08d0:
        r11 = r11 + 1;
        goto L_0x0888;
    L_0x08d3:
        r2 = move-exception;
        r21 = r3;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r14;
        r3 = r20;
    L_0x08e6:
        r0 = r33;
        r2 = r0.b;
        r13 = 0;
        r2.mHasActiveThread = r13;
        if (r21 == 0) goto L_0x08f2;
    L_0x08ef:
        r21.release();
    L_0x08f2:
        if (r22 == 0) goto L_0x0907;
    L_0x08f4:
        r2 = r22.getClass();
        r13 = "close";
        r14 = 0;
        r2 = r2.getMethod(r13, r14);	 Catch:{ SecurityException -> 0x1083, NoSuchMethodException -> 0x1080, IllegalArgumentException -> 0x107d, IllegalAccessException -> 0x107a, InvocationTargetException -> 0x1077 }
        r13 = 0;
        r13 = new java.lang.Object[r13];	 Catch:{ SecurityException -> 0x1083, NoSuchMethodException -> 0x1080, IllegalArgumentException -> 0x107d, IllegalAccessException -> 0x107a, InvocationTargetException -> 0x1077 }
        r0 = r22;
        r2.invoke(r0, r13);	 Catch:{ SecurityException -> 0x1083, NoSuchMethodException -> 0x1080, IllegalArgumentException -> 0x107d, IllegalAccessException -> 0x107a, InvocationTargetException -> 0x1077 }
    L_0x0907:
        if (r12 == 0) goto L_0x090c;
    L_0x0909:
        r12.close();	 Catch:{ IOException -> 0x0ba6 }
    L_0x090c:
        if (r8 == 0) goto L_0x091f;
    L_0x090e:
        r2 = com.igexin.download.Downloads.isStatusError(r3);
        if (r2 == 0) goto L_0x0926;
    L_0x0914:
        if (r11 != 0) goto L_0x091f;
    L_0x0916:
        r2 = new java.io.File;
        r2.<init>(r8);
        r2.delete();
        r8 = 0;
    L_0x091f:
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x00d3;
    L_0x0926:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x091f;
    L_0x092c:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x1074 }
        r12 = r2.getMethods();	 Catch:{ Exception -> 0x1074 }
        r2 = 0;
        r11 = 0;
    L_0x0938:
        r13 = r12.length;	 Catch:{ Exception -> 0x1074 }
        if (r11 >= r13) goto L_0x094b;
    L_0x093b:
        r13 = r12[r11];	 Catch:{ Exception -> 0x1074 }
        r13 = r13.getName();	 Catch:{ Exception -> 0x1074 }
        r14 = "setPermissions";
        r13 = r13.endsWith(r14);	 Catch:{ Exception -> 0x1074 }
        if (r13 == 0) goto L_0x0980;
    L_0x0949:
        r2 = r12[r11];	 Catch:{ Exception -> 0x1074 }
    L_0x094b:
        if (r2 == 0) goto L_0x0970;
    L_0x094d:
        r11 = 0;
        r12 = 4;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x1074 }
        r13 = 0;
        r12[r13] = r8;	 Catch:{ Exception -> 0x1074 }
        r13 = 1;
        r14 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1074 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1074 }
        r13 = 2;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1074 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1074 }
        r13 = 3;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x1074 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x1074 }
        r2.invoke(r11, r12);	 Catch:{ Exception -> 0x1074 }
    L_0x0970:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x097e, SyncFailedException -> 0x1071, IOException -> 0x106e, RuntimeException -> 0x106b }
        r11 = 1;
        r2.<init>(r8, r11);	 Catch:{ FileNotFoundException -> 0x097e, SyncFailedException -> 0x1071, IOException -> 0x106e, RuntimeException -> 0x106b }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x097e, SyncFailedException -> 0x1071, IOException -> 0x106e, RuntimeException -> 0x106b }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x097e, SyncFailedException -> 0x1071, IOException -> 0x106e, RuntimeException -> 0x106b }
        goto L_0x091f;
    L_0x097e:
        r2 = move-exception;
        goto L_0x091f;
    L_0x0980:
        r11 = r11 + 1;
        goto L_0x0938;
    L_0x0983:
        r2 = move-exception;
        r21 = r3;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r14;
        r3 = r20;
    L_0x0996:
        r0 = r33;
        r2 = r0.b;
        r13 = 0;
        r2.mHasActiveThread = r13;
        if (r21 == 0) goto L_0x09a2;
    L_0x099f:
        r21.release();
    L_0x09a2:
        if (r22 == 0) goto L_0x09b7;
    L_0x09a4:
        r2 = r22.getClass();
        r13 = "close";
        r14 = 0;
        r2 = r2.getMethod(r13, r14);	 Catch:{ SecurityException -> 0x0f08, NoSuchMethodException -> 0x0f05, IllegalArgumentException -> 0x0f02, IllegalAccessException -> 0x0eff, InvocationTargetException -> 0x0efc }
        r13 = 0;
        r13 = new java.lang.Object[r13];	 Catch:{ SecurityException -> 0x0f08, NoSuchMethodException -> 0x0f05, IllegalArgumentException -> 0x0f02, IllegalAccessException -> 0x0eff, InvocationTargetException -> 0x0efc }
        r0 = r22;
        r2.invoke(r0, r13);	 Catch:{ SecurityException -> 0x0f08, NoSuchMethodException -> 0x0f05, IllegalArgumentException -> 0x0f02, IllegalAccessException -> 0x0eff, InvocationTargetException -> 0x0efc }
    L_0x09b7:
        if (r12 == 0) goto L_0x09bc;
    L_0x09b9:
        r12.close();	 Catch:{ IOException -> 0x0ba9 }
    L_0x09bc:
        if (r8 == 0) goto L_0x09cf;
    L_0x09be:
        r2 = com.igexin.download.Downloads.isStatusError(r3);
        if (r2 == 0) goto L_0x09d6;
    L_0x09c4:
        if (r11 != 0) goto L_0x09cf;
    L_0x09c6:
        r2 = new java.io.File;
        r2.<init>(r8);
        r2.delete();
        r8 = 0;
    L_0x09cf:
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x00d3;
    L_0x09d6:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x09cf;
    L_0x09dc:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x0ef9 }
        r12 = r2.getMethods();	 Catch:{ Exception -> 0x0ef9 }
        r2 = 0;
        r11 = 0;
    L_0x09e8:
        r13 = r12.length;	 Catch:{ Exception -> 0x0ef9 }
        if (r11 >= r13) goto L_0x09fb;
    L_0x09eb:
        r13 = r12[r11];	 Catch:{ Exception -> 0x0ef9 }
        r13 = r13.getName();	 Catch:{ Exception -> 0x0ef9 }
        r14 = "setPermissions";
        r13 = r13.endsWith(r14);	 Catch:{ Exception -> 0x0ef9 }
        if (r13 == 0) goto L_0x0a30;
    L_0x09f9:
        r2 = r12[r11];	 Catch:{ Exception -> 0x0ef9 }
    L_0x09fb:
        if (r2 == 0) goto L_0x0a20;
    L_0x09fd:
        r11 = 0;
        r12 = 4;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0ef9 }
        r13 = 0;
        r12[r13] = r8;	 Catch:{ Exception -> 0x0ef9 }
        r13 = 1;
        r14 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0ef9 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x0ef9 }
        r13 = 2;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0ef9 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x0ef9 }
        r13 = 3;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0ef9 }
        r12[r13] = r14;	 Catch:{ Exception -> 0x0ef9 }
        r2.invoke(r11, r12);	 Catch:{ Exception -> 0x0ef9 }
    L_0x0a20:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x0a2e, SyncFailedException -> 0x0ef6, IOException -> 0x0ef3, RuntimeException -> 0x0ef0 }
        r11 = 1;
        r2.<init>(r8, r11);	 Catch:{ FileNotFoundException -> 0x0a2e, SyncFailedException -> 0x0ef6, IOException -> 0x0ef3, RuntimeException -> 0x0ef0 }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x0a2e, SyncFailedException -> 0x0ef6, IOException -> 0x0ef3, RuntimeException -> 0x0ef0 }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x0a2e, SyncFailedException -> 0x0ef6, IOException -> 0x0ef3, RuntimeException -> 0x0ef0 }
        goto L_0x09cf;
    L_0x0a2e:
        r2 = move-exception;
        goto L_0x09cf;
    L_0x0a30:
        r11 = r11 + 1;
        goto L_0x09e8;
    L_0x0a33:
        r2 = move-exception;
        r21 = r3;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r14;
        r3 = r20;
    L_0x0a46:
        r0 = r33;
        r2 = r0.b;
        r13 = 0;
        r2.mHasActiveThread = r13;
        if (r21 == 0) goto L_0x0a52;
    L_0x0a4f:
        r21.release();
    L_0x0a52:
        if (r22 == 0) goto L_0x0a67;
    L_0x0a54:
        r2 = r22.getClass();
        r13 = "close";
        r14 = 0;
        r2 = r2.getMethod(r13, r14);	 Catch:{ SecurityException -> 0x0d8d, NoSuchMethodException -> 0x0d8a, IllegalArgumentException -> 0x0d87, IllegalAccessException -> 0x0d84, InvocationTargetException -> 0x0d81 }
        r13 = 0;
        r13 = new java.lang.Object[r13];	 Catch:{ SecurityException -> 0x0d8d, NoSuchMethodException -> 0x0d8a, IllegalArgumentException -> 0x0d87, IllegalAccessException -> 0x0d84, InvocationTargetException -> 0x0d81 }
        r0 = r22;
        r2.invoke(r0, r13);	 Catch:{ SecurityException -> 0x0d8d, NoSuchMethodException -> 0x0d8a, IllegalArgumentException -> 0x0d87, IllegalAccessException -> 0x0d84, InvocationTargetException -> 0x0d81 }
    L_0x0a67:
        if (r12 == 0) goto L_0x0a6c;
    L_0x0a69:
        r12.close();	 Catch:{ IOException -> 0x0bac }
    L_0x0a6c:
        if (r8 == 0) goto L_0x0a7f;
    L_0x0a6e:
        r2 = com.igexin.download.Downloads.isStatusError(r3);
        if (r2 == 0) goto L_0x0a86;
    L_0x0a74:
        if (r11 != 0) goto L_0x0a7f;
    L_0x0a76:
        r2 = new java.io.File;
        r2.<init>(r8);
        r2.delete();
        r8 = 0;
    L_0x0a7f:
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x00d3;
    L_0x0a86:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x0a7f;
    L_0x0a8c:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x0d7e }
        r12 = r2.getMethods();	 Catch:{ Exception -> 0x0d7e }
        r2 = 0;
        r11 = 0;
    L_0x0a98:
        r13 = r12.length;	 Catch:{ Exception -> 0x0d7e }
        if (r11 >= r13) goto L_0x0aab;
    L_0x0a9b:
        r13 = r12[r11];	 Catch:{ Exception -> 0x0d7e }
        r13 = r13.getName();	 Catch:{ Exception -> 0x0d7e }
        r14 = "setPermissions";
        r13 = r13.endsWith(r14);	 Catch:{ Exception -> 0x0d7e }
        if (r13 == 0) goto L_0x0ae0;
    L_0x0aa9:
        r2 = r12[r11];	 Catch:{ Exception -> 0x0d7e }
    L_0x0aab:
        if (r2 == 0) goto L_0x0ad0;
    L_0x0aad:
        r11 = 0;
        r12 = 4;
        r12 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0d7e }
        r13 = 0;
        r12[r13] = r8;	 Catch:{ Exception -> 0x0d7e }
        r13 = 1;
        r14 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0d7e }
        r12[r13] = r14;	 Catch:{ Exception -> 0x0d7e }
        r13 = 2;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0d7e }
        r12[r13] = r14;	 Catch:{ Exception -> 0x0d7e }
        r13 = 3;
        r14 = -1;
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0d7e }
        r12[r13] = r14;	 Catch:{ Exception -> 0x0d7e }
        r2.invoke(r11, r12);	 Catch:{ Exception -> 0x0d7e }
    L_0x0ad0:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x0ade, SyncFailedException -> 0x0d7b, IOException -> 0x0d78, RuntimeException -> 0x0d75 }
        r11 = 1;
        r2.<init>(r8, r11);	 Catch:{ FileNotFoundException -> 0x0ade, SyncFailedException -> 0x0d7b, IOException -> 0x0d78, RuntimeException -> 0x0d75 }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x0ade, SyncFailedException -> 0x0d7b, IOException -> 0x0d78, RuntimeException -> 0x0d75 }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x0ade, SyncFailedException -> 0x0d7b, IOException -> 0x0d78, RuntimeException -> 0x0d75 }
        goto L_0x0a7f;
    L_0x0ade:
        r2 = move-exception;
        goto L_0x0a7f;
    L_0x0ae0:
        r11 = r11 + 1;
        goto L_0x0a98;
    L_0x0ae3:
        r2 = move-exception;
        r21 = r3;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r14;
        r3 = r20;
        r32 = r11;
        r11 = r2;
        r2 = r32;
    L_0x0afb:
        r0 = r33;
        r13 = r0.b;
        r14 = 0;
        r13.mHasActiveThread = r14;
        if (r21 == 0) goto L_0x0b07;
    L_0x0b04:
        r21.release();
    L_0x0b07:
        if (r22 == 0) goto L_0x0b1c;
    L_0x0b09:
        r13 = r22.getClass();
        r14 = "close";
        r15 = 0;
        r13 = r13.getMethod(r14, r15);	 Catch:{ SecurityException -> 0x0bc8, NoSuchMethodException -> 0x0bc5, IllegalArgumentException -> 0x0bc2, IllegalAccessException -> 0x0bbf, InvocationTargetException -> 0x0bbc }
        r14 = 0;
        r14 = new java.lang.Object[r14];	 Catch:{ SecurityException -> 0x0bc8, NoSuchMethodException -> 0x0bc5, IllegalArgumentException -> 0x0bc2, IllegalAccessException -> 0x0bbf, InvocationTargetException -> 0x0bbc }
        r0 = r22;
        r13.invoke(r0, r14);	 Catch:{ SecurityException -> 0x0bc8, NoSuchMethodException -> 0x0bc5, IllegalArgumentException -> 0x0bc2, IllegalAccessException -> 0x0bbf, InvocationTargetException -> 0x0bbc }
    L_0x0b1c:
        if (r12 == 0) goto L_0x0b21;
    L_0x0b1e:
        r12.close();	 Catch:{ IOException -> 0x0baf }
    L_0x0b21:
        if (r8 == 0) goto L_0x0b34;
    L_0x0b23:
        r12 = com.igexin.download.Downloads.isStatusError(r3);
        if (r12 == 0) goto L_0x0b3a;
    L_0x0b29:
        if (r2 != 0) goto L_0x0b34;
    L_0x0b2b:
        r2 = new java.io.File;
        r2.<init>(r8);
        r2.delete();
        r8 = 0;
    L_0x0b34:
        r2 = r33;
        r2.a(r3, r4, r5, r6, r7, r8, r9, r10);
        throw r11;
    L_0x0b3a:
        r2 = com.igexin.download.Downloads.isStatusSuccess(r3);
        if (r2 == 0) goto L_0x0b34;
    L_0x0b40:
        r2 = "android.os.FileUtils";
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x0bba }
        r13 = r2.getMethods();	 Catch:{ Exception -> 0x0bba }
        r2 = 0;
        r12 = 0;
    L_0x0b4c:
        r14 = r13.length;	 Catch:{ Exception -> 0x0bba }
        if (r12 >= r14) goto L_0x0b5f;
    L_0x0b4f:
        r14 = r13[r12];	 Catch:{ Exception -> 0x0bba }
        r14 = r14.getName();	 Catch:{ Exception -> 0x0bba }
        r15 = "setPermissions";
        r14 = r14.endsWith(r15);	 Catch:{ Exception -> 0x0bba }
        if (r14 == 0) goto L_0x0b94;
    L_0x0b5d:
        r2 = r13[r12];	 Catch:{ Exception -> 0x0bba }
    L_0x0b5f:
        if (r2 == 0) goto L_0x0b84;
    L_0x0b61:
        r12 = 0;
        r13 = 4;
        r13 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0bba }
        r14 = 0;
        r13[r14] = r8;	 Catch:{ Exception -> 0x0bba }
        r14 = 1;
        r15 = 420; // 0x1a4 float:5.89E-43 double:2.075E-321;
        r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x0bba }
        r13[r14] = r15;	 Catch:{ Exception -> 0x0bba }
        r14 = 2;
        r15 = -1;
        r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x0bba }
        r13[r14] = r15;	 Catch:{ Exception -> 0x0bba }
        r14 = 3;
        r15 = -1;
        r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x0bba }
        r13[r14] = r15;	 Catch:{ Exception -> 0x0bba }
        r2.invoke(r12, r13);	 Catch:{ Exception -> 0x0bba }
    L_0x0b84:
        r2 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x0b92, SyncFailedException -> 0x0bb7, IOException -> 0x0bb4, RuntimeException -> 0x0bb2 }
        r12 = 1;
        r2.<init>(r8, r12);	 Catch:{ FileNotFoundException -> 0x0b92, SyncFailedException -> 0x0bb7, IOException -> 0x0bb4, RuntimeException -> 0x0bb2 }
        r2 = r2.getFD();	 Catch:{ FileNotFoundException -> 0x0b92, SyncFailedException -> 0x0bb7, IOException -> 0x0bb4, RuntimeException -> 0x0bb2 }
        r2.sync();	 Catch:{ FileNotFoundException -> 0x0b92, SyncFailedException -> 0x0bb7, IOException -> 0x0bb4, RuntimeException -> 0x0bb2 }
        goto L_0x0b34;
    L_0x0b92:
        r2 = move-exception;
        goto L_0x0b34;
    L_0x0b94:
        r12 = r12 + 1;
        goto L_0x0b4c;
    L_0x0b97:
        r2 = move-exception;
        goto L_0x00b3;
    L_0x0b9a:
        r4 = move-exception;
        goto L_0x063e;
    L_0x0b9d:
        r8 = move-exception;
        goto L_0x029f;
    L_0x0ba0:
        r2 = move-exception;
        goto L_0x0724;
    L_0x0ba3:
        r2 = move-exception;
        goto L_0x085c;
    L_0x0ba6:
        r2 = move-exception;
        goto L_0x090c;
    L_0x0ba9:
        r2 = move-exception;
        goto L_0x09bc;
    L_0x0bac:
        r2 = move-exception;
        goto L_0x0a6c;
    L_0x0baf:
        r12 = move-exception;
        goto L_0x0b21;
    L_0x0bb2:
        r2 = move-exception;
        goto L_0x0b34;
    L_0x0bb4:
        r2 = move-exception;
        goto L_0x0b34;
    L_0x0bb7:
        r2 = move-exception;
        goto L_0x0b34;
    L_0x0bba:
        r2 = move-exception;
        goto L_0x0b84;
    L_0x0bbc:
        r13 = move-exception;
        goto L_0x0b1c;
    L_0x0bbf:
        r13 = move-exception;
        goto L_0x0b1c;
    L_0x0bc2:
        r13 = move-exception;
        goto L_0x0b1c;
    L_0x0bc5:
        r13 = move-exception;
        goto L_0x0b1c;
    L_0x0bc8:
        r13 = move-exception;
        goto L_0x0b1c;
    L_0x0bcb:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r5 = r18;
        r12 = r14;
        r32 = r2;
        r2 = r11;
        r11 = r32;
        goto L_0x0afb;
    L_0x0be3:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        r32 = r2;
        r2 = r11;
        r11 = r32;
        goto L_0x0afb;
    L_0x0bfa:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r14;
        r32 = r2;
        r2 = r11;
        r11 = r32;
        goto L_0x0afb;
    L_0x0c0f:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r5 = r18;
        r12 = r14;
        r32 = r2;
        r2 = r11;
        r11 = r32;
        goto L_0x0afb;
    L_0x0c27:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r2;
        r2 = r11;
        r11 = r3;
        r3 = r20;
        goto L_0x0afb;
    L_0x0c3c:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r2;
        r2 = r11;
        r11 = r3;
        r3 = r20;
        goto L_0x0afb;
    L_0x0c51:
        r3 = move-exception;
        r12 = r2;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r2 = r11;
        r11 = r3;
        r3 = r20;
        goto L_0x0afb;
    L_0x0c64:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r32 = r2;
        r2 = r11;
        r11 = r32;
        goto L_0x0afb;
    L_0x0c79:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r32 = r2;
        r2 = r11;
        r11 = r32;
        goto L_0x0afb;
    L_0x0c8a:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r32 = r11;
        r11 = r2;
        r2 = r32;
        goto L_0x0afb;
    L_0x0c99:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r32 = r11;
        r11 = r2;
        r2 = r32;
        goto L_0x0afb;
    L_0x0cac:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r5 = r18;
        r4 = r19;
        r32 = r11;
        r11 = r2;
        r2 = r32;
        goto L_0x0afb;
    L_0x0cbb:
        r4 = move-exception;
        r11 = r4;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r32 = r3;
        r3 = r2;
        r2 = r32;
        goto L_0x0afb;
    L_0x0ccf:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r7 = r15;
        r32 = r2;
        r2 = r11;
        r11 = r32;
        goto L_0x0afb;
    L_0x0ce4:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r7 = r15;
        r32 = r11;
        r11 = r2;
        r2 = r32;
        goto L_0x0afb;
    L_0x0cf7:
        r3 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r2 = r11;
        r5 = r18;
        r7 = r15;
        r11 = r3;
        r3 = r20;
        goto L_0x0afb;
    L_0x0d0a:
        r6 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r15;
        r2 = r11;
        r5 = r18;
        r11 = r6;
        r6 = r17;
        goto L_0x0afb;
    L_0x0d19:
        r6 = move-exception;
        r11 = r6;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r4;
        r2 = r3;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        goto L_0x0afb;
    L_0x0d2c:
        r9 = move-exception;
        r11 = r9;
        r12 = r2;
        r10 = r7;
        r2 = r3;
        r7 = r4;
        r9 = r16;
        r4 = r6;
        r3 = r8;
        r8 = r5;
        r6 = r17;
        r5 = r18;
        goto L_0x0afb;
    L_0x0d3d:
        r4 = move-exception;
        r11 = r4;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r6;
        r2 = r3;
        r5 = r18;
        r4 = r19;
        r6 = r17;
        r3 = r20;
        goto L_0x0afb;
    L_0x0d50:
        r2 = move-exception;
        r11 = r2;
        r12 = r4;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r6;
        r2 = r3;
        r5 = r18;
        r4 = r19;
        r6 = r17;
        r3 = r20;
        goto L_0x0afb;
    L_0x0d63:
        r4 = move-exception;
        r11 = r4;
        r12 = r2;
        r10 = r7;
        r9 = r16;
        r7 = r6;
        r2 = r3;
        r4 = r19;
        r6 = r17;
        r3 = r8;
        r8 = r5;
        r5 = r18;
        goto L_0x0afb;
    L_0x0d75:
        r2 = move-exception;
        goto L_0x0a7f;
    L_0x0d78:
        r2 = move-exception;
        goto L_0x0a7f;
    L_0x0d7b:
        r2 = move-exception;
        goto L_0x0a7f;
    L_0x0d7e:
        r2 = move-exception;
        goto L_0x0ad0;
    L_0x0d81:
        r2 = move-exception;
        goto L_0x0a67;
    L_0x0d84:
        r2 = move-exception;
        goto L_0x0a67;
    L_0x0d87:
        r2 = move-exception;
        goto L_0x0a67;
    L_0x0d8a:
        r2 = move-exception;
        goto L_0x0a67;
    L_0x0d8d:
        r2 = move-exception;
        goto L_0x0a67;
    L_0x0d90:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        r5 = r18;
        goto L_0x0a46;
    L_0x0da3:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        goto L_0x0a46;
    L_0x0db5:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r14;
        goto L_0x0a46;
    L_0x0dc5:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        r5 = r18;
        goto L_0x0a46;
    L_0x0dd8:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r2;
        r5 = r18;
        goto L_0x0a46;
    L_0x0deb:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r12 = r2;
        goto L_0x0a46;
    L_0x0dfe:
        r3 = move-exception;
        r12 = r2;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        goto L_0x0a46;
    L_0x0e0f:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        goto L_0x0a46;
    L_0x0e1f:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        goto L_0x0a46;
    L_0x0e2b:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        goto L_0x0a46;
    L_0x0e35:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        goto L_0x0a46;
    L_0x0e43:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r5 = r18;
        r4 = r19;
        goto L_0x0a46;
    L_0x0e4d:
        r4 = move-exception;
        r11 = r3;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r2;
        goto L_0x0a46;
    L_0x0e5d:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r7 = r15;
        goto L_0x0a46;
    L_0x0e6d:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r7 = r15;
        goto L_0x0a46;
    L_0x0e7b:
        r3 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r7 = r15;
        r5 = r18;
        goto L_0x0a46;
    L_0x0e8c:
        r6 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r15;
        r5 = r18;
        goto L_0x0a46;
    L_0x0e99:
        r6 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r4;
        r5 = r18;
        r3 = r20;
        r4 = r19;
        goto L_0x0a46;
    L_0x0eab:
        r9 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r9 = r16;
        r7 = r4;
        r3 = r8;
        r4 = r6;
        r8 = r5;
        r6 = r17;
        r5 = r18;
        goto L_0x0a46;
    L_0x0ebb:
        r4 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r4 = r19;
        r5 = r18;
        r7 = r6;
        r3 = r20;
        r6 = r17;
        goto L_0x0a46;
    L_0x0ecd:
        r2 = move-exception;
        r11 = r3;
        r12 = r4;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r6;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r6 = r17;
        goto L_0x0a46;
    L_0x0edf:
        r4 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r9 = r16;
        r4 = r19;
        r7 = r6;
        r3 = r8;
        r8 = r5;
        r6 = r17;
        r5 = r18;
        goto L_0x0a46;
    L_0x0ef0:
        r2 = move-exception;
        goto L_0x09cf;
    L_0x0ef3:
        r2 = move-exception;
        goto L_0x09cf;
    L_0x0ef6:
        r2 = move-exception;
        goto L_0x09cf;
    L_0x0ef9:
        r2 = move-exception;
        goto L_0x0a20;
    L_0x0efc:
        r2 = move-exception;
        goto L_0x09b7;
    L_0x0eff:
        r2 = move-exception;
        goto L_0x09b7;
    L_0x0f02:
        r2 = move-exception;
        goto L_0x09b7;
    L_0x0f05:
        r2 = move-exception;
        goto L_0x09b7;
    L_0x0f08:
        r2 = move-exception;
        goto L_0x09b7;
    L_0x0f0b:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        r5 = r18;
        goto L_0x0996;
    L_0x0f1e:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        goto L_0x0996;
    L_0x0f30:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r14;
        goto L_0x0996;
    L_0x0f40:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        r5 = r18;
        goto L_0x0996;
    L_0x0f53:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r2;
        r5 = r18;
        goto L_0x0996;
    L_0x0f66:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r12 = r2;
        goto L_0x0996;
    L_0x0f79:
        r3 = move-exception;
        r12 = r2;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        goto L_0x0996;
    L_0x0f8a:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        goto L_0x0996;
    L_0x0f9a:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        goto L_0x0996;
    L_0x0fa6:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        goto L_0x0996;
    L_0x0fb0:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        goto L_0x0996;
    L_0x0fbe:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r5 = r18;
        r4 = r19;
        goto L_0x0996;
    L_0x0fc8:
        r4 = move-exception;
        r11 = r3;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r2;
        goto L_0x0996;
    L_0x0fd8:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r7 = r15;
        goto L_0x0996;
    L_0x0fe8:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r7 = r15;
        goto L_0x0996;
    L_0x0ff6:
        r3 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r7 = r15;
        r5 = r18;
        goto L_0x0996;
    L_0x1007:
        r6 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r15;
        r5 = r18;
        goto L_0x0996;
    L_0x1014:
        r6 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r4;
        r5 = r18;
        r3 = r20;
        r4 = r19;
        goto L_0x0996;
    L_0x1026:
        r9 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r9 = r16;
        r7 = r4;
        r3 = r8;
        r4 = r6;
        r8 = r5;
        r6 = r17;
        r5 = r18;
        goto L_0x0996;
    L_0x1036:
        r4 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r4 = r19;
        r5 = r18;
        r7 = r6;
        r3 = r20;
        r6 = r17;
        goto L_0x0996;
    L_0x1048:
        r2 = move-exception;
        r11 = r3;
        r12 = r4;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r6;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r6 = r17;
        goto L_0x0996;
    L_0x105a:
        r4 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r9 = r16;
        r4 = r19;
        r7 = r6;
        r3 = r8;
        r8 = r5;
        r6 = r17;
        r5 = r18;
        goto L_0x0996;
    L_0x106b:
        r2 = move-exception;
        goto L_0x091f;
    L_0x106e:
        r2 = move-exception;
        goto L_0x091f;
    L_0x1071:
        r2 = move-exception;
        goto L_0x091f;
    L_0x1074:
        r2 = move-exception;
        goto L_0x0970;
    L_0x1077:
        r2 = move-exception;
        goto L_0x0907;
    L_0x107a:
        r2 = move-exception;
        goto L_0x0907;
    L_0x107d:
        r2 = move-exception;
        goto L_0x0907;
    L_0x1080:
        r2 = move-exception;
        goto L_0x0907;
    L_0x1083:
        r2 = move-exception;
        goto L_0x0907;
    L_0x1086:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        r5 = r18;
        goto L_0x08e6;
    L_0x1099:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        goto L_0x08e6;
    L_0x10ab:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r14;
        goto L_0x08e6;
    L_0x10bb:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r14;
        r5 = r18;
        goto L_0x08e6;
    L_0x10ce:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r12 = r2;
        r5 = r18;
        goto L_0x08e6;
    L_0x10e1:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r12 = r2;
        goto L_0x08e6;
    L_0x10f4:
        r3 = move-exception;
        r12 = r2;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        goto L_0x08e6;
    L_0x1105:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        goto L_0x08e6;
    L_0x1115:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        goto L_0x08e6;
    L_0x1121:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        goto L_0x08e6;
    L_0x112b:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        goto L_0x08e6;
    L_0x1139:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r5 = r18;
        r4 = r19;
        goto L_0x08e6;
    L_0x1143:
        r4 = move-exception;
        r11 = r3;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r2;
        goto L_0x08e6;
    L_0x1153:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r7 = r15;
        goto L_0x08e6;
    L_0x1163:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r7 = r15;
        goto L_0x08e6;
    L_0x1171:
        r3 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r3 = r20;
        r7 = r15;
        r5 = r18;
        goto L_0x08e6;
    L_0x1182:
        r6 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r15;
        r5 = r18;
        goto L_0x08e6;
    L_0x118f:
        r6 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r4;
        r5 = r18;
        r3 = r20;
        r4 = r19;
        goto L_0x08e6;
    L_0x11a1:
        r9 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r9 = r16;
        r7 = r4;
        r3 = r8;
        r4 = r6;
        r8 = r5;
        r6 = r17;
        r5 = r18;
        goto L_0x08e6;
    L_0x11b1:
        r4 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r4 = r19;
        r5 = r18;
        r7 = r6;
        r3 = r20;
        r6 = r17;
        goto L_0x08e6;
    L_0x11c3:
        r2 = move-exception;
        r11 = r3;
        r12 = r4;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r6;
        r5 = r18;
        r4 = r19;
        r3 = r20;
        r6 = r17;
        goto L_0x08e6;
    L_0x11d5:
        r4 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r9 = r16;
        r4 = r19;
        r7 = r6;
        r3 = r8;
        r8 = r5;
        r6 = r17;
        r5 = r18;
        goto L_0x08e6;
    L_0x11e6:
        r2 = move-exception;
        goto L_0x086f;
    L_0x11e9:
        r2 = move-exception;
        goto L_0x086f;
    L_0x11ec:
        r2 = move-exception;
        goto L_0x086f;
    L_0x11ef:
        r2 = move-exception;
        goto L_0x08c0;
    L_0x11f2:
        r2 = move-exception;
        goto L_0x0857;
    L_0x11f5:
        r2 = move-exception;
        goto L_0x0857;
    L_0x11f8:
        r2 = move-exception;
        goto L_0x0857;
    L_0x11fb:
        r2 = move-exception;
        goto L_0x0857;
    L_0x11fe:
        r2 = move-exception;
        goto L_0x0857;
    L_0x1201:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r14;
        goto L_0x0834;
    L_0x1212:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r14;
        goto L_0x0834;
    L_0x1222:
        r2 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r14;
        goto L_0x0834;
    L_0x1233:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r12 = r2;
        goto L_0x0834;
    L_0x1244:
        r3 = move-exception;
        r22 = r12;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r2;
        goto L_0x0834;
    L_0x1255:
        r3 = move-exception;
        r12 = r2;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        goto L_0x0834;
    L_0x1264:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        goto L_0x0834;
    L_0x1272:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        goto L_0x0834;
    L_0x127e:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        goto L_0x0834;
    L_0x1288:
        r2 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r5 = r18;
        r4 = r19;
        goto L_0x0834;
    L_0x1292:
        r2 = move-exception;
        r11 = r3;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        goto L_0x0834;
    L_0x12a1:
        r2 = move-exception;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r7 = r15;
        goto L_0x0834;
    L_0x12af:
        r3 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r7 = r15;
        goto L_0x0834;
    L_0x12be:
        r3 = move-exception;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r15;
        r5 = r18;
        goto L_0x0834;
    L_0x12cb:
        r6 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r7 = r4;
        r5 = r18;
        r4 = r19;
        goto L_0x0834;
    L_0x12db:
        r8 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r4;
        r5 = r18;
        r4 = r6;
        r6 = r17;
        goto L_0x0834;
    L_0x12ea:
        r4 = move-exception;
        r11 = r3;
        r12 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r4 = r19;
        r5 = r18;
        r7 = r6;
        r6 = r17;
        goto L_0x0834;
    L_0x12fa:
        r2 = move-exception;
        r11 = r3;
        r12 = r4;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r6;
        r5 = r18;
        r4 = r19;
        r6 = r17;
        goto L_0x0834;
    L_0x130a:
        r2 = move-exception;
        goto L_0x0737;
    L_0x130d:
        r2 = move-exception;
        goto L_0x0737;
    L_0x1310:
        r2 = move-exception;
        goto L_0x0737;
    L_0x1313:
        r2 = move-exception;
        goto L_0x080e;
    L_0x1316:
        r2 = move-exception;
        goto L_0x071f;
    L_0x1319:
        r2 = move-exception;
        goto L_0x071f;
    L_0x131c:
        r2 = move-exception;
        goto L_0x071f;
    L_0x131f:
        r2 = move-exception;
        goto L_0x071f;
    L_0x1322:
        r2 = move-exception;
        goto L_0x071f;
    L_0x1325:
        r2 = move-exception;
        r2 = r3;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r13 = r14;
        goto L_0x06fe;
    L_0x1335:
        r2 = move-exception;
        r2 = r21;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r13 = r14;
        goto L_0x06fe;
    L_0x1346:
        r2 = move-exception;
        r2 = r21;
        r10 = r13;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r13 = r14;
        goto L_0x06fe;
    L_0x1356:
        r2 = move-exception;
        r2 = r21;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r13 = r14;
        goto L_0x06fe;
    L_0x1367:
        r3 = move-exception;
        r10 = r13;
        r8 = r5;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r5 = r18;
        r13 = r2;
        r2 = r21;
        goto L_0x06fe;
    L_0x1378:
        r3 = move-exception;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r13 = r2;
        r2 = r21;
        goto L_0x06fe;
    L_0x1389:
        r3 = move-exception;
        r12 = r22;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r13 = r2;
        r2 = r21;
        goto L_0x06fe;
    L_0x139c:
        r2 = move-exception;
        r2 = r21;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r13 = r12;
        r12 = r22;
        goto L_0x06fe;
    L_0x13af:
        r2 = move-exception;
        r2 = r21;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r13 = r12;
        r12 = r22;
        goto L_0x06fe;
    L_0x13c0:
        r2 = move-exception;
        r2 = r21;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r13 = r12;
        r12 = r22;
        goto L_0x06fe;
    L_0x13cf:
        r2 = move-exception;
        r2 = r21;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r5 = r18;
        r4 = r19;
        r13 = r12;
        r12 = r22;
        goto L_0x06fe;
    L_0x13de:
        r2 = move-exception;
        r11 = r3;
        r2 = r21;
        r10 = r13;
        r8 = r14;
        r7 = r15;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r13 = r12;
        r12 = r22;
        goto L_0x06fe;
    L_0x13f2:
        r2 = move-exception;
        r2 = r21;
        r13 = r12;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r22;
        r7 = r15;
        goto L_0x06fe;
    L_0x1405:
        r2 = move-exception;
        r2 = r21;
        r13 = r12;
        r10 = r7;
        r8 = r14;
        r9 = r16;
        r6 = r17;
        r5 = r18;
        r4 = r19;
        r12 = r22;
        r7 = r15;
        goto L_0x06fe;
    L_0x1418:
        r3 = move-exception;
        r12 = r22;
        r13 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r4 = r19;
        r7 = r15;
        r5 = r18;
        r2 = r21;
        goto L_0x06fe;
    L_0x142b:
        r3 = move-exception;
        r12 = r22;
        r13 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r2 = r21;
        r7 = r15;
        r5 = r18;
        goto L_0x06fe;
    L_0x143c:
        r6 = move-exception;
        r11 = r3;
        r12 = r22;
        r13 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r6 = r17;
        r2 = r21;
        r7 = r4;
        r5 = r18;
        r4 = r19;
        goto L_0x06fe;
    L_0x1450:
        r8 = move-exception;
        r11 = r3;
        r12 = r22;
        r13 = r2;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r4;
        r2 = r21;
        r5 = r18;
        r4 = r6;
        r6 = r17;
        goto L_0x06fe;
    L_0x1463:
        r2 = move-exception;
        r11 = r3;
        r2 = r21;
        r12 = r22;
        r13 = r4;
        r10 = r7;
        r8 = r5;
        r9 = r16;
        r7 = r6;
        r5 = r18;
        r4 = r19;
        r6 = r17;
        goto L_0x06fe;
    L_0x1477:
        r2 = move-exception;
        goto L_0x07a1;
    L_0x147a:
        r12 = move-exception;
        goto L_0x029a;
    L_0x147d:
        r12 = move-exception;
        goto L_0x029a;
    L_0x1480:
        r12 = move-exception;
        goto L_0x029a;
    L_0x1483:
        r12 = move-exception;
        goto L_0x029a;
    L_0x1486:
        r12 = move-exception;
        goto L_0x029a;
    L_0x1489:
        r8 = move-exception;
        r32 = r8;
        r8 = r4;
        r4 = r32;
        goto L_0x06ea;
    L_0x1491:
        r4 = move-exception;
        goto L_0x06ea;
    L_0x1494:
        r2 = move-exception;
        goto L_0x00c4;
    L_0x1497:
        r2 = move-exception;
        goto L_0x00c4;
    L_0x149a:
        r2 = move-exception;
        goto L_0x00c4;
    L_0x149d:
        r2 = move-exception;
        goto L_0x011e;
    L_0x14a0:
        r2 = move-exception;
        goto L_0x00ae;
    L_0x14a3:
        r2 = move-exception;
        goto L_0x00ae;
    L_0x14a6:
        r2 = move-exception;
        goto L_0x00ae;
    L_0x14a9:
        r2 = move-exception;
        goto L_0x00ae;
    L_0x14ac:
        r2 = move-exception;
        goto L_0x00ae;
    L_0x14af:
        r8 = r11;
        goto L_0x02b3;
    L_0x14b2:
        r8 = r10;
        r4 = r13;
        goto L_0x0670;
    L_0x14b6:
        r2 = r3;
        goto L_0x06a3;
    L_0x14b9:
        r13 = r4;
        goto L_0x045f;
    L_0x14bc:
        r2 = r9;
        goto L_0x0450;
    L_0x14bf:
        r26 = r8;
        goto L_0x0444;
    L_0x14c3:
        r7 = r13;
        goto L_0x0436;
    L_0x14c6:
        r5 = r7;
        goto L_0x0415;
    L_0x14c9:
        r2 = r12;
        r7 = r13;
        r5 = r14;
        r14 = r4;
        goto L_0x0501;
    L_0x14cf:
        r12 = r2;
        goto L_0x01ba;
    L_0x14d2:
        r22 = r12;
        goto L_0x01ab;
    L_0x14d6:
        r4 = r10;
        goto L_0x0168;
    L_0x14d9:
        r5 = r8;
        goto L_0x014a;
    L_0x14dc:
        r2 = r14;
        r14 = r8;
        r8 = r4;
        r4 = r10;
        goto L_0x0170;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.download.g.run():void");
    }
}
