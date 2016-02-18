package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.douban.book.reader.R;
import com.douban.book.reader.location.TocItem;
import io.realm.internal.Table;
import java.util.List;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ColumnTocCard_ extends ColumnTocCard implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.card.ColumnTocCard_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ List val$tocItemList;

        AnonymousClass2(List list) {
            this.val$tocItemList = list;
        }

        public void run() {
            super.updateContent(this.val$tocItemList);
        }
    }

    /* renamed from: com.douban.book.reader.view.card.ColumnTocCard_.4 */
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

    public ColumnTocCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ColumnTocCard build(Context context) {
        ColumnTocCard_ instance = new ColumnTocCard_(context);
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
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public void onViewChanged(HasViews hasViews) {
        View view_btn_bottom = hasViews.findViewById(R.id.btn_bottom);
        if (view_btn_bottom != null) {
            view_btn_bottom.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ColumnTocCard_.this.onBottomButtonClicked();
                }
            });
        }
        init();
    }

    void updateContent(List<TocItem> tocItemList) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(tocItemList), 0);
    }

    void onLoadFailed() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onLoadFailed();
            }
        }, 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
