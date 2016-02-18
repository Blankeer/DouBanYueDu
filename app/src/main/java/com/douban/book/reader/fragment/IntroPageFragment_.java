package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.PageManager.Page;
import com.douban.book.reader.manager.PageManager_;
import com.douban.book.reader.manager.exception.DataLoadException;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class IntroPageFragment_ extends IntroPageFragment implements HasViews, OnViewChangedListener {
    public static final String PAGE_ARG = "page";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.IntroPageFragment_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ DataLoadException val$ex;

        AnonymousClass1(DataLoadException dataLoadException) {
            this.val$ex = dataLoadException;
        }

        public void run() {
            super.onLoadFailed(this.val$ex);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.IntroPageFragment_.4 */
    class AnonymousClass4 extends Task {
        AnonymousClass4(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadPageMeta();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, IntroPageFragment> {
        public IntroPageFragment build() {
            IntroPageFragment_ fragment_ = new IntroPageFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ page(Page page) {
            this.args.putSerializable(IntroPageFragment_.PAGE_ARG, page);
            return this;
        }
    }

    public IntroPageFragment_() {
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
        injectFragmentArguments_();
        this.mPageManager = PageManager_.getInstance_(getActivity());
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null && args_.containsKey(PAGE_ARG)) {
            this.page = (Page) args_.getSerializable(PAGE_ARG);
        }
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void onLoadFailed(DataLoadException ex) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(ex), 0);
    }

    void refreshViews() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.refreshViews();
            }
        }, 0);
    }

    void refreshTitle() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.refreshTitle();
            }
        }, 0);
    }

    void loadPageMeta() {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
