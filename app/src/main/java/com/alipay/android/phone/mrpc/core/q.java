package com.alipay.android.phone.mrpc.core;

import com.alipay.sdk.protocol.h;
import com.douban.book.reader.fragment.share.ShareGiftEditFragment_;
import com.douban.book.reader.helper.WorksListUri;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public final class q extends d {
    private n g;

    public q(n nVar, Method method, int i, String str, byte[] bArr, boolean z) {
        super(method, i, str, bArr, HttpRequest.CONTENT_TYPE_FORM, z);
        this.g = nVar;
    }

    public final Object a() {
        Throwable e;
        aa vVar = new v(this.g.a());
        vVar.b = this.b;
        vVar.c = this.e;
        vVar.e = this.f;
        vVar.a(WorksListUri.KEY_ID, String.valueOf(this.d));
        vVar.a("operationType", this.c);
        vVar.a(HttpRequest.ENCODING_GZIP, String.valueOf(this.g.d()));
        vVar.a(new BasicHeader(ShareGiftEditFragment_.UUID_ARG, UUID.randomUUID().toString()));
        List<Header> list = this.g.c().b;
        if (!(list == null || list.isEmpty())) {
            for (Header a : list) {
                vVar.a(a);
            }
        }
        new StringBuilder("threadid = ").append(Thread.currentThread().getId()).append("; ").append(vVar.toString());
        try {
            ab abVar = (ab) this.g.b().a(vVar).get();
            if (abVar != null) {
                return abVar.a();
            }
            throw new c(Integer.valueOf(9), "response is null");
        } catch (Throwable e2) {
            throw new c(Integer.valueOf(13), Table.STRING_DEFAULT_VALUE, e2);
        } catch (Throwable e22) {
            Throwable th = e22;
            e22 = th.getCause();
            if (e22 == null || !(e22 instanceof a)) {
                throw new c(Integer.valueOf(9), Table.STRING_DEFAULT_VALUE, th);
            }
            a aVar = (a) e22;
            int i = aVar.k;
            switch (i) {
                case dx.b /*1*/:
                    i = 2;
                    break;
                case dx.c /*2*/:
                    i = 3;
                    break;
                case dx.d /*3*/:
                    i = 4;
                    break;
                case dx.e /*4*/:
                    i = 5;
                    break;
                case dj.f /*5*/:
                    i = 6;
                    break;
                case ci.g /*6*/:
                    i = 7;
                    break;
                case ci.h /*7*/:
                    i = 8;
                    break;
                case h.g /*8*/:
                    i = 15;
                    break;
                case h.h /*9*/:
                    i = 16;
                    break;
            }
            throw new c(Integer.valueOf(i), aVar.l);
        } catch (Throwable e222) {
            throw new c(Integer.valueOf(13), Table.STRING_DEFAULT_VALUE, e222);
        }
    }
}
