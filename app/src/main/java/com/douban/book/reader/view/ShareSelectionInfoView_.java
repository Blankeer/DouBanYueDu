package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShareSelectionInfoView_ extends ShareSelectionInfoView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.ShareSelectionInfoView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ CharSequence val$selection;

        AnonymousClass1(CharSequence charSequence) {
            this.val$selection = charSequence;
        }

        public void run() {
            super.setSelection(this.val$selection);
        }
    }

    /* renamed from: com.douban.book.reader.view.ShareSelectionInfoView_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ String val$url;

        AnonymousClass2(String str) {
            this.val$url = str;
        }

        public void run() {
            super.setIllusUrl(this.val$url);
        }
    }

    /* renamed from: com.douban.book.reader.view.ShareSelectionInfoView_.3 */
    class AnonymousClass3 extends Task {
        final /* synthetic */ Range val$range;
        final /* synthetic */ int val$worksId;

        AnonymousClass3(String x0, long x1, String x2, int i, Range range) {
            this.val$worksId = i;
            this.val$range = range;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData(this.val$worksId, this.val$range);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public ShareSelectionInfoView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ShareSelectionInfoView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ShareSelectionInfoView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ShareSelectionInfoView build(Context context) {
        ShareSelectionInfoView_ instance = new ShareSelectionInfoView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_share_selection_info, this);
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

    public static ShareSelectionInfoView build(Context context, AttributeSet attrs) {
        ShareSelectionInfoView_ instance = new ShareSelectionInfoView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ShareSelectionInfoView build(Context context, AttributeSet attrs, int defStyle) {
        ShareSelectionInfoView_ instance = new ShareSelectionInfoView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mImageIllus = (ImageView) hasViews.findViewById(R.id.image_illus);
        this.mSelection = (ParagraphView) hasViews.findViewById(R.id.selection);
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
    }

    void setSelection(CharSequence selection) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(selection), 0);
    }

    void setIllusUrl(String url) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(url), 0);
    }

    void loadData(int worksId, Range range) {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksId, range));
    }
}
