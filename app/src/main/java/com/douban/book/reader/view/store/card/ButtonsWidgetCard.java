package com.douban.book.reader.view.store.card;

import android.content.Context;
import com.douban.book.reader.entity.store.ButtonsStoreWidgetEntity;
import com.douban.book.reader.view.store.card.content.ButtonsWidgetContentView_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class ButtonsWidgetCard extends BaseWidgetCard<ButtonsStoreWidgetEntity> {
    public ButtonsWidgetCard(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        noHeader();
    }

    protected void onEntityBound(ButtonsStoreWidgetEntity entity) {
        if (entity == null || entity.payload == null || entity.payload.buttons == null || entity.payload.buttons.size() <= 0) {
            hide();
            return;
        }
        setVisibility(0);
        ((ButtonsWidgetContentView_) getOrCreateContentView(ButtonsWidgetContentView_.class)).setButtons(entity.payload.buttons);
    }
}
