package com.douban.book.reader.entity;

import android.provider.BaseColumns;
import android.text.SpannableStringBuilder;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.chapter.ChapterIndexer;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.database.UniqueAwareDao;
import com.douban.book.reader.location.PositionWrapper;
import com.douban.book.reader.location.SortIndexable;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.StringUtils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.realm.internal.Table;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(daoClass = UniqueAwareDao.class, tableName = "bookmark")
public class Bookmark extends PositionWrapper implements Identifiable, SortIndexable {
    public static final String TABLE_NAME = "bookmark";
    @DatabaseField(columnName = "create_time", dataType = DataType.DATE_LONG)
    public Date createTime;
    @DatabaseField(columnName = "is_deleted")
    public boolean isDeleted;
    private CharSequence mAbstractText;
    private String mThumbnailUri;
    @DatabaseField(columnName = "package_id", uniqueCombo = true)
    public int packageId;
    @DatabaseField(columnName = "paragraph_id", uniqueCombo = true)
    public int paragraphId;
    @DatabaseField(columnName = "paragraph_offset", uniqueCombo = true)
    public int paragraphOffset;
    @DatabaseField(columnName = "sort_index")
    public transient long sortIndex;
    @DatabaseField(columnName = "update_time", dataType = DataType.DATE_LONG)
    public Date updateTime;
    @DatabaseField(columnName = "uuid", id = true)
    public UUID uuid;
    @DatabaseField(columnName = "works_id", uniqueCombo = true)
    public int worksId;

    public static final class Column implements BaseColumns {
        public static final String CREATE_TIME = "create_time";
        public static final String IS_DELETED = "is_deleted";
        public static final String PACKAGE_ID = "package_id";
        public static final String PARAGRAPH_ID = "paragraph_id";
        public static final String PARAGRAPH_OFFSET = "paragraph_offset";
        public static final String SORT_INDEX = "sort_index";
        public static final String UPDATE_TIME = "update_time";
        public static final String UUID = "uuid";
        public static final String WORKS_ID = "works_id";
    }

    public Bookmark() {
        this.sortIndex = -1;
        this.mThumbnailUri = null;
        this.uuid = UUID.randomUUID();
        this.isDeleted = false;
        this.createTime = Calendar.getInstance().getTime();
        this.updateTime = this.createTime;
    }

    protected Position calculatePosition() {
        try {
            Position position = new Position();
            position.packageId = this.packageId;
            position.paragraphId = this.paragraphId;
            position.packageIndex = Book.get(this.worksId).getChapterIndex(this.packageId);
            position.paragraphIndex = ChapterIndexer.get(this.worksId, this.packageId).getIndexById(this.paragraphId);
            position.paragraphOffset = this.paragraphOffset;
            return position;
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            return Position.NOT_FOUND;
        }
    }

    public String toString() {
        String str = "Bookmark:%s uuid=%s, position=%s";
        Object[] objArr = new Object[3];
        objArr[0] = this.isDeleted ? " [DELETED]" : Table.STRING_DEFAULT_VALUE;
        objArr[1] = this.uuid;
        objArr[2] = peekPosition();
        return String.format(str, objArr);
    }

    public CharSequence getAbstract() {
        if (StringUtils.isEmpty(this.mAbstractText)) {
            Position position = getElegancePosition();
            Paragraph paragraph = Book.get(this.worksId).getParagraph(position);
            if (paragraph != null) {
                CharSequence text = paragraph.getPrintableText(position.paragraphOffset);
                if (position.paragraphOffset > 0) {
                    if (StringUtils.isNotEmpty(text)) {
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(Paragraph.ELLIPSIS).append(" ").append(text);
                        this.mAbstractText = builder;
                    }
                }
                this.mAbstractText = text;
            }
        }
        return this.mAbstractText;
    }

    public String getThumbnailUri() {
        if (StringUtils.isEmpty(this.mThumbnailUri)) {
            Book book = Book.get(this.worksId);
            Position position = getElegancePosition();
            Paragraph paragraph = book.getParagraph(position);
            if (paragraph instanceof IllusParagraph) {
                this.mThumbnailUri = ReaderUri.illus(this.worksId, book.getPackageId(position.packageIndex), ((IllusParagraph) paragraph).getIllusSeq(), ImageSize.NORMAL).toString();
            }
        }
        return this.mThumbnailUri;
    }

    public Position getElegancePosition() {
        Book book = Book.get(this.worksId);
        Range range = book.getPageInfo(book.getPageForPosition(getPosition())).getRange();
        Position position = range.startPosition;
        Position nextPosition = Book.get(this.worksId).getNextReadableParagraphPosition(range.startPosition);
        if (nextPosition.isInRange(range)) {
            return nextPosition;
        }
        return position;
    }

    public UUID getId() {
        return this.uuid;
    }

    public void calculateSortIndex() {
        this.sortIndex = getPosition().getActualPosition();
    }
}
