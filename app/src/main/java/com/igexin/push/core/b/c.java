package com.igexin.push.core.b;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.RemoteException;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.b.a;
import com.igexin.push.core.g;
import com.igexin.sdk.PushConsts;
import java.util.List;

public class c {
    private static c a;

    public static c a() {
        if (a == null) {
            a = new c();
        }
        return a;
    }

    public int a(String str, i iVar) {
        if (str == null) {
            return -1;
        }
        h a = e.a().a(str);
        if (System.currentTimeMillis() > Long.valueOf(a.c()).longValue()) {
            return -1;
        }
        List e = a.e();
        for (int i = 0; i < e.size(); i++) {
            if (e.get(i) == iVar) {
                return 0;
            }
        }
        return -2;
    }

    public String a(String str) {
        List installedPackages = g.g.getPackageManager().getInstalledPackages(64);
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = (PackageInfo) installedPackages.get(i);
            if (packageInfo.packageName.equals(str)) {
                Signature[] signatureArr = packageInfo.signatures;
                if (signatureArr != null && signatureArr.length > 0) {
                    return signatureArr[0].toCharsString();
                }
            }
        }
        return null;
    }

    public void a(Intent intent) {
        if (intent != null && intent.hasExtra(DoubanAccountOperationFragment_.ACTION_ARG)) {
            String stringExtra = intent.getStringExtra(DoubanAccountOperationFragment_.ACTION_ARG);
            if (stringExtra.equals("connected")) {
                stringExtra = intent.getStringExtra("pkgname");
                a a = b.a().a(stringExtra);
                long currentTimeMillis = System.currentTimeMillis();
                h a2 = e.a().a(stringExtra);
                String b = a2.b();
                long c = a2.c();
                if (currentTimeMillis > c) {
                    b = a.a(stringExtra + "-" + currentTimeMillis);
                    c = 604800 + currentTimeMillis;
                    a2.b(b);
                    a2.a(c);
                }
                e.a().a(stringExtra, a2);
                try {
                    if (a.b() != null) {
                        a.b().onAuthenticated(g.e, "com.igexin.sdk.PushService", b, c);
                    }
                } catch (RemoteException e) {
                }
                try {
                    if (a.b() != null) {
                        g.g.unbindService(a.a());
                    }
                } catch (Exception e2) {
                }
            } else if (stringExtra.equals("disconnected")) {
                a a3 = b.a().a(intent.getStringExtra("pkgname"));
                try {
                    if (a3.b() != null) {
                        g.g.unbindService(a3.a());
                    }
                } catch (Exception e3) {
                }
            }
        }
    }

    public void b(Intent intent) {
        if (intent.getStringExtra(DoubanAccountOperationFragment_.ACTION_ARG).equals(PushConsts.ACTION_BROADCAST_REFRESHLS)) {
            String stringExtra = intent.getStringExtra("callback_pkgname");
            String stringExtra2 = intent.getStringExtra("callback_classname");
            a aVar = new a();
            aVar.a(stringExtra);
            aVar.a(new d(this));
            b.a().a(stringExtra, aVar);
            try {
                Context createPackageContext = g.g.createPackageContext(stringExtra, 3);
                g.g.bindService(new Intent(createPackageContext, createPackageContext.getClassLoader().loadClass(stringExtra2)), aVar.a(), 1);
            } catch (Exception e) {
            }
        }
    }
}
