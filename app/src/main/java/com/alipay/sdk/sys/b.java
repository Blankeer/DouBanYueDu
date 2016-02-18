package com.alipay.sdk.sys;

import android.content.Context;
import android.text.TextUtils;
import com.alipay.sdk.data.d;
import com.ta.utdid2.device.UTDevice;
import java.io.File;
import java.util.Random;
import u.aly.dx;

public final class b {
    private static b b;
    public Context a;
    private d c;

    private b() {
    }

    public static b a() {
        if (b == null) {
            b = new b();
        }
        return b;
    }

    private Context d() {
        return this.a;
    }

    public final void a(Context context, d dVar) {
        this.a = context.getApplicationContext();
        this.c = dVar;
    }

    public final d b() {
        if (this.c != null) {
            return this.c;
        }
        return d.a();
    }

    public static boolean c() {
        String[] strArr = new String[]{"/system/xbin/", "/system/bin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        int i = 0;
        while (i < 5) {
            try {
                if (new File(strArr[i] + "su").exists()) {
                    String a = a(new String[]{"ls", "-l", strArr[i] + "su"});
                    if (TextUtils.isEmpty(a) || a.indexOf("root") == a.lastIndexOf("root")) {
                        return false;
                    }
                    return true;
                }
                i++;
            } catch (Exception e) {
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String a(java.lang.String[] r6) {
        /*
        r2 = "";
        r0 = 0;
        r1 = new java.lang.ProcessBuilder;	 Catch:{ Exception -> 0x0035, all -> 0x003e }
        r1.<init>(r6);	 Catch:{ Exception -> 0x0035, all -> 0x003e }
        r3 = 0;
        r1.redirectErrorStream(r3);	 Catch:{ Exception -> 0x0035, all -> 0x003e }
        r1 = r1.start();	 Catch:{ Exception -> 0x0035, all -> 0x003e }
        r3 = new java.io.DataOutputStream;	 Catch:{ Exception -> 0x004c, all -> 0x004a }
        r0 = r1.getOutputStream();	 Catch:{ Exception -> 0x004c, all -> 0x004a }
        r3.<init>(r0);	 Catch:{ Exception -> 0x004c, all -> 0x004a }
        r0 = new java.io.DataInputStream;	 Catch:{ Exception -> 0x004c, all -> 0x004a }
        r4 = r1.getInputStream();	 Catch:{ Exception -> 0x004c, all -> 0x004a }
        r0.<init>(r4);	 Catch:{ Exception -> 0x004c, all -> 0x004a }
        r0 = r0.readLine();	 Catch:{ Exception -> 0x004c, all -> 0x004a }
        r2 = "exit\n";
        r3.writeBytes(r2);	 Catch:{ Exception -> 0x004f, all -> 0x004a }
        r3.flush();	 Catch:{ Exception -> 0x004f, all -> 0x004a }
        r1.waitFor();	 Catch:{ Exception -> 0x004f, all -> 0x004a }
        r1.destroy();	 Catch:{ Exception -> 0x0046 }
    L_0x0034:
        return r0;
    L_0x0035:
        r1 = move-exception;
        r1 = r0;
        r0 = r2;
    L_0x0038:
        r1.destroy();	 Catch:{ Exception -> 0x003c }
        goto L_0x0034;
    L_0x003c:
        r1 = move-exception;
        goto L_0x0034;
    L_0x003e:
        r1 = move-exception;
        r5 = r1;
        r1 = r0;
        r0 = r5;
    L_0x0042:
        r1.destroy();	 Catch:{ Exception -> 0x0048 }
    L_0x0045:
        throw r0;
    L_0x0046:
        r1 = move-exception;
        goto L_0x0034;
    L_0x0048:
        r1 = move-exception;
        goto L_0x0045;
    L_0x004a:
        r0 = move-exception;
        goto L_0x0042;
    L_0x004c:
        r0 = move-exception;
        r0 = r2;
        goto L_0x0038;
    L_0x004f:
        r2 = move-exception;
        goto L_0x0038;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alipay.sdk.sys.b.a(java.lang.String[]):java.lang.String");
    }

    private static String e() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            switch (random.nextInt(3)) {
                case dx.a /*0*/:
                    stringBuilder.append(String.valueOf((char) ((int) Math.round((Math.random() * 25.0d) + 65.0d))));
                    break;
                case dx.b /*1*/:
                    stringBuilder.append(String.valueOf((char) ((int) Math.round((Math.random() * 25.0d) + 97.0d))));
                    break;
                case dx.c /*2*/:
                    stringBuilder.append(String.valueOf(new Random().nextInt(10)));
                    break;
                default:
                    break;
            }
        }
        return stringBuilder.toString();
    }

    private String f() {
        return UTDevice.getUtdid(this.a);
    }
}
