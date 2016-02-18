package com.igexin.push.core.a;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.network.NetWorker;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.douban.book.reader.fragment.GiftMessageEditFragment_;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.helper.WorksListUri;
import com.igexin.a.a.c.a;
import com.igexin.download.Downloads;
import com.igexin.push.config.SDKUrlConfig;
import com.igexin.push.core.a.a.b;
import com.igexin.push.core.a.a.c;
import com.igexin.push.core.a.a.d;
import com.igexin.push.core.a.a.f;
import com.igexin.push.core.a.a.g;
import com.igexin.push.core.a.a.h;
import com.igexin.push.core.a.a.i;
import com.igexin.push.core.a.a.j;
import com.igexin.push.core.a.a.l;
import com.igexin.push.core.bean.BaseAction;
import com.igexin.push.core.bean.PushTaskBean;
import com.igexin.push.core.c.w;
import com.igexin.push.d.k;
import com.igexin.push.extension.stub.IPushExtension;
import com.igexin.sdk.PushConsts;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.connect.common.Constants;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.UnresolvedAddressException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class e extends a implements k {
    private static Map a;
    private static Map b;
    private static e c;

    private e() {
        a = new HashMap();
        a.put(Integer.valueOf(0), new h());
        a.put(Integer.valueOf(5), new i());
        a.put(Integer.valueOf(37), new k());
        a.put(Integer.valueOf(9), new o());
        a.put(Integer.valueOf(26), new g());
        a.put(Integer.valueOf(28), new d());
        b = new HashMap();
        b.put("goto", new g());
        b.put("notification", new h());
        b.put("startapp", new com.igexin.push.core.a.a.k());
        b.put("null", new f());
        b.put("wakeupsdk", new l());
        b.put("startweb", new j());
        b.put("checkapp", new b());
        b.put("cleanext", new c());
        b.put("enablelog", new com.igexin.push.core.a.a.e());
        b.put("disablelog", new d());
        b.put("reportext", new i());
    }

    private void C() {
        com.igexin.push.core.i.a().a(com.igexin.push.core.k.NETWORK_SWITCH);
        a.b("CoreAction onInternalReceiver: network changed");
        if (com.igexin.push.core.f.a().j().getActiveNetworkInfo() == null || !com.igexin.push.core.f.a().j().getActiveNetworkInfo().isAvailable()) {
            com.igexin.push.core.g.i = false;
        } else {
            com.igexin.push.core.g.i = true;
        }
        a.b("CoreAction|network changed|" + com.igexin.push.core.g.i);
        e();
        com.igexin.push.core.f.a().g().c(true);
        a.b("CoreAction network changed start to check condition status....");
        if (u()) {
            t();
        }
    }

    private int D() {
        if (com.igexin.push.core.g.ai.isEmpty() && com.igexin.push.core.g.o) {
            Cursor a = com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, new String[]{SettingsJsonConstants.APP_STATUS_KEY}, new String[]{Constants.VIA_RESULT_SUCCESS}, null, null);
            if (a != null) {
                while (a.moveToNext()) {
                    try {
                        JSONObject jSONObject = new JSONObject(new String(com.igexin.a.b.a.c(a.getBlob(a.getColumnIndex(NetWorker.PARAM_KEY_APP_INFO)))));
                        String string = jSONObject.getString(WorksListUri.KEY_ID);
                        String string2 = jSONObject.getString(SocialConstants.PARAM_APP_ID);
                        String string3 = jSONObject.getString("messageid");
                        String string4 = jSONObject.getString("taskid");
                        String string5 = jSONObject.getString("appkey");
                        String a2 = a().a(string4, string3);
                        PushTaskBean pushTaskBean = new PushTaskBean();
                        pushTaskBean.setAppid(string2);
                        pushTaskBean.setMessageId(string3);
                        pushTaskBean.setTaskId(string4);
                        pushTaskBean.setId(string);
                        pushTaskBean.setAppKey(string5);
                        pushTaskBean.setCurrentActionid(1);
                        pushTaskBean.setStatus(a.getInt(a.getColumnIndex(SettingsJsonConstants.APP_STATUS_KEY)));
                        if (jSONObject.has("cdnType")) {
                            pushTaskBean.setCDNType(jSONObject.getBoolean("cdnType"));
                        }
                        if (jSONObject.has("condition")) {
                            b(jSONObject, pushTaskBean);
                        }
                        com.igexin.push.core.g.ai.put(a2, pushTaskBean);
                    } catch (JSONException e) {
                    }
                }
                a.close();
            }
            com.igexin.push.core.g.o = false;
        }
        return com.igexin.push.core.g.ai.size();
    }

    public static e a() {
        if (c == null) {
            c = new e();
        }
        return c;
    }

    private void a(int i, String str, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(i));
        com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, contentValues, new String[]{"taskid"}, new String[]{str});
    }

    private void a(com.igexin.push.c.c.c cVar, PushTaskBean pushTaskBean, String str, String str2) {
        cVar.a(new com.igexin.push.e.b.b(pushTaskBean, str, k()));
        com.igexin.push.core.g.al.put(str2, cVar);
    }

    private void a(List list) {
        Comparator fVar = new f(this);
        PackageManager packageManager = com.igexin.push.core.g.g.getPackageManager();
        List installedPackages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            try {
                PackageInfo packageInfo = (PackageInfo) installedPackages.get(i);
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if ((applicationInfo.flags & 1) <= 0) {
                    com.igexin.push.core.bean.l lVar = new com.igexin.push.core.bean.l();
                    lVar.b(applicationInfo.loadLabel(packageManager).toString());
                    lVar.d(applicationInfo.packageName);
                    lVar.c(String.valueOf(packageInfo.versionCode));
                    lVar.a(packageInfo.versionName);
                    list.add(lVar);
                }
            } catch (Exception e) {
            }
        }
        Collections.sort(list, fVar);
    }

    private void b(List list) {
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String str = (String) list.get(i);
                PushTaskBean pushTaskBean = (PushTaskBean) com.igexin.push.core.g.ai.get(str);
                pushTaskBean.setStatus(com.igexin.push.core.a.l);
                com.igexin.push.core.g.ai.put(str, pushTaskBean);
            }
        }
    }

    private void b(JSONObject jSONObject, PushTaskBean pushTaskBean) {
        try {
            JSONObject jSONObject2 = jSONObject.getJSONObject("condition");
            Map hashMap = new HashMap();
            if (jSONObject2.has("wifi")) {
                hashMap.put("wifi", jSONObject2.getString("wifi"));
            }
            if (jSONObject2.has("screenOn")) {
                hashMap.put("screenOn", jSONObject2.getString("screenOn"));
            }
            if (jSONObject2.has("ssid")) {
                hashMap.put("ssid", jSONObject2.getString("ssid"));
                if (jSONObject2.has("bssid")) {
                    hashMap.put("bssid", jSONObject2.getString("bssid"));
                }
            }
            if (jSONObject2.has("duration")) {
                String string = jSONObject2.getString("duration");
                if (string.contains("-")) {
                    int indexOf = string.indexOf("-");
                    String substring = string.substring(0, indexOf);
                    string = string.substring(indexOf + 1, string.length());
                    hashMap.put("startTime", substring);
                    hashMap.put("endTime", string);
                }
            }
            pushTaskBean.setConditionMap(hashMap);
        } catch (JSONException e) {
        }
    }

    private void e(Intent intent) {
        String stringExtra = intent.getStringExtra("taskid");
        String stringExtra2 = intent.getStringExtra("messageid");
        String stringExtra3 = intent.getStringExtra("actionid");
        String stringExtra4 = intent.getStringExtra("accesstoken");
        int intExtra = intent.getIntExtra("notifID", 0);
        NotificationManager notificationManager = (NotificationManager) com.igexin.push.core.g.g.getSystemService("notification");
        if (intExtra != 0) {
            notificationManager.cancel(intExtra);
        } else if (com.igexin.push.core.g.aj.get(stringExtra) != null) {
            notificationManager.cancel(((Integer) com.igexin.push.core.g.aj.get(stringExtra)).intValue());
        }
        if (stringExtra4.equals(com.igexin.push.core.g.au)) {
            b(stringExtra, stringExtra2, stringExtra3);
        }
    }

    private void f(Intent intent) {
        Throwable th;
        Cursor cursor = null;
        String stringExtra = intent.getStringExtra("taskid");
        String stringExtra2 = intent.getStringExtra("messageid");
        String stringExtra3 = intent.getStringExtra(SocialConstants.PARAM_APP_ID);
        String stringExtra4 = intent.getStringExtra("pkgname");
        ContentValues contentValues = new ContentValues();
        String str = "EXEC_" + stringExtra;
        contentValues.put("taskid", stringExtra);
        contentValues.put(SocialConstants.PARAM_APP_ID, stringExtra3);
        contentValues.put("key", str);
        contentValues.put("createtime", Long.valueOf(System.currentTimeMillis()));
        Cursor a;
        try {
            a = com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, new String[]{"key"}, new String[]{str}, null, null);
            if (a != null) {
                try {
                    if (a.getCount() == 0) {
                        com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, contentValues);
                        if (stringExtra4.equals(com.igexin.push.core.g.e)) {
                            if (stringExtra2 == null || stringExtra == null) {
                                if (a != null) {
                                    a.close();
                                    return;
                                }
                                return;
                            } else if (com.igexin.push.core.f.a() != null && b(stringExtra, stringExtra2) == com.igexin.push.core.b.success) {
                                a(stringExtra, stringExtra2, Constants.VIA_TO_TYPE_QQ_GROUP);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (a != null) {
                        a.close();
                    }
                } catch (Throwable th2) {
                    cursor = a;
                    th = th2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (a != null) {
                a.close();
            }
        } catch (Exception e2) {
            a = null;
            if (a != null) {
                a.close();
            }
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private void f(String str) {
        if (str != null && str.startsWith("package:")) {
            String substring = str.substring(8);
            if (com.igexin.push.core.c.f.a().d().containsKey(substring)) {
                com.igexin.push.core.c.f.a().d().remove(substring);
            }
        }
    }

    private void g(String str) {
        if (str != null && str.startsWith("package:")) {
            String substring = str.substring(8);
            try {
                PackageInfo packageInfo = com.igexin.push.core.g.g.getPackageManager().getPackageInfo(substring, 4);
                if (packageInfo != null) {
                    ServiceInfo[] serviceInfoArr = packageInfo.services;
                    if (serviceInfoArr != null) {
                        for (ServiceInfo serviceInfo : serviceInfoArr) {
                            if (com.igexin.push.core.a.o.equals(serviceInfo.name) || com.igexin.push.core.a.n.equals(serviceInfo.name) || com.igexin.push.core.a.p.equals(serviceInfo.name)) {
                                com.igexin.push.core.c.f.a().d().put(substring, serviceInfo.name);
                                return;
                            }
                        }
                    }
                }
            } catch (NameNotFoundException e) {
            }
        }
    }

    private void h(String str) {
        FileOutputStream fileOutputStream;
        Throwable th;
        FileOutputStream fileOutputStream2 = null;
        try {
            File file = new File(com.igexin.push.core.g.aa);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(com.igexin.push.core.g.aa);
            try {
                fileOutputStream.write(com.igexin.a.b.a.a(str).getBytes());
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e2) {
                fileOutputStream2 = fileOutputStream;
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (Exception e3) {
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e4) {
                    }
                }
                throw th;
            }
        } catch (Exception e5) {
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
        } catch (Throwable th3) {
            Throwable th4 = th3;
            fileOutputStream = null;
            th = th4;
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            throw th;
        }
    }

    public void A() {
        if (com.igexin.push.core.g.O < System.currentTimeMillis()) {
            com.igexin.push.core.c.f.a().a(false);
        }
    }

    public void B() {
        if (!com.igexin.push.core.g.af) {
            com.igexin.push.core.g.af = com.igexin.a.a.b.d.c().a(com.igexin.push.e.b.c.g(), false, true);
        }
        if (!com.igexin.push.core.g.ag) {
            com.igexin.push.core.g.ag = com.igexin.a.a.b.d.c().a(com.igexin.push.e.b.e.g(), true, true);
        }
        if (!com.igexin.push.core.g.ah) {
            com.igexin.push.core.f.a().c();
        }
    }

    public com.igexin.push.core.bean.f a(JSONObject jSONObject) {
        com.igexin.push.core.bean.f fVar = new com.igexin.push.core.bean.f();
        fVar.a(jSONObject.getString(ShareRequestParam.REQ_PARAM_VERSION));
        JSONArray jSONArray = jSONObject.getJSONArray("extensions");
        if (jSONArray == null || jSONArray.length() <= 0) {
            fVar.a(new HashMap());
        } else {
            Map hashMap = new HashMap();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = (JSONObject) jSONArray.get(i);
                com.igexin.push.core.bean.e eVar = new com.igexin.push.core.bean.e();
                eVar.a(jSONObject2.getInt(WorksListUri.KEY_ID));
                eVar.a(jSONObject2.getString(ShareRequestParam.REQ_PARAM_VERSION));
                eVar.b(jSONObject2.getString(SelectCountryActivity.EXTRA_COUNTRY_NAME));
                eVar.c(jSONObject2.getString("cls_name"));
                eVar.d(jSONObject2.getString(ShareUrlEditFragment_.URL_ARG));
                eVar.e(jSONObject2.getString(Keys.checksum));
                eVar.f(jSONObject2.getString("key"));
                if (jSONObject2.has("isdestroy")) {
                    eVar.a(jSONObject2.getBoolean("isdestroy"));
                }
                if (jSONObject2.has("effective")) {
                    String string = jSONObject2.getString("effective");
                    long j = 0;
                    if (string != null && string.length() <= 13) {
                        j = Long.parseLong(string);
                    }
                    eVar.a(j);
                }
                if (jSONObject2.has("loadTime")) {
                    eVar.b(jSONObject2.getLong("loadTime"));
                }
                hashMap.put(Integer.valueOf(eVar.a()), eVar);
            }
            fVar.a(hashMap);
        }
        return fVar;
    }

    public String a(com.igexin.push.core.bean.f fVar) {
        JSONObject jSONObject = new JSONObject();
        try {
            String a = fVar.a();
            Map b = fVar.b();
            String str = "[]";
            if (a != null) {
                jSONObject.put(ShareRequestParam.REQ_PARAM_VERSION, a);
            }
            if (b != null && b.size() > 0) {
                a = "[";
                for (Entry value : b.entrySet()) {
                    com.igexin.push.core.bean.e eVar = (com.igexin.push.core.bean.e) value.getValue();
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put(WorksListUri.KEY_ID, eVar.a());
                    jSONObject2.put(ShareRequestParam.REQ_PARAM_VERSION, eVar.b());
                    jSONObject2.put(SelectCountryActivity.EXTRA_COUNTRY_NAME, eVar.c());
                    jSONObject2.put("cls_name", eVar.d());
                    jSONObject2.put(ShareUrlEditFragment_.URL_ARG, eVar.e());
                    jSONObject2.put(Keys.checksum, eVar.f());
                    jSONObject2.put("isdestroy", eVar.g());
                    jSONObject2.put("effective", eVar.h());
                    jSONObject2.put("loadTime", eVar.i());
                    jSONObject2.put("key", eVar.j());
                    a = (a + jSONObject2.toString()) + ",";
                }
                str = (a.endsWith(",") ? a.substring(0, a.length() - 1) : a) + "]";
            }
            jSONObject.put("extensions", new JSONArray(str));
            return jSONObject.toString();
        } catch (JSONException e) {
            return null;
        }
    }

    public String a(String str, String str2) {
        return str + ":" + str2;
    }

    public String a(boolean z, int i) {
        String str;
        Throwable th;
        Cursor cursor;
        Cursor cursor2 = null;
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (i == -1) {
            try {
                str = format + "|" + com.igexin.push.core.g.A + "|" + "register" + "|" + com.igexin.push.core.g.t;
            } catch (Exception e) {
                str = null;
                if (cursor2 != null) {
                    cursor2.close();
                }
                return str;
            } catch (Throwable th2) {
                th = th2;
                cursor = null;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } else if (i == 0) {
            cursor = z ? com.igexin.push.core.f.a().k().a("bi", new String[]{SocialConstants.PARAM_TYPE}, new String[]{Constants.VIA_TO_TYPE_QQ_GROUP, Constants.VIA_SSO_LOGIN}, null, null) : com.igexin.push.core.f.a().k().a("bi", new String[]{SocialConstants.PARAM_TYPE}, new String[]{Constants.VIA_SSO_LOGIN}, null, null);
            if (cursor != null) {
                str = null;
                while (cursor.moveToNext()) {
                    try {
                        int i2 = cursor.getInt(cursor.getColumnIndexOrThrow("start_service_count"));
                        int i3 = cursor.getInt(cursor.getColumnIndexOrThrow("login_count"));
                        int i4 = cursor.getInt(cursor.getColumnIndexOrThrow("loginerror_nonetwork_count"));
                        int i5 = cursor.getInt(cursor.getColumnIndexOrThrow("loginerror_connecterror_count"));
                        int i6 = cursor.getInt(cursor.getColumnIndexOrThrow("online_time"));
                        int i7 = cursor.getInt(cursor.getColumnIndexOrThrow("network_time"));
                        int i8 = cursor.getInt(cursor.getColumnIndexOrThrow("running_time"));
                        String str2 = cursor.getString(cursor.getColumnIndexOrThrow(WBConstants.GAME_PARAMS_GAME_CREATE_TIME)) + " 00:00:00";
                        str = str == null ? str2 + "|" + com.igexin.push.core.g.A + "|" + "startservice" + "|" + i2 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "login" + "|" + i3 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "loginerror-nonetwork" + "|" + i4 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "loginerror-connecterror" + "|" + i5 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "online" + "|" + i6 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "network" + "|" + i7 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "running" + "|" + i8 : str + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "startservice" + "|" + i2 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "login" + "|" + i3 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "loginerror-nonetwork" + "|" + i4 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "loginerror-connecterror" + "|" + i5 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "online" + "|" + i6 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "network" + "|" + i7 + "\n" + str2 + "|" + com.igexin.push.core.g.A + "|" + "running" + "|" + i8;
                    } catch (Exception e2) {
                        cursor2 = cursor;
                    } catch (Throwable th3) {
                        th = th3;
                    }
                }
            } else {
                str = null;
            }
            cursor2 = cursor;
        } else if (i == 1) {
            long j = com.igexin.push.core.i.a().a;
            if (com.igexin.push.config.l.d > 0) {
                j = (long) (com.igexin.push.config.l.d * AppUri.OPEN_URL);
            }
            str = format + "|" + com.igexin.push.core.g.s + "|" + com.igexin.push.core.g.a + "|" + com.igexin.push.core.g.j + "|" + (com.igexin.push.config.l.a + "," + com.igexin.push.config.l.b) + "|" + j + "|";
        } else {
            str = i == 4 ? format + "|" + com.igexin.push.core.g.s + "|" + com.igexin.push.core.g.a + "|" : i == 5 ? format + "|" + com.igexin.push.core.g.s + "|" + com.igexin.push.core.g.a : null;
        }
        if (cursor2 != null) {
            cursor2.close();
        }
        return str;
    }

    public void a(int i) {
        Intent intent = new Intent();
        intent.addFlags(32);
        intent.setAction("com.igexin.sdk.action." + com.igexin.push.core.g.a);
        Bundle bundle = new Bundle();
        bundle.putInt(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.GET_SDKSERVICEPID);
        bundle.putInt("pid", i);
        intent.putExtras(bundle);
        com.igexin.push.core.f.a().a(intent);
    }

    public void a(int i, int i2, String str) {
        com.igexin.push.config.l.a = i;
        com.igexin.push.config.l.b = i2;
        com.igexin.push.config.a.a().b();
        com.igexin.push.a.a.c.c().d();
    }

    public void a(int i, String str) {
        com.igexin.push.config.l.d = i;
        com.igexin.push.config.a.a().c();
        if (com.igexin.push.core.g.m) {
            a.b("setHeartbeatInterval heartbeatReq");
            if (System.currentTimeMillis() - com.igexin.push.core.g.R > 5000) {
                com.igexin.push.core.g.R = System.currentTimeMillis();
                f();
            }
        }
    }

    public void a(Intent intent) {
        if (intent != null) {
            com.igexin.push.core.f.a().a(false);
            if (intent.hasExtra("op_app")) {
                com.igexin.push.core.g.C = intent.getStringExtra("op_app");
            } else {
                com.igexin.push.core.g.C = Table.STRING_DEFAULT_VALUE;
            }
            com.igexin.push.core.g.n = false;
            if (com.igexin.push.core.g.m) {
                l();
                com.igexin.push.core.g.n = true;
            }
        }
    }

    public void a(Bundle bundle) {
        String string = bundle.getString(DoubanAccountOperationFragment_.ACTION_ARG);
        if (string.equals("setTag")) {
            if (com.igexin.push.config.l.j) {
                b(bundle.getString("tags"));
            }
        } else if (string.equals("setSilentTime")) {
            if (com.igexin.push.config.l.k) {
                a(bundle.getInt("beginHour", 0), bundle.getInt("duration", 0), com.igexin.push.core.g.g.getPackageName());
            }
        } else if (string.equals("sendMessage")) {
            a.b("CoreAction onPushManagerMessage recevie action : sendMessage");
            if (com.igexin.push.config.l.i) {
                string = bundle.getString("taskid");
                byte[] byteArray = bundle.getByteArray("extraData");
                a.b("CoreAction receive broadcast msg data , task id : " + string + " ######@##@@@#");
                a(string, byteArray);
            }
        } else if (string.equals("stopService")) {
            com.igexin.push.core.f.a().a(com.igexin.push.core.g.g.getPackageName());
        } else if (string.equals("setHeartbeatInterval")) {
            if (com.igexin.push.config.l.l) {
                a(bundle.getInt("interval", 0), com.igexin.push.core.g.g.getPackageName());
            }
        } else if (string.equals("setSocketTimeout")) {
            if (com.igexin.push.config.l.m) {
                b(bundle.getInt("timeout", 0), com.igexin.push.core.g.g.getPackageName());
            }
        } else if (string.equals("sendFeedbackMessage")) {
            if (com.igexin.push.config.l.s && com.igexin.push.core.g.an <= Downloads.STATUS_SUCCESS) {
                string = bundle.getString("taskid");
                String string2 = bundle.getString("messageid");
                String string3 = bundle.getString("actionid");
                String str = string + ":" + string2 + ":" + string3;
                if (com.igexin.push.core.g.am.get(str) == null) {
                    long currentTimeMillis = System.currentTimeMillis();
                    PushTaskBean pushTaskBean = new PushTaskBean();
                    pushTaskBean.setTaskId(string);
                    pushTaskBean.setMessageId(string2);
                    pushTaskBean.setAppid(com.igexin.push.core.g.a);
                    pushTaskBean.setAppKey(com.igexin.push.core.g.b);
                    a(pushTaskBean, string3);
                    com.igexin.push.core.g.an++;
                    com.igexin.push.core.g.am.put(str, Long.valueOf(currentTimeMillis));
                }
            }
        } else if (string.equals("turnOffPush")) {
            com.igexin.push.core.f.a().b(com.igexin.push.core.g.g.getPackageName());
        } else if (string.equals("bindAlias")) {
            string = bundle.getString("alias");
            a.b("-> CoreAction onPushManagerMessage bindAlias...");
            c(string);
        } else if (string.equals("unbindAlias")) {
            string = bundle.getString("alias");
            boolean z = bundle.getBoolean("isSeft");
            a.b("-> CoreAction onPushManagerMessage unbindAlias...");
            a(string, z);
        }
    }

    public void a(PushTaskBean pushTaskBean) {
        com.igexin.push.c.c.e cVar = new com.igexin.push.c.c.c();
        cVar.a();
        cVar.c = "RCV" + pushTaskBean.getMessageId();
        cVar.d = com.igexin.push.core.g.s;
        cVar.a = (int) System.currentTimeMillis();
        com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, cVar);
        a.b("cdnreceive|" + pushTaskBean.getTaskId() + "|" + pushTaskBean.getMessageId());
    }

    public void a(PushTaskBean pushTaskBean, String str) {
        if (pushTaskBean.isCDNType()) {
            b(pushTaskBean, str);
        } else {
            a(pushTaskBean, str, "ok");
        }
    }

    public void a(PushTaskBean pushTaskBean, String str, String str2) {
        long currentTimeMillis = System.currentTimeMillis();
        String str3 = "{\"action\":\"pushmessage_feedback\",\"appid\":\"" + pushTaskBean.getAppid() + "\", \"id\":\"" + currentTimeMillis + "\", \"appkey\":\"" + pushTaskBean.getAppKey() + "\", \"messageid\":\"" + pushTaskBean.getMessageId() + "\",\"taskid\":\"" + pushTaskBean.getTaskId() + "\",\"actionid\": \"" + str + "\",\"result\":\"" + str2 + "\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
        com.igexin.push.c.c.e dVar = new com.igexin.push.c.c.d();
        dVar.a();
        dVar.a = (int) currentTimeMillis;
        dVar.d = "17258000";
        dVar.e = str3;
        dVar.g = com.igexin.push.core.g.s;
        com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, dVar);
        com.igexin.push.core.c.c a = com.igexin.push.core.c.c.a();
        if (a != null) {
            a.a(new com.igexin.push.core.bean.i(currentTimeMillis, str3, (byte) 3, currentTimeMillis));
        }
        a.b("feedback|" + pushTaskBean.getTaskId() + "|" + pushTaskBean.getMessageId() + "|" + str);
    }

    public void a(String str) {
        String str2 = "{\"action\":\"received\",\"id\":\"" + str + "\"}";
        com.igexin.push.c.c.e dVar = new com.igexin.push.c.c.d();
        dVar.a();
        dVar.a = (int) System.currentTimeMillis();
        dVar.d = "17258000";
        dVar.e = str2;
        dVar.g = com.igexin.push.core.g.s;
        com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, dVar);
    }

    public void a(String str, com.igexin.push.c.c.a aVar, PushTaskBean pushTaskBean) {
        com.igexin.a.a.b.d.c().a(new com.igexin.push.e.a.a(new com.igexin.push.core.d.c(str, aVar, pushTaskBean)), false, true);
    }

    public void a(String str, String str2, String str3, String str4) {
        Intent intent = new Intent("com.igexin.sdk.action.execute");
        intent.putExtra("taskid", str);
        intent.putExtra("messageid", str2);
        intent.putExtra(SocialConstants.PARAM_APP_ID, com.igexin.push.core.g.a);
        intent.putExtra("pkgname", com.igexin.push.core.g.e);
        com.igexin.push.core.f.a().a(intent);
    }

    public void a(String str, String str2, String str3, String str4, long j) {
        Intent intent = new Intent();
        intent.addFlags(32);
        intent.setAction("com.igexin.sdk.action." + com.igexin.push.core.g.a);
        Bundle bundle = new Bundle();
        bundle.putInt(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.THIRDPART_FEEDBACK);
        bundle.putString(SocialConstants.PARAM_APP_ID, str);
        bundle.putString("taskid", str2);
        bundle.putString("actionid", str3);
        bundle.putString("result", str4);
        bundle.putLong("timestamp", j);
        intent.putExtras(bundle);
        com.igexin.push.core.f.a().a(intent);
    }

    public void a(String str, boolean z) {
        if (!z || !com.igexin.a.b.a.b(com.igexin.push.core.g.s)) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - com.igexin.push.core.g.T > 5000) {
                String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date(currentTimeMillis));
                if (!format.equals(com.igexin.push.core.g.S)) {
                    com.igexin.push.core.c.f.a().d(format);
                    com.igexin.push.core.c.f.a().a(0);
                }
                if (com.igexin.push.core.g.U < 100) {
                    com.igexin.push.core.g.T = currentTimeMillis;
                    com.igexin.push.core.c.f.a().a(com.igexin.push.core.g.U + 1);
                    com.igexin.a.a.b.d.c().a(new com.igexin.push.e.a.c(new com.igexin.push.core.d.i(SDKUrlConfig.getAmpServiceUrl(), str, z)), false, true);
                }
            }
        }
    }

    public void a(String str, byte[] bArr) {
        if (com.igexin.push.core.g.s != null) {
            JSONObject jSONObject = new JSONObject();
            long currentTimeMillis = System.currentTimeMillis();
            try {
                jSONObject.put(DoubanAccountOperationFragment_.ACTION_ARG, "sendmessage");
                jSONObject.put(WorksListUri.KEY_ID, String.valueOf(currentTimeMillis));
                jSONObject.put(StatConstant.JSON_KEY_CELLID, com.igexin.push.core.g.s);
                jSONObject.put(SocialConstants.PARAM_APP_ID, com.igexin.push.core.g.a);
                jSONObject.put("taskid", str);
                String jSONObject2 = jSONObject.toString();
                com.igexin.push.c.c.e dVar = new com.igexin.push.c.c.d();
                dVar.a();
                dVar.a = (int) currentTimeMillis;
                dVar.d = com.igexin.push.core.g.s;
                dVar.e = jSONObject2;
                dVar.f = bArr;
                dVar.g = com.igexin.push.core.g.s;
                com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, dVar, true);
                if (str != null && str.startsWith("4T5@S_")) {
                    a.b("CoreAction sending lbs report message : " + jSONObject2);
                }
            } catch (JSONException e) {
                a.b("CoreAction" + e.toString());
            }
        }
    }

    public void a(boolean z) {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(byte[] r7) {
        /*
        r6 = this;
        r0 = 0;
        r1 = 0;
        r1 = android.util.Base64.decode(r7, r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = com.igexin.a.b.a.c(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2 = new java.lang.String;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2.<init>(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2.<init>();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "CoreAction parse sdk config from server resp : ";
        r2 = r2.append(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2 = r2.append(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.a.a.c.a.b(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2 = "result";
        r2 = r1.has(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r2 == 0) goto L_0x0330;
    L_0x0032:
        r2 = "result";
        r2 = r1.getString(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "ok";
        r2 = r3.equals(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r2 == 0) goto L_0x0330;
    L_0x0040:
        r2 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r4 = com.igexin.push.core.c.f.a();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r4.g(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2 = "config";
        r2 = r1.has(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r2 == 0) goto L_0x0330;
    L_0x0053:
        r2 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "config";
        r1 = r1.getString(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2.<init>(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = "sdk.uploadapplist.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0086;
    L_0x0066:
        r1 = "sdk.uploadapplist.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x007c;
    L_0x0074:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x0086;
    L_0x007c:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.h = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x0086:
        r1 = "sdk.feature.sendmessage.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x00ae;
    L_0x008e:
        r1 = "sdk.feature.sendmessage.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x00a4;
    L_0x009c:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x00ae;
    L_0x00a4:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.i = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x00ae:
        r1 = "sdk.readlocalcell.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x00d6;
    L_0x00b6:
        r1 = "sdk.readlocalcell.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x00cc;
    L_0x00c4:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x00d6;
    L_0x00cc:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.g = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x00d6:
        r1 = "sdk.ca.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x00fe;
    L_0x00de:
        r1 = "sdk.ca.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x00f4;
    L_0x00ec:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x00fe;
    L_0x00f4:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.n = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x00fe:
        r1 = "sdk.snl.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0126;
    L_0x0106:
        r1 = "sdk.snl.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x011c;
    L_0x0114:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x0126;
    L_0x011c:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.o = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x0126:
        r1 = "sdk.domainbackup.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x014e;
    L_0x012e:
        r1 = "sdk.domainbackup.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x0144;
    L_0x013c:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x014e;
    L_0x0144:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.f = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x014e:
        r1 = "sdk.feature.setsilenttime.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0186;
    L_0x0156:
        r1 = "sdk.feature.setsilenttime.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x016c;
    L_0x0164:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x0186;
    L_0x016c:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.k = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = com.igexin.push.config.l.k;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 != 0) goto L_0x0186;
    L_0x017a:
        r1 = com.igexin.push.config.l.b;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0186;
    L_0x017e:
        r1 = 12;
        r3 = 0;
        r4 = "server";
        r6.a(r1, r3, r4);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x0186:
        r1 = "sdk.snl.maxactiveflow";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x019b;
    L_0x018e:
        r1 = "sdk.snl.maxactiveflow";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x033c, Throwable -> 0x033a }
        r4 = (long) r1;	 Catch:{ Exception -> 0x033c, Throwable -> 0x033a }
        com.igexin.push.config.l.p = r4;	 Catch:{ Exception -> 0x033c, Throwable -> 0x033a }
    L_0x019b:
        r1 = "sdk.feature.settag.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x01c3;
    L_0x01a3:
        r1 = "sdk.feature.settag.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x01b9;
    L_0x01b1:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x01c3;
    L_0x01b9:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.j = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x01c3:
        r1 = "sdk.feature.setheartbeatinterval.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x01eb;
    L_0x01cb:
        r1 = "sdk.feature.setheartbeatinterval.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x01e1;
    L_0x01d9:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x01eb;
    L_0x01e1:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.l = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x01eb:
        r1 = "sdk.feature.setsockettimeout.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0213;
    L_0x01f3:
        r1 = "sdk.feature.setsockettimeout.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x0209;
    L_0x0201:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x0213;
    L_0x0209:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.m = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x0213:
        r1 = "sdk.guard.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x023b;
    L_0x021b:
        r1 = "sdk.guard.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x0231;
    L_0x0229:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x023b;
    L_0x0231:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.q = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x023b:
        r1 = "sdk.wakeupsdk.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0263;
    L_0x0243:
        r1 = "sdk.wakeupsdk.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x0259;
    L_0x0251:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x0263;
    L_0x0259:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.r = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x0263:
        r1 = "sdk.feature.feedback.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x028b;
    L_0x026b:
        r1 = "sdk.feature.feedback.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x0281;
    L_0x0279:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x028b;
    L_0x0281:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.s = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x028b:
        r1 = "sdk.watchout.app";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x029b;
    L_0x0293:
        r1 = "sdk.watchout.app";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.u = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x029b:
        r1 = "sdk.watchout.service";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x02ab;
    L_0x02a3:
        r1 = "sdk.watchout.service";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.v = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x02ab:
        r1 = "sdk.daemon.enable";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x02d3;
    L_0x02b3:
        r1 = "sdk.daemon.enable";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = "true";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 != 0) goto L_0x02c9;
    L_0x02c1:
        r3 = "false";
        r3 = r1.equals(r3);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x02d3;
    L_0x02c9:
        r1 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = r1.booleanValue();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        com.igexin.push.config.l.w = r1;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x02d3:
        r1 = "ext_infos";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0329;
    L_0x02db:
        r1 = "ext_infos";
        r1 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0329;
    L_0x02e3:
        r2 = "";
        r2 = r2.equals(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r2 != 0) goto L_0x0329;
    L_0x02eb:
        r2 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2.<init>(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = "version";
        r1 = r2.has(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r1 == 0) goto L_0x0329;
    L_0x02f8:
        r1 = "version";
        r3 = r2.getString(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1 = 1;
        r4 = com.igexin.push.config.l.t;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r4 == 0) goto L_0x033f;
    L_0x0303:
        r4 = com.igexin.push.config.l.t;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r4 = r4.a();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r3 = r3.equals(r4);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r3 == 0) goto L_0x033f;
    L_0x030f:
        if (r0 == 0) goto L_0x0329;
    L_0x0311:
        r0 = r6.a(r2);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        if (r0 == 0) goto L_0x0329;
    L_0x0317:
        r1 = new android.os.Message;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1.<init>();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r2 = com.igexin.push.core.a.i;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1.what = r2;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r1.obj = r0;	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r0 = com.igexin.push.core.f.a();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r0.a(r1);	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x0329:
        r0 = com.igexin.push.config.a.a();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
        r0.f();	 Catch:{ Exception -> 0x0331, Throwable -> 0x033a }
    L_0x0330:
        return;
    L_0x0331:
        r0 = move-exception;
        r0 = r0.toString();
        r6.d(r0);
        goto L_0x0330;
    L_0x033a:
        r0 = move-exception;
        goto L_0x0330;
    L_0x033c:
        r1 = move-exception;
        goto L_0x019b;
    L_0x033f:
        r0 = r1;
        goto L_0x030f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.push.core.a.e.a(byte[]):void");
    }

    public boolean a(long j) {
        Date date = new Date(j);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        int i = instance.get(11);
        int i2 = com.igexin.push.config.l.a + com.igexin.push.config.l.b;
        if (i2 >= 24) {
            i2 -= 24;
        }
        if (com.igexin.push.config.l.b == 0) {
            return false;
        }
        if (com.igexin.push.config.l.a < i2) {
            if (i >= com.igexin.push.config.l.a && i < i2) {
                return true;
            }
        } else if (com.igexin.push.config.l.a > i2) {
            if (i >= 0 && i < i2) {
                return true;
            }
            if (i >= com.igexin.push.config.l.a && i < 24) {
                return true;
            }
        }
        return false;
    }

    public boolean a(com.igexin.a.a.d.d dVar) {
        switch (dVar.b()) {
            case -2047:
                a.b("disconnected|network");
                com.igexin.push.core.i.a().a(com.igexin.push.core.k.NETWORK_ERROR);
                w.d();
                if ((dVar.M instanceof ClosedChannelException) || (dVar.M instanceof SocketTimeoutException) || (dVar.M instanceof UnknownHostException) || (dVar.M instanceof UnresolvedAddressException) || (dVar.M instanceof UnknownServiceException)) {
                    w.a();
                }
                if (com.igexin.push.core.g.j && com.igexin.push.core.g.k) {
                    if (com.igexin.push.core.g.m) {
                        com.igexin.push.core.g.m = false;
                        m();
                    }
                    com.igexin.push.core.f.a().g().c(false);
                } else if (com.igexin.push.core.g.m) {
                    com.igexin.push.core.g.m = false;
                    m();
                }
                com.igexin.a.a.b.d.c().a(com.igexin.a.a.b.a.a.g.class);
                break;
            case -2045:
                a.b("disconnected|user");
                break;
            default:
                return true;
        }
        return false;
    }

    public boolean a(com.igexin.push.c.c.e eVar) {
        if (eVar == null) {
            return false;
        }
        a aVar = (a) a.get(Integer.valueOf(eVar.i));
        if (aVar != null) {
            aVar.a((Object) eVar);
        }
        com.igexin.push.e.b.c.g().h();
        return true;
    }

    public boolean a(Object obj) {
        com.igexin.push.d.j g = com.igexin.push.core.f.a().g();
        if ((obj instanceof com.igexin.push.c.c.e) && g != null) {
            g.a((com.igexin.push.c.c.e) obj);
        } else if (obj instanceof com.igexin.a.a.b.a.a.b) {
            if (com.igexin.push.core.g.m) {
                com.igexin.push.core.g.m = false;
                m();
            }
        } else if (!(obj instanceof com.igexin.a.a.b.a.a.a)) {
            if (obj instanceof com.igexin.push.c.b.a) {
                g.c(false);
            } else if (obj instanceof com.igexin.push.c.b.b) {
                g.c(true);
            }
        }
        a.b("doHandleFilter return false");
        return false;
    }

    public boolean a(String str, String str2, String str3) {
        Bundle bundle = new Bundle();
        bundle.putString("taskid", str);
        bundle.putString("messageid", str2);
        bundle.putString("actionid", str3);
        Message message = new Message();
        message.what = com.igexin.push.core.a.h;
        message.obj = bundle;
        return com.igexin.push.core.f.a().a(message);
    }

    public boolean a(JSONObject jSONObject, PushTaskBean pushTaskBean) {
        List arrayList = new ArrayList();
        try {
            Object obj;
            JSONArray jSONArray = jSONObject.getJSONArray("action_chains");
            for (int i = 0; i < jSONArray.length(); i++) {
                String string = ((JSONObject) jSONArray.get(i)).getString(SocialConstants.PARAM_TYPE);
                if (string != null) {
                    for (IPushExtension isActionSupported : com.igexin.push.extension.a.a().c()) {
                        if (isActionSupported.isActionSupported(string)) {
                            obj = 1;
                            break;
                        }
                    }
                    obj = null;
                    if (obj == null && b.get(string) == null) {
                        return false;
                    }
                }
            }
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                JSONObject jSONObject2 = (JSONObject) jSONArray.get(i2);
                String string2 = jSONObject2.getString(SocialConstants.PARAM_TYPE);
                if (string2 != null) {
                    BaseAction baseAction = null;
                    for (IPushExtension parseAction : com.igexin.push.extension.a.a().c()) {
                        baseAction = parseAction.parseAction(jSONObject2);
                        if (baseAction != null) {
                            break;
                        }
                    }
                    if (baseAction == null) {
                        com.igexin.push.core.a.a.a aVar = (com.igexin.push.core.a.a.a) b.get(string2);
                        if (aVar != null) {
                            obj = aVar.a(jSONObject2);
                            if (obj != null) {
                                obj.setSupportExt(false);
                            }
                            if (obj == null) {
                                return false;
                            }
                            arrayList.add(obj);
                        }
                    }
                    BaseAction baseAction2 = baseAction;
                    if (obj == null) {
                        return false;
                    }
                    arrayList.add(obj);
                }
            }
        } catch (JSONException e) {
        }
        pushTaskBean.setActionChains(arrayList);
        return true;
    }

    public boolean a(JSONObject jSONObject, byte[] bArr, boolean z) {
        Cursor a;
        Cursor cursor;
        Throwable th;
        try {
            if (jSONObject.has(DoubanAccountOperationFragment_.ACTION_ARG) && jSONObject.getString(DoubanAccountOperationFragment_.ACTION_ARG).equals("pushmessage")) {
                String string = jSONObject.getString(WorksListUri.KEY_ID);
                String string2 = jSONObject.getString(SocialConstants.PARAM_APP_ID);
                String string3 = jSONObject.getString("messageid");
                String string4 = jSONObject.getString("taskid");
                String string5 = jSONObject.getString("appkey");
                JSONArray jSONArray = jSONObject.getJSONArray("action_chains");
                a.b("pushmessage|" + string4 + "|" + string3 + "|" + string2 + "|" + z);
                if (!(string2 == null || string == null || string3 == null || string4 == null || jSONArray == null || !string2.equals(com.igexin.push.core.g.a))) {
                    PushTaskBean pushTaskBean = new PushTaskBean();
                    pushTaskBean.setAppid(string2);
                    pushTaskBean.setMessageId(string3);
                    pushTaskBean.setTaskId(string4);
                    pushTaskBean.setId(string);
                    pushTaskBean.setAppKey(string5);
                    pushTaskBean.setCurrentActionid(1);
                    if (jSONObject.has("cdnType")) {
                        pushTaskBean.setCDNType(jSONObject.getBoolean("cdnType"));
                    }
                    String a2 = a().a(string4, string3);
                    if (z) {
                        a().a(pushTaskBean, Constants.VIA_RESULT_SUCCESS);
                        if (a().a(System.currentTimeMillis())) {
                            return true;
                        }
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("messageid", string3);
                    contentValues.put("taskid", string4);
                    contentValues.put(SocialConstants.PARAM_APP_ID, string2);
                    contentValues.put("key", "CACHE_" + a2);
                    contentValues.put(NetWorker.PARAM_KEY_APP_INFO, com.igexin.a.b.a.b(jSONObject.toString().getBytes()));
                    contentValues.put("createtime", Long.valueOf(System.currentTimeMillis()));
                    if (bArr != null) {
                        contentValues.put("msgextra", bArr);
                        pushTaskBean.setMsgExtra(bArr);
                    }
                    if (jSONArray.length() > 0 && !a().a(jSONObject, pushTaskBean)) {
                        return true;
                    }
                    if (z) {
                        try {
                            a = com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, new String[]{"taskid"}, new String[]{string4}, null, null);
                            if (a != null) {
                                try {
                                    if (a.getCount() == 0) {
                                        if (jSONObject.has("condition")) {
                                            b(jSONObject, pushTaskBean);
                                            pushTaskBean.setStatus(com.igexin.push.core.a.k);
                                            contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(com.igexin.push.core.a.k));
                                        } else {
                                            pushTaskBean.setStatus(com.igexin.push.core.a.l);
                                            contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(com.igexin.push.core.a.l));
                                        }
                                        com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, contentValues);
                                        com.igexin.push.core.g.ai.put(a2, pushTaskBean);
                                        if (jSONObject.has("condition")) {
                                            t();
                                        } else {
                                            a().a(string4, string3, com.igexin.push.core.g.a, com.igexin.push.core.g.e);
                                        }
                                    } else if (a == null) {
                                        return true;
                                    } else {
                                        a.close();
                                        return true;
                                    }
                                } catch (Exception e) {
                                    cursor = a;
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                    return true;
                                } catch (Throwable th2) {
                                    th = th2;
                                    if (a != null) {
                                        a.close();
                                    }
                                    throw th;
                                }
                            }
                            if (a != null) {
                                a.close();
                            }
                        } catch (Exception e2) {
                            cursor = null;
                            if (cursor != null) {
                                cursor.close();
                            }
                            return true;
                        } catch (Throwable th3) {
                            th = th3;
                            a = null;
                            if (a != null) {
                                a.close();
                            }
                            throw th;
                        }
                    }
                    if (jSONObject.has("condition")) {
                        b(jSONObject, pushTaskBean);
                    }
                    pushTaskBean.setStatus(com.igexin.push.core.a.l);
                    com.igexin.push.core.g.ai.put(a2, pushTaskBean);
                }
            }
        } catch (Exception e3) {
        }
        return true;
    }

    public com.igexin.push.core.b b(String str, String str2) {
        com.igexin.push.core.b bVar = com.igexin.push.core.b.success;
        PushTaskBean pushTaskBean = (PushTaskBean) com.igexin.push.core.g.ai.get(str + ":" + str2);
        if (pushTaskBean == null) {
            return com.igexin.push.core.b.stop;
        }
        int i = 0;
        com.igexin.push.core.b bVar2 = bVar;
        for (BaseAction baseAction : pushTaskBean.getActionChains()) {
            bVar = com.igexin.push.core.b.stop;
            if (baseAction == null) {
                return bVar;
            }
            com.igexin.push.core.b bVar3;
            for (IPushExtension prepareExecuteAction : com.igexin.push.extension.a.a().c()) {
                bVar = prepareExecuteAction.prepareExecuteAction(pushTaskBean, baseAction);
                if (bVar != com.igexin.push.core.b.stop) {
                    bVar3 = bVar;
                    break;
                }
            }
            bVar3 = bVar;
            if (bVar3 == com.igexin.push.core.b.stop) {
                com.igexin.push.core.a.a.a aVar = (com.igexin.push.core.a.a.a) b.get(baseAction.getType());
                if (aVar == null) {
                    return bVar3;
                }
                bVar3 = aVar.a(pushTaskBean, baseAction);
                if (bVar3 == com.igexin.push.core.b.stop) {
                    return bVar3;
                }
            }
            i = bVar3 == com.igexin.push.core.b.wait ? i + 1 : i;
            bVar2 = bVar2 == com.igexin.push.core.b.success ? bVar3 : bVar2;
        }
        if (!(i == 0 || com.igexin.push.core.g.a(str, Integer.valueOf(i), true))) {
            bVar2 = com.igexin.push.core.b.success;
        }
        return bVar2;
    }

    public void b() {
        d();
    }

    public void b(int i, String str) {
        com.igexin.push.config.l.e = i;
        com.igexin.push.config.a.a().d();
    }

    public void b(Intent intent) {
        if (intent != null && intent.hasExtra("isSlave") && intent.getBooleanExtra("isSlave", false)) {
            com.igexin.push.core.f.a().a(true);
            if (intent.hasExtra("op_app")) {
                com.igexin.push.core.g.C = intent.getStringExtra("op_app");
            } else {
                com.igexin.push.core.g.C = Table.STRING_DEFAULT_VALUE;
            }
            if (com.igexin.push.core.g.m) {
                l();
            }
        }
    }

    public void b(PushTaskBean pushTaskBean, String str) {
        String str2 = pushTaskBean.getMessageId() + "|" + str;
        com.igexin.push.c.c.c cVar;
        if (com.igexin.push.core.g.al.containsKey(str2)) {
            cVar = (com.igexin.push.c.c.c) com.igexin.push.core.g.al.get(str2);
            if (cVar.c() < 2) {
                com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, cVar);
                cVar.a(cVar.c() + 1);
                a(cVar, pushTaskBean, str, str2);
            }
        } else {
            cVar = new com.igexin.push.c.c.c();
            long currentTimeMillis = System.currentTimeMillis();
            cVar.a();
            cVar.c = "FDB" + pushTaskBean.getMessageId() + "|" + pushTaskBean.getTaskId() + "|" + str + "|" + "ok" + "|" + currentTimeMillis;
            cVar.d = com.igexin.push.core.g.s;
            cVar.a = (int) currentTimeMillis;
            com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, cVar);
            a(cVar, pushTaskBean, str, str2);
        }
        a.b("cdnfeedback|" + pushTaskBean.getTaskId() + "|" + pushTaskBean.getMessageId() + "|" + str);
    }

    public void b(String str) {
        if (com.igexin.push.core.g.s != null) {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                String str2 = "{\"action\":\"set_tag\",\"id\":\"" + currentTimeMillis + "\", \"cid\":\"" + com.igexin.push.core.g.s + "\", \"appid\":\"" + com.igexin.push.core.g.a + "\", \"tags\":\"" + URLEncoder.encode(str, "utf-8") + "\"}";
                com.igexin.push.core.c.c a = com.igexin.push.core.c.c.a();
                if (a != null) {
                    a.a(new com.igexin.push.core.bean.i(currentTimeMillis, str2, (byte) 2, currentTimeMillis));
                }
                com.igexin.push.c.c.d dVar = new com.igexin.push.c.c.d();
                dVar.a();
                dVar.d = "17258000";
                dVar.e = str2;
                com.igexin.a.a.b.d.c().a(SDKUrlConfig.getCmAddress(), 3, com.igexin.push.core.f.a().f(), dVar, false);
                a.b("settag");
            } catch (Exception e) {
            }
        }
    }

    public boolean b(String str, String str2, String str3) {
        Cursor cursor;
        BaseAction baseAction;
        com.igexin.push.core.a.a.a aVar;
        Throwable th;
        PushTaskBean pushTaskBean = (PushTaskBean) com.igexin.push.core.g.ai.get(str + ":" + str2);
        if (pushTaskBean == null) {
            Cursor a;
            try {
                a = com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, new String[]{"taskid", "messageid"}, new String[]{str, str2}, null, null);
                if (a != null) {
                    try {
                        if (a.getCount() > 0) {
                            while (a.moveToNext()) {
                                a(new JSONObject(new String(com.igexin.a.b.a.c(a.getBlob(a.getColumnIndexOrThrow(NetWorker.PARAM_KEY_APP_INFO))))), a.getBlob(a.getColumnIndexOrThrow("msgextra")), false);
                                PushTaskBean pushTaskBean2 = (PushTaskBean) com.igexin.push.core.g.ai.get(str + ":" + str2);
                                if (pushTaskBean2 == null) {
                                    if (a != null) {
                                        a.close();
                                    }
                                    return false;
                                }
                                pushTaskBean = pushTaskBean2;
                            }
                            if (a != null) {
                                a.close();
                            }
                        } else {
                            if (a != null) {
                                a.close();
                            }
                            return false;
                        }
                    } catch (Exception e) {
                        cursor = a;
                        if (cursor != null) {
                            cursor.close();
                        }
                        a().a(pushTaskBean, str3);
                        baseAction = pushTaskBean.getBaseAction(str3);
                        if (baseAction != null) {
                            return false;
                        }
                        if (baseAction.isSupportExt()) {
                            for (IPushExtension executeAction : com.igexin.push.extension.a.a().c()) {
                                if (executeAction.executeAction(pushTaskBean, baseAction)) {
                                    return true;
                                }
                            }
                        }
                        aVar = (com.igexin.push.core.a.a.a) b.get(baseAction.getType());
                        if (aVar != null) {
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (a != null) {
                            a.close();
                        }
                        throw th;
                    }
                }
                if (a != null) {
                    a.close();
                }
                return false;
            } catch (Exception e2) {
                cursor = null;
                if (cursor != null) {
                    cursor.close();
                }
                a().a(pushTaskBean, str3);
                baseAction = pushTaskBean.getBaseAction(str3);
                if (baseAction != null) {
                    return false;
                }
                if (baseAction.isSupportExt()) {
                    while (r2.hasNext()) {
                        if (executeAction.executeAction(pushTaskBean, baseAction)) {
                            return true;
                        }
                    }
                }
                aVar = (com.igexin.push.core.a.a.a) b.get(baseAction.getType());
                if (aVar != null) {
                }
            } catch (Throwable th3) {
                th = th3;
                a = null;
                if (a != null) {
                    a.close();
                }
                throw th;
            }
        }
        a().a(pushTaskBean, str3);
        baseAction = pushTaskBean.getBaseAction(str3);
        if (baseAction != null) {
            return false;
        }
        if (baseAction.isSupportExt()) {
            while (r2.hasNext()) {
                if (executeAction.executeAction(pushTaskBean, baseAction)) {
                    return true;
                }
            }
        }
        aVar = (com.igexin.push.core.a.a.a) b.get(baseAction.getType());
        return (aVar != null || pushTaskBean.isStop()) ? false : aVar.b(pushTaskBean, baseAction);
    }

    public boolean b(String str, String str2, String str3, String str4) {
        byte[] bytes;
        a.b("startapp|broadcastPayload");
        Intent intent = new Intent();
        intent.addFlags(32);
        Bundle bundle = new Bundle();
        bundle.putInt(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.GET_MSG_DATA);
        bundle.putString("taskid", str);
        bundle.putString("messageid", str2);
        bundle.putString(SocialConstants.PARAM_APP_ID, str3);
        bundle.putString("payloadid", str2 + ":" + str);
        bundle.putString(ShareRequestParam.REQ_PARAM_PACKAGENAME, com.igexin.push.core.g.e);
        intent.setAction("com.igexin.sdk.action." + str3);
        if (str4 != null) {
            bytes = str4.getBytes();
        } else {
            PushTaskBean pushTaskBean = (PushTaskBean) com.igexin.push.core.g.ai.get(a(str, str2));
            bytes = pushTaskBean != null ? pushTaskBean.getMsgExtra() : null;
        }
        if (bytes != null) {
            a.b("startapp|broadcast|payload is " + new String(bytes));
        } else {
            a.b("startapp|broadcast|payload is empty!");
        }
        bundle.putByteArray("payload", bytes);
        intent.putExtras(bundle);
        if (bytes != null) {
            try {
                a.b("startapp|broadcast|" + str3 + "|" + new String(bytes, "utf-8"));
            } catch (Exception e) {
                a.b("startapp|broadcast|error|" + e.toString());
                return false;
            }
        }
        com.igexin.push.core.g.g.sendBroadcast(intent);
        return true;
    }

    public com.igexin.push.c.c.i c() {
        com.igexin.push.c.c.i iVar = new com.igexin.push.c.c.i();
        iVar.a = com.igexin.push.core.g.r;
        iVar.b = (byte) 0;
        iVar.c = MotionEventCompat.ACTION_POINTER_INDEX_MASK;
        iVar.d = com.igexin.push.core.g.a;
        try {
            if (com.igexin.push.f.a.a()) {
                List arrayList = new ArrayList();
                WifiManager wifiManager = (WifiManager) com.igexin.push.core.g.g.getSystemService("wifi");
                if (wifiManager != null && wifiManager.isWifiEnabled()) {
                    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo != null) {
                        String ssid = connectionInfo.getSSID();
                        String bssid = connectionInfo.getBSSID();
                        if (ssid != null) {
                            com.igexin.push.c.c.j jVar = new com.igexin.push.c.c.j();
                            jVar.a = (byte) 1;
                            jVar.b = ssid;
                            arrayList.add(jVar);
                        }
                        if (bssid != null) {
                            com.igexin.push.c.c.j jVar2 = new com.igexin.push.c.c.j();
                            jVar2.a = (byte) 4;
                            jVar2.b = bssid;
                            arrayList.add(jVar2);
                        }
                    }
                }
                if (arrayList.size() > 0) {
                    iVar.e = arrayList;
                }
            }
        } catch (Exception e) {
        }
        return iVar;
    }

    public void c(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            try {
                String action = intent.getAction();
                if (PushConsts.ACTION_BROADCAST_NETWORK_CHANGE.equals(action)) {
                    if (com.igexin.a.a.b.d.c() != null) {
                        C();
                    }
                } else if ("com.igexin.sdk.action.snlrefresh".equals(action) || com.igexin.push.core.g.W.equals(action) || "com.igexin.sdk.action.snlretire".equals(action)) {
                    com.igexin.push.core.f.a().h().a(intent);
                } else if ("com.igexin.sdk.action.execute".equals(action)) {
                    f(intent);
                } else if ("com.igexin.sdk.action.doaction".equals(action)) {
                    e(intent);
                } else if ("android.intent.action.TIME_SET".equals(action)) {
                    if (com.igexin.push.config.l.b != 0) {
                        com.igexin.push.a.a.c.c().d();
                    }
                } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                    com.igexin.push.core.g.q = 1;
                    if (u()) {
                        t();
                    }
                } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    com.igexin.push.core.g.q = 0;
                } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    g(intent.getDataString());
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    f(intent.getDataString());
                } else if ("com.igexin.sdk.action.core.clearmsg".equals(action)) {
                    com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, null);
                } else if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action) && intent.getIntExtra("wifi_state", 0) == 3) {
                    com.igexin.push.core.f.a().d();
                }
            } catch (Exception e) {
                a.b("CoreAction" + e.toString());
            }
        }
    }

    public void c(String str) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - com.igexin.push.core.g.T > 5000) {
            String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date(currentTimeMillis));
            if (!format.equals(com.igexin.push.core.g.S)) {
                com.igexin.push.core.c.f.a().d(format);
                com.igexin.push.core.c.f.a().a(0);
            }
            a.b("-> CoreRuntimeInfo.opAliasTimes:" + com.igexin.push.core.g.U);
            if (com.igexin.push.core.g.U < 100) {
                a.b("requestService bindAlias HttpTask ...");
                com.igexin.push.core.g.T = currentTimeMillis;
                com.igexin.push.core.c.f.a().a(com.igexin.push.core.g.U + 1);
                com.igexin.a.a.b.d.c().a(new com.igexin.push.e.a.c(new com.igexin.push.core.d.b(SDKUrlConfig.getAmpServiceUrl(), str)), false, true);
            }
        }
    }

    public int d() {
        Cursor a;
        Cursor cursor;
        Throwable th;
        if (!com.igexin.push.core.g.j || !com.igexin.push.core.g.k || a(System.currentTimeMillis()) || !com.igexin.push.f.a.b()) {
            return -1;
        }
        boolean z;
        if (com.igexin.push.core.g.l) {
            com.igexin.push.core.g.l = !com.igexin.push.core.g.l;
            com.igexin.push.core.g.L = (((long) Math.abs(new Random().nextInt() % 24)) * com.umeng.analytics.a.h) + System.currentTimeMillis();
        }
        w.b();
        if (com.igexin.push.core.g.r == 0) {
            a.b("registerReqBefore|" + com.igexin.push.core.g.A);
            z = com.igexin.push.core.f.a().g().a(new StringBuilder().append("R-").append(com.igexin.push.core.g.A).toString(), new com.igexin.push.c.c.f(com.igexin.push.core.g.u, com.igexin.push.core.g.v, com.igexin.push.core.g.A, com.igexin.push.core.g.a), true) >= 0;
            a.b("registerReq|" + z + "|" + com.igexin.push.core.g.A);
        } else {
            com.igexin.push.c.c.e c = c();
            a.b("loginReqBefore|" + c.a);
            z = com.igexin.push.core.f.a().g().a(new StringBuilder().append("S-").append(String.valueOf(com.igexin.push.core.g.r)).toString(), c, true) >= 0;
            a.b("loginReq|" + z + "|" + com.igexin.push.core.g.s);
        }
        if (z) {
            return 1;
        }
        try {
            String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            a = com.igexin.push.core.f.a().k().a("bi", new String[]{SocialConstants.PARAM_TYPE}, new String[]{Constants.VIA_TO_TYPE_QQ_GROUP}, null, null);
            if (a != null) {
                try {
                    if (a.getCount() == 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("loginerror_nonetwork_count", Integer.valueOf(1));
                        contentValues.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, format);
                        contentValues.put(SocialConstants.PARAM_TYPE, Constants.VIA_TO_TYPE_QQ_GROUP);
                        com.igexin.push.core.f.a().k().a("bi", contentValues);
                    } else {
                        int i = 0;
                        while (a.moveToNext()) {
                            String string = a.getString(a.getColumnIndexOrThrow(WBConstants.GAME_PARAMS_GAME_CREATE_TIME));
                            String string2 = a.getString(a.getColumnIndexOrThrow(WorksListUri.KEY_ID));
                            ContentValues contentValues2;
                            if (format.equals(string)) {
                                i = a.getInt(a.getColumnIndexOrThrow("loginerror_nonetwork_count"));
                                contentValues2 = new ContentValues();
                                contentValues2.put("loginerror_nonetwork_count", Integer.valueOf(i + 1));
                                com.igexin.push.core.f.a().k().a("bi", contentValues2, new String[]{WorksListUri.KEY_ID}, new String[]{string2});
                            } else {
                                contentValues2 = new ContentValues();
                                contentValues2.put(SocialConstants.PARAM_TYPE, Constants.VIA_SSO_LOGIN);
                                com.igexin.push.core.f.a().k().a("bi", contentValues2, new String[]{WorksListUri.KEY_ID}, new String[]{string2});
                                contentValues2 = new ContentValues();
                                contentValues2.put("loginerror_nonetwork_count", Integer.valueOf(i + 1));
                                contentValues2.put(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, format);
                                contentValues2.put(SocialConstants.PARAM_TYPE, Constants.VIA_TO_TYPE_QQ_GROUP);
                                com.igexin.push.core.f.a().k().a("bi", contentValues2);
                            }
                        }
                    }
                } catch (Exception e) {
                    cursor = a;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            if (a != null) {
                a.close();
            }
        } catch (Exception e2) {
            cursor = null;
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        } catch (Throwable th3) {
            th = th3;
            a = null;
            if (a != null) {
                a.close();
            }
            throw th;
        }
        return 0;
    }

    public void d(Intent intent) {
        if (intent != null) {
            try {
                int intExtra = intent.getIntExtra(WorksListUri.KEY_ID, -1);
                boolean booleanExtra = intent.getBooleanExtra("result", false);
                if (intExtra != -1) {
                    com.igexin.push.core.g.ar++;
                    if (booleanExtra) {
                        if (intent.getBooleanExtra("isReload", false)) {
                            Process.killProcess(Process.myPid());
                            return;
                        }
                        com.igexin.push.core.g.aq++;
                        Map b = com.igexin.push.core.g.as != null ? com.igexin.push.core.g.as.b() : null;
                        if (b != null) {
                            Object obj;
                            Map map;
                            if (com.igexin.push.config.l.t != null) {
                                Map b2 = com.igexin.push.config.l.t.b();
                                if (b2 == null) {
                                    return;
                                }
                                if (b2.containsKey(Integer.valueOf(intExtra))) {
                                    obj = 1;
                                    com.igexin.push.core.bean.e eVar = (com.igexin.push.core.bean.e) b2.get(Integer.valueOf(intExtra));
                                    if (eVar != null) {
                                        com.igexin.push.f.b.a(eVar.c());
                                    }
                                    b2.remove(Integer.valueOf(intExtra));
                                    map = b2;
                                } else {
                                    obj = null;
                                    map = b2;
                                }
                            } else {
                                Map hashMap = new HashMap();
                                com.igexin.push.core.bean.f fVar = new com.igexin.push.core.bean.f();
                                fVar.a(Constants.VIA_RESULT_SUCCESS);
                                fVar.a(hashMap);
                                com.igexin.push.config.l.t = fVar;
                                map = hashMap;
                                obj = null;
                            }
                            com.igexin.push.core.bean.e eVar2 = (com.igexin.push.core.bean.e) b.get(Integer.valueOf(intExtra));
                            if (eVar2 != null) {
                                String str = com.igexin.push.core.g.ad + "/" + eVar2.c();
                                if (new File(str).exists()) {
                                    map.put(Integer.valueOf(intExtra), eVar2);
                                    if (com.igexin.push.core.g.aq == com.igexin.push.core.g.ap) {
                                        com.igexin.push.config.l.t.a(com.igexin.push.core.g.as.a());
                                    }
                                    if (obj == null && com.igexin.push.extension.a.a().a(com.igexin.push.core.g.g, str, eVar2.d(), eVar2.j(), eVar2.c())) {
                                        a.b("CoreAction load " + eVar2.d() + " success");
                                        eVar2.b(System.currentTimeMillis());
                                        if (eVar2.g()) {
                                            com.igexin.push.f.b.a(eVar2.c());
                                            map.remove(Integer.valueOf(intExtra));
                                        }
                                    }
                                    com.igexin.push.config.a.a().g();
                                }
                            } else {
                                return;
                            }
                        }
                        return;
                    }
                    if (com.igexin.push.core.g.ar == com.igexin.push.core.g.ap && com.igexin.push.core.g.at) {
                        a.b("CoreActiondown load ext success, restart service ############");
                        Process.killProcess(Process.myPid());
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public void d(String str) {
        com.igexin.a.a.b.d.c().a(new com.igexin.push.e.a.c(new com.igexin.push.core.d.j(SDKUrlConfig.getBiUploadServiceUrl(), ((a(true, 4) + "2.6.1.0|sdkconfig-error|") + str).getBytes(), 0, true)), false, true);
    }

    public String e(String str) {
        return com.igexin.push.core.g.c() == null ? null : (String) com.igexin.push.core.g.c().get(str);
    }

    public void e() {
        a.a("CoreAction|do disconnect|" + SDKUrlConfig.getCmAddress().replaceFirst("socket", "disConnect"));
        com.igexin.a.a.b.d.c().a(SDKUrlConfig.getCmAddress().replaceFirst("socket", "disConnect"), 0, null);
    }

    public int f() {
        a.a("CoreAction send heart beat data ........");
        return com.igexin.push.core.f.a().g().a("H-" + com.igexin.push.core.g.s, new com.igexin.push.c.c.h(), true);
    }

    public void g() {
        for (com.igexin.push.core.bean.i iVar : com.igexin.push.core.c.c.a().b()) {
            if (iVar.d() + 10000 <= System.currentTimeMillis()) {
                long currentTimeMillis = System.currentTimeMillis();
                com.igexin.push.c.c.e dVar = new com.igexin.push.c.c.d();
                dVar.a();
                dVar.a = (int) currentTimeMillis;
                dVar.d = "17258000";
                dVar.e = iVar.b();
                dVar.g = com.igexin.push.core.g.s;
                com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, dVar);
                a.b("freshral|" + iVar.b());
                com.igexin.push.core.c.c.a().a(iVar.a());
                iVar.a(iVar.d() + 10000);
                com.igexin.push.core.c.c.a().a(iVar);
                return;
            }
        }
    }

    public void h() {
        long currentTimeMillis = System.currentTimeMillis();
        String str = "{\"action\":\"request_deviceid\",\"id\":\"" + currentTimeMillis + "\"}";
        com.igexin.push.c.c.e dVar = new com.igexin.push.c.c.d();
        dVar.a();
        dVar.a = (int) currentTimeMillis;
        dVar.d = "17258000";
        dVar.e = str;
        dVar.g = com.igexin.push.core.g.s;
        com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, dVar);
        a.b("deviceidReq");
    }

    public void i() {
        long j;
        String str = null;
        long j2 = -1;
        try {
            com.igexin.push.core.bean.a aVar = new com.igexin.push.core.bean.a();
            j2 = aVar.l;
            str = com.igexin.push.core.bean.a.a(aVar);
            j = j2;
        } catch (JSONException e) {
            j = j2;
        }
        if (str != null) {
            a.b("addphoneinfo");
            com.igexin.push.core.c.c a = com.igexin.push.core.c.c.a();
            if (a != null) {
                a.a(new com.igexin.push.core.bean.i(j, str, (byte) 5, j));
            }
            com.igexin.push.c.c.e dVar = new com.igexin.push.c.c.d();
            dVar.a();
            dVar.a = (int) j;
            dVar.d = "17258000";
            dVar.e = str;
            dVar.g = com.igexin.push.core.g.s;
            com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, dVar);
        }
    }

    public void j() {
        long currentTimeMillis = System.currentTimeMillis();
        String str = "{\"action\":\"request_ca_list\",\"id\":\"" + currentTimeMillis + "\", \"appid\":\"" + com.igexin.push.core.g.a + "\", \"cid\":\"" + com.igexin.push.core.g.s + "\"}";
        com.igexin.push.c.c.e dVar = new com.igexin.push.c.c.d();
        dVar.a();
        dVar.a = (int) currentTimeMillis;
        dVar.d = "17258000";
        dVar.e = str;
        dVar.g = com.igexin.push.core.g.s;
        com.igexin.push.core.f.a().g().a("C-" + com.igexin.push.core.g.s, dVar);
    }

    public long k() {
        return ((long) (new Random().nextInt(6) + 2)) * 60000;
    }

    public void l() {
        Intent intent = new Intent();
        intent.addFlags(32);
        intent.setAction("com.igexin.sdk.action." + com.igexin.push.core.g.a);
        Bundle bundle = new Bundle();
        bundle.putInt(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.GET_CLIENTID);
        bundle.putString("clientid", com.igexin.push.core.g.s);
        intent.putExtras(bundle);
        a.b("broadcastClientid|" + com.igexin.push.core.g.s);
        com.igexin.push.core.f.a().a(intent);
        Log.d("PushService", "clientid is " + com.igexin.push.core.g.s);
    }

    public void m() {
        Intent intent = new Intent();
        intent.addFlags(32);
        intent.setAction("com.igexin.sdk.action." + com.igexin.push.core.g.a);
        Bundle bundle = new Bundle();
        bundle.putInt(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.GET_SDKONLINESTATE);
        bundle.putBoolean("onlineState", com.igexin.push.core.g.m);
        intent.putExtras(bundle);
        com.igexin.push.core.f.a().a(intent);
    }

    public String n() {
        FileInputStream fileInputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        ByteArrayOutputStream byteArrayOutputStream2;
        FileInputStream fileInputStream2;
        Throwable th;
        if (new File(com.igexin.push.core.g.aa).exists()) {
            byte[] bArr = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            try {
                fileInputStream = new FileInputStream(com.igexin.push.core.g.aa);
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    while (true) {
                        try {
                            int read = fileInputStream.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            byteArrayOutputStream.write(bArr, 0, read);
                        } catch (Exception e) {
                            byteArrayOutputStream2 = byteArrayOutputStream;
                            fileInputStream2 = fileInputStream;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                    String str = new String(byteArrayOutputStream.toByteArray());
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e2) {
                        }
                    }
                    if (byteArrayOutputStream == null) {
                        return str;
                    }
                    try {
                        byteArrayOutputStream.close();
                        return str;
                    } catch (Exception e3) {
                        return str;
                    }
                } catch (Exception e4) {
                    byteArrayOutputStream2 = null;
                    fileInputStream2 = fileInputStream;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (Exception e5) {
                        }
                    }
                    if (byteArrayOutputStream2 != null) {
                        try {
                            byteArrayOutputStream2.close();
                            return null;
                        } catch (Exception e6) {
                            return null;
                        }
                    }
                    return null;
                } catch (Throwable th3) {
                    th = th3;
                    byteArrayOutputStream = null;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e7) {
                        }
                    }
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Exception e8) {
                        }
                    }
                    throw th;
                }
            } catch (Exception e9) {
                byteArrayOutputStream2 = null;
                fileInputStream2 = null;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                if (byteArrayOutputStream2 != null) {
                    byteArrayOutputStream2.close();
                    return null;
                }
                return null;
            } catch (Throwable th4) {
                th = th4;
                byteArrayOutputStream = null;
                fileInputStream = null;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                throw th;
            }
        }
        return null;
    }

    public void o() {
        List arrayList = new ArrayList();
        a(arrayList);
        int size = arrayList.size();
        if (size > 0) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(DoubanAccountOperationFragment_.ACTION_ARG, "reportapplist");
                jSONObject.put("session_last", com.igexin.push.core.g.r);
                JSONArray jSONArray = new JSONArray();
                for (int i = 0; i < size; i++) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put(SocialConstants.PARAM_APP_ID, ((com.igexin.push.core.bean.l) arrayList.get(i)).d());
                    jSONObject2.put(SelectCountryActivity.EXTRA_COUNTRY_NAME, ((com.igexin.push.core.bean.l) arrayList.get(i)).b());
                    jSONObject2.put(ShareRequestParam.REQ_PARAM_VERSION, ((com.igexin.push.core.bean.l) arrayList.get(i)).c());
                    jSONObject2.put("versionName", ((com.igexin.push.core.bean.l) arrayList.get(i)).a());
                    jSONArray.put(jSONObject2);
                }
                jSONObject.put("applist", jSONArray);
            } catch (JSONException e) {
            }
            byte[] b = com.igexin.a.b.a.b(jSONObject.toString().getBytes());
            if (b != null) {
                com.igexin.a.a.b.d.c().a(new com.igexin.push.e.a.c(new com.igexin.push.core.d.a(SDKUrlConfig.getBiUploadServiceUrl(), b)), false, true);
                h(p());
                a.b("reportapplist");
            }
        }
    }

    public String p() {
        ArrayList arrayList = new ArrayList();
        List arrayList2 = new ArrayList();
        a(arrayList2);
        int size = arrayList2.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                arrayList.add(((com.igexin.push.core.bean.l) arrayList2.get(i)).d());
            }
        }
        return arrayList.toString();
    }

    public boolean q() {
        String packageName;
        boolean z;
        boolean z2;
        boolean z3;
        if (com.igexin.push.core.g.g == null) {
            packageName = com.igexin.push.core.g.g.getApplicationContext().getPackageName();
        } else {
            packageName = com.igexin.push.core.g.g.getApplicationContext().getPackageName();
        }
        try {
            int i;
            ServiceInfo[] serviceInfoArr = com.igexin.push.core.g.g.getPackageManager().getPackageInfo(packageName, 4).services;
            if (serviceInfoArr != null) {
                int length = serviceInfoArr.length;
                i = 0;
                z = false;
                while (i < length) {
                    try {
                        if (serviceInfoArr[i].name.indexOf("DownloadService") != -1) {
                            z = true;
                        }
                        i++;
                    } catch (NameNotFoundException e) {
                        z2 = false;
                        z3 = z;
                        z = false;
                    }
                }
                z3 = z;
            } else {
                z3 = false;
            }
            try {
                int length2;
                ProviderInfo[] providerInfoArr = com.igexin.push.core.g.g.getPackageManager().getPackageInfo(packageName, 8).providers;
                if (providerInfoArr != null) {
                    length2 = providerInfoArr.length;
                    i = 0;
                    z = false;
                    while (i < length2) {
                        try {
                            if (providerInfoArr[i].name.indexOf("DownloadProvider") != -1) {
                                z = true;
                            }
                            i++;
                        } catch (NameNotFoundException e2) {
                            z2 = z;
                            z = false;
                        }
                    }
                    z2 = z;
                } else {
                    z2 = false;
                }
                try {
                    ActivityInfo[] activityInfoArr = com.igexin.push.core.g.g.getPackageManager().getPackageInfo(packageName, 2).receivers;
                    if (activityInfoArr != null) {
                        length2 = activityInfoArr.length;
                        int i2 = 0;
                        z = false;
                        while (i2 < length2) {
                            try {
                                if (activityInfoArr[i2].name.indexOf("DownloadReceiver") != -1) {
                                    z = true;
                                }
                                i2++;
                            } catch (NameNotFoundException e3) {
                            }
                        }
                    } else {
                        z = false;
                    }
                } catch (NameNotFoundException e4) {
                    z = false;
                }
            } catch (NameNotFoundException e5) {
                z = false;
                z2 = false;
            }
        } catch (NameNotFoundException e6) {
            z = false;
            z2 = false;
            z3 = false;
        }
        return z3 && z2 && z;
    }

    public void r() {
        com.igexin.push.core.f.a().k().a(GiftMessageEditFragment_.MESSAGE_ARG, "createtime <= " + (System.currentTimeMillis() - 604800000));
    }

    public void s() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        String str = "/sdcard/libs/";
        File file = new File(str);
        String str2 = com.igexin.push.core.g.e;
        if (str2 == null) {
            str2 = "unknowPacageName";
        }
        if (file.exists()) {
            String[] list = file.list();
            int length = list.length;
            int i = 0;
            while (i < length) {
                int length2 = list[i].length();
                if (list[i].startsWith(str2) && list[i].endsWith(".log") && length2 > str2.length() + 14 && str2.equals(list[i].substring(0, length2 - 15))) {
                    try {
                        if (Math.abs((simpleDateFormat.parse(format).getTime() - simpleDateFormat.parse(list[i].substring(str2.length() + 1, length2 - 4)).getTime()) / com.umeng.analytics.a.g) > 6) {
                            File file2 = new File(str + list[i]);
                            if (file2.exists()) {
                                file2.delete();
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                i++;
            }
        }
    }

    public void t() {
        if (D() > 0) {
            List arrayList = new ArrayList();
            for (Entry key : com.igexin.push.core.g.ai.entrySet()) {
                String str = (String) key.getKey();
                PushTaskBean pushTaskBean = (PushTaskBean) com.igexin.push.core.g.ai.get(str);
                Object obj = Table.STRING_DEFAULT_VALUE;
                if (pushTaskBean != null && pushTaskBean.getStatus() == com.igexin.push.core.a.k) {
                    String taskId = pushTaskBean.getTaskId();
                    Map conditionMap = pushTaskBean.getConditionMap();
                    if (conditionMap != null) {
                        if (!conditionMap.containsKey("endTime") || Long.valueOf((String) conditionMap.get("endTime")).longValue() >= System.currentTimeMillis()) {
                            int intValue;
                            String str2;
                            if (conditionMap.containsKey("wifi")) {
                                intValue = Integer.valueOf((String) conditionMap.get("wifi")).intValue();
                                w();
                                if (intValue != com.igexin.push.core.g.p) {
                                }
                            }
                            if (conditionMap.containsKey("screenOn")) {
                                intValue = Integer.valueOf((String) conditionMap.get("screenOn")).intValue();
                                v();
                                if (intValue != com.igexin.push.core.g.q) {
                                }
                            }
                            if (conditionMap.containsKey("ssid")) {
                                str2 = (String) conditionMap.get("ssid");
                                x();
                                if (com.igexin.push.core.g.ao.containsValue(str2)) {
                                    obj = str2;
                                }
                            }
                            if (conditionMap.containsKey("bssid")) {
                                str2 = (String) conditionMap.get("bssid");
                                if (com.igexin.push.core.g.ao.containsKey(str2)) {
                                    if (!((String) com.igexin.push.core.g.ao.get(str2)).equals(obj)) {
                                    }
                                }
                            }
                            if (!conditionMap.containsKey("startTime") || Long.valueOf((String) conditionMap.get("startTime")).longValue() <= System.currentTimeMillis()) {
                                a().a(taskId, pushTaskBean.getMessageId(), com.igexin.push.core.g.a, com.igexin.push.core.g.e);
                                a(com.igexin.push.core.a.l, taskId, str);
                                arrayList.add(str);
                            }
                        } else {
                            a(com.igexin.push.core.a.m, taskId, str);
                            arrayList.add(str);
                        }
                    } else {
                        return;
                    }
                }
            }
            b(arrayList);
        }
    }

    public boolean u() {
        long currentTimeMillis = System.currentTimeMillis();
        if (com.igexin.push.core.g.I <= 0) {
            com.igexin.push.core.g.I = currentTimeMillis - 60000;
            return true;
        } else if (currentTimeMillis - com.igexin.push.core.g.I <= 60000) {
            return false;
        } else {
            com.igexin.push.core.g.I = currentTimeMillis;
            return true;
        }
    }

    public void v() {
        if (((PowerManager) com.igexin.push.core.g.g.getSystemService("power")).isScreenOn()) {
            com.igexin.push.core.g.q = 1;
        } else {
            com.igexin.push.core.g.q = 0;
        }
    }

    public void w() {
        State state = ((ConnectivityManager) com.igexin.push.core.g.g.getSystemService("connectivity")).getNetworkInfo(1).getState();
        if (state == State.CONNECTED || state == State.CONNECTING) {
            com.igexin.push.core.g.p = 1;
        } else {
            com.igexin.push.core.g.p = 0;
        }
    }

    public void x() {
        List scanResults = ((WifiManager) com.igexin.push.core.g.g.getSystemService("wifi")).getScanResults();
        com.igexin.push.core.g.ao.clear();
        if (scanResults != null) {
            for (int i = 0; i < scanResults.size(); i++) {
                com.igexin.push.core.g.ao.put(((ScanResult) scanResults.get(i)).BSSID, ((ScanResult) scanResults.get(i)).SSID);
            }
        }
    }

    public void y() {
        if (com.igexin.push.config.l.q) {
            Map d = com.igexin.push.core.c.f.a().d();
            if (d != null && d.size() > 0) {
                for (String str : d.keySet()) {
                    String str2 = (String) d.get(str);
                    try {
                        Intent intent = new Intent();
                        intent.setClassName(str, str2);
                        com.igexin.push.core.g.g.startService(intent);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void z() {
        int i = com.igexin.push.core.g.an - 100;
        if (i < 0) {
            com.igexin.push.core.g.an = 0;
        } else {
            com.igexin.push.core.g.an = i;
        }
        List<String> arrayList = new ArrayList();
        long currentTimeMillis = System.currentTimeMillis();
        for (Entry entry : com.igexin.push.core.g.am.entrySet()) {
            if (currentTimeMillis - ((Long) entry.getValue()).longValue() > com.umeng.analytics.a.h) {
                arrayList.add((String) entry.getKey());
            }
        }
        for (String remove : arrayList) {
            com.igexin.push.core.g.am.remove(remove);
        }
    }
}
