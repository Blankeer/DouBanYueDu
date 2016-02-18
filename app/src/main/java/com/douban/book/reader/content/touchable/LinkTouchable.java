package com.douban.book.reader.content.touchable;

import android.net.Uri;

public class LinkTouchable extends Touchable {
    public Uri link;

    public LinkTouchable() {
        setPriority(20);
    }

    public String toString() {
        try {
            return String.format("LinkTouchable: %s", new Object[]{this.link});
        } catch (Throwable th) {
            return super.toString();
        }
    }
}
