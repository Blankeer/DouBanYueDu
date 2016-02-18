package com.douban.book.reader.location;

import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.page.Range.Topology;

public abstract class RangeWrapper extends PositionWrapper {
    private transient Range mRange;

    protected abstract Range calculateRange();

    public RangeWrapper() {
        this.mRange = null;
    }

    public Range getRange() {
        if (this.mRange == null) {
            this.mRange = calculateRange();
        }
        return this.mRange;
    }

    public void setRange(Range range) {
        this.mRange = new Range(range);
    }

    protected Range peekRange() {
        return this.mRange;
    }

    public boolean isRangeValid() {
        return getRange().isValid();
    }

    public Topology compareTopology(RangeWrapper opponent) {
        return compareTopology(opponent.getRange());
    }

    public Topology compareTopology(Range range) {
        return getRange().compareTopology(range);
    }

    protected Position calculatePosition() {
        return getRange().startPosition;
    }
}
