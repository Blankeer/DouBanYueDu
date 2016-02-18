package com.igexin.push.core.a;

import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.push.config.a;
import com.igexin.push.config.k;
import com.igexin.push.config.l;
import org.json.JSONObject;

public class c extends b {
    private static final String a;

    static {
        a = k.a;
    }

    public boolean a(Object obj, JSONObject jSONObject) {
        try {
            if (jSONObject.has(DoubanAccountOperationFragment_.ACTION_ARG) && jSONObject.getString(DoubanAccountOperationFragment_.ACTION_ARG).equals("block_client") && jSONObject.has("duration")) {
                long j = jSONObject.getLong("duration") * 1000;
                long currentTimeMillis = System.currentTimeMillis();
                if (j != 0) {
                    l.c = j + currentTimeMillis;
                    a.a().e();
                    com.igexin.push.a.a.c.c().d();
                }
            }
        } catch (Exception e) {
        }
        return true;
    }
}
