package com.tencent.open.a;

import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import com.tencent.connect.common.Constants;
import com.tencent.open.a.d.e;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;

/* compiled from: ProGuard */
public class b {
    private static SimpleDateFormat a;
    private static FileFilter b;
    private String c;
    private int d;
    private int e;
    private int f;
    private long g;
    private File h;
    private int i;
    private String j;
    private long k;
    private FileFilter l;
    private Comparator<? super File> m;

    static {
        a = e.a("yyyy-MM-dd");
        b = new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory() && b.a(file) > 0) {
                    return true;
                }
                return false;
            }
        };
    }

    public static long a(File file) {
        try {
            return a.parse(file.getName()).getTime();
        } catch (Exception e) {
            return -1;
        }
    }

    public b(File file, int i, int i2, int i3, String str, long j, int i4, String str2, long j2) {
        this.c = "Tracer.File";
        this.d = AdvancedShareActionProvider.WEIGHT_MAX;
        this.e = AdvancedShareActionProvider.WEIGHT_MAX;
        this.f = CodedOutputStream.DEFAULT_BUFFER_SIZE;
        this.g = 10000;
        this.i = 10;
        this.j = ".log";
        this.k = Long.MAX_VALUE;
        this.l = new FileFilter() {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public boolean accept(File file) {
                if (file.getName().endsWith(this.a.i()) && b.f(file) != -1) {
                    return true;
                }
                return false;
            }
        };
        this.m = new Comparator<File>() {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public /* synthetic */ int compare(Object obj, Object obj2) {
                return a((File) obj, (File) obj2);
            }

            public int a(File file, File file2) {
                return b.f(file) - b.f(file2);
            }
        };
        c(file);
        b(i);
        a(i2);
        c(i3);
        a(str);
        b(j);
        d(i4);
        b(str2);
        c(j2);
    }

    public File a() {
        return d(System.currentTimeMillis());
    }

    private File d(long j) {
        return e(a(j));
    }

    public File a(long j) {
        File file = new File(g(), a.format(Long.valueOf(j)));
        file.mkdirs();
        return file;
    }

    private File e(File file) {
        File[] b = b(file);
        if (b == null || b.length == 0) {
            return new File(file, Constants.VIA_TO_TYPE_QQ_GROUP + i());
        }
        a(b);
        File file2 = b[b.length - 1];
        int length = b.length - e();
        if (((int) file2.length()) > d()) {
            file2 = new File(file, (f(file2) + 1) + i());
            length++;
        }
        for (int i = 0; i < length; i++) {
            b[i].delete();
        }
        return file2;
    }

    public File[] b(File file) {
        return file.listFiles(this.l);
    }

    public void b() {
        if (g() != null) {
            File[] listFiles = g().listFiles(b);
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (System.currentTimeMillis() - a(file) > j()) {
                        com.tencent.open.a.d.b.a(file);
                    }
                }
            }
        }
    }

    public File[] a(File[] fileArr) {
        Arrays.sort(fileArr, this.m);
        return fileArr;
    }

    private static int f(File file) {
        try {
            String name = file.getName();
            return Integer.parseInt(name.substring(0, name.indexOf(46)));
        } catch (Exception e) {
            return -1;
        }
    }

    public String c() {
        return this.c;
    }

    public void a(String str) {
        this.c = str;
    }

    public int d() {
        return this.d;
    }

    public void a(int i) {
        this.d = i;
    }

    public int e() {
        return this.e;
    }

    public void b(int i) {
        this.e = i;
    }

    public int f() {
        return this.f;
    }

    public void c(int i) {
        this.f = i;
    }

    public void b(long j) {
        this.g = j;
    }

    public File g() {
        return this.h;
    }

    public void c(File file) {
        this.h = file;
    }

    public int h() {
        return this.i;
    }

    public void d(int i) {
        this.i = i;
    }

    public String i() {
        return this.j;
    }

    public void b(String str) {
        this.j = str;
    }

    public long j() {
        return this.k;
    }

    public void c(long j) {
        this.k = j;
    }
}
