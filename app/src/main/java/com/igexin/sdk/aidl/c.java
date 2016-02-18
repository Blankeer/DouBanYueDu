package com.igexin.sdk.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.alipay.sdk.protocol.h;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public abstract class c extends Binder implements IGexinMsgService {
    public c() {
        attachInterface(this, "com.igexin.sdk.aidl.IGexinMsgService");
    }

    public static IGexinMsgService a(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.igexin.sdk.aidl.IGexinMsgService");
        return (queryLocalInterface == null || !(queryLocalInterface instanceof IGexinMsgService)) ? new d(iBinder) : (IGexinMsgService) queryLocalInterface;
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        int startService;
        switch (i) {
            case dx.b /*1*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = startService(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case dx.c /*2*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = stopService(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case dx.d /*3*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = isStarted(parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case dx.e /*4*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = setSilentTime(parcel.readInt(), parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case dj.f /*5*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                byte[] extFunction = extFunction(parcel.createByteArray());
                parcel2.writeNoException();
                parcel2.writeByteArray(extFunction);
                return true;
            case ci.g /*6*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = onASNLConnected(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readLong());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case ci.h /*7*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = onPSNLConnected(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readLong());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case h.g /*8*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = sendByASNL(parcel.readString(), parcel.readString(), parcel.createByteArray());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case h.h /*9*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = receiveToPSNL(parcel.readString(), parcel.readString(), parcel.createByteArray());
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case h.i /*10*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = onASNLNetworkConnected();
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.IGexinMsgService");
                startService = onASNLNetworkDisconnected();
                parcel2.writeNoException();
                parcel2.writeInt(startService);
                return true;
            case 1598968902:
                parcel2.writeString("com.igexin.sdk.aidl.IGexinMsgService");
                return true;
            default:
                return super.onTransact(i, parcel, parcel2, i2);
        }
    }
}
