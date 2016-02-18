package org.androidannotations.api;

import android.os.Looper;
import android.util.Log;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BackgroundExecutor {
    private static final ThreadLocal<String> CURRENT_SERIAL;
    public static final Executor DEFAULT_EXECUTOR;
    public static final WrongThreadListener DEFAULT_WRONG_THREAD_LISTENER;
    private static final String TAG = "BackgroundExecutor";
    private static final List<Task> TASKS;
    private static Executor executor;
    private static WrongThreadListener wrongThreadListener;

    public static abstract class Task implements Runnable {
        private boolean executionAsked;
        private Future<?> future;
        private String id;
        private AtomicBoolean managed;
        private long remainingDelay;
        private String serial;
        private long targetTimeMillis;

        public abstract void execute();

        public Task(String id, long delay, String serial) {
            this.managed = new AtomicBoolean();
            if (!Table.STRING_DEFAULT_VALUE.equals(id)) {
                this.id = id;
            }
            if (delay > 0) {
                this.remainingDelay = delay;
                this.targetTimeMillis = System.currentTimeMillis() + delay;
            }
            if (!Table.STRING_DEFAULT_VALUE.equals(serial)) {
                this.serial = serial;
            }
        }

        public void run() {
            if (!this.managed.getAndSet(true)) {
                try {
                    BackgroundExecutor.CURRENT_SERIAL.set(this.serial);
                    execute();
                } finally {
                    postExecute();
                }
            }
        }

        private void postExecute() {
            if (this.id != null || this.serial != null) {
                BackgroundExecutor.CURRENT_SERIAL.set(null);
                synchronized (BackgroundExecutor.class) {
                    BackgroundExecutor.TASKS.remove(this);
                    if (this.serial != null) {
                        Task next = BackgroundExecutor.take(this.serial);
                        if (next != null) {
                            if (next.remainingDelay != 0) {
                                next.remainingDelay = Math.max(0, this.targetTimeMillis - System.currentTimeMillis());
                            }
                            BackgroundExecutor.execute(next);
                        }
                    }
                }
            }
        }
    }

    public interface WrongThreadListener {
        void onBgExpected(String... strArr);

        void onUiExpected();

        void onWrongBgSerial(String str, String... strArr);
    }

    /* renamed from: org.androidannotations.api.BackgroundExecutor.2 */
    static class AnonymousClass2 extends Task {
        final /* synthetic */ Runnable val$runnable;

        AnonymousClass2(String x0, long x1, String x2, Runnable runnable) {
            this.val$runnable = runnable;
            super(x0, x1, x2);
        }

        public void execute() {
            this.val$runnable.run();
        }
    }

    static {
        DEFAULT_EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        executor = DEFAULT_EXECUTOR;
        DEFAULT_WRONG_THREAD_LISTENER = new WrongThreadListener() {
            public void onUiExpected() {
                throw new IllegalStateException("Method invocation is expected from the UI thread");
            }

            public void onBgExpected(String... expectedSerials) {
                if (expectedSerials.length == 0) {
                    throw new IllegalStateException("Method invocation is expected from a background thread, but it was called from the UI thread");
                }
                throw new IllegalStateException("Method invocation is expected from one of serials " + Arrays.toString(expectedSerials) + ", but it was called from the UI thread");
            }

            public void onWrongBgSerial(String currentSerial, String... expectedSerials) {
                if (currentSerial == null) {
                    currentSerial = "anonymous";
                }
                throw new IllegalStateException("Method invocation is expected from one of serials " + Arrays.toString(expectedSerials) + ", but it was called from " + currentSerial + " serial");
            }
        };
        wrongThreadListener = DEFAULT_WRONG_THREAD_LISTENER;
        TASKS = new ArrayList();
        CURRENT_SERIAL = new ThreadLocal();
    }

    private BackgroundExecutor() {
    }

    private static Future<?> directExecute(Runnable runnable, long delay) {
        if (delay > 0) {
            if (executor instanceof ScheduledExecutorService) {
                return executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
            }
            throw new IllegalArgumentException("The executor set does not support scheduling");
        } else if (executor instanceof ExecutorService) {
            return executor.submit(runnable);
        } else {
            executor.execute(runnable);
            return null;
        }
    }

    public static synchronized void execute(Task task) {
        synchronized (BackgroundExecutor.class) {
            Future<?> future = null;
            if (task.serial == null || !hasSerialRunning(task.serial)) {
                task.executionAsked = true;
                future = directExecute(task, task.remainingDelay);
            }
            if (!(task.id == null && task.serial == null)) {
                task.future = future;
                TASKS.add(task);
            }
        }
    }

    public static void execute(Runnable runnable, String id, long delay, String serial) {
        execute(new AnonymousClass2(id, delay, serial, runnable));
    }

    public static void execute(Runnable runnable, long delay) {
        directExecute(runnable, delay);
    }

    public static void execute(Runnable runnable) {
        directExecute(runnable, 0);
    }

    public static void execute(Runnable runnable, String id, String serial) {
        execute(runnable, id, 0, serial);
    }

    public static void setExecutor(Executor executor) {
        executor = executor;
    }

    public static void setWrongThreadListener(WrongThreadListener listener) {
        wrongThreadListener = listener;
    }

    public static synchronized void cancelAll(String id, boolean mayInterruptIfRunning) {
        synchronized (BackgroundExecutor.class) {
            for (int i = TASKS.size() - 1; i >= 0; i--) {
                Task task = (Task) TASKS.get(i);
                if (id.equals(task.id)) {
                    if (task.future != null) {
                        task.future.cancel(mayInterruptIfRunning);
                        if (!task.managed.getAndSet(true)) {
                            task.postExecute();
                        }
                    } else if (task.executionAsked) {
                        Log.w(TAG, "A task with id " + task.id + " cannot be cancelled (the executor set does not support it)");
                    } else {
                        TASKS.remove(i);
                    }
                }
            }
        }
    }

    public static void checkUiThread() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            wrongThreadListener.onUiExpected();
        }
    }

    public static void checkBgThread(String... serials) {
        if (serials.length != 0) {
            String current = (String) CURRENT_SERIAL.get();
            if (current == null) {
                wrongThreadListener.onWrongBgSerial(null, serials);
                return;
            }
            String[] arr$ = serials;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                if (!arr$[i$].equals(current)) {
                    i$++;
                } else {
                    return;
                }
            }
            wrongThreadListener.onWrongBgSerial(current, serials);
        } else if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            wrongThreadListener.onBgExpected(serials);
        }
    }

    private static boolean hasSerialRunning(String serial) {
        for (Task task : TASKS) {
            if (task.executionAsked && serial.equals(task.serial)) {
                return true;
            }
        }
        return false;
    }

    private static Task take(String serial) {
        int len = TASKS.size();
        for (int i = 0; i < len; i++) {
            if (serial.equals(((Task) TASKS.get(i)).serial)) {
                return (Task) TASKS.remove(i);
            }
        }
        return null;
    }
}
