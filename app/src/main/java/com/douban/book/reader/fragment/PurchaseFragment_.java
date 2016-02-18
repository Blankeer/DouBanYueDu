package com.douban.book.reader.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.GiftPackManager_;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class PurchaseFragment_ extends PurchaseFragment implements HasViews, OnViewChangedListener {
    public static final String CHAPTER_ID_ARG = "chapterId";
    public static final String GIFT_PACK_ID_ARG = "giftPackId";
    public static final String PROMPT_DOWNLOAD_ARG = "promptDownload";
    public static final String URI_ARG = "uri";
    public static final String WORKS_ID_ARG = "worksId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.PurchaseFragment_.3 */
    class AnonymousClass3 extends Task {
        AnonymousClass3(String x0, long x1, String x2) {
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

    /* renamed from: com.douban.book.reader.fragment.PurchaseFragment_.4 */
    class AnonymousClass4 extends Task {
        final /* synthetic */ boolean val$secretly;

        AnonymousClass4(String x0, long x1, String x2, boolean z) {
            this.val$secretly = z;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.doPurchase(this.val$secretly);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.PurchaseFragment_.5 */
    class AnonymousClass5 extends Task {
        AnonymousClass5(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.refreshUserInfo();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.PurchaseFragment_.6 */
    class AnonymousClass6 extends Task {
        AnonymousClass6(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.updateWorks();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.PurchaseFragment_.7 */
    class AnonymousClass7 extends Task {
        AnonymousClass7(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.updateGiftPack();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, PurchaseFragment> {
        public PurchaseFragment build() {
            PurchaseFragment_ fragment_ = new PurchaseFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ uri(Uri uri) {
            this.args.putParcelable(PurchaseFragment_.URI_ARG, uri);
            return this;
        }

        public FragmentBuilder_ worksId(int worksId) {
            this.args.putInt(PurchaseFragment_.WORKS_ID_ARG, worksId);
            return this;
        }

        public FragmentBuilder_ chapterId(int chapterId) {
            this.args.putInt(PurchaseFragment_.CHAPTER_ID_ARG, chapterId);
            return this;
        }

        public FragmentBuilder_ giftPackId(int giftPackId) {
            this.args.putInt(PurchaseFragment_.GIFT_PACK_ID_ARG, giftPackId);
            return this;
        }

        public FragmentBuilder_ promptDownload(boolean promptDownload) {
            this.args.putBoolean(PurchaseFragment_.PROMPT_DOWNLOAD_ARG, promptDownload);
            return this;
        }
    }

    public PurchaseFragment_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public View findViewById(int id) {
        if (this.contentView_ == null) {
            return null;
        }
        return this.contentView_.findViewById(id);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
    }

    private void init_(Bundle savedInstanceState) {
        injectFragmentArguments_();
        this.mWorksManager = WorksManager_.getInstance_(getActivity());
        this.mGiftPackManager = GiftPackManager_.getInstance_(getActivity());
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null) {
            if (args_.containsKey(URI_ARG)) {
                this.uri = (Uri) args_.getParcelable(URI_ARG);
            }
            if (args_.containsKey(WORKS_ID_ARG)) {
                this.worksId = args_.getInt(WORKS_ID_ARG);
            }
            if (args_.containsKey(CHAPTER_ID_ARG)) {
                this.chapterId = args_.getInt(CHAPTER_ID_ARG);
            }
            if (args_.containsKey(GIFT_PACK_ID_ARG)) {
                this.giftPackId = args_.getInt(GIFT_PACK_ID_ARG);
            }
            if (args_.containsKey(PROMPT_DOWNLOAD_ARG)) {
                this.promptDownload = args_.getBoolean(PROMPT_DOWNLOAD_ARG);
            }
        }
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }

    void updateViews() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateViews();
            }
        }, 0);
    }

    void onPurchaseSucceed() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onPurchaseSucceed();
            }
        }, 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void doPurchase(boolean secretly) {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, secretly));
    }

    void refreshUserInfo() {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void updateWorks() {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void updateGiftPack() {
        BackgroundExecutor.execute(new AnonymousClass7(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
