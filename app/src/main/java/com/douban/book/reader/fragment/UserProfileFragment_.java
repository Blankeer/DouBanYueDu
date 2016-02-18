package com.douban.book.reader.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.AccountManager_;
import com.douban.book.reader.manager.UserManager_;
import com.douban.book.reader.view.UserAvatarView;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class UserProfileFragment_ extends UserProfileFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.UserProfileFragment_.5 */
    class AnonymousClass5 extends Task {
        final /* synthetic */ Bitmap val$bitmap;

        AnonymousClass5(String x0, long x1, String x2, Bitmap bitmap) {
            this.val$bitmap = bitmap;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.uploadAvatar(this.val$bitmap);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, UserProfileFragment> {
        public UserProfileFragment build() {
            UserProfileFragment_ fragment_ = new UserProfileFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public UserProfileFragment_() {
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
            this.contentView_ = inflater.inflate(R.layout.frag_user_profile, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mUserAvatar = null;
        this.mUserName = null;
        this.mBtnEditAvatar = null;
        this.mBtnEditNickname = null;
        this.mTvAgreement = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
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
        this.mUserAvatar = (UserAvatarView) hasViews.findViewById(R.id.user_avatar);
        this.mUserName = (TextView) hasViews.findViewById(R.id.user_name);
        this.mBtnEditAvatar = (TextView) hasViews.findViewById(R.id.btn_edit_avatar);
        this.mBtnEditNickname = (TextView) hasViews.findViewById(R.id.btn_edit_nickname);
        this.mTvAgreement = (TextView) hasViews.findViewById(R.id.text_agreement);
        View view_btn_logout = hasViews.findViewById(R.id.btn_logout);
        if (this.mBtnEditAvatar != null) {
            this.mBtnEditAvatar.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserProfileFragment_.this.onBtnEditAvatarClicked(view);
                }
            });
        }
        if (this.mBtnEditNickname != null) {
            this.mBtnEditNickname.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserProfileFragment_.this.onBtnEditNicknameClicked();
                }
            });
        }
        if (view_btn_logout != null) {
            view_btn_logout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserProfileFragment_.this.onBtnLogoutClicked();
                }
            });
        }
        init();
    }

    void updateUserInfo() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateUserInfo();
            }
        }, 0);
    }

    void uploadAvatar(Bitmap bitmap) {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, bitmap));
    }
}
