package com.douban.book.reader.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.WeiboAuthActivity;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.lib.view.IconCheckButton;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903188)
public class ShareTargetSelectorView extends LinearLayout {
    @ViewById(2131558937)
    IconCheckButton mShareToDouban;
    @ViewById(2131558938)
    IconCheckButton mShareToWeibo;
    @Bean
    UserManager mUserManager;

    public ShareTargetSelectorView(Context context) {
        super(context);
    }

    public ShareTargetSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareTargetSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    void init() {
        this.mShareToDouban.setButtonDrawable(Res.getDrawable(R.drawable.v_douban));
        this.mShareToWeibo.setButtonDrawable(Res.getDrawable(R.drawable.v_weibo));
        if (isLoggedInWithDoubanAccount()) {
            this.mShareToDouban.setChecked(Pref.ofApp().getBoolean(Key.APP_SHARE_TO_DOUBAN_SELECTED, true));
        }
        if (hasBoundedWeibo()) {
            this.mShareToWeibo.setChecked(Pref.ofApp().getBoolean(Key.APP_SHARE_TO_WEIBO_SELECTED, true));
        }
        ViewUtils.setEventAware(this);
    }

    public JsonRequestParam getRequestParam() {
        return (JsonRequestParam) ((JsonRequestParam) RequestParam.json().appendShareToIf(this.mShareToDouban.isChecked(), ShareTo.DOUBAN)).appendShareToIf(this.mShareToWeibo.isChecked(), ShareTo.WEIBO);
    }

    @Click({2131558937})
    void onShareToDoubanClicked() {
        if (!isLoggedInWithDoubanAccount()) {
            LoginFragment_.builder().build().showAsActivity((View) this);
            this.mShareToDouban.setChecked(false);
        }
    }

    @Click({2131558938})
    void onShareToWeiboClicked() {
        if (!hasBoundedWeibo()) {
            new Builder().setMessage((int) R.string.dialog_message_bind_weibo_confirm).setPositiveButton((int) R.string.dialog_button_bind, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    WeiboAuthActivity.startAuth(PageOpenHelper.from(ShareTargetSelectorView.this));
                }
            }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
            this.mShareToWeibo.setChecked(false);
        }
    }

    @CheckedChange({2131558937})
    void onShareToDoubanCheckedChange(boolean isChecked) {
        Pref.ofApp().set(Key.APP_SHARE_TO_DOUBAN_SELECTED, Boolean.valueOf(isChecked));
    }

    @CheckedChange({2131558938})
    void onShareToWeiboCheckedChange(boolean isChecked) {
        Pref.ofApp().set(Key.APP_SHARE_TO_WEIBO_SELECTED, Boolean.valueOf(isChecked));
    }

    public void onEventMainThread(ArkEvent event) {
        if (event == ArkEvent.WEIBO_AUTHENTICATED) {
            ToastUtils.showToast(Res.getString(R.string.toast_share_bind_sina_success));
            this.mShareToWeibo.setChecked(true);
        } else if (event == ArkEvent.LOGIN_COMPLETED && isLoggedInWithDoubanAccount()) {
            this.mShareToDouban.setChecked(true);
        }
    }

    private boolean isLoggedInWithDoubanAccount() {
        return this.mUserManager.isNormalUser();
    }

    private boolean hasBoundedWeibo() {
        return WeiboAuthActivity.isAuthenticated();
    }
}
