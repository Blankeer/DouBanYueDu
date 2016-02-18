package com.douban.book.reader.manager;

import android.net.Uri;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.content.cipher.ArkKeyPair;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.entity.DummyEntity;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Tag;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.WorksUpdatedEvent;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.fragment.WorksListFragment.WorksListMeta;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.location.TocItem;
import com.douban.book.reader.manager.cache.MemoryCache;
import com.douban.book.reader.manager.cache.PinnableCache;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.Analysis;
import com.sina.weibo.sdk.component.ShareRequestParam;
import java.util.List;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class WorksManager extends BaseManager<Works> {
    public static synchronized WorksManager getInstance() {
        WorksManager instance_;
        synchronized (WorksManager.class) {
            instance_ = WorksManager_.getInstance_(App.get());
        }
        return instance_;
    }

    public WorksManager() {
        super(Works.class);
        restPath(BaseShareEditFragment.CONTENT_TYPE_WORKS);
        excludes("toc");
        cache(new PinnableCache(new MemoryCache(), Works.class));
    }

    public Lister<Works> worksLister(Uri uri) {
        return defaultLister().filter(DataFilter.fromUri(uri));
    }

    public WorksListMeta worksListMeta(Uri uri) throws DataLoadException, RestException {
        return (WorksListMeta) new RestClient("works/list_meta", WorksListMeta.class).get(RequestParam.queryString().appendUriQuery(uri));
    }

    public Lister<Works> listPurchased() {
        return defaultLister().restPath("library");
    }

    public Lister<Works> searchStore(CharSequence keyword) {
        return defaultLister().restPath("/search").filter(new DataFilter().append("word", keyword));
    }

    public Lister<Works> searchLibrary(CharSequence keyword) {
        return defaultLister().restPath("/search/library").filter(new DataFilter().append("word", keyword));
    }

    public List<String> hotSearchWordsList() throws DataLoadException {
        try {
            return new RestClient("/search/hot_words", String.class).lister().list();
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public List<Tag> worksTags(int worksId) throws DataLoadException {
        return getTagManager(worksId).list().loadAll();
    }

    public Works getWorks(int worksId) throws DataLoadException {
        return (Works) get((Object) Integer.valueOf(worksId));
    }

    public Manifest getManifest(int worksId) throws DataLoadException {
        try {
            return (Manifest) getSubManagerForId(Integer.valueOf(worksId), "manifest", Manifest.class).post((JsonRequestParam) ((JsonRequestParam) RequestParam.json().append("pk", ArkKeyPair.getPublic())).append(ShareRequestParam.REQ_PARAM_VERSION, Integer.valueOf(3)));
        } catch (Throwable e) {
            Crashlytics.logException(e);
            throw new DataLoadException(e);
        }
    }

    public Works getFromRemote(Object id) throws DataLoadException {
        Works works = (Works) super.getFromRemote(id);
        EventBusUtils.post(new WorksUpdatedEvent(((Integer) id).intValue()));
        return works;
    }

    public Works getWorksByLegacyColumnId(int columnId) throws DataLoadException {
        try {
            Works works = (Works) new RestClient("column/mapping", Works.class).get(RequestParam.queryString().append(WorksListUri.KEY_ID, Integer.valueOf(columnId)));
            addToCache(works);
            return works;
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public List<Works> getSimilarWorks(int worksId) throws DataLoadException {
        return defaultLister().restPathWithId(Integer.valueOf(worksId), "similar").loadAll();
    }

    public List<TocItem> getWorksToc(int worksId) throws DataLoadException {
        return getSubManagerForId(Integer.valueOf(worksId), "toc", TocItem.class).list().loadAll();
    }

    public void shareWorks(int worksId, ShareTo shareTo, String text) throws DataLoadException {
        getSubManagerForId(Integer.valueOf(worksId), "rec").post(((JsonRequestParam) RequestParam.json().appendShareTo(shareTo)).appendIfNotEmpty("text", text));
    }

    public void shareRange(int worksId, ShareTo shareTo, Range range, String comment) throws DataLoadException {
        getSubManagerForId(Integer.valueOf(worksId), "share_piece").post(((JsonRequestParam) ((JsonRequestParam) ((JsonRequestParam) RequestParam.json().appendShareTo(shareTo)).appendRange(range)).appendIf(((Works) get((Object) Integer.valueOf(worksId))).isColumnOrSerial(), "chapter_id", Integer.valueOf(range.startPosition.packageId))).appendIfNotEmpty("text", comment));
    }

    public void correctRange(int worksId, Range range, String comment) throws DataLoadException {
        getSubManagerForId(Integer.valueOf(worksId), "erratum").post(((JsonRequestParam) RequestParam.json().appendRange(range)).appendIfNotEmpty(BaseShareEditFragment.CONTENT_TYPE_NOTE, comment));
    }

    private BaseManager<Tag> getTagManager(int worksId) {
        return getSubManagerForId(Integer.valueOf(worksId), "tags", Tag.class);
    }

    public void subscribe(int worksId) throws DataLoadException {
        try {
            getSubscriptionClient(worksId).put();
            Analysis.sendEventWithExtra("subscription", "subscribe", String.valueOf(worksId));
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public void unSubscribe(int worksId) throws DataLoadException {
        try {
            getSubscriptionClient(worksId).deleteEntity();
            Analysis.sendEventWithExtra("subscription", "unsubscribe", String.valueOf(worksId));
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    private RestClient<DummyEntity> getSubscriptionClient(int worksId) {
        return getRestClient().getSubClientWithId(Integer.valueOf(worksId), "subscription", DummyEntity.class);
    }

    public void addNotifiee(int worksId) throws DataLoadException {
        try {
            getNotifieeClient(worksId).put();
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public void removeNotifiee(int worksId) throws DataLoadException {
        try {
            getNotifieeClient(worksId).deleteEntity();
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    private RestClient<DummyEntity> getNotifieeClient(int worksId) {
        return getRestClient().getSubClientWithId(Integer.valueOf(worksId), "notifiee", DummyEntity.class);
    }
}
