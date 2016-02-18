package com.google.analytics.tracking.android;

import android.content.Context;
import android.support.v4.media.TransportMediator;
import com.google.android.gms.common.util.VisibleForTesting;
import com.tencent.connect.common.Constants;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

class ClientIdDefaultProvider implements DefaultProvider {
    private static ClientIdDefaultProvider sInstance;
    private static final Object sInstanceLock;
    private String mClientId;
    private boolean mClientIdLoaded;
    private final Object mClientIdLock;
    private final Context mContext;

    /* renamed from: com.google.analytics.tracking.android.ClientIdDefaultProvider.1 */
    class AnonymousClass1 extends Thread {
        AnonymousClass1(String x0) {
            super(x0);
        }

        public void run() {
            synchronized (ClientIdDefaultProvider.this.mClientIdLock) {
                ClientIdDefaultProvider.this.mClientId = ClientIdDefaultProvider.this.initializeClientId();
                ClientIdDefaultProvider.this.mClientIdLoaded = true;
                ClientIdDefaultProvider.this.mClientIdLock.notifyAll();
            }
        }
    }

    static {
        sInstanceLock = new Object();
    }

    public static void initializeProvider(Context c) {
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new ClientIdDefaultProvider(c);
            }
        }
    }

    @VisibleForTesting
    static void dropInstance() {
        synchronized (sInstanceLock) {
            sInstance = null;
        }
    }

    public static ClientIdDefaultProvider getProvider() {
        ClientIdDefaultProvider clientIdDefaultProvider;
        synchronized (sInstanceLock) {
            clientIdDefaultProvider = sInstance;
        }
        return clientIdDefaultProvider;
    }

    protected ClientIdDefaultProvider(Context c) {
        this.mClientIdLoaded = false;
        this.mClientIdLock = new Object();
        this.mContext = c;
        asyncInitializeClientId();
    }

    public boolean providesField(String field) {
        return Fields.CLIENT_ID.equals(field);
    }

    public String getValue(String field) {
        if (Fields.CLIENT_ID.equals(field)) {
            return blockingGetClientId();
        }
        return null;
    }

    private String blockingGetClientId() {
        if (!this.mClientIdLoaded) {
            synchronized (this.mClientIdLock) {
                if (!this.mClientIdLoaded) {
                    Log.v("Waiting for clientId to load");
                    do {
                        try {
                            this.mClientIdLock.wait();
                        } catch (InterruptedException e) {
                            Log.e("Exception while waiting for clientId: " + e);
                        }
                    } while (!this.mClientIdLoaded);
                }
            }
        }
        Log.v("Loaded clientId");
        return this.mClientId;
    }

    private boolean storeClientId(String clientId) {
        try {
            Log.v("Storing clientId.");
            FileOutputStream fos = this.mContext.openFileOutput("gaClientId", 0);
            fos.write(clientId.getBytes());
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("Error creating clientId file.");
            return false;
        } catch (IOException e2) {
            Log.e("Error writing to clientId file.");
            return false;
        }
    }

    protected String generateClientId() {
        String result = UUID.randomUUID().toString().toLowerCase();
        if (storeClientId(result)) {
            return result;
        }
        return Constants.VIA_RESULT_SUCCESS;
    }

    private void asyncInitializeClientId() {
        new AnonymousClass1("client_id_fetcher").start();
    }

    @VisibleForTesting
    String initializeClientId() {
        String rslt = null;
        try {
            FileInputStream input = this.mContext.openFileInput("gaClientId");
            byte[] bytes = new byte[TransportMediator.FLAG_KEY_MEDIA_NEXT];
            int readLen = input.read(bytes, 0, TransportMediator.FLAG_KEY_MEDIA_NEXT);
            if (input.available() > 0) {
                Log.e("clientId file seems corrupted, deleting it.");
                input.close();
                this.mContext.deleteFile("gaClientId");
            } else if (readLen <= 0) {
                Log.e("clientId file seems empty, deleting it.");
                input.close();
                this.mContext.deleteFile("gaClientId");
            } else {
                String rslt2 = new String(bytes, 0, readLen);
                try {
                    input.close();
                    rslt = rslt2;
                } catch (FileNotFoundException e) {
                    rslt = rslt2;
                } catch (IOException e2) {
                    rslt = rslt2;
                    Log.e("Error reading clientId file, deleting it.");
                    this.mContext.deleteFile("gaClientId");
                }
            }
        } catch (FileNotFoundException e3) {
        } catch (IOException e4) {
            Log.e("Error reading clientId file, deleting it.");
            this.mContext.deleteFile("gaClientId");
        }
        if (rslt == null) {
            return generateClientId();
        }
        return rslt;
    }
}
