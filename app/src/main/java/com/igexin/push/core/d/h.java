package com.igexin.push.core.d;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.a.a.c.a;
import com.igexin.download.Downloads;
import com.igexin.push.core.bean.e;
import com.igexin.push.core.g;
import com.igexin.push.f.b;
import com.igexin.sdk.PushService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class h implements Runnable {
    private Context a;
    private e b;
    private boolean c;
    private int d;

    public h(Context context, e eVar, boolean z) {
        this.a = context;
        this.b = eVar;
        this.c = z;
    }

    protected boolean a(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str2)) {
            a.b("HttpExtensionDownload downLoad ext name is invalid name = " + str2);
            return false;
        } else if (TextUtils.isEmpty(str)) {
            this.d = 3;
            com.igexin.push.core.a.e.a().d("url is invalid");
            a.b("HttpExtensionDownload downLoad ext url is invalid, url = " + str);
            return false;
        } else if (str.startsWith("http://") || str.startsWith("https://")) {
            Process.setThreadPriority(10);
            try {
                HttpResponse execute = new DefaultHttpClient().execute(new HttpGet(str));
                if (execute.getStatusLine().getStatusCode() != Downloads.STATUS_SUCCESS) {
                    this.d++;
                    return false;
                }
                InputStream content = execute.getEntity().getContent();
                String str4 = g.ad + "/" + str2;
                File file = new File(str4);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                while (true) {
                    int read = content.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
                fileOutputStream.close();
                content.close();
                if (com.igexin.a.b.a.a(this.a, str4).equals(str3)) {
                    File file2 = new File(g.ad + "/" + this.b.c());
                    file.renameTo(file2);
                    if (this.b.g() || this.b.h() != 0) {
                        a.b("HttpExtensionDownload downLoadFile success do not copy ext to local tmp name = " + this.b.c());
                    } else {
                        File file3 = new File(g.ac + "/" + this.b.c());
                        if (!file3.exists()) {
                            a.b("HttpExtensionDownload downLoadFile success cope ext to local tmp name = " + this.b.c());
                            b.a(file2, file3, this.b.f());
                        } else if (!com.igexin.a.b.a.a(g.g, file3.getAbsolutePath()).equals(this.b.f())) {
                            file3.delete();
                            b.a(file2, file3, this.b.f());
                            a.b("HttpExtensionDownload downLoadFile success delete local tmp and copy ext name = " + this.b.c());
                        }
                    }
                    return true;
                }
                a.b("HttpExtensionDownload download ext failed CheckSum error name = " + this.b.c());
                if (file.exists()) {
                    file.delete();
                }
                this.d = 4;
                return false;
            } catch (IllegalArgumentException e) {
                this.d = 3;
                com.igexin.push.core.a.e.a().d(e.toString());
                a.b("HttpExtensionDownload" + e.toString());
                return false;
            } catch (Exception e2) {
                this.d++;
                a.b("HttpExtensionDownload" + e2.toString());
                return false;
            }
        } else {
            this.d = 3;
            com.igexin.push.core.a.e.a().d("httpUrl : " + str + " is invalid ...");
            a.b("HttpExtensionDownload downLoad ext url is invalid url = " + str);
            return false;
        }
    }

    public void run() {
        Intent intent;
        do {
            a.b("HttpExtensionDownload downloading " + this.b.c() + ".tmp");
            if (a(this.b.e(), this.b.c() + ".tmp", this.b.f())) {
                a.b("HttpExtensionDownload download " + this.b.c() + ".tmp, success ########");
                intent = new Intent(this.a, PushService.class);
                intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "com.igexin.sdk.action.extdownloadsuccess");
                intent.putExtra(WorksListUri.KEY_ID, this.b.a());
                intent.putExtra("result", true);
                intent.putExtra("isReload", this.c);
                this.a.startService(intent);
                return;
            }
            a.b("HttpExtensionDownload download ext failed name = " + this.b.c() + " downloadFailedTimes = " + this.d);
        } while (this.d < 3);
        intent = new Intent(this.a, PushService.class);
        intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "com.igexin.sdk.action.extdownloadsuccess");
        intent.putExtra(WorksListUri.KEY_ID, this.b.a());
        intent.putExtra("result", false);
        this.a.startService(intent);
    }
}
