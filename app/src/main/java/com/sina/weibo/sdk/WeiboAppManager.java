package com.sina.weibo.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import java.util.List;

public class WeiboAppManager {
    private static final String SDK_INT_FILE_NAME = "weibo_for_sdk.json";
    private static final String TAG;
    private static final String WEIBO_IDENTITY_ACTION = "com.sina.weibo.action.sdkidentity";
    private static final Uri WEIBO_NAME_URI;
    private static WeiboAppManager sInstance;
    private Context mContext;

    public static class WeiboInfo {
        private String mPackageName;
        private int mSupportApi;

        private void setPackageName(String packageName) {
            this.mPackageName = packageName;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        private void setSupportApi(int supportApi) {
            this.mSupportApi = supportApi;
        }

        public int getSupportApi() {
            return this.mSupportApi;
        }

        public boolean isLegal() {
            if (TextUtils.isEmpty(this.mPackageName) || this.mSupportApi <= 0) {
                return false;
            }
            return true;
        }

        public String toString() {
            return "WeiboInfo: PackageName = " + this.mPackageName + ", supportApi = " + this.mSupportApi;
        }
    }

    private com.sina.weibo.sdk.WeiboAppManager.WeiboInfo queryWeiboInfoByProvider(android.content.Context r15) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x006b in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JavaClass.getMethods(JavaClass.java:188)
*/
        /*
        r14 = this;
        r0 = r15.getContentResolver();
        r6 = 0;
        r1 = WEIBO_NAME_URI;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r2 = 0;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r3 = 0;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r4 = 0;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r5 = 0;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        if (r6 != 0) goto L_0x0019;
    L_0x0011:
        if (r6 == 0) goto L_0x0017;
    L_0x0013:
        r6.close();
        r6 = 0;
    L_0x0017:
        r13 = 0;
    L_0x0018:
        return r13;
    L_0x0019:
        r1 = "support_api";	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r11 = r6.getColumnIndex(r1);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r1 = "package";	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r8 = r6.getColumnIndex(r1);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r1 = r6.moveToFirst();	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        if (r1 == 0) goto L_0x0075;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
    L_0x002b:
        r12 = -1;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r10 = r6.getString(r11);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r12 = java.lang.Integer.parseInt(r10);	 Catch:{ NumberFormatException -> 0x0056 }
    L_0x0034:
        r9 = r6.getString(r8);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r1 = android.text.TextUtils.isEmpty(r9);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        if (r1 != 0) goto L_0x0075;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
    L_0x003e:
        r1 = com.sina.weibo.sdk.ApiUtils.validateWeiboSign(r15, r9);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        if (r1 == 0) goto L_0x0075;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
    L_0x0044:
        r13 = new com.sina.weibo.sdk.WeiboAppManager$WeiboInfo;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r13.<init>();	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r13.setPackageName(r9);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r13.setSupportApi(r12);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        if (r6 == 0) goto L_0x0018;
    L_0x0051:
        r6.close();
        r6 = 0;
        goto L_0x0018;
    L_0x0056:
        r7 = move-exception;
        r7.printStackTrace();	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        goto L_0x0034;
    L_0x005b:
        r7 = move-exception;
        r1 = TAG;	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        r2 = r7.getMessage();	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        com.sina.weibo.sdk.utils.LogUtil.e(r1, r2);	 Catch:{ Exception -> 0x005b, all -> 0x006d }
        if (r6 == 0) goto L_0x006b;
    L_0x0067:
        r6.close();
        r6 = 0;
    L_0x006b:
        r13 = 0;
        goto L_0x0018;
    L_0x006d:
        r1 = move-exception;
        if (r6 == 0) goto L_0x0074;
    L_0x0070:
        r6.close();
        r6 = 0;
    L_0x0074:
        throw r1;
    L_0x0075:
        if (r6 == 0) goto L_0x006b;
    L_0x0077:
        r6.close();
        r6 = 0;
        goto L_0x006b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sina.weibo.sdk.WeiboAppManager.queryWeiboInfoByProvider(android.content.Context):com.sina.weibo.sdk.WeiboAppManager$WeiboInfo");
    }

    static {
        TAG = WeiboAppManager.class.getName();
        WEIBO_NAME_URI = Uri.parse("content://com.sina.weibo.sdkProvider/query/package");
    }

    private WeiboAppManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static synchronized WeiboAppManager getInstance(Context context) {
        WeiboAppManager weiboAppManager;
        synchronized (WeiboAppManager.class) {
            if (sInstance == null) {
                sInstance = new WeiboAppManager(context);
            }
            weiboAppManager = sInstance;
        }
        return weiboAppManager;
    }

    public synchronized WeiboInfo getWeiboInfo() {
        return queryWeiboInfoInternal(this.mContext);
    }

    private WeiboInfo queryWeiboInfoInternal(Context context) {
        boolean hasWinfo1;
        boolean hasWinfo2 = true;
        WeiboInfo winfo1 = queryWeiboInfoByProvider(context);
        WeiboInfo winfo2 = queryWeiboInfoByAsset(context);
        if (winfo1 != null) {
            hasWinfo1 = true;
        } else {
            hasWinfo1 = false;
        }
        if (winfo2 == null) {
            hasWinfo2 = false;
        }
        if (hasWinfo1 && hasWinfo2) {
            if (winfo1.getSupportApi() >= winfo2.getSupportApi()) {
                return winfo1;
            }
            return winfo2;
        } else if (hasWinfo1) {
            return winfo1;
        } else {
            if (hasWinfo2) {
                return winfo2;
            }
            return null;
        }
    }

    private WeiboInfo queryWeiboInfoByAsset(Context context) {
        Intent intent = new Intent(WEIBO_IDENTITY_ACTION);
        intent.addCategory("android.intent.category.DEFAULT");
        List<ResolveInfo> list = context.getPackageManager().queryIntentServices(intent, 0);
        if (list == null || list.isEmpty()) {
            return null;
        }
        WeiboInfo weiboInfo = null;
        for (ResolveInfo ri : list) {
            if (!(ri.serviceInfo == null || ri.serviceInfo.applicationInfo == null || TextUtils.isEmpty(ri.serviceInfo.applicationInfo.packageName))) {
                WeiboInfo tmpWeiboInfo = parseWeiboInfoByAsset(ri.serviceInfo.applicationInfo.packageName);
                if (tmpWeiboInfo != null) {
                    if (weiboInfo == null) {
                        weiboInfo = tmpWeiboInfo;
                    } else if (weiboInfo.getSupportApi() < tmpWeiboInfo.getSupportApi()) {
                        weiboInfo = tmpWeiboInfo;
                    }
                }
            }
        }
        return weiboInfo;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.sina.weibo.sdk.WeiboAppManager.WeiboInfo parseWeiboInfoByAsset(java.lang.String r15) {
        /*
        r14 = this;
        r13 = -1;
        r10 = 0;
        r11 = android.text.TextUtils.isEmpty(r15);
        if (r11 == 0) goto L_0x000a;
    L_0x0008:
        r9 = r10;
    L_0x0009:
        return r9;
    L_0x000a:
        r3 = 0;
        r11 = r14.mContext;	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r12 = 2;
        r8 = r11.createPackageContext(r15, r12);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r1 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r11 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = new byte[r11];	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r11 = r8.getAssets();	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r12 = "weibo_for_sdk.json";
        r3 = r11.open(r12);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r6 = new java.lang.StringBuilder;	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r6.<init>();	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
    L_0x0027:
        r11 = 0;
        r12 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r5 = r3.read(r0, r11, r12);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        if (r5 != r13) goto L_0x0049;
    L_0x0030:
        r11 = r6.toString();	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r11 = android.text.TextUtils.isEmpty(r11);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        if (r11 != 0) goto L_0x0042;
    L_0x003a:
        r11 = r14.mContext;	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r11 = com.sina.weibo.sdk.ApiUtils.validateWeiboSign(r11, r15);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        if (r11 != 0) goto L_0x006f;
    L_0x0042:
        if (r3 == 0) goto L_0x0047;
    L_0x0044:
        r3.close();	 Catch:{ IOException -> 0x0064 }
    L_0x0047:
        r9 = r10;
        goto L_0x0009;
    L_0x0049:
        r11 = new java.lang.String;	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r12 = 0;
        r11.<init>(r0, r12, r5);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r6.append(r11);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        goto L_0x0027;
    L_0x0053:
        r2 = move-exception;
        r11 = TAG;	 Catch:{ all -> 0x00fb }
        r12 = r2.getMessage();	 Catch:{ all -> 0x00fb }
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);	 Catch:{ all -> 0x00fb }
        if (r3 == 0) goto L_0x0062;
    L_0x005f:
        r3.close();	 Catch:{ IOException -> 0x009d }
    L_0x0062:
        r9 = r10;
        goto L_0x0009;
    L_0x0064:
        r2 = move-exception;
        r11 = TAG;
        r12 = r2.getMessage();
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);
        goto L_0x0047;
    L_0x006f:
        r4 = new org.json.JSONObject;	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r11 = r6.toString();	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r4.<init>(r11);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r11 = "support_api";
        r12 = -1;
        r7 = r4.optInt(r11, r12);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r9 = new com.sina.weibo.sdk.WeiboAppManager$WeiboInfo;	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r9.<init>();	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r9.setPackageName(r15);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        r9.setSupportApi(r7);	 Catch:{ NameNotFoundException -> 0x0053, IOException -> 0x00a8, JSONException -> 0x00c3, Exception -> 0x00de }
        if (r3 == 0) goto L_0x0009;
    L_0x008c:
        r3.close();	 Catch:{ IOException -> 0x0091 }
        goto L_0x0009;
    L_0x0091:
        r2 = move-exception;
        r10 = TAG;
        r11 = r2.getMessage();
        com.sina.weibo.sdk.utils.LogUtil.e(r10, r11);
        goto L_0x0009;
    L_0x009d:
        r2 = move-exception;
        r11 = TAG;
        r12 = r2.getMessage();
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);
        goto L_0x0062;
    L_0x00a8:
        r2 = move-exception;
        r11 = TAG;	 Catch:{ all -> 0x00fb }
        r12 = r2.getMessage();	 Catch:{ all -> 0x00fb }
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);	 Catch:{ all -> 0x00fb }
        if (r3 == 0) goto L_0x0062;
    L_0x00b4:
        r3.close();	 Catch:{ IOException -> 0x00b8 }
        goto L_0x0062;
    L_0x00b8:
        r2 = move-exception;
        r11 = TAG;
        r12 = r2.getMessage();
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);
        goto L_0x0062;
    L_0x00c3:
        r2 = move-exception;
        r11 = TAG;	 Catch:{ all -> 0x00fb }
        r12 = r2.getMessage();	 Catch:{ all -> 0x00fb }
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);	 Catch:{ all -> 0x00fb }
        if (r3 == 0) goto L_0x0062;
    L_0x00cf:
        r3.close();	 Catch:{ IOException -> 0x00d3 }
        goto L_0x0062;
    L_0x00d3:
        r2 = move-exception;
        r11 = TAG;
        r12 = r2.getMessage();
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);
        goto L_0x0062;
    L_0x00de:
        r2 = move-exception;
        r11 = TAG;	 Catch:{ all -> 0x00fb }
        r12 = r2.getMessage();	 Catch:{ all -> 0x00fb }
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);	 Catch:{ all -> 0x00fb }
        if (r3 == 0) goto L_0x0062;
    L_0x00ea:
        r3.close();	 Catch:{ IOException -> 0x00ef }
        goto L_0x0062;
    L_0x00ef:
        r2 = move-exception;
        r11 = TAG;
        r12 = r2.getMessage();
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);
        goto L_0x0062;
    L_0x00fb:
        r10 = move-exception;
        if (r3 == 0) goto L_0x0101;
    L_0x00fe:
        r3.close();	 Catch:{ IOException -> 0x0102 }
    L_0x0101:
        throw r10;
    L_0x0102:
        r2 = move-exception;
        r11 = TAG;
        r12 = r2.getMessage();
        com.sina.weibo.sdk.utils.LogUtil.e(r11, r12);
        goto L_0x0101;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sina.weibo.sdk.WeiboAppManager.parseWeiboInfoByAsset(java.lang.String):com.sina.weibo.sdk.WeiboAppManager$WeiboInfo");
    }
}
