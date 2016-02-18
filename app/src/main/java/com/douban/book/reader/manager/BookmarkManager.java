package com.douban.book.reader.manager;

import android.util.LruCache;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.entity.Bookmark;
import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.manager.cache.DbCache;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.OrmUtils;
import com.douban.book.reader.util.Pref;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.Where;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookmarkManager extends BaseLegacySyncedManager<Bookmark> {
    private static LruCache<Integer, BookmarkManager> sInstances;
    private int mWorksId;

    static {
        sInstances = new LruCache(5);
    }

    public static BookmarkManager ofWorks(int worksId) {
        BookmarkManager instance = (BookmarkManager) sInstances.get(Integer.valueOf(worksId));
        if (instance != null) {
            return instance;
        }
        instance = new BookmarkManager(worksId);
        sInstances.put(Integer.valueOf(worksId), instance);
        return instance;
    }

    public BookmarkManager(int worksId) {
        super("bookmarks", Bookmark.class);
        cache(new DbCache(Bookmark.class));
        this.mWorksId = worksId;
    }

    public void refresh() throws DataLoadException {
        int currentUser = UserManager.getInstance().getUserId();
        Date lastUpdatedAt = null;
        if (Pref.ofWorks(this.mWorksId).getInt(Key.WORKS_BOOKMARK_LAST_UPDATED_USER, 0) == currentUser) {
            lastUpdatedAt = Pref.ofWorks(this.mWorksId).getDate(Key.WORKS_BOOKMARK_LAST_UPDATED_AT, null);
        }
        Pref.ofWorks(this.mWorksId).set(Key.WORKS_BOOKMARK_LAST_UPDATED_AT, refresh(new DataFilter().append(Column.WORKS_ID, Integer.valueOf(this.mWorksId)).appendIfNotNull("since", lastUpdatedAt)));
        Pref.ofWorks(this.mWorksId).set(Key.WORKS_BOOKMARK_LAST_UPDATED_USER, Integer.valueOf(currentUser));
    }

    public void updateIndex() {
        try {
            for (Bookmark bookmark : listUnIndexed()) {
                bookmark.calculateSortIndex();
                addToCache(bookmark);
            }
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    public List<Bookmark> listAll() throws DataLoadException {
        List<Bookmark> bookmarkList = loadFromLocal();
        Position lastPosition = Position.NOT_FOUND;
        List<Bookmark> result = new ArrayList();
        for (Bookmark bookmark : bookmarkList) {
            Position position = bookmark.getElegancePosition();
            if (Position.isValid(position) && !position.equals(lastPosition)) {
                result.add(bookmark);
            }
            lastPosition = position;
        }
        return result;
    }

    public boolean hasBookmarkForPage(int page) {
        try {
            if (whereInRange(Book.get(this.mWorksId).getPageInfo(page).getRange()).countOf() > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public void toggleBookmarkStatusForPage(int page) throws DataLoadException {
        if (hasBookmarkForPage(page)) {
            deleteBookmarkForPage(page);
        } else {
            addBookmarkForPage(page);
        }
    }

    private void addBookmarkForPage(int page) throws DataLoadException {
        Book book = Book.get(this.mWorksId);
        PageInfo pageInfo = book.getPageInfo(page);
        if (pageInfo != null) {
            Paragraph paragraph = book.getParagraph(book.getChapterIndexByPage(page), pageInfo.startParaIndex);
            Bookmark bookmark = new Bookmark();
            bookmark.worksId = this.mWorksId;
            bookmark.packageId = book.getChapterIdByPage(page);
            bookmark.paragraphId = paragraph.getId();
            bookmark.paragraphOffset = pageInfo.startOffset;
            add(bookmark);
        }
    }

    private void deleteBookmarkForPage(int page) throws DataLoadException {
        deleteBookmarksInRange(Book.get(this.mWorksId).getPageInfo(page).getRange());
    }

    private List<Bookmark> bookmarksInRange(Range range) {
        List<Bookmark> list = null;
        if (range.isValid()) {
            try {
                list = whereInRange(range).query();
            } catch (SQLException e) {
            }
        }
        return list;
    }

    private void deleteBookmarksInRange(Range range) {
        List<Bookmark> bookmarksToDelete = bookmarksInRange(range);
        if (bookmarksToDelete != null) {
            for (Bookmark bookmark : bookmarksToDelete) {
                try {
                    delete(bookmark.uuid);
                } catch (DataLoadException e) {
                    Logger.e(this.TAG, e);
                }
            }
        }
    }

    private List<Bookmark> loadFromNetwork() throws DataLoadException {
        Lister<Bookmark> lister = list().cache(null).filter(new DataFilter().append(Column.WORKS_ID, Integer.valueOf(this.mWorksId)));
        List<Bookmark> results = new ArrayList();
        while (lister.hasMore()) {
            results.addAll(lister.loadMore());
        }
        return results;
    }

    private List<Bookmark> loadFromLocal() throws DataLoadException {
        try {
            return OrmUtils.getDao(Bookmark.class).queryBuilder().orderBy(Column.SORT_INDEX, true).where().eq(Column.WORKS_ID, Integer.valueOf(this.mWorksId)).and().gt(Column.SORT_INDEX, Integer.valueOf(0)).and().raw(String.format("%s = %d", new Object[]{Column.IS_DELETED, Integer.valueOf(0)}), new ArgumentHolder[0]).query();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    private List<Bookmark> listUnIndexed() throws DataLoadException {
        try {
            return OrmUtils.getDao(Bookmark.class).queryBuilder().where().eq(Column.WORKS_ID, Integer.valueOf(this.mWorksId)).and().le(Column.SORT_INDEX, Integer.valueOf(0)).and().raw(String.format("%s = %d", new Object[]{Column.IS_DELETED, Integer.valueOf(0)}), new ArgumentHolder[0]).query();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    private Where<Bookmark, Object> whereInRange(Range range) throws SQLException {
        return OrmUtils.getDao(Bookmark.class).queryBuilder().where().eq(Column.WORKS_ID, Integer.valueOf(this.mWorksId)).and().between(Column.SORT_INDEX, Long.valueOf(range.startPosition.getActualPosition()), Long.valueOf(range.endPosition.getActualPosition())).and().raw(String.format("%s = %d", new Object[]{Column.IS_DELETED, Integer.valueOf(0)}), new ArgumentHolder[0]);
    }
}
