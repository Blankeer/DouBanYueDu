package com.igexin.push.config;

import com.douban.book.reader.entity.Annotation.Type;
import com.igexin.a.a.c.a;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class n {
    private static final String a;

    static {
        a = k.a + "_IDCConfigParse";
    }

    public static void a(String str) {
        JSONObject jSONObject;
        a.b(a + " parse idc config data : " + str);
        try {
            jSONObject = new JSONObject(str);
        } catch (Exception e) {
            jSONObject = null;
        }
        if (jSONObject != null) {
            String[] a;
            if (jSONObject.has(Type.NOTE)) {
                try {
                    SDKUrlConfig.setLocation(jSONObject.getString(Type.NOTE));
                } catch (JSONException e2) {
                }
            }
            if (jSONObject.has("X1")) {
                a = a(jSONObject, "X1");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.XFR_ADDRESS_IPS = a;
                }
            }
            if (jSONObject.has("X2")) {
                a = a(jSONObject, "X2");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.XFR_ADDRESS_IPS_BAK = a;
                }
            }
            if (jSONObject.has("B")) {
                a = a(jSONObject, "B");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.BI_ADDRESS_IPS = a;
                }
            }
            if (jSONObject.has("C")) {
                a = a(jSONObject, "C");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.CONFIG_ADDRESS_IPS = a;
                }
            }
            if (jSONObject.has("S")) {
                a = a(jSONObject, "S");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.STATE_ADDRESS_IPS = a;
                }
            }
            if (jSONObject.has("LO")) {
                a = a(jSONObject, "LO");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.LOG_ADDRESS_IPS = a;
                }
            }
            if (jSONObject.has("A")) {
                a = a(jSONObject, "A");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.AMP_ADDRESS_IPS = a;
                }
            }
            if (jSONObject.has("LB")) {
                a = a(jSONObject, "LB");
                if (a != null && a.length > 0) {
                    SDKUrlConfig.LBS_ADDRESS_IPS = a;
                }
            }
            if (jSONObject.has("I")) {
                String[] a2 = a(jSONObject, "I");
                if (a2 != null && a2.length > 0) {
                    SDKUrlConfig.INC_ADDRESS_IPS = a2;
                }
            }
        }
    }

    private static String[] a(JSONObject jSONObject, String str) {
        try {
            JSONArray jSONArray = jSONObject.getJSONArray(str);
            int length = jSONArray.length();
            String[] strArr = new String[length];
            for (int i = 0; i < length; i++) {
                if (str.equals("X1") || str.equals("X2")) {
                    strArr[i] = "socket://" + jSONArray.getString(i);
                } else {
                    strArr[i] = "http://" + jSONArray.getString(i);
                }
            }
            return strArr;
        } catch (Exception e) {
            return null;
        }
    }
}
