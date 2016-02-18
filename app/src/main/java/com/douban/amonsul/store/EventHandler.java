package com.douban.amonsul.store;

import com.douban.amonsul.model.StatEvent;
import java.util.List;

public abstract class EventHandler {
    public abstract void cleanAllEvent();

    public abstract List<StatEvent> getAllEvents();

    public abstract String getEventJsonArrayStr();

    public abstract int getEventsCnt();

    public abstract void saveEvent(StatEvent statEvent);
}
