package com.douban.book.reader.view.page;

import android.content.Context;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class MetaPageView_ extends MetaPageView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.page.MetaPageView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Manifest val$manifest;
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works, Manifest manifest) {
            this.val$works = works;
            this.val$manifest = manifest;
        }

        public void run() {
            super.setWorks(this.val$works, this.val$manifest);
        }
    }

    /* renamed from: com.douban.book.reader.view.page.MetaPageView_.2 */
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

    public MetaPageView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static MetaPageView build(Context context) {
        MetaPageView_ instance = new MetaPageView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_page_meta, this);
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
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
        this.mSubTitle = (TextView) hasViews.findViewById(R.id.subtitle);
        this.mAuthor = (TextView) hasViews.findViewById(R.id.author);
        this.mTranslator = (TextView) hasViews.findViewById(R.id.translator);
        this.mPublisher = (TextView) hasViews.findViewById(R.id.publisher);
        this.mPublishTime = (TextView) hasViews.findViewById(R.id.publish_time);
        this.mOnSaleTime = (TextView) hasViews.findViewById(R.id.on_sale_time);
        this.mISBN = (TextView) hasViews.findViewById(R.id.isbn);
        this.mWorksAcknowledgements = (TextView) hasViews.findViewById(R.id.works_acknowledgements);
    }

    void setWorks(Works works, Manifest manifest) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(works, manifest), 0);
    }

    void loadData(int worksId) {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksId));
    }
}
