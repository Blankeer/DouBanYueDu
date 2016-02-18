package com.path.android.jobqueue.nonPersistentQueue;

import com.path.android.jobqueue.JobHolder;
import java.util.Comparator;

public class ConsistentTimedComparator implements Comparator<JobHolder> {
    final Comparator<JobHolder> baseComparator;

    public ConsistentTimedComparator(Comparator<JobHolder> baseComparator) {
        this.baseComparator = baseComparator;
    }

    public int compare(JobHolder jobHolder, JobHolder jobHolder2) {
        if (jobHolder.getDelayUntilNs() < jobHolder2.getDelayUntilNs()) {
            return -1;
        }
        if (jobHolder.getDelayUntilNs() > jobHolder2.getDelayUntilNs()) {
            return 1;
        }
        return this.baseComparator.compare(jobHolder, jobHolder2);
    }
}
