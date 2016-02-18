package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.store.IndicatedViewPager;
import com.douban.book.reader.view.store.WorksGridView;
import com.douban.book.reader.view.store.WorksGridView_;
import java.util.List;

public class BannerWidgetContentView extends IndicatedViewPager {
    private static final int WORKS_PER_PAGE = 3;
    private int mPageCount;
    private List<Works> mWorksList;

    public BannerWidgetContentView(Context context) {
        super(context);
    }

    public BannerWidgetContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerWidgetContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWorksList(List<Works> worksList) {
        if (worksList != null) {
            this.mWorksList = worksList;
            this.mPageCount = (int) Math.ceil(((double) this.mWorksList.size()) / 3.0d);
            populateData();
        }
    }

    protected int getPageCount() {
        return this.mPageCount;
    }

    protected View getPageView(int page) {
        WorksGridView view = WorksGridView_.build(getContext());
        view.setWorksList(getPageWorksList(page));
        ViewUtils.setTopPaddingResId(view, R.dimen.general_subview_vertical_padding_medium);
        return view;
    }

    private List<Works> getPageWorksList(int page) {
        int start = page * WORKS_PER_PAGE;
        return this.mWorksList.subList(start, Math.min(start + WORKS_PER_PAGE, this.mWorksList.size()));
    }
}
