package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.manager.AnnotationManager_;
import io.realm.internal.Table;
import java.util.UUID;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShareNoteInfoView_ extends ShareNoteInfoView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.ShareNoteInfoView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Annotation val$annotation;

        AnonymousClass1(Annotation annotation) {
            this.val$annotation = annotation;
        }

        public void run() {
            super.updateViews(this.val$annotation);
        }
    }

    /* renamed from: com.douban.book.reader.view.ShareNoteInfoView_.2 */
    class AnonymousClass2 extends Task {
        final /* synthetic */ UUID val$uuid;

        AnonymousClass2(String x0, long x1, String x2, UUID uuid) {
            this.val$uuid = uuid;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData(this.val$uuid);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public ShareNoteInfoView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ShareNoteInfoView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ShareNoteInfoView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ShareNoteInfoView build(Context context) {
        ShareNoteInfoView_ instance = new ShareNoteInfoView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_share_note_info, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mAnnotationManager = AnnotationManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ShareNoteInfoView build(Context context, AttributeSet attrs) {
        ShareNoteInfoView_ instance = new ShareNoteInfoView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ShareNoteInfoView build(Context context, AttributeSet attrs, int defStyleAttr) {
        ShareNoteInfoView_ instance = new ShareNoteInfoView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mNoteContent = (TextView) hasViews.findViewById(R.id.note_content);
        init();
    }

    void updateViews(Annotation annotation) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(annotation), 0);
    }

    void loadData(UUID uuid) {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, uuid));
    }
}
