package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.DebugEvents;
import com.google.analytics.containertag.proto.Debug.EventInfo;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.protobuf.nano.MessageNano;
import java.io.IOException;

class CtfeDebugInformationHandler implements DebugInformationHandler {
    @VisibleForTesting
    static final String CTFE_URL_PATH_PREFIX = "/d?";
    @VisibleForTesting
    static final int NUM_EVENTS_PER_SEND = 1;
    private int currentDebugEventNumber;
    private NetworkClient mClient;
    private CtfeHost mCtfeHost;
    private DebugEvents mDebugEvents;

    @VisibleForTesting
    CtfeDebugInformationHandler(NetworkClient client, CtfeHost host) {
        this.mCtfeHost = host;
        this.mClient = client;
        this.mDebugEvents = new DebugEvents();
    }

    public CtfeDebugInformationHandler(CtfeHost host) {
        this(new NetworkClientFactory().createNetworkClient(), host);
    }

    public synchronized void receiveEventInfo(EventInfo event) {
        this.mDebugEvents.event = ArrayUtils.appendToArray(this.mDebugEvents.event, event);
        if (this.mDebugEvents.event.length >= NUM_EVENTS_PER_SEND && sendDebugInformationtoCtfe()) {
            this.mDebugEvents.clear();
        }
    }

    private byte[] getDebugEventsAsBytes() throws IOException {
        return MessageNano.toByteArray(this.mDebugEvents);
    }

    private boolean sendDebugInformationtoCtfe() {
        try {
            NetworkClient networkClient = this.mClient;
            CtfeHost ctfeHost = this.mCtfeHost;
            int i = this.currentDebugEventNumber;
            this.currentDebugEventNumber = i + NUM_EVENTS_PER_SEND;
            networkClient.sendPostRequest(ctfeHost.constructCtfeDebugUrl(i), getDebugEventsAsBytes());
            return true;
        } catch (IOException ex) {
            Log.e("CtfeDebugInformationHandler: Error sending information to server that handles debug information: " + ex.getMessage());
            return false;
        }
    }
}
