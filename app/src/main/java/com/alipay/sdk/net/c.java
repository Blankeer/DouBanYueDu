package com.alipay.sdk.net;

import android.content.Context;
import android.text.TextUtils;
import com.alipay.sdk.exception.NetErrorException;
import org.apache.http.HttpResponse;

public final class c {
    static a a;

    private static HttpResponse a(Context context, String str, String str2, com.alipay.sdk.data.c cVar) throws NetErrorException {
        if (a == null) {
            a = new a(context, str);
        } else if (!TextUtils.equals(str, a.a)) {
            a.a = str;
        }
        if (cVar != null) {
            return a.a(str2, cVar);
        }
        return a.a(str2, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String a(org.apache.http.HttpResponse r7) throws com.alipay.sdk.exception.NetErrorException {
        /*
        r1 = r7.getStatusLine();
        r2 = r1.getStatusCode();
        r3 = r7.getEntity();
        r0 = 0;
        r0 = r3.getContent();	 Catch:{ Exception -> 0x003c, all -> 0x009b }
        r4 = r1.getStatusCode();	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r5 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r4 != r5) goto L_0x001b;
    L_0x0019:
        if (r0 != 0) goto L_0x004b;
    L_0x001b:
        r3 = new com.alipay.sdk.exception.NetErrorException;	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r4.<init>();	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r2 = r4.append(r2);	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r4 = " ";
        r2 = r2.append(r4);	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r1 = r1.getReasonPhrase();	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r1 = r2.append(r1);	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r3.<init>(r1);	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        throw r3;	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
    L_0x003c:
        r1 = move-exception;
        r1 = new com.alipay.sdk.exception.NetErrorException;	 Catch:{ all -> 0x0043 }
        r1.<init>();	 Catch:{ all -> 0x0043 }
        throw r1;	 Catch:{ all -> 0x0043 }
    L_0x0043:
        r1 = move-exception;
        r6 = r1;
        r1 = r0;
        r0 = r6;
    L_0x0047:
        r1.close();	 Catch:{ Exception -> 0x0099 }
    L_0x004a:
        throw r0;
    L_0x004b:
        r1 = r3.getContentEncoding();	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        if (r1 == 0) goto L_0x0063;
    L_0x0051:
        r1 = r1.getValue();	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r2 = "gzip";
        r1 = r1.contains(r2);	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        if (r1 == 0) goto L_0x0063;
    L_0x005d:
        r1 = new java.util.zip.GZIPInputStream;	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r1.<init>(r0);	 Catch:{ Exception -> 0x003c, all -> 0x00a0 }
        r0 = r1;
    L_0x0063:
        r4 = r3.getContentLength();	 Catch:{ Exception -> 0x003c }
        r1 = (int) r4;	 Catch:{ Exception -> 0x003c }
        if (r1 >= 0) goto L_0x00a5;
    L_0x006a:
        r1 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r2 = r1;
    L_0x006d:
        r1 = org.apache.http.util.EntityUtils.getContentCharSet(r3);	 Catch:{ Exception -> 0x003c }
        if (r1 != 0) goto L_0x0075;
    L_0x0073:
        r1 = "UTF-8";
    L_0x0075:
        r3 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x003c }
        r3.<init>(r0, r1);	 Catch:{ Exception -> 0x003c }
        r1 = new org.apache.http.util.CharArrayBuffer;	 Catch:{ Exception -> 0x003c }
        r1.<init>(r2);	 Catch:{ Exception -> 0x003c }
        r2 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r2 = new char[r2];	 Catch:{ Exception -> 0x003c }
    L_0x0083:
        r4 = r3.read(r2);	 Catch:{ Exception -> 0x003c }
        r5 = -1;
        if (r4 == r5) goto L_0x008f;
    L_0x008a:
        r5 = 0;
        r1.append(r2, r5, r4);	 Catch:{ Exception -> 0x003c }
        goto L_0x0083;
    L_0x008f:
        r1 = r1.toString();	 Catch:{ Exception -> 0x003c }
        r0.close();	 Catch:{ Exception -> 0x0097 }
    L_0x0096:
        return r1;
    L_0x0097:
        r0 = move-exception;
        goto L_0x0096;
    L_0x0099:
        r1 = move-exception;
        goto L_0x004a;
    L_0x009b:
        r1 = move-exception;
        r6 = r1;
        r1 = r0;
        r0 = r6;
        goto L_0x0047;
    L_0x00a0:
        r1 = move-exception;
        r6 = r1;
        r1 = r0;
        r0 = r6;
        goto L_0x0047;
    L_0x00a5:
        r2 = r1;
        goto L_0x006d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alipay.sdk.net.c.a(org.apache.http.HttpResponse):java.lang.String");
    }

    private static void a() {
        a = null;
    }
}
