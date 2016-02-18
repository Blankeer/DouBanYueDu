package com.douban.book.reader.manager;

import android.net.Uri;
import android.util.LruCache;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.data.DataStore;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.ReaderUriUtils;
import java.util.Map;
import org.mapdb.BTreeMap;

public class PageInfoManager {
    private static LruCache<Uri, PageInfoManager> sInstances;
    private int mPackageId;
    private PageMetrics mPageMetrics;
    private int mWorksId;

    static {
        sInstances = new LruCache(5);
    }

    public static PageInfoManager of(Uri uri) {
        PageInfoManager instance = (PageInfoManager) sInstances.get(uri);
        if (instance != null) {
            return instance;
        }
        instance = new PageInfoManager(uri);
        sInstances.put(uri, instance);
        return instance;
    }

    public PageInfoManager(Uri uri) {
        this.mWorksId = ReaderUriUtils.getWorksId(uri);
        this.mPackageId = ReaderUriUtils.getPackageId(uri);
    }

    public void setCurrentPageMetrics(PageMetrics pageMetrics) {
        this.mPageMetrics = pageMetrics;
    }

    public boolean isPageMapValid() {
        if (DebugSwitch.on(Key.APP_DEBUG_SKIP_PAGING_CACHE) || getPageInfoMap() == null || getPageInfoMap().isEmpty()) {
            return false;
        }
        Object metaData = DataStore.ofWorks(this.mWorksId).getMapMeta(getPageInfoMapName());
        if ((metaData instanceof Integer) && ((Integer) metaData).intValue() == AppInfo.getVersionCode()) {
            return true;
        }
        return false;
    }

    public void markPagingSucceed(boolean succeed) {
        if (succeed) {
            DataStore.ofWorks(this.mWorksId).setMapMeta(getPageInfoMapName(), Integer.valueOf(AppInfo.getVersionCode()));
        } else {
            DataStore.ofWorks(this.mWorksId).clearMapMeta(getPageInfoMapName());
        }
    }

    public void addPageInfo(int pageNo, PageInfo pageInfo) {
        getPageInfoMap().put(Integer.valueOf(pageNo), pageInfo);
    }

    public PageInfo getPageInfo(int pageNo) {
        return (PageInfo) getPageInfoMap().get(Integer.valueOf(pageNo));
    }

    public int getPageCount() {
        try {
            return ((Integer) ((BTreeMap) getPageInfoMap()).lastKey()).intValue() + 1;
        } catch (Throwable th) {
            return 0;
        }
    }

    public void clear() {
        getPageInfoMap().clear();
    }

    private Map<Integer, PageInfo> getPageInfoMap() {
        return DataStore.ofWorks(this.mWorksId).getTreeMap(getPageInfoMapName());
    }

    private String getPageInfoMapName() {
        return String.format("page_map_%s_%s", new Object[]{Integer.valueOf(this.mPackageId), this.mPageMetrics});
    }
}
