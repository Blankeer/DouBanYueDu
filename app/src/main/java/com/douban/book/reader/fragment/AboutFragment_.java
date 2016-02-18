package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.UserManager_;
import com.douban.book.reader.manager.VersionManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class AboutFragment_ extends AboutFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.AboutFragment_.6 */
    class AnonymousClass6 extends Task {
        AnonymousClass6(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.onBtnCheckUpdateClicked();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, AboutFragment> {
        public AboutFragment build() {
            AboutFragment_ fragment_ = new AboutFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public AboutFragment_() {
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
            this.contentView_ = inflater.inflate(R.layout.frag_about, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mTextVersion = null;
        this.mTextUid = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mVersionManager = VersionManager_.getInstance_(getActivity());
        this.mUserManager = UserManager_.getInstance_(getActivity());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mTextVersion = (TextView) hasViews.findViewById(R.id.text_version);
        this.mTextUid = (TextView) hasViews.findViewById(R.id.text_uid);
        View view_button_check_update = hasViews.findViewById(R.id.button_check_update);
        View view_button_changelog = hasViews.findViewById(R.id.button_changelog);
        View view_copyright = hasViews.findViewById(R.id.copyright);
        if (this.mTextVersion != null) {
            this.mTextVersion.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AboutFragment_.this.onVersionClicked();
                }
            });
        }
        if (this.mTextUid != null) {
            this.mTextUid.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AboutFragment_.this.onUidClicked();
                }
            });
        }
        if (view_button_check_update != null) {
            view_button_check_update.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AboutFragment_.this.onBtnCheckUpdateClicked();
                }
            });
        }
        if (view_button_changelog != null) {
            view_button_changelog.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AboutFragment_.this.onBtnChangeLogClicked();
                }
            });
        }
        if (view_copyright != null) {
            view_copyright.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AboutFragment_.this.onCopyrightClicked();
                }
            });
        }
        init();
    }

    void onBtnCheckUpdateClicked() {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
