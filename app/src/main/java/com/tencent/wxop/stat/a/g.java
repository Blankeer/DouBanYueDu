package com.tencent.wxop.stat.a;

import android.content.Context;
import com.douban.amonsul.StatConstant;
import com.tencent.wxop.stat.b.l;
import com.tencent.wxop.stat.b.r;
import com.tencent.wxop.stat.f;
import org.json.JSONObject;

public final class g extends d {
    private static String a;
    private String aR;
    private String aS;

    static {
        a = null;
    }

    public g(Context context, int i, f fVar) {
        super(context, i, fVar);
        this.aR = null;
        this.aS = null;
        this.aR = com.tencent.wxop.stat.g.r(context).b();
        if (a == null) {
            a = l.C(context);
        }
    }

    public final e ac() {
        return e.NETWORK_MONITOR;
    }

    public final void b(String str) {
        this.aS = str;
    }

    public final boolean b(JSONObject jSONObject) {
        r.a(jSONObject, "op", a);
        r.a(jSONObject, StatConstant.JSON_KEY_COUNTRY, this.aR);
        jSONObject.put("sp", this.aS);
        return true;
    }
}
