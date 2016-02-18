package com.douban.book.reader.location;

import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.chapter.ChapterIndexer;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.manager.cache.Identifiable;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class TocItem extends PositionWrapper implements Identifiable {
    @SerializedName("abstract")
    public String abstractText;
    public int level;
    public int packageId;
    public int paragraphId;
    public String title;
    public Date updateTime;
    public transient int worksId;

    protected Position calculatePosition() {
        Position position = new Position();
        position.packageId = this.packageId;
        position.paragraphId = this.paragraphId;
        position.packageIndex = Book.get(this.worksId).getChapterIndex(this.packageId);
        try {
            position.paragraphIndex = ChapterIndexer.get(this.worksId, this.packageId).getIndexById(this.paragraphId);
        } catch (Exception e) {
            position.paragraphIndex = 0;
        }
        position.paragraphOffset = 0;
        return position;
    }

    public boolean isPurchaseNeeded() {
        return WorksData.get(this.worksId).getPackage(this.packageId).isPurchaseNeeded();
    }

    public String toString() {
        return String.format("TocItem@%s %s", new Object[]{peekPosition(), this.title});
    }

    public Object getId() {
        return Integer.valueOf(0);
    }
}
