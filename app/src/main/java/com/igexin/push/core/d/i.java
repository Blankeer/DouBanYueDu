package com.igexin.push.core.d;

import com.douban.amonsul.StatConstant;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.b.a;
import com.igexin.push.core.g;
import com.igexin.push.e.a.b;
import com.tencent.open.SocialConstants;
import org.json.JSONObject;

public class i extends b {
    public i(String str, String str2, boolean z) {
        super(str);
        a(str2, z);
    }

    private void a(String str, boolean z) {
        if (str != null && g.a != null && g.s != null) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(DoubanAccountOperationFragment_.ACTION_ARG, "alias_unbind");
                jSONObject.put("alias", str);
                jSONObject.put(SocialConstants.PARAM_APP_ID, g.a);
                if (z) {
                    jSONObject.put(StatConstant.JSON_KEY_CELLID, g.s);
                }
                b(a.b(jSONObject.toString().getBytes()));
                com.igexin.a.a.c.a.b("-> UnbindAliasHttpPlugin init jsonObject:" + jSONObject);
            } catch (Exception e) {
            }
        }
    }

    public void a(byte[] bArr) {
        if (bArr != null) {
            com.igexin.a.a.c.a.b("-> UnbindAliasHttpPlugin result:" + new String(a.c(bArr)));
            JSONObject jSONObject = new JSONObject();
            if (jSONObject.has("result")) {
                if (!"ok".equals(jSONObject.getString("result"))) {
                }
            }
        }
    }

    public int b() {
        return 0;
    }
}
