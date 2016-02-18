package com.douban.book.reader.view.card;

import android.content.Context;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.WorksSummaryView_;

public class WorksSummaryCard extends Card<WorksSummaryCard> {
    public WorksSummaryCard(Context context) {
        super(context);
    }

    public WorksSummaryCard works(Works works) {
        View view = WorksSummaryView_.build(getContext());
        view.setWorks(works);
        super.content(view);
        ViewUtils.setVerticalPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.page_highlight_bg_color)).updateView();
        return this;
    }
}
