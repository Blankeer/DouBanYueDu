package android.support.v4.media;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.IMediaBrowserServiceCompat.Stub;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

public abstract class MediaBrowserServiceCompat extends Service {
    private static final boolean DBG = false;
    public static final String KEY_MEDIA_ITEM = "media_item";
    public static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserServiceCompat";
    private static final String TAG = "MediaBrowserServiceCompat";
    private ServiceBinder mBinder;
    private final ArrayMap<IBinder, ConnectionRecord> mConnections;
    private final Handler mHandler;
    Token mSession;

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Token val$token;

        AnonymousClass1(Token token) {
            this.val$token = token;
        }

        public void run() {
            for (IBinder key : MediaBrowserServiceCompat.this.mConnections.keySet()) {
                ConnectionRecord connection = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(key);
                try {
                    connection.callbacks.onConnect(connection.root.getRootId(), this.val$token, connection.root.getExtras());
                } catch (RemoteException e) {
                    Log.w(MediaBrowserServiceCompat.TAG, "Connection for " + connection.pkg + " is no longer valid.");
                    MediaBrowserServiceCompat.this.mConnections.remove(key);
                }
            }
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ String val$parentId;

        AnonymousClass2(String str) {
            this.val$parentId = str;
        }

        public void run() {
            for (IBinder binder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
                ConnectionRecord connection = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(binder);
                if (connection.subscriptions.contains(this.val$parentId)) {
                    MediaBrowserServiceCompat.this.performLoadChildren(this.val$parentId, connection);
                }
            }
        }
    }

    public static final class BrowserRoot {
        private final Bundle mExtras;
        private final String mRootId;

        public BrowserRoot(@NonNull String rootId, @Nullable Bundle extras) {
            if (rootId == null) {
                throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead.");
            }
            this.mRootId = rootId;
            this.mExtras = extras;
        }

        public String getRootId() {
            return this.mRootId;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }
    }

    private class ConnectionRecord {
        IMediaBrowserServiceCompatCallbacks callbacks;
        String pkg;
        BrowserRoot root;
        Bundle rootHints;
        HashSet<String> subscriptions;

        private ConnectionRecord() {
            this.subscriptions = new HashSet();
        }
    }

    public class Result<T> {
        private Object mDebug;
        private boolean mDetachCalled;
        private boolean mSendResultCalled;

        Result(Object debug) {
            this.mDebug = debug;
        }

        public void sendResult(T result) {
            if (this.mSendResultCalled) {
                throw new IllegalStateException("sendResult() called twice for: " + this.mDebug);
            }
            this.mSendResultCalled = true;
            onResultSent(result);
        }

        public void detach() {
            if (this.mDetachCalled) {
                throw new IllegalStateException("detach() called when detach() had already been called for: " + this.mDebug);
            } else if (this.mSendResultCalled) {
                throw new IllegalStateException("detach() called when sendResult() had already been called for: " + this.mDebug);
            } else {
                this.mDetachCalled = true;
            }
        }

        boolean isDone() {
            return (this.mDetachCalled || this.mSendResultCalled) ? true : MediaBrowserServiceCompat.DBG;
        }

        void onResultSent(T t) {
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.3 */
    class AnonymousClass3 extends Result<List<MediaItem>> {
        final /* synthetic */ ConnectionRecord val$connection;
        final /* synthetic */ String val$parentId;

        AnonymousClass3(Object x0, String str, ConnectionRecord connectionRecord) {
            this.val$parentId = str;
            this.val$connection = connectionRecord;
            super(x0);
        }

        void onResultSent(List<MediaItem> list) {
            if (list == null) {
                throw new IllegalStateException("onLoadChildren sent null list for id " + this.val$parentId);
            } else if (MediaBrowserServiceCompat.this.mConnections.get(this.val$connection.callbacks.asBinder()) == this.val$connection) {
                try {
                    this.val$connection.callbacks.onLoadChildren(this.val$parentId, list);
                } catch (RemoteException e) {
                    Log.w(MediaBrowserServiceCompat.TAG, "Calling onLoadChildren() failed for id=" + this.val$parentId + " package=" + this.val$connection.pkg);
                }
            }
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.4 */
    class AnonymousClass4 extends Result<MediaItem> {
        final /* synthetic */ ResultReceiver val$receiver;

        AnonymousClass4(Object x0, ResultReceiver resultReceiver) {
            this.val$receiver = resultReceiver;
            super(x0);
        }

        void onResultSent(MediaItem item) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM, item);
            this.val$receiver.send(0, bundle);
        }
    }

    private class ServiceBinder extends Stub {

        /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ IMediaBrowserServiceCompatCallbacks val$callbacks;
            final /* synthetic */ String val$pkg;
            final /* synthetic */ Bundle val$rootHints;
            final /* synthetic */ int val$uid;

            AnonymousClass1(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, String str, Bundle bundle, int i) {
                this.val$callbacks = iMediaBrowserServiceCompatCallbacks;
                this.val$pkg = str;
                this.val$rootHints = bundle;
                this.val$uid = i;
            }

            public void run() {
                IBinder b = this.val$callbacks.asBinder();
                MediaBrowserServiceCompat.this.mConnections.remove(b);
                ConnectionRecord connection = new ConnectionRecord(null);
                connection.pkg = this.val$pkg;
                connection.rootHints = this.val$rootHints;
                connection.callbacks = this.val$callbacks;
                connection.root = MediaBrowserServiceCompat.this.onGetRoot(this.val$pkg, this.val$uid, this.val$rootHints);
                if (connection.root == null) {
                    Log.i(MediaBrowserServiceCompat.TAG, "No root for client " + this.val$pkg + " from service " + getClass().getName());
                    try {
                        this.val$callbacks.onConnectFailed();
                        return;
                    } catch (RemoteException e) {
                        Log.w(MediaBrowserServiceCompat.TAG, "Calling onConnectFailed() failed. Ignoring. pkg=" + this.val$pkg);
                        return;
                    }
                }
                try {
                    MediaBrowserServiceCompat.this.mConnections.put(b, connection);
                    if (MediaBrowserServiceCompat.this.mSession != null) {
                        this.val$callbacks.onConnect(connection.root.getRootId(), MediaBrowserServiceCompat.this.mSession, connection.root.getExtras());
                    }
                } catch (RemoteException e2) {
                    Log.w(MediaBrowserServiceCompat.TAG, "Calling onConnect() failed. Dropping client. pkg=" + this.val$pkg);
                    MediaBrowserServiceCompat.this.mConnections.remove(b);
                }
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.2 */
        class AnonymousClass2 implements Runnable {
            final /* synthetic */ IMediaBrowserServiceCompatCallbacks val$callbacks;

            AnonymousClass2(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) {
                this.val$callbacks = iMediaBrowserServiceCompatCallbacks;
            }

            public void run() {
                if (((ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.remove(this.val$callbacks.asBinder())) == null) {
                }
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.3 */
        class AnonymousClass3 implements Runnable {
            final /* synthetic */ IMediaBrowserServiceCompatCallbacks val$callbacks;
            final /* synthetic */ String val$id;

            AnonymousClass3(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, String str) {
                this.val$callbacks = iMediaBrowserServiceCompatCallbacks;
                this.val$id = str;
            }

            public void run() {
                ConnectionRecord connection = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(this.val$callbacks.asBinder());
                if (connection == null) {
                    Log.w(MediaBrowserServiceCompat.TAG, "addSubscription for callback that isn't registered id=" + this.val$id);
                } else {
                    MediaBrowserServiceCompat.this.addSubscription(this.val$id, connection);
                }
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.4 */
        class AnonymousClass4 implements Runnable {
            final /* synthetic */ IMediaBrowserServiceCompatCallbacks val$callbacks;
            final /* synthetic */ String val$id;

            AnonymousClass4(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks, String str) {
                this.val$callbacks = iMediaBrowserServiceCompatCallbacks;
                this.val$id = str;
            }

            public void run() {
                ConnectionRecord connection = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(this.val$callbacks.asBinder());
                if (connection == null) {
                    Log.w(MediaBrowserServiceCompat.TAG, "removeSubscription for callback that isn't registered id=" + this.val$id);
                } else if (!connection.subscriptions.remove(this.val$id)) {
                    Log.w(MediaBrowserServiceCompat.TAG, "removeSubscription called for " + this.val$id + " which is not subscribed");
                }
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserServiceCompat.ServiceBinder.5 */
        class AnonymousClass5 implements Runnable {
            final /* synthetic */ String val$mediaId;
            final /* synthetic */ ResultReceiver val$receiver;

            AnonymousClass5(String str, ResultReceiver resultReceiver) {
                this.val$mediaId = str;
                this.val$receiver = resultReceiver;
            }

            public void run() {
                MediaBrowserServiceCompat.this.performLoadItem(this.val$mediaId, this.val$receiver);
            }
        }

        private ServiceBinder() {
        }

        public void connect(String pkg, Bundle rootHints, IMediaBrowserServiceCompatCallbacks callbacks) {
            int uid = Binder.getCallingUid();
            if (MediaBrowserServiceCompat.this.isValidPackage(pkg, uid)) {
                MediaBrowserServiceCompat.this.mHandler.post(new AnonymousClass1(callbacks, pkg, rootHints, uid));
                return;
            }
            throw new IllegalArgumentException("Package/uid mismatch: uid=" + uid + " package=" + pkg);
        }

        public void disconnect(IMediaBrowserServiceCompatCallbacks callbacks) {
            MediaBrowserServiceCompat.this.mHandler.post(new AnonymousClass2(callbacks));
        }

        public void addSubscription(String id, IMediaBrowserServiceCompatCallbacks callbacks) {
            MediaBrowserServiceCompat.this.mHandler.post(new AnonymousClass3(callbacks, id));
        }

        public void removeSubscription(String id, IMediaBrowserServiceCompatCallbacks callbacks) {
            MediaBrowserServiceCompat.this.mHandler.post(new AnonymousClass4(callbacks, id));
        }

        public void getMediaItem(String mediaId, ResultReceiver receiver) {
            if (!TextUtils.isEmpty(mediaId) && receiver != null) {
                MediaBrowserServiceCompat.this.mHandler.post(new AnonymousClass5(mediaId, receiver));
            }
        }
    }

    @Nullable
    public abstract BrowserRoot onGetRoot(@NonNull String str, int i, @Nullable Bundle bundle);

    public abstract void onLoadChildren(@NonNull String str, @NonNull Result<List<MediaItem>> result);

    public MediaBrowserServiceCompat() {
        this.mConnections = new ArrayMap();
        this.mHandler = new Handler();
    }

    public void onCreate() {
        super.onCreate();
        this.mBinder = new ServiceBinder();
    }

    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mBinder;
        }
        return null;
    }

    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
    }

    public void onLoadItem(String itemId, Result<MediaItem> result) {
        result.sendResult(null);
    }

    public void setSessionToken(Token token) {
        if (token == null) {
            throw new IllegalArgumentException("Session token may not be null.");
        } else if (this.mSession != null) {
            throw new IllegalStateException("The session token has already been set.");
        } else {
            this.mSession = token;
            this.mHandler.post(new AnonymousClass1(token));
        }
    }

    @Nullable
    public Token getSessionToken() {
        return this.mSession;
    }

    public void notifyChildrenChanged(@NonNull String parentId) {
        if (parentId == null) {
            throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
        }
        this.mHandler.post(new AnonymousClass2(parentId));
    }

    private boolean isValidPackage(String pkg, int uid) {
        if (pkg == null) {
            return DBG;
        }
        for (String equals : getPackageManager().getPackagesForUid(uid)) {
            if (equals.equals(pkg)) {
                return true;
            }
        }
        return DBG;
    }

    private void addSubscription(String id, ConnectionRecord connection) {
        connection.subscriptions.add(id);
        performLoadChildren(id, connection);
    }

    private void performLoadChildren(String parentId, ConnectionRecord connection) {
        Result<List<MediaItem>> result = new AnonymousClass3(parentId, parentId, connection);
        onLoadChildren(parentId, result);
        if (!result.isDone()) {
            throw new IllegalStateException("onLoadChildren must call detach() or sendResult() before returning for package=" + connection.pkg + " id=" + parentId);
        }
    }

    private void performLoadItem(String itemId, ResultReceiver receiver) {
        Result<MediaItem> result = new AnonymousClass4(itemId, receiver);
        onLoadItem(itemId, result);
        if (!result.isDone()) {
            throw new IllegalStateException("onLoadItem must call detach() or sendResult() before returning for id=" + itemId);
        }
    }
}
