package com.igexin.push.core.a.a;

import android.os.Process;
import com.igexin.a.a.c.a;
import com.igexin.push.config.k;
import com.igexin.push.config.l;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.bean.e;
import com.igexin.push.f.b;
import io.realm.internal.Table;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class c implements a {
    private static final String a;

    static {
        a = k.a;
    }

    private boolean a(e eVar) {
        String c = eVar.c();
        if (c == null) {
            return false;
        }
        b.a(c);
        return true;
    }

    public com.igexin.push.core.b a(PushTaskBean pushTaskBean, BaseAction baseAction) {
        return com.igexin.push.core.b.success;
    }

    public BaseAction a(JSONObject jSONObject) {
        if (jSONObject.has("ids")) {
            try {
                JSONArray jSONArray = new JSONArray(jSONObject.getString("ids"));
                if (jSONArray != null && jSONArray.length() > 0) {
                    int[] iArr = new int[jSONArray.length()];
                    for (int i = 0; i < jSONArray.length(); i++) {
                        iArr[i] = jSONArray.getInt(i);
                    }
                    BaseAction cVar = new com.igexin.push.core.bean.c();
                    cVar.setType("cleanext");
                    cVar.a(iArr);
                    cVar.setActionId(jSONObject.getString("actionid"));
                    cVar.setDoActionId(jSONObject.getString("do"));
                    return cVar;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean b(PushTaskBean pushTaskBean, BaseAction baseAction) {
        boolean z = false;
        if (l.t == null || l.t.b() == null || l.t.b().size() == 0) {
            return false;
        }
        if (!(pushTaskBean == null || baseAction == null)) {
            boolean z2;
            com.igexin.push.core.bean.c cVar = (com.igexin.push.core.bean.c) baseAction;
            Map b = l.t.b();
            Object a = cVar.a();
            if (a == null || a.length <= 0) {
                z2 = false;
            } else {
                a.b("cleanext|" + a.toString());
                int i = 0;
                z2 = false;
                while (i < cVar.a().length) {
                    boolean z3;
                    if (b.containsKey(Integer.valueOf(a[i]))) {
                        a((e) b.get(Integer.valueOf(a[i])));
                        b.remove(Integer.valueOf(a[i]));
                        z2 = true;
                        z3 = true;
                    } else {
                        z3 = z2;
                        z2 = z;
                    }
                    i++;
                    z = z2;
                    z2 = z3;
                }
                if (z) {
                    com.igexin.push.config.a.a().g();
                }
            }
            if (z2) {
                Process.killProcess(Process.myPid());
            }
        }
        if (baseAction.getDoActionId().equals(Table.STRING_DEFAULT_VALUE)) {
            return true;
        }
        com.igexin.push.core.a.e.a().a(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), baseAction.getDoActionId());
        return true;
    }
}
