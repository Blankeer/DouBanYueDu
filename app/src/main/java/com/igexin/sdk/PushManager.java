package com.igexin.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.media.TransportMediator;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment_;
import com.douban.book.reader.util.WorksIdentity;
import com.igexin.a.a.c.a;
import com.igexin.sdk.a.c;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PushManager {
    private static PushManager a;
    private long b;
    private long c;
    private long d;
    private byte[] e;
    private String f;

    public PushManager() {
        this.b = 0;
        this.c = 0;
        this.d = 0;
        this.e = null;
    }

    private Intent a() {
        Intent intent = new Intent(PushConsts.ACTION_BROADCAST_PUSHMANAGER);
        intent.putExtra("verifyCode", this.f);
        return intent;
    }

    private String a(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuffer stringBuffer = new StringBuffer(Table.STRING_DEFAULT_VALUE);
            for (int i : digest) {
                int i2;
                if (i2 < 0) {
                    i2 += WorksIdentity.ID_BIT_FINALIZE;
                }
                if (i2 < 16) {
                    stringBuffer.append(Constants.VIA_RESULT_SUCCESS);
                }
                stringBuffer.append(Integer.toHexString(i2));
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private byte[] a(Context context) {
        ByteArrayOutputStream byteArrayOutputStream;
        Throwable th;
        byte[] bArr = null;
        String str = "/data/data/" + context.getPackageName() + "/files/" + "init.pid";
        if (new File(str).exists()) {
            byte[] bArr2 = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(str);
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    while (true) {
                        try {
                            int read = fileInputStream.read(bArr2);
                            if (read == -1) {
                                break;
                            }
                            byteArrayOutputStream.write(bArr2, 0, read);
                        } catch (Exception e) {
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                    bArr = byteArrayOutputStream.toByteArray();
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e2) {
                        }
                    }
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Exception e3) {
                        }
                    }
                } catch (Exception e4) {
                    byteArrayOutputStream = bArr;
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e5) {
                        }
                    }
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (Exception e6) {
                        }
                    }
                    return bArr;
                } catch (Throwable th3) {
                    Throwable th4 = th3;
                    byteArrayOutputStream = bArr;
                    th = th4;
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
                byteArrayOutputStream = bArr;
                fileInputStream = bArr;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                return bArr;
            } catch (Throwable th32) {
                fileInputStream = bArr;
                byte[] bArr3 = bArr;
                th = th32;
                byteArrayOutputStream = bArr3;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                throw th;
            }
        }
        return bArr;
    }

    public static PushManager getInstance() {
        if (a == null) {
            a = new PushManager();
        }
        return a;
    }

    public boolean bindAlias(Context context, String str) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.d <= 5000) {
            return false;
        }
        this.d = currentTimeMillis;
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "bindAlias");
        a.putExtra("alias", str);
        a.b("-> call bindAlias...");
        context.sendBroadcast(a);
        return true;
    }

    public String getClientid(Context context) {
        if (this.e != null) {
            byte[] a = a(context);
            if (!(this.e == null || a == null || this.e.length != a.length)) {
                byte[] bArr = new byte[a.length];
                for (int i = 0; i < bArr.length; i++) {
                    bArr[i] = (byte) (this.e[i] ^ a[i]);
                }
                return new String(bArr);
            }
        }
        return null;
    }

    public String getVerifyCode() {
        return this.f;
    }

    public String getVersion(Context context) {
        return PushBuildConfig.sdk_conf_version;
    }

    public void initialize(Context context) {
        try {
            String packageName = context.getApplicationContext().getPackageName();
            Intent intent = new Intent(context.getApplicationContext(), PushService.class);
            intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.ACTION_SERVICE_INITIALIZE);
            intent.putExtra("op_app", packageName);
            context.getApplicationContext().startService(intent);
            this.f = com.igexin.a.b.a.a(packageName + System.currentTimeMillis());
            if (this.f == null) {
                this.f = String.valueOf(System.currentTimeMillis());
            }
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                String string = applicationInfo.metaData.getString("PUSH_APPID");
                String string2 = applicationInfo.metaData.getString("PUSH_APPSECRET");
                packageName = applicationInfo.metaData.get("PUSH_APPKEY") != null ? applicationInfo.metaData.get("PUSH_APPKEY").toString() : null;
                if (string != null) {
                    string = string.trim();
                }
                if (string2 != null) {
                    string2 = string2.trim();
                }
                if (packageName != null) {
                    packageName = packageName.trim();
                }
                if (!string.equals(Table.STRING_DEFAULT_VALUE) && !string2.equals(Table.STRING_DEFAULT_VALUE) && !packageName.equals(Table.STRING_DEFAULT_VALUE)) {
                    this.e = a(string + string2 + packageName + context.getPackageName()).getBytes();
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean isPushTurnedOn(Context context) {
        return new c(context).c();
    }

    public boolean sendFeedbackMessage(Context context, String str, String str2, int i) {
        if (str == null || str2 == null || i < PushConsts.MIN_FEEDBACK_ACTION || i > PushConsts.MAX_FEEDBACK_ACTION) {
            return false;
        }
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "sendFeedbackMessage");
        a.putExtra("taskid", str);
        a.putExtra("messageid", str2);
        a.putExtra("actionid", String.valueOf(i));
        context.sendBroadcast(a);
        return true;
    }

    public boolean sendMessage(Context context, String str, byte[] bArr) {
        long currentTimeMillis = System.currentTimeMillis();
        if (str == null || bArr == null || ((long) bArr.length) > PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM || currentTimeMillis - this.c < 1000) {
            return false;
        }
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "sendMessage");
        a.putExtra("taskid", str);
        a.putExtra("extraData", bArr);
        context.sendBroadcast(a);
        return true;
    }

    public boolean setHeartbeatInterval(Context context, int i) {
        if (i < 0) {
            return false;
        }
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "setHeartbeatInterval");
        a.putExtra("interval", i);
        context.sendBroadcast(a);
        return true;
    }

    public boolean setSilentTime(Context context, int i, int i2) {
        if (i < 0 || i >= 24 || i2 < 0 || i2 > 23) {
            return false;
        }
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "setSilentTime");
        a.putExtra("beginHour", i);
        a.putExtra("duration", i2);
        context.sendBroadcast(a);
        return true;
    }

    public boolean setSocketTimeout(Context context, int i) {
        if (i < 0) {
            return false;
        }
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "setSocketTimeout");
        a.putExtra("timeout", i);
        context.sendBroadcast(a);
        return true;
    }

    public int setTag(Context context, Tag[] tagArr) {
        if (tagArr == null) {
            return PushConsts.SETTAG_ERROR_NULL;
        }
        if (((long) tagArr.length) > 200) {
            return PushConsts.SETTAG_ERROR_COUNT;
        }
        if (this.b > System.currentTimeMillis() - 1000) {
            return PushConsts.SETTAG_ERROR_FREQUENCY;
        }
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "setTag");
        StringBuffer stringBuffer = new StringBuffer();
        for (Tag name : tagArr) {
            stringBuffer.append(name.getName());
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        a.putExtra("tags", stringBuffer.toString());
        context.sendBroadcast(a);
        this.b = System.currentTimeMillis();
        return 0;
    }

    public void stopService(Context context) {
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "stopService");
        context.sendBroadcast(a);
    }

    public void turnOffPush(Context context) {
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "turnOffPush");
        context.sendBroadcast(a);
    }

    public void turnOnPush(Context context) {
        String packageName = context.getApplicationContext().getPackageName();
        try {
            Intent intent = new Intent(context.getApplicationContext(), PushService.class);
            intent.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, PushConsts.ACTION_SERVICE_INITIALIZE_SLAVE);
            intent.putExtra("op_app", packageName);
            intent.putExtra("isSlave", true);
            context.getApplicationContext().startService(intent);
        } catch (Exception e) {
        }
    }

    public boolean unBindAlias(Context context, String str, boolean z) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.d <= 5000) {
            return false;
        }
        this.d = currentTimeMillis;
        Intent a = a();
        a.putExtra(DoubanAccountOperationFragment_.ACTION_ARG, "unbindAlias");
        a.putExtra("alias", str);
        a.putExtra("isSeft", z);
        a.b("-> call unbindAlias...");
        context.sendBroadcast(a);
        return true;
    }
}
