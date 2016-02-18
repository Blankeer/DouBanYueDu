package com.alipay.security.mobile.module.commonutils;

import io.realm.internal.Table;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class d {
    private static String a;
    private static String b;
    private static String c;

    static {
        a = Table.STRING_DEFAULT_VALUE;
        b = Table.STRING_DEFAULT_VALUE;
        c = Table.STRING_DEFAULT_VALUE;
    }

    public static synchronized void a(String str) {
        synchronized (d.class) {
            List arrayList = new ArrayList();
            arrayList.add(str);
            a(arrayList);
        }
    }

    public static synchronized void a(String str, String str2, String str3) {
        synchronized (d.class) {
            a = str;
            b = str2;
            c = str3;
        }
    }

    public static synchronized void a(Throwable th) {
        synchronized (d.class) {
            List arrayList = new ArrayList();
            arrayList.add(b(th));
            a(arrayList);
        }
    }

    private static synchronized void a(List<String> list) {
        synchronized (d.class) {
            if (!(a.a(b) || a.a(c))) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(c);
                for (String str : list) {
                    stringBuffer.append(", " + str);
                }
                stringBuffer.append("\n");
                try {
                    File file = new File(a);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File file2 = new File(a, b);
                    if (!file2.exists()) {
                        file2.createNewFile();
                    }
                    FileWriter fileWriter = file2.length() + ((long) stringBuffer.length()) <= 51200 ? new FileWriter(file2, true) : new FileWriter(file2);
                    fileWriter.write(stringBuffer.toString());
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {
                    e.toString();
                }
            }
        }
    }

    public static String b(Throwable th) {
        if (th == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        Writer stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
