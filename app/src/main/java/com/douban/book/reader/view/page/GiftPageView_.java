package com.douban.book.reader.view.page;

import android.content.Context;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.view.FlexibleScrollView;
import com.douban.book.reader.view.ParagraphView;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftPageView_ extends GiftPageView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.page.GiftPageView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works) {
            this.val$works = works;
        }

        public void run() {
            super.updateView(this.val$works);
        }
    }

    /* renamed from: com.douban.book.reader.view.page.GiftPageView_.2 */
    class AnonymousClass2 extends Task {
        final /* synthetic */ int val$worksId;

        AnonymousClass2(String x0, long x1, String x2, int i) {
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

    public GiftPageView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static GiftPageView build(Context context) {
        GiftPageView_ instance = new GiftPageView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_page_gift, this);
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
        this.mGiveTime = (TextView) hasViews.findViewById(R.id.give_time);
        this.mGiftNote = (ParagraphView) hasViews.findViewById(R.id.gift_note);
        this.mGiver = (TextView) hasViews.findViewById(R.id.giver);
        this.mRecipient = (TextView) hasViews.findViewById(R.id.recipient);
        this.mGiftNoteContainer = (FlexibleScrollView) hasViews.findViewById(R.id.gift_note_container);
        init();
    }

    void updateView(Works works) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(works), 0);
    }

    void loadData(int worksId) {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksId));
    }
}
