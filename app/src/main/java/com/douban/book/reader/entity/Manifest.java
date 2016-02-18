package com.douban.book.reader.entity;

import android.util.SparseArray;
import com.douban.book.reader.R;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.ManifestUpdatedEvent;
import com.douban.book.reader.exception.ManifestException;
import com.douban.book.reader.location.TocItem;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.IOUtils;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.google.gson.annotations.SerializedName;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class Manifest implements Identifiable {
    private static SparseArray<Manifest> hInstances;
    public Composite composite;
    public String cover;
    public List<String> feature;
    public int galleryMode;
    public String galleryTheme;
    public int id;
    public int identities;
    public boolean isPartial;
    public List<PackMeta> packages;
    public String provider;
    public String title;
    public List<TocItem> toc;
    public String translator;

    public static class Composite {
        public String etag;
        public String url;
    }

    public static class GalleryColorTheme {
        public static final String DAY = "day";
        public static final String NIGHT = "night";
    }

    public static class GalleryMode {
        public static final int CENTER = 1;
        public static final int TOP = 2;
    }

    public static class PackMeta {
        public String etag;
        public int id;
        public String iv;
        public String key;
        public int packVersion;
        public PackagePayload payload;
        public int requireVersion;
        public String url;
    }

    public static class PackagePayload {
        @SerializedName("abstract")
        public String abstractText;
        public String author;
        public int price;
        public Date publishTime;
        public String title;
    }

    static {
        hInstances = new SparseArray();
    }

    public Object getId() {
        return Integer.valueOf(this.id);
    }

    public static Manifest get(int worksId) throws ManifestException {
        Manifest manifest = (Manifest) hInstances.get(worksId);
        if (manifest == null) {
            try {
                manifest = loadFromDisc(worksId);
            } catch (Throwable e) {
                throw new ManifestException(e);
            }
        }
        return manifest;
    }

    public static Manifest load(int worksId) throws ManifestException {
        try {
            return get(worksId);
        } catch (ManifestException e) {
            return loadFromNetwork(worksId);
        }
    }

    public static Manifest loadFromNetwork(int worksId) throws ManifestException {
        try {
            Manifest manifest = WorksManager.getInstance().getManifest(worksId);
            addToCache(manifest);
            manifest.saveToDisc();
            EventBusUtils.post(new ManifestUpdatedEvent(worksId));
            return manifest;
        } catch (Throwable e) {
            throw new ManifestException(e);
        }
    }

    public URL getCompositeUrl() throws MalformedURLException {
        if (this.composite == null || this.composite.url == null) {
            return null;
        }
        return new URL(this.composite.url);
    }

    public int getPackageIndex(int packageId) {
        for (int i = 0; i < this.packages.size(); i++) {
            if (((PackMeta) this.packages.get(i)).id == packageId) {
                return i;
            }
        }
        return -1;
    }

    public PackMeta getPackMeta(int packageId) {
        for (PackMeta packMeta : this.packages) {
            if (packMeta.id == packageId) {
                return packMeta;
            }
        }
        return null;
    }

    public String getAcknowledgements() {
        return Res.getString(R.string.works_acknowledgements, this.provider);
    }

    public int getGalleryColorTheme() {
        return StringUtils.equals(this.galleryTheme, GalleryColorTheme.DAY) ? 0 : 1;
    }

    public boolean isBookDataDowngraded() {
        for (PackMeta packMeta : this.packages) {
            if (packMeta.packVersion < packMeta.requireVersion && 3 >= packMeta.requireVersion) {
                return true;
            }
        }
        return false;
    }

    public boolean hasComposite() {
        if (this.composite != null) {
            if (StringUtils.isNotEmpty(this.composite.etag, this.composite.url)) {
                return true;
            }
        }
        return false;
    }

    private static void clearFromCache(int worksId) {
        hInstances.delete(worksId);
    }

    private static void addToCache(Manifest manifest) {
        if (manifest != null) {
            hInstances.append(manifest.id, manifest);
        }
    }

    private void saveToDisc() throws IOException {
        Throwable th;
        File outputFile = FilePath.manifest(this.id);
        FileUtils.createParentDirIfNeeded(outputFile);
        BufferedOutputStream out = null;
        try {
            BufferedOutputStream out2 = new BufferedOutputStream(new FileOutputStream(outputFile));
            try {
                out2.write(JsonUtils.toJson(this).getBytes(HttpRequest.CHARSET_UTF8));
                addToCache(this);
                IOUtils.closeSilently(out2);
            } catch (Throwable th2) {
                th = th2;
                out = out2;
                IOUtils.closeSilently(out);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            IOUtils.closeSilently(out);
            throw th;
        }
    }

    private static Manifest loadFromDisc(int worksId) throws ManifestException {
        try {
            Manifest manifest = (Manifest) JsonUtils.fromJsonObj(IOUtils.fileToJSONObject(FilePath.manifest(worksId)), Manifest.class);
            addToCache(manifest);
            return manifest;
        } catch (Throwable e) {
            throw new ManifestException(e);
        }
    }
}
