package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.PurchaseRecord;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.WorksCoverView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903159)
public class PurchaseRecordItemView extends RelativeLayout implements ViewBinder<PurchaseRecord> {
    @ViewById(2131558760)
    TextView mCount;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558758)
    TextView mTime;
    @ViewById(2131558546)
    TextView mWorksTitle;

    public PurchaseRecordItemView(Context context) {
        super(context);
    }

    public PurchaseRecordItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PurchaseRecordItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setHorizontalPaddingResId(this, R.dimen.page_horizontal_padding);
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ThemedAttrs.ofView(this).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_list_item));
        ThemedUtils.updateView(this);
    }

    public void bindData(PurchaseRecord record) {
        this.mWorksTitle.setText(record.target.title);
        String str = DateUtils.formatDate(record.time);
        if (record.recipient != null) {
            if (StringUtils.isNotEmpty(record.recipient.name)) {
                str = Res.getString(R.string.msg_bought_time_and_recipient, str, record.recipient.name);
            }
        }
        this.mTime.setText(str);
        this.mCount.setText(Utils.formatPriceWithSymbol(record.amount));
        this.mCover.url(record.target.cover);
    }
}
