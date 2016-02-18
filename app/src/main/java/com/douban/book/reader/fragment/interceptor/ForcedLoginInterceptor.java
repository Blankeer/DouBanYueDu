package com.douban.book.reader.fragment.interceptor;

import android.content.Intent;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.manager.UserManager;

public class ForcedLoginInterceptor implements Interceptor {
    public void performShowAsActivity(PageOpenHelper helper, Intent intent) {
        if (UserManager.getInstance().isAnonymousUser()) {
            LoginFragment_.builder().intentToStartAfterLogin(intent).build().showAsActivity(helper);
        } else {
            helper.open(intent);
        }
    }
}
