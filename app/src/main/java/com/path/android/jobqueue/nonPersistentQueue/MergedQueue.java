package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import java.util.Collection;
import java.util.Comparator;

public abstract class MergedQueue implements JobSet {
    final Comparator<JobHolder> comparator;
    JobSet queue0;
    JobSet queue1;
    final Comparator<JobHolder> retrieveComparator;

    protected enum SetId {
        S0,
        S1
    }

    protected abstract JobSet createQueue(SetId setId, int i, Comparator<JobHolder> comparator);

    protected abstract SetId decideQueue(JobHolder jobHolder);

    public MergedQueue(int initialCapacity, Comparator<JobHolder> comparator, Comparator<JobHolder> retrieveComparator) {
        this.comparator = comparator;
        this.retrieveComparator = retrieveComparator;
        this.queue0 = createQueue(SetId.S0, initialCapacity, comparator);
        this.queue1 = createQueue(SetId.S1, initialCapacity, comparator);
    }

    protected JobHolder pollFromQueue(SetId queueId, Collection<String> excludeGroupIds) {
        if (queueId == SetId.S0) {
            return this.queue0.poll(excludeGroupIds);
        }
        return this.queue1.poll(excludeGroupIds);
    }

    protected JobHolder peekFromQueue(SetId queueId, Collection<String> excludeGroupIds) {
        if (queueId == SetId.S0) {
            return this.queue0.peek(excludeGroupIds);
        }
        return this.queue1.peek(excludeGroupIds);
    }

    public boolean offer(JobHolder jobHolder) {
        if (decideQueue(jobHolder) == SetId.S0) {
            return this.queue0.offer(jobHolder);
        }
        return this.queue1.offer(jobHolder);
    }

    public JobHolder poll(Collection<String> excludeGroupIds) {
        JobHolder delayed = this.queue0.peek(excludeGroupIds);
        if (delayed == null) {
            return this.queue1.poll(excludeGroupIds);
        }
        if (decideQueue(delayed) != SetId.S0) {
            this.queue0.remove(delayed);
            this.queue1.offer(delayed);
            return poll(excludeGroupIds);
        }
        JobHolder nonDelayed = this.queue1.peek(excludeGroupIds);
        if (nonDelayed == null) {
            this.queue0.remove(delayed);
            return delayed;
        } else if (decideQueue(nonDelayed) != SetId.S1) {
            this.queue0.offer(nonDelayed);
            this.queue1.remove(nonDelayed);
            return poll(excludeGroupIds);
        } else if (this.retrieveComparator.compare(delayed, nonDelayed) == -1) {
            this.queue0.remove(delayed);
            return delayed;
        } else {
            this.queue1.remove(nonDelayed);
            return nonDelayed;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.path.android.jobqueue.JobHolder peek(java.util.Collection<java.lang.String> r6) {
        /*
        r5 = this;
    L_0x0000:
        r3 = r5.queue0;
        r1 = r3.peek(r6);
        if (r1 == 0) goto L_0x001b;
    L_0x0008:
        r3 = r5.decideQueue(r1);
        r4 = com.path.android.jobqueue.nonPersistentQueue.MergedQueue.SetId.S0;
        if (r3 == r4) goto L_0x001b;
    L_0x0010:
        r3 = r5.queue1;
        r3.offer(r1);
        r3 = r5.queue0;
        r3.remove(r1);
        goto L_0x0000;
    L_0x001b:
        r3 = r5.queue1;
        r2 = r3.peek(r6);
        if (r2 == 0) goto L_0x0036;
    L_0x0023:
        r3 = r5.decideQueue(r2);
        r4 = com.path.android.jobqueue.nonPersistentQueue.MergedQueue.SetId.S1;
        if (r3 == r4) goto L_0x0036;
    L_0x002b:
        r3 = r5.queue0;
        r3.offer(r2);
        r3 = r5.queue1;
        r3.remove(r2);
        goto L_0x0000;
    L_0x0036:
        if (r1 != 0) goto L_0x0039;
    L_0x0038:
        return r2;
    L_0x0039:
        if (r2 != 0) goto L_0x003d;
    L_0x003b:
        r2 = r1;
        goto L_0x0038;
    L_0x003d:
        r3 = r5.retrieveComparator;
        r0 = r3.compare(r1, r2);
        r3 = -1;
        if (r0 != r3) goto L_0x0038;
    L_0x0046:
        r2 = r1;
        goto L_0x0038;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.path.android.jobqueue.nonPersistentQueue.MergedQueue.peek(java.util.Collection):com.path.android.jobqueue.JobHolder");
    }

    public void clear() {
        this.queue1.clear();
        this.queue0.clear();
    }

    public boolean remove(JobHolder holder) {
        return this.queue1.remove(holder) || this.queue0.remove(holder);
    }

    public int size() {
        return this.queue0.size() + this.queue1.size();
    }

    public CountWithGroupIdsResult countReadyJobs(SetId setId, long now, Collection<String> excludeGroups) {
        if (setId == SetId.S0) {
            return this.queue0.countReadyJobs(now, excludeGroups);
        }
        return this.queue1.countReadyJobs(now, excludeGroups);
    }

    public CountWithGroupIdsResult countReadyJobs(SetId setId, Collection<String> excludeGroups) {
        if (setId == SetId.S0) {
            return this.queue0.countReadyJobs(excludeGroups);
        }
        return this.queue1.countReadyJobs(excludeGroups);
    }

    public JobHolder findById(long id) {
        JobHolder q0 = this.queue0.findById(id);
        return q0 == null ? this.queue1.findById(id) : q0;
    }
}
