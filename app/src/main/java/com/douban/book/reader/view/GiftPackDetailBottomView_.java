package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.GiftPackManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftPackDetailBottomView_ extends GiftPackDetailBottomView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.GiftPackDetailBottomView_.3 */
    class AnonymousClass3 extends Task {
        final /* synthetic */ int val$packId;

        AnonymousClass3(String x0, long x1, String x2, int i) {
            this.val$packId = i;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData(this.val$packId);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.view.GiftPackDetailBottomView_.4 */
    class AnonymousClass4 extends Task {
        final /* synthetic */ String val$hashCode;

        AnonymousClass4(String x0, long x1, String x2, String str) {
            this.val$hashCode = str;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadDataByHashCode(this.val$hashCode);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.view.GiftPackDetailBottomView_.5 */
    class AnonymousClass5 extends Task {
        AnonymousClass5(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.receiveGift();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public GiftPackDetailBottomView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftPackDetailBottomView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public GiftPackDetailBottomView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static GiftPackDetailBottomView build(Context context) {
        GiftPackDetailBottomView_ instance = new GiftPackDetailBottomView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_gift_pack_detail_bottom_bar, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mGiftPackManager = GiftPackManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static GiftPackDetailBottomView build(Context context, AttributeSet attrs) {
        GiftPackDetailBottomView_ instance = new GiftPackDetailBottomView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static GiftPackDetailBottomView build(Context context, AttributeSet attrs, int defStyleAttr) {
        GiftPackDetailBottomView_ instance = new GiftPackDetailBottomView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mTipsView = (TextView) hasViews.findViewById(R.id.tips);
        this.mBtnAction = (TextView) hasViews.findViewById(R.id.btn_action);
        if (this.mBtnAction != null) {
            this.mBtnAction.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    GiftPackDetailBottomView_.this.onBtnActionClicked();
                }
            });
        }
        init();
    }

    void updateViews() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateViews();
            }
        }, 0);
    }

    void loadData(int packId) {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, packId));
    }

    void loadDataByHashCode(String hashCode) {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, hashCode));
    }

    void receiveGift() {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
