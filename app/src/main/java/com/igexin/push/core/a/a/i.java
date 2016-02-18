package com.igexin.push.core.a.a;

import com.igexin.a.a.b.d;
import com.igexin.a.a.c.a;
import com.igexin.push.config.SDKUrlConfig;
import com.igexin.push.config.k;
import com.igexin.push.config.l;
import com.igexin.push.core.b;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.bean.e;
import com.igexin.push.core.g;
import com.igexin.push.e.a.c;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;

public class i implements a {
    private static final String a;

    static {
        a = k.a;
    }

    public b a(PushTaskBean pushTaskBean, BaseAction baseAction) {
        return b.success;
    }

    public BaseAction a(JSONObject jSONObject) {
        try {
            BaseAction baseAction = new BaseAction();
            baseAction.setType("reportext");
            baseAction.setActionId(jSONObject.getString("actionid"));
            baseAction.setDoActionId(jSONObject.getString("do"));
            return baseAction;
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean b(PushTaskBean pushTaskBean, BaseAction baseAction) {
        if (!(pushTaskBean == null || baseAction == null || l.t == null || l.t.b() == null)) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                File[] listFiles = new File(g.ad).listFiles();
                if (!(listFiles == null || listFiles.length == 0)) {
                    Map b = l.t.b();
                    a.b(a + "|DynamicConfig.extInfos");
                    boolean z = true;
                    for (Entry entry : b.entrySet()) {
                        boolean z2;
                        int intValue = ((Integer) entry.getKey()).intValue();
                        e eVar = (e) entry.getValue();
                        if (z) {
                            stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            stringBuilder.append("|");
                            stringBuilder.append(g.s);
                            stringBuilder.append("|");
                            stringBuilder.append(g.a);
                            stringBuilder.append("|");
                            stringBuilder.append(g.M);
                            stringBuilder.append("|");
                            z2 = false;
                        } else {
                            z2 = z;
                        }
                        for (File name : listFiles) {
                            if (name.getName().equals(eVar.c())) {
                                stringBuilder.append(intValue);
                                stringBuilder.append(",");
                                stringBuilder.append(eVar.b());
                                stringBuilder.append(",");
                                stringBuilder.append(eVar.c());
                                stringBuilder.append("|");
                            }
                        }
                        z = z2;
                    }
                    a.b(a + "check ext data : " + stringBuilder.toString());
                    if (stringBuilder.length() > 0) {
                        d.c().a(new c(new com.igexin.push.core.d.k(SDKUrlConfig.getStatServiceUrl(), stringBuilder.toString().getBytes(), 17)), false, true);
                    }
                }
            } catch (Exception e) {
            }
        }
        return true;
    }
}
