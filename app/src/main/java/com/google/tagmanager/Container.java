package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.containertag.proto.Serving.Resource;
import com.google.analytics.containertag.proto.Serving.Supplemental;
import com.google.analytics.containertag.proto.Serving.SupplementedResource;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.tagmanager.CustomFunctionCall.CustomEvaluator;
import com.google.tagmanager.LoadCallback.Failure;
import com.google.tagmanager.ResourceUtil.ExpandedResource;
import com.google.tagmanager.ResourceUtil.InvalidResourceException;
import com.google.tagmanager.TagManager.RefreshMode;
import com.google.tagmanager.proto.Resource.ResourceWithMetadata;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import u.aly.dx;

public class Container {
    static final boolean ENABLE_CONTAINER_DEBUG_MODE = false;
    @VisibleForTesting
    static final int MAX_NUMBER_OF_TOKENS = 30;
    static final int MINIMUM_REFRESH_PERIOD_BURST_MODE_MS = 5000;
    static final long MINIMUM_REFRESH_PERIOD_MS = 900000;
    static final long REFRESH_PERIOD_ON_FAILURE_MS = 3600000;
    static final long REFRESH_PERIOD_ON_SUCCESS_MS = 43200000;
    private Clock mClock;
    private final String mContainerId;
    private final Context mContext;
    private CtfeHost mCtfeHost;
    private volatile String mCtfeServerAddress;
    private volatile String mCtfeUrlPathAndQuery;
    @VisibleForTesting
    LoadCallback<ResourceWithMetadata> mDiskLoadCallback;
    private Map<String, FunctionCallMacroHandler> mFunctionCallMacroHandlers;
    private Map<String, FunctionCallTagHandler> mFunctionCallTagHandlers;
    private SupplementedResource mLastLoadedSupplementedResource;
    private volatile long mLastRefreshMethodCalledTime;
    private volatile long mLastRefreshTime;
    @VisibleForTesting
    LoadCallback<SupplementedResource> mNetworkLoadCallback;
    private ResourceLoaderScheduler mNetworkLoadScheduler;
    private volatile int mNumTokens;
    private volatile int mResourceFormatVersion;
    private ResourceStorage mResourceStorage;
    private volatile String mResourceVersion;
    private Runtime mRuntime;
    private final TagManager mTagManager;
    private Callback mUserCallback;

    /* renamed from: com.google.tagmanager.Container.4 */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$google$tagmanager$LoadCallback$Failure;

        static {
            $SwitchMap$com$google$tagmanager$LoadCallback$Failure = new int[Failure.values().length];
            try {
                $SwitchMap$com$google$tagmanager$LoadCallback$Failure[Failure.NOT_AVAILABLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$tagmanager$LoadCallback$Failure[Failure.IO_ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$tagmanager$LoadCallback$Failure[Failure.SERVER_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public interface Callback {
        void containerRefreshBegin(Container container, RefreshType refreshType);

        void containerRefreshFailure(Container container, RefreshType refreshType, RefreshFailure refreshFailure);

        void containerRefreshSuccess(Container container, RefreshType refreshType);
    }

    public interface FunctionCallMacroHandler {
        Object getValue(String str, Map<String, Object> map);
    }

    public interface FunctionCallTagHandler {
        void execute(String str, Map<String, Object> map);
    }

    public enum RefreshFailure {
        NO_SAVED_CONTAINER,
        IO_ERROR,
        NO_NETWORK,
        NETWORK_ERROR,
        SERVER_ERROR,
        UNKNOWN_ERROR
    }

    public enum RefreshType {
        SAVED,
        NETWORK
    }

    interface ResourceLoaderScheduler {
        void close();

        void loadAfterDelay(long j, String str);

        void setCtfeURLPathAndQuery(String str);

        void setLoadCallback(LoadCallback<SupplementedResource> loadCallback);
    }

    interface ResourceStorage {
        void close();

        ExpandedResource loadExpandedResourceFromJsonAsset(String str);

        Resource loadResourceFromContainerAsset(String str);

        void loadResourceFromDiskInBackground();

        void saveResourceToDiskInBackground(ResourceWithMetadata resourceWithMetadata);

        void setLoadCallback(LoadCallback<ResourceWithMetadata> loadCallback);
    }

    /* renamed from: com.google.tagmanager.Container.2 */
    class AnonymousClass2 implements LoadCallback<ResourceWithMetadata> {
        final /* synthetic */ Clock val$clock;

        AnonymousClass2(Clock clock) {
            this.val$clock = clock;
        }

        public void startLoad() {
            Container.this.callRefreshBegin(RefreshType.SAVED);
        }

        public void onSuccess(ResourceWithMetadata proto) {
            if (Container.this.isDefault()) {
                Resource resource;
                if (proto.supplementedResource != null) {
                    resource = proto.supplementedResource.resource;
                    Container.this.mLastLoadedSupplementedResource = proto.supplementedResource;
                } else {
                    resource = proto.resource;
                    Container.this.mLastLoadedSupplementedResource.resource = resource;
                    Container.this.mLastLoadedSupplementedResource.supplemental = null;
                    Container.this.mLastLoadedSupplementedResource.fingerprint = resource.version;
                }
                Container.this.initEvaluators(resource);
                if (Container.this.mLastLoadedSupplementedResource.supplemental != null) {
                    Container.this.setSupplementals(Container.this.mLastLoadedSupplementedResource.supplemental);
                }
                Log.v("setting refresh time to saved time: " + proto.timeStamp);
                Container.this.mLastRefreshTime = proto.timeStamp;
                Container.this.loadAfterDelay(Math.max(0, Math.min(Container.REFRESH_PERIOD_ON_SUCCESS_MS, (Container.this.mLastRefreshTime + Container.REFRESH_PERIOD_ON_SUCCESS_MS) - this.val$clock.currentTimeMillis())));
            }
            Container.this.callRefreshSuccess(RefreshType.SAVED);
        }

        public void onFailure(Failure failure) {
            Container.this.callRefreshFailure(RefreshType.SAVED, failureToRefreshFailure(failure));
            if (Container.this.isDefault()) {
                Container.this.loadAfterDelay(0);
            }
        }

        private RefreshFailure failureToRefreshFailure(Failure failure) {
            switch (AnonymousClass4.$SwitchMap$com$google$tagmanager$LoadCallback$Failure[failure.ordinal()]) {
                case dx.b /*1*/:
                    return RefreshFailure.NO_SAVED_CONTAINER;
                case dx.c /*2*/:
                    return RefreshFailure.IO_ERROR;
                case dx.d /*3*/:
                    return RefreshFailure.SERVER_ERROR;
                default:
                    return RefreshFailure.UNKNOWN_ERROR;
            }
        }
    }

    /* renamed from: com.google.tagmanager.Container.3 */
    class AnonymousClass3 implements LoadCallback<SupplementedResource> {
        final /* synthetic */ Clock val$clock;

        AnonymousClass3(Clock clock) {
            this.val$clock = clock;
        }

        public void startLoad() {
            Container.this.callRefreshBegin(RefreshType.NETWORK);
        }

        public void onSuccess(SupplementedResource supplementedResource) {
            synchronized (Container.this) {
                Resource resource = supplementedResource.resource;
                if (resource != null) {
                    Container.this.initEvaluators(resource);
                    Container.this.mLastLoadedSupplementedResource.resource = resource;
                } else if (Container.this.mLastLoadedSupplementedResource.resource == null) {
                    onFailure(Failure.SERVER_ERROR);
                    return;
                }
                if (supplementedResource.supplemental.length > 0) {
                    Container.this.setSupplementals(supplementedResource.supplemental);
                    Container.this.mLastLoadedSupplementedResource.supplemental = supplementedResource.supplemental;
                }
                Container.this.mLastRefreshTime = this.val$clock.currentTimeMillis();
                Container.this.mLastLoadedSupplementedResource.fingerprint = supplementedResource.fingerprint;
                if (Container.this.mLastLoadedSupplementedResource.fingerprint.length() == 0) {
                    Container.this.mLastLoadedSupplementedResource.fingerprint = Container.this.mLastLoadedSupplementedResource.resource.version;
                }
                Log.v("setting refresh time to current time: " + Container.this.mLastRefreshTime);
                if (!Container.this.isContainerPreview()) {
                    Container.this.saveResourceToDisk(Container.this.mLastLoadedSupplementedResource);
                }
                Container.this.loadAfterDelay(Container.REFRESH_PERIOD_ON_SUCCESS_MS);
                Container.this.callRefreshSuccess(RefreshType.NETWORK);
            }
        }

        public void onFailure(Failure failure) {
            Container.this.loadAfterDelay(Container.REFRESH_PERIOD_ON_FAILURE_MS);
            Container.this.callRefreshFailure(RefreshType.NETWORK, failureToRefreshFailure(failure));
        }

        private RefreshFailure failureToRefreshFailure(Failure failure) {
            switch (AnonymousClass4.$SwitchMap$com$google$tagmanager$LoadCallback$Failure[failure.ordinal()]) {
                case dx.b /*1*/:
                    return RefreshFailure.NO_NETWORK;
                case dx.c /*2*/:
                    return RefreshFailure.NETWORK_ERROR;
                case dx.d /*3*/:
                    return RefreshFailure.SERVER_ERROR;
                default:
                    return RefreshFailure.UNKNOWN_ERROR;
            }
        }
    }

    private class FunctionCallMacroHandlerAdapter implements CustomEvaluator {
        private FunctionCallMacroHandlerAdapter() {
        }

        public Object evaluate(String key, Map<String, Object> parameters) {
            FunctionCallMacroHandler handler = Container.this.getFunctionCallMacroHandler(key);
            return handler == null ? null : handler.getValue(key, parameters);
        }
    }

    private class FunctionCallTagHandlerAdapter implements CustomEvaluator {
        private FunctionCallTagHandlerAdapter() {
        }

        public Object evaluate(String key, Map<String, Object> parameters) {
            Container.this.getFunctionCallTagHandler(key).execute(key, parameters);
            return Types.getDefaultString();
        }
    }

    Container(Context context, String containerId, TagManager tagManager) {
        this(context, containerId, tagManager, new ResourceStorageImpl(context, containerId));
    }

    @VisibleForTesting
    Container(Context context, String containerId, TagManager tagManager, ResourceStorage resourceStorage) {
        this.mResourceVersion = Table.STRING_DEFAULT_VALUE;
        this.mResourceFormatVersion = 0;
        this.mCtfeHost = new CtfeHost();
        this.mContext = context;
        this.mContainerId = containerId;
        this.mTagManager = tagManager;
        this.mLastLoadedSupplementedResource = new SupplementedResource();
        this.mResourceStorage = resourceStorage;
        this.mNumTokens = MAX_NUMBER_OF_TOKENS;
        this.mFunctionCallMacroHandlers = new HashMap();
        this.mFunctionCallTagHandlers = new HashMap();
        createInitialContainer();
    }

    public String getContainerId() {
        return this.mContainerId;
    }

    public boolean getBoolean(String key) {
        Runtime runtime = getRuntime();
        if (runtime == null) {
            Log.e("getBoolean called for closed container.");
            return Types.getDefaultBoolean().booleanValue();
        }
        try {
            return Types.valueToBoolean((Value) runtime.evaluateMacroReference(key).getObject()).booleanValue();
        } catch (Exception e) {
            Log.e("Calling getBoolean() threw an exception: " + e.getMessage() + " Returning default value.");
            return Types.getDefaultBoolean().booleanValue();
        }
    }

    public double getDouble(String key) {
        Runtime runtime = getRuntime();
        if (runtime == null) {
            Log.e("getDouble called for closed container.");
            return Types.getDefaultDouble().doubleValue();
        }
        try {
            return Types.valueToDouble((Value) runtime.evaluateMacroReference(key).getObject()).doubleValue();
        } catch (Exception e) {
            Log.e("Calling getDouble() threw an exception: " + e.getMessage() + " Returning default value.");
            return Types.getDefaultDouble().doubleValue();
        }
    }

    public long getLong(String key) {
        Runtime runtime = getRuntime();
        if (runtime == null) {
            Log.e("getLong called for closed container.");
            return Types.getDefaultInt64().longValue();
        }
        try {
            return Types.valueToInt64((Value) runtime.evaluateMacroReference(key).getObject()).longValue();
        } catch (Exception e) {
            Log.e("Calling getLong() threw an exception: " + e.getMessage() + " Returning default value.");
            return Types.getDefaultInt64().longValue();
        }
    }

    public String getString(String key) {
        Runtime runtime = getRuntime();
        if (runtime == null) {
            Log.e("getString called for closed container.");
            return Types.getDefaultString();
        }
        try {
            return Types.valueToString((Value) runtime.evaluateMacroReference(key).getObject());
        } catch (Exception e) {
            Log.e("Calling getString() threw an exception: " + e.getMessage() + " Returning default value.");
            return Types.getDefaultString();
        }
    }

    public long getLastRefreshTime() {
        return this.mLastRefreshTime;
    }

    public synchronized void refresh() {
        if (getRuntime() == null) {
            Log.w("refresh called for closed container");
        } else {
            try {
                if (isDefaultContainerRefreshMode()) {
                    Log.w("Container is in DEFAULT_CONTAINER mode. Refresh request is ignored.");
                } else {
                    long currentTime = this.mClock.currentTimeMillis();
                    if (useAvailableToken(currentTime)) {
                        Log.v("Container refresh requested");
                        loadAfterDelay(0);
                        this.mLastRefreshMethodCalledTime = currentTime;
                    } else {
                        Log.v("Container refresh was called too often. Ignored.");
                    }
                }
            } catch (Exception e) {
                Log.e("Calling refresh() throws an exception: " + e.getMessage());
            }
        }
    }

    public synchronized void close() {
        try {
            if (this.mNetworkLoadScheduler != null) {
                this.mNetworkLoadScheduler.close();
            }
            this.mNetworkLoadScheduler = null;
            if (this.mResourceStorage != null) {
                this.mResourceStorage.close();
            }
            this.mResourceStorage = null;
            this.mUserCallback = null;
            this.mTagManager.removeContainer(this.mContainerId);
        } catch (Exception e) {
            Log.e("Calling close() threw an exception: " + e.getMessage());
        }
        this.mRuntime = null;
    }

    public boolean isDefault() {
        return getLastRefreshTime() == 0 ? true : ENABLE_CONTAINER_DEBUG_MODE;
    }

    void load(Callback callback) {
        load(callback, new ResourceLoaderSchedulerImpl(this.mContext, this.mContainerId, this.mCtfeHost), new Clock() {
            public long currentTimeMillis() {
                return System.currentTimeMillis();
            }
        });
    }

    public synchronized void registerFunctionCallMacroHandler(String customMacroName, FunctionCallMacroHandler customMacroHandler) {
        this.mFunctionCallMacroHandlers.put(customMacroName, customMacroHandler);
    }

    public synchronized FunctionCallMacroHandler getFunctionCallMacroHandler(String customMacroName) {
        return (FunctionCallMacroHandler) this.mFunctionCallMacroHandlers.get(customMacroName);
    }

    public synchronized void registerFunctionCallTagHandler(String customTagName, FunctionCallTagHandler customTagHandler) {
        this.mFunctionCallTagHandlers.put(customTagName, customTagHandler);
    }

    public synchronized FunctionCallTagHandler getFunctionCallTagHandler(String customTagName) {
        return (FunctionCallTagHandler) this.mFunctionCallTagHandlers.get(customTagName);
    }

    private synchronized void callRefreshSuccess(RefreshType refreshType) {
        Log.v("calling containerRefreshSuccess(" + refreshType + "): mUserCallback = " + this.mUserCallback);
        if (this.mUserCallback != null) {
            this.mUserCallback.containerRefreshSuccess(this, refreshType);
        }
    }

    private synchronized void callRefreshFailure(RefreshType refreshType, RefreshFailure refreshFailure) {
        if (this.mUserCallback != null) {
            this.mUserCallback.containerRefreshFailure(this, refreshType, refreshFailure);
        }
    }

    private synchronized void callRefreshBegin(RefreshType refreshType) {
        if (this.mUserCallback != null) {
            this.mUserCallback.containerRefreshBegin(this, refreshType);
        }
    }

    @VisibleForTesting
    void evaluateTags(String currentEventName) {
        getRuntime().evaluateTags(currentEventName);
    }

    @VisibleForTesting
    synchronized void load(Callback callback, ResourceLoaderScheduler scheduler, Clock clock) {
        if (this.mDiskLoadCallback != null) {
            throw new RuntimeException("Container already loaded: container ID: " + this.mContainerId);
        }
        this.mClock = clock;
        this.mUserCallback = callback;
        this.mDiskLoadCallback = new AnonymousClass2(clock);
        if (isDefaultContainerRefreshMode()) {
            Log.i("Container is in DEFAULT_CONTAINER mode. Use default container.");
        } else {
            this.mResourceStorage.setLoadCallback(this.mDiskLoadCallback);
            this.mNetworkLoadCallback = new AnonymousClass3(clock);
            scheduler.setLoadCallback(this.mNetworkLoadCallback);
            if (isContainerPreview()) {
                this.mCtfeUrlPathAndQuery = PreviewManager.getInstance().getCTFEUrlPath();
                scheduler.setCtfeURLPathAndQuery(this.mCtfeUrlPathAndQuery);
            }
            if (this.mCtfeServerAddress != null) {
                this.mCtfeHost.setCtfeServerAddress(this.mCtfeServerAddress);
            }
            this.mNetworkLoadScheduler = scheduler;
            this.mResourceStorage.loadResourceFromDiskInBackground();
        }
    }

    @VisibleForTesting
    String getResourceVersion() {
        return this.mResourceVersion;
    }

    @VisibleForTesting
    synchronized void loadAfterDelay(long delay) {
        if (!(this.mNetworkLoadScheduler == null || isDefaultContainerRefreshMode())) {
            this.mNetworkLoadScheduler.loadAfterDelay(delay, this.mLastLoadedSupplementedResource.fingerprint);
        }
    }

    private synchronized void saveResourceToDisk(SupplementedResource supplementedResource) {
        if (this.mResourceStorage != null) {
            ResourceWithMetadata resourceWithMetadata = new ResourceWithMetadata();
            resourceWithMetadata.timeStamp = getLastRefreshTime();
            resourceWithMetadata.resource = new Resource();
            resourceWithMetadata.supplementedResource = supplementedResource;
            this.mResourceStorage.saveResourceToDiskInBackground(resourceWithMetadata);
        }
    }

    private void initEvaluators(Resource resource) {
        try {
            initEvaluatorsWithExpandedResource(ResourceUtil.getExpandedResource(resource));
        } catch (InvalidResourceException err) {
            Log.e("Not loading resource: " + resource + " because it is invalid: " + err.toString());
        }
    }

    private void setSupplementals(Supplemental[] supplementals) {
        List<Supplemental> supplementalList = new ArrayList();
        for (Supplemental supplemental : supplementals) {
            supplementalList.add(supplemental);
        }
        getRuntime().setSupplementals(supplementalList);
    }

    private void initEvaluatorsWithExpandedResource(ExpandedResource expandedResource) {
        this.mResourceVersion = expandedResource.getVersion();
        this.mResourceFormatVersion = expandedResource.getResourceFormatVersion();
        ExpandedResource expandedResource2 = expandedResource;
        setRuntime(new Runtime(this.mContext, expandedResource2, this.mTagManager.getDataLayer(), new FunctionCallMacroHandlerAdapter(), new FunctionCallTagHandlerAdapter(), createEventInfoDistributor(this.mResourceVersion)));
    }

    @VisibleForTesting
    EventInfoDistributor createEventInfoDistributor(String resourceVersion) {
        if (PreviewManager.getInstance().getPreviewMode().equals(PreviewMode.CONTAINER_DEBUG)) {
        }
        return new NoopEventInfoDistributor();
    }

    private synchronized void setRuntime(Runtime runtime) {
        this.mRuntime = runtime;
    }

    private synchronized Runtime getRuntime() {
        return this.mRuntime;
    }

    @VisibleForTesting
    synchronized void setCtfeServerAddress(String addr) {
        this.mCtfeServerAddress = addr;
        if (addr != null) {
            this.mCtfeHost.setCtfeServerAddress(addr);
        }
    }

    @VisibleForTesting
    synchronized void setCtfeUrlPathAndQuery(String ctfeUrlPathAndQuery) {
        this.mCtfeUrlPathAndQuery = ctfeUrlPathAndQuery;
        if (this.mNetworkLoadScheduler != null) {
            this.mNetworkLoadScheduler.setCtfeURLPathAndQuery(ctfeUrlPathAndQuery);
        }
    }

    String getCtfeUrlPathAndQuery() {
        return this.mCtfeUrlPathAndQuery;
    }

    private boolean isContainerPreview() {
        PreviewManager previewManager = PreviewManager.getInstance();
        return ((previewManager.getPreviewMode() == PreviewMode.CONTAINER || previewManager.getPreviewMode() == PreviewMode.CONTAINER_DEBUG) && this.mContainerId.equals(previewManager.getContainerId())) ? true : ENABLE_CONTAINER_DEBUG_MODE;
    }

    private void createInitialContainer() {
        String containerFile = "tagmanager/" + this.mContainerId;
        Resource resource = this.mResourceStorage.loadResourceFromContainerAsset(containerFile);
        if (resource != null) {
            initEvaluators(resource);
            return;
        }
        ExpandedResource expandedResource = this.mResourceStorage.loadExpandedResourceFromJsonAsset(containerFile + ".json");
        if (expandedResource == null) {
            Log.w("No default container found; creating an empty container.");
            expandedResource = ExpandedResource.newBuilder().build();
        }
        initEvaluatorsWithExpandedResource(expandedResource);
    }

    private boolean isDefaultContainerRefreshMode() {
        return this.mTagManager.getRefreshMode() == RefreshMode.DEFAULT_CONTAINER ? true : ENABLE_CONTAINER_DEBUG_MODE;
    }

    private boolean useAvailableToken(long currentTime) {
        if (this.mLastRefreshMethodCalledTime == 0) {
            this.mNumTokens--;
            return true;
        }
        long timeElapsed = currentTime - this.mLastRefreshMethodCalledTime;
        if (timeElapsed < 5000) {
            return ENABLE_CONTAINER_DEBUG_MODE;
        }
        if (this.mNumTokens < MAX_NUMBER_OF_TOKENS) {
            this.mNumTokens = Math.min(MAX_NUMBER_OF_TOKENS, this.mNumTokens + ((int) Math.floor((double) (timeElapsed / MINIMUM_REFRESH_PERIOD_MS))));
        }
        if (this.mNumTokens <= 0) {
            return ENABLE_CONTAINER_DEBUG_MODE;
        }
        this.mNumTokens--;
        return true;
    }
}
