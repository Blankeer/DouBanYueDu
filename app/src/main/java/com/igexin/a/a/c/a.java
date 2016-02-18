package com.igexin.a.a.c;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import com.douban.book.reader.constant.Char;
import com.igexin.push.config.p;
import com.igexin.push.core.g;
import io.realm.internal.Table;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

public class a extends Activity implements UncaughtExceptionHandler {
    public static boolean a;

    static {
        a = p.a.equals("debug");
    }

    public static synchronized void a(String str) {
        synchronized (a.class) {
            b(str);
        }
    }

    public static final void a(String str, String str2) {
        if (a) {
            Log.d(str, str2);
        }
    }

    public static synchronized void b(String str) {
        synchronized (a.class) {
            if (a || (g.N && g.O >= System.currentTimeMillis())) {
                c(str, g.e);
            }
        }
    }

    public static final void b(String str, String str2) {
        if (a) {
            Log.i(str, str2);
        }
    }

    public static synchronized void c(String str, String str2) {
        synchronized (a.class) {
            String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (!(str2 == null || Table.STRING_DEFAULT_VALUE.equals(str2))) {
                if (Environment.getExternalStorageState().equals("mounted")) {
                    String str3 = "/sdcard/libs/";
                    File file = new File(str3);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    try {
                        file = new File(str3 + str2 + "." + format + ".log");
                        if (file.exists() || file.createNewFile()) {
                            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                            fileOutputStream.write(((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "|" + str) + Char.CRLF).getBytes());
                            fileOutputStream.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void uncaughtException(Thread thread, Throwable th) {
    }
}
