package com.igexin.push.core.a;

import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.push.config.k;
import com.igexin.push.core.b.e;
import com.igexin.push.core.b.h;
import com.igexin.push.core.c.f;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class p extends b {
    private static final String a;

    static {
        a = k.a;
    }

    public boolean a(Object obj, JSONObject jSONObject) {
        try {
            if (jSONObject.has(DoubanAccountOperationFragment_.ACTION_ARG) && jSONObject.getString(DoubanAccountOperationFragment_.ACTION_ARG).equals("response_ca_list")) {
                JSONArray jSONArray = jSONObject.getJSONArray("ca_list");
                Map hashMap = new HashMap();
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                    h hVar = new h();
                    hVar.a(jSONObject2.getString("pkgname"));
                    hVar.c(jSONObject2.getString("signature"));
                    hVar.a(e.a().c(jSONObject2.getString("permissions")));
                    hashMap.put(hVar.a(), hVar);
                }
                f.a().f(System.currentTimeMillis());
                if (hashMap.size() > 0) {
                    e.a().a(hashMap);
                }
            }
        } catch (JSONException e) {
        }
        return true;
    }
}
