package com.igexin.push.e.a;

import android.os.Process;
import com.igexin.a.a.c.a;
import com.igexin.a.a.d.d;
import com.igexin.download.Downloads;
import java.io.ByteArrayInputStream;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class c extends d {
    public b a;
    public HttpRequestBase b;
    public HttpClient c;

    public c(b bVar) {
        super(0);
        this.a = bVar;
    }

    public final void a_() {
        super.a_();
        Process.setThreadPriority(10);
        if (this.a != null && this.a.e != null) {
            this.c = new DefaultHttpClient();
            int i = 0;
            while (i < 3) {
                try {
                    HttpResponse execute;
                    if (this.a.f == null && this.a.g == null) {
                        HttpUriRequest httpGet = new HttpGet(URI.create(this.a.e));
                        this.b = httpGet;
                        execute = this.c.execute(httpGet);
                    } else {
                        Object httpPost = new HttpPost(URI.create(this.a.e));
                        this.b = httpPost;
                        if (this.a.g != null) {
                            httpPost.setEntity(new InputStreamEntity(this.a.g, this.a.h));
                        } else {
                            httpPost.setEntity(new InputStreamEntity(new ByteArrayInputStream(this.a.f), (long) this.a.f.length));
                        }
                        execute = this.c.execute(httpPost);
                    }
                    if (execute.getStatusLine().getStatusCode() == Downloads.STATUS_SUCCESS) {
                        this.a.a(EntityUtils.toByteArray(execute.getEntity()));
                        com.igexin.a.a.b.d.c().a((Object) this.a);
                        com.igexin.a.a.b.d.c().d();
                        return;
                    } else if (i == 2) {
                        throw new Exception("Http response code is : " + execute.getStatusLine().getStatusCode() + " try times = " + (i + 1));
                    } else {
                        i++;
                    }
                } catch (Exception e) {
                    a.b("HttpTask" + e.toString());
                    if (i == 2) {
                        this.a.a(this.M);
                    }
                }
            }
        }
    }

    public final int b() {
        return -2147483638;
    }

    public void d() {
        this.y = true;
    }

    protected void e() {
    }

    public void f() {
        super.f();
        if (this.b != null) {
            this.b.abort();
        }
        this.c = null;
    }
}
