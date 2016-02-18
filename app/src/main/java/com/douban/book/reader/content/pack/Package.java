package com.douban.book.reader.content.pack;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.cipher.CipherFactory;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Manifest.PackMeta;
import com.douban.book.reader.entity.Manifest.PackagePayload;
import com.douban.book.reader.exception.CipherException;
import com.douban.book.reader.exception.DataException;
import com.douban.book.reader.exception.ManifestException;
import com.douban.book.reader.exception.PackageException;
import com.douban.book.reader.exception.WorksException;
import com.douban.book.reader.helper.DownloadHelper;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.StringUtils;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import io.realm.internal.Table;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.zip.ZipFile;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import u.aly.dx;

public class Package {
    private static final String[] CHAPTER_FILE_ENTRIES;
    private static final String TAG;
    private static SparseArray<Package> hInstances;
    private CipherFactory mCipherFactory;
    private DownloadHelper mDownloadHelper;
    private File mFile;
    private int mPackageId;
    private Uri mUri;
    private int mWorksId;
    private ZipFile mZipFile;

    static {
        TAG = Package.class.getSimpleName();
        CHAPTER_FILE_ENTRIES = new String[]{WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT};
        hInstances = new SparseArray();
    }

    public Package(int worksId, int packageId) {
        this.mUri = null;
        this.mFile = null;
        this.mZipFile = null;
        this.mCipherFactory = null;
        this.mDownloadHelper = null;
        this.mWorksId = worksId;
        this.mPackageId = packageId;
        this.mUri = ReaderUri.pack(this.mWorksId, this.mPackageId);
        this.mFile = getFile();
        this.mDownloadHelper = new DownloadHelper(this.mUri, this.mFile);
        addToCache(packageId, this);
    }

    public static Package get(Uri uri) throws PackageException {
        switch (ReaderUriUtils.getType(uri)) {
            case dx.c /*2*/:
            case dx.d /*3*/:
            case dx.e /*4*/:
                return get(ReaderUriUtils.getWorksId(uri), ReaderUriUtils.getPackageId(uri));
            default:
                throw new PackageException(String.format("Unsupported uri %s", new Object[]{uri}));
        }
    }

    public static Package get(int bookId, int packageId) {
        Package pack = (Package) hInstances.get(packageId);
        if (pack == null) {
            return new Package(bookId, packageId);
        }
        return pack;
    }

    public static void clearFromCache(int bookId) {
        hInstances.delete(bookId);
    }

    private static void addToCache(int bookId, Package pack) {
        if (pack != null) {
            hInstances.append(bookId, pack);
        }
    }

    public int getDownloadProgress() {
        return this.mDownloadHelper.getDownloadProgress();
    }

    public long getCurrentSize() {
        return this.mDownloadHelper.getCurrentSize();
    }

    public long getRemainedSize() {
        return this.mDownloadHelper.getRemainedSize();
    }

    public long getTotalSize() {
        return this.mDownloadHelper.getTotalSize();
    }

    public void selfCheck() throws WorksException {
        if (!isPackageReady()) {
            if (fileUpdatedAfterLastCheck()) {
                throw new PackageException("Check failed. (No data update since last failed check.)");
            }
            doSelfCheck();
        }
    }

    public void forceSelfCheck() throws PackageException {
        doSelfCheck();
    }

    public boolean newVersionAvailable() {
        CharSequence etag = Pref.ofObj(this.mUri).getString(Key.PACKAGE_ETAG);
        PackMeta packMeta = getMetaData();
        if (packMeta == null || !StringUtils.equals(packMeta.etag, etag)) {
            return true;
        }
        return false;
    }

    public void open() throws IOException {
        if (this.mZipFile == null) {
            this.mFile = getFile();
            this.mZipFile = new ZipFile(this.mFile);
        }
    }

    public void close() throws IOException {
        if (this.mZipFile != null) {
            this.mZipFile.close();
        }
        this.mZipFile = null;
    }

    public void delete() throws IOException {
        close();
        if (!this.mFile.exists() || this.mFile.delete()) {
            Pref.ofPackage(this.mPackageId).clear();
            Pref.ofObj(this.mUri).clear();
            return;
        }
        throw new IOException("failed to delete file " + this.mFile.getPath());
    }

    public Status getStatus() {
        if (DownloadManager.isScheduled(this.mUri)) {
            if (DownloadManager.isDownloading(this.mUri)) {
                return Status.DOWNLOADING;
            }
            return Status.PENDING;
        } else if (!this.mFile.exists()) {
            return Status.EMPTY;
        } else {
            if (isPackageReady()) {
                return Status.READY;
            }
            return Status.FAILED;
        }
    }

    public InputStream getInputStream(String entryPath) throws IOException {
        DataException e;
        if (DebugSwitch.on(Key.APP_DEBUG_DECIPHER_PACKAGE_ENTRIES)) {
            decipherEntry(entryPath);
        }
        try {
            open();
            return new CipherInputStream(new BufferedInputStream(this.mZipFile.getInputStream(this.mZipFile.getEntry(entryPath))), getCipher());
        } catch (CipherException e2) {
            e = e2;
            throw new IOException(e);
        } catch (ManifestException e3) {
            e = e3;
            throw new IOException(e);
        }
    }

    public InputStream getIllusInputStream(int illusSeq, ImageSize size) throws IOException {
        String path = getValidIllusPath(illusSeq, size);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getInputStream(path);
    }

    public Drawable getIllusDrawable(int illusSeq) {
        try {
            return Drawable.createFromStream(getIllusInputStream(illusSeq, ImageSize.NORMAL), Table.STRING_DEFAULT_VALUE);
        } catch (IOException e) {
            return null;
        }
    }

    public void obtainPackageSize() throws WorksException, IOException, InterruptedException {
        PackMeta packMeta = Manifest.load(this.mWorksId).getPackMeta(this.mPackageId);
        this.mDownloadHelper.obtainPackageSize(new URL(packMeta.url), packMeta.etag);
    }

    public void download() throws WorksException, IOException, InterruptedException {
        PackMeta packMeta = Manifest.load(this.mWorksId).getPackMeta(this.mPackageId);
        URL url = new URL(packMeta.url);
        close();
        this.mDownloadHelper.download(url, packMeta.etag);
        try {
            doSelfCheck();
        } catch (PackageException e) {
            Pref.ofObj(this.mUri).remove(Key.PACKAGE_ETAG);
            throw e;
        }
    }

    public PackMeta getMetaData() {
        try {
            return Manifest.get(this.mWorksId).getPackMeta(this.mPackageId);
        } catch (ManifestException e) {
            return null;
        }
    }

    private PackagePayload getPayload() {
        if (getMetaData() == null) {
            return null;
        }
        return getMetaData().payload;
    }

    public String getTitle() {
        if (getPayload() == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return getPayload().title;
    }

    public String getAbstractText() {
        if (getPayload() == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return getPayload().abstractText;
    }

    public int getPrice() {
        if (getPayload() == null) {
            return 0;
        }
        return getPayload().price;
    }

    public Date getPublishDate() {
        if (getPayload() == null) {
            return null;
        }
        return getPayload().publishTime;
    }

    public boolean isPurchaseNeeded() {
        return getMetaData() == null || StringUtils.isEmpty(getMetaData().url);
    }

    private String getIllusPath(int illusSeq, ImageSize size) throws IOException {
        String path = String.format("image/%s", new Object[]{Integer.valueOf(illusSeq)});
        if (size == ImageSize.LARGE) {
            path = path + "l";
        }
        open();
        return (this.mZipFile == null || this.mZipFile.getEntry(path) == null) ? null : path;
    }

    private String getValidIllusPath(int illusSeq, ImageSize size) throws IOException {
        String path = getIllusPath(illusSeq, size);
        if (!TextUtils.isEmpty(path)) {
            return path;
        }
        if (size == ImageSize.NORMAL) {
            size = ImageSize.LARGE;
        } else {
            size = ImageSize.NORMAL;
        }
        return getIllusPath(illusSeq, size);
    }

    private File getFile() {
        return FilePath.pack(this.mWorksId, this.mPackageId);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doSelfCheck() throws com.douban.book.reader.exception.PackageException {
        /*
        r11 = this;
        r5 = 0;
        r11.open();	 Catch:{ IOException -> 0x002f }
        r7 = CHAPTER_FILE_ENTRIES;	 Catch:{ IOException -> 0x002f }
        r8 = r7.length;	 Catch:{ IOException -> 0x002f }
        r6 = r5;
    L_0x0008:
        if (r6 >= r8) goto L_0x005f;
    L_0x000a:
        r1 = r7[r6];	 Catch:{ IOException -> 0x002f }
        r9 = r11.mZipFile;	 Catch:{ IOException -> 0x002f }
        r9 = r9.getEntry(r1);	 Catch:{ IOException -> 0x002f }
        if (r9 != 0) goto L_0x005c;
    L_0x0014:
        r6 = new com.douban.book.reader.exception.PackageException;	 Catch:{ IOException -> 0x002f }
        r7 = "Package %d missing entry: %s";
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ IOException -> 0x002f }
        r9 = 0;
        r10 = r11.mPackageId;	 Catch:{ IOException -> 0x002f }
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ IOException -> 0x002f }
        r8[r9] = r10;	 Catch:{ IOException -> 0x002f }
        r9 = 1;
        r8[r9] = r1;	 Catch:{ IOException -> 0x002f }
        r7 = java.lang.String.format(r7, r8);	 Catch:{ IOException -> 0x002f }
        r6.<init>(r7);	 Catch:{ IOException -> 0x002f }
        throw r6;	 Catch:{ IOException -> 0x002f }
    L_0x002f:
        r0 = move-exception;
        r6 = 0;
        r11.setPackageReady(r6);	 Catch:{ all -> 0x0057 }
        r6 = java.util.zip.ZipException.class;
        r6 = com.douban.book.reader.util.ExceptionUtils.isCausedBy(r0, r6);	 Catch:{ all -> 0x0057 }
        if (r6 == 0) goto L_0x0067;
    L_0x003c:
        com.crashlytics.android.Crashlytics.logException(r0);	 Catch:{ all -> 0x0057 }
    L_0x003f:
        r5 = new com.douban.book.reader.exception.PackageException;	 Catch:{ all -> 0x0057 }
        r6 = "Failed to open package %d";
        r7 = 1;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0057 }
        r8 = 0;
        r9 = r11.mPackageId;	 Catch:{ all -> 0x0057 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0057 }
        r7[r8] = r9;	 Catch:{ all -> 0x0057 }
        r6 = java.lang.String.format(r6, r7);	 Catch:{ all -> 0x0057 }
        r5.<init>(r6, r0);	 Catch:{ all -> 0x0057 }
        throw r5;	 Catch:{ all -> 0x0057 }
    L_0x0057:
        r5 = move-exception;
        r11.updateLastCheckedTime();
        throw r5;
    L_0x005c:
        r6 = r6 + 1;
        goto L_0x0008;
    L_0x005f:
        r6 = 1;
        r11.setPackageReady(r6);	 Catch:{ IOException -> 0x002f }
        r11.updateLastCheckedTime();
        return;
    L_0x0067:
        r6 = java.io.FileNotFoundException.class;
        r6 = com.douban.book.reader.util.ExceptionUtils.isCausedBy(r0, r6);	 Catch:{ all -> 0x0057 }
        if (r6 == 0) goto L_0x003f;
    L_0x006f:
        r6 = TAG;	 Catch:{ Throwable -> 0x00b9 }
        r7 = "Package data %s not found.";
        r8 = 1;
        r8 = new java.lang.Object[r8];	 Catch:{ Throwable -> 0x00b9 }
        r9 = 0;
        r10 = r11.getFile();	 Catch:{ Throwable -> 0x00b9 }
        r8[r9] = r10;	 Catch:{ Throwable -> 0x00b9 }
        com.douban.book.reader.util.Logger.dc(r6, r7, r8);	 Catch:{ Throwable -> 0x00b9 }
        r6 = r11.mWorksId;	 Catch:{ Throwable -> 0x00b9 }
        r2 = com.douban.book.reader.util.FilePath.packRoot(r6);	 Catch:{ Throwable -> 0x00b9 }
        r4 = r2.listFiles();	 Catch:{ Throwable -> 0x00b9 }
        r6 = r4.length;	 Catch:{ Throwable -> 0x00b9 }
    L_0x008b:
        if (r5 >= r6) goto L_0x009f;
    L_0x008d:
        r3 = r4[r5];	 Catch:{ Throwable -> 0x00b9 }
        r7 = TAG;	 Catch:{ Throwable -> 0x00b9 }
        r8 = "Found pack in path: %s";
        r9 = 1;
        r9 = new java.lang.Object[r9];	 Catch:{ Throwable -> 0x00b9 }
        r10 = 0;
        r9[r10] = r3;	 Catch:{ Throwable -> 0x00b9 }
        com.douban.book.reader.util.Logger.dc(r7, r8, r9);	 Catch:{ Throwable -> 0x00b9 }
        r5 = r5 + 1;
        goto L_0x008b;
    L_0x009f:
        r5 = TAG;	 Catch:{ Throwable -> 0x00b9 }
        r6 = "Connection: %s";
        r7 = 1;
        r7 = new java.lang.Object[r7];	 Catch:{ Throwable -> 0x00b9 }
        r8 = 0;
        r9 = com.douban.book.reader.util.Utils.getNetworkInfo();	 Catch:{ Throwable -> 0x00b9 }
        r7[r8] = r9;	 Catch:{ Throwable -> 0x00b9 }
        com.douban.book.reader.util.Logger.dc(r5, r6, r7);	 Catch:{ Throwable -> 0x00b9 }
        r5 = new com.douban.book.reader.exception.WrongPackageException;	 Catch:{ Throwable -> 0x00b9 }
        r5.<init>(r0);	 Catch:{ Throwable -> 0x00b9 }
        com.crashlytics.android.Crashlytics.logException(r5);	 Catch:{ Throwable -> 0x00b9 }
        goto L_0x003f;
    L_0x00b9:
        r5 = move-exception;
        goto L_0x003f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.content.pack.Package.doSelfCheck():void");
    }

    private long getLastModified() {
        return this.mFile.lastModified();
    }

    private void updateLastCheckedTime() {
        Pref.ofPackage(this.mPackageId).set(Key.PACKAGE_LAST_CHECKED_FILE_TIME, Long.valueOf(getLastModified()));
    }

    private boolean fileUpdatedAfterLastCheck() {
        return Pref.ofPackage(this.mPackageId).getLong(Key.PACKAGE_LAST_CHECKED_FILE_TIME, -1) < getLastModified();
    }

    private void setPackageReady(boolean ready) {
        Pref.ofPackage(this.mPackageId).set(Key.PACKAGE_IS_LAST_CHECK_SUCCEED, Boolean.valueOf(ready));
    }

    private boolean isPackageReady() {
        return !fileUpdatedAfterLastCheck() && Pref.ofPackage(this.mPackageId).getBoolean(Key.PACKAGE_IS_LAST_CHECK_SUCCEED, false);
    }

    private Cipher getCipher() throws CipherException, ManifestException {
        try {
            return doGetCipher();
        } catch (CipherException e) {
            Logger.dc(TAG, "Failed to get cipher for %s (%s). Trying to reload manifest", this, e);
            Manifest.loadFromNetwork(this.mWorksId);
            this.mCipherFactory = null;
            return doGetCipher();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private javax.crypto.Cipher doGetCipher() throws com.douban.book.reader.exception.CipherException {
        /*
        r7 = this;
        r4 = 1;
        r5 = 0;
        r2 = r7.mCipherFactory;
        if (r2 != 0) goto L_0x0027;
    L_0x0006:
        r1 = r7.getMetaData();
        if (r1 != 0) goto L_0x001c;
    L_0x000c:
        r2 = new com.douban.book.reader.exception.CipherException;
        r3 = "Failed loading metadata for package %s";
        r4 = new java.lang.Object[r4];
        r4[r5] = r7;
        r3 = java.lang.String.format(r3, r4);
        r2.<init>(r3);
        throw r2;
    L_0x001c:
        r2 = new com.douban.book.reader.content.cipher.CipherFactory;
        r3 = r1.key;
        r4 = r1.iv;
        r2.<init>(r3, r4);
        r7.mCipherFactory = r2;
    L_0x0027:
        r2 = r7.mCipherFactory;	 Catch:{ CipherException -> 0x002e }
        r2 = r2.getCipher();	 Catch:{ CipherException -> 0x002e }
        return r2;
    L_0x002e:
        r0 = move-exception;
        r2 = java.security.InvalidKeyException.class;
        r2 = com.douban.book.reader.util.ExceptionUtils.isCausedBy(r0, r2);	 Catch:{ Throwable -> 0x004d }
        if (r2 == 0) goto L_0x004c;
    L_0x0037:
        r2 = TAG;	 Catch:{ Throwable -> 0x004d }
        r3 = "Manifest.PackMeta: %s";
        r4 = 1;
        r4 = new java.lang.Object[r4];	 Catch:{ Throwable -> 0x004d }
        r5 = 0;
        r6 = r7.getMetaData();	 Catch:{ Throwable -> 0x004d }
        r6 = com.douban.book.reader.util.JsonUtils.toJson(r6);	 Catch:{ Throwable -> 0x004d }
        r4[r5] = r6;	 Catch:{ Throwable -> 0x004d }
        com.douban.book.reader.util.Logger.dc(r2, r3, r4);	 Catch:{ Throwable -> 0x004d }
    L_0x004c:
        throw r0;
    L_0x004d:
        r2 = move-exception;
        goto L_0x004c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.content.pack.Package.doGetCipher():javax.crypto.Cipher");
    }

    private void decipherEntry(String entryPath) {
        Exception e;
        Throwable th;
        if (AppInfo.isDebug()) {
            Logger.v(TAG, "deciphering %s to %s ...", entryPath, String.format("%s_%s_decipher", new Object[]{getFile(), entryPath}));
            InputStream in = null;
            OutputStream out = null;
            try {
                open();
                InputStream in2 = new BufferedInputStream(new CipherInputStream(this.mZipFile.getInputStream(this.mZipFile.getEntry(entryPath)), getCipher()));
                try {
                    OutputStream out2 = new BufferedOutputStream(new FileOutputStream(new File(decipherPath)));
                    try {
                        IOUtils.copyStream(in2, out2);
                        IOUtils.closeSilently(in2);
                        IOUtils.closeSilently(out2);
                        out = out2;
                        in = in2;
                        return;
                    } catch (Exception e2) {
                        e = e2;
                        out = out2;
                        in = in2;
                        try {
                            Logger.e(TAG, e);
                            IOUtils.closeSilently(in);
                            IOUtils.closeSilently(out);
                            return;
                        } catch (Throwable th2) {
                            th = th2;
                            IOUtils.closeSilently(in);
                            IOUtils.closeSilently(out);
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        out = out2;
                        in = in2;
                        IOUtils.closeSilently(in);
                        IOUtils.closeSilently(out);
                        throw th;
                    }
                } catch (Exception e3) {
                    e = e3;
                    in = in2;
                    Logger.e(TAG, e);
                    IOUtils.closeSilently(in);
                    IOUtils.closeSilently(out);
                    return;
                } catch (Throwable th4) {
                    th = th4;
                    in = in2;
                    IOUtils.closeSilently(in);
                    IOUtils.closeSilently(out);
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                Logger.e(TAG, e);
                IOUtils.closeSilently(in);
                IOUtils.closeSilently(out);
                return;
            }
        }
        Logger.e(TAG, new SecurityException("WARNING!! decipherFile should never be called in release mode."));
    }
}
