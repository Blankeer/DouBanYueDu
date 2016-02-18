package com.douban.book.reader.fragment;

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
import com.douban.book.reader.fragment.UserProfileEditFragment.Action;
import com.douban.book.reader.manager.AccountManager_;
import com.douban.book.reader.manager.UserManager_;
import com.douban.book.reader.view.UserAvatarView;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class UserProfileEditFragment_ extends UserProfileEditFragment implements HasViews, OnViewChangedListener {
    public static final String ACTION_ARG = "action";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.UserProfileEditFragment_.5 */
    class AnonymousClass5 extends Task {
        AnonymousClass5(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.setNickname();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, UserProfileEditFragment> {
        public UserProfileEditFragment build() {
            UserProfileEditFragment_ fragment_ = new UserProfileEditFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ action(Action action) {
            this.args.putSerializable(UserProfileEditFragment_.ACTION_ARG, action);
            return this;
        }
    }

    public UserProfileEditFragment_() {
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
            this.contentView_ = inflater.inflate(R.layout.frag_user_profile_edit, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mLayoutAvatar = null;
        this.mUserAvatar = null;
        this.mEdtNickname = null;
        this.mBtnConfirm = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectFragmentArguments_();
        this.mUserManager = UserManager_.getInstance_(getActivity());
        this.mAccountManager = AccountManager_.getInstance_(getActivity());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mLayoutAvatar = hasViews.findViewById(R.id.layout_avatar);
        this.mUserAvatar = (UserAvatarView) hasViews.findViewById(R.id.user_avatar);
        this.mEdtNickname = (EditText) hasViews.findViewById(R.id.edt_nickname);
        this.mBtnConfirm = (Button) hasViews.findViewById(R.id.btn_confirm);
        if (this.mUserAvatar != null) {
            this.mUserAvatar.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserProfileEditFragment_.this.onEditAvatarClicked(view);
                }
            });
        }
        if (this.mBtnConfirm != null) {
            this.mBtnConfirm.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserProfileEditFragment_.this.onBtnConfirmClicked();
                }
            });
        }
        if (this.mEdtNickname != null) {
            this.mEdtNickname.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                    UserProfileEditFragment_.this.onBtnConfirmClicked();
                    return true;
                }
            });
        }
        TextView view = (TextView) hasViews.findViewById(R.id.edt_nickname);
        if (view != null) {
            view.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    UserProfileEditFragment_.this.onEditTextChange();
                }

                public void afterTextChanged(Editable s) {
                }
            });
        }
        init();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null && args_.containsKey(ACTION_ARG)) {
            this.action = (Action) args_.getSerializable(ACTION_ARG);
        }
    }

    void setNickname() {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
