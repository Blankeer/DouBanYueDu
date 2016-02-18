package com.douban.book.reader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.entity.Bookmark;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.location.TocItem;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.item.UgcItemHeaderView;
import com.douban.book.reader.view.item.UgcItemHeaderView_;
import com.douban.book.reader.view.item.UgcItemView;
import com.douban.book.reader.view.item.UgcItemView_;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class BookmarkAdapter extends BaseArrayAdapter<Bookmark> implements StickyListHeadersAdapter {
    private int mWorksId;

    public BookmarkAdapter(Context context, int worksId, List<Bookmark> dataList) {
        super(context, 0, dataList);
        this.mWorksId = worksId;
    }

    public boolean isEnabled(int position) {
        return ((Bookmark) getItem(position)).isPositionValid();
    }

    protected View newView() {
        return UgcItemView_.build(getContext());
    }

    protected void bindView(View itemView, Bookmark item) {
        ((UgcItemView) itemView).setBookmark(item);
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        UgcItemHeaderView headerView;
        if (convertView == null) {
            headerView = UgcItemHeaderView_.build(getContext());
        } else {
            UgcItemHeaderView_ headerView2 = (UgcItemHeaderView_) convertView;
        }
        if (StringUtils.isNotEmpty(Toc.get(this.mWorksId).getChapterTitle(((Bookmark) getItem(position)).getPosition()))) {
            headerView.setTitle(Toc.get(this.mWorksId).getChapterTitle(((Bookmark) getItem(position)).getPosition()));
            headerView.setVisibility(0);
        } else {
            headerView.setVisibility(8);
        }
        return headerView;
    }

    public long getHeaderId(int position) {
        TocItem tocItem = Toc.get(this.mWorksId).getChapterTocItem(((Bookmark) getItem(position)).getPosition());
        if (tocItem != null) {
            return tocItem.getPosition().getActualPosition();
        }
        return 0;
    }
}
