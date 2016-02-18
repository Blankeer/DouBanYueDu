package com.douban.book.reader.view.store.card;

import android.content.Context;
import com.douban.book.reader.entity.store.LinkStoreWidgetEntity;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.store.LinkImageView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

@EViewGroup
public class LinkWidgetCard extends BaseWidgetCard<LinkStoreWidgetEntity> {
    private LinkImageView mLinkImageView;

    public LinkWidgetCard(Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        noDivider();
        this.mLinkImageView = new LinkImageView(getContext());
        content(this.mLinkImageView);
    }

    protected void onEntityBound(LinkStoreWidgetEntity entity) {
        if (!(entity == null || entity.payload == null)) {
            if (StringUtils.isNotEmpty(entity.payload.img)) {
                setVisibility(0);
                this.mLinkImageView.setData(entity.payload);
                return;
            }
        }
        hide();
    }
}
