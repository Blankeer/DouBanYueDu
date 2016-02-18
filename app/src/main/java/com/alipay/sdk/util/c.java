package com.alipay.sdk.util;

import com.alipay.sdk.cons.a;
import com.alipay.sdk.encrypt.d;
import com.alipay.sdk.encrypt.e;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public final class c {
    public static JSONObject a(JSONObject jSONObject, JSONObject jSONObject2) {
        JSONObject jSONObject3 = new JSONObject();
        try {
            JSONObject[] jSONObjectArr = new JSONObject[]{jSONObject, jSONObject2};
            for (int i = 0; i < 2; i++) {
                JSONObject jSONObject4 = jSONObjectArr[i];
                if (jSONObject4 != null) {
                    Iterator keys = jSONObject4.keys();
                    while (keys.hasNext()) {
                        String str = (String) keys.next();
                        jSONObject3.put(str, jSONObject4.get(str));
                    }
                }
            }
        } catch (JSONException e) {
        }
        return jSONObject3;
    }

    private static String a(String str, String str2) {
        String a = d.a(str, a.c);
        String a2 = e.a(str, str2);
        return String.format(Locale.getDefault(), "%08X%s%08X%s", new Object[]{Integer.valueOf(a.length()), a, Integer.valueOf(a2.length()), a2});
    }
}
