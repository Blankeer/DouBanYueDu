package com.igexin.push.core.e;

import android.content.Intent;
import com.igexin.push.core.g;
import com.igexin.sdk.PushActivity;
import java.util.HashMap;
import java.util.Map;

public class b {
    private static b b;
    private Map a;

    public b() {
        this.a = new HashMap();
    }

    public static b a() {
        if (b == null) {
            b = new b();
        }
        return b;
    }

    private void d(a aVar) {
        if (aVar != null) {
            this.a.put(aVar.a(), aVar);
        }
    }

    public a a(Long l) {
        return (a) this.a.get(l);
    }

    public void a(a aVar) {
        if (aVar != null) {
            d(aVar);
            Intent intent = new Intent(g.g, PushActivity.class);
            intent.putExtra("activityid", aVar.a());
            intent.setFlags(268435456);
            g.g.startActivity(intent);
        }
    }

    public void b(a aVar) {
        if (aVar != null) {
            aVar.i();
            c(aVar);
        }
    }

    public void c(a aVar) {
        if (aVar != null) {
            this.a.remove(aVar.a());
        }
    }
}
