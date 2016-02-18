package com.igexin.push.d;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import com.alipay.sdk.protocol.h;
import com.igexin.a.a.b.g;
import com.igexin.push.c.c.a;
import com.igexin.push.c.c.b;
import com.igexin.push.c.c.e;
import com.igexin.push.c.c.i;
import com.igexin.push.c.c.k;
import com.igexin.push.c.c.l;
import com.igexin.push.c.c.m;
import com.igexin.push.c.c.n;
import com.igexin.push.c.c.o;
import com.igexin.push.core.d;
import com.igexin.push.core.q;
import com.igexin.push.e.b.f;
import com.igexin.sdk.PushBuildConfig;
import com.igexin.sdk.PushService;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class c {
    private Context a;
    private d b;
    private f c;
    private f d;
    private b e;
    private List f;
    private Map g;
    private Map h;
    private boolean i;

    private e a(byte[] bArr) {
        b b = b(bArr);
        e eVar = null;
        switch (b.b) {
            case dx.e /*4*/:
                eVar = new i();
                break;
            case dj.f /*5*/:
                eVar = new k();
                break;
            case ci.g /*6*/:
                eVar = new com.igexin.push.c.c.f();
                break;
            case h.h /*9*/:
                eVar = new o();
                break;
            case HeaderMapDB.TUPLE2_COMPARATOR_STATIC /*25*/:
                eVar = new com.igexin.push.c.c.d();
                break;
            case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
                eVar = new n();
                break;
            case HeaderMapDB.TUPLE4_COMPARATOR_STATIC /*27*/:
                eVar = new com.igexin.push.c.c.c();
                break;
            case HeaderMapDB.FUN_COMPARATOR_REVERSE /*28*/:
                eVar = new a();
                break;
            case HeaderMapDB.HASHER_INT_ARRAY /*36*/:
                eVar = new l();
                break;
            case HeaderMapDB.HASHER_LONG_ARRAY /*37*/:
                eVar = new m();
                break;
        }
        if (eVar != null) {
            eVar.a(b.d);
        }
        return eVar;
    }

    private b b(byte[] bArr) {
        b bVar = new b();
        bVar.a = g.b(bArr, 0);
        bVar.b = bArr[2];
        bVar.d = new byte[bVar.a];
        g.a(bArr, 3, bVar.d, 0, bVar.a);
        return bVar;
    }

    private void b(String str) {
        b bVar = (b) this.g.get(str);
        bVar.a(new g(this, bVar, str));
        try {
            Context createPackageContext = this.a.createPackageContext(bVar.a(), 3);
            this.a.bindService(new Intent(createPackageContext, createPackageContext.getClassLoader().loadClass(bVar.b())), bVar.d(), 0);
        } catch (Exception e) {
        }
        this.g.put(str, bVar);
    }

    private List c(String str) {
        List arrayList = new ArrayList();
        for (Entry entry : this.h.entrySet()) {
            String obj = entry.getKey().toString();
            if (((b) entry.getValue()).e().equals(str)) {
                arrayList.add(obj);
            }
        }
        return arrayList;
    }

    private void c() {
        this.b = d.init;
        a aVar = new a();
        aVar.a(com.igexin.push.core.c.start);
        a(aVar);
    }

    private void d() {
        this.f.clear();
        this.b = d.active;
        if (this.i) {
            for (Entry key : this.g.entrySet()) {
                b((String) key.getKey());
            }
            this.i = false;
        }
        com.igexin.push.core.f.a().g().a(true);
    }

    public int a(String str) {
        if (this.b != d.prepare || str.equals(Table.STRING_DEFAULT_VALUE)) {
            return -1;
        }
        this.d.t();
        this.g.clear();
        this.h.clear();
        this.i = false;
        this.f.clear();
        this.e.c(str);
        this.b = d.passive;
        com.igexin.push.core.f.a().g().a(false);
        return 0;
    }

    public int a(String str, e eVar) {
        b bVar = new b();
        bVar.d = eVar.d();
        if (bVar.d != null) {
            bVar.a = bVar.d.length;
            bVar.b = (byte) eVar.i;
            byte[] a = bVar.a();
            if (!(this.e == null || this.e.c() == null)) {
                try {
                    return this.e.c().sendByASNL(this.e.e(), str, a);
                } catch (Exception e) {
                    c();
                }
            }
        }
        return -1;
    }

    public int a(String str, String str2) {
        if (this.b == d.prepare || this.b == d.passive) {
            return -1;
        }
        b bVar = new b();
        bVar.a(str);
        bVar.b(str2);
        bVar.c(str);
        this.g.put(str, bVar);
        if (this.b == d.active) {
            b(str);
        } else {
            this.i = true;
        }
        return 0;
    }

    public int a(String str, String str2, byte[] bArr) {
        e a = a(bArr);
        b bVar = (b) this.g.get(str);
        if (bVar == null || !com.igexin.push.core.g.m) {
            return -1;
        }
        this.h.put(str2, bVar);
        return com.igexin.push.core.f.a().g().a(str2, a);
    }

    public void a(Context context) {
        this.a = context;
        this.b = d.init;
        this.f = new ArrayList();
        this.g = new HashMap();
        this.h = new HashMap();
    }

    public void a(Intent intent) {
        String stringExtra;
        String stringExtra2;
        if (intent.getAction().equals("com.igexin.sdk.action.snlrefresh") && com.igexin.push.config.l.o) {
            stringExtra = intent.getStringExtra("groupid");
            stringExtra2 = intent.getStringExtra("responseSNLAction");
            boolean z = com.igexin.push.core.g.j;
            boolean z2 = com.igexin.push.core.g.k;
            String stringExtra3 = intent.getStringExtra("branch");
            boolean a = com.igexin.push.core.a.e.a().a(System.currentTimeMillis());
            long a2 = q.a() + q.b();
            if (com.igexin.push.core.g.d.equals(stringExtra) && PushBuildConfig.sdk_conf_channelid.equals(stringExtra3) && z && z2 && !a) {
                Intent intent2 = new Intent();
                intent2.setAction(stringExtra2);
                intent2.putExtra("groupid", com.igexin.push.core.g.d);
                intent2.putExtra("branch", PushBuildConfig.sdk_conf_channelid);
                intent2.putExtra("pkgname", com.igexin.push.core.g.g.getPackageName());
                intent2.putExtra("classname", PushService.class.getName());
                intent2.putExtra("startup_time", com.igexin.push.core.g.V);
                intent2.putExtra("network_traffic", a2);
                com.igexin.push.core.g.g.sendBroadcast(intent2);
            }
        } else if (intent.getAction().equals(com.igexin.push.core.g.W) && com.igexin.push.config.l.o) {
            stringExtra = intent.getStringExtra("groupid");
            stringExtra2 = intent.getStringExtra("branch");
            if (com.igexin.push.core.g.d.equals(stringExtra) && PushBuildConfig.sdk_conf_channelid.equals(stringExtra2)) {
                i iVar = new i();
                iVar.a(intent.getStringExtra("groupid"));
                iVar.b(intent.getStringExtra("pkgname"));
                iVar.c(intent.getStringExtra("classname"));
                iVar.a(intent.getLongExtra("startup_time", 0));
                iVar.b(intent.getLongExtra("network_traffic", 0));
                this.f.add(iVar);
            }
        } else if (intent.getAction().equals("com.igexin.sdk.action.snlretire") && com.igexin.push.config.l.o) {
            stringExtra = intent.getStringExtra("groupid");
            stringExtra2 = intent.getStringExtra("branch");
            if (com.igexin.push.core.g.d.equals(stringExtra) && PushBuildConfig.sdk_conf_channelid.equals(stringExtra2)) {
                a aVar = new a();
                aVar.a(com.igexin.push.core.c.retire);
                com.igexin.push.core.f.a().h().a(aVar);
            }
        }
    }

    public void a(a aVar) {
        Intent intent;
        a aVar2;
        switch (h.b[this.b.ordinal()]) {
            case dx.b /*1*/:
                switch (h.a[aVar.a.ordinal()]) {
                    case dx.b /*1*/:
                        this.f.clear();
                        this.g.clear();
                        this.h.clear();
                        if (this.c != null) {
                            this.c.t();
                        }
                        if (this.d != null) {
                            this.d.t();
                        }
                        this.i = false;
                        boolean z = com.igexin.push.core.g.j;
                        boolean z2 = com.igexin.push.core.g.k;
                        boolean a = com.igexin.push.core.a.e.a().a(System.currentTimeMillis());
                        boolean b = com.igexin.push.f.a.b();
                        if (!z || !z2 || a || !b) {
                            return;
                        }
                        if (com.igexin.push.config.l.o) {
                            intent = new Intent();
                            intent.setAction("com.igexin.sdk.action.snlrefresh");
                            intent.putExtra("groupid", com.igexin.push.core.g.d);
                            intent.putExtra("branch", PushBuildConfig.sdk_conf_channelid);
                            intent.putExtra("responseSNLAction", com.igexin.push.core.g.W);
                            this.a.sendBroadcast(intent);
                            this.c = new d(this, 1000);
                            if (!com.igexin.push.core.f.a().a(this.c)) {
                                return;
                            }
                            return;
                        }
                        d();
                    case dx.c /*2*/:
                        int size = this.f.size();
                        if (size == 0) {
                            d();
                            return;
                        }
                        int i = 1;
                        i iVar = (i) this.f.get(0);
                        while (i < size) {
                            i iVar2 = (i) this.f.get(i);
                            if (iVar2.c() >= iVar.c()) {
                                iVar2 = iVar;
                            }
                            i++;
                            iVar = iVar2;
                        }
                        if (this.a.getPackageName().equals(iVar.a())) {
                            d();
                            return;
                        }
                        this.b = d.prepare;
                        this.e = new b();
                        this.e.a(iVar.a());
                        this.e.b(iVar.b());
                        aVar2 = new a();
                        aVar2.a(com.igexin.push.core.c.determine);
                        a(aVar2);
                    case dx.d /*3*/:
                    case dx.e /*4*/:
                        if (this.c != null) {
                            this.c.t();
                        }
                        c();
                    default:
                }
            case dx.c /*2*/:
                switch (h.a[aVar.a.ordinal()]) {
                    case dx.d /*3*/:
                    case dx.e /*4*/:
                        if (this.d != null) {
                            this.d.t();
                        }
                        if (this.e.c() != null) {
                            try {
                                this.a.unbindService(this.e.d());
                            } catch (Exception e) {
                            }
                        }
                        c();
                    case dj.f /*5*/:
                        this.d = new e(this, 5000);
                        if (com.igexin.push.core.f.a().a(this.d)) {
                            this.e.a(new f(this));
                        } else {
                            this.e.a(new f(this));
                        }
                        try {
                            Context createPackageContext = this.a.createPackageContext(this.e.a(), 3);
                            this.a.bindService(new Intent(createPackageContext, createPackageContext.getClassLoader().loadClass(this.e.b())), this.e.d(), 0);
                        } catch (Exception e2) {
                        }
                    case ci.g /*6*/:
                        try {
                            this.e.c().onPSNLConnected(this.a.getPackageName(), PushService.class.getName(), Table.STRING_DEFAULT_VALUE, 0);
                        } catch (Exception e3) {
                        }
                    default:
                }
            case dx.d /*3*/:
                switch (h.a[aVar.a.ordinal()]) {
                    case dx.d /*3*/:
                        com.igexin.push.core.f.a().g().a(false);
                        com.igexin.push.core.f.a().g().b(true);
                        if (com.igexin.push.config.l.o) {
                            intent = new Intent();
                            intent.setAction("com.igexin.sdk.action.snlretire");
                            intent.putExtra("groupid", com.igexin.push.core.g.d);
                            intent.putExtra("branch", PushBuildConfig.sdk_conf_channelid);
                            this.a.sendBroadcast(intent);
                            return;
                        }
                        aVar2 = new a();
                        aVar2.a(com.igexin.push.core.c.retire);
                        com.igexin.push.core.f.a().h().a(aVar2);
                    case dx.e /*4*/:
                        com.igexin.push.core.g.V = System.currentTimeMillis();
                        for (Entry value : this.g.entrySet()) {
                            try {
                                this.a.unbindService(((b) value.getValue()).d());
                            } catch (Exception e4) {
                            }
                        }
                        com.igexin.push.core.f.a().g().a(false);
                        c();
                    case ci.h /*7*/:
                        intent = new Intent();
                        intent.setAction("com.igexin.sdk.action.snlretire");
                        intent.putExtra("groupid", com.igexin.push.core.g.d);
                        intent.putExtra("branch", PushBuildConfig.sdk_conf_channelid);
                        this.a.sendBroadcast(intent);
                    default:
                }
            case dx.e /*4*/:
                switch (h.a[aVar.a.ordinal()]) {
                    case dx.d /*3*/:
                        if (com.igexin.push.core.g.m) {
                            e lVar = new l();
                            lVar.a = com.igexin.push.core.g.r;
                            com.igexin.push.core.f.a().g().a("S-" + String.valueOf(com.igexin.push.core.g.r), lVar, true);
                            break;
                        }
                        break;
                    case dx.e /*4*/:
                        break;
                    default:
                        return;
                }
                com.igexin.push.core.g.V = System.currentTimeMillis();
                com.igexin.push.core.f.a().g().b(true);
                try {
                    this.a.unbindService(this.e.d());
                } catch (Exception e5) {
                }
                c();
            default:
        }
    }

    public boolean a() {
        if (this.b != d.active) {
            return false;
        }
        for (Entry value : this.g.entrySet()) {
            try {
                ((b) value.getValue()).c().onASNLNetworkConnected();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public int b(String str, e eVar) {
        b bVar = new b();
        bVar.d = eVar.d();
        bVar.a = bVar.d.length;
        bVar.b = (byte) eVar.i;
        byte[] a = bVar.a();
        b bVar2 = (b) this.h.get(str);
        if (bVar2 != null) {
            try {
                if (str.startsWith("S-")) {
                    this.h.put("C-" + com.igexin.a.b.a.a(str.substring(2, str.length())), bVar2);
                }
                return bVar2.c().receiveToPSNL(bVar2.e(), str, a);
            } catch (RemoteException e) {
                this.g.remove(bVar2.e());
                this.h.remove(str);
            }
        }
        return -1;
    }

    public int b(String str, String str2, byte[] bArr) {
        com.igexin.push.core.f.a().g().a(a(bArr));
        return 0;
    }

    public boolean b() {
        if (this.b != d.active) {
            return false;
        }
        for (Entry value : this.g.entrySet()) {
            try {
                ((b) value.getValue()).c().onASNLNetworkDisconnected();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
