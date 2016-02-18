package com.alipay.security.mobile.module.localstorage;

import android.os.Environment;
import com.alipay.security.mobile.module.localstorage.util.a;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class b {
    public static String a(String str) {
        try {
            if (a()) {
                File file = new File(Environment.getExternalStorageDirectory(), str);
                if (file.exists()) {
                    return a.b(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static void a(String str, String str2) {
        FileWriter fileWriter;
        Throwable th;
        try {
            if (a()) {
                File file = new File(Environment.getExternalStorageDirectory(), str);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                FileWriter fileWriter2 = null;
                try {
                    fileWriter = new FileWriter(new File(file.getAbsolutePath()), false);
                    try {
                        fileWriter.write(str2);
                        try {
                            fileWriter.close();
                        } catch (IOException e) {
                        }
                    } catch (Exception e2) {
                        if (fileWriter != null) {
                            try {
                                fileWriter.close();
                            } catch (IOException e3) {
                            }
                        }
                    } catch (Throwable th2) {
                        Throwable th3 = th2;
                        fileWriter2 = fileWriter;
                        th = th3;
                        if (fileWriter2 != null) {
                            try {
                                fileWriter2.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                } catch (Exception e5) {
                    fileWriter = null;
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                } catch (Throwable th4) {
                    th = th4;
                    if (fileWriter2 != null) {
                        fileWriter2.close();
                    }
                    throw th;
                }
            }
        } catch (Exception e6) {
        }
    }

    public static boolean a() {
        String externalStorageState = Environment.getExternalStorageState();
        return externalStorageState != null && externalStorageState.length() > 0 && ((externalStorageState.equals("mounted") || externalStorageState.equals("mounted_ro")) && Environment.getExternalStorageDirectory() != null);
    }
}
