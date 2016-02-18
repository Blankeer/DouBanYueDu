package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.RedeemRecord;
import com.douban.book.reader.manager.UserManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class RedeemView_ extends RedeemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.RedeemView_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ RedeemRecord val$redeemRecord;

        AnonymousClass2(RedeemRecord redeemRecord) {
            this.val$redeemRecord = redeemRecord;
        }

        public void run() {
            super.onRedeemSucceed(this.val$redeemRecord);
        }
    }

    /* renamed from: com.douban.book.reader.view.RedeemView_.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ Exception val$e;

        AnonymousClass3(Exception exception) {
            this.val$e = exception;
        }

        public void run() {
            super.onRedeemFailed(this.val$e);
        }
    }

    /* renamed from: com.douban.book.reader.view.RedeemView_.4 */
    class AnonymousClass4 extends Task {
        final /* synthetic */ String val$code;

        AnonymousClass4(String x0, long x1, String x2, String str) {
            this.val$code = str;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.exchange(this.val$code);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public RedeemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public RedeemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public RedeemView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static RedeemView build(Context context) {
        RedeemView_ instance = new RedeemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_redeem, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mUserManager = UserManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static RedeemView build(Context context, AttributeSet attrs) {
        RedeemView_ instance = new RedeemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static RedeemView build(Context context, AttributeSet attrs, int defStyle) {
        RedeemView_ instance = new RedeemView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBtnConfirm = (Button) hasViews.findViewById(R.id.btn_confirm);
        this.mInput = (EditText) hasViews.findViewById(R.id.input);
        init();
    }

    void onRedeemStarted() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onRedeemStarted();
            }
        }, 0);
    }

    void onRedeemSucceed(RedeemRecord redeemRecord) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(redeemRecord), 0);
    }

    void onRedeemFailed(Exception e) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass3(e), 0);
    }

    void exchange(String code) {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, code));
    }
}
