package com.google.tagmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.analytics.containertag.proto.Serving.SupplementedResource;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.LoadCallback.Failure;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class ResourceLoader implements Runnable {
    private static final String CTFE_URL_PREFIX = "/r?id=";
    private static final String CTFE_URL_SUFFIX = "&v=a62676326";
    private static final String PREVIOUS_CONTAINER_VERSION_QUERY_NAME = "pv";
    @VisibleForTesting
    static final String SDK_VERSION = "a62676326";
    private LoadCallback<SupplementedResource> mCallback;
    private final NetworkClientFactory mClientFactory;
    private final String mContainerId;
    private final Context mContext;
    private volatile CtfeHost mCtfeHost;
    private volatile String mCtfeUrlPathAndQuery;
    private final String mDefaultCtfeUrlPathAndQuery;
    private volatile String mPreviousVersion;

    public ResourceLoader(Context context, String containerId, CtfeHost ctfeHost) {
        this(context, containerId, new NetworkClientFactory(), ctfeHost);
    }

    @VisibleForTesting
    ResourceLoader(Context context, String containerId, NetworkClientFactory factory, CtfeHost ctfeHost) {
        this.mContext = context;
        this.mClientFactory = factory;
        this.mContainerId = containerId;
        this.mCtfeHost = ctfeHost;
        this.mDefaultCtfeUrlPathAndQuery = CTFE_URL_PREFIX + containerId;
        this.mCtfeUrlPathAndQuery = this.mDefaultCtfeUrlPathAndQuery;
        this.mPreviousVersion = null;
    }

    public void run() {
        if (this.mCallback == null) {
            throw new IllegalStateException("callback must be set before execute");
        }
        this.mCallback.startLoad();
        loadResource();
    }

    private boolean okToLoad() {
        NetworkInfo network = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (network != null && network.isConnected()) {
            return true;
        }
        Log.v("...no network connectivity");
        return false;
    }

    void setLoadCallback(LoadCallback<SupplementedResource> callback) {
        this.mCallback = callback;
    }

    private void loadResource() {
        if (okToLoad()) {
            Log.v("Start loading resource from network ...");
            String url = getCtfeUrl();
            NetworkClient networkClient = this.mClientFactory.createNetworkClient();
            try {
                InputStream inputStream = networkClient.getInputStream(url);
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ResourceUtil.copyStream(inputStream, outputStream);
                    SupplementedResource resource = SupplementedResource.parseFrom(outputStream.toByteArray());
                    Log.v("Successfully loaded supplemented resource: " + resource);
                    if (resource.resource == null) {
                        Log.v("No change for container: " + this.mContainerId);
                    }
                    this.mCallback.onSuccess(resource);
                    Log.v("Load resource from network finished.");
                } catch (IOException e) {
                    Log.w("Error when parsing downloaded resources from url: " + url + " " + e.getMessage(), e);
                    this.mCallback.onFailure(Failure.SERVER_ERROR);
                    networkClient.close();
                }
            } catch (FileNotFoundException e2) {
                Log.w("No data is retrieved from the given url: " + url + ". Make sure container_id: " + this.mContainerId + " is correct.");
                this.mCallback.onFailure(Failure.SERVER_ERROR);
            } catch (IOException e3) {
                Log.w("Error when loading resources from url: " + url + " " + e3.getMessage(), e3);
                this.mCallback.onFailure(Failure.IO_ERROR);
            } finally {
                networkClient.close();
            }
        } else {
            this.mCallback.onFailure(Failure.NOT_AVAILABLE);
        }
    }

    @VisibleForTesting
    String getCtfeUrl() {
        String url = this.mCtfeHost.getCtfeServerAddress() + this.mCtfeUrlPathAndQuery + CTFE_URL_SUFFIX;
        if (!(this.mPreviousVersion == null || this.mPreviousVersion.trim().equals(Table.STRING_DEFAULT_VALUE))) {
            url = url + "&pv=" + this.mPreviousVersion;
        }
        if (PreviewManager.getInstance().getPreviewMode().equals(PreviewMode.CONTAINER_DEBUG)) {
            return url + "&gtm_debug=x";
        }
        return url;
    }

    @VisibleForTesting
    void setCtfeURLPathAndQuery(String urlPathAndQuery) {
        if (urlPathAndQuery == null) {
            this.mCtfeUrlPathAndQuery = this.mDefaultCtfeUrlPathAndQuery;
            return;
        }
        Log.d("Setting CTFE URL path: " + urlPathAndQuery);
        this.mCtfeUrlPathAndQuery = urlPathAndQuery;
    }

    @VisibleForTesting
    void setPreviousVersion(String version) {
        Log.d("Setting previous container version: " + version);
        this.mPreviousVersion = version;
    }
}
