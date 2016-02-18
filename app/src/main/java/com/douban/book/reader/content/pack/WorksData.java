package com.douban.book.reader.content.pack;

import android.net.Uri;
import android.util.SparseArray;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Manifest.PackMeta;
import com.douban.book.reader.exception.ManifestException;
import com.douban.book.reader.exception.PackageException;
import com.douban.book.reader.exception.PackageSizeThresholdExceededException;
import com.douban.book.reader.exception.WorksException;
import com.douban.book.reader.helper.DownloadHelper;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ZipUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import u.aly.dx;

public class WorksData {
    public static final int DOWNLOAD_CONFIRM_THRESHOLD = 1048576;
    private static SparseArray<WorksData> hInstances;
    private File mCompositeFile;
    private DownloadHelper mDownloadHelper;
    private Uri mUri;
    private int mWorksId;

    public enum Status {
        EMPTY,
        PENDING,
        DOWNLOADING,
        READY,
        PAUSED,
        FAILED
    }

    static {
        hInstances = new SparseArray();
    }

    public static WorksData get(int worksId) {
        WorksData worksData = (WorksData) hInstances.get(worksId);
        if (worksData != null) {
            return worksData;
        }
        worksData = new WorksData(worksId);
        hInstances.put(worksId, worksData);
        return worksData;
    }

    public WorksData(int worksId) {
        this.mCompositeFile = null;
        this.mDownloadHelper = null;
        this.mWorksId = worksId;
        this.mUri = ReaderUri.works(worksId);
        this.mCompositeFile = FilePath.composite(this.mWorksId);
        this.mDownloadHelper = new DownloadHelper(this.mUri, this.mCompositeFile);
        hInstances.put(worksId, this);
    }

    public Package getPackage(int packageId) {
        return Package.get(this.mWorksId, packageId);
    }

    public Status getStatus() {
        if (DownloadManager.isScheduled(this.mWorksId)) {
            if (DownloadManager.isDownloading(this.mWorksId)) {
                return Status.DOWNLOADING;
            }
            return Status.PENDING;
        } else if (isLastCheckSucceed()) {
            return Status.READY;
        } else {
            if (isDownloadPaused()) {
                return Status.PAUSED;
            }
            if (hasShelfChecked()) {
                return Status.FAILED;
            }
            return Status.EMPTY;
        }
    }

    public boolean isReady() {
        return getStatus() == Status.READY;
    }

    public boolean isPartial() {
        try {
            Manifest manifest = Manifest.get(this.mWorksId);
            if (manifest.hasComposite()) {
                return Pref.ofWorks(this.mWorksId).getBoolean(Key.WORKS_PACKAGE_IS_PARTIAL, false);
            }
            for (PackMeta packMeta : manifest.packages) {
                if (getPackage(packMeta.id).getStatus() != Status.READY) {
                    return true;
                }
            }
            return Pref.ofWorks(this.mWorksId).getBoolean(Key.WORKS_PACKAGE_IS_PARTIAL, false);
        } catch (ManifestException e) {
        }
    }

    public void setIsPartial(boolean isPartial) {
        Pref.ofWorks(this.mWorksId).set(Key.WORKS_PACKAGE_IS_PARTIAL, Boolean.valueOf(isPartial));
    }

    public void setIsDownloadPaused(boolean isDownloadPaused) {
        Pref.ofWorks(this.mWorksId).set(Key.WORKS_PACKAGE_IS_DOWNLOAD_PAUSED, Boolean.valueOf(isDownloadPaused));
    }

    public static InputStream getInputStream(Uri uri) throws IOException {
        int type = ReaderUriUtils.getType(uri);
        switch (type) {
            case dx.d /*3*/:
            case dx.e /*4*/:
                int worksId = ReaderUriUtils.getWorksId(uri);
                int packageId = ReaderUriUtils.getPackageId(uri);
                return get(worksId).getPackage(packageId).getIllusInputStream(ReaderUriUtils.getIllusSeq(uri), type == 4 ? ImageSize.LARGE : ImageSize.NORMAL);
            default:
                return null;
        }
    }

    public int getDownloadProgress() {
        try {
            Manifest manifest = Manifest.get(this.mWorksId);
            if (manifest.hasComposite()) {
                return this.mDownloadHelper.getDownloadProgress();
            }
            long totalSize = 0;
            long downloadedSize = 0;
            for (PackMeta packMeta : manifest.packages) {
                if (StringUtils.isNotEmpty(packMeta.url, packMeta.etag)) {
                    Package pack = getPackage(((PackMeta) r10.next()).id);
                    long packageTotalSize = pack.getTotalSize();
                    if (packageTotalSize < 0) {
                        totalSize = -1;
                        break;
                    }
                    totalSize += packageTotalSize;
                    downloadedSize += pack.getCurrentSize();
                }
            }
            if (downloadedSize == 0) {
                return 0;
            }
            if (totalSize <= 0) {
                return -1;
            }
            return Math.min(100, Math.round((float) ((100 * downloadedSize) / totalSize)));
        } catch (ManifestException e) {
            return -1;
        }
    }

    public int getDisplayDownloadProgress() {
        return Math.max(0, getDownloadProgress());
    }

    public void download(boolean withSizeLimit) throws PackageException {
        try {
            Manifest manifest = Manifest.load(this.mWorksId);
            long remainedSize;
            if (manifest.hasComposite()) {
                URL url = new URL(manifest.composite.url);
                if (withSizeLimit && !Utils.isUsingWifi()) {
                    this.mDownloadHelper.obtainPackageSize(url, manifest.composite.etag);
                    remainedSize = this.mDownloadHelper.getRemainedSize();
                    if (remainedSize > 1048576) {
                        throw new PackageSizeThresholdExceededException(remainedSize);
                    }
                }
                this.mDownloadHelper.download(url, manifest.composite.etag);
                ZipUtils.unzip(this.mCompositeFile);
                for (PackMeta packMeta : manifest.packages) {
                    if (StringUtils.isNotEmpty(packMeta.url, packMeta.etag)) {
                        getPackage(((PackMeta) r7.next()).id).close();
                    }
                }
            } else {
                remainedSize = 0;
                for (PackMeta packMeta2 : manifest.packages) {
                    if (StringUtils.isNotEmpty(packMeta2.url, packMeta2.etag)) {
                        Package pack = getPackage(((PackMeta) r7.next()).id);
                        pack.obtainPackageSize();
                        remainedSize += pack.getRemainedSize();
                    }
                }
                if (!withSizeLimit || Utils.isUsingWifi() || remainedSize <= 1048576) {
                    for (PackMeta packMeta22 : manifest.packages) {
                        if (StringUtils.isNotEmpty(packMeta22.url, packMeta22.etag)) {
                            getPackage(((PackMeta) r7.next()).id).download();
                        }
                    }
                } else {
                    throw new PackageSizeThresholdExceededException(remainedSize);
                }
            }
            doSelfCheck();
        } catch (Throwable e) {
            Pref.ofWorks(this.mWorksId).set(Key.WORKS_PACKAGE_IS_LAST_CHECK_SUCCEED, Boolean.valueOf(false));
            throw new PackageException(e);
        }
    }

    public void delete() {
        try {
            for (PackMeta packMeta : Manifest.get(this.mWorksId).packages) {
                try {
                    getPackage(packMeta.id).delete();
                } catch (IOException e) {
                    Logger.e(Tag.PACKAGE, e);
                }
            }
        } catch (ManifestException e2) {
            Logger.e(Tag.PACKAGE, e2);
        }
        Pref.ofWorks(this.mWorksId).clear();
        Pref.ofObj(this.mUri).clear();
        FileUtils.deleteDir(FilePath.works(this.mWorksId));
    }

    public boolean newVersionAvailable() {
        try {
            Manifest manifest = Manifest.get(this.mWorksId);
            if (manifest.composite != null) {
                if (StringUtils.isNotEmpty(manifest.composite.url)) {
                    if (StringUtils.equals(manifest.composite.etag, Pref.ofObj(this.mUri).getString(Key.PACKAGE_ETAG))) {
                        return false;
                    }
                    return true;
                }
            }
            for (PackMeta packMeta : manifest.packages) {
                Package pack = getPackage(packMeta.id);
                if (pack.getStatus() == Status.READY && pack.newVersionAvailable()) {
                    return true;
                }
            }
            return false;
        } catch (ManifestException e) {
            return true;
        }
    }

    public void doSelfCheck() throws WorksException {
        try {
            for (PackMeta packMeta : Manifest.get(this.mWorksId).packages) {
                if (StringUtils.isNotEmpty(packMeta.url, packMeta.etag)) {
                    getPackage(((PackMeta) r3.next()).id).forceSelfCheck();
                }
            }
            Pref.ofWorks(this.mWorksId).set(Key.WORKS_PACKAGE_IS_LAST_CHECK_SUCCEED, Boolean.valueOf(true));
        } catch (Throwable e) {
            Pref.ofWorks(this.mWorksId).set(Key.WORKS_PACKAGE_IS_LAST_CHECK_SUCCEED, Boolean.valueOf(false));
            throw new PackageException(e);
        }
    }

    private boolean hasShelfChecked() {
        return Pref.ofWorks(this.mWorksId).contains(Key.WORKS_PACKAGE_IS_LAST_CHECK_SUCCEED);
    }

    private boolean isLastCheckSucceed() {
        return Pref.ofWorks(this.mWorksId).getBoolean(Key.WORKS_PACKAGE_IS_LAST_CHECK_SUCCEED, false);
    }

    private boolean isDownloadPaused() {
        return Pref.ofWorks(this.mWorksId).getBoolean(Key.WORKS_PACKAGE_IS_DOWNLOAD_PAUSED, false);
    }
}
