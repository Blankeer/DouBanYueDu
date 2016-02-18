package com.alipay.apmobilesecuritysdk.b;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.alipay.apmobilesecuritysdk.e.e;
import com.alipay.security.mobile.module.a.c.a;
import com.alipay.security.mobile.module.commonutils.c;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.helper.WorksListUri;
import com.tencent.open.SocialConstants;
import io.realm.internal.Table;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public final class b {
    private File a;
    private a b;

    public b(String str, a aVar) {
        this.a = null;
        this.b = null;
        this.a = new File(str);
        this.b = aVar;
    }

    private static String a(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(SocialConstants.PARAM_TYPE, WorksListUri.KEY_ID);
            jSONObject.put(StatConstant.STAT_EVENT_ID_ERROR, str);
            return jSONObject.toString();
        } catch (JSONException e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    private void a(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        Object obj = (activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 1) ? 1 : null;
        boolean a = e.a(context);
        if (obj != null && a) {
            new Thread(new c(this)).start();
        }
    }

    final synchronized void a() {
        int i = 0;
        synchronized (this) {
            if (this.a != null) {
                if (this.a.exists() && this.a.isDirectory() && this.a.list().length != 0) {
                    int i2;
                    String str;
                    List arrayList = new ArrayList();
                    for (Object add : this.a.list()) {
                        arrayList.add(add);
                    }
                    Collections.sort(arrayList);
                    String str2 = (String) arrayList.get(arrayList.size() - 1);
                    int size = arrayList.size();
                    int i3;
                    if (!str2.equals(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()) + ".log")) {
                        i3 = size;
                        str = str2;
                        i2 = i3;
                    } else if (arrayList.size() >= 2) {
                        i3 = size - 1;
                        str = (String) arrayList.get(arrayList.size() - 2);
                        i2 = i3;
                    }
                    size = !this.b.a(a(c.a(this.a.getAbsolutePath(), str))) ? i2 - 1 : i2;
                    while (i < size) {
                        new File(this.a, (String) arrayList.get(i)).delete();
                        i++;
                    }
                }
            }
        }
    }
}
