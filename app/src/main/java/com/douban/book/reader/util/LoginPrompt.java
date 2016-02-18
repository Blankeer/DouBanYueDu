package com.douban.book.reader.util;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.ToastBuilder.OnCloseListener;

public class LoginPrompt {
    private static boolean sPromptShown;

    /* renamed from: com.douban.book.reader.util.LoginPrompt.2 */
    static class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ Activity val$activity;

        AnonymousClass2(Activity activity) {
            this.val$activity = activity;
        }

        public void onClick(View v) {
            LoginFragment_.builder().build().showAsActivity(PageOpenHelper.from(this.val$activity));
        }
    }

    static {
        sPromptShown = false;
    }

    public static void reset() {
        sPromptShown = false;
    }

    public static void showIfNeeded(Activity activity) {
        if (!sPromptShown && UserManager.getInstance().isAnonymousUser()) {
            new ToastBuilder().message((int) R.string.toast_login_recommended).attachTo(activity).autoClose(false).click(new AnonymousClass2(activity)).onClose(new OnCloseListener() {
                public void onClose() {
                    LoginPrompt.sPromptShown = true;
                }
            }).show();
        }
    }
}
