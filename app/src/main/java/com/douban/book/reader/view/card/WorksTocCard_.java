package com.douban.book.reader.view.card;

import android.content.Context;
import com.douban.book.reader.content.paragraph.Paragraph;
import io.realm.internal.Table;
import java.util.List;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksTocCard_ extends WorksTocCard implements HasViews {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.card.WorksTocCard_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ List val$paragraphList;

        AnonymousClass1(List list) {
            this.val$paragraphList = list;
        }

        public void run() {
            super.updateContent(this.val$paragraphList);
        }
    }

    /* renamed from: com.douban.book.reader.view.card.WorksTocCard_.3 */
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

    public WorksTocCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksTocCard build(Context context) {
        WorksTocCard_ instance = new WorksTocCard_(context);
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
        OnViewChangedNotifier.replaceNotifier(OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_));
    }

    void updateContent(List<Paragraph> paragraphList) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(paragraphList), 0);
    }

    void onLoadFailed() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onLoadFailed();
            }
        }, 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
