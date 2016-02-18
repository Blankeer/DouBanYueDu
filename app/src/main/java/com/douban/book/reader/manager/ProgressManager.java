package com.douban.book.reader.manager;

import android.util.LruCache;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.chapter.Chapter;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.entity.Progress;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.ReadingProgressUpdatedEvent;
import com.douban.book.reader.event.RemoteProgressLoadedEvent;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;

public class ProgressManager extends BaseLegacySyncedManager<Progress> {
    private static final String LOCAL_PROGRESS_PREF_KEY = "local_progress";
    private static LruCache<Integer, ProgressManager> sInstances;
    private Progress mHistoryProgress;
    private Progress mLocalProgress;
    private Progress mRemoteProgress;
    private int mWorksId;

    static {
        sInstances = new LruCache(5);
    }

    public static ProgressManager ofWorks(int worksId) {
        ProgressManager instance = (ProgressManager) sInstances.get(Integer.valueOf(worksId));
        if (instance != null) {
            return instance;
        }
        instance = new ProgressManager(worksId);
        sInstances.put(Integer.valueOf(worksId), instance);
        return instance;
    }

    public ProgressManager(int worksId) {
        super(Progress.class);
        this.mWorksId = worksId;
        restPath(String.format("works/%s/progress", new Object[]{Integer.valueOf(worksId)}));
    }

    public void refresh() throws DataLoadException {
        this.mRemoteProgress = getRemoteProgressFromNetwork();
        EventBusUtils.post(new RemoteProgressLoadedEvent(this.mWorksId, this.mRemoteProgress));
    }

    public Progress getRemoteProgress() {
        Logger.d(this.TAG, "getRemoteProgress: return %s", this.mRemoteProgress);
        return this.mRemoteProgress;
    }

    public Progress getLocalProgress() {
        if (this.mLocalProgress == null) {
            this.mLocalProgress = (Progress) Pref.ofWorks(this.mWorksId).getObject(LOCAL_PROGRESS_PREF_KEY, Progress.class);
            Logger.d(this.TAG, "getLocalProgress from pref: %s", this.mLocalProgress);
        }
        if (this.mLocalProgress == null) {
            this.mLocalProgress = getRemoteProgress();
            Logger.d(this.TAG, "getLocalProgress from remoteProgress: %s", this.mLocalProgress);
        }
        if (this.mLocalProgress == null) {
            this.mLocalProgress = createProgressForPage(0);
            Logger.d(this.TAG, "getLocalProgress by create page 0: %s", this.mLocalProgress);
        }
        Logger.d(this.TAG, "getLocalProgress returned: %s", this.mLocalProgress);
        return this.mLocalProgress;
    }

    public void setLocalProgress(Progress progress) {
        this.mLocalProgress = progress;
        Pref.ofWorks(this.mWorksId).set(LOCAL_PROGRESS_PREF_KEY, progress);
        EventBusUtils.post(new ReadingProgressUpdatedEvent(this.mWorksId));
    }

    public void setLocalProgress(int page) {
        setLocalProgress(createProgressForPage(page));
    }

    public void updateRemoteProgress() {
        Logger.d(this.TAG, "updateRemoteProgress from %s to %s", this.mRemoteProgress, getLocalProgress());
        Progress progress = getLocalProgress();
        this.mRemoteProgress = progress;
        if (progress != null && progress.packageId == Chapter.ID_PURCHASE) {
            progress = createProgressForLastContentPage();
        }
        if (progress != null && progress.packageId > 0 && progress.isPositionValid()) {
            asyncAddToRemote(progress);
        }
    }

    private Progress getRemoteProgressFromNetwork() throws DataLoadException {
        try {
            Progress progress = (Progress) getRestClient().getEntity();
            progress.worksId = this.mWorksId;
            return progress;
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public void pushProgress(Progress newProgress) {
        Progress currentProgress = getLocalProgress();
        if (currentProgress == null || newProgress == null || !currentProgress.getPosition().equals(newProgress.getPosition())) {
            this.mHistoryProgress = currentProgress;
            setLocalProgress(newProgress);
        }
    }

    public void pushProgress(int page) {
        pushProgress(createProgressForPage(page));
    }

    public void pushProgress(Position position) {
        pushProgress(createProgressForPosition(position));
    }

    public void toggleHistoryProgress() {
        if (hasHistoryProgress()) {
            pushProgress(this.mHistoryProgress);
        }
    }

    public boolean hasHistoryProgress() {
        return this.mHistoryProgress != null;
    }

    private Progress createProgressForPage(int page) {
        PageInfo pageInfo = Book.get(this.mWorksId).getPageInfo(page);
        if (pageInfo == null) {
            return null;
        }
        return createProgressForPosition(pageInfo.getRange().endPosition);
    }

    private Progress createProgressForPosition(Position position) {
        Book book = Book.get(this.mWorksId);
        Progress progress = new Progress();
        progress.setPosition(position);
        progress.worksId = this.mWorksId;
        progress.packageId = book.getPackageId(position.packageIndex);
        progress.paragraphId = position.paragraphId;
        progress.paragraphOffset = position.paragraphOffset;
        return progress;
    }

    private Progress createProgressForLastContentPage() {
        Book book = Book.get(this.mWorksId);
        return createProgressForPage((book.getPageCount() - book.getBackCoverPageCount()) - 1);
    }
}
