package com.douban.book.reader.content.chapter;

import android.content.Context;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.exception.PagingException;
import com.douban.book.reader.view.page.AbsPageView;

public abstract class Chapter {
    public static final int ID_COVER = -100;
    public static final int ID_GIFT = -101;
    public static final int ID_META = -103;
    public static final int ID_PSEUDO_CHAPTER_MAX = -100;
    public static final int ID_PSEUDO_CHAPTER_MIN = -103;
    public static final int ID_PURCHASE = -102;
    public static final int PACKAGE_INDEX_NOT_FOUND = -1;
    public static final int PAGE_INDEX_NOT_FOUND = -1;
    public static final int PARAGRAPH_INDEX_NOT_FOUND = -1;
    protected static final String TAG;
    private ChapterIndexer mIndexer;
    protected PagingProgressListener mPagingProgressListener;
    private int mWorksId;

    public interface PagingProgressListener {
        void onNewPage();
    }

    public abstract void clearContents();

    public abstract void clearPageInfo();

    protected abstract ChapterIndexer createIndexer();

    public abstract int getPackageId();

    public abstract int getPageCount();

    public abstract int getPageIndexByParagraphIndex(int i, int i2);

    public abstract PageInfo getPageInfo(int i);

    public abstract AbsPageView getPageView(Context context, int i);

    public abstract Paragraph getParagraph(int i);

    public abstract int getParagraphCount();

    public abstract int getParagraphIndexByParagraphId(int i);

    public abstract void paging(PageMetrics pageMetrics) throws PagingException;

    static {
        TAG = Chapter.class.getSimpleName();
    }

    public static boolean isValidPseudoChapterId(int chapterId) {
        return chapterId >= ID_PSEUDO_CHAPTER_MIN && chapterId <= ID_PSEUDO_CHAPTER_MAX;
    }

    public Chapter(int worksId) {
        this.mIndexer = null;
        this.mWorksId = worksId;
    }

    protected int getWorksId() {
        return this.mWorksId;
    }

    public ChapterIndexer getIndexer() {
        if (this.mIndexer == null) {
            this.mIndexer = createIndexer();
        }
        return this.mIndexer;
    }

    public void setPagingProgressListener(PagingProgressListener listener) {
        this.mPagingProgressListener = listener;
    }

    protected PageInfo createDefaultPageInfo() {
        return new PageInfo(getWorksId(), getPackageId());
    }

    public String toString() {
        try {
            return String.format("%s@0x%x [book://%s/package/%s]", new Object[]{getClass().getSimpleName(), Integer.valueOf(hashCode()), Integer.valueOf(getWorksId()), Integer.valueOf(getPackageId())});
        } catch (Throwable e) {
            return e.toString();
        }
    }
}
