package com.tencent.wxop.stat;

import com.douban.amonsul.StatConstant;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public final class b {
    private long K;
    private int L;
    private String M;
    private String c;
    private int g;

    public b() {
        this.K = 0;
        this.g = 0;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.L = 0;
        this.M = Table.STRING_DEFAULT_VALUE;
    }

    public final void a(long j) {
        this.K = j;
    }

    public final JSONObject i() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(StatConstant.JSON_KEY_EVENT_DATE, this.K);
            jSONObject.put("st", this.g);
            if (this.c != null) {
                jSONObject.put("dm", this.c);
            }
            jSONObject.put("pt", this.L);
            if (this.M != null) {
                jSONObject.put("rip", this.M);
            }
            jSONObject.put("ts", System.currentTimeMillis() / 1000);
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    public final void k(String str) {
        this.M = str;
    }

    public final void setDomain(String str) {
        this.c = str;
    }

    public final void setPort(int i) {
        this.L = i;
    }

    public final void setStatusCode(int i) {
        this.g = i;
    }
}
