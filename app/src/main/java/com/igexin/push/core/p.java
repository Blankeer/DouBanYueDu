package com.igexin.push.core;

import com.igexin.push.config.l;
import com.igexin.push.core.b.e;
import com.igexin.push.core.b.i;
import com.igexin.sdk.aidl.c;
import io.realm.internal.Table;

final class p extends c {
    p() {
    }

    public byte[] extFunction(byte[] bArr) {
        return null;
    }

    public int isStarted(String str) {
        int a = com.igexin.push.core.b.c.a().a(e.a().b(str), i.IS_STARTED);
        return (a != 0 || g.j) ? a : 1;
    }

    public int onASNLConnected(String str, String str2, String str3, long j) {
        return f.a() != null ? f.a().h().a(str3) : -1;
    }

    public int onASNLNetworkConnected() {
        if (f.a().g().a()) {
            return -1;
        }
        f.a().g().b();
        return 0;
    }

    public int onASNLNetworkDisconnected() {
        if (f.a().g().a()) {
            return -1;
        }
        f.a().g().b(false);
        return 0;
    }

    public int onPSNLConnected(String str, String str2, String str3, long j) {
        return (f.a() == null || str.equals(Table.STRING_DEFAULT_VALUE) || str2.equals(Table.STRING_DEFAULT_VALUE)) ? -1 : f.a().h().a(str, str2);
    }

    public int receiveToPSNL(String str, String str2, byte[] bArr) {
        return (str2 == null || bArr == null || f.a().g().a()) ? -1 : f.a().h().b(str, str2, bArr);
    }

    public int sendByASNL(String str, String str2, byte[] bArr) {
        return (str2 == null || bArr == null || !f.a().g().a()) ? -1 : f.a().h().a(str, str2, bArr);
    }

    public int setSilentTime(int i, int i2, String str) {
        String b = e.a().b(str);
        int a = com.igexin.push.core.b.c.a().a(b, i.SET_SILENTTIME);
        if (a == 0 && l.k) {
            com.igexin.push.core.a.e.a().a(i, i2, b);
        }
        return a;
    }

    public int startService(String str) {
        String b = e.a().b(str);
        int a = com.igexin.push.core.b.c.a().a(b, i.START_SERVICE);
        if (a == 0) {
            f.a().a(true);
            g.C = b;
        }
        return a;
    }

    public int stopService(String str) {
        String b = e.a().b(str);
        int a = com.igexin.push.core.b.c.a().a(b, i.STOP_SERVICE);
        if (a == 0) {
            f.a().a(b);
        }
        return a;
    }
}
