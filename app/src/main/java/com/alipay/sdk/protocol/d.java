package com.alipay.sdk.protocol;

import android.text.TextUtils;
import com.alipay.sdk.data.a;
import com.alipay.sdk.data.f;
import com.alipay.sdk.exception.NetErrorException;
import com.alipay.sdk.tid.b;
import com.douban.book.reader.entity.Annotation.Column;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.json.JSONException;
import org.json.JSONObject;

public final class d {
    private static c a(c cVar) throws NetErrorException {
        e eVar = new e();
        c a = e.a(cVar);
        if (a != null) {
            cVar = a;
        }
        f fVar = cVar.b;
        JSONObject jSONObject = cVar.c;
        a aVar = cVar.a.a;
        a aVar2 = cVar.b.l;
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
                CharSequence charSequence = b.a().a;
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
        return cVar;
    }
}
