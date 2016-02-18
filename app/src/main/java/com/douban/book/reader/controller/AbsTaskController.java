package com.douban.book.reader.controller;

import android.os.Bundle;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.AssertUtils;
import com.douban.book.reader.util.Logger;
import java.util.concurrent.Callable;
import natalya.os.TaskExecutor;
import natalya.os.TaskExecutor.TaskCallback;

public abstract class AbsTaskController {
    private static final boolean DEBUG;
    private static final String TAG;

    /* renamed from: com.douban.book.reader.controller.AbsTaskController.1 */
    class AnonymousClass1 implements Callable<T> {
        final /* synthetic */ Callable val$callable;

        AnonymousClass1(Callable callable) {
            this.val$callable = callable;
        }

        public T call() throws Exception {
            return this.val$callable.call();
        }
    }

    /* renamed from: com.douban.book.reader.controller.AbsTaskController.3 */
    class AnonymousClass3 implements Callable<Boolean> {
        final /* synthetic */ Runnable val$runnable;

        AnonymousClass3(Runnable runnable) {
            this.val$runnable = runnable;
        }

        public Boolean call() throws Exception {
            this.val$runnable.run();
            return Boolean.valueOf(true);
        }
    }

    /* renamed from: com.douban.book.reader.controller.AbsTaskController.2 */
    class AnonymousClass2 implements TaskCallback<T> {
        final /* synthetic */ TaskCallback val$callback;

        AnonymousClass2(TaskCallback taskCallback) {
            this.val$callback = taskCallback;
        }

        public void onTaskSuccess(T result, Bundle extras, Object object) {
            if (this.val$callback != null) {
                this.val$callback.onTaskSuccess(result, extras, object);
            }
        }

        public void onTaskFailure(Throwable e, Bundle extras) {
            Logger.e(AbsTaskController.TAG, e);
            if (this.val$callback != null) {
                this.val$callback.onTaskFailure(e, extras);
            }
        }
    }

    static {
        DEBUG = AppInfo.isDebug();
        TAG = AbsTaskController.class.getSimpleName();
    }

    public <T> String execute(Callable<T> callable, TaskCallback<T> callback, Object caller) {
        if (!DEBUG) {
            return TaskExecutor.getInstance().execute(callable, callback, caller);
        }
        Logger.v(TAG, "execute() caller=" + caller, new Object[0]);
        return TaskExecutor.getInstance().execute(new AnonymousClass1(callable), new AnonymousClass2(callback), caller);
    }

    public String execute(Runnable runnable, Object caller) {
        AssertUtils.notNull("caller and runnable cann't be null", runnable, caller);
        return TaskExecutor.getInstance().execute(new AnonymousClass3(runnable), null, caller);
    }

    public void destroy() {
        TaskExecutor.getInstance().destroy();
    }

    public void cancelByCaller(Object caller) {
        TaskExecutor.getInstance().cancelByCaller(caller);
    }

    public void cancelByTag(String tag) {
        TaskExecutor.getInstance().cancelByTag(tag);
    }

    public void cancelAll() {
        TaskExecutor.getInstance().cancelAll();
    }

    public void onLogout() {
        cancelAll();
    }
}
