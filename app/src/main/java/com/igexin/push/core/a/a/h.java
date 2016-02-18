package com.igexin.push.core.a.a;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import com.douban.book.reader.helper.AppUri;
import com.igexin.a.a.c.a;
import com.igexin.push.config.k;
import com.igexin.push.core.b;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.g;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.SocialConstants;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

public class h implements a {
    public static HashMap a;
    private static final String b;

    static {
        b = k.a;
        a = new HashMap();
    }

    private PendingIntent a(String str, String str2, String str3, int i) {
        Intent intent = new Intent("com.igexin.sdk.action.doaction");
        intent.putExtra("taskid", str);
        intent.putExtra("messageid", str2);
        intent.putExtra(SocialConstants.PARAM_APP_ID, g.a);
        intent.putExtra("actionid", str3);
        intent.putExtra("accesstoken", g.au);
        intent.putExtra("notifID", i);
        return PendingIntent.getBroadcast(g.g, new Random().nextInt(AppUri.OPEN_URL), intent, 134217728);
    }

    public b a(PushTaskBean pushTaskBean, BaseAction baseAction) {
        return b.success;
    }

    public BaseAction a(JSONObject jSONObject) {
        try {
            BaseAction hVar = new com.igexin.push.core.bean.h();
            hVar.setType("notification");
            hVar.setActionId(jSONObject.getString("actionid"));
            hVar.setDoActionId(jSONObject.getString("do"));
            String string = jSONObject.getString(QzoneShare.SHARE_TO_QQ_TITLE);
            String string2 = jSONObject.getString("text");
            hVar.a(string);
            hVar.b(string2);
            if (jSONObject.has("logo") && !Table.STRING_DEFAULT_VALUE.equals(jSONObject.getString("logo"))) {
                string = jSONObject.getString("logo");
                if (string.lastIndexOf(".png") == -1 && string.lastIndexOf(".jpeg") == -1) {
                    string = "null";
                } else {
                    int indexOf = string.indexOf(".png");
                    if (indexOf == -1) {
                        indexOf = string.indexOf(".jpeg");
                    }
                    if (indexOf != -1) {
                        string = string.substring(0, indexOf);
                    }
                }
                hVar.c(string);
            }
            if (jSONObject.has("is_noclear")) {
                hVar.a(jSONObject.getBoolean("is_noclear"));
            }
            if (jSONObject.has("is_novibrate")) {
                hVar.b(jSONObject.getBoolean("is_novibrate"));
            }
            if (jSONObject.has("is_noring")) {
                hVar.c(jSONObject.getBoolean("is_noring"));
            }
            if (jSONObject.has("is_chklayout")) {
                hVar.d(jSONObject.getBoolean("is_chklayout"));
            }
            if (jSONObject.has("logo_url")) {
                hVar.d(jSONObject.getString("logo_url"));
            }
            if (jSONObject.has("banner_url")) {
                hVar.e(jSONObject.getString("banner_url"));
            }
            return hVar;
        } catch (JSONException e) {
            return null;
        }
    }

    public void a(String str, String str2, com.igexin.push.core.bean.h hVar) {
        int currentTimeMillis = (int) System.currentTimeMillis();
        g.aj.put(str, Integer.valueOf(currentTimeMillis));
        PendingIntent a = a(str, str2, hVar.getDoActionId(), currentTimeMillis);
        NotificationManager notificationManager = (NotificationManager) g.g.getSystemService("notification");
        Notification notification = new Notification();
        notification.tickerText = hVar.b();
        notification.defaults = 4;
        notification.ledARGB = -16711936;
        notification.ledOnMS = AppUri.OPEN_URL;
        notification.ledOffMS = 3000;
        notification.flags = 1;
        if (hVar.c()) {
            notification.flags |= 32;
        } else {
            notification.flags |= 16;
        }
        if (!hVar.e()) {
            notification.defaults |= 1;
        }
        if (!hVar.d()) {
            notification.defaults |= 2;
        }
        int identifier = g.g.getResources().getIdentifier("push", "drawable", g.e);
        if (hVar.f() == null) {
            if (identifier != 0) {
                notification.icon = identifier;
            } else {
                notification.icon = 17301651;
            }
        } else if ("null".equals(hVar.f())) {
            notification.icon = 17301651;
        } else if (hVar.f().startsWith("@")) {
            String f = hVar.f();
            if (f.substring(1, f.length()).endsWith(NotificationCompatApi21.CATEGORY_EMAIL)) {
                notification.icon = 17301647;
            } else {
                notification.icon = 17301651;
            }
        } else {
            identifier = g.g.getResources().getIdentifier(hVar.f(), "drawable", g.e);
            if (identifier != 0) {
                notification.icon = identifier;
            } else {
                notification.icon = 17301651;
            }
        }
        if ((hVar.h() == null && hVar.g() == null) || !hVar.i()) {
            notification.setLatestEventInfo(g.g, hVar.a(), hVar.b(), a);
            notificationManager.notify(currentTimeMillis, notification);
        }
    }

    public boolean b(PushTaskBean pushTaskBean, BaseAction baseAction) {
        if (!(pushTaskBean == null || baseAction == null || !(baseAction instanceof com.igexin.push.core.bean.h))) {
            com.igexin.push.core.bean.h hVar = (com.igexin.push.core.bean.h) baseAction;
            if (TextUtils.isEmpty(hVar.a()) || TextUtils.isEmpty(hVar.b())) {
                a.b(b + " title = " + hVar.a() + ", content = " + hVar.b() + ", is invalid, don't show");
            } else {
                a(pushTaskBean.getTaskId(), pushTaskBean.getMessageId(), hVar);
            }
        }
        return true;
    }
}
