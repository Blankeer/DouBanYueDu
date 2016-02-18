package com.igexin.push.core.a.a;

import com.igexin.push.core.a.e;
import com.igexin.push.core.b;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.c.f;
import com.tencent.open.SocialConstants;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public class d implements a {
    public b a(PushTaskBean pushTaskBean, BaseAction baseAction) {
        return b.success;
    }

    public BaseAction a(JSONObject jSONObject) {
        try {
            if (jSONObject.has("do") && jSONObject.has("actionid")) {
                BaseAction baseAction = new BaseAction();
                baseAction.setType(jSONObject.getString(SocialConstants.PARAM_TYPE));
                baseAction.setActionId(jSONObject.getString("actionid"));
                baseAction.setDoActionId(jSONObject.getString("do"));
                return baseAction;
            }
        } catch (JSONException e) {
        }
        return null;
    }

    public boolean b(PushTaskBean pushTaskBean, BaseAction baseAction) {
        f.a().a(false);
        if (!baseAction.getDoActionId().equals(Table.STRING_DEFAULT_VALUE)) {
            e.a().a(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), baseAction.getDoActionId());
        }
        return true;
    }
}
