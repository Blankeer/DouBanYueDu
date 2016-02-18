package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.exception.DataLoadException;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksProfileFragment_ extends WorksProfileFragment implements HasViews, OnViewChangedListener {
    public static final String LEGACY_COLUMN_ID_ARG = "legacyColumnId";
    public static final String WORKS_ID_ARG = "worksId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.WorksProfileFragment_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ DataLoadException val$e;

        AnonymousClass1(DataLoadException dataLoadException) {
            this.val$e = dataLoadException;
        }

        public void run() {
            super.loadFailed(this.val$e);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.WorksProfileFragment_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Works val$works;

        AnonymousClass2(Works works) {
            this.val$works = works;
        }

        public void run() {
            super.updateView(this.val$works);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.WorksProfileFragment_.3 */
    class AnonymousClass3 extends Task {
        AnonymousClass3(String x0, long x1, String x2) {
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

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, WorksProfileFragment> {
        public WorksProfileFragment build() {
            WorksProfileFragment_ fragment_ = new WorksProfileFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ worksId(int worksId) {
            this.args.putInt(WorksProfileFragment_.WORKS_ID_ARG, worksId);
            return this;
        }

        public FragmentBuilder_ legacyColumnId(int legacyColumnId) {
            this.args.putInt(WorksProfileFragment_.LEGACY_COLUMN_ID_ARG, legacyColumnId);
            return this;
        }
    }

    public WorksProfileFragment_() {
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
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        setHasOptionsMenu(true);
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
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.works_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_share) {
            return super.onOptionsItemSelected(item);
        }
        onMenuItemShare();
        return true;
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void loadFailed(DataLoadException e) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(e), 0);
    }

    void updateView(Works works) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(works), 0);
    }

    void loadWorks() {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
