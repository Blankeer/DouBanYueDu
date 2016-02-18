package com.alipay.android.phone.mrpc.core.a;

import com.alipay.a.a.e;
import com.alipay.android.phone.mrpc.core.c;
import com.douban.book.reader.helper.AppUri;
import io.realm.internal.Table;
import java.lang.reflect.Type;
import org.json.JSONObject;

public final class d extends a {
    public d(Type type, byte[] bArr) {
        super(type, bArr);
    }

    public final Object a() {
        try {
            String str = new String(this.b);
            new StringBuilder("threadid = ").append(Thread.currentThread().getId()).append("; rpc response:  ").append(str);
            JSONObject jSONObject = new JSONObject(str);
            int i = jSONObject.getInt("resultStatus");
            if (i == AppUri.OPEN_URL) {
                return this.a == String.class ? jSONObject.optString("result") : e.a(jSONObject.optString("result"), this.a);
            } else {
                throw new c(Integer.valueOf(i), jSONObject.optString("tips"));
            }
        } catch (Exception e) {
            throw new c(Integer.valueOf(10), new StringBuilder("response  =").append(new String(this.b)).append(":").append(e).toString() == null ? Table.STRING_DEFAULT_VALUE : e.getMessage());
        }
    }
}
