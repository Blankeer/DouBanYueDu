package com.igexin.sdk.a;

import android.content.Context;
import com.igexin.sdk.IPushCore;

public class a {
    private static String a;
    private static a c;
    private IPushCore b;

    static {
        a = "PushSdk";
    }

    private a() {
    }

    public static a a() {
        if (c == null) {
            c = new a();
        }
        return c;
    }

    public void a(IPushCore iPushCore) {
        this.b = iPushCore;
    }

    public boolean a(Context context) {
        try {
            IPushCore iPushCore = (IPushCore) context.getClassLoader().loadClass("com.igexin.push.core.stub.PushCore").newInstance();
            if (iPushCore != null) {
                a(iPushCore);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public IPushCore b() {
        return this.b;
    }
}
