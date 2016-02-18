package com.douban.book.reader.helper;

import android.net.Uri;
import com.alipay.sdk.util.h;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.DownloadProgressChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.network.ConnectionUtils;
import com.douban.book.reader.network.exception.CdnConnException;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.IOUtils.ProgressListener;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.NetworkUtils;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadHelper {
    public static final int DOWNLOAD_PROGRESS_UNKNOWN = -1;
    private File mFile;
    private Uri mUri;

    public DownloadHelper(Uri uri, File file) {
        this.mUri = null;
        this.mFile = null;
        this.mUri = uri;
        this.mFile = file;
    }

    public int getDownloadProgress() {
        long totalSize = getTotalSize();
        long downloadedSize = getCurrentSize();
        if (totalSize > 0) {
            return Math.min(100, Math.round((float) ((100 * downloadedSize) / totalSize)));
        }
        return DOWNLOAD_PROGRESS_UNKNOWN;
    }

    public long getCurrentSize() {
        return this.mFile.length();
    }

    public long getRemainedSize() {
        if (getTotalSize() < 0) {
            return -1;
        }
        return getTotalSize() - getCurrentSize();
    }

    public long getTotalSize() {
        return Pref.ofObj(this.mUri).getLong(Key.PACKAGE_TOTAL_SIZE, -1);
    }

    private void setTotalSize(long size) {
        Pref.ofObj(this.mUri).set(Key.PACKAGE_TOTAL_SIZE, Long.valueOf(size));
    }

    public void download(URL url, String etag) throws IOException, InterruptedException {
        doDownload(url, etag, false);
    }

    public void obtainPackageSize(URL url, String etag) throws IOException, InterruptedException {
        doDownload(url, etag, true);
    }

    private void doDownload(URL url, String etag, boolean checkSizeOnly) throws IOException, InterruptedException {
        HttpURLConnection conn = null;
        boolean skipped = false;
        try {
            long bytesDone = this.mFile.length();
            if (bytesDone > 0) {
                if (!StringUtils.equals((CharSequence) etag, Pref.ofObj(this.mUri).getString(Key.PACKAGE_ETAG))) {
                    FileUtils.deleteDir(this.mFile);
                    bytesDone = 0;
                } else if (bytesDone == getTotalSize()) {
                    skipped = true;
                }
            }
            String str = Tag.PACKAGE;
            String str2 = "%s[%s] %s";
            Object[] objArr = new Object[3];
            objArr[0] = skipped ? "(SKIPPED) " : Table.STRING_DEFAULT_VALUE;
            objArr[1] = checkSizeOnly ? "Fetching" : "Downloading";
            objArr[2] = url;
            Logger.dc(str, str2, objArr);
            if (!skipped) {
                conn = getPackageConnectionWithFallback(url, bytesDone);
                if (conn.getResponseCode() == 416) {
                    Logger.d(Tag.PACKAGE, String.format("416 received for %s. download from beginning...", new Object[]{url}), new Object[0]);
                    bytesDone = 0;
                    conn.disconnect();
                    conn = getPackageConnectionWithFallback(url, 0);
                }
                String range = conn.getHeaderField("Content-Range");
                long bytesTotal = 0;
                if (StringUtils.isNotEmpty(range)) {
                    Logger.dc(Tag.PACKAGE, String.format("url=%s range=%s", new Object[]{conn.getURL(), range}), new Object[0]);
                    bytesTotal = Long.valueOf(range.substring(range.lastIndexOf(47) + 1)).longValue();
                }
                if (bytesTotal == 0) {
                    bytesDone = 0;
                    bytesTotal = (long) conn.getContentLength();
                }
                boolean append = false;
                if (bytesDone > 0) {
                    append = true;
                }
                setTotalSize(bytesTotal);
                if (!checkSizeOnly) {
                    Pref.ofObj(this.mUri).set(Key.PACKAGE_ETAG, etag);
                    IOUtils.writeStreamToFileAndClose(this.mFile, conn.getInputStream(), append, bytesTotal / 100, bytesDone, bytesTotal, new ProgressListener() {
                        public void onNewProgress(long bytesDone, long totalBytes) {
                            EventBusUtils.post(new DownloadProgressChangedEvent(DownloadHelper.this.mUri));
                        }
                    });
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } else if (conn != null) {
                conn.disconnect();
            }
        } catch (Exception e) {
            Logger.e(Tag.PACKAGE, e);
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private HttpURLConnection getPackageConnectionWithFallback(URL url, long start) throws IOException, InterruptedException {
        HttpURLConnection packageConnection;
        try {
            packageConnection = getPackageConnection(url, null, start);
        } catch (CdnConnException e) {
            Logger.dc(Tag.NETWORK, "Failed to connect CDN (url=%s). trying to obtain IP using HttpDNS...", url);
            String domain = url.getAuthority();
            Iterable<String> ips = NetworkUtils.getIpForDomain(domain);
            if (ips == null || ips.isEmpty()) {
                Logger.ec(Tag.NETWORK, e, "Failed to obtain IP for domain %s", domain);
                Answers.getInstance().logCustom((CustomEvent) new CustomEvent("CdnConnException").putCustomAttribute("retryUsingHttpDNS", "failedToGetIp"));
                throw e;
            }
            Logger.dc(Tag.NETWORK, "Obtained IP for domain %s: %s", domain, StringUtils.join((CharSequence) "; ", ips));
            for (String ip : ips) {
                try {
                    Logger.dc(Tag.NETWORK, "Retrying with %s", new URL(url.toString().replaceFirst(domain, ip)));
                    packageConnection = getPackageConnection(ipUrl, domain, start);
                    Answers.getInstance().logCustom((CustomEvent) new CustomEvent("CdnConnException").putCustomAttribute("retryUsingHttpDNS", "succeed"));
                } catch (IOException e1) {
                    ExceptionUtils.addSuppressed(e, e1);
                    Logger.dc(Tag.NETWORK, "Failed while retrying with %s: %s", ip, e1);
                }
            }
            Logger.ec(Tag.NETWORK, e, "Failed while downloading %s. (Even after retry)", url);
            Answers.getInstance().logCustom((CustomEvent) new CustomEvent("CdnConnException").putCustomAttribute("retryUsingHttpDNS", h.b));
            throw e;
        }
        return packageConnection;
    }

    private HttpURLConnection getPackageConnection(URL url, String domain, long start) throws IOException, InterruptedException {
        int retries = 0;
        while (retries < 6) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException(String.format("Download for %s cancelled.", new Object[]{url}));
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(ConnectionUtils.CONNECTION_TIMEOUT);
            conn.setRequestProperty(HttpRequest.HEADER_USER_AGENT, ConnectionUtils.getUserAgent());
            if (StringUtils.isNotEmpty(domain)) {
                conn.setRequestProperty("Host", domain);
            }
            if (start > 0) {
                Logger.dc(Tag.PACKAGE, String.format("Range: bytes=%d-", new Object[]{Long.valueOf(start)}), new Object[0]);
                conn.setRequestProperty("Range", String.format("bytes=%d-", new Object[]{Long.valueOf(start)}));
            }
            conn.setUseCaches(true);
            conn.connect();
            int httpStatusCode = conn.getResponseCode();
            if (httpStatusCode == 404) {
                Logger.dc(Tag.PACKAGE, String.format("Package for %s not found. waiting for retry (%d) ...", new Object[]{url, Integer.valueOf(retries)}), new Object[0]);
                Thread.sleep(10000);
                retries++;
                conn.disconnect();
            } else if (httpStatusCode < AppUri.READER || httpStatusCode == 416) {
                return conn;
            } else {
                CdnConnException e = new CdnConnException(conn);
                conn.disconnect();
                throw e;
            }
        }
        throw new IOException(String.format("Failed to get package data. Exceed max retries (%s). url=%s", new Object[]{Integer.valueOf(6), url}));
    }
}
