package com.douban.book.reader.entity;

import android.provider.BaseColumns;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.chapter.ChapterIndexer;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.database.AndroidDao;
import com.douban.book.reader.location.RangeWrapper;
import com.douban.book.reader.location.SortIndexable;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.realm.internal.Table;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(daoClass = AndroidDao.class, tableName = "annotation")
public class Annotation extends RangeWrapper implements Identifiable, SortIndexable {
    public static final String TABLE_NAME = "annotation";
    @DatabaseField(columnName = "create_time")
    public Date createTime;
    @DatabaseField(columnName = "end_offset")
    public int endOffset;
    @DatabaseField(columnName = "end_paragraph_id")
    public int endParagraphId;
    @DatabaseField(columnName = "end_position")
    public transient long endPosition;
    @DatabaseField(columnName = "id")
    public int id;
    @DatabaseField(columnName = "is_deleted")
    public boolean isDeleted;
    private transient CharSequence markedText;
    @DatabaseField(columnName = "note")
    public String note;
    @DatabaseField(columnName = "note_update_time")
    public Date noteUpdateTime;
    @DatabaseField(columnName = "package_id")
    public int packageId;
    @DatabaseField(columnName = "privacy")
    public String privacy;
    @DatabaseField(columnName = "start_offset")
    public int startOffset;
    @DatabaseField(columnName = "start_paragraph_id")
    public int startParagraphId;
    @DatabaseField(columnName = "start_position")
    public transient long startPosition;
    @DatabaseField(columnName = "type")
    public String type;
    @DatabaseField(columnName = "update_time")
    public Date updateTime;
    @DatabaseField(columnName = "user_id")
    public int userId;
    @DatabaseField(columnName = "uuid", id = true)
    public UUID uuid;
    @DatabaseField(columnName = "works_id")
    public int worksId;

    public static final class Column implements BaseColumns {
        public static final String CREATE_TIME = "create_time";
        public static final String END_OFFSET = "end_offset";
        public static final String END_PARAGRAPH_ID = "end_paragraph_id";
        public static final String END_POSITION = "end_position";
        public static final String ID = "id";
        public static final String IS_DELETED = "is_deleted";
        public static final String NOTE = "note";
        public static final String NOTE_UPDATE_TIME = "note_update_time";
        public static final String PACKAGE_ID = "package_id";
        public static final String PRIVACY = "privacy";
        public static final String START_OFFSET = "start_offset";
        public static final String START_PARAGRAPH_ID = "start_paragraph_id";
        public static final String START_POSITION = "start_position";
        public static final String TYPE = "type";
        public static final String UPDATE_TIME = "update_time";
        public static final String USER_ID = "user_id";
        public static final String UUID = "uuid";
        public static final String WORKS_ID = "works_id";
    }

    public static final class Privacy {
        public static final String PRIVATE = "X";
        public static final String PUBLIC = "P";
    }

    public static final class Type {
        public static final String NOTE = "N";
        public static final String UNDERLINE = "U";
    }

    public Annotation() {
        this.startPosition = -1;
        this.endPosition = -1;
        this.markedText = null;
        this.uuid = UUID.randomUUID();
        this.createTime = new Date();
        this.updateTime = this.createTime;
        this.userId = UserManager.getInstance().getUserId();
        this.privacy = Privacy.PRIVATE;
    }

    public Annotation(int worksId, String type) {
        this();
        this.worksId = worksId;
        this.type = type;
    }

    public static Annotation createUnderline(int worksId, Range range) {
        Annotation annotation = new Annotation(worksId, Type.UNDERLINE);
        annotation.setRange(range);
        return annotation;
    }

    public static Annotation createNote(int worksId, Range range, String text, boolean isPublic) {
        Annotation annotation = new Annotation(worksId, Type.NOTE);
        annotation.note = text;
        annotation.privacy = isPublic ? Privacy.PUBLIC : Privacy.PRIVATE;
        annotation.setRange(range);
        return annotation;
    }

    public Object getId() {
        return this.uuid;
    }

    public boolean isPublic() {
        return StringUtils.equalsIgnoreCase(this.privacy, Privacy.PUBLIC);
    }

    public boolean isPrivate() {
        return StringUtils.equalsIgnoreCase(this.privacy, Privacy.PRIVATE);
    }

    public void setIsPrivate(boolean isPrivate) {
        setPrivacy(isPrivate ? Privacy.PRIVATE : Privacy.PUBLIC);
    }

    private void setPrivacy(String privacy) {
        if (StringUtils.inList(privacy, Privacy.PUBLIC, Privacy.PRIVATE)) {
            this.privacy = privacy;
        } else {
            throw new IllegalArgumentException(String.format("unknown privacy %s", new Object[]{privacy}));
        }
    }

    public boolean wasCreatedByMe() {
        return this.userId == 0 || this.userId == UserManager.getInstance().getUserId();
    }

    public boolean canBeDisplayed() {
        return !this.isDeleted && (isPublic() || wasCreatedByMe());
    }

    public void calculateSortIndex() {
        this.startPosition = getRange().startPosition.getActualPosition();
        this.endPosition = getRange().endPosition.getActualPosition();
    }

    public void setRange(Range range) {
        super.setRange(range);
        this.packageId = range.startPosition.packageId;
        this.startParagraphId = range.startPosition.paragraphId;
        this.startOffset = range.startPosition.paragraphOffset;
        this.endParagraphId = range.endPosition.paragraphId;
        this.endOffset = range.endPosition.paragraphOffset;
        calculateSortIndex();
    }

    protected Range calculateRange() {
        try {
            Position start = new Position();
            start.packageId = this.packageId;
            start.paragraphId = this.startParagraphId;
            start.packageIndex = Book.get(this.worksId).getChapterIndex(this.packageId);
            start.paragraphIndex = ChapterIndexer.get(this.worksId, this.packageId).getIndexById(this.startParagraphId);
            start.paragraphOffset = this.startOffset;
            Position end = new Position();
            end.packageId = this.packageId;
            end.paragraphId = this.endParagraphId;
            end.packageIndex = Book.get(this.worksId).getChapterIndex(this.packageId);
            end.paragraphIndex = ChapterIndexer.get(this.worksId, this.packageId).getIndexById(this.endParagraphId);
            end.paragraphOffset = this.endOffset;
            return new Range(start, end);
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            return Range.EMPTY;
        }
    }

    public CharSequence getMarkedText() {
        if (this.markedText == null) {
            this.markedText = Book.get(this.worksId).getText(getRange());
        }
        return this.markedText;
    }

    public boolean mergeAllowed() {
        return isUnderline();
    }

    public boolean isUnderline() {
        return StringUtils.equals(this.type, Type.UNDERLINE);
    }

    public boolean isNote() {
        return StringUtils.equals(this.type, Type.NOTE);
    }

    public String toString() {
        String str = "Annotation(%s):%s uuid=%s, range=%s";
        Object[] objArr = new Object[4];
        objArr[0] = isUnderline() ? "Underline" : StringUtils.truncate(this.note, 10);
        objArr[1] = this.isDeleted ? " [DELETED]" : Table.STRING_DEFAULT_VALUE;
        objArr[2] = this.uuid;
        objArr[3] = peekRange();
        return String.format(str, objArr);
    }
}
