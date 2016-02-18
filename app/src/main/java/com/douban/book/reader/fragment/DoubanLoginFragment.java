package com.douban.book.reader.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment.Action;
import com.douban.book.reader.helper.LoginHelper;
import com.douban.book.reader.manager.SessionManager;
import com.douban.book.reader.manager.ShelfManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

@EFragment(2130903103)
public class DoubanLoginFragment extends BaseFragment {
    @FragmentArg
    Intent intentToStartAfterLogin;
    @ViewById(2131558608)
    Button mBtnLogin;
    @ViewById(2131558616)
    EditText mEditPassword;
    @ViewById(2131558615)
    EditText mEditUsername;
    @Bean
    LoginHelper mLoginHelper;
    @ViewById(2131558617)
    TextView mResetPassword;
    @Bean
    SessionManager mSessionManager;
    @Bean
    ShelfManager mShelfManager;
    @Bean
    UserManager mUserManager;
    @FragmentArg
    ArkRequest requestToSendAfterLogin;

    public DoubanLoginFragment() {
        setDrawerEnabled(false);
    }

    @AfterViews
    void init() {
        setTitle((int) R.string.title_douban_login);
        if (StringUtils.isNotEmpty(Pref.ofApp().getString(Key.APP_USER_NAME))) {
            this.mEditUsername.setText(Pref.ofApp().getString(Key.APP_USER_NAME));
            this.mEditPassword.requestFocus();
        }
        this.mResetPassword.setText(RichText.linkify(R.string.text_forget_password));
        this.mLoginHelper.init(this, this.intentToStartAfterLogin, this.requestToSendAfterLogin);
    }

    @TextChange({2131558615, 2131558616})
    void onEditTextChange() {
        ViewUtils.enableIf(StringUtils.isNotEmpty(this.mEditUsername.getText(), this.mEditPassword.getText()), this.mBtnLogin);
    }

    @Click({2131558608})
    @EditorAction({2131558616})
    void onBtnLoginClicked() {
        loginWithPassword();
    }

    @Click({2131558617})
    void onResetPasswordClicked() {
        if (StringUtils.isNotEmpty(this.mEditUsername.getText())) {
            Pref.ofApp().set(Key.APP_USER_NAME, this.mEditUsername.getText());
        }
        DoubanAccountOperationFragment_.builder().action(Action.RESET_PASSWORD).intentToStartAfterLogin(this.intentToStartAfterLogin).requestToSendAfterLogin(this.requestToSendAfterLogin).build().setDrawerEnabled(false).showAsActivity((Fragment) this);
    }

    public void onEventMainThread(ArkEvent event) {
        if (event == ArkEvent.LOGIN_COMPLETED) {
            finish();
        }
    }

    private void loginWithPassword() {
        String username = this.mEditUsername.getText().toString();
        String password = this.mEditPassword.getText().toString();
        Pref.ofApp().set(Key.APP_USER_NAME, username);
        this.mLoginHelper.loginWithPassword(username, password);
    }
}
