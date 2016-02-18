package com.alipay.sdk.protocol;

import com.alipay.sdk.data.e;
import com.alipay.sdk.data.f;
import org.json.JSONObject;

public class c {
    public e a;
    public f b;
    public JSONObject c;

    public c(e eVar, f fVar) {
        this.a = eVar;
        this.b = fVar;
    }

    private e a() {
        return this.a;
    }

    private f b() {
        return this.b;
    }

    private JSONObject c() {
        return this.c;
    }

    public void a(JSONObject jSONObject) {
        this.c = jSONObject;
    }
}
