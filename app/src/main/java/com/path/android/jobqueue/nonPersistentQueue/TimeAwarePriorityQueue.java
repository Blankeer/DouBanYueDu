package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import java.util.Collection;
import java.util.Comparator;

public class TimeAwarePriorityQueue extends MergedQueue {
    public TimeAwarePriorityQueue(int initialCapacity, Comparator<JobHolder> comparator) {
        super(initialCapacity, comparator, new TimeAwareComparator(comparator));
    }

    protected SetId decideQueue(JobHolder jobHolder) {
        return jobHolder.getDelayUntilNs() <= System.nanoTime() ? SetId.S0 : SetId.S1;
    }

    protected JobSet createQueue(SetId setId, int initialCapacity, Comparator<JobHolder> comparator) {
        if (setId == SetId.S0) {
            return new NonPersistentJobSet(comparator);
        }
        return new NonPersistentJobSet(new ConsistentTimedComparator(comparator));
    }

    public CountWithGroupIdsResult countReadyJobs(long now, Collection<String> excludeGroups) {
        return super.countReadyJobs(SetId.S0, excludeGroups).mergeWith(super.countReadyJobs(SetId.S1, now, excludeGroups));
    }

    public CountWithGroupIdsResult countReadyJobs(Collection<String> collection) {
        throw new UnsupportedOperationException("cannot call time aware priority queue's count ready jobs w/o providing a time");
    }
}
