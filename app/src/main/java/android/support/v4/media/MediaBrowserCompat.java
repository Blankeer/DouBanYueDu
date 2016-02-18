package android.support.v4.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.IMediaBrowserServiceCompat.Stub;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public final class MediaBrowserCompat {
    private final MediaBrowserImplBase mImpl;

    public static class ConnectionCallback {
        public void onConnected() {
        }

        public void onConnectionSuspended() {
        }

        public void onConnectionFailed() {
        }
    }

    public static abstract class ItemCallback {
        public void onItemLoaded(MediaItem item) {
        }

        public void onError(@NonNull String itemId) {
        }
    }

    static class MediaBrowserImplBase {
        private static final int CONNECT_STATE_CONNECTED = 2;
        private static final int CONNECT_STATE_CONNECTING = 1;
        private static final int CONNECT_STATE_DISCONNECTED = 0;
        private static final int CONNECT_STATE_SUSPENDED = 3;
        private static final boolean DBG = false;
        private static final String TAG = "MediaBrowserCompat";
        private final ConnectionCallback mCallback;
        private final Context mContext;
        private Bundle mExtras;
        private final Handler mHandler;
        private Token mMediaSessionToken;
        private final Bundle mRootHints;
        private String mRootId;
        private IMediaBrowserServiceCompat mServiceBinder;
        private IMediaBrowserServiceCompatCallbacks mServiceCallbacks;
        private final ComponentName mServiceComponent;
        private MediaServiceConnection mServiceConnection;
        private int mState;
        private final ArrayMap<String, Subscription> mSubscriptions;

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ ServiceConnection val$thisConnection;

            AnonymousClass1(ServiceConnection serviceConnection) {
                this.val$thisConnection = serviceConnection;
            }

            public void run() {
                if (this.val$thisConnection == MediaBrowserImplBase.this.mServiceConnection) {
                    MediaBrowserImplBase.this.forceCloseConnection();
                    MediaBrowserImplBase.this.mCallback.onConnectionFailed();
                }
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.2 */
        class AnonymousClass2 implements Runnable {
            final /* synthetic */ ItemCallback val$cb;
            final /* synthetic */ String val$mediaId;

            AnonymousClass2(ItemCallback itemCallback, String str) {
                this.val$cb = itemCallback;
                this.val$mediaId = str;
            }

            public void run() {
                this.val$cb.onError(this.val$mediaId);
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.4 */
        class AnonymousClass4 implements Runnable {
            final /* synthetic */ ItemCallback val$cb;
            final /* synthetic */ String val$mediaId;

            AnonymousClass4(ItemCallback itemCallback, String str) {
                this.val$cb = itemCallback;
                this.val$mediaId = str;
            }

            public void run() {
                this.val$cb.onError(this.val$mediaId);
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.5 */
        class AnonymousClass5 implements Runnable {
            final /* synthetic */ IMediaBrowserServiceCompatCallbacks val$callback;
            final /* synthetic */ Bundle val$extra;
            final /* synthetic */ String val$root;
            final /* synthetic */ Token val$session;

            AnonymousClass5(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, String str, Token token, Bundle bundle) {
                this.val$callback = iMediaBrowserServiceCompatCallbacks;
                this.val$root = str;
                this.val$session = token;
                this.val$extra = bundle;
            }

            public void run() {
                if (!MediaBrowserImplBase.this.isCurrent(this.val$callback, "onConnect")) {
                    return;
                }
                if (MediaBrowserImplBase.this.mState != MediaBrowserImplBase.CONNECT_STATE_CONNECTING) {
                    Log.w(MediaBrowserImplBase.TAG, "onConnect from service while mState=" + MediaBrowserImplBase.getStateLabel(MediaBrowserImplBase.this.mState) + "... ignoring");
                    return;
                }
                MediaBrowserImplBase.this.mRootId = this.val$root;
                MediaBrowserImplBase.this.mMediaSessionToken = this.val$session;
                MediaBrowserImplBase.this.mExtras = this.val$extra;
                MediaBrowserImplBase.this.mState = MediaBrowserImplBase.CONNECT_STATE_CONNECTED;
                MediaBrowserImplBase.this.mCallback.onConnected();
                for (String id : MediaBrowserImplBase.this.mSubscriptions.keySet()) {
                    try {
                        MediaBrowserImplBase.this.mServiceBinder.addSubscription(id, MediaBrowserImplBase.this.mServiceCallbacks);
                    } catch (RemoteException e) {
                        Log.d(MediaBrowserImplBase.TAG, "addSubscription failed with RemoteException parentId=" + id);
                    }
                }
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.6 */
        class AnonymousClass6 implements Runnable {
            final /* synthetic */ IMediaBrowserServiceCompatCallbacks val$callback;

            AnonymousClass6(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) {
                this.val$callback = iMediaBrowserServiceCompatCallbacks;
            }

            public void run() {
                Log.e(MediaBrowserImplBase.TAG, "onConnectFailed for " + MediaBrowserImplBase.this.mServiceComponent);
                if (!MediaBrowserImplBase.this.isCurrent(this.val$callback, "onConnectFailed")) {
                    return;
                }
                if (MediaBrowserImplBase.this.mState != MediaBrowserImplBase.CONNECT_STATE_CONNECTING) {
                    Log.w(MediaBrowserImplBase.TAG, "onConnect from service while mState=" + MediaBrowserImplBase.getStateLabel(MediaBrowserImplBase.this.mState) + "... ignoring");
                    return;
                }
                MediaBrowserImplBase.this.forceCloseConnection();
                MediaBrowserImplBase.this.mCallback.onConnectionFailed();
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.7 */
        class AnonymousClass7 implements Runnable {
            final /* synthetic */ IMediaBrowserServiceCompatCallbacks val$callback;
            final /* synthetic */ List val$list;
            final /* synthetic */ String val$parentId;

            AnonymousClass7(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, List list, String str) {
                this.val$callback = iMediaBrowserServiceCompatCallbacks;
                this.val$list = list;
                this.val$parentId = str;
            }

            public void run() {
                if (MediaBrowserImplBase.this.isCurrent(this.val$callback, "onLoadChildren")) {
                    List<MediaItem> data = this.val$list;
                    if (data == null) {
                        data = Collections.emptyList();
                    }
                    Subscription subscription = (Subscription) MediaBrowserImplBase.this.mSubscriptions.get(this.val$parentId);
                    if (subscription != null) {
                        subscription.callback.onChildrenLoaded(this.val$parentId, data);
                    }
                }
            }
        }

        private class MediaServiceConnection implements ServiceConnection {
            private MediaServiceConnection() {
            }

            public void onServiceConnected(ComponentName name, IBinder binder) {
                if (isCurrent("onServiceConnected")) {
                    MediaBrowserImplBase.this.mServiceBinder = Stub.asInterface(binder);
                    MediaBrowserImplBase.this.mServiceCallbacks = MediaBrowserImplBase.this.getNewServiceCallbacks();
                    MediaBrowserImplBase.this.mState = MediaBrowserImplBase.CONNECT_STATE_CONNECTING;
                    try {
                        MediaBrowserImplBase.this.mServiceBinder.connect(MediaBrowserImplBase.this.mContext.getPackageName(), MediaBrowserImplBase.this.mRootHints, MediaBrowserImplBase.this.mServiceCallbacks);
                    } catch (RemoteException e) {
                        Log.w(MediaBrowserImplBase.TAG, "RemoteException during connect for " + MediaBrowserImplBase.this.mServiceComponent);
                    }
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                if (isCurrent("onServiceDisconnected")) {
                    MediaBrowserImplBase.this.mServiceBinder = null;
                    MediaBrowserImplBase.this.mServiceCallbacks = null;
                    MediaBrowserImplBase.this.mState = MediaBrowserImplBase.CONNECT_STATE_SUSPENDED;
                    MediaBrowserImplBase.this.mCallback.onConnectionSuspended();
                }
            }

            private boolean isCurrent(String funcName) {
                if (MediaBrowserImplBase.this.mServiceConnection == this) {
                    return true;
                }
                if (MediaBrowserImplBase.this.mState != 0) {
                    Log.i(MediaBrowserImplBase.TAG, funcName + " for " + MediaBrowserImplBase.this.mServiceComponent + " with mServiceConnection=" + MediaBrowserImplBase.this.mServiceConnection + " this=" + this);
                }
                return MediaBrowserImplBase.DBG;
            }
        }

        private static class Subscription {
            SubscriptionCallback callback;
            final String id;

            Subscription(String id) {
                this.id = id;
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplBase.3 */
        class AnonymousClass3 extends ResultReceiver {
            final /* synthetic */ ItemCallback val$cb;
            final /* synthetic */ String val$mediaId;

            AnonymousClass3(Handler x0, ItemCallback itemCallback, String str) {
                this.val$cb = itemCallback;
                this.val$mediaId = str;
                super(x0);
            }

            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 0 && resultData != null && resultData.containsKey(MediaBrowserServiceCompat.KEY_MEDIA_ITEM)) {
                    Parcelable item = resultData.getParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM);
                    if (item instanceof MediaItem) {
                        this.val$cb.onItemLoaded((MediaItem) item);
                        return;
                    } else {
                        this.val$cb.onError(this.val$mediaId);
                        return;
                    }
                }
                this.val$cb.onError(this.val$mediaId);
            }
        }

        private static class ServiceCallbacks extends IMediaBrowserServiceCompatCallbacks.Stub {
            private WeakReference<MediaBrowserImplBase> mMediaBrowser;

            public ServiceCallbacks(MediaBrowserImplBase mediaBrowser) {
                this.mMediaBrowser = new WeakReference(mediaBrowser);
            }

            public void onConnect(String root, Token session, Bundle extras) {
                MediaBrowserImplBase mediaBrowser = (MediaBrowserImplBase) this.mMediaBrowser.get();
                if (mediaBrowser != null) {
                    mediaBrowser.onServiceConnected(this, root, session, extras);
                }
            }

            public void onConnectFailed() {
                MediaBrowserImplBase mediaBrowser = (MediaBrowserImplBase) this.mMediaBrowser.get();
                if (mediaBrowser != null) {
                    mediaBrowser.onConnectionFailed(this);
                }
            }

            public void onLoadChildren(String parentId, List list) {
                MediaBrowserImplBase mediaBrowser = (MediaBrowserImplBase) this.mMediaBrowser.get();
                if (mediaBrowser != null) {
                    mediaBrowser.onLoadChildren(this, parentId, list);
                }
            }
        }

        public MediaBrowserImplBase(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
            this.mHandler = new Handler();
            this.mSubscriptions = new ArrayMap();
            this.mState = CONNECT_STATE_DISCONNECTED;
            if (context == null) {
                throw new IllegalArgumentException("context must not be null");
            } else if (serviceComponent == null) {
                throw new IllegalArgumentException("service component must not be null");
            } else if (callback == null) {
                throw new IllegalArgumentException("connection callback must not be null");
            } else {
                this.mContext = context;
                this.mServiceComponent = serviceComponent;
                this.mCallback = callback;
                this.mRootHints = rootHints;
            }
        }

        public void connect() {
            if (this.mState != 0) {
                throw new IllegalStateException("connect() called while not disconnected (state=" + getStateLabel(this.mState) + ")");
            } else if (this.mServiceBinder != null) {
                throw new RuntimeException("mServiceBinder should be null. Instead it is " + this.mServiceBinder);
            } else if (this.mServiceCallbacks != null) {
                throw new RuntimeException("mServiceCallbacks should be null. Instead it is " + this.mServiceCallbacks);
            } else {
                this.mState = CONNECT_STATE_CONNECTING;
                Intent intent = new Intent(MediaBrowserServiceCompat.SERVICE_INTERFACE);
                intent.setComponent(this.mServiceComponent);
                ServiceConnection thisConnection = new MediaServiceConnection();
                this.mServiceConnection = thisConnection;
                boolean bound = DBG;
                try {
                    bound = this.mContext.bindService(intent, this.mServiceConnection, CONNECT_STATE_CONNECTING);
                } catch (Exception e) {
                    Log.e(TAG, "Failed binding to service " + this.mServiceComponent);
                }
                if (!bound) {
                    this.mHandler.post(new AnonymousClass1(thisConnection));
                }
            }
        }

        public void disconnect() {
            if (this.mServiceCallbacks != null) {
                try {
                    this.mServiceBinder.disconnect(this.mServiceCallbacks);
                } catch (RemoteException e) {
                    Log.w(TAG, "RemoteException during connect for " + this.mServiceComponent);
                }
            }
            forceCloseConnection();
        }

        private void forceCloseConnection() {
            if (this.mServiceConnection != null) {
                this.mContext.unbindService(this.mServiceConnection);
            }
            this.mState = CONNECT_STATE_DISCONNECTED;
            this.mServiceConnection = null;
            this.mServiceBinder = null;
            this.mServiceCallbacks = null;
            this.mRootId = null;
            this.mMediaSessionToken = null;
        }

        public boolean isConnected() {
            return this.mState == CONNECT_STATE_CONNECTED ? true : DBG;
        }

        @NonNull
        public ComponentName getServiceComponent() {
            if (isConnected()) {
                return this.mServiceComponent;
            }
            throw new IllegalStateException("getServiceComponent() called while not connected (state=" + this.mState + ")");
        }

        @NonNull
        public String getRoot() {
            if (isConnected()) {
                return this.mRootId;
            }
            throw new IllegalStateException("getSessionToken() called while not connected(state=" + getStateLabel(this.mState) + ")");
        }

        @Nullable
        public Bundle getExtras() {
            if (isConnected()) {
                return this.mExtras;
            }
            throw new IllegalStateException("getExtras() called while not connected (state=" + getStateLabel(this.mState) + ")");
        }

        @NonNull
        public Token getSessionToken() {
            if (isConnected()) {
                return this.mMediaSessionToken;
            }
            throw new IllegalStateException("getSessionToken() called while not connected(state=" + this.mState + ")");
        }

        public void subscribe(@NonNull String parentId, @NonNull SubscriptionCallback callback) {
            if (parentId == null) {
                throw new IllegalArgumentException("parentId is null");
            } else if (callback == null) {
                throw new IllegalArgumentException("callback is null");
            } else {
                Subscription sub = (Subscription) this.mSubscriptions.get(parentId);
                if (sub == null ? true : DBG) {
                    sub = new Subscription(parentId);
                    this.mSubscriptions.put(parentId, sub);
                }
                sub.callback = callback;
                if (this.mState == CONNECT_STATE_CONNECTED) {
                    try {
                        this.mServiceBinder.addSubscription(parentId, this.mServiceCallbacks);
                    } catch (RemoteException e) {
                        Log.d(TAG, "addSubscription failed with RemoteException parentId=" + parentId);
                    }
                }
            }
        }

        public void unsubscribe(@NonNull String parentId) {
            if (TextUtils.isEmpty(parentId)) {
                throw new IllegalArgumentException("parentId is empty.");
            }
            Subscription sub = (Subscription) this.mSubscriptions.remove(parentId);
            if (this.mState == CONNECT_STATE_CONNECTED && sub != null) {
                try {
                    this.mServiceBinder.removeSubscription(parentId, this.mServiceCallbacks);
                } catch (RemoteException e) {
                    Log.d(TAG, "removeSubscription failed with RemoteException parentId=" + parentId);
                }
            }
        }

        public void getItem(@NonNull String mediaId, @NonNull ItemCallback cb) {
            if (TextUtils.isEmpty(mediaId)) {
                throw new IllegalArgumentException("mediaId is empty.");
            } else if (cb == null) {
                throw new IllegalArgumentException("cb is null.");
            } else if (this.mState != CONNECT_STATE_CONNECTED) {
                Log.i(TAG, "Not connected, unable to retrieve the MediaItem.");
                this.mHandler.post(new AnonymousClass2(cb, mediaId));
            } else {
                try {
                    this.mServiceBinder.getMediaItem(mediaId, new AnonymousClass3(this.mHandler, cb, mediaId));
                } catch (RemoteException e) {
                    Log.i(TAG, "Remote error getting media item.");
                    this.mHandler.post(new AnonymousClass4(cb, mediaId));
                }
            }
        }

        private static String getStateLabel(int state) {
            switch (state) {
                case CONNECT_STATE_DISCONNECTED /*0*/:
                    return "CONNECT_STATE_DISCONNECTED";
                case CONNECT_STATE_CONNECTING /*1*/:
                    return "CONNECT_STATE_CONNECTING";
                case CONNECT_STATE_CONNECTED /*2*/:
                    return "CONNECT_STATE_CONNECTED";
                case CONNECT_STATE_SUSPENDED /*3*/:
                    return "CONNECT_STATE_SUSPENDED";
                default:
                    return "UNKNOWN/" + state;
            }
        }

        private final void onServiceConnected(IMediaBrowserServiceCompatCallbacks callback, String root, Token session, Bundle extra) {
            this.mHandler.post(new AnonymousClass5(callback, root, session, extra));
        }

        private final void onConnectionFailed(IMediaBrowserServiceCompatCallbacks callback) {
            this.mHandler.post(new AnonymousClass6(callback));
        }

        private final void onLoadChildren(IMediaBrowserServiceCompatCallbacks callback, String parentId, List list) {
            this.mHandler.post(new AnonymousClass7(callback, list, parentId));
        }

        private boolean isCurrent(IMediaBrowserServiceCompatCallbacks callback, String funcName) {
            if (this.mServiceCallbacks == callback) {
                return true;
            }
            if (this.mState != 0) {
                Log.i(TAG, funcName + " for " + this.mServiceComponent + " with mServiceConnection=" + this.mServiceCallbacks + " this=" + this);
            }
            return DBG;
        }

        private ServiceCallbacks getNewServiceCallbacks() {
            return new ServiceCallbacks(this);
        }

        void dump() {
            Log.d(TAG, "MediaBrowserCompat...");
            Log.d(TAG, "  mServiceComponent=" + this.mServiceComponent);
            Log.d(TAG, "  mCallback=" + this.mCallback);
            Log.d(TAG, "  mRootHints=" + this.mRootHints);
            Log.d(TAG, "  mState=" + getStateLabel(this.mState));
            Log.d(TAG, "  mServiceConnection=" + this.mServiceConnection);
            Log.d(TAG, "  mServiceBinder=" + this.mServiceBinder);
            Log.d(TAG, "  mServiceCallbacks=" + this.mServiceCallbacks);
            Log.d(TAG, "  mRootId=" + this.mRootId);
            Log.d(TAG, "  mMediaSessionToken=" + this.mMediaSessionToken);
        }
    }

    public static class MediaItem implements Parcelable {
        public static final Creator<MediaItem> CREATOR;
        public static final int FLAG_BROWSABLE = 1;
        public static final int FLAG_PLAYABLE = 2;
        private final MediaDescriptionCompat mDescription;
        private final int mFlags;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Flags {
        }

        public MediaItem(@NonNull MediaDescriptionCompat description, int flags) {
            if (description == null) {
                throw new IllegalArgumentException("description cannot be null");
            } else if (TextUtils.isEmpty(description.getMediaId())) {
                throw new IllegalArgumentException("description must have a non-empty media id");
            } else {
                this.mFlags = flags;
                this.mDescription = description;
            }
        }

        private MediaItem(Parcel in) {
            this.mFlags = in.readInt();
            this.mDescription = (MediaDescriptionCompat) MediaDescriptionCompat.CREATOR.createFromParcel(in);
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mFlags);
            this.mDescription.writeToParcel(out, flags);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("MediaItem{");
            sb.append("mFlags=").append(this.mFlags);
            sb.append(", mDescription=").append(this.mDescription);
            sb.append('}');
            return sb.toString();
        }

        static {
            CREATOR = new Creator<MediaItem>() {
                public MediaItem createFromParcel(Parcel in) {
                    return new MediaItem(null);
                }

                public MediaItem[] newArray(int size) {
                    return new MediaItem[size];
                }
            };
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean isBrowsable() {
            return (this.mFlags & FLAG_BROWSABLE) != 0;
        }

        public boolean isPlayable() {
            return (this.mFlags & FLAG_PLAYABLE) != 0;
        }

        @NonNull
        public MediaDescriptionCompat getDescription() {
            return this.mDescription;
        }

        @NonNull
        public String getMediaId() {
            return this.mDescription.getMediaId();
        }
    }

    public static abstract class SubscriptionCallback {
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaItem> list) {
        }

        public void onError(@NonNull String parentId) {
        }
    }

    public MediaBrowserCompat(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
        this.mImpl = new MediaBrowserImplBase(context, serviceComponent, callback, rootHints);
    }

    public void connect() {
        this.mImpl.connect();
    }

    public void disconnect() {
        this.mImpl.disconnect();
    }

    public boolean isConnected() {
        return this.mImpl.isConnected();
    }

    @NonNull
    public ComponentName getServiceComponent() {
        return this.mImpl.getServiceComponent();
    }

    @NonNull
    public String getRoot() {
        return this.mImpl.getRoot();
    }

    @Nullable
    public Bundle getExtras() {
        return this.mImpl.getExtras();
    }

    @NonNull
    public Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }

    public void subscribe(@NonNull String parentId, @NonNull SubscriptionCallback callback) {
        this.mImpl.subscribe(parentId, callback);
    }

    public void unsubscribe(@NonNull String parentId) {
        this.mImpl.unsubscribe(parentId);
    }

    public void getItem(@NonNull String mediaId, @NonNull ItemCallback cb) {
        this.mImpl.getItem(mediaId, cb);
    }
}
