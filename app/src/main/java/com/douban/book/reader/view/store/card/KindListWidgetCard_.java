package com.douban.book.reader.view.store.card;

import android.content.Context;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.entity.store.KindListStoreWidgetEntity;
import com.douban.book.reader.manager.WorksKindManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class KindListWidgetCard_ extends KindListWidgetCard implements HasViews {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.store.card.KindListWidgetCard_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ KindListStoreWidgetEntity val$entity;
        final /* synthetic */ WorksKind val$worksRootKind;

        AnonymousClass1(KindListStoreWidgetEntity kindListStoreWidgetEntity, WorksKind worksKind) {
            this.val$entity = kindListStoreWidgetEntity;
            this.val$worksRootKind = worksKind;
        }

        public void run() {
            super.updateViews(this.val$entity, this.val$worksRootKind);
        }
    }

    /* renamed from: com.douban.book.reader.view.store.card.KindListWidgetCard_.2 */
    class AnonymousClass2 extends Task {
        final /* synthetic */ KindListStoreWidgetEntity val$entity;

        AnonymousClass2(String x0, long x1, String x2, KindListStoreWidgetEntity kindListStoreWidgetEntity) {
            this.val$entity = kindListStoreWidgetEntity;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadRootKind(this.val$entity);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public KindListWidgetCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static KindListWidgetCard build(Context context) {
        KindListWidgetCard_ instance = new KindListWidgetCard_(context);
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
        this.mWorksKindManager = WorksKindManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    void updateViews(KindListStoreWidgetEntity entity, WorksKind worksRootKind) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(entity, worksRootKind), 0);
    }

    void loadRootKind(KindListStoreWidgetEntity entity) {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, entity));
    }
}
