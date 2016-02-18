package com.douban.book.reader.event;

import com.douban.book.reader.constant.ArkAction;
import com.douban.book.reader.entity.Annotation;
import java.util.UUID;

public class AnnotationUpdatedEvent extends WorksEvent {
    private ArkAction mAction;
    private UUID mUuid;

    public AnnotationUpdatedEvent(int worksId, UUID uuid, ArkAction action) {
        super(worksId);
        this.mUuid = uuid;
        this.mAction = action;
    }

    public boolean isValidFor(UUID uuid) {
        return this.mUuid == null || this.mUuid.equals(uuid);
    }

    public boolean isValidFor(Annotation annotation) {
        return annotation != null && isValidFor(annotation.uuid);
    }

    public boolean isDeletion() {
        return this.mAction == ArkAction.DELETION;
    }
}
