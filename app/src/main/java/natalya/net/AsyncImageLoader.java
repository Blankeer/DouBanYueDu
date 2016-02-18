package natalya.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import natalya.app.ExternalStorageUtils;
import natalya.codec.DigestUtils;
import natalya.graphics.BitmapUtils;
import natalya.io.FileUtils;

public class AsyncImageLoader {
    private static final int CORE_POOL_SIZE = 2;
    private static final int CORE_POOL_SIZE2 = 1;
    private static final int DELAY_BEFORE_PURGE = 10000;
    private static final int HARD_CACHE_CAPACITY = 64;
    private static final int IMAGEVIEW_TAG_KEY = 2131296255;
    private static final int KEEP_ALIVE = 1;
    private static boolean LOCAL_CACHE = false;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final int MAXIMUM_POOL_SIZE2 = 2;
    private static int MAX_WIDTH = 0;
    private static boolean SCALE_BITMAP = false;
    private static final String TAG = "AID";
    private static Handler handler;
    private static final Handler purgeHandler;
    private static final Runnable purger;
    private static final ThreadPoolExecutor sExecutor;
    private static final ThreadPoolExecutor sExecutor2;
    private static final ThreadPoolExecutor sExecutor3;
    private static final HashMap<String, Bitmap> sHardBitmapCache;
    private static final ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache;
    private static final ThreadFactory sThreadFactory;
    private static final BlockingQueue<Runnable> sWorkQueue;
    private static final BlockingQueue<Runnable> sWorkQueue2;
    private static final BlockingQueue<Runnable> sWorkQueue3;

    /* renamed from: natalya.net.AsyncImageLoader.1 */
    static class AnonymousClass1 extends LinkedHashMap<String, Bitmap> {
        AnonymousClass1(int x0, float x1, boolean x2) {
            super(x0, x1, x2);
        }

        public boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
            if (size() <= AsyncImageLoader.HARD_CACHE_CAPACITY) {
                return false;
            }
            AsyncImageLoader.sSoftBitmapCache.put(eldest.getKey(), new SoftReference(eldest.getValue()));
            return true;
        }
    }

    /* renamed from: natalya.net.AsyncImageLoader.5 */
    static class AnonymousClass5 implements Runnable {
        final /* synthetic */ String val$fileDir;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ Bitmap val$image;

        AnonymousClass5(Bitmap bitmap, String str, String str2) {
            this.val$image = bitmap;
            this.val$fileDir = str;
            this.val$fileName = str2;
        }

        public void run() {
            try {
                FileUtils.writeStreamToFile(new ByteArrayInputStream(BitmapUtils.generateBitstream(this.val$image, CompressFormat.JPEG, 85)), this.val$fileDir, this.val$fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: natalya.net.AsyncImageLoader.6 */
    static class AnonymousClass6 implements Runnable {
        final /* synthetic */ ImageAttacher val$attacher;
        final /* synthetic */ String val$fileDir;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ String val$url;

        AnonymousClass6(String str, String str2, ImageAttacher imageAttacher, String str3) {
            this.val$fileDir = str;
            this.val$fileName = str2;
            this.val$attacher = imageAttacher;
            this.val$url = str3;
        }

        public void run() {
            Bitmap bitmap = AsyncImageLoader.getLocalBitmap(this.val$fileDir, this.val$fileName);
            if (bitmap != null) {
                if (AsyncImageLoader.LOCAL_CACHE) {
                    AsyncImageLoader.addBitmapToCache(this.val$fileName, bitmap);
                }
                bitmap = this.val$attacher.prepare(bitmap);
                Message msg = AsyncImageLoader.handler.obtainMessage();
                Object obj = new Object[AsyncImageLoader.MAXIMUM_POOL_SIZE2];
                obj[0] = bitmap;
                obj[AsyncImageLoader.KEEP_ALIVE] = this.val$attacher;
                msg.obj = obj;
                AsyncImageLoader.handler.sendMessage(msg);
                return;
            }
            AsyncImageLoader.sExecutor.execute(new Runnable() {
                public void run() {
                    Bitmap bitmap = AsyncImageLoader.getNetBitmap(AnonymousClass6.this.val$url, AnonymousClass6.this.val$fileDir, AnonymousClass6.this.val$fileName);
                    if (AsyncImageLoader.LOCAL_CACHE) {
                        AsyncImageLoader.addBitmapToCache(AnonymousClass6.this.val$fileName, bitmap);
                    }
                    bitmap = AnonymousClass6.this.val$attacher.prepare(bitmap);
                    Message msg = AsyncImageLoader.handler.obtainMessage();
                    Object obj = new Object[AsyncImageLoader.MAXIMUM_POOL_SIZE2];
                    obj[0] = bitmap;
                    obj[AsyncImageLoader.KEEP_ALIVE] = AnonymousClass6.this.val$attacher;
                    msg.obj = obj;
                    AsyncImageLoader.handler.sendMessage(msg);
                }
            });
        }
    }

    /* renamed from: natalya.net.AsyncImageLoader.7 */
    static class AnonymousClass7 implements Runnable {
        final /* synthetic */ ImageAttacher val$attacher;
        final /* synthetic */ String val$fileDir;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ String val$url;

        AnonymousClass7(String str, String str2, String str3, ImageAttacher imageAttacher) {
            this.val$url = str;
            this.val$fileDir = str2;
            this.val$fileName = str3;
            this.val$attacher = imageAttacher;
        }

        public void run() {
            Bitmap bitmap = AsyncImageLoader.getNetBitmap(this.val$url, this.val$fileDir, this.val$fileName);
            if (AsyncImageLoader.LOCAL_CACHE) {
                AsyncImageLoader.addBitmapToCache(this.val$fileName, bitmap);
            }
            bitmap = this.val$attacher.prepare(bitmap);
            Message msg = AsyncImageLoader.handler.obtainMessage();
            Object obj = new Object[AsyncImageLoader.MAXIMUM_POOL_SIZE2];
            obj[0] = bitmap;
            obj[AsyncImageLoader.KEEP_ALIVE] = this.val$attacher;
            msg.obj = obj;
            AsyncImageLoader.handler.sendMessage(msg);
        }
    }

    public interface ImageAttacher {
        void attach(Bitmap bitmap);

        Bitmap prepare(Bitmap bitmap);
    }

    public static class DefaultImageAttacher implements ImageAttacher {
        private String fileName;
        private ImageView v;

        public DefaultImageAttacher(ImageView v, String fileName) {
            this.v = v;
            this.fileName = fileName;
        }

        public Bitmap prepare(Bitmap bitmap) {
            return bitmap;
        }

        public void attach(Bitmap bitmap) {
            String fileName0 = (String) this.v.getTag(AsyncImageLoader.IMAGEVIEW_TAG_KEY);
            if (this.v == null) {
                return;
            }
            if (fileName0 == null || fileName0.equals(this.fileName)) {
                this.v.setImageBitmap(bitmap);
            }
        }
    }

    public static class DummyImageAttacher implements ImageAttacher {
        public Bitmap prepare(Bitmap bitmap) {
            return bitmap;
        }

        public void attach(Bitmap bitmap) {
        }
    }

    public static class FadeinImageAttacher implements ImageAttacher {
        private String fileName;
        private ImageView v;

        public FadeinImageAttacher(ImageView v, String fileName) {
            this.v = v;
            this.fileName = fileName;
        }

        public Bitmap prepare(Bitmap bitmap) {
            return bitmap;
        }

        public void attach(Bitmap bitmap) {
            String fileName0 = (String) this.v.getTag(AsyncImageLoader.IMAGEVIEW_TAG_KEY);
            if (this.v == null) {
                return;
            }
            if (fileName0 == null || fileName0.equals(this.fileName)) {
                this.v.setImageBitmap(bitmap);
                AlphaAnimation fadein = new AlphaAnimation(0.1f, 1.0f);
                fadein.setDuration(500);
                fadein.setInterpolator(new AccelerateDecelerateInterpolator());
                fadein.setRepeatCount(0);
                this.v.startAnimation(fadein);
            }
        }
    }

    public static void setMaxWidth(int width) {
        MAX_WIDTH = width;
    }

    public static void enableLocalCache(boolean enable) {
        LOCAL_CACHE = enable;
    }

    public static void enableBitmapScale(boolean enable) {
        SCALE_BITMAP = enable;
    }

    public static void loadImage(Context context, String url, ImageView iv) {
        loadImage(context, url, iv, -1, false);
    }

    public static void loadImage(Context context, String url, ImageView iv, int defaultResource, boolean fadeinAnim) {
        loadImage(0, context, url, iv, -1, fadeinAnim);
    }

    public static void loadImage(int imageViewResourceId, Context context, String url, ImageView iv, int defaultResource, boolean fadeinAnim) {
        if (!TextUtils.isEmpty(url)) {
            ImageAttacher attacher;
            String fileName = getFileName(url);
            if (fadeinAnim) {
                attacher = new FadeinImageAttacher(iv, fileName);
            } else {
                attacher = new DefaultImageAttacher(iv, fileName);
            }
            iv.setTag(IMAGEVIEW_TAG_KEY, fileName);
            Bitmap bitmap = getBitmapFromCache(fileName);
            if (bitmap != null) {
                iv.setImageBitmap(bitmap);
                return;
            }
            if (defaultResource > 0) {
                iv.setImageResource(defaultResource);
            } else {
                iv.setImageDrawable(null);
            }
            async(context, url, fileName, attacher);
        } else if (defaultResource > 0) {
            iv.setImageResource(defaultResource);
        }
    }

    public static void loadImage(Context context, String url, ImageAttacher attacher) {
        if (!TextUtils.isEmpty(url)) {
            String fileName = getFileName(url);
            Bitmap bitmap = getBitmapFromCache(fileName);
            if (bitmap != null) {
                bitmap = attacher.prepare(bitmap);
                Message msg = handler.obtainMessage();
                Object obj = new Object[MAXIMUM_POOL_SIZE2];
                obj[0] = bitmap;
                obj[KEEP_ALIVE] = attacher;
                msg.obj = obj;
                handler.sendMessage(msg);
                return;
            }
            async(context, url, fileName, attacher);
        }
    }

    static {
        MAX_WIDTH = 640;
        LOCAL_CACHE = true;
        SCALE_BITMAP = false;
        sWorkQueue = new LinkedBlockingQueue(30);
        sWorkQueue2 = new LinkedBlockingQueue(10);
        sWorkQueue3 = new LinkedBlockingQueue(10);
        sHardBitmapCache = new AnonymousClass1(32, 0.75f, true);
        sSoftBitmapCache = new ConcurrentHashMap(32);
        purgeHandler = new Handler();
        purger = new Runnable() {
            public void run() {
                AsyncImageLoader.clearCache();
            }
        };
        sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount;

            {
                this.mCount = new AtomicInteger(AsyncImageLoader.KEEP_ALIVE);
            }

            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncImageLoader #" + this.mCount.getAndIncrement());
            }
        };
        sExecutor = new ThreadPoolExecutor(MAXIMUM_POOL_SIZE2, MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, sWorkQueue, sThreadFactory, new DiscardOldestPolicy());
        sExecutor2 = new ThreadPoolExecutor(MAXIMUM_POOL_SIZE2, MAXIMUM_POOL_SIZE2, 1, TimeUnit.SECONDS, sWorkQueue2, sThreadFactory, new DiscardOldestPolicy());
        sExecutor3 = new ThreadPoolExecutor(MAXIMUM_POOL_SIZE2, MAXIMUM_POOL_SIZE2, 1, TimeUnit.SECONDS, sWorkQueue3, sThreadFactory, new DiscardOldestPolicy());
        handler = new Handler() {
            public void handleMessage(Message message) {
                Object[] data = (Object[]) message.obj;
                data[AsyncImageLoader.KEEP_ALIVE].attach(data[0]);
            }
        };
    }

    private static void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, 10000);
    }

    public static void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    public static void clearBitmap(String url) {
        String fileName = getFileName(url);
        if (sHardBitmapCache.containsKey(fileName)) {
            Bitmap bitmap = (Bitmap) sHardBitmapCache.get(fileName);
            if (bitmap != null) {
                try {
                    bitmap.recycle();
                    sHardBitmapCache.remove(fileName);
                    sSoftBitmapCache.remove(fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (sSoftBitmapCache.containsKey(fileName)) {
            SoftReference<Bitmap> bitmapReference = (SoftReference) sSoftBitmapCache.get(fileName);
            if (bitmapReference != null) {
                try {
                    ((Bitmap) bitmapReference.get()).recycle();
                    sSoftBitmapCache.remove(fileName);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public static void clearFiles(Context context) {
        try {
            File directory = new File(getFileDir(context));
            if (directory.exists()) {
                File[] arr$ = directory.listFiles();
                int len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$ += KEEP_ALIVE) {
                    arr$[i$].delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileDir(Context context) {
        return ExternalStorageUtils.getExternalFilesDir(context.getPackageName(), "Pictures").getAbsolutePath();
    }

    public static String getFileName(String url) {
        return DigestUtils.md5Hex(url) + ".jpg";
    }

    public static boolean isImageDownloaded(Context context, String url) {
        return FileUtils.isFileExists(getFileDir(context), getFileName(url));
    }

    public static Uri getFileUri(Context context, String url) {
        String fileDir = getFileDir(context);
        String fileName = getFileName(url);
        if (FileUtils.isFileExists(fileDir, fileName)) {
            try {
                return Uri.fromFile(new File(fileDir + File.separator + fileName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Bitmap createBitmap(String fileDir, String fileName) {
        Bitmap pic = null;
        try {
            InputStream stream = FileUtils.openInputStream(fileDir, fileName);
            pic = BitmapFactory.decodeStream(stream);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
        }
        return pic;
    }

    private static Bitmap createScaleBitmap(String fileDir, String fileName) {
        Bitmap pic = null;
        try {
            InputStream stream = FileUtils.openInputStream(fileDir, fileName);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            pic = BitmapFactory.decodeStream(stream, null, options);
            int be = options.outWidth / MAX_WIDTH;
            if (be < 0) {
                be = KEEP_ALIVE;
            }
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;
            if (options.outWidth > MAX_WIDTH) {
                options.inScaled = true;
                options.inDensity = options.outWidth;
                options.inTargetDensity = MAX_WIDTH;
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
            stream = FileUtils.openInputStream(fileDir, fileName);
            pic = BitmapFactory.decodeStream(stream, null, options);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e2) {
                }
            }
        } catch (OutOfMemoryError e3) {
            e3.printStackTrace();
        }
        return pic;
    }

    private static void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    private static Bitmap getBitmapFromCache(String url) {
        synchronized (sHardBitmapCache) {
            Bitmap bitmap = (Bitmap) sHardBitmapCache.get(url);
            if (bitmap != null) {
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
            SoftReference<Bitmap> bitmapReference = (SoftReference) sSoftBitmapCache.get(url);
            if (bitmapReference != null) {
                bitmap = (Bitmap) bitmapReference.get();
                if (bitmap != null) {
                    return bitmap;
                }
                sSoftBitmapCache.remove(url);
            }
            return null;
        }
    }

    private static Bitmap getLocalBitmap(String fileDir, String fileName) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(fileName);
        if (bitmap != null) {
            return bitmap;
        }
        if (SCALE_BITMAP) {
            bitmap = createScaleBitmap(fileDir, fileName);
        } else {
            bitmap = createBitmap(fileDir, fileName);
        }
        if (LOCAL_CACHE) {
            addBitmapToCache(fileName, bitmap);
        }
        return bitmap;
    }

    private static Bitmap getNetBitmap(String url, String fileDir, String fileName) {
        Bitmap bitmap = null;
        InputStream stream = null;
        try {
            stream = Crawler.crawlUrl(url);
            if (stream != null) {
                if (!SCALE_BITMAP) {
                    try {
                        bitmap = BitmapFactory.decodeStream(stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        sExecutor3.execute(new AnonymousClass5(bitmap, fileDir, fileName));
                    }
                } else if (FileUtils.writeStreamToFile(stream, fileDir, fileName)) {
                    bitmap = createScaleBitmap(fileDir, fileName);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e2) {
                }
            }
        } catch (IOException e3) {
            e3.printStackTrace();
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e4) {
                }
            }
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e5) {
                }
            }
        }
        return bitmap;
    }

    private static void async(Context context, String url, String fileName, ImageAttacher attacher) {
        String fileDir = getFileDir(context);
        if (FileUtils.isFileExists(fileDir, fileName)) {
            sExecutor2.execute(new AnonymousClass6(fileDir, fileName, attacher, url));
        } else {
            sExecutor.execute(new AnonymousClass7(url, fileDir, fileName, attacher));
        }
    }
}
