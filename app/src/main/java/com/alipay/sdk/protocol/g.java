package com.alipay.sdk.protocol;

import android.text.TextUtils;
import com.alipay.sdk.cons.c;
import com.alipay.sdk.data.e;
import com.alipay.sdk.data.f;
import com.douban.book.reader.fragment.IntroPageFragment_;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import org.json.JSONObject;

public final class g extends h {
    private int l;
    private boolean m;

    protected g(e eVar, f fVar) {
        super(eVar, fVar);
        this.m = false;
    }

    public final boolean a() {
        return this.l == 4 || this.l == 9;
    }

    public final int b() {
        return this.l;
    }

    public final String c() {
        return null;
    }

    private boolean d() {
        return this.m;
    }

    public final void a(JSONObject jSONObject) {
        int i = 0;
        super.a(jSONObject);
        if (jSONObject.has(c.c)) {
            JSONObject optJSONObject = jSONObject.optJSONObject(c.c);
            CharSequence optString = optJSONObject.optString(SocialConstants.PARAM_TYPE);
            this.k = Boolean.parseBoolean(optJSONObject.optString("oneTime"));
            if (TextUtils.equals(IntroPageFragment_.PAGE_ARG, optString)) {
                this.m = true;
                this.l = 9;
            } else if (TextUtils.equals("dialog", optString)) {
                this.l = 7;
                this.m = false;
            } else if (TextUtils.equals("toast", optString)) {
                b a = b.a(optJSONObject, c.d);
                this.l = 6;
                if (a != null) {
                    a[] a2 = a.a(a);
                    int length = a2.length;
                    while (i < length) {
                        a aVar = a2[i];
                        if (aVar == a.Confirm || aVar == a.Alert) {
                            this.l = 10;
                        }
                        i++;
                    }
                }
            } else if (!TextUtils.equals("confirm", optString)) {
                this.m = TextUtils.equals(optString, "fullscreen");
                this.l = 4;
            }
        } else if (f.a(jSONObject.optString(SettingsJsonConstants.APP_STATUS_KEY)) == f.POP_TYPE) {
            this.l = -10;
        } else {
            this.l = 8;
        }
    }
}
