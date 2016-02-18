package com.douban.book.reader.content.chapter;

public class PseudoChapterIndexer extends ChapterIndexer {
    public PseudoChapterIndexer(int worksId, int packageId) {
        super(worksId, packageId);
    }

    public int getIndexById(int paragraphId) {
        return 0;
    }
}
