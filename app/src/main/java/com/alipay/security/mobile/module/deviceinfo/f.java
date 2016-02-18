package com.alipay.security.mobile.module.deviceinfo;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

final class f extends PhoneStateListener {
    final /* synthetic */ e a;
    final /* synthetic */ TelephonyManager b;

    f(e eVar, TelephonyManager telephonyManager) {
        this.a = eVar;
        this.b = telephonyManager;
    }

    public final void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (signalStrength != null) {
            this.a.a = signalStrength.getGsmSignalStrength();
        }
        this.b.listen(this, 0);
    }
}
