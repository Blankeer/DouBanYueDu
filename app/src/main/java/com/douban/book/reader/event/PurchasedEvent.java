package com.douban.book.reader.event;

import android.net.Uri;

public class PurchasedEvent {
    private Uri mPurchasedItem;

    public PurchasedEvent(Uri purchasedItem) {
        this.mPurchasedItem = purchasedItem;
    }

    public Uri getPurchasedItem() {
        return this.mPurchasedItem;
    }
}
