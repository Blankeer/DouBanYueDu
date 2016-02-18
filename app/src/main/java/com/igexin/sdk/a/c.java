package com.igexin.sdk.a;

import android.content.Context;
import java.io.File;
import java.io.IOException;

public class c {
    private String a;

    public c(Context context) {
        if (context != null) {
            context.getFilesDir();
            this.a = "/data/data/" + context.getPackageName() + "/files/" + "push.pid";
        }
    }

    public void a() {
        if (!c() && this.a != null) {
            try {
                new File(this.a).createNewFile();
            } catch (IOException e) {
            }
        }
    }

    public void b() {
        if (c() && this.a != null) {
            new File(this.a).delete();
        }
    }

    public boolean c() {
        return this.a != null ? new File(this.a).exists() : false;
    }
}
