package com.igexin.push.core.a.a;

import android.content.Intent;
import android.content.pm.PackageManager;
import com.igexin.a.a.c.a;
import com.igexin.push.core.a.e;
import com.igexin.push.core.b;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.bean.j;
import com.igexin.push.core.g;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public class k implements a {
    private static final String a;

    static {
        a = com.igexin.push.config.k.a;
    }

    public b a(PushTaskBean pushTaskBean, BaseAction baseAction) {
        return b.success;
    }

    public BaseAction a(JSONObject jSONObject) {
        try {
            BaseAction jVar = new j();
            jVar.setType("startapp");
            jVar.setActionId(jSONObject.getString("actionid"));
            jVar.setDoActionId(jSONObject.getString("do"));
            if (jSONObject.has("appstartupid")) {
                jVar.a(jSONObject.getJSONObject("appstartupid").getString(AbstractSpiCall.ANDROID_CLIENT_TYPE));
            }
            if (jSONObject.has("is_autostart")) {
                jVar.d(jSONObject.getString("is_autostart"));
            }
            if (jSONObject.has(SocialConstants.PARAM_APP_ID)) {
                jVar.b(jSONObject.getString(SocialConstants.PARAM_APP_ID));
            }
            if (!jSONObject.has("noinstall_action")) {
                return jVar;
            }
            jVar.c(jSONObject.getString("noinstall_action"));
            return jVar;
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean b(PushTaskBean pushTaskBean, BaseAction baseAction) {
        boolean z = false;
        if (!(pushTaskBean == null || baseAction == null)) {
            boolean z2;
            j jVar = (j) baseAction;
            PackageManager packageManager = g.g.getPackageManager();
            String b = jVar.b();
            if (b.equals(Table.STRING_DEFAULT_VALUE)) {
                b = g.a;
                z2 = true;
            } else {
                z2 = g.a.equals(jVar.b());
            }
            a.b("doStartApp|" + z2 + "|" + b);
            Intent launchIntentForPackage;
            if (z2) {
                try {
                    e.a().b(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), b, null);
                    if (((j) baseAction).d().equals("true")) {
                        launchIntentForPackage = packageManager.getLaunchIntentForPackage(g.e);
                        if (launchIntentForPackage == null) {
                            return false;
                        }
                        g.g.startActivity(launchIntentForPackage);
                    }
                    if (jVar.getDoActionId() != null) {
                        e.a().a(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), jVar.getDoActionId());
                    }
                } catch (Exception e) {
                }
            } else {
                e.a().b(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), b, null);
                if (!jVar.d().equals("true")) {
                    z = true;
                } else if (com.igexin.push.f.a.a(jVar.a())) {
                    launchIntentForPackage = packageManager.getLaunchIntentForPackage(((j) baseAction).a());
                    if (launchIntentForPackage == null) {
                        return false;
                    }
                    g.g.startActivity(launchIntentForPackage);
                    z = true;
                }
                if (z) {
                    if (jVar.getDoActionId() != null) {
                        e.a().a(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), jVar.getDoActionId());
                    }
                } else if (jVar.c() != null) {
                    e.a().a(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), jVar.c());
                }
            }
        }
        return true;
    }
}
