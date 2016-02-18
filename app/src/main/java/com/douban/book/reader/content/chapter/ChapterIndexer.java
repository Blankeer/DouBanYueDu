package com.douban.book.reader.content.chapter;

import com.douban.book.reader.content.Book;

public class ChapterIndexer {
    private int mPackageId;
    private int mWorksId;

    public static ChapterIndexer get(int worksId, int packageId) {
        return Book.get(worksId).getChapterById(packageId).getIndexer();
    }

    public ChapterIndexer(int worksId, int packageId) {
        this.mWorksId = worksId;
        this.mPackageId = packageId;
    }

    public int getIndexById(int paragraphId) {
        return Book.get(this.mWorksId).getChapterById(this.mPackageId).getParagraphIndexByParagraphId(paragraphId);
    }
}
