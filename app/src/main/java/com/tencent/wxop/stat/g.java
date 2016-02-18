package com.tencent.wxop.stat;

import android.content.Context;
import android.content.IntentFilter;
import com.igexin.sdk.PushConsts;
import com.tencent.wxop.stat.b.b;
import com.tencent.wxop.stat.b.f;
import com.tencent.wxop.stat.b.l;
import com.tencent.wxop.stat.b.r;
import io.realm.internal.Table;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import org.apache.http.HttpHost;
import org.json.JSONObject;

public class g {
    private static g bg;
    private List<String> bc;
    private volatile HttpHost bd;
    private f be;
    private int bf;
    private Context bh;
    private b bi;
    private volatile String c;
    private volatile int g;

    static {
        bg = null;
    }

    private g(Context context) {
        this.bc = null;
        this.g = 2;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.bd = null;
        this.be = null;
        this.bf = 0;
        this.bh = null;
        this.bi = null;
        this.bh = context.getApplicationContext();
        this.be = new f();
        ak.j(context);
        this.bi = l.av();
        Y();
        this.bc = new ArrayList(10);
        this.bc.add("117.135.169.101");
        this.bc.add("140.207.54.125");
        this.bc.add("180.153.8.53");
        this.bc.add("120.198.203.175");
        this.bc.add("14.17.43.18");
        this.bc.add("163.177.71.186");
        this.bc.add("111.30.131.31");
        this.bc.add("123.126.121.167");
        this.bc.add("123.151.152.111");
        this.bc.add("113.142.45.79");
        this.bc.add("123.138.162.90");
        this.bc.add("103.7.30.94");
        Z();
    }

    private String O() {
        try {
            String str = "pingma.qq.com";
            if (!d(str)) {
                return InetAddress.getByName(str).getHostAddress();
            }
        } catch (Throwable e) {
            this.bi.b(e);
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    private void Y() {
        this.g = 0;
        this.bd = null;
        this.c = null;
    }

    private static boolean d(String str) {
        return Pattern.compile("(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})").matcher(str).matches();
    }

    public static g r(Context context) {
        if (bg == null) {
            synchronized (g.class) {
                if (bg == null) {
                    bg = new g(context);
                }
            }
        }
        return bg;
    }

    public final int D() {
        return this.g;
    }

    public final void I() {
        this.bf = (this.bf + 1) % this.bc.size();
    }

    public final HttpHost V() {
        return this.bd;
    }

    public final boolean W() {
        return this.g == 1;
    }

    public final boolean X() {
        return this.g != 0;
    }

    final void Z() {
        if (r.W(this.bh)) {
            if (c.ad) {
                String O = O();
                if (c.k()) {
                    this.bi.b("remoteIp ip is " + O);
                }
                if (l.e(O)) {
                    String str;
                    if (this.bc.contains(O)) {
                        str = O;
                    } else {
                        str = (String) this.bc.get(this.bf);
                        if (c.k()) {
                            this.bi.c(O + " not in ip list, change to:" + str);
                        }
                    }
                    c.o("http://" + str + ":80/mstat/report");
                }
            }
            this.c = l.E(this.bh);
            if (c.k()) {
                this.bi.b("NETWORK name:" + this.c);
            }
            if (l.e(this.c)) {
                if ("WIFI".equalsIgnoreCase(this.c)) {
                    this.g = 1;
                } else {
                    this.g = 2;
                }
                this.bd = l.v(this.bh);
            }
            if (e.a()) {
                e.n(this.bh);
                return;
            }
            return;
        }
        if (c.k()) {
            this.bi.b((Object) "NETWORK TYPE: network is close.");
        }
        Y();
    }

    public final void aa() {
        this.bh.getApplicationContext().registerReceiver(new z(this), new IntentFilter(PushConsts.ACTION_BROADCAST_NETWORK_CHANGE));
    }

    public final String b() {
        return this.c;
    }

    public final void b(String str) {
        if (c.k()) {
            this.bi.b("updateIpList " + str);
        }
        try {
            if (l.e(str)) {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.length() > 0) {
                    Iterator keys = jSONObject.keys();
                    while (keys.hasNext()) {
                        String string = jSONObject.getString((String) keys.next());
                        if (l.e(string)) {
                            for (String str2 : string.split(";")) {
                                String str22;
                                if (l.e(str22)) {
                                    String[] split = str22.split(":");
                                    if (split.length > 1) {
                                        str22 = split[0];
                                        if (d(str22) && !this.bc.contains(str22)) {
                                            if (c.k()) {
                                                this.bi.b("add new ip:" + str22);
                                            }
                                            this.bc.add(str22);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            this.bi.b(e);
        }
        this.bf = new Random().nextInt(this.bc.size());
    }
}
