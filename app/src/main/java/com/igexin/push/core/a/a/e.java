package com.igexin.push.core.a.a;

import com.igexin.push.core.b;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.bean.d;
import com.igexin.push.core.c.f;
import com.tencent.open.SocialConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class e implements a {
    public b a(PushTaskBean pushTaskBean, BaseAction baseAction) {
        return b.success;
    }

    public BaseAction a(JSONObject jSONObject) {
        try {
            if (jSONObject.has("do") && jSONObject.has("actionid") && jSONObject.has("duration")) {
                BaseAction dVar = new d();
                dVar.setType(jSONObject.getString(SocialConstants.PARAM_TYPE));
                dVar.setActionId(jSONObject.getString("actionid"));
                dVar.setDoActionId(jSONObject.getString("do"));
                if (!jSONObject.has("duration")) {
                    return dVar;
                }
                dVar.a(Long.valueOf(jSONObject.getString("duration")).longValue());
                return dVar;
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public boolean b(PushTaskBean pushTaskBean, BaseAction baseAction) {
        long currentTimeMillis = System.currentTimeMillis() + (((d) baseAction).a() * 1000);
        f.a().a(true);
        f.a().h(currentTimeMillis);
        return true;
    }
}
