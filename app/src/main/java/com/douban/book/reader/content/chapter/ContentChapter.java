package com.douban.book.reader.content.chapter;

import android.support.v4.util.LruCache;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.page.ParagraphIterator;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.data.DataStore;
import com.douban.book.reader.entity.Manifest.PackMeta;
import com.douban.book.reader.exception.DataException;
import com.douban.book.reader.exception.ManifestException;
import com.douban.book.reader.exception.PagingException;
import com.douban.book.reader.manager.PageInfoManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.AssertUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Tag;
import java.util.Map;
import org.json.JSONObject;

public abstract class ContentChapter extends Chapter {
    private static final int PARAGRAPH_CACHE_SIZE = 512;
    protected int mPackageId;
    private PageInfoManager mPageInfoManager;
    private String mParaIndexMapName;
    private String mParaMapName;
    final LruCache<Integer, Paragraph> mParagraphCache;

    public abstract void onPaging(ParagraphIterator paragraphIterator, PageMetrics pageMetrics) throws PagingException, InterruptedException;

    public static Chapter create(int worksId, PackMeta packMeta) throws DataException {
        int packageId = packMeta.id;
        if (WorksData.get(worksId).getPackage(packageId).getStatus() != Status.READY) {
            return null;
        }
        try {
            if (WorksManager.getInstance().getWorks(worksId).isGallery()) {
                return new GalleryChapter(worksId, packageId);
            }
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
        return new TextChapter(worksId, packageId);
    }

    public ContentChapter(int bookId, int packageId) throws ManifestException {
        super(bookId);
        this.mParagraphCache = new LruCache(PARAGRAPH_CACHE_SIZE);
        this.mPackageId = packageId;
        this.mPageInfoManager = PageInfoManager.of(ReaderUri.pack(bookId, packageId));
        this.mParaMapName = String.format("para_map_%d", new Object[]{Integer.valueOf(this.mPackageId)});
        this.mParaIndexMapName = String.format("para_index_map_%d", new Object[]{Integer.valueOf(this.mPackageId)});
    }

    protected ChapterIndexer createIndexer() {
        return new ChapterIndexer(getWorksId(), getPackageId());
    }

    public Paragraph getParagraph(int paragraphIndex) {
        Paragraph paragraph = getFromParagraphCache(paragraphIndex);
        if (paragraph != null) {
            return paragraph;
        }
        try {
            paragraph = Paragraph.parse(new JSONObject((String) DataStore.ofWorks(getWorksId()).getTreeMap(this.mParaMapName).get(Integer.valueOf(paragraphIndex))));
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
        if (paragraph != null) {
            addToParagraphCache(paragraphIndex, paragraph);
        }
        return paragraph;
    }

    public int getPageCount() {
        return this.mPageInfoManager.getPageCount();
    }

    public PageInfo getPageInfo(int page) {
        PageInfo pageInfo = this.mPageInfoManager.getPageInfo(page);
        if (pageInfo == null) {
            return createDefaultPageInfo();
        }
        return pageInfo;
    }

    public int getPackageId() {
        return this.mPackageId;
    }

    public int getPageIndexByParagraphIndex(int paragraphIndex, int charOffset) {
        int start = 0;
        int end = this.mPageInfoManager.getPageCount() - 1;
        while (start <= end) {
            int middle = (start + end) / 2;
            PageInfo pageInfo = this.mPageInfoManager.getPageInfo(middle);
            if (pageInfo == null) {
                return -1;
            }
            if (paragraphIndex > pageInfo.endParaIndex || (paragraphIndex == pageInfo.endParaIndex && pageInfo.endOffset > 0 && charOffset > pageInfo.endOffset)) {
                if (start == middle) {
                    start++;
                } else {
                    start = middle;
                }
            } else if (paragraphIndex >= pageInfo.startParaIndex && (paragraphIndex != pageInfo.startParaIndex || charOffset >= pageInfo.startOffset)) {
                return middle;
            } else {
                if (end == middle) {
                    end--;
                } else {
                    end = middle;
                }
            }
        }
        return -1;
    }

    public int getParagraphIndexByParagraphId(int paragraphId) {
        if (paragraphId == 0) {
            return 0;
        }
        try {
            Integer index = (Integer) DataStore.ofWorks(getWorksId()).getTreeMap(this.mParaIndexMapName).get(Integer.valueOf(paragraphId));
            if (index != null) {
                return index.intValue();
            }
            return -1;
        } catch (Throwable th) {
            return -1;
        }
    }

    public void clearPageInfo() {
        this.mPageInfoManager.clear();
    }

    public synchronized void paging(PageMetrics pageMetrics) throws PagingException {
        Throwable e;
        Throwable th;
        Logger.t(Tag.PAGING, "------ start paging for %s", this);
        ParagraphIterator iterator = null;
        try {
            Map<Integer, String> paraMap = DataStore.ofWorks(getWorksId()).getTreeMap(this.mParaMapName);
            Map<Integer, Integer> paraIndexMap = DataStore.ofWorks(getWorksId()).getTreeMap(this.mParaIndexMapName);
            this.mPageInfoManager.setCurrentPageMetrics(pageMetrics);
            if (!this.mPageInfoManager.isPageMapValid() || paraMap.isEmpty() || paraIndexMap.isEmpty()) {
                ParagraphIterator iterator2 = new ParagraphIterator(getWorksId(), this.mPackageId, paraMap, paraIndexMap);
                try {
                    this.mPageInfoManager.markPagingSucceed(false);
                    clearPageInfo();
                    onPaging(iterator2, pageMetrics);
                    if (this.mPagingProgressListener != null) {
                        this.mPagingProgressListener.onNewPage();
                    }
                    AssertUtils.throwIfInterrupted(this);
                    this.mPageInfoManager.markPagingSucceed(true);
                    DataStore.ofWorks(getWorksId()).commit();
                    if (iterator2 != null) {
                        iterator2.close();
                    }
                    Logger.t(Tag.PAGING, "------ end paging for %s", this);
                    iterator = iterator2;
                } catch (Throwable th2) {
                    th = th2;
                    iterator = iterator2;
                    if (iterator != null) {
                        iterator.close();
                    }
                    Logger.t(Tag.PAGING, "------ end paging for %s", this);
                    throw th;
                }
            }
            if (this.mPagingProgressListener != null) {
                this.mPagingProgressListener.onNewPage();
            }
            if (iterator != null) {
                iterator.close();
            }
            Logger.t(Tag.PAGING, "------ end paging for %s", this);
        } catch (Throwable th3) {
            e = th3;
            Logger.e(Tag.PAGING, e, "------ error occurred while paging for %s", this);
            this.mPageInfoManager.clear();
            DataStore.ofWorks(getWorksId()).commit();
            throw new PagingException(e);
        }
    }

    public void clearContents() {
        this.mParagraphCache.evictAll();
    }

    public int getParagraphCount() {
        return DataStore.ofWorks(getWorksId()).getTreeMap(this.mParaMapName).size();
    }

    protected void addToParagraphCache(int paragraphIndex, Paragraph paragraph) {
        synchronized (this.mParagraphCache) {
            this.mParagraphCache.put(Integer.valueOf(paragraphIndex), paragraph);
        }
    }

    protected void appendPageInfo(int pageNo, PageInfo pageInfo) {
        this.mPageInfoManager.addPageInfo(pageNo, pageInfo);
        if (this.mPagingProgressListener == null) {
            return;
        }
        if (pageNo == 5 || pageNo % 50 == 0) {
            Logger.d(TAG, "onNewPage for %s, pageNo = %d", this, Integer.valueOf(pageNo));
            this.mPagingProgressListener.onNewPage();
        }
    }

    private Paragraph getFromParagraphCache(int paragraphIndex) {
        Paragraph result;
        synchronized (this.mParagraphCache) {
            result = (Paragraph) this.mParagraphCache.get(Integer.valueOf(paragraphIndex));
        }
        return result;
    }
}
