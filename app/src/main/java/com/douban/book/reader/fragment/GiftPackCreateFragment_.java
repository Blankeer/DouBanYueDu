package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.GiftEventManager_;
import com.douban.book.reader.manager.GiftPackManager_;
import com.douban.book.reader.manager.UserManager_;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.view.BoxedWorksView;
import com.douban.book.reader.view.GiftMessageView;
import com.douban.book.reader.view.RoundTipView;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftPackCreateFragment_ extends GiftPackCreateFragment implements HasViews, OnViewChangedListener {
    public static final String EVENT_ID_ARG = "eventId";
    public static final String WORKS_ID_ARG = "worksId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.GiftPackCreateFragment_.5 */
    class AnonymousClass5 implements Runnable {
        final /* synthetic */ Works val$works;

        AnonymousClass5(Works works) {
            this.val$works = works;
        }

        public void run() {
            super.updateViews(this.val$works);
        }
    }

    /* renamed from: com.douban.book.reader.fragment.GiftPackCreateFragment_.7 */
    class AnonymousClass7 extends Task {
        AnonymousClass7(String x0, long x1, String x2) {
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

    /* renamed from: com.douban.book.reader.fragment.GiftPackCreateFragment_.8 */
    class AnonymousClass8 extends Task {
        AnonymousClass8(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.onBtnCreateGiftPackClicked();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, GiftPackCreateFragment> {
        public GiftPackCreateFragment build() {
            GiftPackCreateFragment_ fragment_ = new GiftPackCreateFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ worksId(int worksId) {
            this.args.putInt(GiftPackCreateFragment_.WORKS_ID_ARG, worksId);
            return this;
        }

        public FragmentBuilder_ eventId(int eventId) {
            this.args.putInt(GiftPackCreateFragment_.EVENT_ID_ARG, eventId);
            return this;
        }
    }

    public GiftPackCreateFragment_() {
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
        this.mWorksTitle = null;
        this.mWorksSubTitle = null;
        this.mBoxedWorksView = null;
        this.mRoundTipView1 = null;
        this.mQuantity = null;
        this.mAvailableAmount = null;
        this.mRoundTipView2 = null;
        this.mBtnEditGiftNote = null;
        this.mBtnWriteGiftNote = null;
        this.mBtnCreateGiftPack = null;
        this.mGiftMessageView = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectFragmentArguments_();
        this.mGiftPackManager = GiftPackManager_.getInstance_(getActivity());
        this.mGiftEventManager = GiftEventManager_.getInstance_(getActivity());
        this.mWorksManager = WorksManager_.getInstance_(getActivity());
        this.mUserManager = UserManager_.getInstance_(getActivity());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mWorksTitle = (TextView) hasViews.findViewById(R.id.works_title);
        this.mWorksSubTitle = (TextView) hasViews.findViewById(R.id.works_sub_title);
        this.mBoxedWorksView = (BoxedWorksView) hasViews.findViewById(R.id.boxed_works);
        this.mRoundTipView1 = (RoundTipView) hasViews.findViewById(R.id.round_tip_1);
        this.mQuantity = (EditText) hasViews.findViewById(R.id.quantity);
        this.mAvailableAmount = (TextView) hasViews.findViewById(R.id.available_amount);
        this.mRoundTipView2 = (RoundTipView) hasViews.findViewById(R.id.round_tip_2);
        this.mBtnEditGiftNote = (TextView) hasViews.findViewById(R.id.btn_edit_gift_note);
        this.mBtnWriteGiftNote = (TextView) hasViews.findViewById(R.id.txt_write_gift_note);
        this.mBtnCreateGiftPack = (Button) hasViews.findViewById(R.id.btn_create_gift_pack);
        this.mGiftMessageView = (GiftMessageView) hasViews.findViewById(R.id.gift_note_view);
        if (this.mBtnWriteGiftNote != null) {
            this.mBtnWriteGiftNote.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    GiftPackCreateFragment_.this.onBtnEditGiftMessageClicked();
                }
            });
        }
        if (this.mBtnEditGiftNote != null) {
            this.mBtnEditGiftNote.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    GiftPackCreateFragment_.this.onBtnEditGiftMessageClicked();
                }
            });
        }
        if (this.mBtnCreateGiftPack != null) {
            this.mBtnCreateGiftPack.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    GiftPackCreateFragment_.this.onBtnCreateGiftPackClicked();
                }
            });
        }
        TextView view = (TextView) hasViews.findViewById(R.id.quantity);
        if (view != null) {
            view.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    GiftPackCreateFragment_.this.onQuantityChanged();
                }

                public void afterTextChanged(Editable s) {
                }
            });
        }
        init();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null) {
            if (args_.containsKey(WORKS_ID_ARG)) {
                this.worksId = args_.getInt(WORKS_ID_ARG);
            }
            if (args_.containsKey(EVENT_ID_ARG)) {
                this.eventId = args_.getInt(EVENT_ID_ARG);
            }
        }
    }

    void updateViews(Works works) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass5(works), 0);
    }

    void updateAvailableCount() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateAvailableCount();
            }
        }, 0);
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass7(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void onBtnCreateGiftPackClicked() {
        BackgroundExecutor.execute(new AnonymousClass8(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
