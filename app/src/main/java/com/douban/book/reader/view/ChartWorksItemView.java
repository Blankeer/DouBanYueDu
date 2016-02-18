package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903153)
public class ChartWorksItemView extends LinearLayout implements ViewBinder<Works> {
    @ViewById(2131558857)
    TextView mRankNum;
    @ViewById(2131558858)
    CoverLeftWorksView_ mWorksView;

    public ChartWorksItemView(Context context) {
        super(context);
    }

    public ChartWorksItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartWorksItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setHorizontalPadding(this, Res.getDimensionPixelSize(R.dimen.page_horizontal_padding));
    }

    public void bindData(Works works) {
        this.mWorksView.noHorizontalPadding();
        this.mWorksView.bindData(works);
        this.mWorksView.showAbstract();
        this.mWorksView.showRatingInfo(true);
    }

    public void setRankNum(int rankNum) {
        this.mRankNum.setText(Res.getString(R.string.title_rank_num, Integer.valueOf(rankNum)));
    }
}
