package io.fabric.sdk.android.services.concurrency;

import io.fabric.sdk.android.services.concurrency.AsyncTask.Status;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public abstract class PriorityAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements Dependency<Task>, PriorityProvider, Task, DelegateProvider {
    private final PriorityTask priorityTask;

    private static class ProxyExecutor<Result> implements Executor {
        private final Executor executor;
        private final PriorityAsyncTask task;

        /* renamed from: io.fabric.sdk.android.services.concurrency.PriorityAsyncTask.ProxyExecutor.1 */
        class AnonymousClass1 extends PriorityFutureTask<Result> {
            AnonymousClass1(Runnable x0, Object x1) {
                super(x0, x1);
            }

            public <T extends Dependency<Task> & PriorityProvider & Task> T getDelegate() {
                return ProxyExecutor.this.task;
            }
        }

        public ProxyExecutor(Executor ex, PriorityAsyncTask task) {
            this.executor = ex;
            this.task = task;
        }

        public void execute(Runnable command) {
            this.executor.execute(new AnonymousClass1(command, null));
        }
    }

    public PriorityAsyncTask() {
        this.priorityTask = new PriorityTask();
    }

    public final void executeOnExecutor(ExecutorService exec, Params... params) {
        super.executeOnExecutor(new ProxyExecutor(exec, this), params);
    }

    public int compareTo(Object another) {
        return Priority.compareTo(this, another);
    }

    public void addDependency(Task task) {
        if (getStatus() != Status.PENDING) {
            throw new IllegalStateException("Must not add Dependency after task is running");
        }
        ((Dependency) ((PriorityProvider) getDelegate())).addDependency(task);
    }

    public Collection<Task> getDependencies() {
        return ((Dependency) ((PriorityProvider) getDelegate())).getDependencies();
    }

    public boolean areDependenciesMet() {
        return ((Dependency) ((PriorityProvider) getDelegate())).areDependenciesMet();
    }

    public Priority getPriority() {
        return ((PriorityProvider) getDelegate()).getPriority();
    }

    public void setFinished(boolean finished) {
        ((Task) ((PriorityProvider) getDelegate())).setFinished(finished);
    }

    public boolean isFinished() {
        return ((Task) ((PriorityProvider) getDelegate())).isFinished();
    }

    public void setError(Throwable throwable) {
        ((Task) ((PriorityProvider) getDelegate())).setError(throwable);
    }

    public Throwable getError() {
        return ((Task) ((PriorityProvider) getDelegate())).getError();
    }

    public <T extends Dependency<Task> & PriorityProvider & Task> T getDelegate() {
        return this.priorityTask;
    }
}
