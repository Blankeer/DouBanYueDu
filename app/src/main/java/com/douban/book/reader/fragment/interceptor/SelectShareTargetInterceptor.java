package com.douban.book.reader.fragment.interceptor;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.GeneralFragmentActivity;
import com.douban.book.reader.activity.WeiboAuthActivity;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.manager.UserManager;
import java.util.ArrayList;
import java.util.List;

public class SelectShareTargetInterceptor implements Interceptor {
    private static final ShareTo[] SHARE_TARGETS;

    /* renamed from: com.douban.book.reader.fragment.interceptor.SelectShareTargetInterceptor.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ PageOpenHelper val$helper;
        final /* synthetic */ Intent val$intent;

        AnonymousClass1(Intent intent, PageOpenHelper pageOpenHelper) {
            this.val$intent = intent;
            this.val$helper = pageOpenHelper;
        }

        public void onClick(DialogInterface dialog, int which) {
            ShareTo shareTo = SelectShareTargetInterceptor.SHARE_TARGETS[which];
            if (this.val$intent != null) {
                GeneralFragmentActivity.putAdditionalArgs(this.val$intent, BaseShareEditFragment.KEY_SHARE_TO, shareTo);
                if (shareTo == ShareTo.WEIBO && !WeiboAuthActivity.isAuthenticated()) {
                    new Builder().setMessage((int) R.string.dialog_message_bind_weibo_confirm).setPositiveButton((int) R.string.dialog_button_bind, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            WeiboAuthActivity.startAuth(AnonymousClass1.this.val$helper, AnonymousClass1.this.val$intent);
                        }
                    }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
                } else if (shareTo == ShareTo.DOUBAN && UserManager.getInstance().isAnonymousUser()) {
                    LoginFragment_.builder().intentToStartAfterLogin(this.val$intent).build().showAsActivity(this.val$helper);
                } else {
                    if (shareTo == ShareTo.OTHER_APPS || shareTo == ShareTo.WEIXIN || shareTo == ShareTo.MOMENTS) {
                        this.val$intent.putExtra(GeneralFragmentActivity.KEY_SHOW_ACTION_BAR, false);
                    }
                    this.val$helper.open(this.val$intent);
                }
            }
        }
    }

    static {
        SHARE_TARGETS = new ShareTo[]{ShareTo.DOUBAN, ShareTo.WEIXIN, ShareTo.MOMENTS, ShareTo.WEIBO, ShareTo.OTHER_APPS};
    }

    private CharSequence[] getDisplayTitleList() {
        List<CharSequence> result = new ArrayList();
        for (ShareTo shareTo : SHARE_TARGETS) {
            result.add(shareTo.getDisplayText());
        }
        return (CharSequence[]) result.toArray(new CharSequence[0]);
    }

    public void performShowAsActivity(PageOpenHelper helper, Intent intent) {
        new Builder().setTitle((int) R.string.share_to).setItems(getDisplayTitleList(), new AnonymousClass1(intent, helper)).setCanceledOnTouchOutside(true).setCancelable(true).create().show(helper);
    }
}
