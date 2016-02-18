package com.douban.book.reader.util;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;
import com.douban.book.reader.R;

public class ToastUtils {
    public static void showToast(CharSequence message) {
        new ToastBuilder().message(message).show();
    }

    public static void showToast(Throwable e) {
        showToast(e, (int) R.string.general_load_failed);
    }

    public static void showToast(Throwable e, @StringRes int defaultStrResId) {
        showToast(ExceptionUtils.getHumanReadableMessage(e, defaultStrResId));
    }

    public static void showToast(Fragment fragment, CharSequence message) {
        new ToastBuilder().message(message).attachTo(fragment.getActivity()).show();
    }

    public static void showToast(View view, CharSequence message) {
        new ToastBuilder().message(message).attachTo(ViewUtils.getAttachedActivity(view)).show();
    }

    public static void showToast(int resId) {
        showToast(Res.getString(resId));
    }

    public static void showToast(Fragment fragment, int resId) {
        showToast(fragment, Res.getString(resId));
    }
}
