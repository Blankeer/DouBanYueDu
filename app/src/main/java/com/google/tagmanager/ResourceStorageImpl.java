package com.google.tagmanager;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.ResourceUtil.ExpandedResource;
import com.google.tagmanager.proto.Resource.ResourceWithMetadata;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;

class ResourceStorageImpl implements ResourceStorage {
    private static final String SAVED_RESOURCE_FILENAME_PREFIX = "resource_";
    private static final String SAVED_RESOURCE_SUB_DIR = "google_tagmanager";
    private LoadCallback<ResourceWithMetadata> mCallback;
    private final String mContainerId;
    private final Context mContext;
    private final ExecutorService mExecutor;

    /* renamed from: com.google.tagmanager.ResourceStorageImpl.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ ResourceWithMetadata val$resource;

        AnonymousClass2(ResourceWithMetadata resourceWithMetadata) {
            this.val$resource = resourceWithMetadata;
        }

        public void run() {
            ResourceStorageImpl.this.saveResourceToDisk(this.val$resource);
        }
    }

    ResourceStorageImpl(Context context, String containerId) {
        this.mContext = context;
        this.mContainerId = containerId;
        this.mExecutor = Executors.newSingleThreadExecutor();
    }

    public void setLoadCallback(LoadCallback<ResourceWithMetadata> callback) {
        this.mCallback = callback;
    }

    public void loadResourceFromDiskInBackground() {
        this.mExecutor.execute(new Runnable() {
            public void run() {
                ResourceStorageImpl.this.loadResourceFromDisk();
            }
        });
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @com.google.android.gms.common.util.VisibleForTesting
    void loadResourceFromDisk() {
        /*
        r5 = this;
        r3 = r5.mCallback;
        if (r3 != 0) goto L_0x000c;
    L_0x0004:
        r3 = new java.lang.IllegalStateException;
        r4 = "callback must be set before execute";
        r3.<init>(r4);
        throw r3;
    L_0x000c:
        r3 = r5.mCallback;
        r3.startLoad();
        r3 = "Start loading resource from disk ...";
        com.google.tagmanager.Log.v(r3);
        r3 = com.google.tagmanager.PreviewManager.getInstance();
        r3 = r3.getPreviewMode();
        r4 = com.google.tagmanager.PreviewManager.PreviewMode.CONTAINER;
        if (r3 == r4) goto L_0x002e;
    L_0x0022:
        r3 = com.google.tagmanager.PreviewManager.getInstance();
        r3 = r3.getPreviewMode();
        r4 = com.google.tagmanager.PreviewManager.PreviewMode.CONTAINER_DEBUG;
        if (r3 != r4) goto L_0x0046;
    L_0x002e:
        r3 = r5.mContainerId;
        r4 = com.google.tagmanager.PreviewManager.getInstance();
        r4 = r4.getContainerId();
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0046;
    L_0x003e:
        r3 = r5.mCallback;
        r4 = com.google.tagmanager.LoadCallback.Failure.NOT_AVAILABLE;
        r3.onFailure(r4);
    L_0x0045:
        return;
    L_0x0046:
        r2 = 0;
        r2 = new java.io.FileInputStream;	 Catch:{ FileNotFoundException -> 0x006e }
        r3 = r5.getResourceFile();	 Catch:{ FileNotFoundException -> 0x006e }
        r2.<init>(r3);	 Catch:{ FileNotFoundException -> 0x006e }
        r1 = new java.io.ByteArrayOutputStream;	 Catch:{ IOException -> 0x0083 }
        r1.<init>();	 Catch:{ IOException -> 0x0083 }
        com.google.tagmanager.ResourceUtil.copyStream(r2, r1);	 Catch:{ IOException -> 0x0083 }
        r3 = r5.mCallback;	 Catch:{ IOException -> 0x0083 }
        r4 = r1.toByteArray();	 Catch:{ IOException -> 0x0083 }
        r4 = com.google.tagmanager.proto.Resource.ResourceWithMetadata.parseFrom(r4);	 Catch:{ IOException -> 0x0083 }
        r3.onSuccess(r4);	 Catch:{ IOException -> 0x0083 }
        r2.close();	 Catch:{ IOException -> 0x007c }
    L_0x0068:
        r3 = "Load resource from disk finished.";
        com.google.tagmanager.Log.v(r3);
        goto L_0x0045;
    L_0x006e:
        r0 = move-exception;
        r3 = "resource not on disk";
        com.google.tagmanager.Log.d(r3);
        r3 = r5.mCallback;
        r4 = com.google.tagmanager.LoadCallback.Failure.NOT_AVAILABLE;
        r3.onFailure(r4);
        goto L_0x0045;
    L_0x007c:
        r0 = move-exception;
        r3 = "error closing stream for reading resource from disk";
        com.google.tagmanager.Log.w(r3);
        goto L_0x0068;
    L_0x0083:
        r0 = move-exception;
        r3 = "error reading resource from disk";
        com.google.tagmanager.Log.w(r3);	 Catch:{ all -> 0x009b }
        r3 = r5.mCallback;	 Catch:{ all -> 0x009b }
        r4 = com.google.tagmanager.LoadCallback.Failure.IO_ERROR;	 Catch:{ all -> 0x009b }
        r3.onFailure(r4);	 Catch:{ all -> 0x009b }
        r2.close();	 Catch:{ IOException -> 0x0094 }
        goto L_0x0068;
    L_0x0094:
        r0 = move-exception;
        r3 = "error closing stream for reading resource from disk";
        com.google.tagmanager.Log.w(r3);
        goto L_0x0068;
    L_0x009b:
        r3 = move-exception;
        r2.close();	 Catch:{ IOException -> 0x00a0 }
    L_0x009f:
        throw r3;
    L_0x00a0:
        r0 = move-exception;
        r4 = "error closing stream for reading resource from disk";
        com.google.tagmanager.Log.w(r4);
        goto L_0x009f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.tagmanager.ResourceStorageImpl.loadResourceFromDisk():void");
    }

    public void saveResourceToDiskInBackground(ResourceWithMetadata resource) {
        this.mExecutor.execute(new AnonymousClass2(resource));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.analytics.containertag.proto.Serving.Resource loadResourceFromContainerAsset(java.lang.String r10) {
        /*
        r9 = this;
        r6 = 0;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Loading default container from ";
        r7 = r7.append(r8);
        r7 = r7.append(r10);
        r7 = r7.toString();
        com.google.tagmanager.Log.v(r7);
        r7 = r9.mContext;
        r0 = r7.getAssets();
        if (r0 != 0) goto L_0x0026;
    L_0x001f:
        r7 = "No assets found in package";
        com.google.tagmanager.Log.e(r7);
        r5 = r6;
    L_0x0025:
        return r5;
    L_0x0026:
        r2 = 0;
        r2 = r0.open(r10);	 Catch:{ IOException -> 0x0057 }
        r3 = new java.io.ByteArrayOutputStream;	 Catch:{ IOException -> 0x0076 }
        r3.<init>();	 Catch:{ IOException -> 0x0076 }
        com.google.tagmanager.ResourceUtil.copyStream(r2, r3);	 Catch:{ IOException -> 0x0076 }
        r4 = r3.toByteArray();	 Catch:{ IOException -> 0x0076 }
        r5 = com.google.analytics.containertag.proto.Serving.Resource.parseFrom(r4);	 Catch:{ IOException -> 0x0076 }
        r7 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0076 }
        r7.<init>();	 Catch:{ IOException -> 0x0076 }
        r8 = "Parsed default container: ";
        r7 = r7.append(r8);	 Catch:{ IOException -> 0x0076 }
        r7 = r7.append(r5);	 Catch:{ IOException -> 0x0076 }
        r7 = r7.toString();	 Catch:{ IOException -> 0x0076 }
        com.google.tagmanager.Log.v(r7);	 Catch:{ IOException -> 0x0076 }
        r2.close();	 Catch:{ IOException -> 0x0055 }
        goto L_0x0025;
    L_0x0055:
        r6 = move-exception;
        goto L_0x0025;
    L_0x0057:
        r1 = move-exception;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "No asset file: ";
        r7 = r7.append(r8);
        r7 = r7.append(r10);
        r8 = " found.";
        r7 = r7.append(r8);
        r7 = r7.toString();
        com.google.tagmanager.Log.w(r7);
        r5 = r6;
        goto L_0x0025;
    L_0x0076:
        r1 = move-exception;
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0092 }
        r7.<init>();	 Catch:{ all -> 0x0092 }
        r8 = "Error when parsing: ";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0092 }
        r7 = r7.append(r10);	 Catch:{ all -> 0x0092 }
        r7 = r7.toString();	 Catch:{ all -> 0x0092 }
        com.google.tagmanager.Log.w(r7);	 Catch:{ all -> 0x0092 }
        r2.close();	 Catch:{ IOException -> 0x0097 }
    L_0x0090:
        r5 = r6;
        goto L_0x0025;
    L_0x0092:
        r6 = move-exception;
        r2.close();	 Catch:{ IOException -> 0x0099 }
    L_0x0096:
        throw r6;
    L_0x0097:
        r7 = move-exception;
        goto L_0x0090;
    L_0x0099:
        r7 = move-exception;
        goto L_0x0096;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.tagmanager.ResourceStorageImpl.loadResourceFromContainerAsset(java.lang.String):com.google.analytics.containertag.proto.Serving$Resource");
    }

    public ExpandedResource loadExpandedResourceFromJsonAsset(String assetFile) {
        ExpandedResource expandedResource = null;
        Log.v("loading default container from " + assetFile);
        AssetManager assets = this.mContext.getAssets();
        if (assets == null) {
            Log.w("Looking for default JSON container in package, but no assets were found.");
        } else {
            InputStream is = null;
            try {
                is = assets.open(assetFile);
                expandedResource = JsonUtils.expandedResourceFromJsonString(stringFromInputStream(is));
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException e2) {
                Log.w("No asset file: " + assetFile + " found (or errors reading it).");
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e3) {
                    }
                }
            } catch (JSONException e4) {
                Log.w("Error parsing JSON file" + assetFile + " : " + e4);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e5) {
                    }
                }
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e6) {
                    }
                }
            }
        }
        return expandedResource;
    }

    public synchronized void close() {
        this.mExecutor.shutdown();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @com.google.android.gms.common.util.VisibleForTesting
    boolean saveResourceToDisk(com.google.tagmanager.proto.Resource.ResourceWithMetadata r7) {
        /*
        r6 = this;
        r4 = 0;
        r2 = 0;
        r1 = r6.getResourceFile();
        r3 = new java.io.FileOutputStream;	 Catch:{ FileNotFoundException -> 0x0018 }
        r3.<init>(r1);	 Catch:{ FileNotFoundException -> 0x0018 }
        r5 = com.google.tagmanager.protobuf.nano.MessageNano.toByteArray(r7);	 Catch:{ IOException -> 0x0026 }
        r3.write(r5);	 Catch:{ IOException -> 0x0026 }
        r4 = 1;
        r3.close();	 Catch:{ IOException -> 0x001f }
    L_0x0016:
        r2 = r3;
    L_0x0017:
        return r4;
    L_0x0018:
        r0 = move-exception;
        r5 = "Error opening resource file for writing";
        com.google.tagmanager.Log.e(r5);
        goto L_0x0017;
    L_0x001f:
        r0 = move-exception;
        r5 = "error closing stream for writing resource to disk";
        com.google.tagmanager.Log.w(r5);
        goto L_0x0016;
    L_0x0026:
        r0 = move-exception;
        r5 = "Error writing resource to disk. Removing resource from disk.";
        com.google.tagmanager.Log.w(r5);	 Catch:{ all -> 0x003b }
        r1.delete();	 Catch:{ all -> 0x003b }
        r3.close();	 Catch:{ IOException -> 0x0034 }
    L_0x0032:
        r2 = r3;
        goto L_0x0017;
    L_0x0034:
        r0 = move-exception;
        r5 = "error closing stream for writing resource to disk";
        com.google.tagmanager.Log.w(r5);
        goto L_0x0032;
    L_0x003b:
        r4 = move-exception;
        r3.close();	 Catch:{ IOException -> 0x0040 }
    L_0x003f:
        throw r4;
    L_0x0040:
        r0 = move-exception;
        r5 = "error closing stream for writing resource to disk";
        com.google.tagmanager.Log.w(r5);
        goto L_0x003f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.tagmanager.ResourceStorageImpl.saveResourceToDisk(com.google.tagmanager.proto.Resource$ResourceWithMetadata):boolean");
    }

    @VisibleForTesting
    File getResourceFile() {
        return new File(this.mContext.getDir(SAVED_RESOURCE_SUB_DIR, 0), SAVED_RESOURCE_FILENAME_PREFIX + this.mContainerId);
    }

    private String stringFromInputStream(InputStream is) throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
        Reader reader = new BufferedReader(new InputStreamReader(is, HttpRequest.CHARSET_UTF8));
        while (true) {
            int n = reader.read(buffer);
            if (n == -1) {
                return writer.toString();
            }
            writer.write(buffer, 0, n);
        }
    }
}
