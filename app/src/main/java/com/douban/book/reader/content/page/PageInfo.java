package com.douban.book.reader.content.page;

import com.douban.book.reader.content.Book;
import com.douban.book.reader.location.RangeWrapper;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.io.Serializable;

public class PageInfo extends RangeWrapper implements Serializable {
    public int endLine;
    public int endOffset;
    public int endParaId;
    public int endParaIndex;
    public int packageId;
    public int startLine;
    public int startOffset;
    public int startParaId;
    public int startParaIndex;
    public int worksId;

    public PageInfo(int worksId, int packageId) {
        this.worksId = worksId;
        this.packageId = packageId;
    }

    public int getStartLine(int seq) {
        return seq == this.startParaIndex ? this.startLine : 0;
    }

    public int getEndLine(int seq) {
        return seq == this.endParaIndex ? this.endLine : AdvancedShareActionProvider.WEIGHT_MAX;
    }

    protected Range calculateRange() {
        Position startPos = new Position();
        startPos.packageId = this.packageId;
        startPos.packageIndex = Book.get(this.worksId).getChapterIndex(this.packageId);
        startPos.paragraphId = this.startParaId;
        startPos.paragraphIndex = this.startParaIndex;
        startPos.paragraphOffset = this.startOffset;
        Position endPos = new Position();
        endPos.packageId = this.packageId;
        endPos.packageIndex = Book.get(this.worksId).getChapterIndex(this.packageId);
        endPos.paragraphId = this.endParaId;
        endPos.paragraphIndex = this.endParaIndex;
        endPos.paragraphOffset = this.endOffset;
        return new Range(startPos, endPos);
    }

    public String toString() {
        return String.format("PageInfo: range=%s", new Object[]{peekRange()});
    }
}
