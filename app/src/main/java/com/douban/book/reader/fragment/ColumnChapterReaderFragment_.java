package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ColumnChapterReaderFragment_ extends ColumnChapterReaderFragment implements HasViews, OnViewChangedListener {
    public static final String CHAPTER_ID_ARG = "chapterId";
    public static final String LEGACY_COLUMN_ID_ARG = "legacyColumnId";
    public static final String WORKS_ID_ARG = "worksId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.ColumnChapterReaderFragment_.2 */
    class AnonymousClass2 extends Task {
        AnonymousClass2(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadWorks();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, ColumnChapterReaderFragment> {
        public ColumnChapterReaderFragment build() {
            ColumnChapterReaderFragment_ fragment_ = new ColumnChapterReaderFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ worksId(int worksId) {
            this.args.putInt(ColumnChapterReaderFragment_.WORKS_ID_ARG, worksId);
            return this;
        }

        public FragmentBuilder_ legacyColumnId(int legacyColumnId) {
            this.args.putInt(ColumnChapterReaderFragment_.LEGACY_COLUMN_ID_ARG, legacyColumnId);
            return this;
        }

        public FragmentBuilder_ chapterId(int chapterId) {
            this.args.putInt(ColumnChapterReaderFragment_.CHAPTER_ID_ARG, chapterId);
            return this;
        }
    }

    public ColumnChapterReaderFragment_() {
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
        this.mWorksManager = WorksManager_.getInstance_(getActivity());
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
        if (args_ != null) {
            if (args_.containsKey(WORKS_ID_ARG)) {
                this.worksId = args_.getInt(WORKS_ID_ARG);
            }
            if (args_.containsKey(LEGACY_COLUMN_ID_ARG)) {
                this.legacyColumnId = args_.getInt(LEGACY_COLUMN_ID_ARG);
            }
            if (args_.containsKey(CHAPTER_ID_ARG)) {
                this.chapterId = args_.getInt(CHAPTER_ID_ARG);
            }
        }
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void loadUri() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.loadUri();
            }
        }, 0);
    }

    void loadWorks() {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
