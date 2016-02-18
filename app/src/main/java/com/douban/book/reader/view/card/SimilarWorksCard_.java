package com.douban.book.reader.view.card;

import android.content.Context;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import java.util.List;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SimilarWorksCard_ extends SimilarWorksCard implements HasViews {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.card.SimilarWorksCard_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ List val$worksList;

        AnonymousClass2(List list) {
            this.val$worksList = list;
        }

        public void run() {
            super.updateViews(this.val$worksList);
        }
    }

    /* renamed from: com.douban.book.reader.view.card.SimilarWorksCard_.3 */
    class AnonymousClass3 extends Task {
        AnonymousClass3(String x0, long x1, String x2) {
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

    public SimilarWorksCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static SimilarWorksCard build(Context context) {
        SimilarWorksCard_ instance = new SimilarWorksCard_(context);
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
        this.mWorksManager = WorksManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    void onLoadFailed() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onLoadFailed();
            }
        }, 0);
    }

    void updateViews(List<Works> worksList) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(worksList), 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
