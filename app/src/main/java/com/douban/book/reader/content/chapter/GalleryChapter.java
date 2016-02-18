package com.douban.book.reader.content.chapter;

import android.content.Context;
import android.text.TextUtils;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.page.ParagraphIterator;
import com.douban.book.reader.content.paragraph.ContainerParagraph;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.exception.ManifestException;
import com.douban.book.reader.exception.PagingException;
import com.douban.book.reader.util.AssertUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.view.page.AbsPageView;
import com.douban.book.reader.view.page.CatalogGalleryPageView;
import com.douban.book.reader.view.page.CenterGalleryPageView;
import com.douban.book.reader.view.page.NoLegendTopGalleryPageView;
import com.douban.book.reader.view.page.PureTextGalleryPageView;
import com.douban.book.reader.view.page.TextPageView;
import com.douban.book.reader.view.page.TopGalleryPageView;

public class GalleryChapter extends ContentChapter {
    private static final String TAG;

    static {
        TAG = GalleryChapter.class.getSimpleName();
    }

    public GalleryChapter(int bookId, int packageId) throws ManifestException {
        super(bookId, packageId);
    }

    public void onPaging(ParagraphIterator iterator, PageMetrics pageMetrics) throws PagingException, InterruptedException {
        int pageNo = 0;
        while (iterator.hasNext()) {
            AssertUtils.throwIfInterrupted(this);
            iterator.next();
            if (iterator.getParagraphIndex() >= 0) {
                PageInfo pageInfo = createDefaultPageInfo();
                pageInfo.startParaIndex = iterator.getParagraphIndex();
                pageInfo.startParaId = iterator.getParagraphId();
                pageInfo.startLine = 0;
                pageInfo.startOffset = 0;
                pageInfo.endParaIndex = pageInfo.startParaIndex;
                pageInfo.endParaId = iterator.getParagraphId();
                pageInfo.endLine = 0;
                pageInfo.endOffset = 0;
                int pageNo2 = pageNo + 1;
                appendPageInfo(pageNo, pageInfo);
                pageNo = pageNo2;
            }
        }
    }

    public AbsPageView getPageView(Context context, int pageNum) {
        try {
            int galleryMode;
            Book book = Book.get(getWorksId());
            Paragraph paragraph = book.getParagraph(book.getChapterIndexByPage(pageNum), book.getPageInfo(pageNum).startParaIndex);
            try {
                galleryMode = Manifest.get(getWorksId()).galleryMode;
            } catch (ManifestException e) {
                galleryMode = 2;
            }
            if (paragraph instanceof IllusParagraph) {
                if (galleryMode == 2) {
                    if (TextUtils.isEmpty(paragraph.getPrintableText())) {
                        return new NoLegendTopGalleryPageView(context);
                    }
                    return new TopGalleryPageView(context);
                } else if (galleryMode == 1) {
                    return new CenterGalleryPageView(context);
                } else {
                    return null;
                }
            } else if (!(paragraph instanceof ContainerParagraph)) {
                return null;
            } else {
                if (((ContainerParagraph) paragraph).isCatalog()) {
                    return new CatalogGalleryPageView(context);
                }
                return new PureTextGalleryPageView(context);
            }
        } catch (Exception e2) {
            Logger.e(TAG, e2);
            return new TextPageView(context);
        }
    }
}
