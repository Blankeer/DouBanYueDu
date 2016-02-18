package com.douban.book.reader.event;

import com.douban.book.reader.entity.Annotation;

public class ReaderPanelShowNoteDetailRequest {
    private final Annotation mAnnotationToShow;

    public ReaderPanelShowNoteDetailRequest(Annotation annotation) {
        this.mAnnotationToShow = annotation;
    }

    public Annotation getAnnotationToShow() {
        return this.mAnnotationToShow;
    }
}
