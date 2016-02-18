package android.support.v7.graphics.drawable;

import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
import android.support.v4.media.TransportMediator;
import com.alipay.sdk.protocol.h;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dj;
import u.aly.dx;

public class DrawableUtils {
    public static Mode parseTintMode(int value, Mode defaultMode) {
        switch (value) {
            case dx.d /*3*/:
                return Mode.SRC_OVER;
            case dj.f /*5*/:
                return Mode.SRC_IN;
            case h.h /*9*/:
                return Mode.SRC_ATOP;
            case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                return Mode.MULTIPLY;
            case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                return Mode.SCREEN;
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                if (VERSION.SDK_INT >= 11) {
                    return Mode.valueOf("ADD");
                }
                return defaultMode;
            default:
                return defaultMode;
        }
    }
}
