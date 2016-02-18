package com.alipay.sdk.protocol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.alipay.sdk.cons.c;
import com.alipay.sdk.data.f;
import com.alipay.sdk.exception.NetErrorException;
import com.alipay.sdk.sys.b;
import com.alipay.sdk.tid.a;
import com.douban.book.reader.entity.Annotation.Column;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.json.JSONException;
import org.json.JSONObject;
import u.aly.dx;

public final class e {

    /* renamed from: com.alipay.sdk.protocol.e.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            a = new int[f.values().length];
            try {
                a[f.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                a[f.NOT_POP_TYPE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                a[f.POP_TYPE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                a[f.TID_REFRESH.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static g a(c cVar) throws NetErrorException {
        SQLiteDatabase writableDatabase;
        Throwable th;
        com.alipay.sdk.data.e eVar = cVar.a;
        f fVar = cVar.b;
        JSONObject jSONObject = cVar.c;
        if (jSONObject.has(c.c)) {
            g gVar = new g(eVar, fVar);
            gVar.a(cVar.c);
            return gVar;
        }
        if (jSONObject.has(SettingsJsonConstants.APP_STATUS_KEY)) {
            switch (AnonymousClass1.a[f.a(jSONObject.optString(SettingsJsonConstants.APP_STATUS_KEY)).ordinal()]) {
                case dx.b /*1*/:
                case dx.c /*2*/:
                case dx.d /*3*/:
                    gVar = new g(eVar, fVar);
                    gVar.a(jSONObject);
                    return gVar;
                case dx.e /*4*/:
                    Context context = b.a().a;
                    String a = com.alipay.sdk.util.b.a(context).a();
                    String b = com.alipay.sdk.util.b.a(context).b();
                    a aVar = new a(context);
                    try {
                        writableDatabase = aVar.getWritableDatabase();
                        try {
                            aVar.a(writableDatabase, a, b, Table.STRING_DEFAULT_VALUE, Table.STRING_DEFAULT_VALUE);
                            a.a(writableDatabase, a.c(a, b));
                            if (writableDatabase != null && writableDatabase.isOpen()) {
                                writableDatabase.close();
                            }
                        } catch (Exception e) {
                            writableDatabase.close();
                            aVar.close();
                            return null;
                        } catch (Throwable th2) {
                            th = th2;
                            writableDatabase.close();
                            throw th;
                        }
                    } catch (Exception e2) {
                        writableDatabase = null;
                        if (writableDatabase != null && writableDatabase.isOpen()) {
                            writableDatabase.close();
                        }
                        aVar.close();
                        return null;
                    } catch (Throwable th3) {
                        th = th3;
                        writableDatabase = null;
                        if (writableDatabase != null && writableDatabase.isOpen()) {
                            writableDatabase.close();
                        }
                        throw th;
                    }
                    aVar.close();
            }
        }
        return null;
    }

    private static void b(c cVar) throws NetErrorException {
        f fVar = cVar.b;
        JSONObject jSONObject = cVar.c;
        com.alipay.sdk.data.a aVar = cVar.a.a;
        com.alipay.sdk.data.a aVar2 = cVar.b.l;
        if (TextUtils.isEmpty(aVar2.c)) {
            aVar2.c = aVar.c;
        }
        if (TextUtils.isEmpty(aVar2.d)) {
            aVar2.d = aVar.d;
        }
        if (TextUtils.isEmpty(aVar2.b)) {
            aVar2.b = aVar.b;
        }
        if (TextUtils.isEmpty(aVar2.a)) {
            aVar2.a = aVar.a;
        }
        String str = SettingsJsonConstants.SESSION_KEY;
        JSONObject optJSONObject = jSONObject.optJSONObject("reflected_data");
        if (optJSONObject != null) {
            new StringBuilder("session = ").append(optJSONObject.optString(str, Table.STRING_DEFAULT_VALUE));
            cVar.b.i = optJSONObject;
        } else if (jSONObject.has(str)) {
            optJSONObject = new JSONObject();
            try {
                optJSONObject.put(str, jSONObject.optString(str));
                CharSequence charSequence = com.alipay.sdk.tid.b.a().a;
                if (!TextUtils.isEmpty(charSequence)) {
                    optJSONObject.put(com.alipay.sdk.cons.b.c, charSequence);
                }
                fVar.i = optJSONObject;
            } catch (JSONException e) {
            }
        }
        fVar.f = jSONObject.optString("end_code", Constants.VIA_RESULT_SUCCESS);
        fVar.j = jSONObject.optString(Column.USER_ID, Table.STRING_DEFAULT_VALUE);
        str = jSONObject.optString("result");
        try {
            str = URLDecoder.decode(jSONObject.optString("result"), HttpRequest.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e2) {
        }
        fVar.g = str;
        fVar.h = jSONObject.optString("memo", Table.STRING_DEFAULT_VALUE);
    }
}
