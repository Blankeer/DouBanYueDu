package com.igexin.push.core.a.a;

import com.igexin.push.core.a.e;
import com.igexin.push.core.b;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public class g implements a {
    public b a(PushTaskBean pushTaskBean, BaseAction baseAction) {
        return b.success;
    }

    public BaseAction a(JSONObject jSONObject) {
        try {
            BaseAction gVar = new com.igexin.push.core.bean.g();
            gVar.setType("goto");
            gVar.setActionId(jSONObject.getString("actionid"));
            gVar.setDoActionId(jSONObject.getString("do"));
            return gVar;
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean b(PushTaskBean pushTaskBean, BaseAction baseAction) {
        if (!(pushTaskBean == null || baseAction == null || baseAction.getDoActionId() == null || baseAction.getDoActionId().equals(Table.STRING_DEFAULT_VALUE))) {
            e.a().a(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), baseAction.getDoActionId());
        }
        return true;
    }
}
