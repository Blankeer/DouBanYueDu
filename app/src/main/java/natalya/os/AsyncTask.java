package natalya.os;

import android.os.Build.VERSION;
import android.util.Pair;
import java.util.concurrent.Executor;

public abstract class AsyncTask<Params, Progress, Result> extends android.os.AsyncTask<Params, Progress, Pair<Result, Throwable>> {
    private AsyncTaskListener<Result> mAsyncTaskListener;

    public interface AsyncTaskListener<Result> {
        void onAsyncTaskFailure(Throwable th);

        void onAsyncTaskPreExecute();

        void onAsyncTaskSuccess(Result result);
    }

    protected abstract Result onExecute(Params... paramsArr) throws Exception;

    public AsyncTask(AsyncTaskListener<Result> asyncTaskListener) {
        this.mAsyncTaskListener = asyncTaskListener;
    }

    public void setAsyncTaskListener(AsyncTaskListener<Result> listener) {
        this.mAsyncTaskListener = listener;
    }

    protected final Pair<Result, Throwable> doInBackground(Params... params) {
        Object res = null;
        Throwable ex = null;
        try {
            res = onExecute(params);
        } catch (Throwable e) {
            ex = e;
        }
        return new Pair(res, ex);
    }

    protected final void onPostExecute(Pair<Result, Throwable> result) {
        try {
            if (result.first != null) {
                onPostExecuteSuccess(result.first);
            } else {
                onPostExecuteFailure((Throwable) result.second);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void onPostExecuteSuccess(Result result) {
        if (this.mAsyncTaskListener != null) {
            this.mAsyncTaskListener.onAsyncTaskSuccess(result);
        }
    }

    protected void onPostExecuteFailure(Throwable exception) {
        if (this.mAsyncTaskListener != null) {
            this.mAsyncTaskListener.onAsyncTaskFailure(exception);
        }
    }

    public AsyncTask<Params, Progress, Result> smartExecute(Executor exec, Params... params) {
        if (VERSION.SDK_INT > 10) {
            if (exec == null) {
                exec = THREAD_POOL_EXECUTOR;
            }
            super.executeOnExecutor(exec, params);
        } else {
            super.execute(params);
        }
        return this;
    }

    public AsyncTask<Params, Progress, Result> smartExecute(Params... params) {
        if (VERSION.SDK_INT > 10) {
            super.executeOnExecutor(THREAD_POOL_EXECUTOR, params);
        } else {
            super.execute(params);
        }
        return this;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        if (this.mAsyncTaskListener != null) {
            this.mAsyncTaskListener.onAsyncTaskPreExecute();
        }
    }
}
