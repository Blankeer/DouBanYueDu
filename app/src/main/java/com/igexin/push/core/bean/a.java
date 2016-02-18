package com.igexin.push.core.bean;

import android.os.Build.VERSION;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.network.NetWorker;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.push.core.g;
import com.igexin.sdk.PushBuildConfig;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import org.json.JSONObject;

public class a {
    public String a;
    public String b;
    public String c;
    public String d;
    public String e;
    public String f;
    public String g;
    public String h;
    public String i;
    public String j;
    public String k;
    public long l;

    public a() {
        this.f = PushBuildConfig.sdk_conf_channelid;
        if (g.e != null) {
            this.f += ":" + g.e;
        }
        this.e = PushBuildConfig.sdk_conf_version;
        this.b = g.v;
        this.c = g.u;
        this.d = g.x;
        this.i = g.y;
        this.a = g.w;
        this.h = "ANDROID";
        this.j = AbstractSpiCall.ANDROID_CLIENT_TYPE + VERSION.RELEASE;
        this.k = "MDP";
        this.g = g.z;
        this.l = System.currentTimeMillis();
    }

    public static String a(a aVar) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("model", aVar.a == null ? Table.STRING_DEFAULT_VALUE : aVar.a);
        jSONObject.put("sim", aVar.b == null ? Table.STRING_DEFAULT_VALUE : aVar.b);
        jSONObject.put("imei", aVar.c == null ? Table.STRING_DEFAULT_VALUE : aVar.c);
        jSONObject.put(StatConstant.JSON_KEY_MAC, aVar.d == null ? Table.STRING_DEFAULT_VALUE : aVar.d);
        jSONObject.put(ShareRequestParam.REQ_PARAM_VERSION, aVar.e == null ? Table.STRING_DEFAULT_VALUE : aVar.e);
        jSONObject.put("channelid", aVar.f == null ? Table.STRING_DEFAULT_VALUE : aVar.f);
        jSONObject.put(SocialConstants.PARAM_TYPE, "ANDROID");
        jSONObject.put(SettingsJsonConstants.APP_KEY, aVar.k == null ? Table.STRING_DEFAULT_VALUE : aVar.k);
        jSONObject.put("deviceid", "ANDROID-" + (aVar.g == null ? Table.STRING_DEFAULT_VALUE : aVar.g));
        jSONObject.put("system_version", aVar.j == null ? Table.STRING_DEFAULT_VALUE : aVar.j);
        jSONObject.put("cell", aVar.i == null ? Table.STRING_DEFAULT_VALUE : aVar.i);
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(DoubanAccountOperationFragment_.ACTION_ARG, "addphoneinfo");
        jSONObject2.put(WorksListUri.KEY_ID, String.valueOf(aVar.l));
        jSONObject2.put(NetWorker.PARAM_KEY_APP_INFO, jSONObject);
        return jSONObject2.toString();
    }
}
