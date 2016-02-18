package com.douban.book.reader.content.chapter;

import android.content.Context;
import com.douban.book.reader.view.page.AbsPageView;
import com.douban.book.reader.view.page.ChapterPreviewPageView;
import com.douban.book.reader.view.page.ChapterPreviewPageView_;

public class PreviewChapter extends PseudoChapter {
    private int mPackageId;

    public PreviewChapter(int bookId, int packageId) {
        super(bookId);
        this.mPackageId = packageId;
    }

    public int getPackageId() {
        return this.mPackageId;
    }

    public AbsPageView getPageView(Context context, int pageNum) {
        ChapterPreviewPageView view = ChapterPreviewPageView_.build(context);
        view.setData(getWorksId(), this.mPackageId);
        return view;
    }
}
