package com.igexin.push.f;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Process;
import com.igexin.push.config.l;
import com.igexin.push.core.bean.e;
import com.igexin.push.core.bean.f;
import com.igexin.push.core.g;
import com.igexin.sdk.PushBuildConfig;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class a {
    public static void a(f fVar) {
        g.ap = 0;
        g.aq = 0;
        g.as = fVar;
        Map b = fVar.b();
        e eVar;
        if (l.t != null) {
            int intValue;
            Map b2 = l.t.b();
            List<Integer> arrayList = new ArrayList();
            for (Entry entry : b2.entrySet()) {
                intValue = ((Integer) entry.getKey()).intValue();
                eVar = (e) entry.getValue();
                if (!b.containsKey(Integer.valueOf(intValue))) {
                    g.at = true;
                    b.a(eVar.c());
                    arrayList.add(Integer.valueOf(intValue));
                }
            }
            if (arrayList != null && arrayList.size() > 0) {
                for (Integer intValue2 : arrayList) {
                    b2.remove(Integer.valueOf(intValue2.intValue()));
                }
                com.igexin.push.config.a.a().g();
            }
            boolean z = true;
            for (Entry entry2 : b.entrySet()) {
                boolean z2;
                intValue = ((Integer) entry2.getKey()).intValue();
                eVar = (e) entry2.getValue();
                if (b2.containsKey(Integer.valueOf(intValue))) {
                    if (!((e) b2.get(Integer.valueOf(intValue))).b().equals(eVar.b())) {
                        g.at = true;
                        g.ap++;
                        b.a(eVar);
                        z = false;
                    }
                    z2 = z;
                } else {
                    g.ap++;
                    b.a(eVar);
                    z2 = false;
                }
                z = z2;
            }
            if (z) {
                l.t.a(fVar.a());
                com.igexin.push.config.a.a().g();
                Process.killProcess(Process.myPid());
                return;
            }
            return;
        }
        for (Entry entry22 : b.entrySet()) {
            eVar = (e) entry22.getValue();
            g.ap++;
            b.a(eVar);
        }
    }

    public static boolean a() {
        try {
            if (PushBuildConfig.sdk_conf_debug_level.equals(l.u)) {
                return false;
            }
            String[] split = l.u.split(",");
            for (String b : split) {
                if (b(b)) {
                    return false;
                }
            }
            if (PushBuildConfig.sdk_conf_debug_level.equals(l.v)) {
                return false;
            }
            split = l.v.split(",");
            Class cls = Class.forName("android.os.ServiceManager");
            Method method = cls.getMethod("getService", new Class[]{String.class});
            method.setAccessible(true);
            for (String a : split) {
                if (a(cls, method, a)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean a(Class cls, Method method, String str) {
        try {
            return method.invoke(cls, new Object[]{str}) != null;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean a(String str) {
        PackageManager packageManager = g.g.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", null);
        intent.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean b() {
        return System.currentTimeMillis() > l.c;
    }

    private static boolean b(String str) {
        try {
            g.g.getPackageManager().getPackageInfo(str, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean c() {
        PackageManager packageManager = g.g.getPackageManager();
        String packageName = g.g.getPackageName();
        return packageManager.checkPermission("android.permission.ACCESS_WIFI_STATE", packageName) == 0 && packageManager.checkPermission("android.permission.CHANGE_WIFI_STATE", packageName) == 0;
    }
}
