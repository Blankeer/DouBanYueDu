package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.manager.WorksKindManager_;
import io.realm.internal.Table;
import java.util.List;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksKindFragment_ extends WorksKindFragment implements HasViews, OnViewChangedListener {
    public static final String DEFAULT_TAB_TITLE_ARG = "defaultTabTitle";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.WorksKindFragment_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ List val$rootKindList;

        AnonymousClass1(List list) {
            this.val$rootKindList = list;
        }

        public void run() {
            super.updateTabs(this.val$rootKindList);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.WorksKindFragment_.3 */
    class AnonymousClass3 extends Task {
        AnonymousClass3(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadRootWorksKind();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, WorksKindFragment> {
        public WorksKindFragment build() {
            WorksKindFragment_ fragment_ = new WorksKindFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ defaultTabTitle(String defaultTabTitle) {
            this.args.putString(WorksKindFragment_.DEFAULT_TAB_TITLE_ARG, defaultTabTitle);
            return this;
        }
    }

    public WorksKindFragment_() {
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
        this.mWorksKindManager = WorksKindManager_.getInstance_(getActivity());
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
        if (args_ != null && args_.containsKey(DEFAULT_TAB_TITLE_ARG)) {
            this.defaultTabTitle = args_.getString(DEFAULT_TAB_TITLE_ARG);
        }
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void updateTabs(List<WorksKind> rootKindList) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(rootKindList), 0);
    }

    void onLoadFailed() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onLoadFailed();
            }
        }, 0);
    }

    void loadRootWorksKind() {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
