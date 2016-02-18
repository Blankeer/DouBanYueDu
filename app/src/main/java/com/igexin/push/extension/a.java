package com.igexin.push.extension;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Process;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.igexin.push.config.k;
import com.igexin.push.config.l;
import com.igexin.push.core.bean.e;
import com.igexin.push.core.d.h;
import com.igexin.push.core.g;
import com.igexin.push.extension.stub.IPushExtension;
import com.igexin.push.f.b;
import dalvik.system.DexClassLoader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class a {
    private static String a;
    private static a c;
    private List b;

    static {
        a = k.a;
    }

    private a() {
        this.b = new ArrayList();
    }

    public static a a() {
        if (c == null) {
            c = new a();
        }
        return c;
    }

    public void a(File file, File file2, String str) {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2;
        FileInputStream fileInputStream2;
        Throwable th;
        Throwable th2;
        BufferedOutputStream bufferedOutputStream = null;
        BufferedOutputStream bufferedOutputStream2;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    bufferedOutputStream2 = new BufferedOutputStream(fileOutputStream);
                } catch (Exception e) {
                    bufferedOutputStream2 = null;
                    fileOutputStream2 = fileOutputStream;
                    fileInputStream2 = fileInputStream;
                    try {
                        if (file2.exists()) {
                            file2.delete();
                        }
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (Exception e2) {
                                return;
                            }
                        }
                        if (bufferedOutputStream2 != null) {
                            bufferedOutputStream2.close();
                        }
                        if (fileOutputStream2 == null) {
                            fileOutputStream2.close();
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        fileInputStream = fileInputStream2;
                        fileOutputStream = fileOutputStream2;
                        bufferedOutputStream = bufferedOutputStream2;
                        th2 = th;
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Exception e3) {
                                throw th2;
                            }
                        }
                        if (bufferedOutputStream != null) {
                            bufferedOutputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        throw th2;
                    }
                } catch (Throwable th4) {
                    th2 = th4;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw th2;
                }
            } catch (Exception e4) {
                bufferedOutputStream2 = null;
                fileInputStream2 = fileInputStream;
                if (file2.exists()) {
                    file2.delete();
                }
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (bufferedOutputStream2 != null) {
                    bufferedOutputStream2.close();
                }
                if (fileOutputStream2 == null) {
                    fileOutputStream2.close();
                }
            } catch (Throwable th5) {
                th2 = th5;
                fileOutputStream = null;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th2;
            }
            try {
                Object obj = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                while (true) {
                    int read = fileInputStream.read(obj);
                    if (read == -1) {
                        break;
                    }
                    byte[] bArr = new byte[read];
                    System.arraycopy(obj, 0, bArr, 0, read);
                    bufferedOutputStream2.write(com.igexin.a.a.a.a.a(bArr, str));
                }
                fileInputStream.close();
                bufferedOutputStream2.flush();
                bufferedOutputStream2.close();
                fileOutputStream.close();
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e5) {
                        return;
                    }
                }
                if (bufferedOutputStream2 != null) {
                    bufferedOutputStream2.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e6) {
                fileOutputStream2 = fileOutputStream;
                fileInputStream2 = fileInputStream;
                if (file2.exists()) {
                    file2.delete();
                }
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (bufferedOutputStream2 != null) {
                    bufferedOutputStream2.close();
                }
                if (fileOutputStream2 == null) {
                    fileOutputStream2.close();
                }
            } catch (Throwable th6) {
                th = th6;
                bufferedOutputStream = bufferedOutputStream2;
                th2 = th;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                throw th2;
            }
        } catch (Exception e7) {
            bufferedOutputStream2 = null;
            fileInputStream2 = null;
            if (file2.exists()) {
                file2.delete();
            }
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            if (bufferedOutputStream2 != null) {
                bufferedOutputStream2.close();
            }
            if (fileOutputStream2 == null) {
                fileOutputStream2.close();
            }
        } catch (Throwable th7) {
            th2 = th7;
            fileOutputStream = null;
            fileInputStream = null;
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            throw th2;
        }
    }

    public boolean a(Context context) {
        try {
            if (l.t != null) {
                Map b = l.t.b();
                List<Integer> arrayList = new ArrayList();
                for (Entry entry : b.entrySet()) {
                    int intValue = ((Integer) entry.getKey()).intValue();
                    e eVar = (e) entry.getValue();
                    String str = g.ad + "/" + eVar.c();
                    File file = new File(str);
                    if (file.exists()) {
                        long currentTimeMillis = System.currentTimeMillis();
                        if (eVar.h() == 0 || eVar.i() + eVar.h() >= currentTimeMillis) {
                            if (a(context, str, eVar.d(), eVar.j(), eVar.c()) && eVar.i() != 0) {
                                eVar.b(currentTimeMillis);
                            }
                            if (eVar.g()) {
                                b.a(eVar.c());
                                arrayList.add(Integer.valueOf(intValue));
                            }
                        } else {
                            b.a(eVar.c());
                            arrayList.add(Integer.valueOf(intValue));
                        }
                    } else {
                        File file2 = new File(g.ac + "/" + eVar.c());
                        if (file2.exists() && eVar.f().equals(com.igexin.a.b.a.a(g.g, file2.getAbsolutePath()))) {
                            com.igexin.a.a.c.a.b(a + " public local file match the condition...");
                            if (file.createNewFile()) {
                                if (b.a(file2, file, eVar.f())) {
                                    com.igexin.a.a.c.a.b(a + " restart the service to load ext:" + file.getAbsolutePath());
                                    Process.killProcess(Process.myPid());
                                } else {
                                    com.igexin.a.a.c.a.b(a + " copy " + file2.getAbsolutePath() + " to " + file.getAbsolutePath() + " failed...");
                                    b.a(eVar.c());
                                }
                            }
                        }
                        if (((ConnectivityManager) g.g.getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
                            new Thread(new h(g.g, eVar, true)).start();
                        } else {
                            com.igexin.a.a.c.a.b(a + " init ext not exit don't download because connect state is not wifi");
                        }
                    }
                }
                if (arrayList != null && arrayList.size() > 0) {
                    for (Integer intValue2 : arrayList) {
                        b.remove(Integer.valueOf(intValue2.intValue()));
                    }
                    com.igexin.a.a.c.a.b(a + " save extension info data to db");
                    com.igexin.push.config.a.a().g();
                }
                return true;
            }
            List<String> arrayList2 = new ArrayList();
            arrayList2.add("com.igexin.push.extension.distribution.basic.stub.PushExtension");
            arrayList2.add("com.igexin.push.extension.distribution.gbd.stub.PushExtension");
            arrayList2.add("com.igexin.push.extension.distribution.lbs.stub.PushExtension");
            for (String str2 : arrayList2) {
                try {
                    IPushExtension iPushExtension = (IPushExtension) context.getClassLoader().loadClass(str2).newInstance();
                    iPushExtension.init(g.g);
                    this.b.add(iPushExtension);
                    com.igexin.a.a.c.a.b("[main] extension loaded(mock): " + str2);
                } catch (Exception e) {
                    com.igexin.a.a.c.a.b(a + e.toString());
                }
            }
            return true;
        } catch (Exception e2) {
            com.igexin.a.a.c.a.b(a + e2.toString());
        }
    }

    public boolean a(Context context, String str, String str2, String str3, String str4) {
        Class cls = null;
        File file = new File(str);
        File file2 = new File(str + ".jar");
        File file3 = new File(context.getFilesDir().getAbsolutePath() + "/" + str4 + ".dex");
        a(file, file2, str3);
        if (file2.exists()) {
            try {
                try {
                    cls = new DexClassLoader(file2.getAbsolutePath(), context.getFilesDir().getAbsolutePath(), null, context.getClassLoader()).loadClass(str2);
                } catch (Exception e) {
                }
                file2.delete();
                IPushExtension iPushExtension;
                if (file3.exists()) {
                    if (cls == null) {
                        return false;
                    }
                    iPushExtension = (IPushExtension) cls.newInstance();
                    if (iPushExtension != null) {
                        iPushExtension.init(g.g);
                        this.b.add(iPushExtension);
                        com.igexin.a.a.c.a.b("[main] extension loaded: " + str);
                        return true;
                    }
                } else if (cls == null) {
                    return false;
                } else {
                    iPushExtension = (IPushExtension) cls.newInstance();
                    if (iPushExtension != null) {
                        iPushExtension.init(g.g);
                        this.b.add(iPushExtension);
                        com.igexin.a.a.c.a.b("[main] extension loaded: " + str);
                        return true;
                    }
                }
            } catch (Exception e2) {
                com.igexin.a.a.c.a.b(a + e2.toString());
            } catch (Throwable th) {
                com.igexin.a.a.c.a.b(a + th.toString());
                if (file2.exists()) {
                    file2.delete();
                }
                if (file3.exists()) {
                }
            }
        }
        return false;
    }

    public void b() {
        for (IPushExtension onDestroy : this.b) {
            onDestroy.onDestroy();
        }
    }

    public List c() {
        return this.b;
    }
}
