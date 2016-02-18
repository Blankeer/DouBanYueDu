package com.igexin.sdk.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import u.aly.dx;

public abstract class a extends Binder implements ICACallback {
    public static ICACallback a(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.igexin.sdk.aidl.ICACallback");
        return (queryLocalInterface == null || !(queryLocalInterface instanceof ICACallback)) ? new b(iBinder) : (ICACallback) queryLocalInterface;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        int i3 = 0;
        boolean onAuthenticated;
        switch (i) {
            case dx.b /*1*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.ICACallback");
                onAuthenticated = onAuthenticated(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readLong());
                parcel2.writeNoException();
                parcel2.writeInt(onAuthenticated ? 1 : 0);
                return true;
            case dx.c /*2*/:
                parcel.enforceInterface("com.igexin.sdk.aidl.ICACallback");
                onAuthenticated = onError(parcel.readInt());
                parcel2.writeNoException();
                if (onAuthenticated) {
                    i3 = 1;
                }
                parcel2.writeInt(i3);
                return true;
            case 1598968902:
                parcel2.writeString("com.igexin.sdk.aidl.ICACallback");
                return true;
            default:
                return super.onTransact(i, parcel, parcel2, i2);
        }
    }
}
