package natalya.net;

import android.app.Application;
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
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
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

public class AsyncImageLoader2 {
    public static final int BIG_SINGLE = 1;
    private static final int IMAGEVIEW_TAG_KEY = 2131296255;
    private static final int MSG_CLEAR_BITMAP = 0;
    public static final int SMALL_MULTI = 0;
    private static final String TAG = "AID";
    private final int DELAY_BEFORE_PURGE;
    private int HARD_CACHE_CAPACITY;
    private final int KEEP_ALIVE;
    private boolean LOCAL_CACHE;
    private int MAX_WIDTH;
    private int MODE;
    private boolean SCALE_BITMAP;
    private Handler clearHandler;
    private Application context;
    private CustomBitmapLoader customBitmapLoader;
    private Handler handler;
    private Handler purgeHandler;
    private Runnable purger;
    private ThreadPoolExecutor sExecutor;
    private ThreadPoolExecutor sExecutor2;
    private ThreadPoolExecutor sExecutor3;
    private HashMap<String, Bitmap> sHardBitmapCache;
    private ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache;
    private final ThreadFactory sThreadFactory;
    private BlockingQueue<Runnable> sWorkQueue;
    private BlockingQueue<Runnable> sWorkQueue2;
    private BlockingQueue<Runnable> sWorkQueue3;

    /* renamed from: natalya.net.AsyncImageLoader2.1 */
    class AnonymousClass1 extends LinkedHashMap<String, Bitmap> {
        AnonymousClass1(int x0, float x1, boolean x2) {
            super(x0, x1, x2);
        }

        public boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
            if (size() <= AsyncImageLoader2.this.HARD_CACHE_CAPACITY) {
                return false;
            }
            AsyncImageLoader2.this.sSoftBitmapCache.put(eldest.getKey(), new SoftReference(eldest.getValue()));
            return true;
        }
    }

    /* renamed from: natalya.net.AsyncImageLoader2.6 */
    class AnonymousClass6 implements Runnable {
        final /* synthetic */ String val$fileDir;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ Bitmap val$image;

        AnonymousClass6(Bitmap bitmap, String str, String str2) {
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

    /* renamed from: natalya.net.AsyncImageLoader2.7 */
    class AnonymousClass7 implements Runnable {
        final /* synthetic */ String val$fileDir;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ Bitmap val$image;

        AnonymousClass7(Bitmap bitmap, String str, String str2) {
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

    /* renamed from: natalya.net.AsyncImageLoader2.8 */
    class AnonymousClass8 implements Runnable {
        final /* synthetic */ ImageAttacher val$attacher;
        final /* synthetic */ Object val$bitmapKey;
        final /* synthetic */ String val$fileDir;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ String val$url;

        AnonymousClass8(String str, String str2, Object obj, ImageAttacher imageAttacher, String str3) {
            this.val$fileDir = str;
            this.val$fileName = str2;
            this.val$bitmapKey = obj;
            this.val$attacher = imageAttacher;
            this.val$url = str3;
        }

        public void run() {
            Bitmap bitmap = null;
            if (FileUtils.isFileExists(this.val$fileDir, this.val$fileName)) {
                bitmap = AsyncImageLoader2.this.getLocalBitmap(this.val$fileDir, this.val$fileName);
            }
            if (bitmap == null && this.val$bitmapKey != null) {
                bitmap = AsyncImageLoader2.this.getCustomBitmap(this.val$bitmapKey, this.val$fileDir, this.val$fileName);
            }
            if (bitmap != null) {
                AsyncImageLoader2.this.addBitmapToCache(this.val$fileName, bitmap);
                bitmap = this.val$attacher.prepare(bitmap);
                Message msg = AsyncImageLoader2.this.handler.obtainMessage();
                msg.obj = new Object[]{bitmap, this.val$attacher};
                AsyncImageLoader2.this.handler.sendMessage(msg);
                return;
            }
            AsyncImageLoader2.this.sExecutor.execute(new Runnable() {
                public void run() {
                    Bitmap bitmap = AsyncImageLoader2.this.getNetBitmap(AnonymousClass8.this.val$url, AnonymousClass8.this.val$fileDir, AnonymousClass8.this.val$fileName);
                    AsyncImageLoader2.this.addBitmapToCache(AnonymousClass8.this.val$fileName, bitmap);
                    bitmap = AnonymousClass8.this.val$attacher.prepare(bitmap);
                    Message msg = AsyncImageLoader2.this.handler.obtainMessage();
                    msg.obj = new Object[]{bitmap, AnonymousClass8.this.val$attacher};
                    AsyncImageLoader2.this.handler.sendMessage(msg);
                }
            });
        }
    }

    /* renamed from: natalya.net.AsyncImageLoader2.9 */
    class AnonymousClass9 implements Runnable {
        final /* synthetic */ ImageAttacher val$attacher;
        final /* synthetic */ String val$fileDir;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ String val$url;

        AnonymousClass9(String str, String str2, String str3, ImageAttacher imageAttacher) {
            this.val$url = str;
            this.val$fileDir = str2;
            this.val$fileName = str3;
            this.val$attacher = imageAttacher;
        }

        public void run() {
            Bitmap bitmap = AsyncImageLoader2.this.getNetBitmap(this.val$url, this.val$fileDir, this.val$fileName);
            AsyncImageLoader2.this.addBitmapToCache(this.val$fileName, bitmap);
            bitmap = this.val$attacher.prepare(bitmap);
            Message msg = AsyncImageLoader2.this.handler.obtainMessage();
            msg.obj = new Object[]{bitmap, this.val$attacher};
            AsyncImageLoader2.this.handler.sendMessage(msg);
        }
    }

    public interface CustomBitmapLoader {
        Bitmap getBitmap(Object obj);
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
            String fileName0 = (String) this.v.getTag(AsyncImageLoader2.IMAGEVIEW_TAG_KEY);
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
            String fileName0 = (String) this.v.getTag(AsyncImageLoader2.IMAGEVIEW_TAG_KEY);
            if (this.v == null) {
                return;
            }
            if (fileName0 == null || fileName0.equals(this.fileName)) {
                this.v.setImageBitmap(bitmap);
                AlphaAnimation fadein = new AlphaAnimation(0.1f, 1.0f);
                fadein.setDuration(500);
                fadein.setInterpolator(new AccelerateDecelerateInterpolator());
                fadein.setRepeatCount(AsyncImageLoader2.SMALL_MULTI);
                this.v.startAnimation(fadein);
            }
        }
    }

    public AsyncImageLoader2(Application app) {
        this.MODE = SMALL_MULTI;
        this.MAX_WIDTH = 640;
        this.LOCAL_CACHE = true;
        this.SCALE_BITMAP = false;
        this.KEEP_ALIVE = BIG_SINGLE;
        this.HARD_CACHE_CAPACITY = 64;
        this.DELAY_BEFORE_PURGE = AbstractSpiCall.DEFAULT_TIMEOUT;
        this.customBitmapLoader = null;
        this.sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount;

            {
                this.mCount = new AtomicInteger(AsyncImageLoader2.BIG_SINGLE);
            }

            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncImageLoader #" + this.mCount.getAndIncrement());
            }
        };
        this.handler = new Handler() {
            public void handleMessage(Message message) {
                Object[] data = (Object[]) message.obj;
                data[AsyncImageLoader2.BIG_SINGLE].attach(data[AsyncImageLoader2.SMALL_MULTI]);
            }
        };
        this.context = app;
    }

    public void setMode(int mode) {
        this.MODE = mode;
    }

    public void setBitmapScale(boolean enable, int maxWidth) {
        this.SCALE_BITMAP = enable;
        this.MAX_WIDTH = maxWidth;
    }

    public void setLocalCache(boolean enable, int size) {
        this.LOCAL_CACHE = enable;
        this.HARD_CACHE_CAPACITY = size;
    }

    public void setCustomBitmapLoader(CustomBitmapLoader loader) {
        this.customBitmapLoader = loader;
    }

    public void init() {
        if (this.LOCAL_CACHE) {
            this.sHardBitmapCache = new AnonymousClass1(this.HARD_CACHE_CAPACITY / 2, 0.75f, true);
            this.sSoftBitmapCache = new ConcurrentHashMap(this.HARD_CACHE_CAPACITY / 2);
            this.purgeHandler = new Handler();
            this.clearHandler = new Handler() {
                public void handleMessage(Message m) {
                    switch (m.what) {
                        case AsyncImageLoader2.SMALL_MULTI /*0*/:
                            String url = m.obj;
                            if (url != null) {
                                AsyncImageLoader2.this.doClearBitmap(url);
                            }
                        default:
                    }
                }
            };
            this.purger = new Runnable() {
                public void run() {
                    AsyncImageLoader2.this.clearCache(false);
                }
            };
        }
        DiscardOldestPolicy policy = new DiscardOldestPolicy();
        switch (this.MODE) {
            case SMALL_MULTI /*0*/:
                this.sWorkQueue = new LinkedBlockingQueue(32);
                this.sExecutor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.SECONDS, this.sWorkQueue, this.sThreadFactory, policy);
                this.sWorkQueue2 = new LinkedBlockingQueue(8);
                this.sExecutor2 = new ThreadPoolExecutor(BIG_SINGLE, 2, 1, TimeUnit.SECONDS, this.sWorkQueue2, this.sThreadFactory, policy);
                this.sWorkQueue3 = new LinkedBlockingQueue(8);
                this.sExecutor3 = new ThreadPoolExecutor(BIG_SINGLE, 2, 1, TimeUnit.SECONDS, this.sWorkQueue3, this.sThreadFactory, policy);
            case BIG_SINGLE /*1*/:
                this.sWorkQueue = new LinkedBlockingQueue(3);
                this.sExecutor = new ThreadPoolExecutor(BIG_SINGLE, 3, 1, TimeUnit.SECONDS, this.sWorkQueue, this.sThreadFactory, policy);
                this.sWorkQueue2 = new LinkedBlockingQueue(6);
                this.sExecutor2 = new ThreadPoolExecutor(2, 6, 1, TimeUnit.SECONDS, this.sWorkQueue2, this.sThreadFactory, policy);
                this.sWorkQueue3 = new LinkedBlockingQueue(6);
                this.sExecutor3 = new ThreadPoolExecutor(BIG_SINGLE, 3, 1, TimeUnit.SECONDS, this.sWorkQueue3, this.sThreadFactory, policy);
            default:
        }
    }

    public void loadImage(String url, ImageView iv) {
        loadImage(url, iv, -1, false);
    }

    public void loadImage(String url, ImageView iv, int defaultResource, boolean fadeinAnim) {
        loadImage(url, null, iv, defaultResource, fadeinAnim);
    }

    public void loadImage(String url, Object bitmapKey, ImageView iv, int defaultResource, boolean fadeinAnim) {
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
            async(url, bitmapKey, fileName, attacher);
        } else if (defaultResource > 0) {
            iv.setImageResource(defaultResource);
        }
    }

    public void loadImage(String url, ImageAttacher attacher) {
        if (!TextUtils.isEmpty(url)) {
            String fileName = getFileName(url);
            Bitmap bitmap = getBitmapFromCache(fileName);
            if (bitmap != null) {
                bitmap = attacher.prepare(bitmap);
                Message msg = this.handler.obtainMessage();
                msg.obj = new Object[]{bitmap, attacher};
                this.handler.sendMessage(msg);
                return;
            }
            async(url, null, fileName, attacher);
        }
    }

    private void resetPurgeTimer() {
        if (this.purgeHandler != null) {
            this.purgeHandler.removeCallbacks(this.purger);
            this.purgeHandler.postDelayed(this.purger, 10000);
        }
    }

    public void clearCache(boolean force) {
        if (this.LOCAL_CACHE) {
            if (force) {
                Bitmap b;
                Set<String> keys = this.sHardBitmapCache.keySet();
                for (String s : keys) {
                    b = (Bitmap) this.sHardBitmapCache.get(s);
                    if (b != null) {
                        try {
                            b.recycle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Set<String> keys2 = this.sSoftBitmapCache.keySet();
                for (String s2 : keys) {
                    SoftReference<Bitmap> bitmapReference = (SoftReference) this.sSoftBitmapCache.get(s2);
                    if (bitmapReference != null) {
                        b = (Bitmap) bitmapReference.get();
                        if (b != null) {
                            try {
                                b.recycle();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            }
            this.sHardBitmapCache.clear();
            this.sSoftBitmapCache.clear();
        }
    }

    public void clearBitmap(String url) {
        clearBitmap(url, 0);
    }

    public void clearBitmap(String url, long delayMillis) {
        if (!this.LOCAL_CACHE) {
            return;
        }
        if (delayMillis > 0) {
            Message msg = this.clearHandler.obtainMessage(SMALL_MULTI);
            msg.obj = url;
            this.clearHandler.sendMessageDelayed(msg, delayMillis);
            return;
        }
        doClearBitmap(url);
    }

    private void doClearBitmap(String url) {
        if (this.LOCAL_CACHE) {
            String fileName = getFileName(url);
            if (this.sHardBitmapCache.containsKey(fileName)) {
                Bitmap bitmap = (Bitmap) this.sHardBitmapCache.get(fileName);
                if (bitmap != null) {
                    try {
                        bitmap.recycle();
                        this.sHardBitmapCache.remove(fileName);
                        this.sSoftBitmapCache.remove(fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (this.sSoftBitmapCache.containsKey(fileName)) {
                SoftReference<Bitmap> bitmapReference = (SoftReference) this.sSoftBitmapCache.get(fileName);
                if (bitmapReference != null) {
                    try {
                        ((Bitmap) bitmapReference.get()).recycle();
                        this.sSoftBitmapCache.remove(fileName);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    public void clearFiles() {
        try {
            File directory = new File(getFileDir(this.context));
            if (directory.exists()) {
                File[] arr$ = directory.listFiles();
                int len$ = arr$.length;
                for (int i$ = SMALL_MULTI; i$ < len$; i$ += BIG_SINGLE) {
                    arr$[i$].delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileDir(Application app) {
        return ExternalStorageUtils.getExternalFilesDir(app.getPackageName(), "Pictures").getAbsolutePath();
    }

    public static String getFileName(String url) {
        return DigestUtils.md5Hex(url) + ".jpg";
    }

    public boolean isImageDownloaded(String url) {
        return isImageDownloaded(this.context, url);
    }

    public static boolean isImageDownloaded(Application app, String url) {
        return FileUtils.isFileExists(getFileDir(app), getFileName(url));
    }

    public static Uri getFileUri(Application app, String url) {
        String fileDir = getFileDir(app);
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

    private Bitmap createBitmap(String fileDir, String fileName) {
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

    private Bitmap createScaleBitmap(String fileDir, String fileName) {
        Bitmap pic = null;
        try {
            InputStream stream = FileUtils.openInputStream(fileDir, fileName);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            pic = BitmapFactory.decodeStream(stream, null, options);
            int be = options.outWidth / this.MAX_WIDTH;
            if (be < 0) {
                be = BIG_SINGLE;
            }
            options.inSampleSize = be;
            options.inJustDecodeBounds = false;
            if (options.outWidth > this.MAX_WIDTH) {
                options.inScaled = true;
                options.inDensity = options.outWidth;
                options.inTargetDensity = this.MAX_WIDTH;
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

    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (this.LOCAL_CACHE && bitmap != null) {
            synchronized (this.sHardBitmapCache) {
                this.sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    private Bitmap getBitmapFromCache(String url) {
        if (!this.LOCAL_CACHE) {
            return null;
        }
        synchronized (this.sHardBitmapCache) {
            Bitmap bitmap = (Bitmap) this.sHardBitmapCache.get(url);
            if (bitmap != null) {
                this.sHardBitmapCache.remove(url);
                this.sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
            SoftReference<Bitmap> bitmapReference = (SoftReference) this.sSoftBitmapCache.get(url);
            if (bitmapReference != null) {
                bitmap = (Bitmap) bitmapReference.get();
                if (bitmap != null) {
                    return bitmap;
                }
                this.sSoftBitmapCache.remove(url);
            }
            return null;
        }
    }

    public Bitmap getLocalBitmap(String url) {
        if (isImageDownloaded(url)) {
            return getLocalBitmap(getFileDir(this.context), getFileName(url));
        }
        return null;
    }

    private Bitmap getLocalBitmap(String fileDir, String fileName) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(fileName);
        if (bitmap != null) {
            return bitmap;
        }
        if (this.SCALE_BITMAP) {
            bitmap = createScaleBitmap(fileDir, fileName);
        } else {
            bitmap = createBitmap(fileDir, fileName);
        }
        addBitmapToCache(fileName, bitmap);
        return bitmap;
    }

    private Bitmap getNetBitmap(String url, String fileDir, String fileName) {
        Bitmap bitmap = null;
        InputStream stream = null;
        try {
            stream = Crawler.crawlUrl(url);
            if (stream != null) {
                if (!this.SCALE_BITMAP) {
                    try {
                        bitmap = BitmapFactory.decodeStream(stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        this.sExecutor3.execute(new AnonymousClass6(bitmap, fileDir, fileName));
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

    private Bitmap getCustomBitmap(Object bitmapKey, String fileDir, String fileName) {
        Bitmap bitmap = null;
        if (!(this.customBitmapLoader == null || bitmapKey == null)) {
            bitmap = this.customBitmapLoader.getBitmap(bitmapKey);
        }
        if (bitmap != null && this.SCALE_BITMAP) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > this.MAX_WIDTH) {
                try {
                    Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, this.MAX_WIDTH, height * (this.MAX_WIDTH / width), true);
                    if (newBitmap != null) {
                        bitmap.recycle();
                        bitmap = newBitmap;
                    }
                } catch (Exception e) {
                }
            }
            this.sExecutor3.execute(new AnonymousClass7(bitmap, fileDir, fileName));
        }
        return bitmap;
    }

    private void async(String url, Object bitmapKey, String fileName, ImageAttacher attacher) {
        String fileDir = getFileDir(this.context);
        if ((this.customBitmapLoader == null || bitmapKey == null) && !FileUtils.isFileExists(fileDir, fileName)) {
            this.sExecutor.execute(new AnonymousClass9(url, fileDir, fileName, attacher));
        } else {
            this.sExecutor2.execute(new AnonymousClass8(fileDir, fileName, bitmapKey, attacher, url));
        }
    }
}
