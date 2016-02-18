package com.douban.book.reader.content;

import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.chapter.Chapter;
import com.douban.book.reader.content.chapter.Chapter.PagingProgressListener;
import com.douban.book.reader.content.chapter.ContentChapter;
import com.douban.book.reader.content.chapter.CorruptedChapter;
import com.douban.book.reader.content.chapter.GiftChapter;
import com.douban.book.reader.content.chapter.LastPageChapter;
import com.douban.book.reader.content.chapter.MetaChapter;
import com.douban.book.reader.content.chapter.PreviewChapter;
import com.douban.book.reader.content.chapter.TextChapter;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.ContainerParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.data.DataStore;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Manifest.PackMeta;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.exception.DataException;
import com.douban.book.reader.exception.PagingException;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReflectionUtils;
import com.douban.book.reader.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Book {
    private static final String TAG = "Book";
    private static final SparseArray<Book> hBookInstances;
    private static final Object mOpenRefLock;
    private int mBookId;
    private List<Chapter> mChapterArray;
    private DataStore mDataStore;
    private int mOpenRefCount;
    private Toc mToc;

    public enum ImageSize {
        NORMAL,
        LARGE
    }

    static {
        mOpenRefLock = new Object();
        hBookInstances = new SparseArray();
    }

    public static Book get(int bookId) {
        Book book;
        synchronized (hBookInstances) {
            book = (Book) hBookInstances.get(bookId);
            if (book == null) {
                book = new Book(bookId);
                hBookInstances.put(bookId, book);
            }
        }
        return book;
    }

    public static void clearInstances() {
        hBookInstances.clear();
    }

    public Book(int bookId) {
        this.mChapterArray = new CopyOnWriteArrayList();
        this.mOpenRefCount = 0;
        this.mToc = null;
        this.mDataStore = null;
        this.mBookId = bookId;
    }

    public String toString() {
        try {
            return String.format("Book: %s", new Object[]{Integer.valueOf(this.mBookId)});
        } catch (Throwable e) {
            return e.toString();
        }
    }

    public Toc getToc() {
        if (this.mToc == null) {
            this.mToc = new Toc(this.mBookId);
        }
        return this.mToc;
    }

    public void initChapters() throws DataException {
        Manifest manifest = Manifest.load(this.mBookId);
        try {
            Works works = WorksManager.getInstance().getWorks(this.mBookId);
            List<Chapter> tempArray = new ArrayList();
            tempArray.add(new MetaChapter(this.mBookId));
            if (works.isGift()) {
                tempArray.add(new GiftChapter(this.mBookId));
            }
            for (PackMeta packMeta : manifest.packages) {
                Chapter chapter = ContentChapter.create(this.mBookId, packMeta);
                if (chapter == null) {
                    if (works.isColumnOrSerial()) {
                        chapter = new PreviewChapter(this.mBookId, packMeta.id);
                    } else {
                        chapter = new CorruptedChapter(this.mBookId, packMeta.id);
                    }
                }
                if (!(chapter instanceof PreviewChapter) || works.isColumnOrSerial()) {
                    tempArray.add(chapter);
                }
            }
            tempArray.add(new LastPageChapter(this.mBookId));
            synchronized (this) {
                this.mChapterArray.clear();
                this.mChapterArray.addAll(tempArray);
            }
        } catch (Throwable e) {
            throw new DataException(e);
        }
    }

    public Chapter getChapterByIndex(int index) {
        try {
            return (Chapter) this.mChapterArray.get(index);
        } catch (IndexOutOfBoundsException e) {
            Logger.e(TAG, e);
            return null;
        }
    }

    public Chapter getChapterByType(Class<? extends Chapter> type) {
        for (Object chapter : this.mChapterArray) {
            if (ReflectionUtils.isInstanceOf(chapter, (Class) type)) {
                return chapter;
            }
        }
        return null;
    }

    public int getChapterIndex(int packageId) {
        for (Chapter chapter : this.mChapterArray) {
            if (chapter.getPackageId() == packageId) {
                return this.mChapterArray.indexOf(chapter);
            }
        }
        return -1;
    }

    public int getChapterIndex(Chapter chapter) {
        int index = this.mChapterArray.indexOf(chapter);
        if (index < 0) {
            return -1;
        }
        return index;
    }

    public int getChapterCount() {
        return this.mChapterArray.size();
    }

    public Chapter getChapterById(int packageId) {
        for (Chapter chapter : this.mChapterArray) {
            if (chapter.getPackageId() == packageId) {
                return chapter;
            }
        }
        return null;
    }

    public int getPackageId(int chapterIndex) {
        Chapter chapter = getChapterByIndex(chapterIndex);
        if (chapter != null) {
            return chapter.getPackageId();
        }
        return 0;
    }

    public int getBookId() {
        return this.mBookId;
    }

    public void paging(PageMetrics pageMetrics, PagingProgressListener listener) throws PagingException {
        try {
            for (Chapter chapter : this.mChapterArray) {
                chapter.setPagingProgressListener(listener);
                chapter.paging(pageMetrics);
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException(String.format("paging for %s was interrupted.", new Object[]{this}));
                }
            }
            getToc().invalidatePositions();
        } catch (Throwable e) {
            PagingException pagingException = new PagingException(e);
        }
    }

    public void openBook() throws DataException {
        synchronized (mOpenRefLock) {
            if (this.mOpenRefCount <= 0) {
                openDataStore();
                Logger.dc(TAG, "openBook do the real thing for %s", this);
                initChapters();
            }
            this.mOpenRefCount++;
            Logger.dc(TAG, "openBook (refCount=%d) %s", Integer.valueOf(this.mOpenRefCount), this);
        }
    }

    public void clearContents() {
        int count = this.mChapterArray.size();
        for (int i = 0; i < count; i++) {
            ((Chapter) this.mChapterArray.get(i)).clearContents();
        }
        this.mChapterArray.clear();
    }

    public static void clearCacheData(int bookId) {
        get(bookId).closeDataStore();
        FileUtils.deleteDir(FilePath.worksCache(bookId));
        Logger.dc(TAG, "cleared DataStore for %s", Integer.valueOf(bookId));
    }

    private synchronized void openDataStore() {
        if (this.mDataStore == null) {
            this.mDataStore = new DataStore(FilePath.worksMapDb(this.mBookId));
            Logger.dc(TAG, "opened DataStore for %s", this);
        }
    }

    private synchronized void closeDataStore() {
        if (this.mDataStore != null) {
            this.mDataStore.close();
            this.mDataStore = null;
            Logger.dc(TAG, "closed DataStore for %s", this);
        }
    }

    public synchronized DataStore getDataStore() {
        if (this.mOpenRefCount <= 0) {
            throw new IllegalStateException(String.format("failed to get DataStore for %s: Book not open", new Object[]{this}));
        }
        openDataStore();
        return this.mDataStore;
    }

    public void closeBook() {
        synchronized (mOpenRefLock) {
            this.mOpenRefCount--;
            Logger.dc(TAG, "closeBook (refCount=%d) %s", Integer.valueOf(this.mOpenRefCount), this);
            if (this.mOpenRefCount <= 0) {
                Logger.dc(TAG, "closeBook do the real thing for %s", this);
                SelectionManager_.getInstance_(App.get()).clearSelection();
                clearContents();
                closeDataStore();
                AnnotationManager.ofWorks(this.mBookId).setActiveNote(null);
                this.mOpenRefCount = 0;
            }
        }
    }

    public int getChapterIndexByPage(int page) {
        for (int i = 0; i < this.mChapterArray.size(); i++) {
            int pageCount = ((Chapter) this.mChapterArray.get(i)).getPageCount();
            if (page < pageCount) {
                return i;
            }
            page -= pageCount;
        }
        return 0;
    }

    @Nullable
    public Chapter getChapterByPage(int pageNum) {
        try {
            return (Chapter) this.mChapterArray.get(getChapterIndexByPage(pageNum));
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getChapterIdByPage(int pageNum) {
        Chapter chapter = getChapterByPage(pageNum);
        if (chapter == null) {
            return 0;
        }
        return chapter.getPackageId();
    }

    public int getPageIndexInBook(int chapterIndex, int pageInChapter) {
        if (pageInChapter < 0) {
            return -1;
        }
        int page = 0;
        for (int i = 0; i < chapterIndex; i++) {
            page += ((Chapter) this.mChapterArray.get(i)).getPageCount();
        }
        return page + pageInChapter;
    }

    public int getPageIndexInBook(Chapter chapter) {
        int page = 0;
        for (Chapter c : this.mChapterArray) {
            if (c == chapter) {
                break;
            }
            page += c.getPageCount();
        }
        return page;
    }

    public PageInfo getPageInfo(int page) {
        int curPage = page;
        for (int i = 0; i < this.mChapterArray.size(); i++) {
            Chapter chapter = (Chapter) this.mChapterArray.get(i);
            int pageCount = chapter.getPageCount();
            if (curPage < pageCount) {
                return chapter.getPageInfo(curPage);
            }
            curPage -= pageCount;
        }
        return new PageInfo(this.mBookId, -100);
    }

    public int getPageCount() {
        int result = 0;
        for (Chapter chapter : this.mChapterArray) {
            result += chapter.getPageCount();
        }
        return result;
    }

    public int getBackCoverPageCount() {
        int count = 0;
        for (int i = this.mChapterArray.size() - 1; i >= 0; i--) {
            Chapter chapter = (Chapter) this.mChapterArray.get(i);
            if (!Chapter.isValidPseudoChapterId(chapter.getPackageId())) {
                break;
            }
            count += chapter.getPageCount();
        }
        return count;
    }

    @Nullable
    public Paragraph getParagraph(Position position) {
        return getParagraphByIndex(position.packageIndex, position.paragraphIndex);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.douban.book.reader.content.page.Position getNextReadableParagraphPosition(com.douban.book.reader.content.page.Position r4) {
        /*
        r3 = this;
        r2 = com.douban.book.reader.content.page.Position.isValid(r4);
        if (r2 != 0) goto L_0x0007;
    L_0x0006:
        return r4;
    L_0x0007:
        r1 = new com.douban.book.reader.content.page.Position;
        r1.<init>(r4);
        r0 = r3.getParagraph(r1);
    L_0x0010:
        r2 = r1.paragraphOffset;
        if (r2 > 0) goto L_0x0020;
    L_0x0014:
        if (r0 == 0) goto L_0x0036;
    L_0x0016:
        r2 = r0.getPrintableText();
        r2 = com.douban.book.reader.util.StringUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x0036;
    L_0x0020:
        r2 = r1.paragraphIndex;
        r2 = r2 + 1;
        r1.paragraphIndex = r2;
        r2 = 0;
        r1.paragraphOffset = r2;
        r0 = r3.getParagraph(r1);
        if (r0 == 0) goto L_0x0010;
    L_0x002f:
        r2 = r0.getId();
        r1.paragraphId = r2;
        goto L_0x0010;
    L_0x0036:
        r4 = r1;
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.content.Book.getNextReadableParagraphPosition(com.douban.book.reader.content.page.Position):com.douban.book.reader.content.page.Position");
    }

    public Position getPrevPosition(Position position) {
        if (!Position.isValid(position)) {
            return position;
        }
        Position target = new Position(position);
        target.paragraphOffset--;
        if (position.paragraphOffset < 0) {
            Paragraph paragraph;
            do {
                target.paragraphIndex--;
                paragraph = getParagraphByIndex(target.packageIndex, target.paragraphIndex);
                if (paragraph == null) {
                    break;
                }
                target.paragraphId = paragraph.getId();
                target.paragraphOffset = paragraph.getText().length() - 1;
            } while (StringUtils.isEmpty(paragraph.getText()));
        }
        return target;
    }

    public Position getNextPosition(Position position) {
        if (!Position.isValid(position)) {
            return position;
        }
        Position target = new Position(position);
        Paragraph paragraph = getParagraph(position);
        target.paragraphOffset++;
        if (paragraph == null || position.paragraphOffset >= paragraph.getText().length()) {
            do {
                target.paragraphIndex++;
                target.paragraphOffset = 0;
                paragraph = getParagraphByIndex(target.packageIndex, target.paragraphIndex);
                if (paragraph == null) {
                    break;
                }
                target.paragraphId = paragraph.getId();
            } while (StringUtils.isEmpty(paragraph.getText()));
        }
        return target;
    }

    @Nullable
    public Paragraph getParagraphByIndex(int chapterIndex, int index) {
        return getChapterByIndex(chapterIndex).getParagraph(index);
    }

    @Deprecated
    public Paragraph getParagraph(int chapterIndex, int index) {
        Paragraph result = null;
        try {
            result = ((Chapter) this.mChapterArray.get(chapterIndex)).getParagraph(index);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        if (result == null) {
            return new ContainerParagraph();
        }
        return result;
    }

    public Position getPositionForPage(int page) {
        PageInfo pageInfo = getPageInfo(page);
        int chapterIndex = getChapterIndexByPage(page);
        Paragraph paragraph = getParagraph(chapterIndex, pageInfo.startParaIndex);
        Position position = new Position();
        position.packageId = getPackageId(chapterIndex);
        position.paragraphId = paragraph.getId();
        position.packageIndex = chapterIndex;
        position.paragraphIndex = pageInfo.startParaIndex;
        position.paragraphOffset = pageInfo.startOffset;
        return position;
    }

    public int getPageForPosition(Position position) {
        if (position == null || !position.isValid()) {
            return -1;
        }
        Chapter chapter = getChapterByIndex(position.packageIndex);
        if (chapter == null) {
            return -1;
        }
        return getPageIndexInBook(position.packageIndex, chapter.getPageIndexByParagraphIndex(position.paragraphIndex, position.paragraphOffset));
    }

    @Nullable
    public Position getPositionForChapter(Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        Position position = new Position();
        position.packageId = chapter.getPackageId();
        position.packageIndex = getChapterIndex(chapter);
        position.paragraphId = 0;
        position.paragraphIndex = 0;
        position.paragraphOffset = 0;
        return position;
    }

    public Position getPositionForChapter(int chapterId) {
        return getPositionForChapter(getChapterById(chapterId));
    }

    public CharSequence getText(Range range) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        if (range.isValid()) {
            for (int packageIndex = range.startPosition.packageIndex; packageIndex <= range.endPosition.packageIndex; packageIndex++) {
                Chapter chapter = getChapterByIndex(packageIndex);
                if (chapter instanceof TextChapter) {
                    text.append(((TextChapter) chapter).getText(range));
                }
            }
        }
        return text;
    }
}
