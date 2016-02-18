package com.igexin.push.a.a;

public class d implements com.igexin.push.e.b.d {
    private long a;

    public d() {
        this.a = 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a() {
        /*
        r15 = this;
        r6 = 0;
        r12 = 0;
        r10 = 0;
        r8 = 1800000; // 0x1b7740 float:2.522337E-39 double:8.89318E-318;
        r0 = com.igexin.push.core.g.m;	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        if (r0 == 0) goto L_0x000f;
    L_0x000c:
        r12 = 1800000; // 0x1b7740 float:2.522337E-39 double:8.89318E-318;
    L_0x000f:
        r0 = com.igexin.push.core.g.i;	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        if (r0 == 0) goto L_0x0016;
    L_0x0013:
        r10 = 1800000; // 0x1b7740 float:2.522337E-39 double:8.89318E-318;
    L_0x0016:
        r0 = new java.text.SimpleDateFormat;	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r1 = "yyyy-MM-dd";
        r0.<init>(r1);	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r1 = new java.util.Date;	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r1.<init>();	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r7 = r0.format(r1);	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r0 = com.igexin.push.core.f.a();	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r0 = r0.k();	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r1 = "bi";
        r2 = 1;
        r2 = new java.lang.String[r2];	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r3 = 0;
        r4 = "type";
        r2[r3] = r4;	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r3 = 1;
        r3 = new java.lang.String[r3];	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r4 = 0;
        r5 = "1";
        r3[r4] = r5;	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        r4 = 0;
        r5 = 0;
        r6 = r0.a(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x01e6, all -> 0x01df }
        if (r6 == 0) goto L_0x0093;
    L_0x0048:
        r0 = r6.getCount();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        if (r0 != 0) goto L_0x01e9;
    L_0x004e:
        r0 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r0.<init>();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r2 = 0;
        r1 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1));
        if (r1 == 0) goto L_0x0062;
    L_0x0059:
        r1 = "online_time";
        r2 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r0.put(r1, r2);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
    L_0x0062:
        r2 = 0;
        r1 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1));
        if (r1 == 0) goto L_0x0071;
    L_0x0068:
        r1 = "network_time";
        r2 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r0.put(r1, r2);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
    L_0x0071:
        r1 = "running_time";
        r2 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r0.put(r1, r2);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r1 = "create_time";
        r0.put(r1, r7);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r1 = "type";
        r2 = "1";
        r0.put(r1, r2);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r1 = com.igexin.push.core.f.a();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r1 = r1.k();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r2 = "bi";
        r1.a(r2, r0);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
    L_0x0093:
        if (r6 == 0) goto L_0x0098;
    L_0x0095:
        r6.close();
    L_0x0098:
        r0 = java.lang.System.currentTimeMillis();
        r2 = com.igexin.push.core.g.L;
        r0 = r0 - r2;
        r2 = 86400000; // 0x5265c00 float:7.82218E-36 double:4.2687272E-316;
        r0 = r0 - r2;
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x00da;
    L_0x00a9:
        r0 = com.igexin.push.core.a.e.a();
        r1 = 0;
        r2 = 0;
        r0 = r0.a(r1, r2);
        if (r0 == 0) goto L_0x00da;
    L_0x00b5:
        r1 = "";
        r1 = r0.equals(r1);
        if (r1 != 0) goto L_0x00da;
    L_0x00bd:
        r0 = r0.getBytes();
        r1 = new com.igexin.push.e.a.c;
        r2 = new com.igexin.push.core.d.j;
        r3 = com.igexin.push.config.SDKUrlConfig.getBiUploadServiceUrl();
        r4 = 0;
        r5 = 1;
        r2.<init>(r3, r0, r4, r5);
        r1.<init>(r2);
        r0 = com.igexin.a.a.b.d.c();
        r2 = 0;
        r3 = 1;
        r0.a(r1, r2, r3);
    L_0x00da:
        return;
    L_0x00db:
        r8 = r6.moveToNext();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        if (r8 == 0) goto L_0x0093;
    L_0x00e1:
        r8 = "create_time";
        r8 = r6.getColumnIndexOrThrow(r8);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8 = r6.getString(r8);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r9 = "id";
        r9 = r6.getColumnIndexOrThrow(r9);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r9 = r6.getString(r9);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8 = r7.equals(r8);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        if (r8 == 0) goto L_0x0171;
    L_0x00fb:
        r8 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.<init>();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = 0;
        r10 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r10 == 0) goto L_0x011b;
    L_0x0106:
        r10 = "online_time";
        r10 = r6.getColumnIndexOrThrow(r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = r6.getInt(r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = (long) r10;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r4 = r4 + r10;
        r10 = "online_time";
        r11 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.put(r10, r11);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
    L_0x011b:
        r10 = 0;
        r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r10 == 0) goto L_0x0136;
    L_0x0121:
        r10 = "network_time";
        r10 = r6.getColumnIndexOrThrow(r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = r6.getInt(r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = (long) r10;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r2 = r2 + r10;
        r10 = "network_time";
        r11 = java.lang.Long.valueOf(r2);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.put(r10, r11);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
    L_0x0136:
        r10 = "running_time";
        r10 = r6.getColumnIndexOrThrow(r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = r6.getInt(r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = (long) r10;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r0 = r0 + r10;
        r10 = "running_time";
        r11 = java.lang.Long.valueOf(r0);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.put(r10, r11);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = com.igexin.push.core.f.a();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = r10.k();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r11 = "bi";
        r12 = 1;
        r12 = new java.lang.String[r12];	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r13 = 0;
        r14 = "id";
        r12[r13] = r14;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r13 = 1;
        r13 = new java.lang.String[r13];	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r14 = 0;
        r13[r14] = r9;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10.a(r11, r8, r12, r13);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        goto L_0x00db;
    L_0x0168:
        r0 = move-exception;
        r0 = r6;
    L_0x016a:
        if (r0 == 0) goto L_0x0098;
    L_0x016c:
        r0.close();
        goto L_0x0098;
    L_0x0171:
        r8 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.<init>();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = "type";
        r11 = "2";
        r8.put(r10, r11);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = com.igexin.push.core.f.a();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = r10.k();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r11 = "bi";
        r12 = 1;
        r12 = new java.lang.String[r12];	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r13 = 0;
        r14 = "id";
        r12[r13] = r14;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r13 = 1;
        r13 = new java.lang.String[r13];	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r14 = 0;
        r13[r14] = r9;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10.a(r11, r8, r12, r13);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.<init>();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = 0;
        r9 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r9 == 0) goto L_0x01ac;
    L_0x01a3:
        r9 = "online_time";
        r10 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.put(r9, r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
    L_0x01ac:
        r10 = 0;
        r9 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r9 == 0) goto L_0x01bb;
    L_0x01b2:
        r9 = "network_time";
        r10 = java.lang.Long.valueOf(r2);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.put(r9, r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
    L_0x01bb:
        r9 = "running_time";
        r10 = java.lang.Long.valueOf(r0);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r8.put(r9, r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r9 = "create_time";
        r8.put(r9, r7);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r9 = "type";
        r10 = "1";
        r8.put(r9, r10);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r9 = com.igexin.push.core.f.a();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r9 = r9.k();	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        r10 = "bi";
        r9.a(r10, r8);	 Catch:{ Exception -> 0x0168, all -> 0x01df }
        goto L_0x00db;
    L_0x01df:
        r0 = move-exception;
        if (r6 == 0) goto L_0x01e5;
    L_0x01e2:
        r6.close();
    L_0x01e5:
        throw r0;
    L_0x01e6:
        r0 = move-exception;
        r0 = r6;
        goto L_0x016a;
    L_0x01e9:
        r0 = r8;
        r2 = r10;
        r4 = r12;
        goto L_0x00db;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.push.a.a.d.a():void");
    }

    public void a(long j) {
        this.a = j;
    }

    public boolean b() {
        return System.currentTimeMillis() - this.a > 1800000;
    }
}
