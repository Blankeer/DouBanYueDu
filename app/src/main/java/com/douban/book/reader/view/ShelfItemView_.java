package com.douban.book.reader.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShelfItemView_ extends ShelfItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.ShelfItemView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Manifest val$manifest;

        AnonymousClass1(Manifest manifest) {
            this.val$manifest = manifest;
        }

        public void run() {
            super.setWorks(this.val$manifest);
        }
    }

    /* renamed from: com.douban.book.reader.view.ShelfItemView_.2 */
    class AnonymousClass2 extends Task {
        AnonymousClass2(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadWorksData();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public ShelfItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ShelfItemView build(Context context) {
        ShelfItemView_ instance = new ShelfItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_shelf_item, this);
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
        this.mIcCheck = (CheckableImageView) hasViews.findViewById(R.id.ic_check);
        this.mCover = (WorksCoverView) hasViews.findViewById(R.id.cover);
        this.mTitle = (TextView) hasViews.findViewById(R.id.text_title);
        this.mDownloadProgress = (ProgressBar) hasViews.findViewById(R.id.download_progress);
        this.mDownloadProgressText = (TextView) hasViews.findViewById(R.id.download_progress_text);
        this.mDownloadStatus = (ImageView) hasViews.findViewById(R.id.ic_download_status);
        initView();
    }

    void setWorks(Manifest manifest) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(manifest), 0);
    }

    void loadWorksData() {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
