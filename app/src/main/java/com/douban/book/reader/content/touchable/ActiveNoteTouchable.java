package com.douban.book.reader.content.touchable;

import java.util.UUID;

public class ActiveNoteTouchable extends Touchable {
    public UUID uuid;

    public ActiveNoteTouchable(UUID uuid) {
        this.uuid = uuid;
        setPriority(31);
    }
}
