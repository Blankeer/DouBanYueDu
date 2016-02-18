package com.path.android.jobqueue.executor;

import com.path.android.jobqueue.JobHolder;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.JqLog;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JobConsumerExecutor {
    private final AtomicInteger activeConsumerCount;
    private final Contract contract;
    private final int keepAliveSeconds;
    private int loadFactor;
    private int maxConsumerSize;
    private int minConsumerSize;
    private final ConcurrentHashMap<String, JobHolder> runningJobHolders;
    private final ThreadGroup threadGroup;

    public interface Contract {
        int countRemainingReadyJobs();

        JobHolder getNextJob(int i, TimeUnit timeUnit);

        void insertOrReplace(JobHolder jobHolder);

        boolean isRunning();

        void removeJob(JobHolder jobHolder);
    }

    private static class JobConsumer implements Runnable {
        private final Contract contract;
        private boolean didRunOnce;
        private final JobConsumerExecutor executor;

        public JobConsumer(Contract contract, JobConsumerExecutor executor) {
            this.didRunOnce = false;
            this.executor = executor;
            this.contract = contract;
        }

        public void run() {
            boolean canDie;
            do {
                if (JqLog.isDebugEnabled()) {
                    if (this.didRunOnce) {
                        try {
                            JqLog.d("re-running consumer %s", Thread.currentThread().getName());
                        } catch (Throwable th) {
                            canDie = this.executor.canIDie();
                            if (JqLog.isDebugEnabled()) {
                                if (canDie) {
                                    JqLog.d("finishing consumer %s", Thread.currentThread().getName());
                                } else {
                                    JqLog.d("didn't allow me to die, re-running %s", Thread.currentThread().getName());
                                }
                            }
                        }
                    } else {
                        JqLog.d("starting consumer %s", Thread.currentThread().getName());
                        this.didRunOnce = true;
                    }
                }
                JobHolder nextJob;
                do {
                    nextJob = this.contract.isRunning() ? this.contract.getNextJob(this.executor.keepAliveSeconds, TimeUnit.SECONDS) : null;
                    if (nextJob != null) {
                        this.executor.onBeforeRun(nextJob);
                        if (nextJob.safeRun(nextJob.getRunCount())) {
                            this.contract.removeJob(nextJob);
                        } else {
                            this.contract.insertOrReplace(nextJob);
                        }
                        this.executor.onAfterRun(nextJob);
                        continue;
                    }
                } while (nextJob != null);
                canDie = this.executor.canIDie();
                if (JqLog.isDebugEnabled()) {
                    if (canDie) {
                        JqLog.d("finishing consumer %s", Thread.currentThread().getName());
                        continue;
                    } else {
                        JqLog.d("didn't allow me to die, re-running %s", Thread.currentThread().getName());
                        continue;
                    }
                }
            } while (!canDie);
        }
    }

    public JobConsumerExecutor(Configuration config, Contract contract) {
        this.activeConsumerCount = new AtomicInteger(0);
        this.loadFactor = config.getLoadFactor();
        this.maxConsumerSize = config.getMaxConsumerCount();
        this.minConsumerSize = config.getMinConsumerCount();
        this.keepAliveSeconds = config.getConsumerKeepAlive();
        this.contract = contract;
        this.threadGroup = new ThreadGroup("JobConsumers");
        this.runningJobHolders = new ConcurrentHashMap();
    }

    public void considerAddingConsumer() {
        doINeedANewThread(false, true);
    }

    private boolean canIDie() {
        if (doINeedANewThread(true, false)) {
            return false;
        }
        return true;
    }

    private boolean doINeedANewThread(boolean inConsumerThread, boolean addIfNeeded) {
        boolean z = false;
        if (this.contract.isRunning()) {
            synchronized (this.threadGroup) {
                if (isAboveLoadFactor(inConsumerThread) && canAddMoreConsumers()) {
                    if (addIfNeeded) {
                        addConsumer();
                    }
                    z = true;
                } else {
                    if (inConsumerThread) {
                        this.activeConsumerCount.decrementAndGet();
                    }
                }
            }
        } else if (inConsumerThread) {
            this.activeConsumerCount.decrementAndGet();
        }
        return z;
    }

    private void addConsumer() {
        JqLog.d("adding another consumer", new Object[0]);
        synchronized (this.threadGroup) {
            Thread thread = new Thread(this.threadGroup, new JobConsumer(this.contract, this));
            this.activeConsumerCount.incrementAndGet();
            thread.start();
        }
    }

    private boolean canAddMoreConsumers() {
        boolean z;
        synchronized (this.threadGroup) {
            z = this.activeConsumerCount.intValue() < this.maxConsumerSize;
        }
        return z;
    }

    private boolean isAboveLoadFactor(boolean inConsumerThread) {
        boolean res = false;
        synchronized (this.threadGroup) {
            int i;
            int intValue = this.activeConsumerCount.intValue();
            if (inConsumerThread) {
                i = 1;
            } else {
                i = 0;
            }
            int consumerCnt = intValue - i;
            if (consumerCnt < this.minConsumerSize || this.loadFactor * consumerCnt < this.contract.countRemainingReadyJobs() + this.runningJobHolders.size()) {
                res = true;
            }
            if (JqLog.isDebugEnabled()) {
                JqLog.d("%s: load factor check. %s = (%d < %d)|| (%d * %d < %d + %d). consumer thread: %s", Thread.currentThread().getName(), Boolean.valueOf(res), Integer.valueOf(consumerCnt), Integer.valueOf(this.minConsumerSize), Integer.valueOf(consumerCnt), Integer.valueOf(this.loadFactor), Integer.valueOf(this.contract.countRemainingReadyJobs()), Integer.valueOf(this.runningJobHolders.size()), Boolean.valueOf(inConsumerThread));
            }
        }
        return res;
    }

    private void onBeforeRun(JobHolder jobHolder) {
        this.runningJobHolders.put(createRunningJobHolderKey(jobHolder), jobHolder);
    }

    private void onAfterRun(JobHolder jobHolder) {
        this.runningJobHolders.remove(createRunningJobHolderKey(jobHolder));
    }

    private String createRunningJobHolderKey(JobHolder jobHolder) {
        return createRunningJobHolderKey(jobHolder.getId().longValue(), jobHolder.getBaseJob().isPersistent());
    }

    private String createRunningJobHolderKey(long id, boolean isPersistent) {
        return id + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR + (isPersistent ? "t" : "f");
    }

    public boolean isRunning(long id, boolean persistent) {
        return this.runningJobHolders.containsKey(createRunningJobHolderKey(id, persistent));
    }
}
