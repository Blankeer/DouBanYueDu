package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.RedeemRecord;
import com.douban.book.reader.entity.RedeemRecord.Type;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.WorksCoverView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903159)
public class RedeemRecordItemView extends RelativeLayout implements ViewBinder<RedeemRecord> {
    @ViewById(2131558760)
    TextView mCount;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558758)
    TextView mTime;
    @ViewById(2131558546)
    TextView mWorksTitle;

    /* renamed from: com.douban.book.reader.view.item.RedeemRecordItemView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ RedeemRecord val$record;

        AnonymousClass1(RedeemRecord redeemRecord) {
            this.val$record = redeemRecord;
        }

        public void onClick(View v) {
            WorksProfileFragment_.builder().worksId(this.val$record.works.id).build().showAsActivity(RedeemRecordItemView.this);
        }
    }

    public RedeemRecordItemView(Context context) {
        super(context);
    }

    public RedeemRecordItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RedeemRecordItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
        ThemedUtils.updateView(this);
    }

    public void bindData(RedeemRecord record) {
        this.mTime.setText(DateUtils.formatDateTime(record.time));
        if (record.type == Type.WORKS) {
            this.mCount.setVisibility(8);
            this.mWorksTitle.setText(record.works.title);
            this.mCover.url(record.works.coverUrl);
        }
        if (record.type == Type.CASH) {
            this.mCount.setVisibility(0);
            this.mCount.setText(Utils.formatPriceWithSymbol(record.works.price));
            this.mWorksTitle.setText(R.string.title_money);
            this.mCover.setImageResource(R.drawable.ic_gift_s);
        }
        if (record.works != null) {
            setOnClickListener(new AnonymousClass1(record));
        }
    }
}
