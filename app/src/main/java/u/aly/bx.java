package u.aly;

import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import org.json.JSONObject;

/* compiled from: ReportResponse */
public class bx extends ca {
    public a a;

    /* compiled from: ReportResponse */
    public enum a {
        SUCCESS,
        FAIL
    }

    public bx(JSONObject jSONObject) {
        super(jSONObject);
        if ("ok".equalsIgnoreCase(jSONObject.optString(SettingsJsonConstants.APP_STATUS_KEY)) || "ok".equalsIgnoreCase(jSONObject.optString("success"))) {
            this.a = a.SUCCESS;
        } else {
            this.a = a.FAIL;
        }
    }
}
