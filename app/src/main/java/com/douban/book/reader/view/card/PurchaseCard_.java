package com.douban.book.reader.view.card;

import android.content.Context;
import com.douban.book.reader.manager.GiftPackManager_;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class PurchaseCard_ extends PurchaseCard implements HasViews {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.card.PurchaseCard_.2 */
    class AnonymousClass2 extends Task {
        AnonymousClass2(String x0, long x1, String x2) {
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

    public PurchaseCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static PurchaseCard build(Context context) {
        PurchaseCard_ instance = new PurchaseCard_(context);
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
        this.mGiftPackManager = GiftPackManager_.getInstance_(getContext());
        this.mWorksManager = WorksManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    void updateViews() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateViews();
            }
        }, 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
