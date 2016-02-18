package com.igexin.sdk.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;

public abstract class e extends Binder implements UserServiceListener {
    public e() {
        attachInterface(this, "com.igexin.sdk.aidl.UserServiceListener");
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        switch (i) {
            case 1598968902:
                parcel2.writeString("com.igexin.sdk.aidl.UserServiceListener");
                return true;
            default:
                return super.onTransact(i, parcel, parcel2, i2);
        }
    }
}
