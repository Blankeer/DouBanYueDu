package com.douban.book.reader.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;

public class ParagraphPreloadThread extends Thread {
    private static final int PARAGRAPH_OFFSCREEN_CACHE_RANGE = 3;
    private static final String TAG = "ParagraphPreloadThread";
    private int mBookId;
    private boolean mCacheLargeImage;
    private Handler mHandler;
    private int mLastPage;

    public ParagraphPreloadThread(int bookId) {
        this.mHandler = null;
        this.mLastPage = -1;
        this.mCacheLargeImage = false;
        this.mBookId = bookId;
    }

    public void setCacheLargeImage(boolean cacheLargeImage) {
        this.mCacheLargeImage = cacheLargeImage;
    }

    public void run() {
        Looper.prepare();
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                ParagraphPreloadThread.this.doPreload(msg.what);
            }
        };
        Looper.loop();
    }

    public void quit() {
        if (this.mHandler != null) {
            this.mHandler.getLooper().quit();
        }
    }

    public void setCurrentPage(int page) {
        if (this.mHandler != null) {
            this.mHandler.sendEmptyMessage(page);
        }
    }

    private void doPreload(int page) {
        try {
            Book book = Book.get(this.mBookId);
            for (int p = 2; p <= PARAGRAPH_OFFSCREEN_CACHE_RANGE; p++) {
                PageInfo pageInfo;
                int i;
                int chapter;
                Paragraph paragraph;
                int nextCache = page + p;
                int prevCache = page - p;
                if (nextCache >= 0) {
                    pageInfo = book.getPageInfo(nextCache);
                    if (pageInfo != null) {
                        for (i = pageInfo.startParaIndex; i <= pageInfo.endParaIndex; i++) {
                            chapter = book.getChapterIndexByPage(nextCache);
                            paragraph = book.getParagraph(chapter, i);
                            paragraph.generate();
                            if ((paragraph instanceof IllusParagraph) && this.mLastPage < page) {
                                cacheDrawable(book.getPackageId(chapter), ((IllusParagraph) paragraph).getIllusSeq());
                            }
                        }
                    }
                }
                if (prevCache >= 0) {
                    pageInfo = book.getPageInfo(prevCache);
                    if (pageInfo != null) {
                        for (i = pageInfo.startParaIndex; i <= pageInfo.endParaIndex; i++) {
                            chapter = book.getChapterIndexByPage(prevCache);
                            paragraph = book.getParagraph(chapter, i);
                            paragraph.generate();
                            if ((paragraph instanceof IllusParagraph) && this.mLastPage > page) {
                                cacheDrawable(book.getPackageId(chapter), ((IllusParagraph) paragraph).getIllusSeq());
                            }
                        }
                    }
                }
            }
            this.mLastPage = page;
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    private void cacheDrawable(int packageId, int seq) {
        ImageLoaderUtils.loadImageSkippingCache(ReaderUri.illus(this.mBookId, packageId, seq, this.mCacheLargeImage ? ImageSize.LARGE : ImageSize.NORMAL), null);
    }
}
