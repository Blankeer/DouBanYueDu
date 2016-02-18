package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class BoxedWorksView_ extends BoxedWorksView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.BoxedWorksView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works) {
            this.val$works = works;
        }

        public void run() {
            super.updateWorksCover(this.val$works);
        }
    }

    /* renamed from: com.douban.book.reader.view.BoxedWorksView_.2 */
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

    public BoxedWorksView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public BoxedWorksView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public BoxedWorksView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static BoxedWorksView build(Context context) {
        BoxedWorksView_ instance = new BoxedWorksView_(context);
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
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static BoxedWorksView build(Context context, AttributeSet attrs) {
        BoxedWorksView_ instance = new BoxedWorksView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static BoxedWorksView build(Context context, AttributeSet attrs, int defStyleAttr) {
        BoxedWorksView_ instance = new BoxedWorksView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void updateWorksCover(Works works) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(works), 0);
    }

    void loadWorks(int worksId) {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksId));
    }
}
