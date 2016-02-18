package com.douban.book.reader.manager;

import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.WorksKind;
import com.douban.book.reader.manager.cache.PrefCache;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.manager.exception.NoSuchDataException;
import com.douban.book.reader.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class WorksKindManager extends BaseManager<WorksKind> {
    public static final String PATH = "works/kinds";

    public WorksKindManager() {
        super(PATH, WorksKind.class);
        cache(new PrefCache(Key.APP_PREF_CACHE_WORKS_KIND, WorksKind.class));
    }

    public void checkUpdateWorksKinds() {
        try {
            defaultLister().sync();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    public WorksKind getRootKind(WorksKind childWorksKind) throws DataLoadException {
        if (childWorksKind == null) {
            return null;
        }
        if (childWorksKind.isRoot) {
            return childWorksKind;
        }
        return getRootKind((WorksKind) getFromCache(Integer.valueOf(childWorksKind.parentId)));
    }

    public List<WorksKind> getRootKinds() throws NoSuchDataException {
        List<WorksKind> worksKindList = new ArrayList();
        List<WorksKind> rootKindList = new ArrayList();
        try {
            worksKindList = getAllFromCache();
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
        if (worksKindList.size() <= 0) {
            try {
                worksKindList = defaultLister().loadAll();
            } catch (Exception e2) {
                Logger.e(this.TAG, e2);
            }
        }
        for (WorksKind worksKind : worksKindList) {
            if (worksKind.isRoot) {
                rootKindList.add(worksKind);
            }
        }
        Collections.sort(rootKindList);
        if (rootKindList.size() > 0) {
            return rootKindList;
        }
        throw new NoSuchDataException("no root works kind");
    }

    public List<WorksKind> getChildList(int parentId) throws DataLoadException {
        WorksKind parentKind = (WorksKind) getFromCache(Integer.valueOf(parentId));
        if (parentKind == null) {
            parentKind = (WorksKind) get((Object) Integer.valueOf(parentId));
        }
        List<WorksKind> childList = new ArrayList();
        for (int id : parentKind.childIds) {
            WorksKind worksKind;
            try {
                worksKind = (WorksKind) getFromCache(Integer.valueOf(id));
                if (worksKind != null) {
                    childList.add(worksKind);
                }
            } catch (Exception e) {
            }
            try {
                worksKind = (WorksKind) get((Object) Integer.valueOf(id));
                if (worksKind != null) {
                    childList.add(worksKind);
                }
            } catch (Exception e2) {
            }
        }
        return childList;
    }
}
