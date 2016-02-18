package com.douban.amonsul.network;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatLogger;
import com.douban.book.reader.helper.AppUri;
import com.igexin.download.Downloads;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.http.NameValuePair;

public class URLClient {
    private static final String BOUNDARY = "--gc0p4Jq0M2Yt08jU534c0p\r\n";
    public static final String BOUNDARYSTR = "gc0p4Jq0M2Yt08jU534c0p";
    private static final int RETRY_COUNT = 3;
    private static final String TAG;
    private static final int URL_CONNECTION_TIMEOUT = 15000;
    private static final int URL_READ_TIMEOUT = 15000;
    private static final String URL_USER_AGENT = "com.douban.amonsul/android 1.0.13";
    private int mRetryCount;

    static {
        TAG = URLClient.class.getName();
    }

    public URLClient() {
        this.mRetryCount = RETRY_COUNT;
    }

    public URLClient(int retryCount) {
        this.mRetryCount = retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.mRetryCount = retryCount;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.douban.amonsul.network.Response catchConfig(java.lang.String r18, com.douban.amonsul.network.NetWorker.Method r19, java.util.List<org.apache.http.NameValuePair> r20, java.util.List<org.apache.http.NameValuePair> r21) {
        /*
        r17 = this;
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r7 = 0;
    L_0x0003:
        r0 = r17;
        r13 = r0.mRetryCount;
        if (r7 >= r13) goto L_0x0187;
    L_0x0009:
        r13 = com.douban.amonsul.network.NetWorker.Method.GET;	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        r0 = r19;
        r13 = r0.equals(r13);	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        if (r13 == 0) goto L_0x001d;
    L_0x0013:
        r0 = r17;
        r1 = r18;
        r2 = r20;
        r18 = r0.getQuery(r1, r2);	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
    L_0x001d:
        r3 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        r0 = r18;
        r3.<init>(r0);	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        r5 = r3.openConnection();	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        r5 = (java.net.HttpURLConnection) r5;	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        r13 = 15000; // 0x3a98 float:2.102E-41 double:7.411E-320;
        r5.setConnectTimeout(r13);	 Catch:{ all -> 0x0075 }
        r13 = 15000; // 0x3a98 float:2.102E-41 double:7.411E-320;
        r5.setReadTimeout(r13);	 Catch:{ all -> 0x0075 }
        r13 = r19.name();	 Catch:{ all -> 0x0075 }
        r5.setRequestMethod(r13);	 Catch:{ all -> 0x0075 }
        r13 = "Accept-Encoding";
        r14 = "gzip";
        r5.setRequestProperty(r13, r14);	 Catch:{ all -> 0x0075 }
        r13 = "User-Agent";
        r14 = "com.douban.amonsul/android";
        r5.setRequestProperty(r13, r14);	 Catch:{ all -> 0x0075 }
        r13 = "Accept-Charset";
        r14 = "UTF-8";
        r5.setRequestProperty(r13, r14);	 Catch:{ all -> 0x0075 }
        r13 = "Content-Type";
        r14 = "application/x-www-form-urlencoded";
        r5.setRequestProperty(r13, r14);	 Catch:{ all -> 0x0075 }
        if (r21 == 0) goto L_0x0084;
    L_0x0059:
        r8 = r21.iterator();	 Catch:{ all -> 0x0075 }
    L_0x005d:
        r13 = r8.hasNext();	 Catch:{ all -> 0x0075 }
        if (r13 == 0) goto L_0x0084;
    L_0x0063:
        r11 = r8.next();	 Catch:{ all -> 0x0075 }
        r11 = (org.apache.http.NameValuePair) r11;	 Catch:{ all -> 0x0075 }
        r13 = r11.getName();	 Catch:{ all -> 0x0075 }
        r14 = r11.getValue();	 Catch:{ all -> 0x0075 }
        r5.setRequestProperty(r13, r14);	 Catch:{ all -> 0x0075 }
        goto L_0x005d;
    L_0x0075:
        r13 = move-exception;
        r5.disconnect();	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        throw r13;	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
    L_0x007a:
        r6 = move-exception;
        r13 = com.douban.amonsul.MobileStat.DEBUG;
        if (r13 == 0) goto L_0x0082;
    L_0x007f:
        r6.printStackTrace();
    L_0x0082:
        r13 = 0;
    L_0x0083:
        return r13;
    L_0x0084:
        r13 = 1;
        r5.setDoInput(r13);	 Catch:{ all -> 0x0075 }
        r13 = r19.name();	 Catch:{ all -> 0x0075 }
        r14 = "POST";
        r13 = r13.equals(r14);	 Catch:{ all -> 0x0075 }
        if (r13 != 0) goto L_0x00a0;
    L_0x0094:
        r13 = r19.name();	 Catch:{ all -> 0x0075 }
        r14 = "PUT";
        r13 = r13.equals(r14);	 Catch:{ all -> 0x0075 }
        if (r13 == 0) goto L_0x00c7;
    L_0x00a0:
        r13 = 1;
        r5.setDoOutput(r13);	 Catch:{ all -> 0x0075 }
        if (r20 == 0) goto L_0x00c7;
    L_0x00a6:
        r10 = r5.getOutputStream();	 Catch:{ all -> 0x0075 }
        r12 = new java.io.BufferedWriter;	 Catch:{ all -> 0x0075 }
        r13 = new java.io.OutputStreamWriter;	 Catch:{ all -> 0x0075 }
        r14 = "UTF-8";
        r13.<init>(r10, r14);	 Catch:{ all -> 0x0075 }
        r12.<init>(r13);	 Catch:{ all -> 0x0075 }
        r0 = r17;
        r1 = r20;
        r13 = r0.getQuery(r1);	 Catch:{ all -> 0x0075 }
        r12.write(r13);	 Catch:{ all -> 0x0075 }
        r12.close();	 Catch:{ all -> 0x0075 }
        r10.close();	 Catch:{ all -> 0x0075 }
    L_0x00c7:
        r13 = TAG;	 Catch:{ all -> 0x0075 }
        r14 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0075 }
        r14.<init>();	 Catch:{ all -> 0x0075 }
        r15 = " request Url ";
        r14 = r14.append(r15);	 Catch:{ all -> 0x0075 }
        r0 = r18;
        r14 = r14.append(r0);	 Catch:{ all -> 0x0075 }
        r14 = r14.toString();	 Catch:{ all -> 0x0075 }
        com.douban.amonsul.StatLogger.d(r13, r14);	 Catch:{ all -> 0x0075 }
        r5.connect();	 Catch:{ all -> 0x0075 }
        r4 = r5.getResponseCode();	 Catch:{ all -> 0x0075 }
        r9 = 0;
        r13 = r5.getContentEncoding();	 Catch:{ all -> 0x0075 }
        if (r13 == 0) goto L_0x00f9;
    L_0x00ef:
        r13 = r5.getContentEncoding();	 Catch:{ all -> 0x0075 }
        r14 = "gzip";
        r9 = r13.equalsIgnoreCase(r14);	 Catch:{ all -> 0x0075 }
    L_0x00f9:
        r13 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        if (r4 <= r13) goto L_0x0133;
    L_0x00fd:
        r13 = new com.douban.amonsul.network.Response;	 Catch:{ all -> 0x0075 }
        r14 = r5.getErrorStream();	 Catch:{ all -> 0x0075 }
        r0 = r17;
        r14 = r0.readStream(r14, r9);	 Catch:{ all -> 0x0075 }
        r0 = r17;
        r15 = r0.getMessageByCode(r4);	 Catch:{ all -> 0x0075 }
        r13.<init>(r14, r4, r15);	 Catch:{ all -> 0x0075 }
        r5.disconnect();	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        goto L_0x0083;
    L_0x0117:
        r6 = move-exception;
        r13 = com.douban.amonsul.MobileStat.DEBUG;
        if (r13 == 0) goto L_0x011f;
    L_0x011c:
        r6.printStackTrace();
    L_0x011f:
        r0 = r17;
        r13 = r0.mRetryCount;
        r13 = r13 + -1;
        if (r7 != r13) goto L_0x0183;
    L_0x0127:
        r13 = new com.douban.amonsul.network.Response;
        r14 = "";
        r15 = -1;
        r16 = "can not be connect";
        r13.<init>(r14, r15, r16);
        goto L_0x0083;
    L_0x0133:
        r13 = new com.douban.amonsul.network.Response;	 Catch:{ all -> 0x0075 }
        r14 = r5.getInputStream();	 Catch:{ all -> 0x0075 }
        r0 = r17;
        r14 = r0.readStream(r14, r9);	 Catch:{ all -> 0x0075 }
        r15 = r5.getResponseMessage();	 Catch:{ all -> 0x0075 }
        r13.<init>(r14, r4, r15);	 Catch:{ all -> 0x0075 }
        r5.disconnect();	 Catch:{ MalformedURLException -> 0x007a, IOException -> 0x0117, Exception -> 0x014b, AssertionError -> 0x0167 }
        goto L_0x0083;
    L_0x014b:
        r6 = move-exception;
        r13 = com.douban.amonsul.MobileStat.DEBUG;
        if (r13 == 0) goto L_0x0153;
    L_0x0150:
        r6.printStackTrace();
    L_0x0153:
        r0 = r17;
        r13 = r0.mRetryCount;
        r13 = r13 + -1;
        if (r7 != r13) goto L_0x0183;
    L_0x015b:
        r13 = new com.douban.amonsul.network.Response;
        r14 = "";
        r15 = -1;
        r16 = "can not be connect";
        r13.<init>(r14, r15, r16);
        goto L_0x0083;
    L_0x0167:
        r6 = move-exception;
        r13 = com.douban.amonsul.MobileStat.DEBUG;
        if (r13 == 0) goto L_0x016f;
    L_0x016c:
        r6.printStackTrace();
    L_0x016f:
        r0 = r17;
        r13 = r0.mRetryCount;
        r13 = r13 + -1;
        if (r7 != r13) goto L_0x0183;
    L_0x0177:
        r13 = new com.douban.amonsul.network.Response;
        r14 = "";
        r15 = -1;
        r16 = "can not be connect";
        r13.<init>(r14, r15, r16);
        goto L_0x0083;
    L_0x0183:
        r7 = r7 + 1;
        goto L_0x0003;
    L_0x0187:
        r13 = 0;
        goto L_0x0083;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.amonsul.network.URLClient.catchConfig(java.lang.String, com.douban.amonsul.network.NetWorker$Method, java.util.List, java.util.List):com.douban.amonsul.network.Response");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.douban.amonsul.network.Response send(java.lang.String r22, com.douban.amonsul.network.NetWorker.Method r23, java.util.List<org.apache.http.NameValuePair> r24, java.util.List<org.apache.http.NameValuePair> r25, com.douban.amonsul.network.MultipartParameter r26) {
        /*
        r21 = this;
        r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r11 = 0;
    L_0x0003:
        r0 = r21;
        r0 = r0.mRetryCount;
        r17 = r0;
        r0 = r17;
        if (r11 >= r0) goto L_0x02c9;
    L_0x000d:
        r3 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        r0 = r22;
        r3.<init>(r0);	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        r17 = com.douban.amonsul.network.NetWorker.Method.GET;	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        r0 = r23;
        r1 = r17;
        r17 = r0.equals(r1);	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        if (r17 != 0) goto L_0x002c;
    L_0x0020:
        r17 = com.douban.amonsul.network.NetWorker.Method.POST;	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        r0 = r23;
        r1 = r17;
        r17 = r0.equals(r1);	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        if (r17 == 0) goto L_0x0036;
    L_0x002c:
        r0 = r21;
        r1 = r22;
        r2 = r24;
        r22 = r0.getQuery(r1, r2);	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
    L_0x0036:
        r5 = r3.openConnection();	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        r5 = (java.net.HttpURLConnection) r5;	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        r17 = 0;
        r0 = r17;
        r5.setUseCaches(r0);	 Catch:{ all -> 0x00a8 }
        r17 = 15000; // 0x3a98 float:2.102E-41 double:7.411E-320;
        r0 = r17;
        r5.setConnectTimeout(r0);	 Catch:{ all -> 0x00a8 }
        r17 = 15000; // 0x3a98 float:2.102E-41 double:7.411E-320;
        r0 = r17;
        r5.setReadTimeout(r0);	 Catch:{ all -> 0x00a8 }
        r17 = r23.name();	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r5.setRequestMethod(r0);	 Catch:{ all -> 0x00a8 }
        r17 = "Accept-Encoding";
        r18 = "gzip";
        r0 = r17;
        r1 = r18;
        r5.setRequestProperty(r0, r1);	 Catch:{ all -> 0x00a8 }
        r17 = "User-Agent";
        r18 = "com.douban.amonsul/android 1.0.13";
        r0 = r17;
        r1 = r18;
        r5.setRequestProperty(r0, r1);	 Catch:{ all -> 0x00a8 }
        r17 = "Accept-Charset";
        r18 = "UTF-8";
        r0 = r17;
        r1 = r18;
        r5.setRequestProperty(r0, r1);	 Catch:{ all -> 0x00a8 }
        r17 = "Connection";
        r18 = "keep-alive";
        r0 = r17;
        r1 = r18;
        r5.setRequestProperty(r0, r1);	 Catch:{ all -> 0x00a8 }
        if (r25 == 0) goto L_0x00b8;
    L_0x0088:
        r12 = r25.iterator();	 Catch:{ all -> 0x00a8 }
    L_0x008c:
        r17 = r12.hasNext();	 Catch:{ all -> 0x00a8 }
        if (r17 == 0) goto L_0x00b8;
    L_0x0092:
        r16 = r12.next();	 Catch:{ all -> 0x00a8 }
        r16 = (org.apache.http.NameValuePair) r16;	 Catch:{ all -> 0x00a8 }
        r17 = r16.getName();	 Catch:{ all -> 0x00a8 }
        r18 = r16.getValue();	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r1 = r18;
        r5.setRequestProperty(r0, r1);	 Catch:{ all -> 0x00a8 }
        goto L_0x008c;
    L_0x00a8:
        r17 = move-exception;
        r5.disconnect();	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        throw r17;	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
    L_0x00ad:
        r7 = move-exception;
        r17 = TAG;
        r0 = r17;
        com.douban.amonsul.StatLogger.e(r0, r7);
        r17 = 0;
    L_0x00b7:
        return r17;
    L_0x00b8:
        r17 = "Content-Type";
        r18 = "multipart/form-data;boundary=gc0p4Jq0M2Yt08jU534c0p";
        r0 = r17;
        r1 = r18;
        r5.setRequestProperty(r0, r1);	 Catch:{ all -> 0x00a8 }
        r10 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00a8 }
        r10.<init>();	 Catch:{ all -> 0x00a8 }
        r12 = r24.iterator();	 Catch:{ all -> 0x00a8 }
    L_0x00cc:
        r17 = r12.hasNext();	 Catch:{ all -> 0x00a8 }
        if (r17 == 0) goto L_0x0107;
    L_0x00d2:
        r16 = r12.next();	 Catch:{ all -> 0x00a8 }
        r16 = (org.apache.http.NameValuePair) r16;	 Catch:{ all -> 0x00a8 }
        r17 = "--gc0p4Jq0M2Yt08jU534c0p\r\n";
        r0 = r17;
        r10.append(r0);	 Catch:{ all -> 0x00a8 }
        r17 = "Content-Disposition:form-data;name=\"";
        r0 = r17;
        r10.append(r0);	 Catch:{ all -> 0x00a8 }
        r17 = r16.getName();	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r10.append(r0);	 Catch:{ all -> 0x00a8 }
        r17 = "\"\r\n\r\n";
        r0 = r17;
        r10.append(r0);	 Catch:{ all -> 0x00a8 }
        r17 = r16.getValue();	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r10.append(r0);	 Catch:{ all -> 0x00a8 }
        r17 = "\r\n";
        r0 = r17;
        r10.append(r0);	 Catch:{ all -> 0x00a8 }
        goto L_0x00cc;
    L_0x0107:
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00a8 }
        r9.<init>();	 Catch:{ all -> 0x00a8 }
        r17 = "--gc0p4Jq0M2Yt08jU534c0p\r\n";
        r0 = r17;
        r17 = r9.append(r0);	 Catch:{ all -> 0x00a8 }
        r18 = "Content-Disposition:form-data;name=\"";
        r17 = r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r18 = r26.getName();	 Catch:{ all -> 0x00a8 }
        r17 = r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r18 = "\";filename=\"";
        r17 = r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r18 = r26.getName();	 Catch:{ all -> 0x00a8 }
        r17 = r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r18 = "\"\r\n";
        r17 = r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r18 = "Content-Type:";
        r17 = r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r18 = "text/plain";
        r17 = r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r18 = "\r\n\r\n";
        r17.append(r18);	 Catch:{ all -> 0x00a8 }
        r17 = "\r\n--gc0p4Jq0M2Yt08jU534c0p--\r\n";
        r8 = r17.getBytes();	 Catch:{ all -> 0x00a8 }
        r17 = r26.getContent();	 Catch:{ all -> 0x00a8 }
        r0 = r21;
        r1 = r17;
        r6 = r0.compress(r1);	 Catch:{ all -> 0x00a8 }
        r17 = TAG;	 Catch:{ all -> 0x00a8 }
        r18 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00a8 }
        r18.<init>();	 Catch:{ all -> 0x00a8 }
        r19 = " send Url ";
        r18 = r18.append(r19);	 Catch:{ all -> 0x00a8 }
        r0 = r18;
        r1 = r22;
        r18 = r0.append(r1);	 Catch:{ all -> 0x00a8 }
        r18 = r18.toString();	 Catch:{ all -> 0x00a8 }
        com.douban.amonsul.StatLogger.d(r17, r18);	 Catch:{ all -> 0x00a8 }
        r17 = "Content-Length";
        r18 = r10.toString();	 Catch:{ all -> 0x00a8 }
        r18 = r18.getBytes();	 Catch:{ all -> 0x00a8 }
        r0 = r18;
        r0 = r0.length;	 Catch:{ all -> 0x00a8 }
        r18 = r0;
        r19 = r9.toString();	 Catch:{ all -> 0x00a8 }
        r19 = r19.getBytes();	 Catch:{ all -> 0x00a8 }
        r0 = r19;
        r0 = r0.length;	 Catch:{ all -> 0x00a8 }
        r19 = r0;
        r18 = r18 + r19;
        r0 = r6.length;	 Catch:{ all -> 0x00a8 }
        r19 = r0;
        r18 = r18 + r19;
        r0 = r8.length;	 Catch:{ all -> 0x00a8 }
        r19 = r0;
        r18 = r18 + r19;
        r18 = java.lang.String.valueOf(r18);	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r1 = r18;
        r5.setRequestProperty(r0, r1);	 Catch:{ all -> 0x00a8 }
        r17 = 1;
        r0 = r17;
        r5.setDoInput(r0);	 Catch:{ all -> 0x00a8 }
        r17 = 1;
        r0 = r17;
        r5.setDoOutput(r0);	 Catch:{ all -> 0x00a8 }
        r5.connect();	 Catch:{ all -> 0x00a8 }
        r15 = r5.getOutputStream();	 Catch:{ all -> 0x00a8 }
        r14 = new java.io.BufferedOutputStream;	 Catch:{ all -> 0x00a8 }
        r14.<init>(r15);	 Catch:{ all -> 0x00a8 }
        r17 = r10.toString();	 Catch:{ all -> 0x00a8 }
        r17 = r17.getBytes();	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r14.write(r0);	 Catch:{ all -> 0x00a8 }
        r17 = r9.toString();	 Catch:{ all -> 0x00a8 }
        r17 = r17.getBytes();	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r14.write(r0);	 Catch:{ all -> 0x00a8 }
        r14.write(r6);	 Catch:{ all -> 0x00a8 }
        r14.write(r8);	 Catch:{ all -> 0x00a8 }
        r14.flush();	 Catch:{ all -> 0x00a8 }
        r14.close();	 Catch:{ all -> 0x00a8 }
        r15.close();	 Catch:{ all -> 0x00a8 }
        r13 = 0;
        r17 = r5.getContentEncoding();	 Catch:{ all -> 0x00a8 }
        if (r17 == 0) goto L_0x01fc;
    L_0x01f2:
        r17 = r5.getContentEncoding();	 Catch:{ all -> 0x00a8 }
        r18 = "gzip";
        r13 = r17.equalsIgnoreCase(r18);	 Catch:{ all -> 0x00a8 }
    L_0x01fc:
        r4 = r5.getResponseCode();	 Catch:{ all -> 0x00a8 }
        r17 = TAG;	 Catch:{ all -> 0x00a8 }
        r18 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00a8 }
        r18.<init>();	 Catch:{ all -> 0x00a8 }
        r19 = " reponse code ";
        r18 = r18.append(r19);	 Catch:{ all -> 0x00a8 }
        r0 = r18;
        r18 = r0.append(r4);	 Catch:{ all -> 0x00a8 }
        r18 = r18.toString();	 Catch:{ all -> 0x00a8 }
        com.douban.amonsul.StatLogger.d(r17, r18);	 Catch:{ all -> 0x00a8 }
        r17 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r0 = r17;
        if (r4 <= r0) goto L_0x0263;
    L_0x0220:
        r17 = new com.douban.amonsul.network.Response;	 Catch:{ all -> 0x00a8 }
        r18 = r5.getErrorStream();	 Catch:{ all -> 0x00a8 }
        r0 = r21;
        r1 = r18;
        r18 = r0.readStream(r1, r13);	 Catch:{ all -> 0x00a8 }
        r0 = r21;
        r19 = r0.getMessageByCode(r4);	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r1 = r18;
        r2 = r19;
        r0.<init>(r1, r4, r2);	 Catch:{ all -> 0x00a8 }
        r5.disconnect();	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        goto L_0x00b7;
    L_0x0242:
        r7 = move-exception;
        r17 = TAG;
        r0 = r17;
        com.douban.amonsul.StatLogger.e(r0, r7);
        r0 = r21;
        r0 = r0.mRetryCount;
        r17 = r0;
        r17 = r17 + -1;
        r0 = r17;
        if (r11 != r0) goto L_0x02c5;
    L_0x0256:
        r17 = new com.douban.amonsul.network.Response;
        r18 = "";
        r19 = -1;
        r20 = "can not be connect";
        r17.<init>(r18, r19, r20);
        goto L_0x00b7;
    L_0x0263:
        r17 = new com.douban.amonsul.network.Response;	 Catch:{ all -> 0x00a8 }
        r18 = r5.getInputStream();	 Catch:{ all -> 0x00a8 }
        r0 = r21;
        r1 = r18;
        r18 = r0.readStream(r1, r13);	 Catch:{ all -> 0x00a8 }
        r19 = r5.getResponseMessage();	 Catch:{ all -> 0x00a8 }
        r0 = r17;
        r1 = r18;
        r2 = r19;
        r0.<init>(r1, r4, r2);	 Catch:{ all -> 0x00a8 }
        r5.disconnect();	 Catch:{ MalformedURLException -> 0x00ad, IOException -> 0x0242, Exception -> 0x0283, AssertionError -> 0x02a4 }
        goto L_0x00b7;
    L_0x0283:
        r7 = move-exception;
        r17 = TAG;
        r0 = r17;
        com.douban.amonsul.StatLogger.e(r0, r7);
        r0 = r21;
        r0 = r0.mRetryCount;
        r17 = r0;
        r17 = r17 + -1;
        r0 = r17;
        if (r11 != r0) goto L_0x02c5;
    L_0x0297:
        r17 = new com.douban.amonsul.network.Response;
        r18 = "";
        r19 = -1;
        r20 = "can not be connect";
        r17.<init>(r18, r19, r20);
        goto L_0x00b7;
    L_0x02a4:
        r7 = move-exception;
        r17 = TAG;
        r0 = r17;
        com.douban.amonsul.StatLogger.e(r0, r7);
        r0 = r21;
        r0 = r0.mRetryCount;
        r17 = r0;
        r17 = r17 + -1;
        r0 = r17;
        if (r11 != r0) goto L_0x02c5;
    L_0x02b8:
        r17 = new com.douban.amonsul.network.Response;
        r18 = "";
        r19 = -1;
        r20 = "can not be connect";
        r17.<init>(r18, r19, r20);
        goto L_0x00b7;
    L_0x02c5:
        r11 = r11 + 1;
        goto L_0x0003;
    L_0x02c9:
        r17 = 0;
        goto L_0x00b7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.amonsul.network.URLClient.send(java.lang.String, com.douban.amonsul.network.NetWorker$Method, java.util.List, java.util.List, com.douban.amonsul.network.MultipartParameter):com.douban.amonsul.network.Response");
    }

    private String readStream(InputStream inputStream, boolean isGZIP) throws IOException {
        StringWriter stringWriter = new StringWriter();
        char[] buf = new char[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
        if (inputStream != null && isGZIP) {
            inputStream = new GZIPInputStream(inputStream);
        }
        InputStreamReader reader = new InputStreamReader(inputStream, HttpRequest.CHARSET_UTF8);
        while (true) {
            int l = reader.read(buf);
            if (l > 0) {
                stringWriter.write(buf, 0, l);
            } else {
                stringWriter.flush();
                stringWriter.close();
                reader.close();
                inputStream.close();
                return stringWriter.getBuffer().toString();
            }
        }
    }

    public byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(bytes);
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    private String getMessageByCode(int code) {
        switch (code) {
            case Downloads.STATUS_BAD_REQUEST /*400*/:
                return "Bad Request";
            case AppUri.GIFT_PACK /*401*/:
                return "Unauthorized";
            case AppUri.GIFT_LIST /*403*/:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case StatConstant.DEFAULT_MAX_EVENT_COUNT /*500*/:
                return "Internal Server Error";
            default:
                return "Unknown";
        }
    }

    public String getQuery(List<NameValuePair> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (NameValuePair pair : params) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(pair.getName(), HttpRequest.CHARSET_UTF8));
                result.append(SimpleComparison.EQUAL_TO_OPERATION);
                result.append(URLEncoder.encode(pair.getValue(), HttpRequest.CHARSET_UTF8));
            }
        } catch (Throwable e) {
            StatLogger.e(TAG, e);
        }
        return result.toString();
    }

    private String getQuery(String url, List<NameValuePair> params) {
        StringBuilder result = new StringBuilder(url);
        boolean first = true;
        if (url.contains("?")) {
            first = false;
        }
        try {
            for (NameValuePair pair : params) {
                if (first) {
                    first = false;
                    result.append("?");
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(pair.getName(), HttpRequest.CHARSET_UTF8));
                result.append(SimpleComparison.EQUAL_TO_OPERATION);
                result.append(URLEncoder.encode(pair.getValue(), HttpRequest.CHARSET_UTF8));
            }
            url = result.toString();
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
        return url;
    }
}
