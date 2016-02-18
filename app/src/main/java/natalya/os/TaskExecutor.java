package natalya.os;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import natalya.log.NLog;

public final class TaskExecutor {
    public static final String SEPARATOR = "@@";
    public static final String TAG;
    private boolean mDebug;
    private ExecutorService mExecutor;
    private Map<String, Future<?>> mFutures;
    private final Object mLock;
    private Map<String, ExtendedRunnable> mTasks;
    private Handler mUiHandler;

    /* renamed from: natalya.os.TaskExecutor.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ TaskCallback val$callback;
        final /* synthetic */ Object val$result;

        AnonymousClass2(TaskCallback taskCallback, Object obj) {
            this.val$callback = taskCallback;
            this.val$result = obj;
        }

        public void run() {
            this.val$callback.onTaskSuccess(this.val$result, null, null);
        }
    }

    /* renamed from: natalya.os.TaskExecutor.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ TaskCallback val$callback;
        final /* synthetic */ Exception val$exception;

        AnonymousClass3(TaskCallback taskCallback, Exception exception) {
            this.val$callback = taskCallback;
            this.val$exception = exception;
        }

        public void run() {
            this.val$callback.onTaskFailure(this.val$exception, null);
        }
    }

    /* renamed from: natalya.os.TaskExecutor.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ String val$tag;

        AnonymousClass4(String str) {
            this.val$tag = str;
        }

        public void run() {
            synchronized (TaskExecutor.this.mLock) {
                TaskExecutor.this.remove(this.val$tag);
            }
        }
    }

    public static abstract class ExtendedRunnable implements Runnable {
        public static final String TAG;
        private boolean mCancelled;
        private String mName;

        static {
            TAG = ExtendedRunnable.class.getSimpleName();
        }

        public ExtendedRunnable() {
            this(TAG);
        }

        public ExtendedRunnable(String name) {
            this.mName = name;
            this.mCancelled = false;
        }

        public boolean isInterrupted() {
            return Thread.currentThread().isInterrupted();
        }

        public void cancel() {
            this.mCancelled = true;
        }

        public boolean isCancelled() {
            return this.mCancelled;
        }

        public String getName() {
            return this.mName;
        }
    }

    private static final class SingletonHolder {
        static final TaskExecutor INSTANCE;

        private SingletonHolder() {
        }

        static {
            INSTANCE = new TaskExecutor();
        }
    }

    public interface TaskCallback<Result> {
        void onTaskFailure(Throwable th, Bundle bundle);

        void onTaskSuccess(Result result, Bundle bundle, Object obj);
    }

    /* renamed from: natalya.os.TaskExecutor.1 */
    class AnonymousClass1 extends ExtendedRunnable {
        final /* synthetic */ Callable val$callable;
        final /* synthetic */ TaskCallback val$callback;
        final /* synthetic */ String val$tag;
        final /* synthetic */ WeakReference val$weakTarget;

        AnonymousClass1(String x0, Callable callable, WeakReference weakReference, TaskCallback taskCallback, String str) {
            this.val$callable = callable;
            this.val$weakTarget = weakReference;
            this.val$callback = taskCallback;
            this.val$tag = str;
            super(x0);
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: java.lang.NullPointerException
	at java.util.BitSet.or(BitSet.java:673)
	at jadx.core.utils.BlockUtils.getPathCross(BlockUtils.java:416)
	at jadx.core.dex.visitors.regions.IfMakerHelper.restructureIf(IfMakerHelper.java:80)
	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:599)
	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:90)
	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:912)
	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:881)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JavaClass.getMethods(JavaClass.java:188)
*/
            /*
            r5 = this;
            r2 = natalya.os.TaskExecutor.this;	 Catch:{ Exception -> 0x008c }
            r2 = r2.mDebug;	 Catch:{ Exception -> 0x008c }
            if (r2 == 0) goto L_0x000f;	 Catch:{ Exception -> 0x008c }
        L_0x0008:
            r2 = TAG;	 Catch:{ Exception -> 0x008c }
            r3 = "execute() start";	 Catch:{ Exception -> 0x008c }
            natalya.log.NLog.v(r2, r3);	 Catch:{ Exception -> 0x008c }
        L_0x000f:
            r2 = r5.val$callable;	 Catch:{ Exception -> 0x008c }
            r1 = r2.call();	 Catch:{ Exception -> 0x008c }
            r2 = r5.isCancelled();	 Catch:{ Exception -> 0x008c }
            if (r2 == 0) goto L_0x0032;	 Catch:{ Exception -> 0x008c }
        L_0x001b:
            r2 = natalya.os.TaskExecutor.this;	 Catch:{ Exception -> 0x008c }
            r2 = r2.mDebug;	 Catch:{ Exception -> 0x008c }
            if (r2 == 0) goto L_0x002a;	 Catch:{ Exception -> 0x008c }
        L_0x0023:
            r2 = TAG;	 Catch:{ Exception -> 0x008c }
            r3 = "execute() isCancelled, return";	 Catch:{ Exception -> 0x008c }
            natalya.log.NLog.v(r2, r3);	 Catch:{ Exception -> 0x008c }
        L_0x002a:
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
        L_0x0031:
            return;
        L_0x0032:
            r2 = r5.isInterrupted();	 Catch:{ Exception -> 0x008c }
            if (r2 == 0) goto L_0x004f;	 Catch:{ Exception -> 0x008c }
        L_0x0038:
            r2 = natalya.os.TaskExecutor.this;	 Catch:{ Exception -> 0x008c }
            r2 = r2.mDebug;	 Catch:{ Exception -> 0x008c }
            if (r2 == 0) goto L_0x0047;	 Catch:{ Exception -> 0x008c }
        L_0x0040:
            r2 = TAG;	 Catch:{ Exception -> 0x008c }
            r3 = "execute() isInterrupted, return";	 Catch:{ Exception -> 0x008c }
            natalya.log.NLog.v(r2, r3);	 Catch:{ Exception -> 0x008c }
        L_0x0047:
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
            goto L_0x0031;
        L_0x004f:
            r2 = r5.val$weakTarget;	 Catch:{ Exception -> 0x008c }
            r2 = r2.get();	 Catch:{ Exception -> 0x008c }
            if (r2 != 0) goto L_0x006e;	 Catch:{ Exception -> 0x008c }
        L_0x0057:
            r2 = natalya.os.TaskExecutor.this;	 Catch:{ Exception -> 0x008c }
            r2 = r2.mDebug;	 Catch:{ Exception -> 0x008c }
            if (r2 == 0) goto L_0x0066;	 Catch:{ Exception -> 0x008c }
        L_0x005f:
            r2 = TAG;	 Catch:{ Exception -> 0x008c }
            r3 = "execute() caller is null, return";	 Catch:{ Exception -> 0x008c }
            natalya.log.NLog.v(r2, r3);	 Catch:{ Exception -> 0x008c }
        L_0x0066:
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
            goto L_0x0031;
        L_0x006e:
            r2 = natalya.os.TaskExecutor.this;	 Catch:{ Exception -> 0x008c }
            r3 = r5.val$callback;	 Catch:{ Exception -> 0x008c }
            r2.onTaskSuccess(r1, r3);	 Catch:{ Exception -> 0x008c }
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
        L_0x007c:
            r2 = natalya.os.TaskExecutor.this;
            r2 = r2.mDebug;
            if (r2 == 0) goto L_0x0031;
        L_0x0084:
            r2 = TAG;
            r3 = "execute() end";
            natalya.log.NLog.v(r2, r3);
            goto L_0x0031;
        L_0x008c:
            r0 = move-exception;
            r2 = natalya.os.TaskExecutor.this;	 Catch:{ all -> 0x00ee }
            r2 = r2.mDebug;	 Catch:{ all -> 0x00ee }
            if (r2 == 0) goto L_0x00b0;	 Catch:{ all -> 0x00ee }
        L_0x0095:
            r0.printStackTrace();	 Catch:{ all -> 0x00ee }
            r2 = TAG;	 Catch:{ all -> 0x00ee }
            r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00ee }
            r3.<init>();	 Catch:{ all -> 0x00ee }
            r4 = "execute() error: ";	 Catch:{ all -> 0x00ee }
            r3 = r3.append(r4);	 Catch:{ all -> 0x00ee }
            r3 = r3.append(r0);	 Catch:{ all -> 0x00ee }
            r3 = r3.toString();	 Catch:{ all -> 0x00ee }
            natalya.log.NLog.e(r2, r3);	 Catch:{ all -> 0x00ee }
        L_0x00b0:
            r2 = r5.isCancelled();	 Catch:{ all -> 0x00ee }
            if (r2 == 0) goto L_0x00bf;
        L_0x00b6:
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
            goto L_0x0031;
        L_0x00bf:
            r2 = r5.isInterrupted();	 Catch:{ all -> 0x00ee }
            if (r2 == 0) goto L_0x00ce;
        L_0x00c5:
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
            goto L_0x0031;
        L_0x00ce:
            r2 = r5.val$weakTarget;	 Catch:{ all -> 0x00ee }
            r2 = r2.get();	 Catch:{ all -> 0x00ee }
            if (r2 != 0) goto L_0x00df;
        L_0x00d6:
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
            goto L_0x0031;
        L_0x00df:
            r2 = natalya.os.TaskExecutor.this;	 Catch:{ all -> 0x00ee }
            r3 = r5.val$callback;	 Catch:{ all -> 0x00ee }
            r2.onTaskFailure(r0, r3);	 Catch:{ all -> 0x00ee }
            r2 = natalya.os.TaskExecutor.this;
            r3 = r5.val$tag;
            r2.onFinally(r3);
            goto L_0x007c;
        L_0x00ee:
            r2 = move-exception;
            r3 = natalya.os.TaskExecutor.this;
            r4 = r5.val$tag;
            r3.onFinally(r4);
            throw r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: natalya.os.TaskExecutor.1.run():void");
        }
    }

    public static abstract class SimpleTaskCallback<Result> implements TaskCallback<Result> {
        public void onTaskSuccess(Result result, Bundle extras, Object object) {
        }

        public void onTaskFailure(Throwable e, Bundle extras) {
        }
    }

    public static class BitmapTaskCallback extends SimpleTaskCallback<Bitmap> {
    }

    public static class BooleanTaskCallback extends SimpleTaskCallback<Boolean> {
    }

    public static class StringTaskCallback extends SimpleTaskCallback<String> {
    }

    static {
        TAG = TaskExecutor.class.getSimpleName();
    }

    public static TaskExecutor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private TaskExecutor() {
        this.mLock = new Object();
        if (this.mDebug) {
            NLog.v(TAG, "TaskExecutor()");
        }
        this.mTasks = Collections.synchronizedMap(new WeakHashMap());
        this.mFutures = Collections.synchronizedMap(new WeakHashMap());
        ensureHandler();
        ensureExecutor();
    }

    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    public <Result, Caller> String execute(Callable<Result> callable, TaskCallback<Result> callback, Caller caller) {
        checkArguments(callable, caller);
        WeakReference<Caller> weakTarget = new WeakReference(caller);
        String tag = buildTag(caller);
        if (this.mDebug) {
            NLog.v(TAG, "execute() callable=" + callable + " callback=" + callback + " caller=" + caller);
        }
        return doExecute(tag, new AnonymousClass1(tag, callable, weakTarget, callback, tag));
    }

    public boolean isRunning(String tag) {
        Future<?> future = (Future) this.mFutures.get(tag);
        return (future == null || future.isDone() || future.isCancelled()) ? false : true;
    }

    public void cancelAll() {
        if (this.mDebug) {
            NLog.v(TAG, "cancelAll()");
        }
        cancelAllRunnables();
        cancelAllFutures();
    }

    private void cancelAllRunnables() {
        for (Entry<String, ExtendedRunnable> entry : this.mTasks.entrySet()) {
            ExtendedRunnable runnable = (ExtendedRunnable) entry.getValue();
            if (runnable != null) {
                runnable.cancel();
            }
        }
        this.mTasks.clear();
    }

    private void cancelAllFutures() {
        for (Entry<String, Future<?>> entry : this.mFutures.entrySet()) {
            Future<?> future = (Future) entry.getValue();
            if (future != null) {
                future.cancel(true);
            }
        }
        this.mFutures.clear();
    }

    private void cancelRunnablesByTags(List<String> filterTags) {
        if (filterTags != null && !filterTags.isEmpty()) {
            Iterator<Entry<String, ExtendedRunnable>> taskIterator = this.mTasks.entrySet().iterator();
            while (taskIterator.hasNext()) {
                Entry<String, ExtendedRunnable> entry = (Entry) taskIterator.next();
                if (filterTags.contains((String) entry.getKey())) {
                    ExtendedRunnable runnable = (ExtendedRunnable) entry.getValue();
                    if (runnable != null) {
                        runnable.cancel();
                    }
                    taskIterator.remove();
                }
            }
        }
    }

    private void cancelFuturesByTags(List<String> filterTags) {
        if (filterTags != null && !filterTags.isEmpty()) {
            Iterator<Entry<String, Future<?>>> futureIterator = this.mFutures.entrySet().iterator();
            while (futureIterator.hasNext()) {
                Entry<String, Future<?>> entry = (Entry) futureIterator.next();
                if (filterTags.contains((String) entry.getKey())) {
                    Future<?> future = (Future) entry.getValue();
                    if (future != null) {
                        future.cancel(true);
                    }
                    futureIterator.remove();
                }
            }
        }
    }

    public boolean cancelByTag(String tag) {
        if (this.mDebug) {
            NLog.v(TAG, "cancelByCaller() tag=" + tag);
        }
        ExtendedRunnable runnable = (ExtendedRunnable) this.mTasks.remove(tag);
        if (runnable != null) {
            runnable.cancel();
        }
        Future<?> future = (Future) this.mFutures.remove(tag);
        if (future == null) {
            return false;
        }
        future.cancel(true);
        return true;
    }

    public <Caller> int cancelByCaller(Caller caller) {
        int cancelledCount = 0;
        String tagPrefix = buildTagPrefix(caller).toString();
        if (this.mDebug) {
            NLog.v(TAG, "cancelByCaller() caller=" + caller);
        }
        List<String> filterTags = new ArrayList();
        for (String tag : this.mFutures.keySet()) {
            if (tag.startsWith(tagPrefix)) {
                filterTags.add(tag);
                cancelledCount++;
            }
        }
        cancelRunnablesByTags(filterTags);
        cancelFuturesByTags(filterTags);
        return cancelledCount;
    }

    public void setExecutor(ExecutorService executor) {
        this.mExecutor = executor;
    }

    public void destroy() {
        if (this.mDebug) {
            NLog.v(TAG, "destroy()");
        }
        cancelAll();
        destroyHandler();
        destroyExecutor();
    }

    private void remove(String tag) {
        if (this.mDebug) {
            NLog.v(TAG, "remove() tag=" + tag);
        }
        synchronized (this.mLock) {
            this.mTasks.remove(tag);
            this.mFutures.remove(tag);
        }
    }

    private String doExecute(String tag, ExtendedRunnable runnable) {
        if (this.mDebug) {
            NLog.v(TAG, "doExecute() tag=" + tag + " runnable=" + runnable);
        }
        synchronized (this.mLock) {
            Future<?> future = doSubmit(runnable);
            this.mTasks.put(tag, runnable);
            this.mFutures.put(tag, future);
        }
        return tag;
    }

    private Future<?> doSubmit(Runnable runnable) {
        if (this.mDebug) {
            String name = "Runnable";
            if (runnable instanceof ExtendedRunnable) {
                name = ((ExtendedRunnable) runnable).getName();
            }
            NLog.v(TAG, "submit() name=" + name);
        }
        ensureHandler();
        ensureExecutor();
        return this.mExecutor.submit(runnable);
    }

    private <Result> void onTaskSuccess(Result result, TaskCallback<Result> callback) {
        if (this.mDebug) {
            NLog.v(TAG, "onTaskComplete() result=" + result + " callback=" + callback);
        }
        if (this.mUiHandler != null && callback != null) {
            this.mUiHandler.post(new AnonymousClass2(callback, result));
        }
    }

    private <Result> void onTaskFailure(Exception exception, TaskCallback<Result> callback) {
        if (this.mDebug) {
            NLog.v(TAG, "onTaskComplete() exception=" + exception + " callback=" + callback);
        }
        if (this.mUiHandler != null && callback != null) {
            this.mUiHandler.post(new AnonymousClass3(callback, exception));
        }
    }

    private void onFinally(String tag) {
        if (this.mDebug) {
            NLog.v(TAG, "onFinally() tag=" + tag);
        }
        if (this.mUiHandler != null) {
            this.mUiHandler.post(new AnonymousClass4(tag));
        }
    }

    private ExecutorService ensureExecutor() {
        if (this.mExecutor == null) {
            this.mExecutor = Executors.newCachedThreadPool();
        }
        return this.mExecutor;
    }

    private void ensureHandler() {
        if (this.mDebug) {
            NLog.v(TAG, "ensureHandler()");
        }
        if (this.mUiHandler == null) {
            this.mUiHandler = new Handler(Looper.getMainLooper());
        }
    }

    private void destroyExecutor() {
        if (this.mDebug) {
            NLog.v(TAG, "destroyExecutor()");
        }
        if (this.mExecutor != null) {
            this.mExecutor.shutdownNow();
            this.mExecutor = null;
        }
    }

    private void destroyHandler() {
        if (this.mDebug) {
            NLog.v(TAG, "destroyHandler()");
        }
        synchronized (this.mLock) {
            if (this.mUiHandler != null) {
                this.mUiHandler.removeCallbacksAndMessages(null);
                this.mUiHandler = null;
            }
        }
    }

    private static <Caller> String buildTag(Caller caller) {
        return buildTagPrefix(caller).append(System.currentTimeMillis()).toString();
    }

    private static <Caller> StringBuilder buildTagPrefix(Caller caller) {
        String className = caller.getClass().getName();
        long hashCode = (long) System.identityHashCode(caller);
        StringBuilder builder = new StringBuilder();
        builder.append(hashCode).append(SEPARATOR).append(className).append(SEPARATOR);
        return builder;
    }

    private static void checkArguments(Object... args) {
        for (Object o : args) {
            if (o == null) {
                throw new NullPointerException("argument can not be null.");
            }
        }
    }
}
