package com.tencent.wxop.stat.b;

import com.douban.amonsul.StatConstant;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.connect.common.Constants;
import org.json.JSONException;
import org.json.JSONObject;

public final class c {
    private String W;
    private String a;
    private String b;
    private int bf;
    private String c;
    private int cu;
    private long cv;

    public c() {
        this.a = null;
        this.b = null;
        this.c = null;
        this.W = Constants.VIA_RESULT_SUCCESS;
        this.bf = 0;
        this.cv = 0;
    }

    public c(String str, String str2, int i) {
        this.a = null;
        this.b = null;
        this.c = null;
        this.W = Constants.VIA_RESULT_SUCCESS;
        this.bf = 0;
        this.cv = 0;
        this.a = str;
        this.b = str2;
        this.cu = i;
    }

    private JSONObject aq() {
        JSONObject jSONObject = new JSONObject();
        try {
            r.a(jSONObject, StatConstant.JSON_KEY_USERID, this.a);
            r.a(jSONObject, "mc", this.b);
            r.a(jSONObject, "mid", this.W);
            r.a(jSONObject, ShareRequestParam.REQ_PARAM_AID, this.c);
            jSONObject.put("ts", this.cv);
            jSONObject.put("ver", this.bf);
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    public final String ar() {
        return this.b;
    }

    public final int as() {
        return this.cu;
    }

    public final String b() {
        return this.a;
    }

    public final String toString() {
        return aq().toString();
    }

    public final void z() {
        this.cu = 1;
    }
}
