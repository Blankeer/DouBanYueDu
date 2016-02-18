package com.igexin.push.d;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.a.a.b.d;
import com.igexin.push.c.b;
import com.igexin.push.c.c.a;
import com.igexin.push.c.c.c;
import com.igexin.push.c.c.e;
import com.igexin.push.c.c.f;
import com.igexin.push.c.c.i;
import com.igexin.push.c.c.k;
import com.igexin.push.c.c.l;
import com.igexin.push.c.c.m;
import com.igexin.push.c.c.n;
import com.igexin.push.c.c.o;
import com.igexin.push.config.SDKUrlConfig;
import com.igexin.push.core.c.w;
import com.igexin.push.core.g;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.connect.common.Constants;
import com.tencent.open.SocialConstants;
import io.realm.internal.Table;
import java.text.SimpleDateFormat;
import java.util.Date;

public class j {
    private static String a;
    private Context b;
    private d c;
    private k d;
    private boolean e;
    private long f;
    private long g;
    private boolean h;

    static {
        a = "SNL";
    }

    public j() {
        this.e = false;
        this.f = 0;
        this.g = 0;
        this.h = false;
    }

    private long a(long j) {
        long j2 = j / 10;
        return ((long) (((Math.random() * ((double) j2)) * 2.0d) - ((double) j2))) + j;
    }

    private String b(e eVar) {
        String str = Table.STRING_DEFAULT_VALUE;
        if (eVar instanceof f) {
            return "R-" + ((f) eVar).a();
        }
        if (eVar instanceof o) {
            return "R-" + ((o) eVar).b;
        }
        if (eVar instanceof i) {
            return "S-" + String.valueOf(((i) eVar).a);
        }
        if (eVar instanceof k) {
            if (((k) eVar).e != 0) {
                return "S-" + String.valueOf(((k) eVar).e);
            }
        } else if (eVar instanceof l) {
            return "S-" + String.valueOf(((l) eVar).a);
        } else {
            if (eVar instanceof m) {
                return "S-" + String.valueOf(((m) eVar).e);
            }
            if (eVar instanceof com.igexin.push.c.c.d) {
                return "C-" + ((com.igexin.push.c.c.d) eVar).g;
            }
            if (eVar instanceof n) {
                return "C-" + ((n) eVar).g;
            }
            if (eVar instanceof a) {
                return "C-" + ((a) eVar).d;
            }
            if (eVar instanceof c) {
                return "C-" + ((c) eVar).d;
            }
        }
        return str;
    }

    private boolean d() {
        if (com.igexin.push.config.l.o && this.f + this.g >= com.igexin.push.config.l.p) {
            a aVar = new a();
            aVar.a(com.igexin.push.core.c.check);
            com.igexin.push.core.f.a().h().a(aVar);
        }
        return false;
    }

    private void e() {
        Cursor cursor;
        Throwable th;
        Cursor a;
        try {
            String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            a = com.igexin.push.core.f.a().k().a("bi", new String[]{SocialConstants.PARAM_TYPE}, new String[]{Constants.VIA_TO_TYPE_QQ_GROUP}, null, null);
            if (a != null) {
                try {
                    if (a.getCount() == 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("loginerror_connecterror_count", Integer.valueOf(1));
                        contentValues.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, format);
                        contentValues.put(SocialConstants.PARAM_TYPE, Constants.VIA_TO_TYPE_QQ_GROUP);
                        com.igexin.push.core.f.a().k().a("bi", contentValues);
                    } else {
                        int i = 0;
                        while (a.moveToNext()) {
                            String string = a.getString(a.getColumnIndexOrThrow(WBConstants.GAME_PARAMS_GAME_CREATE_TIME));
                            String string2 = a.getString(a.getColumnIndexOrThrow(WorksListUri.KEY_ID));
                            ContentValues contentValues2;
                            if (format.equals(string)) {
                                i = a.getInt(a.getColumnIndexOrThrow("loginerror_connecterror_count"));
                                contentValues2 = new ContentValues();
                                contentValues2.put("loginerror_connecterror_count", Integer.valueOf(i + 1));
                                com.igexin.push.core.f.a().k().a("bi", contentValues2, new String[]{WorksListUri.KEY_ID}, new String[]{string2});
                            } else {
                                contentValues2 = new ContentValues();
                                contentValues2.put(SocialConstants.PARAM_TYPE, Constants.VIA_SSO_LOGIN);
                                com.igexin.push.core.f.a().k().a("bi", contentValues2, new String[]{WorksListUri.KEY_ID}, new String[]{string2});
                                contentValues2 = new ContentValues();
                                contentValues2.put("loginerror_connecterror_count", Integer.valueOf(i + 1));
                                contentValues2.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, format);
                                contentValues2.put(SocialConstants.PARAM_TYPE, Constants.VIA_TO_TYPE_QQ_GROUP);
                                com.igexin.push.core.f.a().k().a("bi", contentValues2);
                            }
                        }
                    }
                } catch (Exception e) {
                    cursor = a;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (a != null) {
                a.close();
            }
        } catch (Exception e2) {
            cursor = null;
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th3) {
            th = th3;
            a = null;
            if (a != null) {
                a.close();
            }
            throw th;
        }
    }

    public int a(String str, e eVar) {
        return a(str, eVar, false);
    }

    public int a(String str, e eVar, boolean z) {
        if (str == null || eVar == null) {
            return -1;
        }
        if (!g.m) {
            boolean z2 = (eVar instanceof i) || (eVar instanceof f);
            if (!z2) {
                com.igexin.a.a.c.a.a("snl|sendData|not online|" + eVar.getClass().getName());
                return -3;
            }
        }
        if (!this.e) {
            return com.igexin.push.core.f.a().h().a(str, eVar);
        }
        if (z) {
            int i = 15;
            if (com.igexin.push.config.l.e > 0) {
                i = com.igexin.push.config.l.e;
            }
            if (this.c.a(SDKUrlConfig.getCmAddress(), 3, com.igexin.push.core.f.a().f(), eVar, true, i, new b()) == null) {
                return -2;
            }
        } else if (this.c.a(SDKUrlConfig.getCmAddress(), 3, com.igexin.push.core.f.a().f(), eVar, true) == null) {
            return -2;
        }
        byte[] d = eVar.d();
        if (d != null) {
            this.g = (((long) d.length) + 8) + this.g;
        } else {
            this.g += 8;
        }
        d();
        return 0;
    }

    public void a(Context context, d dVar, k kVar) {
        this.b = context;
        this.c = dVar;
        this.d = kVar;
    }

    public void a(e eVar) {
        if (eVar != null) {
            if (this.e) {
                String b = b(eVar);
                if (!b.equals("S-") && !b.equals("R-")) {
                    if (b.length() > 0 && !b.equals("C-") && !b.equals("C-" + g.s) && !b.equals("R-" + g.A) && !b.equals("S-" + g.r)) {
                        com.igexin.push.core.f.a().h().b(b, eVar);
                    } else if (this.d != null) {
                        this.d.a(eVar);
                    }
                    byte[] d = eVar.d();
                    if (d != null) {
                        this.f = (((long) d.length) + 8) + this.f;
                    } else {
                        this.f += 8;
                    }
                    d();
                }
            } else if (this.d != null) {
                this.d.a(eVar);
            }
        }
    }

    public void a(boolean z) {
        if (this.e != z) {
            this.e = z;
            this.h = false;
            if (z) {
                this.g = 0;
                this.f = 0;
                this.c.a((Object) new com.igexin.push.c.b.b());
                this.c.d();
                return;
            }
            this.c.a(SDKUrlConfig.getCmAddress().replaceFirst("socket", "disConnect"), 0, null);
        }
    }

    public boolean a() {
        return this.e;
    }

    public void b() {
        g.D = 0;
        if (!this.e && this.d != null) {
            this.d.b();
        }
    }

    public void b(boolean z) {
        if (z) {
            com.igexin.a.a.c.a.b("disconnected by user");
            w.d();
            if (g.m) {
                g.m = false;
                com.igexin.push.core.a.e.a().m();
            }
        } else {
            com.igexin.a.a.c.a.b("disconnected|network");
            com.igexin.push.core.i.a().a(com.igexin.push.core.k.NETWORK_ERROR);
            w.d();
            w.a();
            e();
            if (g.m) {
                g.m = false;
                com.igexin.push.core.a.e.a().m();
            }
            com.igexin.a.a.c.a.b("SNL autoReconnect notifyNetworkDisconnected");
            c(true);
        }
        if (this.e) {
            com.igexin.push.core.f.a().h().b();
        } else if (this.d != null) {
            this.d.a(z);
        }
    }

    public long c() {
        return g.D;
    }

    public void c(boolean z) {
        com.igexin.a.a.c.a.b(a + "autoReconnect isResetDelay = " + z);
        if (z) {
            g.D = 0;
            com.igexin.a.a.c.a.b(a + " isResetDelay = true, auto reconnect connect DelayTime = 0");
        }
        boolean a = com.igexin.push.core.a.e.a().a(System.currentTimeMillis());
        boolean b = com.igexin.push.f.a.b();
        com.igexin.a.a.c.a.b(a + " autoReconnect isSdkOn = " + g.j + " CoreRuntimeInfo.isPushOn = " + g.k + " checkIsSilentTime = " + a + " isBlockEndTime = " + b + " CoreRuntimeInfo.isNetworkAvailable = " + g.i);
        if (g.i && g.j && g.k && !a && b) {
            if (g.D <= 0) {
                g.D = 200;
            } else if (g.D <= 10000) {
                g.D += 1000;
            } else {
                g.D += 60000;
            }
            if (g.D > com.umeng.analytics.a.h) {
                g.D = com.umeng.analytics.a.h;
            }
            g.D = a(g.D);
            com.igexin.a.a.c.a.b(a + "autoReconnect reConnectDelayTime = " + g.D);
        } else {
            g.D = com.umeng.analytics.a.h;
            com.igexin.a.a.c.a.b(a + "auto reconnect stop");
        }
        com.igexin.push.e.b.e.g().h();
    }
}
