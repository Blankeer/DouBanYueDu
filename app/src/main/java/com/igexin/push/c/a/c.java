package com.igexin.push.c.a;

import android.support.v4.media.TransportMediator;
import com.igexin.a.a.a.a;
import com.igexin.a.a.b.d;
import com.igexin.a.a.b.e;
import com.igexin.a.a.b.f;
import com.igexin.download.Downloads;
import com.igexin.push.c.c.b;
import com.igexin.push.c.c.g;
import com.igexin.push.c.c.h;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.nio.ByteBuffer;

public class c extends com.igexin.a.a.b.c {
    private byte[] e;
    private boolean f;
    private boolean g;

    c(String str) {
        super(str, true);
        this.e = null;
        this.f = false;
        this.g = false;
    }

    public static com.igexin.a.a.b.c a() {
        com.igexin.a.a.b.c cVar = new c("socketProtocol");
        a aVar = new a("command", cVar);
        return cVar;
    }

    static g a(int i) {
        g gVar = new g();
        gVar.b = 1944742139;
        gVar.c = 3;
        gVar.d = g.a;
        gVar.i = (byte) 0;
        gVar.h = (byte) 0;
        gVar.f = i;
        d.e();
        return gVar;
    }

    static g a(int i, byte b) {
        g gVar = new g();
        gVar.b = 1944742139;
        gVar.a(b);
        gVar.c = 3;
        if (gVar.h == 48) {
            gVar.c += d.c().a().length + 1;
        }
        gVar.d = g.a;
        gVar.f = i;
        d.e();
        return gVar;
    }

    public Object a(f fVar, e eVar, Object obj) {
        int a;
        if (obj instanceof b) {
            b bVar = (b) obj;
            g a2 = a(bVar.b > null ? 1 : 0, bVar.c);
            if (bVar.b > null && bVar.a > 0) {
                if ((a2.g & Downloads.STATUS_RUNNING) == TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                    bVar.a(com.igexin.a.a.b.g.a(bVar.d));
                }
                if ((a2.h & 48) == 48) {
                    bVar.a(a.a(bVar.d, d.c().b()));
                }
            }
            Object obj2 = new byte[((bVar.b > null ? bVar.a + 3 : 0) + (a2.c + 5))];
            a = com.igexin.a.a.b.g.a(1944742139, (byte[]) obj2, 0);
            a += com.igexin.a.a.b.g.c(a2.c, obj2, a);
            a += com.igexin.a.a.b.g.c(a2.d, obj2, a);
            a += com.igexin.a.a.b.g.c(a2.c(), obj2, a);
            a += com.igexin.a.a.b.g.c(a2.f, obj2, a);
            if ((a2.h & 48) == 48) {
                byte[] a3 = d.c().a();
                a += com.igexin.a.a.b.g.c(a3.length, obj2, a);
                a += com.igexin.a.a.b.g.a(a3, 0, obj2, a, a3.length);
            }
            if (bVar.b > null) {
                a += com.igexin.a.a.b.g.b(bVar.a, obj2, a);
                a += com.igexin.a.a.b.g.c(bVar.b, obj2, a);
                if (bVar.a > 0) {
                    a += com.igexin.a.a.b.g.a(bVar.d, 0, obj2, a, bVar.a);
                }
            }
            return obj2;
        }
        b[] bVarArr = (b[]) obj;
        g a4 = a(bVarArr.length);
        int i = 0;
        for (b bVar2 : bVarArr) {
            i += bVar2.a + 3;
        }
        Object obj3 = new byte[((i + 5) + a4.c)];
        a = com.igexin.a.a.b.g.a(1944742139, (byte[]) obj3, 0);
        a += com.igexin.a.a.b.g.c(a4.c, obj3, a);
        a += com.igexin.a.a.b.g.c(a4.d, obj3, a);
        a += com.igexin.a.a.b.g.c(a4.c(), obj3, a);
        i = a + com.igexin.a.a.b.g.c(a4.f, obj3, a);
        for (a = 0; a < bVarArr.length; a++) {
            i += com.igexin.a.a.b.g.b(bVarArr[a].a, obj3, i);
            i += com.igexin.a.a.b.g.c(bVarArr[a].b, obj3, i);
            i += com.igexin.a.a.b.g.a(bVarArr[a].d, 0, obj3, i, bVarArr[a].a);
        }
        return obj3;
    }

    public com.igexin.a.a.d.a.f b(f fVar, e eVar, Object obj) {
        if (eVar.a() == null) {
            eVar.a(new b());
        }
        b bVar = (b) eVar.a();
        ByteBuffer wrap = obj instanceof byte[] ? ByteBuffer.wrap((byte[]) obj) : (ByteBuffer) obj;
        byte b;
        if (bVar.a == 0) {
            if (bVar.d == 0) {
                bVar.f = new byte[8];
            }
            while (wrap.remaining() > 0) {
                b = wrap.get();
                byte[] bArr = bVar.f;
                int i = bVar.d;
                bVar.d = i + 1;
                bArr[i] = b;
                if (bVar.d == 4 && com.igexin.a.a.b.g.c(bVar.f, 0) != 1944742139) {
                    bVar.d = 0;
                }
                if (bVar.d >= 8) {
                    g gVar = new g();
                    gVar.c = bVar.f[4] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    gVar.d = bVar.f[5] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    gVar.a(bVar.f[6]);
                    if (gVar.h == 48) {
                        this.g = true;
                        byte b2 = wrap.get();
                        this.e = new byte[b2];
                        for (b = (byte) 0; b < b2; b++) {
                            this.e[b] = wrap.get();
                        }
                    } else {
                        this.g = false;
                    }
                    if (gVar.g == -128) {
                        this.f = true;
                    } else {
                        this.f = false;
                    }
                    gVar.f = bVar.f[7] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    bVar.a(gVar.f);
                    bVar.d = 0;
                    bVar.f = null;
                    if (gVar.f > 0) {
                        bVar.a = 1;
                        return b(fVar, eVar, wrap);
                    }
                    d.c().a((Object) new h());
                    d.c().d();
                    if (wrap.remaining() > 0) {
                        return b(fVar, eVar, wrap);
                    }
                }
            }
            return null;
        }
        while (bVar.a == 1 && wrap.remaining() > 0) {
            b = wrap.get();
            if (bVar.d == 0) {
                bVar.f = new byte[2];
            }
            bArr = bVar.f;
            i = bVar.d;
            bVar.d = i + 1;
            bArr[i] = b;
            if (bVar.d >= 2) {
                if (bVar.d == 2) {
                    bVar.e = com.igexin.a.a.b.g.b(bVar.f, 0);
                    bVar.f = null;
                    bVar.f = new byte[(bVar.e + 3)];
                    com.igexin.a.a.b.g.b(bVar.e, bVar.f, 0);
                } else if (bVar.d >= bVar.e + 3) {
                    b bVar2 = new b();
                    bVar2.a = bVar.e;
                    bVar2.b = bVar.f[2];
                    if (bVar2.a > 0) {
                        byte[] a;
                        bArr = new byte[bVar2.a];
                        com.igexin.a.a.b.g.a(bVar.f, 3, bArr, 0, bVar2.a);
                        if (this.g) {
                            a = a.a(bArr, this.e == null ? d.c().b() : com.igexin.a.b.a.a(this.e));
                        } else {
                            a = bArr;
                        }
                        if (this.f) {
                            a = com.igexin.a.a.b.g.b(a);
                        }
                        bVar2.a(a);
                    }
                    bVar.e = 0;
                    bVar.d = 0;
                    bVar.f = null;
                    if (this.b != null) {
                        d.c().a(this.b.c(fVar, eVar, bVar2));
                    }
                    bVar.b++;
                    if (bVar.b == bVar.c) {
                        bVar.a = 0;
                    }
                }
            }
        }
        if (bVar.b > 0) {
            d.c().d();
        }
        if (wrap.remaining() > 0) {
            return b(fVar, eVar, wrap);
        }
        return null;
    }

    public /* synthetic */ Object c(f fVar, e eVar, Object obj) {
        return b(fVar, eVar, obj);
    }
}
