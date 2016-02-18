package com.douban.book.reader.content.chapter;

import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.exception.PagingException;

public abstract class PseudoChapter extends Chapter {
    public PseudoChapter(int worksId) {
        super(worksId);
    }

    protected ChapterIndexer createIndexer() {
        return new PseudoChapterIndexer(getWorksId(), getPackageId());
    }

    public void paging(PageMetrics pageMetrics) throws PagingException {
    }

    public int getPageCount() {
        return 1;
    }

    public void clearPageInfo() {
    }

    public void clearContents() {
    }

    public PageInfo getPageInfo(int page) {
        return createDefaultPageInfo();
    }

    public Paragraph getParagraph(int paragraphIndex) {
        return null;
    }

    public int getParagraphCount() {
        return 1;
    }

    public int getParagraphIndexByParagraphId(int paragraphId) {
        return 0;
    }

    public int getPageIndexByParagraphIndex(int paragraphIndex, int charOffset) {
        return 0;
    }
}
