package com.douban.book.reader.manager;

import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.entity.GiftPack;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.GiftPackUpdatedEvent;
import com.douban.book.reader.fragment.GiftMessageEditFragment_;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class GiftPackManager extends BaseManager<GiftPack> {
    public GiftPackManager() {
        super("gifts/packs", GiftPack.class);
    }

    public Lister<GiftPack> listerForGiftPacks() {
        return list();
    }

    public GiftPack getGiftPack(int giftPackId) throws DataLoadException {
        return (GiftPack) get((Object) Integer.valueOf(giftPackId));
    }

    public GiftPack getByHashCode(String hashCode) throws DataLoadException {
        return (GiftPack) getSubManager(hashCode, GiftPack.class).get();
    }

    public GiftPack create(int worksId, int eventId, int quantity, CharSequence message) throws DataLoadException {
        return (GiftPack) post(((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append(Column.WORKS_ID, Integer.valueOf(worksId))).appendIf(eventId > 0, "event_id", Integer.valueOf(eventId))).append("quantity", Integer.valueOf(quantity))).append(GiftMessageEditFragment_.MESSAGE_ARG, message));
    }

    public void updateGiftNote(int giftPackId, CharSequence newGiftNote) throws DataLoadException {
        update(Integer.valueOf(giftPackId), RequestParam.json().append(GiftMessageEditFragment_.MESSAGE_ARG, newGiftNote));
        EventBusUtils.post(new GiftPackUpdatedEvent(giftPackId));
    }

    public void share(int giftPackId, ShareTo shareTo, String text) throws DataLoadException {
        getSubManagerForId(Integer.valueOf(giftPackId), "rec").post(((JsonRequestParam) RequestParam.json().appendShareTo(shareTo)).appendIfNotEmpty("text", text));
    }

    public Gift receive(String hashCode) throws DataLoadException {
        return (Gift) getSubManager(String.format("%s/receive", new Object[]{hashCode}), Gift.class).post(RequestParam.json());
    }
}
