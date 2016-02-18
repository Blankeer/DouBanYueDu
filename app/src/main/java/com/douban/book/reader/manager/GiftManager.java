package com.douban.book.reader.manager;

import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.entity.Gift;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import java.util.UUID;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class GiftManager extends BaseManager<Gift> {
    public GiftManager() {
        super("gifts", Gift.class);
    }

    public Lister<Gift> listerForReceivedGifts() {
        return getSubManager("received", Gift.class).list();
    }

    public Lister<Gift> listerForPack(int packId) {
        return defaultLister().filter(new DataFilter().append("pack_id", Integer.valueOf(packId)));
    }

    public Gift getGift(UUID giftId) throws DataLoadException {
        return (Gift) get((Object) giftId);
    }

    public void share(UUID giftId, ShareTo shareTo, String text) throws DataLoadException {
        getSubManagerForId(giftId, "rec").post(((JsonRequestParam) RequestParam.json().appendShareTo(shareTo)).appendIfNotEmpty("text", text));
    }
}
