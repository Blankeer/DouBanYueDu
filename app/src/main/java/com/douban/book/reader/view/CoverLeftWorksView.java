package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.span.LabelSpan;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903154)
public class CoverLeftWorksView extends RelativeLayout implements ViewBinder<Works> {
    @ViewById(2131558548)
    TextView mAbstractText;
    @ViewById(2131558774)
    TextView mAuthor;
    @ViewById(2131558550)
    TextView mAverageRate;
    @ViewById(2131558772)
    WorksCoverView mCover;
    private boolean mNoHorizontalPadding;
    private boolean mNoVerticalPadding;
    @ViewById(2131558554)
    PriceLabelView mPrice;
    @ViewById(2131558549)
    RatingBar mRatingBar;
    @ViewById(2131558859)
    View mRatingLayout;
    @ViewById(2131558586)
    View mRootView;
    private boolean mShowPrice;
    @ViewById(2131558854)
    TextView mSubTitle;
    @ViewById(2131558462)
    TextView mTitle;

    /* renamed from: com.douban.book.reader.view.CoverLeftWorksView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works) {
            this.val$works = works;
        }

        public void onClick(View v) {
            WorksProfileFragment_.builder().worksId(this.val$works.id).build().showAsActivity(CoverLeftWorksView.this);
        }
    }

    public CoverLeftWorksView(Context context) {
        super(context);
        this.mNoVerticalPadding = false;
        this.mNoHorizontalPadding = false;
    }

    public CoverLeftWorksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNoVerticalPadding = false;
        this.mNoHorizontalPadding = false;
    }

    public CoverLeftWorksView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mNoVerticalPadding = false;
        this.mNoHorizontalPadding = false;
    }

    public CoverLeftWorksView noVerticalPadding() {
        this.mNoVerticalPadding = true;
        return this;
    }

    public CoverLeftWorksView noHorizontalPadding() {
        this.mNoHorizontalPadding = true;
        return this;
    }

    public void bindData(Works works) {
        int i;
        View view = this.mRootView;
        if (this.mNoVerticalPadding) {
            i = 0;
        } else {
            i = Res.getDimensionPixelSize(R.dimen.general_subview_vertical_padding_medium);
        }
        ViewUtils.setVerticalPadding(view, i);
        view = this.mRootView;
        if (this.mNoHorizontalPadding) {
            i = 0;
        } else {
            i = Res.getDimensionPixelSize(R.dimen.page_horizontal_padding);
        }
        ViewUtils.setHorizontalPadding(view, i);
        this.mCover.works(works);
        this.mTitle.setText(new RichText().appendIf(DebugSwitch.on(Key.APP_DEBUG_SHOW_BOOK_IDS), String.valueOf(works.id), Character.valueOf(Char.SPACE)).append(works.title).append((char) Char.SPACE).appendWithSpans(works.getWorksRootKindName(), new LabelSpan().backgroundColor(R.array.blue)));
        this.mAuthor.setText(Res.getString(R.string.title_author, works.author));
        ViewUtils.showTextIfNotEmpty(this.mSubTitle, works.subtitle);
        if (this.mShowPrice && works.isPromotion()) {
            this.mPrice.showPriceFor(works);
            this.mCover.noLabel();
        }
        this.mAbstractText.setText(works.abstractText);
        this.mRatingBar.setRating(works.averageRating);
        this.mAverageRate.setText(works.formatRatingInfo());
        setOnClickListener(new AnonymousClass1(works));
    }

    public void showAbstract() {
        this.mAbstractText.setVisibility(0);
    }

    public void showRatingInfo(boolean show) {
        this.mRatingLayout.setVisibility(show ? 0 : 8);
    }

    public void showPrice(boolean show) {
        this.mShowPrice = show;
        ViewUtils.showIf(show, this.mPrice);
    }
}
