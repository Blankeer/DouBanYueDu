package natalya.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.realm.internal.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Utils {
    public static final String generateUUID(Context context) {
        String deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        if (deviceId == null) {
            deviceId = Table.STRING_DEFAULT_VALUE;
        }
        String androidId = Secure.getString(context.getContentResolver(), "android_id");
        if (androidId == null) {
            androidId = Table.STRING_DEFAULT_VALUE;
        }
        String serialId = Table.STRING_DEFAULT_VALUE;
        if (VERSION.SDK_INT >= 9) {
            serialId = Build.SERIAL;
            if (serialId == null) {
                serialId = Table.STRING_DEFAULT_VALUE;
            }
        } else {
            serialId = getDeviceSerial();
        }
        String macAddress = Table.STRING_DEFAULT_VALUE;
        WifiInfo wifiInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        if (wifiInfo != null) {
            macAddress = wifiInfo.getMacAddress();
            if (macAddress == null) {
                macAddress = Table.STRING_DEFAULT_VALUE;
            }
        }
        try {
            return getMD5String(deviceId + androidId + serialId + macAddress);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String getDeviceSerial() {
        String serial = Table.STRING_DEFAULT_VALUE;
        try {
            Class clazz = Class.forName("android.os.Build");
            Class paraTypes = Class.forName("java.lang.String");
            Method method = clazz.getDeclaredMethod("getString", new Class[]{paraTypes});
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return (String) method.invoke(new Build(), new Object[]{"ro.serialno"});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return serial;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return serial;
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return serial;
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
            return serial;
        }
    }

    private static final String getMD5String(String value) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance(CommonUtils.SHA1_INSTANCE).digest(value.getBytes());
        Formatter formatter = new Formatter();
        int len$ = hash.length;
        for (int i$ = 0; i$ < len$; i$++) {
            formatter.format("%02x", new Object[]{Byte.valueOf(arr$[i$])});
        }
        return formatter.toString();
    }

    public static int getMemorySize(int percent) {
        return (int) (((float) Runtime.getRuntime().maxMemory()) * (((float) percent) / 100.0f));
    }
}
