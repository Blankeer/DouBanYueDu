package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

class ConnectivityManagerCompatGingerbread {
    ConnectivityManagerCompatGingerbread() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return true;
        }
        switch (info.getType()) {
            case dx.a /*0*/:
            case dx.c /*2*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
            case dj.f /*5*/:
            case ci.g /*6*/:
                return true;
            case dx.b /*1*/:
                return false;
            default:
                return true;
        }
    }
}
