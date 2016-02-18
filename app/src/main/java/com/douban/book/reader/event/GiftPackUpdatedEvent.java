package com.douban.book.reader.event;

import com.douban.book.reader.entity.GiftPack;

public class GiftPackUpdatedEvent {
    private int mPackId;

    public GiftPackUpdatedEvent(int packId) {
        this.mPackId = packId;
    }

    public boolean isValidFor(GiftPack giftPack) {
        return giftPack != null && giftPack.id == this.mPackId;
    }
}
