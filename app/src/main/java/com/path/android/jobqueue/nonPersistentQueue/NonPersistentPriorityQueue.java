package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import com.path.android.jobqueue.JobQueue;
import java.util.Collection;
import java.util.Comparator;

public class NonPersistentPriorityQueue implements JobQueue {
    private final String id;
    public final Comparator<JobHolder> jobComparator;
    private NetworkAwarePriorityQueue jobs;
    private long nonPersistentJobIdGenerator;
    private final long sessionId;

    public NonPersistentPriorityQueue(long sessionId, String id) {
        this.nonPersistentJobIdGenerator = -2147483648L;
        this.jobComparator = new Comparator<JobHolder>() {
            public int compare(JobHolder holder1, JobHolder holder2) {
                int cmp = NonPersistentPriorityQueue.compareInt(holder1.getPriority(), holder2.getPriority());
                if (cmp != 0) {
                    return cmp;
                }
                cmp = -NonPersistentPriorityQueue.compareLong(holder1.getCreatedNs(), holder2.getCreatedNs());
                if (cmp != 0) {
                    return cmp;
                }
                return -NonPersistentPriorityQueue.compareLong(holder1.getId().longValue(), holder2.getId().longValue());
            }
        };
        this.id = id;
        this.sessionId = sessionId;
        this.jobs = new NetworkAwarePriorityQueue(5, this.jobComparator);
    }

    public synchronized long insert(JobHolder jobHolder) {
        this.nonPersistentJobIdGenerator++;
        jobHolder.setId(Long.valueOf(this.nonPersistentJobIdGenerator));
        this.jobs.offer(jobHolder);
        return jobHolder.getId().longValue();
    }

    public long insertOrReplace(JobHolder jobHolder) {
        remove(jobHolder);
        jobHolder.setRunningSessionId(Long.MIN_VALUE);
        this.jobs.offer(jobHolder);
        return jobHolder.getId().longValue();
    }

    public void remove(JobHolder jobHolder) {
        this.jobs.remove(jobHolder);
    }

    public int count() {
        return this.jobs.size();
    }

    public int countReadyJobs(boolean hasNetwork, Collection<String> excludeGroups) {
        return this.jobs.countReadyJobs(hasNetwork, (Collection) excludeGroups).getCount();
    }

    public JobHolder nextJobAndIncRunCount(boolean hasNetwork, Collection<String> excludeGroups) {
        JobHolder jobHolder = this.jobs.peek(hasNetwork, excludeGroups);
        if (jobHolder == null) {
            return jobHolder;
        }
        if (jobHolder.getDelayUntilNs() > System.nanoTime()) {
            return null;
        }
        jobHolder.setRunningSessionId(this.sessionId);
        jobHolder.setRunCount(jobHolder.getRunCount() + 1);
        this.jobs.remove(jobHolder);
        return jobHolder;
    }

    public Long getNextJobDelayUntilNs(boolean hasNetwork) {
        JobHolder next = this.jobs.peek(hasNetwork, null);
        if (next == null) {
            return null;
        }
        return Long.valueOf(next.getDelayUntilNs());
    }

    public void clear() {
        this.jobs.clear();
    }

    public JobHolder findJobById(long id) {
        return this.jobs.findById(id);
    }

    private static int compareInt(int i1, int i2) {
        if (i1 > i2) {
            return -1;
        }
        if (i2 > i1) {
            return 1;
        }
        return 0;
    }

    private static int compareLong(long l1, long l2) {
        if (l1 > l2) {
            return -1;
        }
        if (l2 > l1) {
            return 1;
        }
        return 0;
    }
}
