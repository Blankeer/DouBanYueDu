package com.google.tagmanager;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.douban.book.reader.helper.AppUri;
import com.google.android.gms.common.util.VisibleForTesting;

class ServiceManagerImpl extends ServiceManager {
    private static final int MSG_KEY = 1;
    private static final Object MSG_OBJECT;
    private static ServiceManagerImpl instance;
    private boolean connected;
    private Context ctx;
    private int dispatchPeriodInSeconds;
    private Handler handler;
    private boolean listenForNetwork;
    private HitStoreStateListener listener;
    private NetworkReceiver networkReceiver;
    private boolean pendingDispatch;
    private boolean readyToDispatch;
    private HitStore store;
    private boolean storeIsEmpty;
    private volatile HitSendingThread thread;

    static {
        MSG_OBJECT = new Object();
    }

    public static ServiceManagerImpl getInstance() {
        if (instance == null) {
            instance = new ServiceManagerImpl();
        }
        return instance;
    }

    private ServiceManagerImpl() {
        this.dispatchPeriodInSeconds = 1800;
        this.pendingDispatch = true;
        this.readyToDispatch = false;
        this.connected = true;
        this.listenForNetwork = true;
        this.listener = new HitStoreStateListener() {
            public void reportStoreIsEmpty(boolean isEmpty) {
                ServiceManagerImpl.this.updatePowerSaveMode(isEmpty, ServiceManagerImpl.this.connected);
            }
        };
        this.storeIsEmpty = false;
    }

    @VisibleForTesting
    static void clearInstance() {
        instance = null;
    }

    @VisibleForTesting
    ServiceManagerImpl(Context ctx, HitSendingThread thread, HitStore store, boolean listenForNetwork) {
        this.dispatchPeriodInSeconds = 1800;
        this.pendingDispatch = true;
        this.readyToDispatch = false;
        this.connected = true;
        this.listenForNetwork = true;
        this.listener = new HitStoreStateListener() {
            public void reportStoreIsEmpty(boolean isEmpty) {
                ServiceManagerImpl.this.updatePowerSaveMode(isEmpty, ServiceManagerImpl.this.connected);
            }
        };
        this.storeIsEmpty = false;
        this.store = store;
        this.thread = thread;
        this.listenForNetwork = listenForNetwork;
        initialize(ctx, thread);
    }

    private void initializeNetworkReceiver() {
        this.networkReceiver = new NetworkReceiver(this);
        this.networkReceiver.register(this.ctx);
    }

    private void initializeHandler() {
        this.handler = new Handler(this.ctx.getMainLooper(), new Callback() {
            public boolean handleMessage(Message msg) {
                if (ServiceManagerImpl.MSG_KEY == msg.what && ServiceManagerImpl.MSG_OBJECT.equals(msg.obj)) {
                    ServiceManagerImpl.this.dispatch();
                    if (ServiceManagerImpl.this.dispatchPeriodInSeconds > 0 && !ServiceManagerImpl.this.storeIsEmpty) {
                        ServiceManagerImpl.this.handler.sendMessageDelayed(ServiceManagerImpl.this.handler.obtainMessage(ServiceManagerImpl.MSG_KEY, ServiceManagerImpl.MSG_OBJECT), (long) (ServiceManagerImpl.this.dispatchPeriodInSeconds * AppUri.OPEN_URL));
                    }
                }
                return true;
            }
        });
        if (this.dispatchPeriodInSeconds > 0) {
            this.handler.sendMessageDelayed(this.handler.obtainMessage(MSG_KEY, MSG_OBJECT), (long) (this.dispatchPeriodInSeconds * AppUri.OPEN_URL));
        }
    }

    synchronized void initialize(Context ctx, HitSendingThread thread) {
        if (this.ctx == null) {
            this.ctx = ctx.getApplicationContext();
            if (this.thread == null) {
                this.thread = thread;
            }
        }
    }

    @VisibleForTesting
    HitStoreStateListener getListener() {
        return this.listener;
    }

    synchronized HitStore getStore() {
        if (this.store == null) {
            if (this.ctx == null) {
                throw new IllegalStateException("Cant get a store unless we have a context");
            }
            this.store = new PersistentHitStore(this.listener, this.ctx);
        }
        if (this.handler == null) {
            initializeHandler();
        }
        this.readyToDispatch = true;
        if (this.pendingDispatch) {
            dispatch();
            this.pendingDispatch = false;
        }
        if (this.networkReceiver == null && this.listenForNetwork) {
            initializeNetworkReceiver();
        }
        return this.store;
    }

    public synchronized void dispatch() {
        if (this.readyToDispatch) {
            this.thread.queueToThread(new Runnable() {
                public void run() {
                    ServiceManagerImpl.this.store.dispatch();
                }
            });
        } else {
            Log.v("Dispatch call queued. Dispatch will run once initialization is complete.");
            this.pendingDispatch = true;
        }
    }

    public synchronized void setDispatchPeriod(int dispatchPeriodInSeconds) {
        if (this.handler == null) {
            Log.v("Dispatch period set with null handler. Dispatch will run once initialization is complete.");
            this.dispatchPeriodInSeconds = dispatchPeriodInSeconds;
        } else {
            if (!this.storeIsEmpty && this.connected && this.dispatchPeriodInSeconds > 0) {
                this.handler.removeMessages(MSG_KEY, MSG_OBJECT);
            }
            this.dispatchPeriodInSeconds = dispatchPeriodInSeconds;
            if (dispatchPeriodInSeconds > 0 && !this.storeIsEmpty && this.connected) {
                this.handler.sendMessageDelayed(this.handler.obtainMessage(MSG_KEY, MSG_OBJECT), (long) (dispatchPeriodInSeconds * AppUri.OPEN_URL));
            }
        }
    }

    @VisibleForTesting
    synchronized void updatePowerSaveMode(boolean storeIsEmpty, boolean connected) {
        if (!(this.storeIsEmpty == storeIsEmpty && this.connected == connected)) {
            if (storeIsEmpty || !connected) {
                if (this.dispatchPeriodInSeconds > 0) {
                    this.handler.removeMessages(MSG_KEY, MSG_OBJECT);
                }
            }
            if (!storeIsEmpty && connected && this.dispatchPeriodInSeconds > 0) {
                this.handler.sendMessageDelayed(this.handler.obtainMessage(MSG_KEY, MSG_OBJECT), (long) (this.dispatchPeriodInSeconds * AppUri.OPEN_URL));
            }
            StringBuilder append = new StringBuilder().append("PowerSaveMode ");
            String str = (storeIsEmpty || !connected) ? "initiated." : "terminated.";
            Log.v(append.append(str).toString());
            this.storeIsEmpty = storeIsEmpty;
            this.connected = connected;
        }
    }

    synchronized void updateConnectivityStatus(boolean connected) {
        updatePowerSaveMode(this.storeIsEmpty, connected);
    }

    synchronized void onRadioPowered() {
        if (!this.storeIsEmpty && this.connected && this.dispatchPeriodInSeconds > 0) {
            this.handler.removeMessages(MSG_KEY, MSG_OBJECT);
            this.handler.sendMessage(this.handler.obtainMessage(MSG_KEY, MSG_OBJECT));
        }
    }
}
