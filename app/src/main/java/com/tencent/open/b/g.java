package com.tencent.open.b;

import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.helper.AppUri;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.utils.AidTask;
import com.tencent.connect.common.Constants;
import com.tencent.open.SocialConstants;
import com.tencent.open.a.f;
import com.tencent.open.utils.Global;
import com.tencent.open.utils.OpenConfig;
import com.tencent.open.utils.ThreadManager;
import com.tencent.open.utils.Util;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.realm.internal.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import org.json.JSONArray;
import org.json.JSONObject;

/* compiled from: ProGuard */
public class g {
    protected static g a;
    protected Random b;
    protected List<Serializable> c;
    protected List<Serializable> d;
    protected HandlerThread e;
    protected Handler f;
    protected Executor g;
    protected Executor h;

    /* compiled from: ProGuard */
    /* renamed from: com.tencent.open.b.g.1 */
    class AnonymousClass1 extends Handler {
        final /* synthetic */ g a;

        AnonymousClass1(g gVar, Looper looper) {
            this.a = gVar;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case AppUri.OPEN_URL /*1000*/:
                    this.a.b();
                    break;
                case AidTask.WHAT_LOAD_AID_SUC /*1001*/:
                    this.a.e();
                    break;
            }
            super.handleMessage(message);
        }
    }

    /* compiled from: ProGuard */
    /* renamed from: com.tencent.open.b.g.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Bundle a;
        final /* synthetic */ boolean b;
        final /* synthetic */ g c;

        AnonymousClass2(g gVar, Bundle bundle, boolean z) {
            this.c = gVar;
            this.a = bundle;
            this.b = z;
        }

        public void run() {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("uin", Constants.DEFAULT_UIN);
                bundle.putString("imei", c.b(Global.getContext()));
                bundle.putString("imsi", c.c(Global.getContext()));
                bundle.putString("android_id", c.d(Global.getContext()));
                bundle.putString(StatConstant.JSON_KEY_MAC, c.a());
                bundle.putString(Constants.PARAM_PLATFORM, Constants.VIA_TO_TYPE_QQ_GROUP);
                bundle.putString("os_ver", VERSION.RELEASE);
                bundle.putString("position", Util.getLocation(Global.getContext()));
                bundle.putString("network", a.a(Global.getContext()));
                bundle.putString("language", c.b());
                bundle.putString("resolution", c.a(Global.getContext()));
                bundle.putString(StatConstant.JSON_KEY_APP_NAME, a.b(Global.getContext()));
                bundle.putString("model_name", Build.MODEL);
                bundle.putString("timezone", TimeZone.getDefault().getID());
                bundle.putString("sdk_ver", Constants.SDK_VERSION);
                bundle.putString("qz_ver", Util.getAppVersionName(Global.getContext(), Constants.PACKAGE_QZONE));
                bundle.putString("qq_ver", Util.getVersionName(Global.getContext(), Constants.PACKAGE_QQ));
                bundle.putString("qua", Util.getQUA3(Global.getContext(), Global.getPackageName()));
                bundle.putString(ShareRequestParam.REQ_PARAM_PACKAGENAME, Global.getPackageName());
                bundle.putString("app_ver", Util.getAppVersionName(Global.getContext(), Global.getPackageName()));
                if (this.a != null) {
                    bundle.putAll(this.a);
                }
                this.c.d.add(new b(bundle));
                int size = this.c.d.size();
                int i = OpenConfig.getInstance(Global.getContext(), null).getInt("Agent_ReportTimeInterval");
                if (i == 0) {
                    i = AbstractSpiCall.DEFAULT_TIMEOUT;
                }
                if (this.c.a("report_via", size) || this.b) {
                    this.c.e();
                    this.c.f.removeMessages(AidTask.WHAT_LOAD_AID_SUC);
                } else if (!this.c.f.hasMessages(AidTask.WHAT_LOAD_AID_SUC)) {
                    Message obtain = Message.obtain();
                    obtain.what = AidTask.WHAT_LOAD_AID_SUC;
                    this.c.f.sendMessageDelayed(obtain, (long) i);
                }
            } catch (Throwable e) {
                f.b("openSDK_LOG.ReportManager", "--> reporVia, exception in sub thread.", e);
            }
        }
    }

    /* compiled from: ProGuard */
    /* renamed from: com.tencent.open.b.g.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ long a;
        final /* synthetic */ String b;
        final /* synthetic */ String c;
        final /* synthetic */ int d;
        final /* synthetic */ long e;
        final /* synthetic */ long f;
        final /* synthetic */ boolean g;
        final /* synthetic */ g h;

        AnonymousClass3(g gVar, long j, String str, String str2, int i, long j2, long j3, boolean z) {
            this.h = gVar;
            this.a = j;
            this.b = str;
            this.c = str2;
            this.d = i;
            this.e = j2;
            this.f = j3;
            this.g = z;
        }

        public void run() {
            int i = 1;
            try {
                long elapsedRealtime = SystemClock.elapsedRealtime() - this.a;
                Bundle bundle = new Bundle();
                String a = a.a(Global.getContext());
                bundle.putString(StatConstant.JSON_KEY_APP_NAME, a);
                bundle.putString(SocialConstants.PARAM_APP_ID, "1000067");
                bundle.putString("commandid", this.b);
                bundle.putString("detail", this.c);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("network=").append(a).append('&');
                stringBuilder.append("sdcard=").append(Environment.getExternalStorageState().equals("mounted") ? 1 : 0).append('&');
                stringBuilder.append("wifi=").append(a.e(Global.getContext()));
                bundle.putString("deviceInfo", stringBuilder.toString());
                int a2 = 100 / this.h.a(this.d);
                if (a2 > 0) {
                    if (a2 > 100) {
                        i = 100;
                    } else {
                        i = a2;
                    }
                }
                bundle.putString("frequency", i + Table.STRING_DEFAULT_VALUE);
                bundle.putString("reqSize", this.e + Table.STRING_DEFAULT_VALUE);
                bundle.putString("resultCode", this.d + Table.STRING_DEFAULT_VALUE);
                bundle.putString("rspSize", this.f + Table.STRING_DEFAULT_VALUE);
                bundle.putString("timeCost", elapsedRealtime + Table.STRING_DEFAULT_VALUE);
                bundle.putString("uin", Constants.DEFAULT_UIN);
                this.h.c.add(new b(bundle));
                int size = this.h.c.size();
                i = OpenConfig.getInstance(Global.getContext(), null).getInt("Agent_ReportTimeInterval");
                if (i == 0) {
                    i = AbstractSpiCall.DEFAULT_TIMEOUT;
                }
                if (this.h.a("report_cgi", size) || this.g) {
                    this.h.b();
                    this.h.f.removeMessages(AppUri.OPEN_URL);
                } else if (!this.h.f.hasMessages(AppUri.OPEN_URL)) {
                    Message obtain = Message.obtain();
                    obtain.what = AppUri.OPEN_URL;
                    this.h.f.sendMessageDelayed(obtain, (long) i);
                }
            } catch (Throwable e) {
                f.b("openSDK_LOG.ReportManager", "--> reportCGI, exception in sub thread.", e);
            }
        }
    }

    /* compiled from: ProGuard */
    /* renamed from: com.tencent.open.b.g.6 */
    class AnonymousClass6 implements Runnable {
        final /* synthetic */ Bundle a;
        final /* synthetic */ String b;
        final /* synthetic */ boolean c;
        final /* synthetic */ String d;
        final /* synthetic */ g e;

        AnonymousClass6(g gVar, Bundle bundle, String str, boolean z, String str2) {
            this.e = gVar;
            this.a = bundle;
            this.b = str;
            this.c = z;
            this.d = str2;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r10 = this;
            r2 = 1;
            r0 = 0;
            r1 = r10.a;	 Catch:{ Exception -> 0x00b5 }
            if (r1 != 0) goto L_0x000e;
        L_0x0006:
            r0 = "openSDK_LOG.ReportManager";
            r1 = "-->httpRequest, params is null!";
            com.tencent.open.a.f.e(r0, r1);	 Catch:{ Exception -> 0x00b5 }
        L_0x000d:
            return;
        L_0x000e:
            r1 = com.tencent.open.b.e.a();	 Catch:{ Exception -> 0x00b5 }
            if (r1 != 0) goto L_0x00bf;
        L_0x0014:
            r1 = 3;
            r4 = r1;
        L_0x0016:
            r1 = "openSDK_LOG.ReportManager";
            r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b5 }
            r3.<init>();	 Catch:{ Exception -> 0x00b5 }
            r5 = "-->httpRequest, retryCount: ";
            r3 = r3.append(r5);	 Catch:{ Exception -> 0x00b5 }
            r3 = r3.append(r4);	 Catch:{ Exception -> 0x00b5 }
            r3 = r3.toString();	 Catch:{ Exception -> 0x00b5 }
            com.tencent.open.a.f.b(r1, r3);	 Catch:{ Exception -> 0x00b5 }
            r1 = com.tencent.open.utils.Global.getContext();	 Catch:{ Exception -> 0x00b5 }
            r3 = 0;
            r5 = r10.b;	 Catch:{ Exception -> 0x00b5 }
            r5 = com.tencent.open.utils.HttpUtils.getHttpClient(r1, r3, r5);	 Catch:{ Exception -> 0x00b5 }
            r1 = r10.a;	 Catch:{ Exception -> 0x00b5 }
            r1 = com.tencent.open.utils.HttpUtils.encodeUrl(r1);	 Catch:{ Exception -> 0x00b5 }
            r3 = r10.c;	 Catch:{ Exception -> 0x00b5 }
            if (r3 == 0) goto L_0x0126;
        L_0x0043:
            r1 = java.net.URLEncoder.encode(r1);	 Catch:{ Exception -> 0x00b5 }
            r3 = r1;
        L_0x0048:
            r1 = r10.d;	 Catch:{ Exception -> 0x00b5 }
            r1 = r1.toUpperCase();	 Catch:{ Exception -> 0x00b5 }
            r6 = "GET";
            r1 = r1.equals(r6);	 Catch:{ Exception -> 0x00b5 }
            if (r1 == 0) goto L_0x00c2;
        L_0x0056:
            r6 = new java.lang.StringBuffer;	 Catch:{ Exception -> 0x00b5 }
            r1 = r10.b;	 Catch:{ Exception -> 0x00b5 }
            r6.<init>(r1);	 Catch:{ Exception -> 0x00b5 }
            r6.append(r3);	 Catch:{ Exception -> 0x00b5 }
            r1 = new org.apache.http.client.methods.HttpGet;	 Catch:{ Exception -> 0x00b5 }
            r3 = r6.toString();	 Catch:{ Exception -> 0x00b5 }
            r1.<init>(r3);	 Catch:{ Exception -> 0x00b5 }
            r3 = r1;
        L_0x006a:
            r1 = "Accept-Encoding";
            r6 = "gzip";
            r3.addHeader(r1, r6);	 Catch:{ Exception -> 0x00b5 }
            r1 = "Content-Type";
            r6 = "application/x-www-form-urlencoded";
            r3.addHeader(r1, r6);	 Catch:{ Exception -> 0x00b5 }
            r1 = r0;
        L_0x0079:
            r1 = r1 + 1;
            r6 = r5.execute(r3);	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r6 = r6.getStatusLine();	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r6 = r6.getStatusCode();	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r7 = "openSDK_LOG.ReportManager";
            r8 = new java.lang.StringBuilder;	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r8.<init>();	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r9 = "-->httpRequest, statusCode: ";
            r8 = r8.append(r9);	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r8 = r8.append(r6);	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r8 = r8.toString();	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            com.tencent.open.a.f.b(r7, r8);	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
            r7 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            if (r6 == r7) goto L_0x00ee;
        L_0x00a3:
            r6 = "openSDK_LOG.ReportManager";
            r7 = "-->ReportCenter httpRequest : HttpStatuscode != 200";
            com.tencent.open.a.f.b(r6, r7);	 Catch:{ ConnectTimeoutException -> 0x00f7, SocketTimeoutException -> 0x0102, Exception -> 0x010b }
        L_0x00aa:
            if (r0 != r2) goto L_0x0114;
        L_0x00ac:
            r0 = "openSDK_LOG.ReportManager";
            r1 = "-->ReportCenter httpRequest Thread request success";
            com.tencent.open.a.f.b(r0, r1);	 Catch:{ Exception -> 0x00b5 }
            goto L_0x000d;
        L_0x00b5:
            r0 = move-exception;
            r0 = "openSDK_LOG.ReportManager";
            r1 = "-->httpRequest, exception in serial executor.";
            com.tencent.open.a.f.b(r0, r1);
            goto L_0x000d;
        L_0x00bf:
            r4 = r1;
            goto L_0x0016;
        L_0x00c2:
            r1 = r10.d;	 Catch:{ Exception -> 0x00b5 }
            r1 = r1.toUpperCase();	 Catch:{ Exception -> 0x00b5 }
            r6 = "POST";
            r1 = r1.equals(r6);	 Catch:{ Exception -> 0x00b5 }
            if (r1 == 0) goto L_0x00e5;
        L_0x00d0:
            r1 = new org.apache.http.client.methods.HttpPost;	 Catch:{ Exception -> 0x00b5 }
            r6 = r10.b;	 Catch:{ Exception -> 0x00b5 }
            r1.<init>(r6);	 Catch:{ Exception -> 0x00b5 }
            r3 = com.tencent.open.utils.Util.getBytesUTF8(r3);	 Catch:{ Exception -> 0x00b5 }
            r6 = new org.apache.http.entity.ByteArrayEntity;	 Catch:{ Exception -> 0x00b5 }
            r6.<init>(r3);	 Catch:{ Exception -> 0x00b5 }
            r1.setEntity(r6);	 Catch:{ Exception -> 0x00b5 }
            r3 = r1;
            goto L_0x006a;
        L_0x00e5:
            r0 = "openSDK_LOG.ReportManager";
            r1 = "-->httpRequest unkonw request method return.";
            com.tencent.open.a.f.e(r0, r1);	 Catch:{ Exception -> 0x00b5 }
            goto L_0x000d;
        L_0x00ee:
            r0 = "openSDK_LOG.ReportManager";
            r6 = "-->ReportCenter httpRequest Thread success";
            com.tencent.open.a.f.b(r0, r6);	 Catch:{ ConnectTimeoutException -> 0x0123, SocketTimeoutException -> 0x0120, Exception -> 0x011d }
            r0 = r2;
            goto L_0x00aa;
        L_0x00f7:
            r6 = move-exception;
        L_0x00f8:
            r6 = "openSDK_LOG.ReportManager";
            r7 = "-->ReportCenter httpRequest ConnectTimeoutException";
            com.tencent.open.a.f.b(r6, r7);	 Catch:{ Exception -> 0x00b5 }
        L_0x00ff:
            if (r1 < r4) goto L_0x0079;
        L_0x0101:
            goto L_0x00aa;
        L_0x0102:
            r6 = move-exception;
        L_0x0103:
            r6 = "openSDK_LOG.ReportManager";
            r7 = "-->ReportCenter httpRequest SocketTimeoutException";
            com.tencent.open.a.f.b(r6, r7);	 Catch:{ Exception -> 0x00b5 }
            goto L_0x00ff;
        L_0x010b:
            r1 = move-exception;
        L_0x010c:
            r1 = "openSDK_LOG.ReportManager";
            r3 = "-->ReportCenter httpRequest Exception";
            com.tencent.open.a.f.b(r1, r3);	 Catch:{ Exception -> 0x00b5 }
            goto L_0x00aa;
        L_0x0114:
            r0 = "openSDK_LOG.ReportManager";
            r1 = "-->ReportCenter httpRequest Thread request failed";
            com.tencent.open.a.f.b(r0, r1);	 Catch:{ Exception -> 0x00b5 }
            goto L_0x000d;
        L_0x011d:
            r0 = move-exception;
            r0 = r2;
            goto L_0x010c;
        L_0x0120:
            r0 = move-exception;
            r0 = r2;
            goto L_0x0103;
        L_0x0123:
            r0 = move-exception;
            r0 = r2;
            goto L_0x00f8;
        L_0x0126:
            r3 = r1;
            goto L_0x0048;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.tencent.open.b.g.6.run():void");
        }
    }

    public static synchronized g a() {
        g gVar;
        synchronized (g.class) {
            if (a == null) {
                a = new g();
            }
            gVar = a;
        }
        return gVar;
    }

    private g() {
        this.e = null;
        this.b = new Random();
        this.d = Collections.synchronizedList(new ArrayList());
        this.c = Collections.synchronizedList(new ArrayList());
        this.g = ThreadManager.newSerialExecutor();
        this.h = ThreadManager.newSerialExecutor();
        if (this.e == null) {
            this.e = new HandlerThread("opensdk.report.handlerthread", 10);
            this.e.start();
        }
        if (this.e.isAlive() && this.e.getLooper() != null) {
            this.f = new AnonymousClass1(this, this.e.getLooper());
        }
    }

    public void a(Bundle bundle, String str, boolean z) {
        if (bundle != null) {
            f.b("openSDK_LOG.ReportManager", "-->reportVia, bundle: " + bundle.toString());
            if (a("report_via", str) || z) {
                this.g.execute(new AnonymousClass2(this, bundle, z));
            }
        }
    }

    public void a(String str, long j, long j2, long j3, int i) {
        a(str, j, j2, j3, i, Table.STRING_DEFAULT_VALUE, false);
    }

    public void a(String str, long j, long j2, long j3, int i, String str2, boolean z) {
        f.b("openSDK_LOG.ReportManager", "-->reportCgi, command: " + str + " | startTime: " + j + " | reqSize:" + j2 + " | rspSize: " + j3 + " | responseCode: " + i + " | detail: " + str2);
        if (a("report_cgi", Table.STRING_DEFAULT_VALUE + i) || z) {
            this.h.execute(new AnonymousClass3(this, j, str, str2, i, j2, j3, z));
        }
    }

    protected void b() {
        this.h.execute(new Runnable() {
            final /* synthetic */ g a;

            {
                this.a = r1;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r8 = this;
                r1 = 0;
                r0 = r8.a;	 Catch:{ Exception -> 0x00b2 }
                r4 = r0.c();	 Catch:{ Exception -> 0x00b2 }
                if (r4 != 0) goto L_0x000a;
            L_0x0009:
                return;
            L_0x000a:
                r0 = com.tencent.open.utils.Global.getContext();	 Catch:{ Exception -> 0x00b2 }
                r2 = 0;
                r0 = com.tencent.open.utils.OpenConfig.getInstance(r0, r2);	 Catch:{ Exception -> 0x00b2 }
                r2 = "Common_HttpRetryCount";
                r0 = r0.getInt(r2);	 Catch:{ Exception -> 0x00b2 }
                if (r0 != 0) goto L_0x00bc;
            L_0x001b:
                r0 = 3;
                r3 = r0;
            L_0x001d:
                r0 = "openSDK_LOG.ReportManager";
                r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b2 }
                r2.<init>();	 Catch:{ Exception -> 0x00b2 }
                r5 = "-->doReportCgi, retryCount: ";
                r2 = r2.append(r5);	 Catch:{ Exception -> 0x00b2 }
                r2 = r2.append(r3);	 Catch:{ Exception -> 0x00b2 }
                r2 = r2.toString();	 Catch:{ Exception -> 0x00b2 }
                com.tencent.open.a.f.b(r0, r2);	 Catch:{ Exception -> 0x00b2 }
                r0 = r1;
            L_0x0036:
                r0 = r0 + 1;
                r2 = com.tencent.open.utils.Global.getContext();	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r5 = 0;
                r6 = "http://wspeed.qq.com/w.cgi";
                r2 = com.tencent.open.utils.HttpUtils.getHttpClient(r2, r5, r6);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r5 = new org.apache.http.client.methods.HttpPost;	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6 = "http://wspeed.qq.com/w.cgi";
                r5.<init>(r6);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6 = "Accept-Encoding";
                r7 = "gzip";
                r5.addHeader(r6, r7);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6 = "Content-Type";
                r7 = "application/x-www-form-urlencoded";
                r5.setHeader(r6, r7);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6 = com.tencent.open.utils.HttpUtils.encodeUrl(r4);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6 = com.tencent.open.utils.Util.getBytesUTF8(r6);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r7 = new org.apache.http.entity.ByteArrayEntity;	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r7.<init>(r6);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r5.setEntity(r7);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r2 = r2.execute(r5);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r2 = r2.getStatusLine();	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r2 = r2.getStatusCode();	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r5 = "openSDK_LOG.ReportManager";
                r6 = new java.lang.StringBuilder;	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6.<init>();	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r7 = "-->doReportCgi, statusCode: ";
                r6 = r6.append(r7);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6 = r6.append(r2);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r6 = r6.toString();	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                com.tencent.open.a.f.b(r5, r6);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r5 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
                if (r2 != r5) goto L_0x009a;
            L_0x0090:
                r2 = com.tencent.open.b.f.a();	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r5 = "report_cgi";
                r2.b(r5);	 Catch:{ ConnectTimeoutException -> 0x00bf, SocketTimeoutException -> 0x00ca, Exception -> 0x00d3 }
                r1 = 1;
            L_0x009a:
                if (r1 != 0) goto L_0x00a9;
            L_0x009c:
                r0 = com.tencent.open.b.f.a();	 Catch:{ Exception -> 0x00b2 }
                r1 = "report_cgi";
                r2 = r8.a;	 Catch:{ Exception -> 0x00b2 }
                r2 = r2.c;	 Catch:{ Exception -> 0x00b2 }
                r0.a(r1, r2);	 Catch:{ Exception -> 0x00b2 }
            L_0x00a9:
                r0 = r8.a;	 Catch:{ Exception -> 0x00b2 }
                r0 = r0.c;	 Catch:{ Exception -> 0x00b2 }
                r0.clear();	 Catch:{ Exception -> 0x00b2 }
                goto L_0x0009;
            L_0x00b2:
                r0 = move-exception;
                r1 = "openSDK_LOG.ReportManager";
                r2 = "-->doReportCgi, doupload exception out.";
                com.tencent.open.a.f.a(r1, r2, r0);
                goto L_0x0009;
            L_0x00bc:
                r3 = r0;
                goto L_0x001d;
            L_0x00bf:
                r2 = move-exception;
                r5 = "openSDK_LOG.ReportManager";
                r6 = "-->doReportCgi, doupload exception";
                com.tencent.open.a.f.a(r5, r6, r2);	 Catch:{ Exception -> 0x00b2 }
            L_0x00c7:
                if (r0 < r3) goto L_0x0036;
            L_0x00c9:
                goto L_0x009a;
            L_0x00ca:
                r2 = move-exception;
                r5 = "openSDK_LOG.ReportManager";
                r6 = "-->doReportCgi, doupload exception";
                com.tencent.open.a.f.a(r5, r6, r2);	 Catch:{ Exception -> 0x00b2 }
                goto L_0x00c7;
            L_0x00d3:
                r0 = move-exception;
                r2 = "openSDK_LOG.ReportManager";
                r3 = "-->doReportCgi, doupload exception";
                com.tencent.open.a.f.a(r2, r3, r0);	 Catch:{ Exception -> 0x00b2 }
                goto L_0x009a;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.tencent.open.b.g.4.run():void");
            }
        });
    }

    protected boolean a(String str, String str2) {
        boolean z = true;
        boolean z2 = false;
        f.b("openSDK_LOG.ReportManager", "-->availableFrequency, report: " + str + " | ext: " + str2);
        if (!TextUtils.isEmpty(str)) {
            int i;
            int a;
            if (str.equals("report_cgi")) {
                try {
                    a = a(Integer.parseInt(str2));
                    if (this.b.nextInt(100) >= a) {
                        z = false;
                    }
                    z2 = z;
                    i = a;
                } catch (Exception e) {
                }
            } else if (str.equals("report_via")) {
                a = e.a(str2);
                if (this.b.nextInt(100) < a) {
                    z2 = true;
                    i = a;
                } else {
                    i = a;
                }
            } else {
                i = 100;
            }
            f.b("openSDK_LOG.ReportManager", "-->availableFrequency, result: " + z2 + " | frequency: " + i);
        }
        return z2;
    }

    protected boolean a(String str, int i) {
        int i2 = 5;
        int i3;
        if (str.equals("report_cgi")) {
            i3 = OpenConfig.getInstance(Global.getContext(), null).getInt("Common_CGIReportMaxcount");
            if (i3 != 0) {
                i2 = i3;
            }
        } else if (str.equals("report_via")) {
            i3 = OpenConfig.getInstance(Global.getContext(), null).getInt("Agent_ReportBatchCount");
            if (i3 != 0) {
                i2 = i3;
            }
        } else {
            i2 = 0;
        }
        f.b("openSDK_LOG.ReportManager", "-->availableCount, report: " + str + " | dataSize: " + i + " | maxcount: " + i2);
        if (i >= i2) {
            return true;
        }
        return false;
    }

    protected int a(int i) {
        int i2;
        if (i == 0) {
            i2 = OpenConfig.getInstance(Global.getContext(), null).getInt("Common_CGIReportFrequencySuccess");
            if (i2 == 0) {
                return 10;
            }
            return i2;
        }
        i2 = OpenConfig.getInstance(Global.getContext(), null).getInt("Common_CGIReportFrequencyFailed");
        if (i2 == 0) {
            return 100;
        }
        return i2;
    }

    protected Bundle c() {
        if (this.c.size() == 0) {
            return null;
        }
        b bVar = (b) this.c.get(0);
        if (bVar == null) {
            f.b("openSDK_LOG.ReportManager", "-->prepareCgiData, the 0th cgireportitem is null.");
            return null;
        }
        String str = (String) bVar.a.get(SocialConstants.PARAM_APP_ID);
        Collection a = f.a().a("report_cgi");
        if (a != null) {
            this.c.addAll(a);
        }
        f.b("openSDK_LOG.ReportManager", "-->prepareCgiData, mCgiList size: " + this.c.size());
        if (this.c.size() == 0) {
            return null;
        }
        Bundle bundle = new Bundle();
        try {
            bundle.putString(SocialConstants.PARAM_APP_ID, str);
            bundle.putString("releaseversion", Constants.SDK_VERSION_REPORT);
            bundle.putString("device", Build.DEVICE);
            bundle.putString("qua", Constants.SDK_QUA);
            bundle.putString("key", "apn,frequency,commandid,resultcode,tmcost,reqsize,rspsize,detail,touin,deviceinfo");
            for (int i = 0; i < this.c.size(); i++) {
                bVar = (b) this.c.get(i);
                bundle.putString(i + "_1", (String) bVar.a.get(StatConstant.JSON_KEY_APP_NAME));
                bundle.putString(i + "_2", (String) bVar.a.get("frequency"));
                bundle.putString(i + "_3", (String) bVar.a.get("commandid"));
                bundle.putString(i + "_4", (String) bVar.a.get("resultCode"));
                bundle.putString(i + "_5", (String) bVar.a.get("timeCost"));
                bundle.putString(i + "_6", (String) bVar.a.get("reqSize"));
                bundle.putString(i + "_7", (String) bVar.a.get("rspSize"));
                bundle.putString(i + "_8", (String) bVar.a.get("detail"));
                bundle.putString(i + "_9", (String) bVar.a.get("uin"));
                bundle.putString(i + "_10", c.e(Global.getContext()) + "&" + ((String) bVar.a.get("deviceInfo")));
            }
            f.b("openSDK_LOG.ReportManager", "-->prepareCgiData, end. params: " + bundle.toString());
            return bundle;
        } catch (Throwable e) {
            f.b("openSDK_LOG.ReportManager", "-->prepareCgiData, exception.", e);
            return null;
        }
    }

    protected Bundle d() {
        Collection a = f.a().a("report_via");
        if (a != null) {
            this.d.addAll(a);
        }
        f.b("openSDK_LOG.ReportManager", "-->prepareViaData, mViaList size: " + this.d.size());
        if (this.d.size() == 0) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        for (Serializable serializable : this.d) {
            JSONObject jSONObject = new JSONObject();
            b bVar = (b) serializable;
            for (String str : bVar.a.keySet()) {
                try {
                    Object obj = (String) bVar.a.get(str);
                    if (obj == null) {
                        obj = Table.STRING_DEFAULT_VALUE;
                    }
                    jSONObject.put(str, obj);
                } catch (Throwable e) {
                    f.a("openSDK_LOG.ReportManager", "-->prepareViaData, put bundle to json array exception", e);
                }
            }
            jSONArray.put(jSONObject);
        }
        f.b("openSDK_LOG.ReportManager", "-->prepareViaData, JSONArray array: " + jSONArray.toString());
        Bundle bundle = new Bundle();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA, jSONArray);
            bundle.putString(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA, jSONObject2.toString());
            return bundle;
        } catch (Throwable e2) {
            f.a("openSDK_LOG.ReportManager", "-->prepareViaData, put bundle to json array exception", e2);
            return null;
        }
    }

    protected void e() {
        this.g.execute(new Runnable() {
            final /* synthetic */ g a;

            {
                this.a = r1;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r18 = this;
                r0 = r18;
                r2 = r0.a;	 Catch:{ Exception -> 0x00a3 }
                r14 = r2.d();	 Catch:{ Exception -> 0x00a3 }
                if (r14 != 0) goto L_0x000b;
            L_0x000a:
                return;
            L_0x000b:
                r2 = "openSDK_LOG.ReportManager";
                r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a3 }
                r3.<init>();	 Catch:{ Exception -> 0x00a3 }
                r4 = "-->doReportVia, params: ";
                r3 = r3.append(r4);	 Catch:{ Exception -> 0x00a3 }
                r4 = r14.toString();	 Catch:{ Exception -> 0x00a3 }
                r3 = r3.append(r4);	 Catch:{ Exception -> 0x00a3 }
                r3 = r3.toString();	 Catch:{ Exception -> 0x00a3 }
                com.tencent.open.a.f.b(r2, r3);	 Catch:{ Exception -> 0x00a3 }
                r11 = com.tencent.open.b.e.a();	 Catch:{ Exception -> 0x00a3 }
                r10 = 0;
                r3 = 0;
                r8 = android.os.SystemClock.elapsedRealtime();	 Catch:{ Exception -> 0x00a3 }
                r6 = 0;
                r4 = 0;
                r2 = 0;
            L_0x0036:
                r10 = r10 + 1;
                r12 = com.tencent.open.utils.Global.getContext();	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
                r13 = "http://appsupport.qq.com/cgi-bin/appstage/mstats_batch_report";
                r15 = "POST";
                r15 = com.tencent.open.utils.HttpUtils.openUrl2(r12, r13, r15, r14);	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
                r12 = r15.response;	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
                r12 = com.tencent.open.utils.Util.parseJson(r12);	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
                r13 = "ret";
                r12 = r12.getInt(r13);	 Catch:{ JSONException -> 0x00ad, ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
            L_0x0050:
                if (r12 == 0) goto L_0x005a;
            L_0x0052:
                r12 = r15.response;	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
                r12 = android.text.TextUtils.isEmpty(r12);	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
                if (r12 != 0) goto L_0x005c;
            L_0x005a:
                r3 = 1;
                r10 = r11;
            L_0x005c:
                r12 = r15.reqSize;	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x00e5, IOException -> 0x0104, Exception -> 0x010f }
                r4 = r15.rspSize;	 Catch:{ ConnectTimeoutException -> 0x00b0, SocketTimeoutException -> 0x00c0, JSONException -> 0x00cb, NetworkUnavailableException -> 0x00d2, HttpStatusException -> 0x012b, IOException -> 0x0104, Exception -> 0x010f }
                r6 = r12;
            L_0x0061:
                if (r10 < r11) goto L_0x0036;
            L_0x0063:
                r10 = r2;
                r13 = r3;
                r16 = r8;
                r8 = r4;
                r4 = r16;
            L_0x006a:
                r0 = r18;
                r2 = r0.a;	 Catch:{ Exception -> 0x00a3 }
                r3 = "mapp_apptrace_sdk";
                r11 = 0;
                r12 = 0;
                r2.a(r3, r4, r6, r8, r10, r11, r12);	 Catch:{ Exception -> 0x00a3 }
                if (r13 == 0) goto L_0x0118;
            L_0x0077:
                r2 = com.tencent.open.b.f.a();	 Catch:{ Exception -> 0x00a3 }
                r3 = "report_via";
                r2.b(r3);	 Catch:{ Exception -> 0x00a3 }
            L_0x0080:
                r0 = r18;
                r2 = r0.a;	 Catch:{ Exception -> 0x00a3 }
                r2 = r2.d;	 Catch:{ Exception -> 0x00a3 }
                r2.clear();	 Catch:{ Exception -> 0x00a3 }
                r2 = "openSDK_LOG.ReportManager";
                r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a3 }
                r3.<init>();	 Catch:{ Exception -> 0x00a3 }
                r4 = "-->doReportVia, uploadSuccess: ";
                r3 = r3.append(r4);	 Catch:{ Exception -> 0x00a3 }
                r3 = r3.append(r13);	 Catch:{ Exception -> 0x00a3 }
                r3 = r3.toString();	 Catch:{ Exception -> 0x00a3 }
                com.tencent.open.a.f.b(r2, r3);	 Catch:{ Exception -> 0x00a3 }
                goto L_0x000a;
            L_0x00a3:
                r2 = move-exception;
                r3 = "openSDK_LOG.ReportManager";
                r4 = "-->doReportVia, exception in serial executor.";
                com.tencent.open.a.f.b(r3, r4, r2);
                goto L_0x000a;
            L_0x00ad:
                r12 = move-exception;
                r12 = -4;
                goto L_0x0050;
            L_0x00b0:
                r2 = move-exception;
                r2 = r10;
                r8 = android.os.SystemClock.elapsedRealtime();	 Catch:{ Exception -> 0x00a3 }
                r12 = 0;
                r6 = 0;
                r4 = -7;
                r10 = r2;
                r2 = r4;
                r4 = r6;
                r6 = r12;
                goto L_0x0061;
            L_0x00c0:
                r2 = move-exception;
                r8 = android.os.SystemClock.elapsedRealtime();	 Catch:{ Exception -> 0x00a3 }
                r6 = 0;
                r4 = 0;
                r2 = -8;
                goto L_0x0061;
            L_0x00cb:
                r2 = move-exception;
                r6 = 0;
                r4 = 0;
                r2 = -4;
                goto L_0x0061;
            L_0x00d2:
                r2 = move-exception;
                r0 = r18;
                r2 = r0.a;	 Catch:{ Exception -> 0x00a3 }
                r2 = r2.d;	 Catch:{ Exception -> 0x00a3 }
                r2.clear();	 Catch:{ Exception -> 0x00a3 }
                r2 = "openSDK_LOG.ReportManager";
                r3 = "doReportVia, NetworkUnavailableException.";
                com.tencent.open.a.f.b(r2, r3);	 Catch:{ Exception -> 0x00a3 }
                goto L_0x000a;
            L_0x00e5:
                r10 = move-exception;
                r16 = r10;
                r10 = r3;
                r3 = r16;
            L_0x00eb:
                r3 = r3.getMessage();	 Catch:{ Exception -> 0x0129 }
                r11 = "http status code error:";
                r12 = "";
                r3 = r3.replace(r11, r12);	 Catch:{ Exception -> 0x0129 }
                r2 = java.lang.Integer.parseInt(r3);	 Catch:{ Exception -> 0x0129 }
            L_0x00fb:
                r13 = r10;
                r10 = r2;
                r16 = r8;
                r8 = r4;
                r4 = r16;
                goto L_0x006a;
            L_0x0104:
                r2 = move-exception;
                r6 = 0;
                r4 = 0;
                r2 = com.tencent.open.utils.HttpUtils.getErrorCodeFromException(r2);	 Catch:{ Exception -> 0x00a3 }
                goto L_0x0061;
            L_0x010f:
                r2 = move-exception;
                r6 = 0;
                r4 = 0;
                r2 = -6;
                r10 = r11;
                goto L_0x0061;
            L_0x0118:
                r2 = com.tencent.open.b.f.a();	 Catch:{ Exception -> 0x00a3 }
                r3 = "report_via";
                r0 = r18;
                r4 = r0.a;	 Catch:{ Exception -> 0x00a3 }
                r4 = r4.d;	 Catch:{ Exception -> 0x00a3 }
                r2.a(r3, r4);	 Catch:{ Exception -> 0x00a3 }
                goto L_0x0080;
            L_0x0129:
                r3 = move-exception;
                goto L_0x00fb;
            L_0x012b:
                r6 = move-exception;
                r10 = r3;
                r3 = r6;
                r6 = r12;
                goto L_0x00eb;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.tencent.open.b.g.5.run():void");
            }
        });
    }

    public void a(String str, String str2, Bundle bundle, boolean z) {
        ThreadManager.executeOnSubThread(new AnonymousClass6(this, bundle, str, z, str2));
    }
}
