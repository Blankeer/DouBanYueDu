package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.SessionManager_;
import com.douban.book.reader.manager.UserManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class DebugSwitchFragment_ extends DebugSwitchFragment implements HasViews {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.DebugSwitchFragment_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Preference val$preference;

        AnonymousClass1(Preference preference) {
            this.val$preference = preference;
        }

        public void run() {
            super.updateUserInfoPref(this.val$preference);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.DebugSwitchFragment_.2 */
    class AnonymousClass2 extends Task {
        final /* synthetic */ Preference val$userInfoPref;

        AnonymousClass2(String x0, long x1, String x2, Preference preference) {
            this.val$userInfoPref = preference;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.refreshLogin(this.val$userInfoPref);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, DebugSwitchFragment> {
        public DebugSwitchFragment build() {
            DebugSwitchFragment_ fragment_ = new DebugSwitchFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public DebugSwitchFragment_() {
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
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
    }

    private void init_(Bundle savedInstanceState) {
        this.mUserManager = UserManager_.getInstance_(getActivity());
        this.mSessionManager = SessionManager_.getInstance_(getActivity());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    void updateUserInfoPref(Preference preference) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(preference), 0);
    }

    void refreshLogin(Preference userInfoPref) {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, userInfoPref));
    }
}
