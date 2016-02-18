package com.sina.weibo.sdk.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.igexin.sdk.PushConsts;
import org.apache.http.HttpHost;

public class NetStateManager {
    public static NetState CUR_NETSTATE;
    private static Context mContext;

    public enum NetState {
        Mobile,
        WIFI,
        NOWAY
    }

    public class NetStateReceive extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            NetStateManager.mContext = context;
            if (PushConsts.ACTION_BROADCAST_NETWORK_CHANGE.equals(intent.getAction())) {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                WifiInfo info = wifiManager.getConnectionInfo();
                if (!wifiManager.isWifiEnabled() || -1 == info.getNetworkId()) {
                    NetStateManager.CUR_NETSTATE = NetState.Mobile;
                }
            }
        }
    }

    static {
        CUR_NETSTATE = NetState.Mobile;
    }

    public static HttpHost getAPN() {
        HttpHost proxy = null;
        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        Cursor mCursor = null;
        if (mContext != null) {
            mCursor = mContext.getContentResolver().query(uri, null, null, null, null);
        }
        if (mCursor != null && mCursor.moveToFirst()) {
            String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
            if (proxyStr != null && proxyStr.trim().length() > 0) {
                proxy = new HttpHost(proxyStr, 80);
            }
            mCursor.close();
        }
        return proxy;
    }
}
