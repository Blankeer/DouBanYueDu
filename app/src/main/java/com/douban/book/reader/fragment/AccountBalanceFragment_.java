package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.UserManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class AccountBalanceFragment_ extends AccountBalanceFragment implements HasViews, OnViewChangedListener {
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.AccountBalanceFragment_.8 */
    class AnonymousClass8 extends Task {
        AnonymousClass8(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadUserInfo();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, AccountBalanceFragment> {
        public AccountBalanceFragment build() {
            AccountBalanceFragment_ fragment_ = new AccountBalanceFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }
    }

    public AccountBalanceFragment_() {
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
        if (this.contentView_ == null) {
            this.contentView_ = inflater.inflate(R.layout.frag_account_balance, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mAccountLeft = null;
        this.mCreditLeft = null;
        this.mBtnDeposit = null;
        this.mBtnRedeem = null;
        this.mFeedbackEntry = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
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
        this.mAccountLeft = (TextView) hasViews.findViewById(R.id.account_left);
        this.mCreditLeft = (TextView) hasViews.findViewById(R.id.credit_left);
        this.mBtnDeposit = (Button) hasViews.findViewById(R.id.btn_deposit);
        this.mBtnRedeem = (Button) hasViews.findViewById(R.id.btn_redeem);
        this.mFeedbackEntry = (TextView) hasViews.findViewById(R.id.text_feedback_entry);
        View view_btn_deposit_record = hasViews.findViewById(R.id.btn_deposit_record);
        View view_btn_purchase_record = hasViews.findViewById(R.id.btn_purchase_record);
        View view_btn_coupon_record = hasViews.findViewById(R.id.btn_coupon_record);
        View view_btn_redemption_record = hasViews.findViewById(R.id.btn_redemption_record);
        if (this.mFeedbackEntry != null) {
            this.mFeedbackEntry.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AccountBalanceFragment_.this.onFeedbackEntryClicked();
                }
            });
        }
        if (this.mBtnDeposit != null) {
            this.mBtnDeposit.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AccountBalanceFragment_.this.onBtnDepositClick();
                }
            });
        }
        if (this.mBtnRedeem != null) {
            this.mBtnRedeem.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AccountBalanceFragment_.this.onBtnRedeemClick();
                }
            });
        }
        if (view_btn_deposit_record != null) {
            view_btn_deposit_record.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AccountBalanceFragment_.this.onBtnDepositRecord();
                }
            });
        }
        if (view_btn_purchase_record != null) {
            view_btn_purchase_record.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AccountBalanceFragment_.this.onBtnPurchaseRecord();
                }
            });
        }
        if (view_btn_coupon_record != null) {
            view_btn_coupon_record.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AccountBalanceFragment_.this.onBtnCouponRecord();
                }
            });
        }
        if (view_btn_redemption_record != null) {
            view_btn_redemption_record.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AccountBalanceFragment_.this.onBtnRedemptionRecord();
                }
            });
        }
        init();
    }

    void loadUserInfo() {
        BackgroundExecutor.execute(new AnonymousClass8(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
