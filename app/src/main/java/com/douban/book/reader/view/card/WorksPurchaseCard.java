package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.event.WorksUpdatedEvent;
import com.douban.book.reader.fragment.GiftPackCreateFragment_;
import com.douban.book.reader.fragment.PurchaseFragment_;
import com.douban.book.reader.fragment.interceptor.LoginRecommendedInterceptor;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.span.ThemedForegroundColorSpan;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.SpanUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.WorksPriceView;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class WorksPurchaseCard extends Card<WorksPurchaseCard> {
    private TextView mBtnPresent;
    private TextView mBtnPurchase;
    private View mDividerToLeftOfBtnPresent;
    private View mDividerToLeftOfBtnPurchase;
    @Bean
    WorksManager mWorkManager;
    private int mWorksId;
    private View mWorksOwnedSign;
    private WorksPriceView mWorksPriceView;

    public WorksPurchaseCard(Context context) {
        super(context);
        init();
    }

    private void init() {
        content((int) R.layout.card_works_purchase);
        this.mWorksPriceView = (WorksPriceView) findViewById(R.id.works_price);
        this.mWorksOwnedSign = findViewById(R.id.works_owned_sign);
        this.mBtnPurchase = (TextView) findViewById(R.id.btn_purchase);
        this.mDividerToLeftOfBtnPurchase = findViewById(R.id.divider_to_left_of_btn_purchase);
        this.mBtnPresent = (TextView) findViewById(R.id.btn_present);
        this.mDividerToLeftOfBtnPresent = findViewById(R.id.divider_to_left_of_btn_present);
        noContentPadding();
        ViewUtils.setEventAware(this);
    }

    public WorksPurchaseCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadWorks();
        return this;
    }

    public void onEventMainThread(WorksUpdatedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            loadWorks();
        }
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateButtons();
    }

    @Background
    void loadWorks() {
        try {
            updateViews(this.mWorkManager.getWorks(this.mWorksId));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateViews(Works works) {
        this.mWorksPriceView.showPriceFor(works);
        ViewUtils.goneIf(works.hasOwned, this.mDividerToLeftOfBtnPurchase, this.mBtnPurchase);
        ViewUtils.showIf(works.hasOwned, this.mWorksOwnedSign);
        updateButtons();
        if (works.price <= 0) {
            this.mBtnPurchase.setText(SpanUtils.applySpan(Res.getString(R.string.btn_take_for_free), new ThemedForegroundColorSpan(R.array.secondary_text_color)));
            this.mBtnPurchase.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    PurchaseFragment_.builder().uri(ReaderUri.works(WorksPurchaseCard.this.mWorksId)).build().setShowInterceptor(new LoginRecommendedInterceptor(Res.getString(R.string.btn_take_for_free))).showAsActivity(WorksPurchaseCard.this);
                }
            });
            ViewUtils.gone(this.mBtnPresent, this.mDividerToLeftOfBtnPresent);
            return;
        }
        this.mBtnPurchase.setText(SpanUtils.applySpan(Res.getString(R.string.btn_purchase), new ThemedForegroundColorSpan(R.array.secondary_text_color)));
        this.mBtnPurchase.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PurchaseFragment_.builder().uri(ReaderUri.works(WorksPurchaseCard.this.mWorksId)).build().showAsActivity(WorksPurchaseCard.this);
            }
        });
        this.mBtnPresent.setText(SpanUtils.applySpan(Res.getString(R.string.btn_present), new ThemedForegroundColorSpan(R.array.secondary_text_color)));
        this.mBtnPresent.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GiftPackCreateFragment_.builder().worksId(WorksPurchaseCard.this.mWorksId).build().showAsActivity(WorksPurchaseCard.this);
            }
        });
        ViewUtils.visible(this.mBtnPresent, this.mDividerToLeftOfBtnPresent);
    }

    public void updateButtons() {
        ViewUtils.setDrawableTopLarge(this.mBtnPurchase, Res.getDrawable(R.drawable.v_purchase));
        ViewUtils.setDrawableTopLarge(this.mBtnPresent, Res.getDrawable(R.drawable.v_gift));
    }
}
