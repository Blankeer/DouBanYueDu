package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.StoreManager_;
import com.douban.book.reader.manager.exception.DataLoadException;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class StoreTabFragment_ extends StoreTabFragment implements HasViews, OnViewChangedListener {
    public static final String TAB_NAME_ARG = "tabName";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.StoreTabFragment_.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ DataLoadException val$e;

        AnonymousClass3(DataLoadException dataLoadException) {
            this.val$e = dataLoadException;
        }

        public void run() {
            super.onLoadDataFailed(this.val$e);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.StoreTabFragment_.4 */
    class AnonymousClass4 extends Task {
        AnonymousClass4(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, StoreTabFragment> {
        public StoreTabFragment build() {
            StoreTabFragment_ fragment_ = new StoreTabFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ tabName(String tabName) {
            this.args.putString(StoreTabFragment_.TAB_NAME_ARG, tabName);
            return this;
        }
    }

    public StoreTabFragment_() {
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
        this.mStoreManager = StoreManager_.getInstance_(getActivity());
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
        if (args_ != null && args_.containsKey(TAB_NAME_ARG)) {
            this.tabName = args_.getString(TAB_NAME_ARG);
        }
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void updateCards() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateCards();
            }
        }, 0);
    }

    void onLoadDataSucceed() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onLoadDataSucceed();
            }
        }, 0);
    }

    void onLoadDataFailed(DataLoadException e) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass3(e), 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
