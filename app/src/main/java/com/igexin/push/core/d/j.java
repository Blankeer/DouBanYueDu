package com.igexin.push.core.d;

import android.content.ContentValues;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.b.a;
import com.igexin.push.core.f;
import com.igexin.push.core.g;
import com.igexin.push.e.a.b;
import com.tencent.connect.common.Constants;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.network.HttpRequest;
import org.json.JSONObject;

public class j extends b {
    private boolean a;

    public j(String str, byte[] bArr, int i, boolean z) {
        super(str);
        this.a = false;
        this.a = z;
        a(bArr, i);
    }

    private void a(byte[] bArr, int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(DoubanAccountOperationFragment_.ACTION_ARG, "upload_BI");
            jSONObject.put("BIType", String.valueOf(i));
            jSONObject.put(StatConstant.JSON_KEY_CELLID, g.s);
            jSONObject.put("BIData", new String(com.igexin.a.a.b.g.e(bArr, 0), HttpRequest.CHARSET_UTF8));
            b(a.b(jSONObject.toString().getBytes()));
        } catch (Exception e) {
        }
    }

    public void a(byte[] bArr) {
        JSONObject jSONObject = new JSONObject(new String(bArr));
        if (this.a && jSONObject.has("result") && jSONObject.getString("result").equals("ok")) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SocialConstants.PARAM_TYPE, Constants.VIA_RESULT_SUCCESS);
            f.a().k().a("bi", contentValues, new String[]{SocialConstants.PARAM_TYPE}, new String[]{Constants.VIA_SSO_LOGIN});
            com.igexin.push.core.c.f.a().c(System.currentTimeMillis());
        }
    }

    public int b() {
        return 0;
    }
}
