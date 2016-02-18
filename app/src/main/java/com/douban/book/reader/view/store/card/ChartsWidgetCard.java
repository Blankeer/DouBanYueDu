package com.douban.book.reader.view.store.card;

import android.content.Context;
import com.douban.book.reader.entity.store.ChartsStoreWidgetEntity;
import com.douban.book.reader.view.store.card.content.ChartsWidgetContentView_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class ChartsWidgetCard extends BaseWidgetCard<ChartsStoreWidgetEntity> {
    public ChartsWidgetCard(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        noHeader();
        noDivider();
    }

    protected void onEntityBound(ChartsStoreWidgetEntity entity) {
        if (entity == null || entity.payload == null || entity.payload.charts == null || entity.payload.charts.size() <= 0) {
            hide();
            return;
        }
        setVisibility(0);
        ((ChartsWidgetContentView_) getOrCreateContentView(ChartsWidgetContentView_.class)).setChartList(entity.payload.charts);
    }
}
