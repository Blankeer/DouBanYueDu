package com.douban.book.reader.manager;

import android.support.annotation.Nullable;
import android.util.LruCache;
import com.douban.book.reader.constant.ArkAction;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.Annotation.Column;
import com.douban.book.reader.entity.Annotation.Privacy;
import com.douban.book.reader.entity.Annotation.Type;
import com.douban.book.reader.entity.Bookmark;
import com.douban.book.reader.event.ActiveNoteChangedEvent;
import com.douban.book.reader.event.AnnotationUpdatedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.exception.ShareFailedWhileCreatingNoteException;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.fragment.share.ShareGiftEditFragment_;
import com.douban.book.reader.manager.cache.DbCache;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.exception.NetworkRequestPostponedException;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.OrmUtils;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.open.SocialConstants;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class AnnotationManager extends BaseSyncedManager<Annotation> {
    private static LruCache<Integer, AnnotationManager> sInstances;
    private Annotation mActiveNote;
    private int mWorksId;

    private class AnnotationDbCache extends DbCache<Annotation> {
        public AnnotationDbCache() {
            super(Annotation.class);
        }

        public void add(Annotation entity) throws DataLoadException {
            super.add(entity);
            notifyUpdated(entity.getId(), ArkAction.ADDITION);
        }

        public void addAll(Collection<Annotation> dataList) throws DataLoadException {
            super.addAll(dataList);
            notifyUpdated(null, ArkAction.ADDITION);
        }

        public void delete(Object id) throws DataLoadException {
            super.delete(id);
            notifyUpdated(id, ArkAction.DELETION);
        }

        public void deleteAll() throws DataLoadException {
            super.deleteAll();
            notifyUpdated(null, ArkAction.DELETION);
        }

        private void notifyUpdated(Object id, ArkAction action) {
            AnnotationUpdatedEvent event = new AnnotationUpdatedEvent(AnnotationManager.this.mWorksId, id instanceof UUID ? (UUID) id : null, action);
            if (event.isDeletion()) {
                clearActiveNoteForOneOrAll(event);
            }
            EventBusUtils.post(event);
        }

        private void clearActiveNoteForOneOrAll(AnnotationUpdatedEvent event) {
            if (AnnotationManager.this.mWorksId <= 0) {
                for (AnnotationManager manager : AnnotationManager.sInstances.snapshot().values()) {
                    if (event.isValidFor(manager.getActiveNote())) {
                        manager.setActiveNote(null);
                    }
                }
            } else if (event.isValidFor(AnnotationManager.this.getActiveNote())) {
                AnnotationManager.this.setActiveNote(null);
            }
        }
    }

    static {
        sInstances = new LruCache(5);
    }

    public static AnnotationManager ofWorks(int worksId) {
        AnnotationManager instance = (AnnotationManager) sInstances.get(Integer.valueOf(worksId));
        if (instance != null) {
            return instance;
        }
        instance = new AnnotationManager();
        instance.setWorksId(worksId);
        sInstances.put(Integer.valueOf(worksId), instance);
        return instance;
    }

    public AnnotationManager() {
        super("annotations", Annotation.class);
        cache(new AnnotationDbCache());
    }

    @Deprecated
    public void setWorksId(int worksId) {
        this.mWorksId = worksId;
    }

    public void setActiveNote(@Nullable Annotation note) {
        if (this.mWorksId <= 0) {
            if (note == null || note.worksId <= 0) {
                throw new IllegalStateException("worksId mast be set before setting active note.");
            }
            ofWorks(note.worksId).setActiveNote(note);
        } else if (this.mActiveNote == null || !this.mActiveNote.equals(note)) {
            this.mActiveNote = note;
            EventBusUtils.post(new ActiveNoteChangedEvent(this.mWorksId, note != null ? note.uuid : null));
        }
    }

    @Nullable
    public Annotation getActiveNote() {
        return this.mActiveNote;
    }

    public Annotation getByIdOrUuid(Object idOrUuid) throws DataLoadException {
        UUID uuid = null;
        if (idOrUuid instanceof UUID) {
            uuid = (UUID) idOrUuid;
        } else if (idOrUuid instanceof CharSequence) {
            uuid = StringUtils.toUUID((CharSequence) idOrUuid);
        }
        if (uuid != null) {
            return (Annotation) get((Object) uuid);
        }
        return (Annotation) getFromRemote(idOrUuid);
    }

    public void newUnderline(Range range) throws DataLoadException {
        Range groupedRange = range;
        if (hasUnderlinesAdjoinsRange(range)) {
            groupedRange = getGroupedRange(range);
            deleteUnderlinesAdjoinsRange(groupedRange);
        }
        if (groupedRange.isValid()) {
            asyncAdd(Annotation.createUnderline(this.mWorksId, groupedRange));
        }
    }

    public void newNote(Annotation annotation, JsonRequestParam shareTo) throws DataLoadException {
        if (annotation != null) {
            try {
                add(annotation, annotation.isPublic() ? shareTo : null);
            } catch (Throwable e) {
                if (annotation.isPublic() && !shareTo.isEmpty() && ExceptionUtils.isCausedBy(e, NetworkRequestPostponedException.class)) {
                    throw new ShareFailedWhileCreatingNoteException(e);
                }
            }
        }
    }

    public void updateNote(UUID uuid, String note) throws DataLoadException {
        Date updateTime = new Date();
        try {
            update(uuid, (JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append(BaseShareEditFragment.CONTENT_TYPE_NOTE, note)).append(Column.NOTE_UPDATE_TIME, updateTime)).append(Bookmark.Column.UPDATE_TIME, updateTime));
        } catch (DataLoadException e) {
            if (ExceptionUtils.isCausedBy(e, NetworkRequestPostponedException.class)) {
                Annotation annotation = (Annotation) getFromCache(uuid);
                annotation.note = note;
                annotation.noteUpdateTime = updateTime;
                annotation.updateTime = updateTime;
                addToCache(annotation);
            }
            throw e;
        }
    }

    public Annotation setIsPrivate(UUID uuid, boolean isPrivate) throws DataLoadException {
        return (Annotation) onlineUpdate(uuid, (JsonRequestParam) ((JsonRequestParam) RequestParam.json().append(Column.PRIVACY, isPrivate ? Privacy.PRIVATE : Privacy.PUBLIC)).append(Bookmark.Column.UPDATE_TIME, new Date()));
    }

    public void deleteUnderline(UUID uuid) throws DataLoadException {
        Annotation annotation = (Annotation) getFromCache(uuid);
        if (annotation.isUnderline() && annotation.isRangeValid()) {
            deleteUnderlinesAdjoinsRange(annotation.getRange());
        }
    }

    public void cutUnderlinesInRange(Range range) throws DataLoadException {
        List<Range> resultList = Range.cut(this.mWorksId, getGroupedRange(range), range);
        deleteUnderlinesAdjoinsRange(range);
        for (Range result : resultList) {
            newUnderline(result);
        }
    }

    public void refresh() throws DataLoadException {
        int currentUser = UserManager.getInstance().getUserId();
        Date lastUpdatedAt = null;
        if (Pref.ofWorks(this.mWorksId).getInt(Key.WORKS_ANNOTATION_LAST_UPDATED_USER, 0) == currentUser) {
            lastUpdatedAt = Pref.ofWorks(this.mWorksId).getDate(Key.WORKS_ANNOTATION_LAST_UPDATED_AT, null);
        }
        Pref.ofWorks(this.mWorksId).set(Key.WORKS_ANNOTATION_LAST_UPDATED_AT, refresh(new DataFilter().append(Bookmark.Column.WORKS_ID, Integer.valueOf(this.mWorksId)).appendIfNotNull("since", lastUpdatedAt)));
        Pref.ofWorks(this.mWorksId).set(Key.WORKS_ANNOTATION_LAST_UPDATED_USER, Integer.valueOf(currentUser));
    }

    public void updateIndex() {
        try {
            for (Annotation annotation : listUnIndexed()) {
                annotation.calculateSortIndex();
                addToCache(annotation);
            }
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    public List<Annotation> listAllUnderlines() throws DataLoadException {
        return loadFromLocal(Type.UNDERLINE);
    }

    public List<Annotation> listAllNotes() throws DataLoadException {
        return loadFromLocal(Type.NOTE);
    }

    public List<Annotation> underlinesAdjoinsRange(Range range) {
        return listAdjoinsRange(range, Type.UNDERLINE);
    }

    public List<Annotation> notesInRange(Range range) {
        return listInRange(range, Type.NOTE);
    }

    public List<Annotation> underlinesInRange(Range range) {
        return listInRange(range, Type.UNDERLINE);
    }

    public boolean hasUnderlinesAdjoinsRange(Range range) {
        try {
            return whereAdjoinsRange(range, Type.UNDERLINE).countOf() > 0;
        } catch (SQLException e) {
            Logger.e(this.TAG, e);
            return false;
        }
    }

    public boolean hasUnderlinesFillsRange(Range range) {
        try {
            return whereFillsRange(range, Type.UNDERLINE).countOf() > 0;
        } catch (SQLException e) {
            Logger.e(this.TAG, e);
            return false;
        }
    }

    private List<Annotation> listAdjoinsRange(Range range, String type) {
        try {
            return whereAdjoinsRange(range, type).query();
        } catch (SQLException e) {
            return new ArrayList();
        }
    }

    private List<Annotation> listInRange(Range range, String type) {
        try {
            return whereInRange(range, type).query();
        } catch (SQLException e) {
            return new ArrayList();
        }
    }

    public List<Annotation> getGroupedUnderlinesAdjoinsRange(Range range) {
        List<Annotation> underlineList = underlinesAdjoinsRange(range);
        Queue<Annotation> toBeExamined = new LinkedList(underlineList);
        while (true) {
            Annotation underline = (Annotation) toBeExamined.poll();
            if (underline == null) {
                return underlineList;
            }
            for (Annotation intersected : underlinesAdjoinsRange(underline.getRange())) {
                if (!containsAnnotationsWithUUID(underlineList, intersected.uuid)) {
                    toBeExamined.add(intersected);
                    underlineList.add(intersected);
                }
            }
        }
    }

    public Range getGroupedRange(Range range) {
        Range result = range;
        for (Annotation underline : getGroupedUnderlinesAdjoinsRange(range)) {
            result = Range.merge(result, underline.getRange());
        }
        return result;
    }

    public void deleteUnderlinesAdjoinsRange(Range range) {
        for (Annotation annotation : getGroupedUnderlinesAdjoinsRange(range)) {
            try {
                asyncDelete(annotation.uuid);
            } catch (DataLoadException e) {
                Logger.e(this.TAG, e);
            }
        }
    }

    public long getNoteCountForCurrentUser() {
        try {
            return whereValidForCurrentUser(Type.NOTE).countOf();
        } catch (SQLException e) {
            Logger.e(this.TAG, e);
            return 0;
        }
    }

    public long getNoteCountForParagraph(int paragraphId, int startOffset, int endOffset) {
        if (startOffset < 0) {
            startOffset = 0;
        }
        if (endOffset <= 0) {
            endOffset = AdvancedShareActionProvider.WEIGHT_MAX;
        }
        try {
            return whereValidForCurrentUser(Type.NOTE).and().eq(Column.END_PARAGRAPH_ID, Integer.valueOf(paragraphId)).and().between(Column.END_OFFSET, Integer.valueOf(startOffset), Integer.valueOf(endOffset)).countOf();
        } catch (SQLException e) {
            Logger.e(this.TAG, e);
            return 0;
        }
    }

    @Nullable
    public Annotation getFirstNoteInParagraph(int paragraphId, int startOffset, int endOffset) {
        if (startOffset < 0) {
            startOffset = 0;
        }
        if (endOffset <= 0) {
            endOffset = AdvancedShareActionProvider.WEIGHT_MAX;
        }
        try {
            QueryBuilder<Annotation, Object> queryBuilder = OrmUtils.getDao(Annotation.class).queryBuilder().orderBy(Column.END_OFFSET, true);
            queryBuilder.setWhere(whereValidForCurrentUser(Type.NOTE).and().eq(Column.END_PARAGRAPH_ID, Integer.valueOf(paragraphId)).and().between(Column.END_OFFSET, Integer.valueOf(startOffset), Integer.valueOf(endOffset)));
            return (Annotation) queryBuilder.queryForFirst();
        } catch (SQLException e) {
            Logger.e(this.TAG, e);
            return null;
        }
    }

    private List<Annotation> loadFromNetwork() throws DataLoadException {
        Lister<Annotation> lister = list().cache(null).filter(new DataFilter().append(Bookmark.Column.WORKS_ID, Integer.valueOf(this.mWorksId)));
        List<Annotation> results = new ArrayList();
        while (lister.hasMore()) {
            results.addAll(lister.loadMore());
        }
        return results;
    }

    private List<Annotation> loadFromLocal(String type) throws DataLoadException {
        try {
            QueryBuilder<Annotation, Object> queryBuilder = OrmUtils.getDao(Annotation.class).queryBuilder().orderBy(Column.END_POSITION, true).orderBy(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, true);
            queryBuilder.setWhere(whereValidForCurrentUser(type));
            return queryBuilder.query();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    @Nullable
    public Annotation getPreviousNote(Annotation annotation) {
        if (annotation == null || !annotation.isNote()) {
            throw new IllegalArgumentException("Annotation must be a valid note.");
        }
        try {
            Dao<Annotation, Object> dao = OrmUtils.getDao(Annotation.class);
            Where<Annotation, Object> where = whereValidForCurrentUser(Type.NOTE);
            where = where.and(where, where.and(where.or(where.lt(Column.END_POSITION, Long.valueOf(annotation.endPosition)), where.eq(Column.END_POSITION, Long.valueOf(annotation.endPosition)).and().lt(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, annotation.createTime), new Where[0]), where.ne(ShareGiftEditFragment_.UUID_ARG, annotation.uuid), new Where[0]), new Where[0]);
            QueryBuilder<Annotation, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(Column.END_POSITION, false).orderBy(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, false).setWhere(where);
            return (Annotation) queryBuilder.queryForFirst();
        } catch (SQLException e) {
            Logger.e(this.TAG, e);
            return null;
        }
    }

    @Nullable
    public Annotation getNextNote(Annotation annotation) {
        if (annotation == null || !annotation.isNote()) {
            throw new IllegalArgumentException("Annotation must be a valid note.");
        }
        try {
            Dao<Annotation, Object> dao = OrmUtils.getDao(Annotation.class);
            Where<Annotation, Object> where = whereValidForCurrentUser(Type.NOTE);
            where = where.and(where, where.and(where.or(where.gt(Column.END_POSITION, Long.valueOf(annotation.endPosition)), where.eq(Column.END_POSITION, Long.valueOf(annotation.endPosition)).and().gt(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, annotation.createTime), new Where[0]), where.ne(ShareGiftEditFragment_.UUID_ARG, annotation.uuid), new Where[0]), new Where[0]);
            QueryBuilder<Annotation, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(Column.END_POSITION, true).orderBy(WBConstants.GAME_PARAMS_GAME_CREATE_TIME, true).setWhere(where);
            return (Annotation) queryBuilder.queryForFirst();
        } catch (SQLException e) {
            Logger.e(this.TAG, e);
            return null;
        }
    }

    public void shareAnnotation(UUID uuid, ShareTo shareTo, String text) throws DataLoadException {
        getSubManagerForId(uuid, "rec").post(((JsonRequestParam) RequestParam.json().appendShareTo(shareTo)).appendIfNotEmpty("text", text));
    }

    public void shareAnnotation(UUID uuid, RequestParam<?> requestParam) throws DataLoadException {
        getSubManagerForId(uuid, "rec").post(requestParam);
    }

    private List<Annotation> listUnIndexed() throws DataLoadException {
        try {
            Where<Annotation, Object> where = OrmUtils.getDao(Annotation.class).queryBuilder().where();
            Where eq = where.eq(Bookmark.Column.WORKS_ID, Integer.valueOf(this.mWorksId));
            Where or = where.or(where.le(Column.START_POSITION, Integer.valueOf(0)), where.le(Column.END_POSITION, Integer.valueOf(0)), new Where[0]);
            Where[] whereArr = new Where[1];
            whereArr[0] = where.raw(String.format("%s = %d", new Object[]{Bookmark.Column.IS_DELETED, Integer.valueOf(0)}), new ArgumentHolder[0]);
            where.and(eq, or, whereArr);
            return where.query();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    private Where<Annotation, Object> whereValid(String type) throws SQLException {
        Where<Annotation, Object> where = OrmUtils.getDao(Annotation.class).queryBuilder().where();
        where.eq(Bookmark.Column.WORKS_ID, Integer.valueOf(this.mWorksId)).and().gt(Column.START_POSITION, Integer.valueOf(0)).and().gt(Column.END_POSITION, Integer.valueOf(0)).and().raw(String.format("%s = %d", new Object[]{Bookmark.Column.IS_DELETED, Integer.valueOf(0)}), new ArgumentHolder[0]);
        if (StringUtils.isNotEmpty(type)) {
            where.and().eq(SocialConstants.PARAM_TYPE, type);
        }
        return where;
    }

    private Where<Annotation, Object> whereValidForCurrentUser(String type) throws SQLException {
        int currentUserId = UserManager.getInstance().getUserId();
        return whereValid(type).and().in(Column.USER_ID, Integer.valueOf(0), Integer.valueOf(currentUserId));
    }

    private Where<Annotation, Object> whereAdjoinsRange(Range range, String type) throws SQLException {
        if (Range.isValid(range)) {
            return whereValidForCurrentUser(type).and().le(Column.START_POSITION, Long.valueOf(range.endPosition.getActualPosition() + 1)).and().ge(Column.END_POSITION, Long.valueOf(range.startPosition.getActualPosition() - 1));
        }
        throw new SQLException(String.format("invalid range %s", new Object[]{range}));
    }

    private Where<Annotation, Object> whereInRange(Range range, String type) throws SQLException {
        if (Range.isValid(range)) {
            return whereValidForCurrentUser(type).and().le(Column.START_POSITION, Long.valueOf(range.endPosition.getActualPosition())).and().ge(Column.END_POSITION, Long.valueOf(range.startPosition.getActualPosition()));
        }
        throw new SQLException(String.format("invalid range %s", new Object[]{range}));
    }

    private Where<Annotation, Object> whereFillsRange(Range range, String type) throws SQLException {
        if (Range.isValid(range)) {
            return whereValidForCurrentUser(type).and().le(Column.START_POSITION, Long.valueOf(range.startPosition.getActualPosition())).and().ge(Column.END_POSITION, Long.valueOf(range.endPosition.getActualPosition()));
        }
        throw new SQLException(String.format("invalid range %s", new Object[]{range}));
    }

    private static boolean containsAnnotationsWithUUID(Iterable<Annotation> collection, UUID uuid) {
        for (Annotation annotation : collection) {
            if (annotation != null && annotation.uuid != null && annotation.uuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}
