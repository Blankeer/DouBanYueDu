package com.douban.book.reader.entity;

import com.douban.book.reader.constant.DeviceType;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.chapter.ChapterIndexer;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.location.PositionWrapper;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import java.util.Date;

public class Progress extends PositionWrapper implements Identifiable {
    public String deviceId;
    public String deviceType;
    public int packageId;
    public int paragraphId;
    public int paragraphOffset;
    public Date updateTime;
    public int worksId;

    public Progress() {
        this.deviceId = Utils.getDeviceUDID();
        this.deviceType = DeviceType.ANDROID;
        this.updateTime = new Date();
    }

    public boolean isValid() {
        if (isPositionValid()) {
            if (StringUtils.isNotEmpty(this.deviceId, this.deviceType)) {
                return true;
            }
        }
        return false;
    }

    public boolean wasCreatedByThisDevice() {
        return StringUtils.equals(this.deviceId, Utils.getDeviceUDID());
    }

    protected boolean shouldCacheInvalidPositions() {
        return false;
    }

    public Object getId() {
        return Integer.valueOf(0);
    }

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
        position.paragraphOffset = this.paragraphOffset;
        return position;
    }

    public String toString() {
        return String.format("Progress: position=%s", new Object[]{peekPosition()});
    }
}
