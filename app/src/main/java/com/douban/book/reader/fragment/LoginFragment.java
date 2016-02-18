package com.douban.book.reader.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.QQAuthActivity;
import com.douban.book.reader.activity.WeiboAuthActivity;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.NewUserRegisteredEvent;
import com.douban.book.reader.event.OpenIdAuthenticatedEvent;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment.Action;
import com.douban.book.reader.helper.LoginHelper;
import com.douban.book.reader.manager.SessionManager;
import com.douban.book.reader.manager.ShelfManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.ClipboardUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.PackageUtils;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.util.WXUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI.WXApp;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903102)
public class LoginFragment extends BaseFragment {
    @FragmentArg
    Intent intentToStartAfterLogin;
    @ViewById(2131558609)
    Button mBtnRegister;
    @Bean
    LoginHelper mLoginHelper;
    @Bean
    SessionManager mSessionManager;
    @Bean
    ShelfManager mShelfManager;
    @ViewById(2131558614)
    TextView mTvTry;
    @Bean
    UserManager mUserManager;
    @ViewById(2131558613)
    ImageView mViewLoginWithQQ;
    @ViewById(2131558612)
    ImageView mViewLoginWithWeibo;
    @ViewById(2131558611)
    ImageView mViewLoginWithWeixin;
    @FragmentArg
    ArkRequest requestToSendAfterLogin;

    public LoginFragment() {
        setDrawerEnabled(false);
        if (!UserManager.getInstance().hasAccessToken()) {
            setActionBarVisible(false);
        }
    }

    @AfterViews
    void init() {
        boolean z = true;
        setTitle((int) R.string.title_login);
        ViewUtils.showIf(DebugSwitch.on(Key.APP_DEBUG_ENABLE_QQ_LOGIN_AND_BIND), this.mViewLoginWithQQ);
        this.mBtnRegister.setAlpha(0.7f);
        this.mViewLoginWithWeixin.setImageDrawable(Res.getDrawable(R.drawable.v_weixin));
        this.mViewLoginWithWeibo.setImageDrawable(Res.getDrawable(R.drawable.v_weibo));
        if (this.mUserManager.hasAccessToken()) {
            z = false;
        }
        ViewUtils.showTextIf(z, this.mTvTry, RichText.buildUpon((int) R.string.button_try).appendIcon((int) R.drawable.v_arrow_right));
        this.mLoginHelper.init(this, this.intentToStartAfterLogin, this.requestToSendAfterLogin);
    }

    @LongClick({2131558610})
    void onDoubanLogoClicked() {
        RichText textToCopy = new RichText().append(Utils.getDeviceUDID());
        if (this.mUserManager.hasAccessToken()) {
            textToCopy.append((CharSequence) "/").append(String.valueOf(this.mUserManager.getUserId()));
        }
        ClipboardUtils.copy(textToCopy);
        ToastUtils.showToast((Fragment) this, (int) R.string.toast_udid_copied);
    }

    @Click({2131558608})
    void onBtnLoginClicked() {
        DoubanLoginFragment_.builder().intentToStartAfterLogin(this.intentToStartAfterLogin).requestToSendAfterLogin(this.requestToSendAfterLogin).build().showAsActivity((Fragment) this);
    }

    @Click({2131558609})
    void onRegisterClicked() {
        DoubanAccountOperationFragment_.builder().action(Action.REGISTER).intentToStartAfterLogin(this.intentToStartAfterLogin).requestToSendAfterLogin(this.requestToSendAfterLogin).build().showAsActivity((Fragment) this);
    }

    @Click({2131558611})
    void onLoginWithWeixinClicked() {
        if (PackageUtils.isInstalled(WXApp.WXAPP_PACKAGE_NAME)) {
            WXUtils.sendLoginRequest();
        } else {
            ToastUtils.showToast(Res.getString(R.string.toast_error_weixin_not_installed));
        }
    }

    @Click({2131558612})
    void onLoginWithWeiboClicked() {
        WeiboAuthActivity.startAuth(PageOpenHelper.from((Fragment) this));
    }

    @Click({2131558613})
    void onLoginWithQQClicked() {
        QQAuthActivity.startAuth(PageOpenHelper.from((Fragment) this));
    }

    @Click({2131558614})
    void onTryClicked() {
        anonymousLogin();
    }

    @Background
    void anonymousLogin() {
        this.mLoginHelper.loginWithDevice();
    }

    public void onEventMainThread(NewUserRegisteredEvent event) {
        if (StringUtils.isNotEmpty(event.userName)) {
            Pref.ofApp().set(Key.APP_USER_NAME, event.userName);
        }
        this.mLoginHelper.loginWithSession(event.session);
    }

    public void onEventMainThread(OpenIdAuthenticatedEvent event) {
        this.mLoginHelper.loginWithOpenId(event.openIdType, event.openId, event.openIdAccessToken);
    }

    public void onEventMainThread(ArkEvent event) {
        if (event == ArkEvent.LOGIN_COMPLETED) {
            finish();
        }
    }
}
