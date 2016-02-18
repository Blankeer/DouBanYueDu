package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ColumnInfoCard_ extends ColumnInfoCard implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.card.ColumnInfoCard_.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ Works val$works;

        AnonymousClass4(Works works) {
            this.val$works = works;
        }

        public void run() {
            super.updateViews(this.val$works);
        }
    }

    /* renamed from: com.douban.book.reader.view.card.ColumnInfoCard_.5 */
    class AnonymousClass5 extends Task {
        AnonymousClass5(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.toggleSubscribeStatus();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.view.card.ColumnInfoCard_.6 */
    class AnonymousClass6 extends Task {
        final /* synthetic */ int val$worksId;

        AnonymousClass6(String x0, long x1, String x2, int i) {
            this.val$worksId = i;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData(this.val$worksId);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public ColumnInfoCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ColumnInfoCard build(Context context) {
        ColumnInfoCard_ instance = new ColumnInfoCard_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
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

    public void onViewChanged(HasViews hasViews) {
        this.mColumnPlanInfo = (TextView) hasViews.findViewById(R.id.column_info);
        this.mBtnSubscribe = (TextView) hasViews.findViewById(R.id.btn_subscribe);
        this.mProgressBar = (ProgressBar) hasViews.findViewById(R.id.progress_bar);
        if (this.mBtnSubscribe != null) {
            this.mBtnSubscribe.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ColumnInfoCard_.this.onBtnSubscribeClicked();
                }
            });
        }
    }

    void onWorksSubscribedOpStart() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onWorksSubscribedOpStart();
            }
        }, 0);
    }

    void onWorksSubscribedOpFinish() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onWorksSubscribedOpFinish();
            }
        }, 0);
    }

    void updateViews(Works works) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass4(works), 0);
    }

    void toggleSubscribeStatus() {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void loadData(int worksId) {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksId));
    }
}
