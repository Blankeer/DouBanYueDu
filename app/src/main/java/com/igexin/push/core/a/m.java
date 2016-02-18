package com.igexin.push.core.a;

import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.push.config.k;
import com.igexin.push.core.c.c;
import org.json.JSONException;
import org.json.JSONObject;

public class m extends b {
    private static final String a;

    static {
        a = k.a;
    }

    public boolean a(Object obj, JSONObject jSONObject) {
        try {
            if (jSONObject.has(DoubanAccountOperationFragment_.ACTION_ARG) && jSONObject.getString(DoubanAccountOperationFragment_.ACTION_ARG).equals("received")) {
                try {
                    if (c.a().a(Long.parseLong(jSONObject.getString(WorksListUri.KEY_ID)))) {
                        e.a().g();
                    }
                } catch (NumberFormatException e) {
                }
            }
        } catch (JSONException e2) {
        }
        return true;
    }
}
