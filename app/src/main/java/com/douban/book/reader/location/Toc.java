package com.douban.book.reader.location;

import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.content.paragraph.RichTextParagraph;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.ProgressManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;

public class Toc {
    private static final int LEVEL_ALL = Integer.MAX_VALUE;
    private static final String TAG;
    private List<TocItem> mTocItems;
    private int mWorksId;
    private WorksManager mWorksManager;

    static {
        TAG = Toc.class.getSimpleName();
    }

    public static Toc get(int worksId) {
        return Book.get(worksId).getToc();
    }

    public Toc(int worksId) {
        this.mTocItems = null;
        this.mWorksManager = WorksManager.getInstance();
        this.mWorksId = worksId;
        loadFromManifest(worksId);
    }

    public List<TocItem> getTocList() {
        return this.mTocItems;
    }

    public List<Paragraph> getTocParagraphList() {
        List<Paragraph> paragraphList = new ArrayList();
        for (TocItem tocItem : this.mTocItems) {
            Paragraph paragraph = new RichTextParagraph();
            paragraph.setText(tocItem.title);
            paragraph.setBaseLeftMargin(0.0f);
            paragraph.setBlockIndentRatio((float) tocItem.level);
            paragraph.setIsBulletItem(true);
            paragraphList.add(paragraph);
        }
        return paragraphList;
    }

    public int getTocItemCount() {
        return this.mTocItems.size();
    }

    public String getTitleForPage(int page) {
        TocItem tocItem = getTocItemForPosition(Book.get(this.mWorksId).getPageInfo(page).getRange().endPosition, LEVEL_ALL);
        if (tocItem != null) {
            return tocItem.title;
        }
        try {
            return WorksManager.getInstance().getWorks(this.mWorksId).title;
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
            return getWorksTitle();
        }
    }

    public TocItem getChapterTocItem(Position position) {
        return getTocItemForPosition(position, 0);
    }

    public String getChapterTitle(Position position) {
        TocItem tocItem = getTocItemForPosition(position, 0);
        if (tocItem != null) {
            return tocItem.title;
        }
        return getWorksTitle();
    }

    public TocItem getTocItemForReadingProgress() {
        return getTocItemForPosition(ProgressManager.ofWorks(this.mWorksId).getLocalProgress().getPosition(), LEVEL_ALL);
    }

    public List<Integer> getTocPageArray() {
        Book book = Book.get(this.mWorksId);
        List<Integer> result = new ArrayList();
        for (TocItem tocItem : this.mTocItems) {
            int page = book.getPageForPosition(tocItem.getPosition());
            if (page != -1) {
                result.add(Integer.valueOf(page));
            }
        }
        return result;
    }

    public void invalidatePositions() {
        for (TocItem tocItem : this.mTocItems) {
            tocItem.invalidatePosition();
        }
    }

    private String getWorksTitle() {
        try {
            Works works = this.mWorksManager.getWorks(this.mWorksId);
            if (works != null) {
                return works.title;
            }
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    private TocItem getTocItemForPosition(Position position, int maxLevel) {
        TocItem result = null;
        for (TocItem tocItem : this.mTocItems) {
            Position tocPosition = tocItem.getPosition();
            if (tocItem.level <= maxLevel && Position.isValid(tocPosition)) {
                if (tocPosition.compareTo(position) > 0) {
                    break;
                }
                result = tocItem;
            }
        }
        return result;
    }

    private void loadFromManifest(int worksId) {
        try {
            this.mTocItems = Manifest.get(worksId).toc;
            for (TocItem tocItem : this.mTocItems) {
                tocItem.worksId = worksId;
            }
        } catch (Exception e) {
            Logger.e(TAG, e, "Failed to load Toc from Manifest. worksId=%s", Integer.valueOf(worksId));
            loadFromNetworks(worksId);
        }
        if (this.mTocItems == null) {
            this.mTocItems = new ArrayList();
        }
    }

    private void loadFromNetworks(int worksId) {
        try {
            this.mTocItems = WorksManager.getInstance().getWorksToc(worksId);
            for (TocItem tocItem : this.mTocItems) {
                tocItem.worksId = worksId;
            }
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }
}
