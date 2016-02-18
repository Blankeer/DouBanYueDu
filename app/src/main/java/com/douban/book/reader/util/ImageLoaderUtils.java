package com.douban.book.reader.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.widget.ImageView;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.app.App;
import com.igexin.download.Downloads;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.io.IOException;

public class ImageLoaderUtils {
    private static final String TAG;
    private static DisplayImageOptions sDefaultDisplayOptions;
    private static ImageLoader sInstance;

    static {
        TAG = ImageLoaderUtils.class.getSimpleName();
        sInstance = ImageLoader.getInstance();
        LruDiskCache diskCache = null;
        try {
            diskCache = new LruDiskCache(FilePath.imageCache(), new Md5FileNameGenerator(), 104857600);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        sDefaultDisplayOptions = new Builder().cacheInMemory(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(Downloads.STATUS_BAD_REQUEST, true, true, false)).build();
        sInstance.init(new ImageLoaderConfiguration.Builder(App.get()).threadPoolSize(6).memoryCacheSize(Utils.getMemorySizeByPercentage(20)).diskCache(diskCache).defaultDisplayImageOptions(sDefaultDisplayOptions).imageDownloader(BookImageDownloader.getInstance()).build());
    }

    public static void displayImage(String uri, ImageView imageView) {
        displayImage(uri, imageView, -1);
    }

    public static void displayImage(String uri, ImageView imageView, int defaultResId) {
        doDisplayImage(uri, imageView, defaultResId, false);
    }

    public static void displayImageSkippingDiscCache(String uri, ImageView imageView) {
        doDisplayImage(uri, imageView, -1, true);
    }

    private static void doDisplayImage(String uri, ImageView imageView, int defaultResId, boolean skipDiskCache) {
        DisplayImageOptions options = sDefaultDisplayOptions;
        if (defaultResId > 0 || skipDiskCache) {
            Builder builder = new Builder().cloneFrom(options);
            if (defaultResId > 0) {
                builder.showImageOnLoading(defaultResId).showImageOnFail(defaultResId);
            }
            if (skipDiskCache) {
                builder.cacheOnDisk(false);
            }
            options = builder.build();
        }
        sInstance.displayImage(uri, imageView, options, new SimpleImageLoadingListener() {
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    return;
                }
                if ((loadedImage.getWidth() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || loadedImage.getHeight() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT) && !ViewUtils.isSoftLayerType(view)) {
                    ViewUtils.setSoftLayerType(view);
                    view.postInvalidate();
                }
            }
        });
    }

    public static void loadImage(String uri, ImageSize imageSize, ImageLoadingListener listener) {
        sInstance.loadImage(uri, imageSize, new Builder().cloneFrom(sDefaultDisplayOptions).cacheOnDisk(true).build(), listener);
    }

    public static Bitmap loadImageSync(String uri, ImageSize imageSize) {
        return sInstance.loadImageSync(uri, imageSize, new Builder().cloneFrom(sDefaultDisplayOptions).cacheOnDisk(true).build());
    }

    public static void loadImageSkippingCache(String uri, ImageLoadingListener listener) {
        sInstance.loadImage(uri, new Builder().cloneFrom(sDefaultDisplayOptions).cacheOnDisk(false).build(), listener);
    }

    public static void loadImageSkippingCache(Uri uri, ImageLoadingListener listener) {
        loadImageSkippingCache(uri.toString(), listener);
    }

    public static Bitmap loadImageInCache(String uri) {
        MemoryCache memoryCache = getMemoryCache();
        for (String key : memoryCache.keys()) {
            if (key.startsWith(uri)) {
                return memoryCache.get(key);
            }
        }
        return null;
    }

    public static Bitmap loadImageSync(String uri) {
        return sInstance.loadImageSync(uri);
    }

    public static void pause() {
        sInstance.pause();
    }

    public static void resume() {
        sInstance.resume();
    }

    private static MemoryCache getMemoryCache() {
        return sInstance.getMemoryCache();
    }

    public static void clearMemoryCache() {
        try {
            sInstance.getMemoryCache().clear();
            Logger.d(TAG, "clear memory cache for image loader", new Object[0]);
        } catch (Throwable th) {
        }
    }
}
