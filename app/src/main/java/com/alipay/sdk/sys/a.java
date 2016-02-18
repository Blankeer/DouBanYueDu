package com.alipay.sdk.sys;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import com.douban.amonsul.StatConstant;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public final class a {
    private String a;
    private String b;

    public a(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            this.a = packageInfo.versionName;
            this.b = packageInfo.packageName;
        } catch (NameNotFoundException e) {
        }
    }

    public final String toString() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("appkey", com.alipay.sdk.cons.a.d);
            jSONObject.put("ty", "and_lite");
            jSONObject.put(StatConstant.JSON_KEY_SDK_VERSION, com.alipay.sdk.cons.a.g);
            jSONObject.put("an", this.b);
            jSONObject.put("av", this.a);
            return jSONObject.toString();
        } catch (JSONException e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }
}
