package com.igexin.sdk.a;

import android.content.Context;
import java.io.File;
import java.io.IOException;

public class b {
    private String a;

    public b(Context context) {
        if (context != null) {
            context.getFilesDir();
            this.a = "/data/data/" + context.getPackageName() + "/files/" + "init.pid";
        }
    }

    public void a() {
        if (!b() && this.a != null) {
            try {
                new File(this.a).createNewFile();
            } catch (IOException e) {
            }
        }
    }

    public boolean b() {
        return this.a != null ? new File(this.a).exists() : false;
    }
}
