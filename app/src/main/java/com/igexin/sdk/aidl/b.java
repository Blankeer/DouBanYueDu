package com.igexin.sdk.aidl;

import android.os.IBinder;
import android.os.Parcel;

class b implements ICACallback {
    private IBinder a;

    b(IBinder iBinder) {
        this.a = iBinder;
    }

    public IBinder asBinder() {
        return this.a;
    }

    public boolean onAuthenticated(String str, String str2, String str3, long j) {
        boolean z = true;
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.igexin.sdk.aidl.ICACallback");
            obtain.writeString(str);
            obtain.writeString(str2);
            obtain.writeString(str3);
            obtain.writeLong(j);
            this.a.transact(1, obtain, obtain2, 0);
            obtain2.readException();
            if (obtain2.readInt() == 0) {
                z = false;
            }
            obtain2.recycle();
            obtain.recycle();
            return z;
        } catch (Throwable th) {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    public boolean onError(int i) {
        boolean z = false;
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.igexin.sdk.aidl.ICACallback");
            obtain.writeInt(i);
            this.a.transact(2, obtain, obtain2, 0);
            obtain2.readException();
            if (obtain2.readInt() != 0) {
                z = true;
            }
            obtain2.recycle();
            obtain.recycle();
            return z;
        } catch (Throwable th) {
            obtain2.recycle();
            obtain.recycle();
        }
    }
}
