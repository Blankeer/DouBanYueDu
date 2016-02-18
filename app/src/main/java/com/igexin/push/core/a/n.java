package com.igexin.push.core.a;

import android.content.Intent;
import android.text.TextUtils;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.a.b.d;
import com.igexin.a.a.c.a;
import com.igexin.push.config.SDKUrlConfig;
import com.igexin.push.config.k;
import com.igexin.push.config.l;
import com.igexin.push.core.c.w;
import com.igexin.push.core.c.z;
import com.igexin.push.core.d.f;
import com.igexin.push.core.g;
import com.igexin.push.e.a.c;
import com.igexin.sdk.PushBuildConfig;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class n extends b {
    private static final String a;

    static {
        a = k.a + "_RedirectServerAction";
    }

    private void a() {
        try {
            String[] idcConfigUrl = SDKUrlConfig.getIdcConfigUrl();
            if (idcConfigUrl == null || idcConfigUrl.length == 0) {
                a.b(a + "idc config is empty or null");
                return;
            }
            String str = idcConfigUrl[0];
            a.b(a + " start fetch idc config, url : " + str);
            d.c().a(new c(new f(str)), false, true);
        } catch (Exception e) {
            a.b(a + e.toString());
        }
    }

    private void a(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !str2.equals(str)) {
            a.b(a + "new location = " + str + "; pre location = " + str2 + ", send snl retire broadcast");
            Intent intent = new Intent();
            intent.setAction("com.igexin.sdk.action.snlretire");
            intent.putExtra("groupid", str2);
            intent.putExtra("branch", PushBuildConfig.sdk_conf_channelid);
            g.g.sendBroadcast(intent);
        }
    }

    public boolean a(Object obj, JSONObject jSONObject) {
        a.b(a + " redirect server resp data : " + jSONObject);
        try {
            if (jSONObject.has(DoubanAccountOperationFragment_.ACTION_ARG) && jSONObject.getString(DoubanAccountOperationFragment_.ACTION_ARG).equals("redirect_server")) {
                int i;
                long parseLong;
                String string = jSONObject.getString("delay");
                List arrayList = new ArrayList();
                JSONArray jSONArray = jSONObject.getJSONArray("address_list");
                a.b("redirect|" + string + "|" + jSONArray.toString());
                for (i = 0; i < jSONArray.length(); i++) {
                    String string2 = jSONArray.getString(i);
                    int indexOf = string2.indexOf(44);
                    if (indexOf > 0) {
                        String substring = string2.substring(0, indexOf);
                        string2 = string2.substring(indexOf + 1);
                        long currentTimeMillis = System.currentTimeMillis();
                        if (string2 != null) {
                            try {
                                long parseLong2 = Long.parseLong(string2);
                                z zVar = new z();
                                zVar.a = "socket://" + substring;
                                zVar.b = (parseLong2 * 1000) + currentTimeMillis;
                                arrayList.add(zVar);
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                }
                try {
                    parseLong = Long.parseLong(string) * 1000;
                } catch (NumberFormatException e2) {
                    parseLong = 0;
                }
                if (parseLong >= 0) {
                    g.D = parseLong;
                }
                if (jSONObject.has("loc") && jSONObject.has("conf")) {
                    try {
                        String string3 = jSONObject.getString("loc");
                        string = g.d;
                        SDKUrlConfig.setLocation(string3);
                        if (l.o) {
                            a(string3, string);
                        }
                        a.b(a + " set group id : " + g.d);
                        JSONArray jSONArray2 = jSONObject.getJSONArray("conf");
                        String[] strArr = new String[jSONArray2.length()];
                        for (i = 0; i < jSONArray2.length(); i++) {
                            strArr[i] = "http://" + jSONArray2.getString(i);
                        }
                        SDKUrlConfig.setIdcConfigUrl(strArr);
                    } catch (Exception e3) {
                        a.b(a + e3.toString());
                    }
                    if (g.aw == 0) {
                        a();
                    } else if (System.currentTimeMillis() - g.aw > 7200000) {
                        a();
                    } else {
                        a.b(a + " get idc cfg last time less than 2 hours return");
                    }
                }
                w.a(arrayList);
                com.igexin.push.core.f.a().i().e();
            }
        } catch (Exception e32) {
            a.b(a + e32.toString());
        }
        return true;
    }
}
