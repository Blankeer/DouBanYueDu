package com.igexin.push.core.d;

import com.douban.amonsul.StatConstant;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.b.a;
import com.igexin.push.core.a.e;
import com.igexin.push.e.a.b;
import com.igexin.sdk.PushBuildConfig;
import com.tencent.open.SocialConstants;
import org.json.JSONObject;

public class g extends b {
    public g(String str) {
        super(str);
        a();
    }

    public void a() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(DoubanAccountOperationFragment_.ACTION_ARG, "sdkconfig");
            jSONObject.put(StatConstant.JSON_KEY_CELLID, com.igexin.push.core.g.s);
            jSONObject.put(SocialConstants.PARAM_APP_ID, com.igexin.push.core.g.a);
            jSONObject.put("sdk_version", PushBuildConfig.sdk_conf_version);
            b(a.b(jSONObject.toString().getBytes()));
        } catch (Exception e) {
        }
    }

    public void a(byte[] bArr) {
        e.a().a(bArr);
    }

    public int b() {
        return 0;
    }
}
