package com.tencent.wxop.stat.b;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.book.reader.helper.AppUri;
import io.realm.internal.Table;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

final class m {
    static int D() {
        int i = 0;
        try {
            String str = Table.STRING_DEFAULT_VALUE;
            InputStream inputStream = new ProcessBuilder(new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"}).start().getInputStream();
            byte[] bArr = new byte[24];
            while (inputStream.read(bArr) != -1) {
                str = str + new String(bArr);
            }
            inputStream.close();
            str = str.trim();
            if (str.length() > 0) {
                i = Integer.valueOf(str).intValue();
            }
        } catch (Throwable th) {
            l.cT.b(th);
        }
        return i * AppUri.OPEN_URL;
    }

    static int aA() {
        int i = 0;
        try {
            String str = Table.STRING_DEFAULT_VALUE;
            InputStream inputStream = new ProcessBuilder(new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"}).start().getInputStream();
            byte[] bArr = new byte[24];
            while (inputStream.read(bArr) != -1) {
                str = str + new String(bArr);
            }
            inputStream.close();
            str = str.trim();
            if (str.length() > 0) {
                i = Integer.valueOf(str).intValue();
            }
        } catch (Throwable e) {
            l.cT.b(e);
        }
        return i * AppUri.OPEN_URL;
    }

    static String ax() {
        int i = 2;
        String[] strArr = new String[]{Table.STRING_DEFAULT_VALUE, Table.STRING_DEFAULT_VALUE};
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/cpuinfo"), AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD);
            String[] split = bufferedReader.readLine().split("\\s+");
            while (i < split.length) {
                strArr[0] = strArr[0] + split[i] + " ";
                i++;
            }
            bufferedReader.close();
        } catch (IOException e) {
        }
        return strArr[0];
    }

    static int r() {
        try {
            return new File("/sys/devices/system/cpu/").listFiles(new n()).length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
