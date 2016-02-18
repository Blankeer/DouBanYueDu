package u.aly;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import com.douban.book.reader.network.ConnectionUtils;
import com.igexin.download.Downloads;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.a;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/* compiled from: NetworkHelper */
public class t {
    private String a;
    private String b;
    private int c;
    private Context d;
    private r e;

    public t(Context context) {
        this.b = "10.0.0.172";
        this.c = 80;
        this.d = context;
        this.a = a(context);
    }

    public void a(r rVar) {
        this.e = rVar;
    }

    public byte[] a(byte[] bArr) {
        byte[] bArr2 = null;
        for (String a : a.f) {
            bArr2 = a(bArr, a);
            if (bArr2 != null) {
                if (this.e != null) {
                    this.e.c();
                }
                return bArr2;
            }
            if (this.e != null) {
                this.e.d();
            }
        }
        return bArr2;
    }

    private boolean a() {
        if (this.d.getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE", this.d.getPackageName()) != 0) {
            return false;
        }
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.d.getSystemService("connectivity")).getActiveNetworkInfo();
            if (!(activeNetworkInfo == null || activeNetworkInfo.getType() == 1)) {
                String extraInfo = activeNetworkInfo.getExtraInfo();
                if (extraInfo != null && (extraInfo.equals("cmwap") || extraInfo.equals("3gwap") || extraInfo.equals("uniwap"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] a(byte[] bArr, String str) {
        HttpUriRequest httpPost = new HttpPost(str);
        HttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, AbstractSpiCall.DEFAULT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(basicHttpParams, ConnectionUtils.CONNECTION_TIMEOUT);
        HttpClient defaultHttpClient = new DefaultHttpClient(basicHttpParams);
        httpPost.addHeader("X-Umeng-UTC", String.valueOf(System.currentTimeMillis()));
        httpPost.addHeader("X-Umeng-Sdk", this.a);
        httpPost.addHeader("Msg-Type", "envelope");
        InputStream content;
        try {
            if (a()) {
                defaultHttpClient.getParams().setParameter("http.route.default-proxy", new HttpHost(this.b, this.c));
            }
            httpPost.setEntity(new InputStreamEntity(new ByteArrayInputStream(bArr), (long) bArr.length));
            if (this.e != null) {
                this.e.a();
            }
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            if (this.e != null) {
                this.e.b();
            }
            int statusCode = execute.getStatusLine().getStatusCode();
            boolean a = cf.a(execute.getFirstHeader(HttpRequest.HEADER_CONTENT_TYPE), "application/thrift");
            bt.c(a.e, "status code : " + statusCode);
            if (statusCode != Downloads.STATUS_SUCCESS || !a) {
                return null;
            }
            bt.a(a.e, "Send message to " + str);
            HttpEntity entity = execute.getEntity();
            if (entity == null) {
                return null;
            }
            content = entity.getContent();
            byte[] b = cf.b(content);
            cf.c(content);
            return b;
        } catch (Exception e) {
            bt.b(a.e, "ClientProtocolException,Failed to send message.", e);
            return null;
        } catch (Exception e2) {
            bt.b(a.e, "IOException,Failed to send message.", e2);
            return null;
        } catch (Throwable th) {
            cf.c(content);
        }
    }

    private String a(Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(a.b);
        stringBuffer.append("/");
        stringBuffer.append(a.c);
        stringBuffer.append(" ");
        try {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(bs.v(context));
            stringBuffer2.append("/");
            stringBuffer2.append(bs.d(context));
            stringBuffer2.append(" ");
            stringBuffer2.append(Build.MODEL);
            stringBuffer2.append("/");
            stringBuffer2.append(VERSION.RELEASE);
            stringBuffer2.append(" ");
            stringBuffer2.append(cf.a(AnalyticsConfig.getAppkey(context)));
            stringBuffer.append(URLEncoder.encode(stringBuffer2.toString(), HttpRequest.CHARSET_UTF8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
