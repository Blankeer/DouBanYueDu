package com.path.android.jobqueue;

import android.content.Context;
import com.path.android.jobqueue.cachedQueue.CachedJobQueue;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.config.Configuration.Builder;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.executor.JobConsumerExecutor;
import com.path.android.jobqueue.executor.JobConsumerExecutor.Contract;
import com.path.android.jobqueue.log.JqLog;
import com.path.android.jobqueue.network.NetworkEventProvider;
import com.path.android.jobqueue.network.NetworkEventProvider.Listener;
import com.path.android.jobqueue.network.NetworkUtil;
import com.path.android.jobqueue.nonPersistentQueue.NonPersistentPriorityQueue;
import com.path.android.jobqueue.persistentQueue.sqlite.SqliteJobQueue;
import com.path.android.jobqueue.persistentQueue.sqlite.SqliteJobQueue.JavaSerializer;
import com.path.android.jobqueue.persistentQueue.sqlite.SqliteJobQueue.JobSerializer;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobManager implements Listener {
    public static final long NOT_DELAYED_JOB_DELAY = Long.MIN_VALUE;
    public static final long NOT_RUNNING_SESSION_ID = Long.MIN_VALUE;
    public static final long NS_PER_MS = 1000000;
    private final Context appContext;
    private final Contract consumerContract;
    private final DependencyInjector dependencyInjector;
    private final Object getNextJobLock;
    private final JobConsumerExecutor jobConsumerExecutor;
    private final NetworkUtil networkUtil;
    private final Object newJobListeners;
    private final JobQueue nonPersistentJobQueue;
    private final ConcurrentHashMap<Long, CountDownLatch> nonPersistentOnAddedLocks;
    private final Runnable notifyRunnable;
    private final JobQueue persistentJobQueue;
    private final ConcurrentHashMap<Long, CountDownLatch> persistentOnAddedLocks;
    private boolean running;
    private final CopyOnWriteGroupSet runningJobGroups;
    private final long sessionId;
    private final ScheduledExecutorService timedExecutor;

    /* renamed from: com.path.android.jobqueue.JobManager.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ BaseJob val$baseJob;
        final /* synthetic */ int val$priority;

        AnonymousClass3(int i, BaseJob baseJob) {
            this.val$priority = i;
            this.val$baseJob = baseJob;
        }

        public void run() {
            JobManager.this.addJob(this.val$priority, this.val$baseJob);
        }
    }

    /* renamed from: com.path.android.jobqueue.JobManager.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ BaseJob val$baseJob;
        final /* synthetic */ long val$callTime;
        final /* synthetic */ AsyncAddCallback val$callback;
        final /* synthetic */ long val$delay;
        final /* synthetic */ int val$priority;

        AnonymousClass4(long j, int i, long j2, BaseJob baseJob, AsyncAddCallback asyncAddCallback) {
            this.val$callTime = j;
            this.val$priority = i;
            this.val$delay = j2;
            this.val$baseJob = baseJob;
            this.val$callback = asyncAddCallback;
        }

        public void run() {
            try {
                long id = JobManager.this.addJob(this.val$priority, Math.max(0, this.val$delay - ((System.nanoTime() - this.val$callTime) / JobManager.NS_PER_MS)), this.val$baseJob);
                if (this.val$callback != null) {
                    this.val$callback.onAdded(id);
                }
            } catch (Throwable t) {
                JqLog.e(t, "addJobInBackground received an exception. job class: %s", this.val$baseJob.getClass().getSimpleName());
            }
        }
    }

    public static class DefaultQueueFactory implements QueueFactory {
        JobSerializer jobSerializer;

        public DefaultQueueFactory() {
            this.jobSerializer = new JavaSerializer();
        }

        public DefaultQueueFactory(JobSerializer jobSerializer) {
            this.jobSerializer = jobSerializer;
        }

        public JobQueue createPersistentQueue(Context context, Long sessionId, String id) {
            return new CachedJobQueue(new SqliteJobQueue(context, sessionId.longValue(), id, this.jobSerializer));
        }

        public JobQueue createNonPersistent(Context context, Long sessionId, String id) {
            return new CachedJobQueue(new NonPersistentPriorityQueue(sessionId.longValue(), id));
        }
    }

    public JobManager(Context context) {
        this(context, "default");
    }

    public JobManager(Context context, String id) {
        this(context, new Builder(context).id(id).build());
    }

    public JobManager(Context context, Configuration config) {
        this.newJobListeners = new Object();
        this.getNextJobLock = new Object();
        this.notifyRunnable = new Runnable() {
            public void run() {
                JobManager.this.notifyJobConsumer();
            }
        };
        this.consumerContract = new Contract() {
            public boolean isRunning() {
                return JobManager.this.running;
            }

            public void insertOrReplace(JobHolder jobHolder) {
                JobManager.this.reAddJob(jobHolder);
            }

            public void removeJob(JobHolder jobHolder) {
                JobManager.this.removeJob(jobHolder);
            }

            public JobHolder getNextJob(int wait, TimeUnit waitDuration) {
                JobHolder nextJob = JobManager.this.getNextJob();
                if (nextJob != null) {
                    return nextJob;
                }
                long waitUntil = waitDuration.toNanos((long) wait) + System.nanoTime();
                long nextJobDelay = JobManager.this.ensureConsumerWhenNeeded(null);
                while (nextJob == null && waitUntil > System.nanoTime()) {
                    nextJob = JobManager.this.running ? JobManager.this.getNextJob() : null;
                    if (nextJob == null) {
                        long remaining = waitUntil - System.nanoTime();
                        if (remaining > 0) {
                            long maxWait = Math.min(nextJobDelay, TimeUnit.NANOSECONDS.toMillis(remaining));
                            if (maxWait < 1) {
                                continue;
                            } else if (JobManager.this.networkUtil instanceof NetworkEventProvider) {
                                synchronized (JobManager.this.newJobListeners) {
                                    try {
                                        JobManager.this.newJobListeners.wait(maxWait);
                                    } catch (InterruptedException e) {
                                        JqLog.e(e, "exception while waiting for a new job.", new Object[0]);
                                    }
                                }
                            } else {
                                synchronized (JobManager.this.newJobListeners) {
                                    try {
                                        JobManager.this.newJobListeners.wait(Math.min(500, maxWait));
                                    } catch (InterruptedException e2) {
                                        JqLog.e(e2, "exception while waiting for a new job.", new Object[0]);
                                    }
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                }
                return nextJob;
            }

            public int countRemainingReadyJobs() {
                return JobManager.this.countReadyJobs(JobManager.this.networkUtil instanceof NetworkEventProvider ? JobManager.this.hasNetwork() : true);
            }
        };
        if (config.getCustomLogger() != null) {
            JqLog.setCustomLogger(config.getCustomLogger());
        }
        this.appContext = context.getApplicationContext();
        this.running = true;
        this.runningJobGroups = new CopyOnWriteGroupSet();
        this.sessionId = System.nanoTime();
        this.persistentJobQueue = config.getQueueFactory().createPersistentQueue(context, Long.valueOf(this.sessionId), config.getId());
        this.nonPersistentJobQueue = config.getQueueFactory().createNonPersistent(context, Long.valueOf(this.sessionId), config.getId());
        this.persistentOnAddedLocks = new ConcurrentHashMap();
        this.nonPersistentOnAddedLocks = new ConcurrentHashMap();
        this.networkUtil = config.getNetworkUtil();
        this.dependencyInjector = config.getDependencyInjector();
        if (this.networkUtil instanceof NetworkEventProvider) {
            ((NetworkEventProvider) this.networkUtil).setListener(this);
        }
        this.jobConsumerExecutor = new JobConsumerExecutor(config, this.consumerContract);
        this.timedExecutor = Executors.newSingleThreadScheduledExecutor();
        start();
    }

    public void stop() {
        this.running = false;
    }

    public void start() {
        if (!this.running) {
            this.running = true;
            notifyJobConsumer();
        }
    }

    public int count() {
        int cnt;
        synchronized (this.nonPersistentJobQueue) {
            cnt = 0 + this.nonPersistentJobQueue.count();
        }
        synchronized (this.persistentJobQueue) {
            cnt += this.persistentJobQueue.count();
        }
        return cnt;
    }

    private int countReadyJobs(boolean hasNetwork) {
        int total;
        synchronized (this.nonPersistentJobQueue) {
            total = 0 + this.nonPersistentJobQueue.countReadyJobs(hasNetwork, this.runningJobGroups.getSafe());
        }
        synchronized (this.persistentJobQueue) {
            total += this.persistentJobQueue.countReadyJobs(hasNetwork, this.runningJobGroups.getSafe());
        }
        return total;
    }

    public long addJob(Job job) {
        return addJob(job.getPriority(), job.getDelayInMs(), job);
    }

    public void addJobInBackground(Job job) {
        addJobInBackground(job.getPriority(), job.getDelayInMs(), job);
    }

    public void addJobInBackground(Job job, AsyncAddCallback callback) {
        addJobInBackground(job.getPriority(), job.getDelayInMs(), job, callback);
    }

    private void addOnAddedLock(ConcurrentHashMap<Long, CountDownLatch> lockMap, long id) {
        lockMap.put(Long.valueOf(id), new CountDownLatch(1));
    }

    private void waitForOnAddedLock(ConcurrentHashMap<Long, CountDownLatch> lockMap, long id) {
        CountDownLatch latch = (CountDownLatch) lockMap.get(Long.valueOf(id));
        if (latch != null) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                JqLog.e(e, "could not wait for onAdded lock", new Object[0]);
            }
        }
    }

    private void clearOnAddedLock(ConcurrentHashMap<Long, CountDownLatch> lockMap, long id) {
        CountDownLatch latch = (CountDownLatch) lockMap.get(Long.valueOf(id));
        if (latch != null) {
            latch.countDown();
        }
        lockMap.remove(Long.valueOf(id));
    }

    private long ensureConsumerWhenNeeded(Boolean hasNetwork) {
        if (hasNetwork == null) {
            hasNetwork = Boolean.valueOf(this.networkUtil instanceof NetworkEventProvider ? hasNetwork() : true);
        }
        synchronized (this.nonPersistentJobQueue) {
            Long nextRunNs = this.nonPersistentJobQueue.getNextJobDelayUntilNs(hasNetwork.booleanValue());
        }
        if (nextRunNs == null || nextRunNs.longValue() > System.nanoTime()) {
            Long persistedJobRunNs;
            synchronized (this.persistentJobQueue) {
                persistedJobRunNs = this.persistentJobQueue.getNextJobDelayUntilNs(hasNetwork.booleanValue());
            }
            if (persistedJobRunNs != null) {
                if (nextRunNs == null) {
                    nextRunNs = persistedJobRunNs;
                } else if (persistedJobRunNs.longValue() < nextRunNs.longValue()) {
                    nextRunNs = persistedJobRunNs;
                }
            }
            if (nextRunNs == null) {
                return Long.MAX_VALUE;
            }
            if (nextRunNs.longValue() < System.nanoTime()) {
                notifyJobConsumer();
                return 0;
            }
            long diff = (long) Math.ceil(((double) (nextRunNs.longValue() - System.nanoTime())) / 1000000.0d);
            ensureConsumerOnTime(diff);
            return diff;
        }
        notifyJobConsumer();
        return 0;
    }

    private void notifyJobConsumer() {
        synchronized (this.newJobListeners) {
            this.newJobListeners.notifyAll();
        }
        this.jobConsumerExecutor.considerAddingConsumer();
    }

    private void ensureConsumerOnTime(long waitMs) {
        this.timedExecutor.schedule(this.notifyRunnable, waitMs, TimeUnit.MILLISECONDS);
    }

    private boolean hasNetwork() {
        return this.networkUtil == null || this.networkUtil.isConnected(this.appContext);
    }

    private JobHolder getNextJob() {
        JobHolder jobHolder;
        boolean haveNetwork = hasNetwork();
        boolean persistent = false;
        synchronized (this.getNextJobLock) {
            Collection<String> runningJobIds = this.runningJobGroups.getSafe();
            synchronized (this.nonPersistentJobQueue) {
                jobHolder = this.nonPersistentJobQueue.nextJobAndIncRunCount(haveNetwork, runningJobIds);
            }
            if (jobHolder == null) {
                synchronized (this.persistentJobQueue) {
                    jobHolder = this.persistentJobQueue.nextJobAndIncRunCount(haveNetwork, runningJobIds);
                    persistent = true;
                }
            }
            if (jobHolder == null) {
                jobHolder = null;
            } else {
                if (persistent && this.dependencyInjector != null) {
                    this.dependencyInjector.inject(jobHolder.getBaseJob());
                }
                if (jobHolder.getGroupId() != null) {
                    this.runningJobGroups.add(jobHolder.getGroupId());
                }
                if (persistent) {
                    waitForOnAddedLock(this.persistentOnAddedLocks, jobHolder.getId().longValue());
                } else {
                    waitForOnAddedLock(this.nonPersistentOnAddedLocks, jobHolder.getId().longValue());
                }
            }
        }
        return jobHolder;
    }

    private void reAddJob(JobHolder jobHolder) {
        JqLog.d("re-adding job %s", jobHolder.getId());
        if (jobHolder.getBaseJob().isPersistent()) {
            synchronized (this.persistentJobQueue) {
                this.persistentJobQueue.insertOrReplace(jobHolder);
            }
        } else {
            synchronized (this.nonPersistentJobQueue) {
                this.nonPersistentJobQueue.insertOrReplace(jobHolder);
            }
        }
        if (jobHolder.getGroupId() != null) {
            this.runningJobGroups.remove(jobHolder.getGroupId());
        }
    }

    public JobStatus getJobStatus(long id, boolean isPersistent) {
        if (this.jobConsumerExecutor.isRunning(id, isPersistent)) {
            return JobStatus.RUNNING;
        }
        JobHolder holder;
        if (isPersistent) {
            synchronized (this.persistentJobQueue) {
                holder = this.persistentJobQueue.findJobById(id);
            }
        } else {
            synchronized (this.nonPersistentJobQueue) {
                holder = this.nonPersistentJobQueue.findJobById(id);
            }
        }
        if (holder == null) {
            return JobStatus.UNKNOWN;
        }
        boolean network = hasNetwork();
        if (holder.requiresNetwork() && !network) {
            return JobStatus.WAITING_NOT_READY;
        }
        if (holder.getDelayUntilNs() > System.nanoTime()) {
            return JobStatus.WAITING_NOT_READY;
        }
        return JobStatus.WAITING_READY;
    }

    private void removeJob(JobHolder jobHolder) {
        if (jobHolder.getBaseJob().isPersistent()) {
            synchronized (this.persistentJobQueue) {
                this.persistentJobQueue.remove(jobHolder);
            }
        } else {
            synchronized (this.nonPersistentJobQueue) {
                this.nonPersistentJobQueue.remove(jobHolder);
            }
        }
        if (jobHolder.getGroupId() != null) {
            this.runningJobGroups.remove(jobHolder.getGroupId());
        }
    }

    public synchronized void clear() {
        synchronized (this.nonPersistentJobQueue) {
            this.nonPersistentJobQueue.clear();
            this.nonPersistentOnAddedLocks.clear();
        }
        synchronized (this.persistentJobQueue) {
            this.persistentJobQueue.clear();
            this.persistentOnAddedLocks.clear();
        }
        this.runningJobGroups.clear();
    }

    public void onNetworkChange(boolean isConnected) {
        ensureConsumerWhenNeeded(Boolean.valueOf(isConnected));
    }

    @Deprecated
    public long addJob(int priority, BaseJob baseJob) {
        return addJob(priority, 0, baseJob);
    }

    @Deprecated
    public long addJob(int priority, long delay, BaseJob baseJob) {
        long nanoTime;
        long id;
        if (delay > 0) {
            nanoTime = (NS_PER_MS * delay) + System.nanoTime();
        } else {
            nanoTime = NOT_RUNNING_SESSION_ID;
        }
        JobHolder jobHolder = new JobHolder(priority, baseJob, nanoTime, NOT_RUNNING_SESSION_ID);
        if (baseJob.isPersistent()) {
            synchronized (this.persistentJobQueue) {
                id = this.persistentJobQueue.insert(jobHolder);
                addOnAddedLock(this.persistentOnAddedLocks, id);
            }
        } else {
            synchronized (this.nonPersistentJobQueue) {
                id = this.nonPersistentJobQueue.insert(jobHolder);
                addOnAddedLock(this.nonPersistentOnAddedLocks, id);
            }
        }
        if (JqLog.isDebugEnabled()) {
            JqLog.d("added job id: %d class: %s priority: %d delay: %d group : %s persistent: %s requires network: %s", Long.valueOf(id), baseJob.getClass().getSimpleName(), Integer.valueOf(priority), Long.valueOf(delay), baseJob.getRunGroupId(), Boolean.valueOf(baseJob.isPersistent()), Boolean.valueOf(baseJob.requiresNetwork()));
        }
        if (this.dependencyInjector != null) {
            this.dependencyInjector.inject(baseJob);
        }
        jobHolder.getBaseJob().onAdded();
        if (baseJob.isPersistent()) {
            synchronized (this.persistentJobQueue) {
                clearOnAddedLock(this.persistentOnAddedLocks, id);
            }
        } else {
            synchronized (this.nonPersistentJobQueue) {
                clearOnAddedLock(this.nonPersistentOnAddedLocks, id);
            }
        }
        notifyJobConsumer();
        return id;
    }

    @Deprecated
    public void addJobInBackground(int priority, BaseJob baseJob) {
        this.timedExecutor.execute(new AnonymousClass3(priority, baseJob));
    }

    @Deprecated
    public void addJobInBackground(int priority, long delay, BaseJob baseJob) {
        addJobInBackground(priority, delay, baseJob, null);
    }

    protected void addJobInBackground(int priority, long delay, BaseJob baseJob, AsyncAddCallback callback) {
        this.timedExecutor.execute(new AnonymousClass4(System.nanoTime(), priority, delay, baseJob, callback));
    }
}
