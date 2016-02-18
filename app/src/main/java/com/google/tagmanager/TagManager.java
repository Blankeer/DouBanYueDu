package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.tracking.android.HitTypes;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.Container.Callback;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TagManager {
    private static TagManager sInstance;
    private final ContainerProvider mContainerProvider;
    private final ConcurrentMap<String, Container> mContainers;
    private final Context mContext;
    private volatile String mCtfeServerAddr;
    private final DataLayer mDataLayer;
    private volatile RefreshMode mRefreshMode;

    /* renamed from: com.google.tagmanager.TagManager.3 */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$google$tagmanager$PreviewManager$PreviewMode;

        static {
            $SwitchMap$com$google$tagmanager$PreviewManager$PreviewMode = new int[PreviewMode.values().length];
            try {
                $SwitchMap$com$google$tagmanager$PreviewManager$PreviewMode[PreviewMode.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$tagmanager$PreviewManager$PreviewMode[PreviewMode.CONTAINER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$tagmanager$PreviewManager$PreviewMode[PreviewMode.CONTAINER_DEBUG.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static class ContainerOpenException extends RuntimeException {
        private final String mContainerId;

        private ContainerOpenException(String containerId) {
            super("Container already open: " + containerId);
            this.mContainerId = containerId;
        }

        public String getContainerId() {
            return this.mContainerId;
        }
    }

    @VisibleForTesting
    interface ContainerProvider {
        Container newContainer(Context context, String str, TagManager tagManager);
    }

    public enum RefreshMode {
        STANDARD,
        DEFAULT_CONTAINER
    }

    @Deprecated
    public interface Logger extends Logger {
    }

    @VisibleForTesting
    TagManager(Context context, ContainerProvider containerProvider, DataLayer dataLayer) {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        this.mContext = context.getApplicationContext();
        this.mContainerProvider = containerProvider;
        this.mRefreshMode = RefreshMode.STANDARD;
        this.mContainers = new ConcurrentHashMap();
        this.mDataLayer = dataLayer;
        this.mDataLayer.registerListener(new Listener() {
            public void changed(Map<Object, Object> update) {
                Object eventValue = update.get(HitTypes.EVENT);
                if (eventValue != null) {
                    TagManager.this.refreshTagsInAllContainers(eventValue.toString());
                }
            }
        });
        this.mDataLayer.registerListener(new AdwordsClickReferrerListener(this.mContext));
    }

    public static TagManager getInstance(Context context) {
        TagManager tagManager;
        synchronized (TagManager.class) {
            if (sInstance == null) {
                if (context == null) {
                    Log.e("TagManager.getInstance requires non-null context.");
                    throw new NullPointerException();
                }
                sInstance = new TagManager(context, new ContainerProvider() {
                    public Container newContainer(Context context, String containerId, TagManager tagManager) {
                        return new Container(context, containerId, tagManager);
                    }
                }, new DataLayer(new DataLayerPersistentStoreImpl(context)));
            }
            tagManager = sInstance;
        }
        return tagManager;
    }

    @VisibleForTesting
    static void clearInstance() {
        synchronized (TagManager.class) {
            sInstance = null;
        }
    }

    public DataLayer getDataLayer() {
        return this.mDataLayer;
    }

    public Container openContainer(String containerId, Callback callback) {
        Container container = this.mContainerProvider.newContainer(this.mContext, containerId, this);
        if (this.mContainers.putIfAbsent(containerId, container) != null) {
            throw new IllegalArgumentException("Container id:" + containerId + " has already been opened.");
        }
        if (this.mCtfeServerAddr != null) {
            container.setCtfeServerAddress(this.mCtfeServerAddr);
        }
        container.load(callback);
        return container;
    }

    public Context getContext() {
        return this.mContext;
    }

    public void setLogger(Logger logger) {
        Log.setLogger(logger);
    }

    public Logger getLogger() {
        return Log.getLogger();
    }

    public void setRefreshMode(RefreshMode mode) {
        this.mRefreshMode = mode;
    }

    public RefreshMode getRefreshMode() {
        return this.mRefreshMode;
    }

    public Container getContainer(String containerId) {
        return (Container) this.mContainers.get(containerId);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    synchronized boolean setPreviewData(android.net.Uri r9) {
        /*
        r8 = this;
        monitor-enter(r8);
        r5 = com.google.tagmanager.PreviewManager.getInstance();	 Catch:{ all -> 0x0033 }
        r6 = r5.setPreviewData(r9);	 Catch:{ all -> 0x0033 }
        if (r6 == 0) goto L_0x0077;
    L_0x000b:
        r4 = r5.getContainerId();	 Catch:{ all -> 0x0033 }
        r6 = com.google.tagmanager.TagManager.AnonymousClass3.$SwitchMap$com$google$tagmanager$PreviewManager$PreviewMode;	 Catch:{ all -> 0x0033 }
        r7 = r5.getPreviewMode();	 Catch:{ all -> 0x0033 }
        r7 = r7.ordinal();	 Catch:{ all -> 0x0033 }
        r6 = r6[r7];	 Catch:{ all -> 0x0033 }
        switch(r6) {
            case 1: goto L_0x0021;
            case 2: goto L_0x0036;
            case 3: goto L_0x0036;
            default: goto L_0x001e;
        };
    L_0x001e:
        r6 = 1;
    L_0x001f:
        monitor-exit(r8);
        return r6;
    L_0x0021:
        r6 = r8.mContainers;	 Catch:{ all -> 0x0033 }
        r2 = r6.get(r4);	 Catch:{ all -> 0x0033 }
        r2 = (com.google.tagmanager.Container) r2;	 Catch:{ all -> 0x0033 }
        if (r2 == 0) goto L_0x001e;
    L_0x002b:
        r6 = 0;
        r2.setCtfeUrlPathAndQuery(r6);	 Catch:{ all -> 0x0033 }
        r2.refresh();	 Catch:{ all -> 0x0033 }
        goto L_0x001e;
    L_0x0033:
        r6 = move-exception;
        monitor-exit(r8);
        throw r6;
    L_0x0036:
        r6 = r8.mContainers;	 Catch:{ all -> 0x0033 }
        r6 = r6.entrySet();	 Catch:{ all -> 0x0033 }
        r3 = r6.iterator();	 Catch:{ all -> 0x0033 }
    L_0x0040:
        r6 = r3.hasNext();	 Catch:{ all -> 0x0033 }
        if (r6 == 0) goto L_0x001e;
    L_0x0046:
        r1 = r3.next();	 Catch:{ all -> 0x0033 }
        r1 = (java.util.Map.Entry) r1;	 Catch:{ all -> 0x0033 }
        r0 = r1.getValue();	 Catch:{ all -> 0x0033 }
        r0 = (com.google.tagmanager.Container) r0;	 Catch:{ all -> 0x0033 }
        r6 = r1.getKey();	 Catch:{ all -> 0x0033 }
        r6 = (java.lang.String) r6;	 Catch:{ all -> 0x0033 }
        r6 = r6.equals(r4);	 Catch:{ all -> 0x0033 }
        if (r6 == 0) goto L_0x0069;
    L_0x005e:
        r6 = r5.getCTFEUrlPath();	 Catch:{ all -> 0x0033 }
        r0.setCtfeUrlPathAndQuery(r6);	 Catch:{ all -> 0x0033 }
        r0.refresh();	 Catch:{ all -> 0x0033 }
        goto L_0x0040;
    L_0x0069:
        r6 = r0.getCtfeUrlPathAndQuery();	 Catch:{ all -> 0x0033 }
        if (r6 == 0) goto L_0x0040;
    L_0x006f:
        r6 = 0;
        r0.setCtfeUrlPathAndQuery(r6);	 Catch:{ all -> 0x0033 }
        r0.refresh();	 Catch:{ all -> 0x0033 }
        goto L_0x0040;
    L_0x0077:
        r6 = 0;
        goto L_0x001f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.tagmanager.TagManager.setPreviewData(android.net.Uri):boolean");
    }

    @VisibleForTesting
    void setCtfeServerAddress(String addr) {
        this.mCtfeServerAddr = addr;
    }

    boolean removeContainer(String containerId) {
        return this.mContainers.remove(containerId) != null;
    }

    private void refreshTagsInAllContainers(String eventName) {
        for (Container container : this.mContainers.values()) {
            container.evaluateTags(eventName);
        }
    }
}
