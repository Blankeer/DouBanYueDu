package com.douban.book.reader.content.chapter;

import android.content.Context;
import com.douban.book.reader.view.page.AbsPageView;
import com.douban.book.reader.view.page.GiftPageView_;

public class GiftChapter extends PseudoChapter {
    public GiftChapter(int worksId) {
        super(worksId);
    }

    public int getPackageId() {
        return Chapter.ID_GIFT;
    }

    public AbsPageView getPageView(Context context, int pageNum) {
        return GiftPageView_.build(context);
    }
}
