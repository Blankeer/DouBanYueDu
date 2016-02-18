package com.alipay.sdk.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.support.v4.media.TransportMediator;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.util.WorksIdentity;
import com.tencent.connect.common.Constants;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import u.aly.dx;

public final class k {
    static final String a = "com.alipay.android.app";
    public static final String b = "com.eg.android.AlipayGphone";
    private static final String c = "7.0.0";

    public static class a {
        public byte[] a;
        public int b;
    }

    public static String a(byte[] bArr) {
        try {
            String obj = ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr))).getPublicKey().toString();
            if (obj.indexOf("modulus") != -1) {
                return obj.substring(obj.indexOf("modulus") + 8, obj.lastIndexOf(",")).trim();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static a a(Context context, String str) {
        for (PackageInfo packageInfo : context.getPackageManager().getInstalledPackages(64)) {
            if (packageInfo.packageName.equals(str)) {
                a aVar = new a();
                aVar.a = packageInfo.signatures[0].toByteArray();
                aVar.b = packageInfo.versionCode;
                return aVar;
            }
        }
        return null;
    }

    public static boolean a(Context context) {
        try {
            if (context.getPackageManager().getPackageInfo(a, TransportMediator.FLAG_KEY_MEDIA_NEXT) == null) {
                return false;
            }
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean b(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(b, TransportMediator.FLAG_KEY_MEDIA_NEXT);
            if (packageInfo == null) {
                return false;
            }
            int parseInt;
            String str = packageInfo.versionName;
            String str2 = c;
            List arrayList = new ArrayList();
            List arrayList2 = new ArrayList();
            arrayList.addAll(Arrays.asList(str.split("\\.")));
            arrayList2.addAll(Arrays.asList(str2.split("\\.")));
            int max = Math.max(arrayList.size(), arrayList2.size());
            while (arrayList.size() < max) {
                arrayList.add(Constants.VIA_RESULT_SUCCESS);
            }
            while (arrayList2.size() < max) {
                arrayList2.add(Constants.VIA_RESULT_SUCCESS);
            }
            for (int i = 0; i < max; i++) {
                if (Integer.parseInt((String) arrayList.get(i)) != Integer.parseInt((String) arrayList2.get(i))) {
                    parseInt = Integer.parseInt((String) arrayList.get(i)) - Integer.parseInt((String) arrayList2.get(i));
                    break;
                }
            }
            parseInt = 0;
            if (parseInt < 0) {
                return false;
            }
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static String c(Context context) {
        String a = a();
        String b = b();
        String d = d(context);
        return " (" + a + ";" + b + ";" + d + ";;" + e(context) + ")(sdk android)";
    }

    public static String a() {
        return "Android " + VERSION.RELEASE;
    }

    public static String b() {
        String c = c();
        int indexOf = c.indexOf("-");
        if (indexOf != -1) {
            c = c.substring(0, indexOf);
        }
        indexOf = c.indexOf("\n");
        if (indexOf != -1) {
            c = c.substring(0, indexOf);
        }
        return "Linux " + c;
    }

    private static String c() {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/version"), WorksIdentity.ID_BIT_FINALIZE);
            CharSequence readLine = bufferedReader.readLine();
            bufferedReader.close();
            Matcher matcher = Pattern.compile("\\w+\\s+\\w+\\s+([^\\s]+)\\s+\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+([^\\s]+)\\s+(?:PREEMPT\\s+)?(.+)").matcher(readLine);
            if (!matcher.matches()) {
                return "Unavailable";
            }
            if (matcher.groupCount() < 4) {
                return "Unavailable";
            }
            return new StringBuilder(matcher.group(1)).append("\n").append(matcher.group(2)).append(" ").append(matcher.group(3)).append("\n").append(matcher.group(4)).toString();
        } catch (IOException e) {
            return "Unavailable";
        } catch (Throwable th) {
            bufferedReader.close();
        }
    }

    public static String d(Context context) {
        return context.getResources().getConfiguration().locale.toString();
    }

    public static String e(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(displayMetrics.widthPixels);
        stringBuilder.append("*");
        stringBuilder.append(displayMetrics.heightPixels);
        return stringBuilder.toString();
    }

    private static DisplayMetrics h(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    private static String d() {
        String str = com.alipay.sdk.cons.a.b;
        return str.substring(0, str.indexOf("://"));
    }

    private static String e() {
        return "-1;-1";
    }

    private static String f() {
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

    private static int a(String str, String str2) {
        List arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        arrayList.addAll(Arrays.asList(str.split("\\.")));
        arrayList2.addAll(Arrays.asList(str2.split("\\.")));
        int max = Math.max(arrayList.size(), arrayList2.size());
        while (arrayList.size() < max) {
            arrayList.add(Constants.VIA_RESULT_SUCCESS);
        }
        while (arrayList2.size() < max) {
            arrayList2.add(Constants.VIA_RESULT_SUCCESS);
        }
        for (int i = 0; i < max; i++) {
            if (Integer.parseInt((String) arrayList.get(i)) != Integer.parseInt((String) arrayList2.get(i))) {
                return Integer.parseInt((String) arrayList.get(i)) - Integer.parseInt((String) arrayList2.get(i));
            }
        }
        return 0;
    }

    public static boolean a(String str) {
        return Pattern.compile("^http(s)?://([a-z0-9_\\-]+\\.)*(alipay|taobao)\\.(com|net)(:\\d+)?(/.*)?$").matcher(str).matches();
    }

    public static String f(Context context) {
        try {
            for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
                if (runningAppProcessInfo.processName.contains(b) && !runningAppProcessInfo.processName.contains("push")) {
                    return runningAppProcessInfo.processName;
                }
            }
            return null;
        } catch (Throwable th) {
            return StatConstant.STAT_EVENT_ID_ERROR;
        }
    }

    public static String g(Context context) {
        List installedPackages = context.getPackageManager().getInstalledPackages(0);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = (PackageInfo) installedPackages.get(i);
            int i2 = packageInfo.applicationInfo.flags;
            if ((i2 & 1) == 0 && (i2 & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 0) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            if (i2 != 0) {
                if (packageInfo.packageName.equals(b)) {
                    stringBuilder.append(packageInfo.packageName).append(packageInfo.versionCode).append("-");
                } else if (!packageInfo.packageName.contains(Key.SETTING_THEME)) {
                    stringBuilder.append(packageInfo.packageName).append("-");
                }
            }
        }
        String stringBuilder2 = stringBuilder.toString();
        if (stringBuilder2 == null || stringBuilder2.length() <= 0) {
            return null;
        }
        return stringBuilder2.substring(0, stringBuilder2.length() - 1);
    }

    private static boolean a(PackageInfo packageInfo) {
        int i = packageInfo.applicationInfo.flags;
        return (i & 1) == 0 && (i & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 0;
    }
}
