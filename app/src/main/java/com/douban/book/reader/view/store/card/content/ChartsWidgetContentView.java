package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.store.ChartsStoreWidgetEntity.Chart;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.view.store.item.ChartItemView;
import com.douban.book.reader.view.store.item.ChartItemView_;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class ChartsWidgetContentView extends LinearLayout {
    public ChartsWidgetContentView(Context context) {
        super(context);
    }

    public ChartsWidgetContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartsWidgetContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        setOrientation(0);
        setDividerPadding(1);
        setDividerDrawable(Res.getDrawable(R.drawable.divider_transparent));
        setShowDividers(2);
    }

    public void setChartList(List<Chart> chartList) {
        for (int i = 0; i < chartList.size(); i++) {
            Chart rank = (Chart) chartList.get(i);
            getOrCreateChartItemView(i).setChart(rank, chartList.indexOf(rank) % chartList.size());
        }
    }

    private ChartItemView getOrCreateChartItemView(int index) {
        View view;
        if (getChildCount() > index) {
            view = getChildAt(index);
        } else {
            view = ChartItemView_.build(App.get());
            addView(view, new LayoutParams(-1, -1, 1.0f));
        }
        return (ChartItemView) view;
    }
}
