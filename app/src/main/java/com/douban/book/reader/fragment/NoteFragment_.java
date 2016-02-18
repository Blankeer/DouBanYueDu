package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Annotation;
import io.realm.internal.Table;
import java.util.List;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public final class NoteFragment_ extends NoteFragment implements HasViews, OnViewChangedListener {
    public static final String WORKS_ID_ARG = "worksId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.NoteFragment_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ List val$array;

        AnonymousClass2(List list) {
            this.val$array = list;
        }

        public void run() {
            super.onLoadSucceed(this.val$array);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NoteFragment_.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ int val$resId;

        AnonymousClass3(int i) {
            this.val$resId = i;
        }

        public void run() {
            super.setHint(this.val$resId);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.NoteFragment_.4 */
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

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, NoteFragment> {
        public NoteFragment build() {
            NoteFragment_ fragment_ = new NoteFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ worksId(int worksId) {
            this.args.putInt(NoteFragment_.WORKS_ID_ARG, worksId);
            return this;
        }
    }

    public NoteFragment_() {
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
            this.contentView_ = inflater.inflate(R.layout.frag_note, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mListView = null;
        this.mHintView = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectFragmentArguments_();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mListView = (StickyListHeadersListView) hasViews.findViewById(R.id.list);
        this.mHintView = (TextView) hasViews.findViewById(R.id.hint_view);
        if (this.mListView != null) {
            this.mListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    NoteFragment_.this.onListItemClicked((Annotation) parent.getAdapter().getItem(position));
                }
            });
        }
        init();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null && args_.containsKey(WORKS_ID_ARG)) {
            this.worksId = args_.getInt(WORKS_ID_ARG);
        }
    }

    void onLoadSucceed(List<Annotation> array) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(array), 0);
    }

    void setHint(int resId) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass3(resId), 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
