package com.igexin.sdk.a;

import android.content.Context;
import java.io.File;
import java.io.IOException;

public class d {
    private String a;
    private String b;
    private Context c;

    public d(Context context) {
        if (context != null) {
            this.c = context;
            context.getFilesDir();
            this.a = "/data/data/" + context.getPackageName() + "/files/" + "run.pid";
            this.b = "/data/data/" + context.getPackageName() + "/files/" + "stop.lock";
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
        File file = null;
        File file2 = this.a != null ? new File(this.a) : null;
        if (this.b != null) {
            file = new File(this.b);
        }
        if (file2 != null && file2.exists()) {
            if (file != null && file.exists()) {
                file.delete();
            }
            return true;
        } else if (file == null || !file.exists() || !file.renameTo(new File(this.a))) {
            return false;
        } else {
            new c(this.c).a();
            return true;
        }
    }
}
