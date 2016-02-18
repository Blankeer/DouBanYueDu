package com.path.android.jobqueue.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.igexin.sdk.PushConsts;
import com.path.android.jobqueue.network.NetworkEventProvider.Listener;

public class NetworkUtilImpl implements NetworkUtil, NetworkEventProvider {
    private Listener listener;

    public NetworkUtilImpl(Context context) {
        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (NetworkUtilImpl.this.listener != null) {
                    NetworkUtilImpl.this.listener.onNetworkChange(NetworkUtilImpl.this.isConnected(context));
                }
            }
        }, new IntentFilter(PushConsts.ACTION_BROADCAST_NETWORK_CHANGE));
    }

    public boolean isConnected(Context context) {
        NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
