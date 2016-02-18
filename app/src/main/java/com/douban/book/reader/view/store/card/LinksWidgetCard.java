package com.douban.book.reader.view.store.card;

import android.content.Context;
import android.view.View;
import com.douban.book.reader.entity.store.LinksStoreWidgetEntity;
import com.douban.book.reader.entity.store.LinksStoreWidgetEntity.Link;
import com.douban.book.reader.view.store.LinkImageView;
import com.douban.book.reader.view.store.card.content.LinksWidgetContentView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class LinksWidgetCard extends BaseWidgetCard<LinksStoreWidgetEntity> {
    public LinksWidgetCard(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        noDivider();
    }

    protected void onEntityBound(LinksStoreWidgetEntity entity) {
        if (entity == null || entity.payload == null || entity.payload.links == null || entity.payload.links.isEmpty()) {
            hide();
            return;
        }
        View view;
        setVisibility(0);
        if (entity.payload.links.size() > 1) {
            view = new LinksWidgetContentView(getContext());
            ((LinksWidgetContentView) view).setData(entity.payload.links);
        } else {
            view = new LinkImageView(getContext());
            ((LinkImageView) view).setData((Link) entity.payload.links.get(0));
        }
        if (view != null) {
            content(view);
        }
    }
}
