package com.alipay.security.mobile.module.deviceinfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public final class a {
    private static a a;

    static {
        a = new a();
    }

    private a() {
    }

    private static a a() {
        return a;
    }

    private static byte[] a(Context context, String str) {
        try {
            for (PackageInfo packageInfo : context.getPackageManager().getInstalledPackages(64)) {
                if (packageInfo.packageName.equals(str)) {
                    return packageInfo.signatures[0].toByteArray();
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static byte[] b(Context context, String str) {
        try {
            return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(a(context, str)))).getPublicKey().getEncoded();
        } catch (Exception e) {
            return null;
        }
    }
}
