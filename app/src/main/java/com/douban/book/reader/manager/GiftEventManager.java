package com.douban.book.reader.manager;

import com.douban.book.reader.entity.GiftEvent;
import com.douban.book.reader.manager.exception.DataLoadException;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class GiftEventManager extends BaseManager<GiftEvent> {
    public GiftEventManager() {
        super("gifts/events", GiftEvent.class);
    }

    public GiftEvent getGiftEvent(int giftEventId) throws DataLoadException {
        return (GiftEvent) get((Object) Integer.valueOf(giftEventId));
    }
}
