package com.alipay.security.mobile.module.deviceinfo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Base64;
import com.alipay.security.mobile.module.deviceinfo.listener.a;
import com.douban.book.reader.util.WorksIdentity;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class e {
    volatile int a;
    private Context b;
    private String c;
    private String d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private String l;

    private e() {
        this.a = 0;
    }

    private static e a(Context context) {
        TelephonyManager telephonyManager;
        String str;
        String str2;
        String str3;
        String str4;
        WifiManager wifiManager;
        e eVar = new e();
        eVar.b = context;
        try {
            telephonyManager = (TelephonyManager) context.getSystemService("phone");
            int i = telephonyManager.getPhoneType() == 2 ? 2 : 1;
            str = Table.STRING_DEFAULT_VALUE;
            str2 = Table.STRING_DEFAULT_VALUE;
            str3 = Table.STRING_DEFAULT_VALUE;
            str4 = Table.STRING_DEFAULT_VALUE;
            if (i == 2) {
                String networkOperator;
                String valueOf;
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) telephonyManager.getCellLocation();
                if (cdmaCellLocation != null) {
                    str4 = String.valueOf(cdmaCellLocation.getNetworkId());
                    networkOperator = telephonyManager.getNetworkOperator();
                    if (!(networkOperator == null || networkOperator.equals(Table.STRING_DEFAULT_VALUE))) {
                        str = networkOperator.substring(0, 3);
                    }
                    str2 = String.valueOf(cdmaCellLocation.getSystemId());
                    valueOf = String.valueOf(cdmaCellLocation.getBaseStationId());
                    str3 = str;
                    networkOperator = str4;
                    str4 = str2;
                } else {
                    networkOperator = str4;
                    valueOf = str3;
                    str4 = str2;
                    str3 = str;
                }
                str2 = str4;
                str = str3;
                str4 = networkOperator;
                str3 = valueOf;
            } else {
                try {
                    GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                    if (gsmCellLocation != null) {
                        String networkOperator2 = telephonyManager.getNetworkOperator();
                        if (!(networkOperator2 == null || networkOperator2.equals(Table.STRING_DEFAULT_VALUE))) {
                            str = telephonyManager.getNetworkOperator().substring(0, 3);
                            str2 = telephonyManager.getNetworkOperator().substring(3, 5);
                        }
                        str3 = String.valueOf(gsmCellLocation.getCid());
                        str4 = String.valueOf(gsmCellLocation.getLac());
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.fillInStackTrace();
        } catch (Throwable th) {
        }
        eVar.i = str;
        eVar.j = str2;
        eVar.k = str3;
        eVar.l = str4;
        try {
            telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager != null) {
                telephonyManager.listen(new f(eVar, telephonyManager), WorksIdentity.ID_BIT_FINALIZE);
            }
        } catch (Throwable th2) {
        }
        try {
            Object obj;
            CdmaCellLocation cdmaCellLocation2;
            LocationManager locationManager = (LocationManager) context.getSystemService("location");
            if (locationManager.isProviderEnabled("network")) {
                LocationListener aVar = new a();
                locationManager.requestLocationUpdates("network", com.alipay.security.mobile.module.deviceinfo.constant.a.b, 0.0f, aVar, Looper.getMainLooper());
                locationManager.removeUpdates(aVar);
                Location lastKnownLocation = locationManager.getLastKnownLocation("network");
                if (lastKnownLocation != null) {
                    eVar.d = lastKnownLocation.getLatitude();
                    eVar.c = lastKnownLocation.getLongitude();
                    obj = 1;
                    telephonyManager = (TelephonyManager) context.getSystemService("phone");
                    if (obj == null && telephonyManager.getPhoneType() == 2) {
                        cdmaCellLocation2 = (CdmaCellLocation) telephonyManager.getCellLocation();
                        if (cdmaCellLocation2 != null && com.alipay.security.mobile.module.commonutils.a.a(eVar.d) && com.alipay.security.mobile.module.commonutils.a.a(eVar.c)) {
                            eVar.d = (((double) cdmaCellLocation2.getBaseStationLatitude()) / 14400.0d);
                            eVar.c = (((double) cdmaCellLocation2.getBaseStationLongitude()) / 14400.0d);
                        }
                    }
                    eVar.g = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected();
                    wifiManager = (WifiManager) context.getSystemService("wifi");
                    if (wifiManager.isWifiEnabled()) {
                        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                        eVar.e = connectionInfo.getBSSID();
                        eVar.f = Base64.encodeToString(connectionInfo.getSSID().getBytes(), 8);
                        eVar.h = connectionInfo.getRssi();
                    }
                    return eVar;
                }
            }
            obj = null;
            telephonyManager = (TelephonyManager) context.getSystemService("phone");
            cdmaCellLocation2 = (CdmaCellLocation) telephonyManager.getCellLocation();
            eVar.d = (((double) cdmaCellLocation2.getBaseStationLatitude()) / 14400.0d);
            eVar.c = (((double) cdmaCellLocation2.getBaseStationLongitude()) / 14400.0d);
        } catch (Exception e22) {
            e22.fillInStackTrace();
        }
        try {
            eVar.g = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected();
            wifiManager = (WifiManager) context.getSystemService("wifi");
            if (wifiManager.isWifiEnabled()) {
                WifiInfo connectionInfo2 = wifiManager.getConnectionInfo();
                eVar.e = connectionInfo2.getBSSID();
                eVar.f = Base64.encodeToString(connectionInfo2.getSSID().getBytes(), 8);
                eVar.h = connectionInfo2.getRssi();
            }
        } catch (Exception e222) {
            e222.fillInStackTrace();
        }
        return eVar;
    }

    private void a(int i) {
        this.a = i;
    }

    private void a(String str) {
        this.c = str;
    }

    private boolean a() {
        return this.a != 0;
    }

    private double b() {
        return (double) this.a;
    }

    private void b(String str) {
        this.d = str;
    }

    private List<Map<String, String>> c() {
        List<Map<String, String>> arrayList = new ArrayList();
        if (this.b == null) {
            return arrayList;
        }
        WifiManager wifiManager = (WifiManager) this.b.getSystemService("wifi");
        if (wifiManager == null) {
            return arrayList;
        }
        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (scanResults == null) {
            return arrayList;
        }
        for (ScanResult scanResult : scanResults) {
            Map hashMap = new HashMap();
            hashMap.put("wifiMac", scanResult.BSSID == null ? Table.STRING_DEFAULT_VALUE : scanResult.BSSID);
            hashMap.put("ssid", scanResult.SSID);
            hashMap.put("rssi", scanResult.level);
            arrayList.add(hashMap);
        }
        return arrayList;
    }

    private void c(String str) {
        this.e = str;
    }

    private void d(String str) {
        this.f = str;
    }

    private boolean d() {
        if (this.b == null) {
            return false;
        }
        LocationManager locationManager = (LocationManager) this.b.getSystemService("location");
        return locationManager == null ? false : locationManager.isProviderEnabled("gps");
    }

    private void e(String str) {
        this.g = str;
    }

    private boolean e() {
        if (this.b == null) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) this.b.getSystemService("wifi");
        if (wifiManager == null) {
            return false;
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return false;
        }
        WifiConfiguration wifiConfiguration;
        Context context = this.b;
        String ssid = connectionInfo.getSSID();
        if (context == null || ssid == null) {
            wifiConfiguration = null;
        } else {
            wifiManager = (WifiManager) context.getSystemService("wifi");
            if (wifiManager != null) {
                List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
                if (configuredNetworks != null) {
                    for (WifiConfiguration wifiConfiguration2 : configuredNetworks) {
                        if (wifiConfiguration2.SSID.equals(ssid)) {
                            break;
                        }
                    }
                }
            }
            wifiConfiguration2 = null;
        }
        if (wifiConfiguration2 == null) {
            return false;
        }
        int i = wifiConfiguration2.allowedKeyManagement.get(1) ? 2 : (wifiConfiguration2.allowedKeyManagement.get(2) || wifiConfiguration2.allowedKeyManagement.get(3)) ? 3 : wifiConfiguration2.wepKeys[0] != null ? 1 : 0;
        return i != 0;
    }

    private String f() {
        return this.c;
    }

    private void f(String str) {
        this.h = str;
    }

    private String g() {
        return this.d;
    }

    private void g(String str) {
        this.i = str;
    }

    private String h() {
        return this.e;
    }

    private void h(String str) {
        this.j = str;
    }

    private String i() {
        return this.f;
    }

    private void i(String str) {
        this.k = str;
    }

    private String j() {
        return this.g;
    }

    private void j(String str) {
        this.l = str;
    }

    private String k() {
        return this.h;
    }

    private String l() {
        return this.i;
    }

    private String m() {
        return this.j;
    }

    private String n() {
        return this.k;
    }

    private String o() {
        return this.l;
    }
}
