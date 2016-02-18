package com.douban.book.reader.view.item;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.CouponRecord;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903119)
public class CouponRecordItemView extends RelativeLayout implements ViewBinder<CouponRecord> {
    @ViewById(2131558760)
    TextView mCount;
    @ViewById(2131558758)
    TextView mTime;

    public CouponRecordItemView(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
        ThemedUtils.updateView(this);
    }

    public void bindData(CouponRecord record) {
        this.mCount.setText(Utils.formatPriceWithSymbol(record.amount));
        this.mTime.setText(DateUtils.formatDateTime(record.createTime));
    }
}
