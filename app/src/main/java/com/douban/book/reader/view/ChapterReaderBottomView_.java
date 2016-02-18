package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ChapterReaderBottomView_ extends ChapterReaderBottomView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.ChapterReaderBottomView_.5 */
    class AnonymousClass5 extends Task {
        AnonymousClass5(String x0, long x1, String x2) {
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

    public ChapterReaderBottomView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ChapterReaderBottomView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ChapterReaderBottomView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ChapterReaderBottomView build(Context context) {
        ChapterReaderBottomView_ instance = new ChapterReaderBottomView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_chapter_reader_bottom_bar, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mWorksManager = WorksManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ChapterReaderBottomView build(Context context, AttributeSet attrs) {
        ChapterReaderBottomView_ instance = new ChapterReaderBottomView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ChapterReaderBottomView build(Context context, AttributeSet attrs, int defStyleAttr) {
        ChapterReaderBottomView_ instance = new ChapterReaderBottomView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mCoverView = (WorksCoverView) hasViews.findViewById(R.id.cover);
        this.mTvAction = (TextView) hasViews.findViewById(R.id.btn_action);
        this.mTvShare = (TextView) hasViews.findViewById(R.id.btn_share);
        if (this.mCoverView != null) {
            this.mCoverView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChapterReaderBottomView_.this.openProfile();
                }
            });
        }
        if (this.mTvAction != null) {
            this.mTvAction.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChapterReaderBottomView_.this.openReader();
                }
            });
        }
        if (this.mTvShare != null) {
            this.mTvShare.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ChapterReaderBottomView_.this.startShare();
                }
            });
        }
        init();
    }

    void updateViews() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateViews();
            }
        }, 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
