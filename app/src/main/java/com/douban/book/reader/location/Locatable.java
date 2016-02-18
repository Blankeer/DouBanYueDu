package com.douban.book.reader.location;

import com.douban.book.reader.content.page.Position;

public interface Locatable extends Comparable<Locatable> {
    Position getPosition();
}
