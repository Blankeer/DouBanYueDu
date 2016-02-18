package com.douban.book.reader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.location.TocItem;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.view.item.NoteItemView;
import com.douban.book.reader.view.item.NoteItemView_;
import com.douban.book.reader.view.item.UgcItemHeaderView;
import com.douban.book.reader.view.item.UgcItemHeaderView_;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class NoteAdapter extends BaseArrayAdapter<Annotation> implements StickyListHeadersAdapter {
    private int mWorksId;

    public NoteAdapter(Context context, int worksId, List<Annotation> annotationList) {
        super(context, 0, annotationList);
        this.mWorksId = worksId;
    }

    public boolean isEnabled(int position) {
        return ((Annotation) getItem(position)).isRangeValid();
    }

    protected View newView() {
        return NoteItemView_.build(getContext());
    }

    protected void bindView(View itemView, Annotation item) {
        ((NoteItemView) itemView).setAnnotation(item);
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        UgcItemHeaderView headerView;
        if (convertView == null) {
            headerView = UgcItemHeaderView_.build(getContext());
        } else {
            UgcItemHeaderView_ headerView2 = (UgcItemHeaderView_) convertView;
        }
        if (StringUtils.isNotEmpty(Toc.get(this.mWorksId).getChapterTitle(((Annotation) getItem(position)).getRange().startPosition))) {
            headerView.setTitle(Toc.get(this.mWorksId).getChapterTitle(((Annotation) getItem(position)).getRange().startPosition));
            headerView.setVisibility(0);
        } else {
            headerView.setVisibility(8);
        }
        return headerView;
    }

    public long getHeaderId(int position) {
        TocItem tocItem = Toc.get(this.mWorksId).getChapterTocItem(((Annotation) getItem(position)).getPosition());
        if (tocItem != null) {
            return tocItem.getPosition().getActualPosition();
        }
        return 0;
    }
}
