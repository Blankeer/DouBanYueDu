package com.igexin.push.core.d;

import com.douban.amonsul.StatConstant;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.b.a;
import com.igexin.push.core.g;
import com.tencent.open.SocialConstants;
import org.json.JSONObject;

public class b extends com.igexin.push.e.a.b {
    public b(String str, String str2) {
        super(str);
        a(str2);
    }

    public void a(String str) {
        if (str != null && g.a != null && g.s != null) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(DoubanAccountOperationFragment_.ACTION_ARG, "alias_bind");
                jSONObject.put("alias", str);
                jSONObject.put(SocialConstants.PARAM_APP_ID, g.a);
                jSONObject.put(StatConstant.JSON_KEY_CELLID, g.s);
                b(a.b(jSONObject.toString().getBytes()));
                com.igexin.a.a.c.a.b("-> BindAliasHttpPlugin init jsonObject:" + jSONObject);
            } catch (Exception e) {
            }
        }
    }

    public void a(byte[] bArr) {
        if (bArr != null) {
            com.igexin.a.a.c.a.b("-> BindAlias result::" + new String(a.c(bArr)));
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
