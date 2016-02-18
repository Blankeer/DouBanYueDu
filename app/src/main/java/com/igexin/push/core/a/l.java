package com.igexin.push.core.a;

import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.push.c.c.n;
import com.igexin.push.config.k;
import com.igexin.push.core.g;
import java.util.Timer;
import org.json.JSONException;
import org.json.JSONObject;

public class l extends b {
    private static final String a;

    static {
        a = k.a;
    }

    public boolean a(Object obj, JSONObject jSONObject) {
        try {
            n nVar = (n) obj;
            if (jSONObject.has(DoubanAccountOperationFragment_.ACTION_ARG) && jSONObject.getString(DoubanAccountOperationFragment_.ACTION_ARG).equals("pushmessage")) {
                byte[] bArr = (nVar.f == null || !(nVar.f instanceof byte[])) ? null : (byte[]) nVar.f;
                String string = jSONObject.getString("taskid");
                if (g.ak.containsKey(string)) {
                    ((Timer) g.ak.get(string)).cancel();
                    g.ak.remove(string);
                }
                e.a().a(jSONObject, bArr, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
