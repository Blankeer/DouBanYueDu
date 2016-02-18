package com.douban.book.reader.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.lib.view.LockView;
import com.douban.book.reader.manager.AnnotationManager_;
import com.douban.book.reader.manager.UserManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class NotePrivacyInfoView_ extends NotePrivacyInfoView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.NotePrivacyInfoView_.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ boolean val$enabled;

        AnonymousClass4(boolean z) {
            this.val$enabled = z;
        }

        public void run() {
            super.setLockEnabled(this.val$enabled);
        }
    }

    /* renamed from: com.douban.book.reader.view.NotePrivacyInfoView_.5 */
    class AnonymousClass5 implements Runnable {
        final /* synthetic */ int val$resId;

        AnonymousClass5(int i) {
            this.val$resId = i;
        }

        public void run() {
            super.updatePrivateInfoText(this.val$resId);
        }
    }

    /* renamed from: com.douban.book.reader.view.NotePrivacyInfoView_.6 */
    class AnonymousClass6 extends Task {
        AnonymousClass6(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.updatePrivacyToServer();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public NotePrivacyInfoView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public NotePrivacyInfoView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public NotePrivacyInfoView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static NotePrivacyInfoView build(Context context) {
        NotePrivacyInfoView_ instance = new NotePrivacyInfoView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_note_privacy_info, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mAnnotationManager = AnnotationManager_.getInstance_(getContext());
        this.mUserManager = UserManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static NotePrivacyInfoView build(Context context, AttributeSet attrs) {
        NotePrivacyInfoView_ instance = new NotePrivacyInfoView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static NotePrivacyInfoView build(Context context, AttributeSet attrs, int defStyleAttr) {
        NotePrivacyInfoView_ instance = new NotePrivacyInfoView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mPrivacyInfo = (TextView) hasViews.findViewById(R.id.text_privacy_info);
        this.mShareTarget = hasViews.findViewById(R.id.share_target);
        this.mLockView = (LockView) hasViews.findViewById(R.id.btn_privacy);
        if (this.mLockView != null) {
            this.mLockView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    NotePrivacyInfoView_.this.onLockClicked();
                }
            });
            this.mLockView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    NotePrivacyInfoView_.this.onLockChanged(isChecked);
                }
            });
        }
        init();
    }

    void updateView() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateView();
            }
        }, 0);
    }

    void setLockEnabled(boolean enabled) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass4(enabled), 0);
    }

    void updatePrivateInfoText(@StringRes int resId) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass5(resId), 0);
    }

    void updatePrivacyToServer() {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
