package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import java.util.Collection;
import java.util.Comparator;

public class NetworkAwarePriorityQueue extends MergedQueue {
    public NetworkAwarePriorityQueue(int initialCapacity, Comparator<JobHolder> comparator) {
        super(initialCapacity, comparator, new TimeAwareComparator(comparator));
    }

    public JobHolder peek(boolean canUseNetwork, Collection<String> excludeGroupIds) {
        if (canUseNetwork) {
            return super.peek(excludeGroupIds);
        }
        return super.peekFromQueue(SetId.S1, excludeGroupIds);
    }

    public JobHolder poll(boolean canUseNetwork, Collection<String> excludeGroupIds) {
        if (canUseNetwork) {
            return super.peek(excludeGroupIds);
        }
        return super.peekFromQueue(SetId.S1, excludeGroupIds);
    }

    protected SetId decideQueue(JobHolder jobHolder) {
        return jobHolder.requiresNetwork() ? SetId.S0 : SetId.S1;
    }

    protected JobSet createQueue(SetId ignoredQueueId, int initialCapacity, Comparator<JobHolder> comparator) {
        return new TimeAwarePriorityQueue(initialCapacity, comparator);
    }

    public CountWithGroupIdsResult countReadyJobs(boolean hasNetwork, Collection<String> excludeGroups) {
        long now = System.nanoTime();
        if (hasNetwork) {
            return super.countReadyJobs(SetId.S0, now, excludeGroups).mergeWith(super.countReadyJobs(SetId.S1, now, excludeGroups));
        }
        return super.countReadyJobs(SetId.S1, now, excludeGroups);
    }

    public CountWithGroupIdsResult countReadyJobs(long now, Collection<String> collection) {
        throw new UnsupportedOperationException("cannot call network aware priority queue count w/o providing network status");
    }

    public CountWithGroupIdsResult countReadyJobs(Collection<String> collection) {
        throw new UnsupportedOperationException("cannot call network aware priority queue count w/o providing network status");
    }
}
