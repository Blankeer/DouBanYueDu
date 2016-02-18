package u.aly;

import io.fabric.sdk.android.services.network.HttpRequest;
import org.json.JSONObject;

/* compiled from: URequest */
public abstract class bz {
    protected static String a;
    protected static String b;
    protected String c;

    public abstract JSONObject a();

    public abstract String b();

    static {
        a = HttpRequest.METHOD_POST;
        b = HttpRequest.METHOD_GET;
    }

    protected String c() {
        return a;
    }

    public bz(String str) {
        this.c = str;
    }

    public void a(String str) {
        this.c = str;
    }

    public String d() {
        return this.c;
    }
}
