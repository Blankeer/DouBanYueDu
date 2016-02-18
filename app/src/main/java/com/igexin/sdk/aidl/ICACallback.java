package com.igexin.sdk.aidl;

import android.os.IInterface;

public interface ICACallback extends IInterface {
    boolean onAuthenticated(String str, String str2, String str3, long j);

    boolean onError(int i);
}
