package com.douban.book.reader.view.store.card;

import android.content.Context;
import com.douban.book.reader.entity.store.BannerStoreWidgetEntity;
import com.douban.book.reader.view.store.card.content.BannerWidgetContentView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class BannerWidgetCard extends BaseWidgetCard<BannerStoreWidgetEntity> {
    public BannerWidgetCard(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        noDivider();
        noTitle();
    }

    protected void onEntityBound(BannerStoreWidgetEntity entity) {
        if (entity == null || entity.payload == null || entity.payload.worksList == null || entity.payload.worksList.isEmpty()) {
            hide();
            return;
        }
        setVisibility(0);
        ((BannerWidgetContentView) getOrCreateContentView(BannerWidgetContentView.class)).setWorksList(entity.payload.worksList);
    }
}
