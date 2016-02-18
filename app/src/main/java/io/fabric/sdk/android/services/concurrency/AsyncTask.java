package io.fabric.sdk.android.services.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AsyncTask<Params, Progress, Result> {
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final int KEEP_ALIVE = 1;
    private static final String LOG_TAG = "AsyncTask";
    private static final int MAXIMUM_POOL_SIZE;
    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final int MESSAGE_POST_RESULT = 1;
    public static final Executor SERIAL_EXECUTOR;
    public static final Executor THREAD_POOL_EXECUTOR;
    private static volatile Executor defaultExecutor;
    private static final InternalHandler handler;
    private static final BlockingQueue<Runnable> poolWorkQueue;
    private static final ThreadFactory threadFactory;
    private final AtomicBoolean cancelled;
    private final FutureTask<Result> future;
    private volatile Status status;
    private final AtomicBoolean taskInvoked;
    private final WorkerRunnable<Params, Result> worker;

    /* renamed from: io.fabric.sdk.android.services.concurrency.AsyncTask.3 */
    class AnonymousClass3 extends FutureTask<Result> {
        AnonymousClass3(Callable x0) {
            super(x0);
        }

        protected void done() {
            try {
                AsyncTask.this.postResultIfNotInvoked(get());
            } catch (InterruptedException e) {
                Log.w(AsyncTask.LOG_TAG, e);
            } catch (ExecutionException e2) {
                throw new RuntimeException("An error occured while executing doInBackground()", e2.getCause());
            } catch (CancellationException e3) {
                AsyncTask.this.postResultIfNotInvoked(null);
            }
        }
    }

    /* renamed from: io.fabric.sdk.android.services.concurrency.AsyncTask.4 */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status;

        static {
            $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status = new int[Status.values().length];
            try {
                $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status[Status.RUNNING.ordinal()] = AsyncTask.MESSAGE_POST_RESULT;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status[Status.FINISHED.ordinal()] = AsyncTask.MESSAGE_POST_PROGRESS;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private static class AsyncTaskResult<Data> {
        final Data[] data;
        final AsyncTask task;

        AsyncTaskResult(AsyncTask task, Data... data) {
            this.task = task;
            this.data = data;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message msg) {
            AsyncTaskResult result = msg.obj;
            switch (msg.what) {
                case AsyncTask.MESSAGE_POST_RESULT /*1*/:
                    result.task.finish(result.data[AsyncTask.MAXIMUM_POOL_SIZE]);
                case AsyncTask.MESSAGE_POST_PROGRESS /*2*/:
                    result.task.onProgressUpdate(result.data);
                default:
            }
        }
    }

    private static class SerialExecutor implements Executor {
        Runnable active;
        final LinkedList<Runnable> tasks;

        /* renamed from: io.fabric.sdk.android.services.concurrency.AsyncTask.SerialExecutor.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ Runnable val$r;

            AnonymousClass1(Runnable runnable) {
                this.val$r = runnable;
            }

            public void run() {
                try {
                    this.val$r.run();
                } finally {
                    SerialExecutor.this.scheduleNext();
                }
            }
        }

        private SerialExecutor() {
            this.tasks = new LinkedList();
        }

        public synchronized void execute(Runnable r) {
            this.tasks.offer(new AnonymousClass1(r));
            if (this.active == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            Runnable runnable = (Runnable) this.tasks.poll();
            this.active = runnable;
            if (runnable != null) {
                AsyncTask.THREAD_POOL_EXECUTOR.execute(this.active);
            }
        }
    }

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] params;

        private WorkerRunnable() {
        }
    }

    protected abstract Result doInBackground(Params... paramsArr);

    static {
        CPU_COUNT = Runtime.getRuntime().availableProcessors();
        CORE_POOL_SIZE = CPU_COUNT + MESSAGE_POST_RESULT;
        MAXIMUM_POOL_SIZE = (CPU_COUNT * MESSAGE_POST_PROGRESS) + MESSAGE_POST_RESULT;
        threadFactory = new ThreadFactory() {
            private final AtomicInteger count;

            {
                this.count = new AtomicInteger(AsyncTask.MESSAGE_POST_RESULT);
            }

            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncTask #" + this.count.getAndIncrement());
            }
        };
        poolWorkQueue = new LinkedBlockingQueue(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
        SERIAL_EXECUTOR = new SerialExecutor();
        handler = new InternalHandler();
        defaultExecutor = SERIAL_EXECUTOR;
    }

    public static void init() {
        handler.getLooper();
    }

    public static void setDefaultExecutor(Executor exec) {
        defaultExecutor = exec;
    }

    public AsyncTask() {
        this.status = Status.PENDING;
        this.cancelled = new AtomicBoolean();
        this.taskInvoked = new AtomicBoolean();
        this.worker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                AsyncTask.this.taskInvoked.set(true);
                Process.setThreadPriority(10);
                return AsyncTask.this.postResult(AsyncTask.this.doInBackground(this.params));
            }
        };
        this.future = new AnonymousClass3(this.worker);
    }

    private void postResultIfNotInvoked(Result result) {
        if (!this.taskInvoked.get()) {
            postResult(result);
        }
    }

    private Result postResult(Result result) {
        InternalHandler internalHandler = handler;
        Object[] objArr = new Object[MESSAGE_POST_RESULT];
        objArr[MAXIMUM_POOL_SIZE] = result;
        internalHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult(this, objArr)).sendToTarget();
        return result;
    }

    public final Status getStatus() {
        return this.status;
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... progressArr) {
    }

    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }

    public final boolean isCancelled() {
        return this.cancelled.get();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled.set(true);
        return this.future.cancel(mayInterruptIfRunning);
    }

    public final Result get() throws InterruptedException, ExecutionException {
        return this.future.get();
    }

    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.future.get(timeout, unit);
    }

    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(defaultExecutor, params);
    }

    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if (this.status != Status.PENDING) {
            switch (AnonymousClass4.$SwitchMap$io$fabric$sdk$android$services$concurrency$AsyncTask$Status[this.status.ordinal()]) {
                case MESSAGE_POST_RESULT /*1*/:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case MESSAGE_POST_PROGRESS /*2*/:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        }
        this.status = Status.RUNNING;
        onPreExecute();
        this.worker.params = params;
        exec.execute(this.future);
        return this;
    }

    public static void execute(Runnable runnable) {
        defaultExecutor.execute(runnable);
    }

    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            handler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        this.status = Status.FINISHED;
    }
}
