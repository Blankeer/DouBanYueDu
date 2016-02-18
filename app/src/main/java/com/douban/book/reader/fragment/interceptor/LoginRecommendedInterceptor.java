package com.douban.book.reader.fragment.interceptor;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.Res;

public class LoginRecommendedInterceptor implements Interceptor {
    private String mActionName;

    /* renamed from: com.douban.book.reader.fragment.interceptor.LoginRecommendedInterceptor.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ PageOpenHelper val$helper;
        final /* synthetic */ Intent val$intent;

        AnonymousClass1(PageOpenHelper pageOpenHelper, Intent intent) {
            this.val$helper = pageOpenHelper;
            this.val$intent = intent;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$helper.open(this.val$intent);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.interceptor.LoginRecommendedInterceptor.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ PageOpenHelper val$helper;
        final /* synthetic */ Intent val$intent;

        AnonymousClass2(Intent intent, PageOpenHelper pageOpenHelper) {
            this.val$intent = intent;
            this.val$helper = pageOpenHelper;
        }

        public void onClick(DialogInterface dialog, int which) {
            LoginFragment_.builder().intentToStartAfterLogin(this.val$intent).build().showAsActivity(this.val$helper);
        }
    }

    public LoginRecommendedInterceptor(String actionName) {
        this.mActionName = actionName;
    }

    public void performShowAsActivity(PageOpenHelper helper, Intent intent) {
        if (UserManager.getInstance().isAnonymousUser()) {
            new Builder().setMessage(Res.getString(R.string.dialog_message_login_suggested, this.mActionName)).setPositiveButton((int) R.string.dialog_button_login, new AnonymousClass2(intent, helper)).setNegativeButton(Res.getString(R.string.dialog_button_skip_login, this.mActionName), new AnonymousClass1(helper, intent)).create().show();
            return;
        }
        helper.open(intent);
    }
}
