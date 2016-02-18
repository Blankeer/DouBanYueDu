package com.douban.book.reader.view;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.UserInfo;
import com.douban.book.reader.entity.WorksAgent;
import com.douban.book.reader.event.UnreadCountChangedEvent;
import com.douban.book.reader.event.UserAgentUpdatedEvent;
import com.douban.book.reader.event.UserInfoUpdatedEvent;
import com.douban.book.reader.fragment.AccountBalanceFragment_;
import com.douban.book.reader.fragment.FeedFragment_;
import com.douban.book.reader.fragment.GiftFragment_;
import com.douban.book.reader.fragment.LoginFragment_;
import com.douban.book.reader.fragment.NotificationListFragment_;
import com.douban.book.reader.fragment.UserOwnListFragment_;
import com.douban.book.reader.fragment.UserProfileFragment_;
import com.douban.book.reader.fragment.WorksAgentFragment_;
import com.douban.book.reader.lib.view.BadgeTextView;
import com.douban.book.reader.manager.FeedManager;
import com.douban.book.reader.manager.NotificationManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksAgentManager;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.span.ThemedForegroundColorSpan;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.SpanUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903199)
public class UserSummaryView extends LinearLayout {
    private static final String TAG;
    @ViewById(2131558960)
    View mAuthorBlock;
    @ViewById(2131558954)
    TextView mBtnAccount;
    @ViewById(2131558955)
    TextView mBtnGift;
    @ViewById(2131558608)
    TextView mBtnLogin;
    @ViewById(2131558953)
    BadgeTextView mBtnNotification;
    @ViewById(2131558962)
    TextView mComposedNumber;
    @Bean
    FeedManager mFeedManager;
    @ViewById(2131558957)
    TextView mFeedsNumber;
    @Bean
    NotificationManager mNotificationManager;
    @ViewById(2131558959)
    TextView mOwnNumber;
    @ViewById(2131558535)
    UserAvatarView mUserAvatar;
    @Bean
    UserManager mUserManager;
    @ViewById(2131558659)
    TextView mUserName;
    @Bean
    WorksAgentManager mWorksAgentManager;

    static {
        TAG = UserSummaryView.class.getSimpleName();
    }

    public UserSummaryView(Context context) {
        super(context);
    }

    public UserSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserSummaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        setOrientation(1);
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.page_highlight_bg_color)).updateView();
        refreshUserInfo();
        ViewUtils.setEventAware(this);
        this.mBtnNotification.setBadgeCenterXY((float) Utils.dp2pixel(28.0f), (float) Utils.dp2pixel(13.0f));
        this.mBtnAccount.setText(new RichText().appendIcon(new IconFontSpan(R.drawable.v_wallet).ratio(2.0f).paddingRight(0.25f)).appendWithSpans((int) R.string.btn_account, new ThemedForegroundColorSpan(R.array.secondary_text_color)));
        this.mBtnGift.setText(new RichText().appendIcon(new IconFontSpan(R.drawable.v_gift).ratio(2.0f).paddingRight(0.25f)).appendWithSpans((int) R.string.btn_gift, new ThemedForegroundColorSpan(R.array.secondary_text_color)));
    }

    @Click({2131558535, 2131558659, 2131558608})
    void onUserNameClicked() {
        if (this.mUserManager.isAnonymousUser()) {
            LoginFragment_.builder().build().showAsActivity((View) this);
        } else {
            UserProfileFragment_.builder().build().showAsActivity((View) this);
        }
    }

    @Click({2131558956})
    void onBtnFeedsClicked() {
        FeedFragment_.builder().build().showAsActivity((View) this);
    }

    @Click({2131558953})
    void onBtnNotificationClicked() {
        NotificationListFragment_.builder().build().showAsActivity((View) this);
    }

    @Click({2131558955})
    void onBtnGiftClicked() {
        GiftFragment_.builder().build().showAsActivity((View) this);
    }

    @Click({2131558961})
    void onBtnComposedClicked() {
        WorksAgentFragment_.builder().agentId(this.mUserManager.getUserInfo().agentId).build().showAsActivity((View) this);
    }

    @Click({2131558958})
    void onBtnOwn() {
        UserOwnListFragment_.builder().build().showAsActivity((View) this);
    }

    @Click({2131558954})
    void onBtnAmountLeftClicked() {
        AccountBalanceFragment_.builder().build().showAsActivity((View) this);
    }

    public void onEventMainThread(UserInfoUpdatedEvent event) {
        refreshUserInfo();
    }

    public void onEventMainThread(UnreadCountChangedEvent event) {
        reloadFeedsNumber();
        refreshNotificationStatus();
    }

    public void onEventMainThread(UserAgentUpdatedEvent event) {
        refreshUserAgent();
    }

    @Background
    void refreshUserAgent() {
        UserInfo userInfo = this.mUserManager.getUserInfo();
        if (userInfo == null || !userInfo.isAuthor()) {
            ViewUtils.gone(this.mAuthorBlock);
            return;
        }
        try {
            refreshUserAgentView((WorksAgent) this.mWorksAgentManager.get((Object) Integer.valueOf(userInfo.agentId)));
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void refreshUserAgentView(WorksAgent worksAgent) {
        boolean z = worksAgent != null && worksAgent.worksCount > 0;
        ViewUtils.showIf(z, this.mAuthorBlock);
        if (worksAgent != null) {
            this.mComposedNumber.setText(new RichText().append(StringUtils.toStr(Integer.valueOf(worksAgent.worksCount))).append((char) Char.SPACE).append((int) R.string.works));
        }
    }

    private void refreshUserInfo() {
        UserInfo userInfo = this.mUserManager.getUserInfo();
        this.mUserAvatar.displayUserAvatar(userInfo);
        if (userInfo == null) {
            this.mBtnLogin.setVisibility(0);
            this.mUserName.setVisibility(8);
            ViewUtils.gone(this.mAuthorBlock);
            this.mOwnNumber.setText(String.valueOf(0));
            return;
        }
        if (userInfo.type == 1) {
            this.mUserName.setText(userInfo.name);
            this.mBtnLogin.setVisibility(8);
            this.mUserName.setVisibility(0);
        } else {
            this.mBtnLogin.setVisibility(0);
            this.mUserName.setVisibility(8);
        }
        refreshUserAgent();
        reloadFeedsNumber();
        refreshNotificationStatus();
        this.mOwnNumber.setText(new RichText().append(StringUtils.toStr(Integer.valueOf(userInfo.purchasedCount))).append((char) Char.SPACE).append((int) R.string.works));
    }

    @Background
    void refreshNotificationStatus() {
        try {
            updateNotificationView(this.mNotificationManager.getUnreadCount() > 0);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    @Background
    void reloadFeedsNumber() {
        try {
            updateUnReadView(this.mFeedsNumber, this.mFeedManager.getUnreadCount());
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void updateNotificationView(boolean hasUnread) {
        this.mBtnNotification.setBadgeVisible(hasUnread);
    }

    @UiThread
    void updateUnReadView(TextView textView, int num) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        CharSequence valueOf = String.valueOf(num);
        Object[] objArr = new Object[2];
        objArr[0] = new StyleSpan(1);
        objArr[1] = new ThemedForegroundColorSpan(num <= 0 ? R.array.secondary_text_color : R.array.red);
        textView.setText(spannableStringBuilder.append(SpanUtils.applySpan(valueOf, objArr)).append(" ").append(Res.getString(R.string.text_unread)));
    }
}
