package com.douban.book.reader.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.media.TransportMediator;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.alipay.sdk.protocol.h;
import com.douban.amonsul.MobileStat;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import io.realm.internal.Table;
import java.util.Random;
import se.emilsjolander.stickylistheaders.R;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public class Utils {
    private static final String TAG;
    private static boolean sHasSmartBar;

    static {
        TAG = Utils.class.getSimpleName();
        sHasSmartBar = checkHasSmartBar();
    }

    public static String getFormattedDisplayMetrics() {
        DisplayMetrics displayMetrics = Res.getDisplayMetrics();
        return String.format("%sx%s %sDPI(@%.1fx) (%sx%sdp)", new Object[]{Integer.valueOf(displayMetrics.widthPixels), Integer.valueOf(displayMetrics.heightPixels), Integer.valueOf(displayMetrics.densityDpi), Float.valueOf(displayMetrics.density), Float.valueOf(((float) displayMetrics.widthPixels) / displayMetrics.density), Float.valueOf(((float) displayMetrics.heightPixels) / displayMetrics.density)});
    }

    public static String getDeviceUDID() {
        String udid = Pref.ofApp().getString(Key.APP_DEVICE_UDID, Table.STRING_DEFAULT_VALUE);
        if (!StringUtils.isEmpty(udid)) {
            return udid;
        }
        udid = MobileStat.getDeviceId(App.get());
        Pref.ofApp().set(Key.APP_DEVICE_UDID, udid);
        return udid;
    }

    public static float getDistance(float x1, float y1, float x2, float y2) {
        return ((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1));
    }

    public static int dp2pixel(float dp) {
        return Math.round(dp * Res.get().getDisplayMetrics().density);
    }

    public static int ratioToInt255(float ratio) {
        if (ratio >= 0.0f && ratio <= 1.0f) {
            return Math.round(255.0f * ratio);
        }
        throw new IllegalArgumentException("ratio must be in range [0.0f ~ 1.0f]");
    }

    public static int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) App.get().getSystemService("connectivity");
        if (connectivity == null) {
            return false;
        }
        NetworkInfo[] infos = connectivity.getAllNetworkInfo();
        if (infos == null) {
            return false;
        }
        for (NetworkInfo info : infos) {
            if (info.getState() == State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public static String getNetworkTypeName(int type) {
        switch (type) {
            case dx.b /*1*/:
                return "GPRS";
            case dx.c /*2*/:
                return "EDGE";
            case dx.d /*3*/:
                return "UMTS";
            case dx.e /*4*/:
                return "CDMA";
            case dj.f /*5*/:
                return "CDMA - EvDo rev. 0";
            case ci.g /*6*/:
                return "CDMA - EvDo rev. A";
            case ci.h /*7*/:
                return "CDMA - 1xRTT";
            case h.g /*8*/:
                return "HSDPA";
            case h.h /*9*/:
                return "HSUPA";
            case h.i /*10*/:
                return "HSPA";
            case R.styleable.StickyListHeadersListView_android_stackFromBottom /*11*/:
                return "iDEN";
            case R.styleable.StickyListHeadersListView_android_scrollingCache /*12*/:
                return "CDMA - EvDo rev. B";
            case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                return "LTE";
            case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                return "CDMA - eHRPD";
            case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                return "HSPA+";
            default:
                return "UNKNOWN";
        }
    }

    public static String getNetworkInfo() {
        String result = "(Unknown)";
        try {
            ConnectivityManager connectivity = (ConnectivityManager) App.get().getSystemService("connectivity");
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.getState() == State.CONNECTED) {
                    result = info.getTypeName();
                    if (info.getType() == 0) {
                        TelephonyManager telephony = (TelephonyManager) App.get().getSystemService("phone");
                        String operatorName = telephony.getNetworkOperatorName();
                        String networkName = getNetworkTypeName(telephony.getNetworkType());
                        result = String.format("%s - %s", new Object[]{operatorName, networkName});
                    }
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    public static boolean isUsingWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) App.get().getSystemService("connectivity");
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.getState() == State.CONNECTED && info.getType() == 1) {
            return true;
        }
        return false;
    }

    public static void changeFonts(View v) {
        if (v != null) {
            if (v instanceof TextView) {
                Typeface typeface = ((TextView) v).getTypeface();
                Typeface convertedTypeface = Font.SANS_SERIF;
                if (typeface != null) {
                    if (typeface.isBold()) {
                        convertedTypeface = Font.SANS_SERIF_BOLD;
                    } else if (typeface.isItalic() || typeface == Font.SERIF) {
                        convertedTypeface = Font.SERIF;
                    }
                }
                ((TextView) v).setTypeface(convertedTypeface);
                ((TextView) v).setPaintFlags(((TextView) v).getPaintFlags() | TransportMediator.FLAG_KEY_MEDIA_NEXT);
            } else if (v instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                    changeFonts(((ViewGroup) v).getChildAt(i));
                }
            }
        }
    }

    public static void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }
    }

    public static int getMemorySizeByPercentage(int percent) {
        return (int) (((float) Runtime.getRuntime().maxMemory()) * (((float) percent) / 100.0f));
    }

    public static String humanReadableByteCount(long bytes) {
        if (bytes < ((long) 1024)) {
            return "<1K";
        }
        char pre = "KMGTPE".charAt(((int) (Math.log((double) bytes) / Math.log((double) 1024))) - 1);
        return String.format("%.1f%s", new Object[]{Double.valueOf(((double) bytes) / Math.pow((double) 1024, (double) exp)), Character.valueOf(pre)});
    }

    public static boolean hasSoftNavigationBar() {
        if (VERSION.SDK_INT < 14) {
            return false;
        }
        boolean hasMenuKey = ViewConfiguration.get(App.get()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(4);
        Logger.d(TAG, " has MenuKey " + hasMenuKey + " hasBack Key " + hasBackKey, new Object[0]);
        if ((hasMenuKey || hasBackKey) && !hasSmartBar()) {
            return false;
        }
        return true;
    }

    public static void hideKeyBoard(Activity activity) {
        if (activity != null) {
            ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static void showKeyBoard(Activity activity, View requestView) {
        if (activity != null && requestView != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService("input_method");
            imm.showSoftInput(requestView, 2);
            imm.toggleSoftInput(2, 1);
        }
    }

    public static int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static String formatPriceWithSymbol(int price) {
        return String.format("\uffe5%s", new Object[]{formatPrice(price)});
    }

    public static String formatPrice(int price) {
        double priceInYuan = (double) (((float) price) / 100.0f);
        return String.format("%.2f", new Object[]{Double.valueOf(priceInYuan)});
    }

    public static String formatObject(Object object) {
        return String.format("%s@0x%x", new Object[]{object.getClass().getSimpleName(), Integer.valueOf(object.hashCode())});
    }

    public static boolean hasSmartBar() {
        return sHasSmartBar;
    }

    private static boolean checkHasSmartBar() {
        try {
            return ((Boolean) Class.forName("android.os.Build").getMethod("hasSmartBar", new Class[0]).invoke(null, new Object[0])).booleanValue();
        } catch (Exception e) {
            return Build.DEVICE.equals("mx2");
        }
    }
}
