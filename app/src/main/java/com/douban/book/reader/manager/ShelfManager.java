package com.douban.book.reader.manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.ShelfItem;
import com.douban.book.reader.entity.ShelfItem.Column;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.NewShelfItemAddedEvent;
import com.douban.book.reader.event.ShelfClearedEvent;
import com.douban.book.reader.event.ShelfNewWorksCountChangedEvent;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.manager.cache.DbCache;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class ShelfManager {
    private static final String TAG;
    private DbCache<ShelfItem> mCache;
    private HashSet<Integer> mNewAddedWorks;
    @Bean
    WorksManager mWorksManager;

    /* renamed from: com.douban.book.reader.manager.ShelfManager.1 */
    class AnonymousClass1 extends SimpleImageLoadingListener {
        final /* synthetic */ Works val$works;
        final /* synthetic */ int val$worksId;

        AnonymousClass1(int i, Works works) {
            this.val$worksId = i;
            this.val$works = works;
        }

        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra("android.intent.extra.shortcut.INTENT", ShelfManager.this.getShortcutIntent(this.val$worksId));
            addIntent.putExtra("android.intent.extra.shortcut.NAME", this.val$works.title);
            addIntent.putExtra("android.intent.extra.shortcut.ICON", loadedImage);
            addIntent.putExtra("duplicate", false);
            App.get().sendBroadcast(addIntent);
            ToastUtils.showToast((int) R.string.toast_add_shortcut_succeed);
        }
    }

    public ShelfManager() {
        this.mCache = new DbCache(ShelfItem.class);
        this.mNewAddedWorks = new HashSet();
    }

    static {
        TAG = ShelfManager.class.getSimpleName();
    }

    public void addWorks(int worksId) throws DataLoadException {
        if (!hasWorks(worksId)) {
            Works works = this.mWorksManager.getWorks(worksId);
            this.mWorksManager.pin(Integer.valueOf(worksId));
            ShelfItem shelfItem = new ShelfItem();
            shelfItem.id = worksId;
            shelfItem.lastTouchedTime = new Date();
            shelfItem.title = works.title;
            this.mCache.add(shelfItem);
            this.mNewAddedWorks.add(Integer.valueOf(worksId));
            EventBusUtils.post(new ShelfNewWorksCountChangedEvent());
            EventBusUtils.post(new NewShelfItemAddedEvent());
        }
    }

    public void clearNewAddedWorksMark() {
        this.mNewAddedWorks.clear();
        EventBusUtils.post(new ShelfNewWorksCountChangedEvent());
    }

    public boolean hasNewAddedWorks() {
        return this.mNewAddedWorks.size() > 0;
    }

    public boolean hasWorks(int worksId) {
        try {
            return this.mCache.getDao().queryForId(Integer.valueOf(worksId)) != null;
        } catch (SQLException e) {
            Logger.e(Tag.GENERAL, e);
            return false;
        }
    }

    public ShelfItem get(int worksId) throws DataLoadException {
        return (ShelfItem) this.mCache.get(Integer.valueOf(worksId));
    }

    public void delete(int worksId) throws DataLoadException {
        removeShortCut(worksId);
        this.mCache.delete(Integer.valueOf(worksId));
        this.mWorksManager.unpin(Integer.valueOf(worksId));
        WorksData.get(worksId).delete();
    }

    public void clear() throws DataLoadException {
        DataLoadException lastException = null;
        for (ShelfItem item : getAll()) {
            try {
                delete(item.id);
            } catch (DataLoadException e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw new DataLoadException((Throwable) lastException);
        }
        EventBusUtils.post(new ShelfClearedEvent());
    }

    public int getCount() throws DataLoadException {
        try {
            return this.mCache.getDao().queryBuilder().query().size();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public List<ShelfItem> getAll() throws DataLoadException {
        try {
            return this.mCache.getDao().queryBuilder().orderBy(Column.LAST_TOUCHED_TIME, false).query();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void worksOpened(int worksId) throws DataLoadException {
        if (this.mNewAddedWorks.remove(Integer.valueOf(worksId))) {
            EventBusUtils.post(new ShelfNewWorksCountChangedEvent());
        }
        ShelfItem item = (ShelfItem) this.mCache.get(Integer.valueOf(worksId));
        if (item != null) {
            item.lastTouchedTime = new Date();
            this.mCache.add(item);
        }
    }

    public void refreshShelfItems() throws DataLoadException {
        Throwable lastException = null;
        for (ShelfItem shelfItem : getAll()) {
            try {
                this.mWorksManager.getFromRemote(Integer.valueOf(shelfItem.id));
            } catch (Throwable e) {
                lastException = e;
            }
            try {
                Manifest.loadFromNetwork(shelfItem.id);
            } catch (Throwable e2) {
                lastException = e2;
            }
        }
        if (lastException != null) {
            throw new DataLoadException(lastException);
        }
    }

    public void addShortCut(int worksId) throws DataLoadException {
        Works works = (Works) this.mWorksManager.getFromCache(Integer.valueOf(worksId));
        ImageLoaderUtils.loadImage(works.coverUrl, new ImageSize(Utils.dp2pixel(48.0f), Utils.dp2pixel(48.0f)), new AnonymousClass1(worksId, works));
    }

    private void removeShortCut(int worksId) {
        try {
            Works works = (Works) this.mWorksManager.getFromCache(Integer.valueOf(worksId));
            Intent addIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
            addIntent.putExtra("android.intent.extra.shortcut.INTENT", getShortcutIntent(worksId));
            addIntent.putExtra("android.intent.extra.shortcut.NAME", works.title);
            App.get().sendBroadcast(addIntent);
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
    }

    private Intent getShortcutIntent(int worksId) {
        Intent intent = new Intent("android.intent.action.VIEW", AppUri.reader(worksId));
        intent.addFlags(67108864);
        return intent;
    }
}
