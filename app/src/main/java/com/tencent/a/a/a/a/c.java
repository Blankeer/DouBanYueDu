package com.tencent.a.a.a.a;

import android.util.Log;
import com.douban.amonsul.StatConstant;
import com.tencent.connect.common.Constants;
import org.json.JSONObject;

public final class c {
    String a;
    String b;
    String c;
    long d;

    public c() {
        this.a = null;
        this.b = null;
        this.c = Constants.VIA_RESULT_SUCCESS;
        this.d = 0;
    }

    static c c(String str) {
        c cVar = new c();
        if (h.d(str)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (!jSONObject.isNull(StatConstant.JSON_KEY_USERID)) {
                    cVar.a = jSONObject.getString(StatConstant.JSON_KEY_USERID);
                }
                if (!jSONObject.isNull("mc")) {
                    cVar.b = jSONObject.getString("mc");
                }
                if (!jSONObject.isNull("mid")) {
                    cVar.c = jSONObject.getString("mid");
                }
                if (!jSONObject.isNull("ts")) {
                    cVar.d = jSONObject.getLong("ts");
                }
            } catch (Throwable e) {
                Log.w("MID", e);
            }
        }
        return cVar;
    }

    private JSONObject d() {
        JSONObject jSONObject = new JSONObject();
        try {
            h.a(jSONObject, StatConstant.JSON_KEY_USERID, this.a);
            h.a(jSONObject, "mc", this.b);
            h.a(jSONObject, "mid", this.c);
            jSONObject.put("ts", this.d);
        } catch (Throwable e) {
            Log.w("MID", e);
        }
        return jSONObject;
    }

    public final String c() {
        return this.c;
    }

    public final String toString() {
        return d().toString();
    }
}
