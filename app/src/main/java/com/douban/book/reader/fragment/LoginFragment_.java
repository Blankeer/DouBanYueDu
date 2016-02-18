package com.douban.book.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.helper.LoginHelper_;
import com.douban.book.reader.manager.SessionManager_;
import com.douban.book.reader.manager.ShelfManager_;
import com.douban.book.reader.manager.UserManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class LoginFragment_ extends LoginFragment implements HasViews, OnViewChangedListener {
    public static final String INTENT_TO_START_AFTER_LOGIN_ARG = "intentToStartAfterLogin";
    public static final String REQUEST_TO_SEND_AFTER_LOGIN_ARG = "requestToSendAfterLogin";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.LoginFragment_.8 */
    class AnonymousClass8 extends Task {
        AnonymousClass8(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.anonymousLogin();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, LoginFragment> {
        public LoginFragment build() {
            LoginFragment_ fragment_ = new LoginFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ intentToStartAfterLogin(Intent intentToStartAfterLogin) {
            this.args.putParcelable(LoginFragment_.INTENT_TO_START_AFTER_LOGIN_ARG, intentToStartAfterLogin);
            return this;
        }

        public FragmentBuilder_ requestToSendAfterLogin(ArkRequest requestToSendAfterLogin) {
            this.args.putSerializable(LoginFragment_.REQUEST_TO_SEND_AFTER_LOGIN_ARG, requestToSendAfterLogin);
            return this;
        }
    }

    public LoginFragment_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public View findViewById(int id) {
        if (this.contentView_ == null) {
            return null;
        }
        return this.contentView_.findViewById(id);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        if (this.contentView_ == null) {
            this.contentView_ = inflater.inflate(R.layout.frag_login, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mBtnRegister = null;
        this.mViewLoginWithWeixin = null;
        this.mViewLoginWithWeibo = null;
        this.mViewLoginWithQQ = null;
        this.mTvTry = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectFragmentArguments_();
        this.mShelfManager = ShelfManager_.getInstance_(getActivity());
        this.mUserManager = UserManager_.getInstance_(getActivity());
        this.mSessionManager = SessionManager_.getInstance_(getActivity());
        this.mLoginHelper = LoginHelper_.getInstance_(getActivity());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBtnRegister = (Button) hasViews.findViewById(R.id.btn_register);
        this.mViewLoginWithWeixin = (ImageView) hasViews.findViewById(R.id.login_with_weixin);
        this.mViewLoginWithWeibo = (ImageView) hasViews.findViewById(R.id.login_with_weibo);
        this.mViewLoginWithQQ = (ImageView) hasViews.findViewById(R.id.login_with_qq);
        this.mTvTry = (TextView) hasViews.findViewById(R.id.text_try);
        View view_btn_login = hasViews.findViewById(R.id.btn_login);
        View view_douban_logo = hasViews.findViewById(R.id.douban_logo);
        if (view_btn_login != null) {
            view_btn_login.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoginFragment_.this.onBtnLoginClicked();
                }
            });
        }
        if (this.mBtnRegister != null) {
            this.mBtnRegister.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoginFragment_.this.onRegisterClicked();
                }
            });
        }
        if (this.mViewLoginWithWeixin != null) {
            this.mViewLoginWithWeixin.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoginFragment_.this.onLoginWithWeixinClicked();
                }
            });
        }
        if (this.mViewLoginWithWeibo != null) {
            this.mViewLoginWithWeibo.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoginFragment_.this.onLoginWithWeiboClicked();
                }
            });
        }
        if (this.mViewLoginWithQQ != null) {
            this.mViewLoginWithQQ.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoginFragment_.this.onLoginWithQQClicked();
                }
            });
        }
        if (this.mTvTry != null) {
            this.mTvTry.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoginFragment_.this.onTryClicked();
                }
            });
        }
        if (view_douban_logo != null) {
            view_douban_logo.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    LoginFragment_.this.onDoubanLogoClicked();
                    return true;
                }
            });
        }
        init();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null) {
            if (args_.containsKey(INTENT_TO_START_AFTER_LOGIN_ARG)) {
                this.intentToStartAfterLogin = (Intent) args_.getParcelable(INTENT_TO_START_AFTER_LOGIN_ARG);
            }
            if (args_.containsKey(REQUEST_TO_SEND_AFTER_LOGIN_ARG)) {
                this.requestToSendAfterLogin = (ArkRequest) args_.getSerializable(REQUEST_TO_SEND_AFTER_LOGIN_ARG);
            }
        }
    }

    void anonymousLogin() {
        BackgroundExecutor.execute(new AnonymousClass8(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
