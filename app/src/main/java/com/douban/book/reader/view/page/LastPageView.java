package com.douban.book.reader.view.page;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.WorksUpdatedEvent;
import com.douban.book.reader.fragment.PurchaseFragment_;
import com.douban.book.reader.fragment.ReviewDetailFragment_;
import com.douban.book.reader.fragment.ReviewEditFragment_;
import com.douban.book.reader.fragment.interceptor.ForcedLoginInterceptor;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import io.realm.internal.Table;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903175)
public class LastPageView extends AbsPageView {
    @ViewById(2131558897)
    Button mBtnDownload;
    @ViewById(2131558545)
    Button mBtnPurchase;
    @ViewById(2131558523)
    Button mBtnSubscribe;
    @ViewById(2131558904)
    TextView mColumnPlanInfo;
    @ViewById(2131558907)
    TextView mDownloadInfo;
    @ViewById(2131558549)
    RatingBar mRatingBar;
    @ViewById(2131558859)
    View mRatingLayout;
    @ViewById(2131558908)
    ProgressBar mSubscribeProgressBar;
    @ViewById(2131558905)
    TextView mTextPurchaseTip;
    @ViewById(2131558909)
    TextView mTextRatingTip;
    @ViewById(2131558906)
    TextView mTextSubscriptionInfo;
    @ViewById(2131558462)
    TextView mTitle;
    private Works mWorks;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    public LastPageView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        setGeneralTouchListener(this);
        ViewUtils.setEventAware(this);
        this.mBtnDownload.setText(RichText.textWithIcon((int) R.drawable.v_download, (int) R.string.text_download_works));
        this.mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    ReviewEditFragment_.builder().worksId(LastPageView.this.mWorksId).rating(ratingBar.getRating()).build().setShowInterceptor(new ForcedLoginInterceptor()).showAsActivity(LastPageView.this);
                }
            }
        });
    }

    @UiThread
    void setIsSubscribing(boolean isSubscribing) {
        boolean z;
        ViewUtils.showIf(isSubscribing, this.mSubscribeProgressBar);
        if (isSubscribing) {
            z = false;
        } else {
            z = true;
        }
        ViewUtils.showIf(z, this.mBtnSubscribe);
    }

    @Background
    @Click({2131558523})
    void onBtnSubscribeClicked() {
        try {
            setIsSubscribing(true);
            this.mWorksManager.subscribe(this.mWorksId);
            this.mWorks = this.mWorksManager.getFromRemote(Integer.valueOf(this.mWorksId));
        } catch (DataLoadException e) {
            ToastUtils.showToast((int) R.string.general_load_failed);
        } finally {
            setIsSubscribing(false);
            updateViews();
        }
    }

    @Click({2131558545})
    void onBtnPurchaseClicked() {
        PurchaseFragment_.builder().uri(ReaderUri.works(this.mWorksId)).build().showAsActivity((View) this);
    }

    @Click({2131558859})
    void onRatingLayoutClicked() {
        if (!hasRated()) {
            return;
        }
        if (this.mWorks.review == null || !this.mWorks.review.hasContent()) {
            ReviewEditFragment_.builder().worksId(this.mWorks.id).build().setShowInterceptor(new ForcedLoginInterceptor()).showAsActivity((View) this);
        } else {
            ReviewDetailFragment_.builder().reviewId(this.mWorks.review.id).worksId(this.mWorks.id).build().showAsActivity((View) this);
        }
    }

    @Click({2131558897})
    void onDownloadClicked() {
        DownloadManager.scheduleDownload(ReaderUri.works(this.mWorksId));
    }

    public void setPage(int worksId, int page) {
        super.setPage(worksId, page);
        this.mWorksId = worksId;
        loadData(worksId);
    }

    public void onEventMainThread(WorksUpdatedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            loadData(this.mWorksId);
        }
    }

    @Background
    void loadData(int worksId) {
        try {
            this.mWorks = this.mWorksManager.getWorks(worksId);
            updateViews();
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    public boolean isDraggable() {
        return false;
    }

    @UiThread
    void updateViews() {
        int i = R.string.btn_purchase_whole_works;
        if (!this.mWorks.isColumnOrSerial() && this.mWorks.hasOwned && WorksData.get(this.mWorksId).isPartial()) {
            ViewUtils.visible(this.mBtnDownload, this.mDownloadInfo);
            this.mTitle.setText(R.string.last_page_view_title_sample_finished);
            ViewUtils.gone(this.mColumnPlanInfo, this.mTextSubscriptionInfo, this.mBtnSubscribe, this.mTextRatingTip, this.mRatingLayout, this.mTextPurchaseTip, this.mBtnPurchase);
            return;
        }
        boolean z;
        CharSequence purchaseBtnText;
        TextView textView = this.mTitle;
        int i2 = this.mWorks.isUncompletedColumnOrSerial() ? R.string.last_page_view_title_to_be_continued : (this.mWorks.isColumnOrSerial() || !WorksData.get(this.mWorksId).isPartial()) ? R.string.last_page_view_title_works_finished : R.string.last_page_view_title_sample_finished;
        textView.setText(i2);
        ViewUtils.showTextIf(this.mWorks.isUncompletedColumnOrSerial(), this.mColumnPlanInfo, this.mWorks.getWorksScheduleSummary());
        if (this.mWorks.isUncompletedColumnOrSerial()) {
            ViewUtils.showTextIf(this.mWorks.hasSubscribed, this.mTextSubscriptionInfo, Res.getString(R.string.last_page_view_message_subscribe_info, Integer.valueOf(this.mWorks.subscriptionCount)));
            ViewUtils.showTextIf(!this.mWorks.hasSubscribed, this.mBtnSubscribe, RichText.textWithIcon((int) R.drawable.v_subscribe, Res.getString(R.string.last_page_view_btn_subscribe_info, Integer.valueOf(this.mWorks.subscriptionCount))));
        } else {
            ViewUtils.gone(this.mTextSubscriptionInfo, this.mBtnSubscribe);
        }
        if ((!this.mWorks.isFree() || this.mWorks.isUncompletedColumnOrSerial()) && !this.mWorks.hasOwned && this.mWorks.isSalable) {
            z = false;
        } else {
            z = true;
        }
        ViewUtils.showIf(z, this.mTextRatingTip, this.mRatingLayout);
        this.mTextRatingTip.setText(hasRated() ? R.string.last_page_view_message_my_rate : R.string.last_page_view_message_rate);
        this.mRatingBar.setRating((float) getRating());
        this.mRatingBar.setIsIndicator(hasRated());
        if (this.mWorks.isFree() || this.mWorks.hasOwned || !this.mWorks.isSalable || this.mWorks.isUncompletedColumnOrSerial()) {
            z = false;
        } else {
            z = true;
        }
        ViewUtils.showIf(z, this.mTextPurchaseTip);
        if (this.mWorks.isFree() || this.mWorks.hasOwned || !this.mWorks.isSalable) {
            z = false;
        } else {
            z = true;
        }
        ViewUtils.showIf(z, this.mBtnPurchase);
        if (this.mWorks.price <= 0 || this.mWorks.promotion == null) {
            if (!this.mWorks.isColumnOrSerial()) {
                i = R.string.btn_purchase_works;
            }
            purchaseBtnText = Res.getString(i, Utils.formatPriceWithSymbol(this.mWorks.price));
        } else {
            if (this.mWorks.isColumnOrSerial()) {
                i2 = R.string.btn_purchase_whole_works;
            } else {
                i2 = R.string.btn_purchase_works;
            }
            purchaseBtnText = new SpannableStringBuilder(Res.getString(i2, Table.STRING_DEFAULT_VALUE)).append(this.mWorks.formatPrice());
        }
        this.mBtnPurchase.setText(RichText.textWithIcon((int) R.drawable.v_purchase, purchaseBtnText));
        this.mTextPurchaseTip.setText(this.mWorks.price > 0 ? R.string.last_page_view_message_purchase : R.string.last_page_view_msg_free_take);
        ViewUtils.gone(this.mBtnDownload, this.mDownloadInfo);
    }

    private boolean hasRated() {
        return (this.mWorks == null || this.mWorks.review == null || this.mWorks.review.rating <= 0) ? false : true;
    }

    private int getRating() {
        return hasRated() ? this.mWorks.review.rating : 0;
    }
}
