package com.douban.book.reader.view.card;

import android.content.Context;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.view.ParagraphView;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksInfoCard_ extends WorksInfoCard implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.card.WorksInfoCard_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works) {
            this.val$works = works;
        }

        public void run() {
            super.updateViews(this.val$works);
        }
    }

    /* renamed from: com.douban.book.reader.view.card.WorksInfoCard_.2 */
    class AnonymousClass2 extends Task {
        final /* synthetic */ int val$worksId;

        AnonymousClass2(String x0, long x1, String x2, int i) {
            this.val$worksId = i;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadWorks(this.val$worksId);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public WorksInfoCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksInfoCard build(Context context) {
        WorksInfoCard_ instance = new WorksInfoCard_(context);
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
        this.mTitle = (TextView) hasViews.findViewById(R.id.works_title);
        this.mSubTitle = (TextView) hasViews.findViewById(R.id.works_sub_title);
        this.mWorksBasicInfo = (TextView) hasViews.findViewById(R.id.works_info);
        this.mAbstract = (ParagraphView) hasViews.findViewById(R.id.abstract_text);
        this.mRatingBar = (RatingBar) hasViews.findViewById(R.id.rating_bar);
        this.mRatingInfo = (TextView) hasViews.findViewById(R.id.rating_info);
    }

    void updateViews(Works works) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(works), 0);
    }

    void loadWorks(int worksId) {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksId));
    }
}
