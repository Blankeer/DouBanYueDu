package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.helper.WorksListUri.Display;
import com.douban.book.reader.helper.WorksListUri.Type;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903204)
public class WorksPriceView extends RelativeLayout {
    @ViewById(2131558539)
    PriceLabelView mPrice;
    @ViewById(2131558970)
    TextView mRemark;

    /* renamed from: com.douban.book.reader.view.WorksPriceView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Works val$works;

        AnonymousClass1(Works works) {
            this.val$works = works;
        }

        public void onClick(View v) {
            WorksListFragment_.builder().uri(WorksListUri.builder().type(Type.PROMOTION).id(this.val$works.promotion.event_id).display(Display.PRICE).build()).build().showAsActivity(WorksPriceView.this);
        }
    }

    public WorksPriceView(Context context) {
        super(context);
    }

    public WorksPriceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksPriceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void showPriceFor(Works works) {
        if (works != null) {
            this.mPrice.showStroke(false);
            this.mPrice.showPriceFor(works);
            ViewUtils.showIf(works.isPromotion(), this.mRemark);
            if (works.isPromotion()) {
                this.mRemark.setText(Res.getString(R.string.text_promotion_remark, DateUtils.formatDateTime(works.promotion.endTime), works.promotion.remark));
                setOnClickListener(new AnonymousClass1(works));
            }
        }
    }
}
