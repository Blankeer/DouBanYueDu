package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.douban.book.reader.entity.store.LinksStoreWidgetEntity.Link;
import com.douban.book.reader.view.store.IndicatedViewPager;
import com.douban.book.reader.view.store.LinkImageView;
import java.util.List;

public class LinksWidgetContentView extends IndicatedViewPager {
    private List<Link> mLinks;

    public LinksWidgetContentView(Context context) {
        super(context);
        init();
    }

    public LinksWidgetContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinksWidgetContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setAutoFlipEnabled();
        overlayIndicator();
        setHighlightMode();
    }

    public void setData(List<Link> links) {
        this.mLinks = links;
        populateData();
    }

    protected int getPageCount() {
        return this.mLinks != null ? this.mLinks.size() : 0;
    }

    protected View getPageView(int page) {
        LinkImageView view = new LinkImageView(getContext());
        Link link = getLink(page);
        if (link != null) {
            view.setData(link);
        }
        return view;
    }

    private Link getLink(int page) {
        try {
            return (Link) this.mLinks.get(page);
        } catch (Throwable th) {
            return null;
        }
    }
}
