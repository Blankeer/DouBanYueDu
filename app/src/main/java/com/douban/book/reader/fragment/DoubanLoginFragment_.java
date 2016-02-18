package com.douban.book.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.douban.book.reader.R;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.helper.LoginHelper_;
import com.douban.book.reader.manager.SessionManager_;
import com.douban.book.reader.manager.ShelfManager_;
import com.douban.book.reader.manager.UserManager_;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class DoubanLoginFragment_ extends DoubanLoginFragment implements HasViews, OnViewChangedListener {
    public static final String INTENT_TO_START_AFTER_LOGIN_ARG = "intentToStartAfterLogin";
    public static final String REQUEST_TO_SEND_AFTER_LOGIN_ARG = "requestToSendAfterLogin";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, DoubanLoginFragment> {
        public DoubanLoginFragment build() {
            DoubanLoginFragment_ fragment_ = new DoubanLoginFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ intentToStartAfterLogin(Intent intentToStartAfterLogin) {
            this.args.putParcelable(DoubanLoginFragment_.INTENT_TO_START_AFTER_LOGIN_ARG, intentToStartAfterLogin);
            return this;
        }

        public FragmentBuilder_ requestToSendAfterLogin(ArkRequest requestToSendAfterLogin) {
            this.args.putSerializable(DoubanLoginFragment_.REQUEST_TO_SEND_AFTER_LOGIN_ARG, requestToSendAfterLogin);
            return this;
        }
    }

    public DoubanLoginFragment_() {
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
            this.contentView_ = inflater.inflate(R.layout.frag_login_douban, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mEditUsername = null;
        this.mEditPassword = null;
        this.mBtnLogin = null;
        this.mResetPassword = null;
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
        this.mEditUsername = (EditText) hasViews.findViewById(R.id.username);
        this.mEditPassword = (EditText) hasViews.findViewById(R.id.password);
        this.mBtnLogin = (Button) hasViews.findViewById(R.id.btn_login);
        this.mResetPassword = (TextView) hasViews.findViewById(R.id.reset_password);
        if (this.mBtnLogin != null) {
            this.mBtnLogin.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    DoubanLoginFragment_.this.onBtnLoginClicked();
                }
            });
        }
        if (this.mResetPassword != null) {
            this.mResetPassword.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    DoubanLoginFragment_.this.onResetPasswordClicked();
                }
            });
        }
        if (this.mEditPassword != null) {
            this.mEditPassword.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                    DoubanLoginFragment_.this.onBtnLoginClicked();
                    return true;
                }
            });
        }
        TextView view = (TextView) hasViews.findViewById(R.id.username);
        if (view != null) {
            view.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    DoubanLoginFragment_.this.onEditTextChange();
                }

                public void afterTextChanged(Editable s) {
                }
            });
        }
        view = (TextView) hasViews.findViewById(R.id.password);
        if (view != null) {
            view.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    DoubanLoginFragment_.this.onEditTextChange();
                }

                public void afterTextChanged(Editable s) {
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
}
