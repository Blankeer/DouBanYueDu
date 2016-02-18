package com.douban.book.reader.location;

import com.douban.book.reader.content.page.Position;

public abstract class PositionWrapper implements Locatable {
    protected transient String TAG;
    private transient Position mPosition;

    protected abstract Position calculatePosition();

    public PositionWrapper() {
        this.TAG = getClass().getSimpleName();
        this.mPosition = null;
    }

    public Position getPosition() {
        if (this.mPosition == null || !(shouldCacheInvalidPositions() || this.mPosition.isValid())) {
            this.mPosition = calculatePosition();
        }
        return this.mPosition;
    }

    public PositionWrapper setPosition(Position position) {
        this.mPosition = new Position(position);
        return this;
    }

    protected Position peekPosition() {
        return this.mPosition;
    }

    public boolean isPositionValid() {
        return getPosition().isValid();
    }

    public int compareTo(Locatable another) {
        return getPosition().compareTo(another.getPosition());
    }

    public void invalidatePosition() {
        this.mPosition = null;
    }

    protected boolean shouldCacheInvalidPositions() {
        return true;
    }
}
