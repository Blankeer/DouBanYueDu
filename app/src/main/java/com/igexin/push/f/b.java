package com.igexin.push.f;

import android.content.Intent;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.a.a.a.a;
import com.igexin.push.config.k;
import com.igexin.push.core.bean.e;
import com.igexin.push.core.d.h;
import com.igexin.push.core.g;
import com.igexin.sdk.PushService;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class b {
    private static final String a;

    static {
        a = k.a + "_FileUtils";
    }

    public static void a() {
        Throwable th;
        FileOutputStream fileOutputStream = null;
        FileOutputStream fileOutputStream2;
        try {
            File file = new File(g.Y);
            if (file.exists() || file.createNewFile()) {
                fileOutputStream2 = new FileOutputStream(g.Y);
                try {
                    fileOutputStream2.write(a.b((("v01" + g.x) + String.valueOf(g.r) + "|" + g.a + "|" + g.s).getBytes(), g.B));
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    }
                    return;
                } catch (Exception e2) {
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Exception e3) {
                            return;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (Exception e4) {
                        }
                    }
                    throw th;
                }
            }
            com.igexin.a.a.c.a.b(a + "create file : " + file.toString() + " failed !!!");
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e5) {
                }
            }
        } catch (Exception e6) {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (Throwable th3) {
            Throwable th4 = th3;
            fileOutputStream2 = fileOutputStream;
            th = th4;
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
            throw th;
        }
    }

    public static void a(e eVar) {
        File file = new File(g.ac + "/" + eVar.c());
        File file2 = new File(g.ad + "/" + eVar.c());
        if (file2.exists()) {
            if (com.igexin.a.b.a.a(g.g, file2.getAbsolutePath()).equals(eVar.f())) {
                com.igexin.a.a.c.a.b(a + " downloadExtension ext is exsit " + file2);
                Intent intent = new Intent(g.g, PushService.class);
                intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "com.igexin.sdk.action.extdownloadsuccess");
                intent.putExtra(WorksListUri.KEY_ID, eVar.a());
                intent.putExtra("result", true);
                g.g.startService(intent);
                return;
            }
            file2.delete();
        }
        if (file.exists() && com.igexin.a.b.a.a(g.g, file.getAbsolutePath()).equals(eVar.f())) {
            com.igexin.a.a.c.a.b(a + " download Extension ext is exsit do copy " + file);
            if (a(file, file2, eVar.f())) {
                intent = new Intent(g.g, PushService.class);
                intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "com.igexin.sdk.action.extdownloadsuccess");
                intent.putExtra(WorksListUri.KEY_ID, eVar.a());
                intent.putExtra("result", true);
                g.g.startService(intent);
                return;
            }
        }
        com.igexin.a.a.c.a.b(a + " ext is not exsit start download name = " + eVar.c());
        new Thread(new h(g.g, eVar, false)).start();
    }

    public static boolean a(File file, File file2, String str) {
        BufferedOutputStream bufferedOutputStream;
        Exception e;
        FileInputStream fileInputStream;
        Throwable th;
        FileOutputStream fileOutputStream = null;
        boolean z = false;
        FileInputStream fileInputStream2;
        FileOutputStream fileOutputStream2;
        try {
            fileInputStream2 = new FileInputStream(file);
            try {
                fileOutputStream2 = new FileOutputStream(file2);
                try {
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream2);
                } catch (Exception e2) {
                    e = e2;
                    bufferedOutputStream = null;
                    fileOutputStream = fileOutputStream2;
                    fileInputStream = fileInputStream2;
                    try {
                        com.igexin.a.a.c.a.b(a + e.toString());
                        if (file2.exists()) {
                            file2.delete();
                        }
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Exception e3) {
                            }
                        }
                        if (bufferedOutputStream != null) {
                            bufferedOutputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        return z;
                    } catch (Throwable th2) {
                        th = th2;
                        fileInputStream2 = fileInputStream;
                        fileOutputStream2 = fileOutputStream;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (Exception e4) {
                                throw th;
                            }
                        }
                        if (bufferedOutputStream != null) {
                            bufferedOutputStream.close();
                        }
                        if (fileOutputStream2 != null) {
                            fileOutputStream2.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    bufferedOutputStream = null;
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();
                    }
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                    throw th;
                }
            } catch (Exception e5) {
                e = e5;
                bufferedOutputStream = null;
                fileInputStream = fileInputStream2;
                com.igexin.a.a.c.a.b(a + e.toString());
                if (file2.exists()) {
                    file2.delete();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return z;
            } catch (Throwable th4) {
                th = th4;
                bufferedOutputStream = null;
                fileOutputStream2 = null;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw th;
            }
            try {
                Object obj = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                while (true) {
                    int read = fileInputStream2.read(obj);
                    if (read == -1) {
                        break;
                    }
                    Object obj2 = new byte[read];
                    System.arraycopy(obj, 0, obj2, 0, read);
                    bufferedOutputStream.write(obj2);
                }
                fileInputStream2.close();
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                fileOutputStream2.close();
                if (com.igexin.a.b.a.a(g.g, file2.getAbsolutePath()).equals(str)) {
                    com.igexin.a.a.c.a.b(a + " copyFile success from : " + file + " to : " + file2);
                    z = true;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Exception e6) {
                        }
                    }
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();
                    }
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                } else {
                    file2.delete();
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Exception e7) {
                        }
                    }
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();
                    }
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                }
            } catch (Exception e8) {
                e = e8;
                fileOutputStream = fileOutputStream2;
                fileInputStream = fileInputStream2;
                com.igexin.a.a.c.a.b(a + e.toString());
                if (file2.exists()) {
                    file2.delete();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return z;
            } catch (Throwable th5) {
                th = th5;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw th;
            }
        } catch (Exception e9) {
            e = e9;
            bufferedOutputStream = null;
            fileInputStream = null;
            com.igexin.a.a.c.a.b(a + e.toString());
            if (file2.exists()) {
                file2.delete();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            return z;
        } catch (Throwable th6) {
            th = th6;
            bufferedOutputStream = null;
            fileOutputStream2 = null;
            fileInputStream2 = null;
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
            throw th;
        }
        return z;
    }

    public static boolean a(String str) {
        try {
            File file = new File(g.ad + "/" + str);
            if (file.exists()) {
                file.delete();
            }
            file = new File(g.ad + "/" + str + ".dex");
            if (file.exists()) {
                file.delete();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String b() {
        String str = null;
        try {
            byte[] b = b(g.Y);
            if (b == null) {
                com.igexin.a.a.c.a.b(a + " read file cid id = null");
                return str;
            }
            String str2 = new String(a.a(b, g.B));
            if (str2 != null) {
                String[] split = str2.split("\\|");
                if (split != null && split.length > 2) {
                    String str3 = split[2];
                    if (str3 != null) {
                        try {
                            if (!str3.equals("null")) {
                                str = str3;
                            }
                        } catch (Exception e) {
                            str = str3;
                        }
                    } else {
                        str = str3;
                    }
                }
            }
            com.igexin.a.a.c.a.b(a + " get cid from file cid = " + str);
            return str;
        } catch (Exception e2) {
        }
    }

    private static byte[] b(String str) {
        FileInputStream fileInputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        Throwable th;
        byte[] bArr = null;
        if (new File(str).exists()) {
            byte[] bArr2 = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            try {
                fileInputStream = new FileInputStream(str);
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    while (true) {
                        try {
                            int read = fileInputStream.read(bArr2);
                            if (read == -1) {
                                break;
                            }
                            byteArrayOutputStream.write(bArr2, 0, read);
                        } catch (Exception e) {
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                    bArr = byteArrayOutputStream.toByteArray();
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e2) {
                        }
                    }
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Exception e3) {
                        }
                    }
                } catch (Exception e4) {
                    byteArrayOutputStream = bArr;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e5) {
                        }
                    }
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Exception e6) {
                        }
                    }
                    return bArr;
                } catch (Throwable th3) {
                    Throwable th4 = th3;
                    byteArrayOutputStream = bArr;
                    th = th4;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e7) {
                        }
                    }
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Exception e8) {
                        }
                    }
                    throw th;
                }
            } catch (Exception e9) {
                byteArrayOutputStream = bArr;
                fileInputStream = bArr;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                return bArr;
            } catch (Throwable th32) {
                fileInputStream = bArr;
                byte[] bArr3 = bArr;
                th = th32;
                byteArrayOutputStream = bArr3;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                throw th;
            }
        }
        return bArr;
    }

    public static String c() {
        Exception e;
        String str;
        try {
            com.igexin.a.a.c.a.b(a + " get device id from file : " + g.Z);
            byte[] b = b(g.Z);
            if (b == null) {
                com.igexin.a.a.c.a.b(a + " read file device id = null");
                return null;
            }
            str = new String(b, "utf-8");
            try {
                com.igexin.a.a.c.a.b(a + " read file device id = " + str);
                return str;
            } catch (Exception e2) {
                e = e2;
                com.igexin.a.a.c.a.b(a + " get device id from file : " + e.toString());
                return str;
            }
        } catch (Exception e3) {
            Exception exception = e3;
            str = null;
            e = exception;
            com.igexin.a.a.c.a.b(a + " get device id from file : " + e.toString());
            return str;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long d() {
        /*
        r2 = 0;
        r0 = com.igexin.push.core.g.Y;	 Catch:{ Exception -> 0x0079 }
        r0 = b(r0);	 Catch:{ Exception -> 0x0079 }
        if (r0 != 0) goto L_0x0023;
    L_0x000a:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0079 }
        r0.<init>();	 Catch:{ Exception -> 0x0079 }
        r1 = a;	 Catch:{ Exception -> 0x0079 }
        r0 = r0.append(r1);	 Catch:{ Exception -> 0x0079 }
        r1 = " read session from file, not exist";
        r0 = r0.append(r1);	 Catch:{ Exception -> 0x0079 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0079 }
        com.igexin.a.a.c.a.b(r0);	 Catch:{ Exception -> 0x0079 }
    L_0x0022:
        return r2;
    L_0x0023:
        r1 = new java.lang.String;	 Catch:{ Exception -> 0x0079 }
        r4 = com.igexin.push.core.g.B;	 Catch:{ Exception -> 0x0079 }
        r0 = com.igexin.a.a.a.a.a(r0, r4);	 Catch:{ Exception -> 0x0079 }
        r1.<init>(r0);	 Catch:{ Exception -> 0x0079 }
        if (r1 == 0) goto L_0x007c;
    L_0x0030:
        r0 = "null";
        r0 = r1.indexOf(r0);	 Catch:{ Exception -> 0x0079 }
        if (r0 < 0) goto L_0x0072;
    L_0x0038:
        r0 = 7;
        r0 = r1.substring(r0);	 Catch:{ Exception -> 0x0079 }
    L_0x003d:
        if (r0 == 0) goto L_0x007c;
    L_0x003f:
        r1 = "|";
        r1 = r0.indexOf(r1);	 Catch:{ Exception -> 0x0079 }
        if (r1 < 0) goto L_0x004c;
    L_0x0047:
        r4 = 0;
        r0 = r0.substring(r4, r1);	 Catch:{ Exception -> 0x0079 }
    L_0x004c:
        r0 = java.lang.Long.parseLong(r0);	 Catch:{ Exception -> 0x0079 }
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x007c;
    L_0x0054:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = a;
        r2 = r2.append(r3);
        r3 = " session : ";
        r2 = r2.append(r3);
        r2 = r2.append(r0);
        r2 = r2.toString();
        com.igexin.a.a.c.a.b(r2);
        r2 = r0;
        goto L_0x0022;
    L_0x0072:
        r0 = 20;
        r0 = r1.substring(r0);	 Catch:{ Exception -> 0x0079 }
        goto L_0x003d;
    L_0x0079:
        r0 = move-exception;
        r0 = r2;
        goto L_0x0054;
    L_0x007c:
        r0 = r2;
        goto L_0x0054;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.push.f.b.d():long");
    }

    public static void e() {
        Throwable th;
        if (g.z != null) {
            com.igexin.a.a.c.a.b(a + " save device id to file : " + g.Z);
            FileOutputStream fileOutputStream = null;
            FileOutputStream fileOutputStream2;
            try {
                File file = new File(g.Z);
                if (!(file.exists() || file.createNewFile())) {
                    com.igexin.a.a.c.a.b(a + "create file : " + file.toString() + " failed !!!");
                }
                fileOutputStream2 = new FileOutputStream(g.Z);
                try {
                    fileOutputStream2.write(g.z.getBytes("utf-8"));
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e2) {
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Exception e3) {
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (Exception e4) {
                        }
                    }
                    throw th;
                }
            } catch (Exception e5) {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Throwable th3) {
                Throwable th4 = th3;
                fileOutputStream2 = null;
                th = th4;
                if (fileOutputStream2 != null) {
                    fileOutputStream2.close();
                }
                throw th;
            }
        }
    }

    public static void f() {
        Throwable th;
        FileOutputStream fileOutputStream = null;
        String str = "/data/data/" + g.e + "/files/" + "init.pid";
        FileOutputStream fileOutputStream2;
        try {
            if (new File(str).exists()) {
                byte[] bytes = g.s.getBytes();
                byte[] bArr = new byte[bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    bArr[i] = (byte) (bytes[i] ^ g.ae[i]);
                }
                fileOutputStream2 = new FileOutputStream(str);
                try {
                    fileOutputStream2.write(bArr);
                } catch (Exception e) {
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Exception e2) {
                            return;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (Exception e3) {
                        }
                    }
                    throw th;
                }
            }
            fileOutputStream2 = null;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (Exception e4) {
                }
            }
        } catch (Exception e5) {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (Throwable th3) {
            Throwable th4 = th3;
            fileOutputStream2 = null;
            th = th4;
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
            throw th;
        }
    }
}
