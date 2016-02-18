package com.igexin.push.core.d;

import com.douban.amonsul.StatConstant;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.igexin.a.b.a;
import com.igexin.push.core.g;
import com.igexin.push.e.a.b;
import io.fabric.sdk.android.services.network.HttpRequest;
import org.json.JSONObject;

public class k extends b {
    public k(String str, byte[] bArr, int i) {
        super(str);
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
        String str = new String(bArr);
    }

    public int b() {
        return 0;
    }
}
