package android.support.v4.media;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.os.ResultReceiver;

public interface IMediaBrowserServiceCompat extends IInterface {

    public static abstract class Stub extends Binder implements IMediaBrowserServiceCompat {
        private static final String DESCRIPTOR = "android.support.v4.media.IMediaBrowserServiceCompat";
        static final int TRANSACTION_addSubscription = 3;
        static final int TRANSACTION_connect = 1;
        static final int TRANSACTION_disconnect = 2;
        static final int TRANSACTION_getMediaItem = 5;
        static final int TRANSACTION_removeSubscription = 4;

        private static class Proxy implements IMediaBrowserServiceCompat {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void connect(String pkg, Bundle rootHints, IMediaBrowserServiceCompatCallbacks callbacks) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg);
                    if (rootHints != null) {
                        _data.writeInt(Stub.TRANSACTION_connect);
                        rootHints.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (callbacks != null) {
                        iBinder = callbacks.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_connect, _data, null, Stub.TRANSACTION_connect);
                } finally {
                    _data.recycle();
                }
            }

            public void disconnect(IMediaBrowserServiceCompatCallbacks callbacks) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (callbacks != null) {
                        iBinder = callbacks.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_disconnect, _data, null, Stub.TRANSACTION_connect);
                } finally {
                    _data.recycle();
                }
            }

            public void addSubscription(String uri, IMediaBrowserServiceCompatCallbacks callbacks) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (callbacks != null) {
                        iBinder = callbacks.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_addSubscription, _data, null, Stub.TRANSACTION_connect);
                } finally {
                    _data.recycle();
                }
            }

            public void removeSubscription(String uri, IMediaBrowserServiceCompatCallbacks callbacks) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (callbacks != null) {
                        iBinder = callbacks.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_removeSubscription, _data, null, Stub.TRANSACTION_connect);
                } finally {
                    _data.recycle();
                }
            }

            public void getMediaItem(String uri, ResultReceiver cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (cb != null) {
                        _data.writeInt(Stub.TRANSACTION_connect);
                        cb.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getMediaItem, _data, null, Stub.TRANSACTION_connect);
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaBrowserServiceCompat asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IMediaBrowserServiceCompat)) {
                return new Proxy(obj);
            }
            return (IMediaBrowserServiceCompat) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String _arg0;
            switch (code) {
                case TRANSACTION_connect /*1*/:
                    Bundle _arg1;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    connect(_arg0, _arg1, android.support.v4.media.IMediaBrowserServiceCompatCallbacks.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_disconnect /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    disconnect(android.support.v4.media.IMediaBrowserServiceCompatCallbacks.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_addSubscription /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    addSubscription(data.readString(), android.support.v4.media.IMediaBrowserServiceCompatCallbacks.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_removeSubscription /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeSubscription(data.readString(), android.support.v4.media.IMediaBrowserServiceCompatCallbacks.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_getMediaItem /*5*/:
                    ResultReceiver _arg12;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg12 = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    getMediaItem(_arg0, _arg12);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void addSubscription(String str, IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) throws RemoteException;

    void connect(String str, Bundle bundle, IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) throws RemoteException;

    void disconnect(IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) throws RemoteException;

    void getMediaItem(String str, ResultReceiver resultReceiver) throws RemoteException;

    void removeSubscription(String str, IMediaBrowserServiceCompatCallbacks iMediaBrowserServiceCompatCallbacks) throws RemoteException;
}
