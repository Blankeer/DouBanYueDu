package com.douban.book.reader.content.chapter;

import android.content.Context;
import com.douban.book.reader.view.page.AbsPageView;
import com.douban.book.reader.view.page.ChapterCorruptedPageView;
import com.douban.book.reader.view.page.ChapterCorruptedPageView_;

public class CorruptedChapter extends PseudoChapter {
    private int mPackageId;

    public CorruptedChapter(int bookId, int packageId) {
        super(bookId);
        this.mPackageId = packageId;
    }

    public int getPackageId() {
        return this.mPackageId;
    }

    public AbsPageView getPageView(Context context, int pageNum) {
        ChapterCorruptedPageView view = ChapterCorruptedPageView_.build(context);
        view.setData(getWorksId(), this.mPackageId);
        return view;
    }
}
