package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.location.TocItem;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903198)
public class TocItemView extends RelativeLayout implements ViewBinder<TocItem> {
    @ViewById(2131558548)
    TextView mAbstractText;
    @ViewById(2131558952)
    ImageView mIcLastRead;
    @ViewById(2131558951)
    View mNotPurchasedSign;
    @ViewById(2131558893)
    TextView mPublishDate;
    private boolean mReferToWorksData;
    private boolean mShouldShowNotPurchasedSign;
    @ViewById(2131558462)
    TextView mTitle;

    public TocItemView(Context context) {
        super(context);
        this.mShouldShowNotPurchasedSign = false;
        this.mReferToWorksData = true;
    }

    public TocItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShouldShowNotPurchasedSign = false;
        this.mReferToWorksData = true;
    }

    public TocItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mShouldShowNotPurchasedSign = false;
        this.mReferToWorksData = true;
    }

    @AfterViews
    void init() {
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        setGravity(16);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
    }

    public void setShouldShowNotPurchasedSign(boolean shouldShowNotPurchasedSign) {
        this.mShouldShowNotPurchasedSign = shouldShowNotPurchasedSign;
    }

    public void setReferToWorksData(boolean referToWorksData) {
        this.mReferToWorksData = referToWorksData;
    }

    public void bindData(TocItem tocItem) {
        boolean z;
        boolean purchaseNeeded = false;
        boolean enabled = true;
        boolean isLastRead = false;
        if (this.mReferToWorksData) {
            purchaseNeeded = tocItem.isPurchaseNeeded();
            enabled = tocItem.isPositionValid() && !purchaseNeeded;
            if (Toc.get(tocItem.worksId).getTocItemForReadingProgress() == tocItem) {
                isLastRead = true;
            } else {
                isLastRead = false;
            }
        }
        ViewUtils.enableIf(enabled, this.mTitle, this.mAbstractText, this.mPublishDate);
        this.mTitle.setText(tocItem.title);
        this.mTitle.setTypeface(enabled ? Font.SANS_SERIF_BOLD : Font.SANS_SERIF);
        ViewUtils.setLeftMargin(this.mTitle, Utils.dp2pixel((float) (Math.max(0, tocItem.level) * 20)));
        ViewUtils.showIf(isLastRead, this.mIcLastRead);
        if (tocItem.updateTime != null) {
            ViewUtils.showText(this.mPublishDate, DateUtils.formatDate(tocItem.updateTime));
        } else {
            ViewUtils.gone(this.mPublishDate);
        }
        ViewUtils.showTextIfNotEmpty(this.mAbstractText, tocItem.abstractText);
        if (this.mShouldShowNotPurchasedSign && purchaseNeeded) {
            z = true;
        } else {
            z = false;
        }
        ViewUtils.showIf(z, this.mNotPurchasedSign);
    }
}
