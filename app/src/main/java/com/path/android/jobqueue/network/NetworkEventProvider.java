package com.path.android.jobqueue.network;

public interface NetworkEventProvider {

    public interface Listener {
        void onNetworkChange(boolean z);
    }

    void setListener(Listener listener);
}
