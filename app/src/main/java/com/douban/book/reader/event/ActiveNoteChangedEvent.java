package com.douban.book.reader.event;

import java.util.UUID;

public class ActiveNoteChangedEvent extends WorksEvent {
    private UUID mActiveNoteUuid;

    public ActiveNoteChangedEvent(int worksId, UUID activeNoteUuid) {
        super(worksId);
        this.mActiveNoteUuid = activeNoteUuid;
    }

    public UUID getActiveNoteUuid() {
        return this.mActiveNoteUuid;
    }
}
