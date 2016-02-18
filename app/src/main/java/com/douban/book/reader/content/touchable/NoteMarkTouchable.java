package com.douban.book.reader.content.touchable;

public class NoteMarkTouchable extends Touchable {
    public int endOffset;
    public int paragraphId;
    public int startOffset;

    public NoteMarkTouchable() {
        setPriority(29);
    }
}
