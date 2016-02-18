package com.igexin.push.core;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.a.b.d;
import com.igexin.a.a.d.a.c;
import com.igexin.push.b.b;
import com.igexin.push.core.a.e;
import com.igexin.push.d.j;
import com.igexin.push.f.a;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushService;
import com.tencent.connect.common.Constants;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class f implements c {
    private static f l;
    private Context a;
    private h b;
    private Handler c;
    private ConcurrentLinkedQueue d;
    private e e;
    private ConnectivityManager f;
    private d g;
    private com.igexin.a.a.b.c h;
    private j i;
    private com.igexin.push.d.c j;
    private b k;

    private f() {
        this.d = new ConcurrentLinkedQueue();
        this.b = new h();
    }

    public static f a() {
        if (l == null) {
            l = new f();
        }
        return l;
    }

    private void p() {
        String packageName = this.a.getPackageName();
        List<PackageInfo> installedPackages = this.a.getPackageManager().getInstalledPackages(4);
        if (installedPackages != null) {
            for (PackageInfo packageInfo : installedPackages) {
                if ((packageInfo.applicationInfo.flags & 1) == 0 || (packageInfo.applicationInfo.flags & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 1) {
                    ServiceInfo[] serviceInfoArr = packageInfo.services;
                    if (!(serviceInfoArr == null || serviceInfoArr.length == 0)) {
                        int length = serviceInfoArr.length;
                        int i = 0;
                        while (i < length) {
                            ServiceInfo serviceInfo = serviceInfoArr[i];
                            if (a.o.equals(serviceInfo.name) || a.n.equals(serviceInfo.name) || a.p.equals(serviceInfo.name)) {
                                String str = packageInfo.packageName;
                                if (!packageName.equals(str)) {
                                    com.igexin.push.core.c.f.a().d().put(str, serviceInfo.name);
                                }
                            } else {
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean q() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PushConsts.ACTION_BROADCAST_NETWORK_CHANGE);
        intentFilter.addAction("com.igexin.sdk.action.snlrefresh");
        intentFilter.addAction("com.igexin.sdk.action.snlretire");
        intentFilter.addAction(g.W);
        intentFilter.addAction("com.igexin.sdk.action.execute");
        intentFilter.addAction("com.igexin.sdk.action.doaction");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        if (a.c()) {
            intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        }
        if (this.a.registerReceiver(n.a(), intentFilter) == null) {
            com.igexin.a.a.c.a.b("CoreLogic|InternalPublicReceiver|null");
        }
        intentFilter = new IntentFilter();
        intentFilter.addDataScheme("package");
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        if (this.a.registerReceiver(m.a(), intentFilter) == null) {
            com.igexin.a.a.c.a.b("CoreLogic|InternalPackageReceiver|null");
        }
        return true;
    }

    public void a(e eVar) {
        this.c = eVar;
    }

    public boolean a(Context context) {
        this.a = context;
        this.b.start();
        return true;
    }

    public boolean a(Intent intent) {
        if (g.g == null) {
            return false;
        }
        g.g.sendBroadcast(intent);
        return true;
    }

    public boolean a(Message message) {
        if (g.h.get()) {
            this.c.sendMessage(message);
        } else {
            this.d.add(message);
        }
        return true;
    }

    public boolean a(com.igexin.a.a.d.a.f fVar, com.igexin.a.a.d.e eVar) {
        return this.e != null ? this.e.a((Object) fVar) : false;
    }

    public boolean a(com.igexin.a.a.d.d dVar, com.igexin.a.a.d.e eVar) {
        return this.e != null ? this.e.a(dVar) : false;
    }

    public boolean a(com.igexin.push.e.b.f fVar) {
        return fVar != null ? d.c().a(fVar, false, true) : false;
    }

    public boolean a(String str) {
        String e = e.a().e("ss");
        if (!(g.g == null || this.j == null)) {
            new com.igexin.sdk.a.d(g.g).b();
            g.j = false;
            g.n = false;
            com.igexin.push.d.a aVar = new com.igexin.push.d.a();
            aVar.a(c.stop);
            this.j.a(aVar);
            if (e != null && Constants.VIA_TO_TYPE_QQ_GROUP.equals(e)) {
                try {
                    InputStream inputStream = Runtime.getRuntime().exec("ps").getInputStream();
                    if (inputStream != null) {
                        Object split;
                        String packageName = g.g.getPackageName();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        List arrayList = new ArrayList();
                        while (true) {
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            split = readLine.split("\\s+");
                            arrayList.add(split);
                            if (readLine.indexOf(packageName + "/files/gdaemon") != -1 && split.length > 0) {
                                break;
                            }
                        }
                        Process.killProcess(Integer.valueOf(split[1]).intValue());
                        bufferedReader.close();
                        inputStream.close();
                    }
                } catch (Exception e2) {
                }
                e();
            }
        }
        return true;
    }

    public boolean a(boolean z) {
        if (!(g.g == null || this.j == null)) {
            new com.igexin.sdk.a.d(g.g).a();
            g.j = true;
            if (!new com.igexin.sdk.a.b(g.g).b()) {
                new com.igexin.sdk.a.c(g.g).a();
                g.k = true;
                new com.igexin.sdk.a.b(g.g).a();
            }
            if (z) {
                new com.igexin.sdk.a.c(g.g).a();
                g.k = true;
            }
            com.igexin.push.d.a aVar = new com.igexin.push.d.a();
            aVar.a(c.start);
            this.j.a(aVar);
        }
        return true;
    }

    public void b() {
        try {
            com.igexin.a.a.c.a.b("CoreLogic|init starts");
            this.f = (ConnectivityManager) this.a.getSystemService("connectivity");
            g.a(this.a);
            this.k = new b(this.a);
            com.igexin.a.a.c.a.b("CoreLogic|init config");
            com.igexin.push.config.j.a().b();
            com.igexin.a.a.c.a.b("CoreLogic|init receiver");
            q();
            this.g = d.c();
            this.g.a(new com.igexin.push.c.a(this.a, j()));
            this.g.a((c) this);
            this.g.a(this.a);
            com.igexin.a.a.d.d aVar = new com.igexin.push.b.a();
            aVar.a(com.igexin.push.core.c.f.a());
            aVar.a(com.igexin.push.core.c.c.a());
            aVar.a(com.igexin.push.core.c.b.a());
            aVar.a(com.igexin.push.core.b.e.a());
            aVar.a(com.igexin.push.config.a.a());
            this.g.a(aVar, true, false);
            d.c().a(com.igexin.a.b.a.a(g.A.getBytes()));
            g.af = this.g.a(com.igexin.push.e.b.c.g(), false, true);
            g.ag = this.g.a(com.igexin.push.e.b.e.g(), true, true);
            c();
            this.e = e.a();
            d();
            this.i = new j();
            this.i.a(this.a, this.g, this.e);
            this.j = new com.igexin.push.d.c();
            this.j.a(this.a);
            com.igexin.push.d.a aVar2 = new com.igexin.push.d.a();
            aVar2.a(c.start);
            this.j.a(aVar2);
            com.igexin.push.a.a.c.c().d();
            g.h.set(true);
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                Message message = (Message) it.next();
                if (this.c != null) {
                    this.c.sendMessage(message);
                }
            }
            e.a().t();
            this.e.a(Process.myPid());
            p();
            com.igexin.a.a.c.a.b("CoreLogic|init extensions");
            com.igexin.push.extension.a.a().a(this.a);
            com.igexin.a.a.c.a.b("CoreLogic|init finished");
        } catch (Exception e) {
            com.igexin.a.a.c.a.b("CoreLogic|init|failed");
        }
    }

    public boolean b(String str) {
        if (!(g.g == null || this.j == null)) {
            new com.igexin.sdk.a.c(g.g).b();
            g.k = false;
            g.n = false;
            com.igexin.push.d.a aVar = new com.igexin.push.d.a();
            aVar.a(c.stop);
            this.j.a(aVar);
        }
        return true;
    }

    public com.igexin.push.e.b.a c() {
        com.igexin.a.a.d.d g = com.igexin.push.e.b.a.g();
        g.a(new com.igexin.push.a.a.a());
        g.a(new com.igexin.push.a.a.b());
        g.a(new com.igexin.push.a.a.d());
        g.a(com.igexin.push.a.a.c.c());
        g.ah = this.g.a(g, false, true);
        this.a.sendBroadcast(new Intent());
        return g;
    }

    public void d() {
        if (TextUtils.isEmpty(g.x)) {
            try {
                if (a.a()) {
                    WifiInfo connectionInfo = ((WifiManager) this.a.getSystemService("wifi")).getConnectionInfo();
                    if (connectionInfo != null) {
                        com.igexin.push.core.c.f.a().a(connectionInfo.getMacAddress());
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public void e() {
        Intent intent = new Intent(this.a.getApplicationContext(), PushService.class);
        intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "stopUserService");
        this.a.getApplicationContext().startService(intent);
        this.a.stopService(new Intent(this.a, PushService.class));
    }

    public com.igexin.a.a.b.c f() {
        if (this.h == null) {
            this.h = com.igexin.push.c.a.c.a();
        }
        return this.h;
    }

    public j g() {
        return this.i;
    }

    public com.igexin.push.d.c h() {
        return this.j;
    }

    public e i() {
        return this.e;
    }

    public ConnectivityManager j() {
        return this.f;
    }

    public b k() {
        return this.k;
    }

    public void l() {
        try {
            this.a.unregisterReceiver(d.c());
        } catch (Exception e) {
        }
        try {
            this.a.unregisterReceiver(m.a());
        } catch (Exception e2) {
        }
        try {
            this.a.unregisterReceiver(n.a());
        } catch (Exception e3) {
        }
        try {
            com.igexin.push.extension.a.a().b();
        } catch (Throwable th) {
        }
    }

    public String m() {
        if (this.f == null) {
            return null;
        }
        NetworkInfo activeNetworkInfo = this.f.getActiveNetworkInfo();
        return activeNetworkInfo != null ? activeNetworkInfo.getType() == 1 ? "wifi" : activeNetworkInfo.getType() == 0 ? "mobile" : null : null;
    }

    public boolean n() {
        return true;
    }

    public long o() {
        return 94808;
    }
}
