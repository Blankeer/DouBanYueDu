package com.douban.book.reader.util;

import android.os.Build.VERSION;
import android.support.annotation.StringRes;
import com.douban.book.reader.exception.HumanReadable;

public class ExceptionUtils {
    public static boolean isCausedBy(Throwable throwable, Class<? extends Throwable> cause) {
        Object throwable2;
        while (throwable2 != null) {
            if (ReflectionUtils.isInstanceOf(throwable2, (Class) cause)) {
                return true;
            }
            throwable2 = throwable2.getCause();
        }
        return false;
    }

    public static <T extends Throwable> T getCauseByType(Throwable throwable, Class<T> cause) {
        Object throwable2;
        while (throwable2 != null) {
            if (ReflectionUtils.isInstanceOf(throwable2, (Class) cause)) {
                return throwable2;
            }
            throwable2 = throwable2.getCause();
        }
        return null;
    }

    public static CharSequence getHumanReadableMessage(Throwable throwable) {
        while (throwable != null) {
            if (throwable instanceof HumanReadable) {
                if (StringUtils.isNotEmpty(((HumanReadable) throwable).getHumanReadableMessage())) {
                    return ((HumanReadable) throwable).getHumanReadableMessage();
                }
            }
            throwable = throwable.getCause();
        }
        return null;
    }

    public static CharSequence getHumanReadableMessage(Throwable throwable, CharSequence defaultMessage) {
        CharSequence msg = getHumanReadableMessage(throwable);
        if (StringUtils.isEmpty(msg)) {
            return defaultMessage;
        }
        return msg;
    }

    public static CharSequence getHumanReadableMessage(Throwable throwable, @StringRes int defaultMsgId) {
        return getHumanReadableMessage(throwable, Res.getString(defaultMsgId));
    }

    public static void addSuppressed(Throwable addTo, Throwable toBeAdded) {
        if (VERSION.SDK_INT >= 19 && addTo != null && toBeAdded != null) {
            addTo.addSuppressed(toBeAdded);
        }
    }
}
